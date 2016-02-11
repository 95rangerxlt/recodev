	/*
	 * RecoDev
	 * BugRepository.java
	 * <OWNER> = Amir H. Moin (moin@in.tum.de)
	 * <ORGANIZATION> = Tehnische Universität München
	 * <YEAR> = 2015
	 * License: BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
	 */

package de.tum.in.db.recodev.dataCollection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class BugRepository {

	private ArrayList<Integer> issuesIDs = null;
	private HashMap<Integer,Issue> issues = null;
	private ArrayList<String> developersUserNames = null;
	 

	public BugRepository(String csvFilePath) {
		
		this.issuesIDs = new ArrayList<Integer>();
		this.issues = new HashMap<Integer,Issue>();
		this.developersUserNames = new ArrayList<String>();
		
		BufferedReader br = null;
		String line = "";
		String delimiter = ",";
		
		try  {
			File csvFile = new File(csvFilePath);
			Main.path = csvFile.getParentFile().getAbsolutePath();
			br = new BufferedReader(new FileReader(csvFile));
			Issue issue = null;
			boolean flag = false; 
			while ((line = br.readLine()) != null) {
				String[] issueInfo = line.split(delimiter);
				if(flag) {
					int issueID = Integer.valueOf(issueInfo[0]);
					this.issuesIDs.add(issueID);
					this.developersUserNames.add(issueInfo[3]);
					try {
						issue = new Issue(issueID);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					this.getIssues().put(issueID, issue);
				}
				flag = true;
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		}
	
	}
	
	
	public ArrayList<Integer> getIssuesIDs() {
		return issuesIDs;
	}

	public void setIssuesIDs(ArrayList<Integer> issuesIDs) {
		this.issuesIDs = issuesIDs;
	}


	public ArrayList<String> getDevelopersUserNames() {
		return developersUserNames;
	}


	public void setDevelopersUserNames(ArrayList<String> developersUserNames) {
		this.developersUserNames = developersUserNames;
	}


	public HashMap<Integer,Issue> getIssues() {
		return issues;
	}


	public void setIssues(HashMap<Integer,Issue> issues) {
		this.issues = issues;
	}
	
	
}
