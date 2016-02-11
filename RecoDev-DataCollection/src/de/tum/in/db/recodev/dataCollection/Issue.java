	/*
	 * RecoDev
	 * Issue.java
	 * <OWNER> = Amir H. Moin (moin@in.tum.de)
	 * <ORGANIZATION> = Tehnische Universität München
	 * <YEAR> = 2015
	 * License: BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
	 */

package de.tum.in.db.recodev.dataCollection;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.print.attribute.standard.DateTimeAtCompleted;

import org.apache.xmlrpc.XmlRpcConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import com.mysql.jdbc.util.TimezoneDump;

import java.net.URL;
import java.security.cert.X509Certificate;

public class Issue {
	
	private int bugID = 0;
	private String summary = "";
	private String description = "";
	private String comments = "";
	private String resolver_dev_email = ""; 
	private String resolver_dev_user_name = ""; 
	private ArrayList<String> cc_list = new ArrayList<String>();
	private ArrayList<String> keywords = new ArrayList<String>(); 
	private Timestamp reported = null;
	private Timestamp modified = null;
	private String priority = "";
	private String severity = "";
	private ArrayList<Integer> blocks = new ArrayList<Integer>();
	private ArrayList<Integer> dependsOn = new ArrayList<Integer>();
	private ArrayList<Integer> relatedIssues = new ArrayList<Integer>();
	
	public Issue(int bugID) throws Exception {
	
		this.bugID = bugID;
		handleSSLCertificate();
		
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL(Main.bugzillaURL));
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);
		Map issueS_Map = new HashMap();
		issueS_Map.put("ids", bugID);
		
		HashMap issueMap = null;
		Object[] issueArray = null;
		try {
			issueMap = (HashMap) client.execute("Bug.get",
					new Object[] { issueS_Map });
			issueArray = (Object[]) (issueMap.get("bugs"));			
			
		} catch (org.apache.xmlrpc.XmlRpcException e) {
			e.printStackTrace();
		}
		
		if(
				!(((HashMap) (issueArray[0])).get("status")
				.toString().equals("RESOLVED"))
				||
				!((((HashMap) (issueArray[0])).get("resolution")
						.toString().equals("FIXED")))
				
				) {
					return;
				}
		
		String issueSummary = ((HashMap) (issueArray[0])).get("summary")
				.toString();
		this.summary = issueSummary;
		
		Timestamp issueReported = new java.sql.Timestamp(((Date)((HashMap) (issueArray[0])).get("creation_time")).getTime());
		this.reported = issueReported;
		
		Timestamp issueModified = new java.sql.Timestamp(((Date)((HashMap) (issueArray[0])).get("last_change_time")).getTime()); 
		this.modified = issueModified;
		
		HashMap commentMap = null;
		Object[] commentsArray = null;
		try {
		commentMap = (HashMap) client.execute("Bug.comments",
				new Object[] { issueS_Map });
		commentsArray = (Object[]) (((HashMap) (((HashMap) commentMap.get("bugs"))
				.get((new Integer(bugID)).toString()))).get("comments"));
		} catch (org.apache.xmlrpc.XmlRpcException e) {
			e.printStackTrace();
		}
		String issueDescription =
				 ((HashMap)commentsArray[0]).get("text").toString();
		this.description = issueDescription;
		
		String issueComments = "";
		for (int i = 0; i < commentsArray.length; i++) {
			issueComments += (((HashMap) commentsArray[i]).get("text"))
					.toString();
			issueComments += "\n";
		}
		
		this.comments = issueComments;
		
		String issueResolverDev_email = ((HashMap) (issueArray[0])).get("assigned_to")
				.toString();
		this.resolver_dev_email = issueResolverDev_email;
		
		String issueResolver_dev_user_name = issueResolverDev_email.substring(0, resolver_dev_email.indexOf('@'));
		this.resolver_dev_user_name = issueResolver_dev_user_name;
		
		Object[] issueCCListObjects = (Object[]) ((HashMap) (issueArray[0])).get("cc");
		if(issueCCListObjects.length > 0) {
		ArrayList<String> issueCCList = new ArrayList<String>();
		for(int i = 0; i < issueCCListObjects.length; i++) {
			issueCCList.add(issueCCListObjects[i].toString());
		}
		this.cc_list = issueCCList;
		}
		
		Object[] issueKeywordsObjects = (Object[]) ((HashMap) (issueArray[0])).get("keywords");
		if(issueKeywordsObjects.length > 0) {
		ArrayList<String> issueKeywords = new ArrayList<String>();
		for(int i = 0; i < issueKeywordsObjects.length; i++) {
			issueKeywords.add(issueKeywordsObjects[i].toString());
		}
		this.keywords = issueKeywords;
		}	
		
		String issuePriority = ((HashMap) (issueArray[0])).get("priority")
				.toString();
		this.priority = issuePriority;

		String issueSeverity = ((HashMap) (issueArray[0])).get("severity")
				.toString();
		this.severity = issueSeverity;
		
		Object[] issueBlocksObjects = (Object[]) ((HashMap) (issueArray[0])).get("blocks");
		if(issueBlocksObjects.length > 0) {
		ArrayList<Integer> issueBlocks = new ArrayList<Integer>();
		for(int i = 0; i < issueBlocksObjects.length; i++) {
			issueBlocks.add(new Integer((int)issueBlocksObjects[i]));
		}
		this.blocks = issueBlocks;
		}
		
		Object[] issueDependsOnObjects = (Object[]) ((HashMap) (issueArray[0])).get("depends_on");
		if(issueDependsOnObjects.length > 0) {
		ArrayList<Integer> issueDependsOn = new ArrayList<Integer>();
		for(int i = 0; i < issueDependsOnObjects.length; i++) {
			issueDependsOn.add(new Integer((int)issueDependsOnObjects[i]));
		}
		this.dependsOn = issueDependsOn;
		}
		
		this.relatedIssues.addAll(this.blocks);
		this.relatedIssues.addAll(this.dependsOn);
	}
	
	
	public int getBugID() {
		return bugID;
	}
	public void setBugID(int bugID) {
		this.bugID = bugID;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList<String> getCc_list() {
		return cc_list;
	}
	public void setCc_list(ArrayList<String> cc_list) {
		this.cc_list = cc_list;
	}
	public ArrayList<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}
	public Timestamp getReported() {
		return reported;
	}
	public void setReported(Timestamp reported) {
		this.reported = reported;
	}
	public Timestamp getModified() {
		return modified;
	}
	public void setModified(Timestamp modified) {
		this.modified = modified;
	}

	public String getResolver_dev_user_name() {
		return resolver_dev_user_name;
	}
	
	public void setResolver_dev_user_name(String resolver_dev_user_name) {
		this.resolver_dev_user_name = resolver_dev_user_name;
	}
	
	private static void handleSSLCertificate() throws Exception {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
				// Trust always
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
				// Trust always
			}
		} };

		// Install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("SSL");
		// Create empty HostnameVerifier
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		};

		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}



	public String getComments() {
		return comments;
	}



	public void setComments(String comments) {
		this.comments = comments;
	}



	public String getResolver_dev_email() {
		return resolver_dev_email;
	}



	public void setResolver_dev_email(String resolver_dev_email) {
		this.resolver_dev_email = resolver_dev_email;
	}


	public String getPriority() {
		return priority;
	}


	public void setPriority(String priority) {
		this.priority = priority;
	}


	public String getSeverity() {
		return severity;
	}


	public void setSeverity(String severity) {
		this.severity = severity;
	}


	public ArrayList<Integer> getBlocks() {
		return blocks;
	}


	public void setBlocks(ArrayList<Integer> blocks) {
		this.blocks = blocks;
	}

	public ArrayList<Integer> getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(ArrayList<Integer> dependsOn) {
		this.dependsOn = dependsOn;
	}
	
	public ArrayList<Integer> getRelatedIssues() {
		return relatedIssues;
	}


	public void setRelatedIssues(ArrayList<Integer> relatedIssues) {
		this.relatedIssues = relatedIssues;
	}
	
}
