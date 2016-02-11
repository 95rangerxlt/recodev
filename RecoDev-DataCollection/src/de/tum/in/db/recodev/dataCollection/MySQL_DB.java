	/*
	 * RecoDev
	 * MySQL_DB.java
	 * <OWNER> = Amir H. Moin (moin@in.tum.de)
	 * <ORGANIZATION> = Tehnische Universität München
	 * <YEAR> = 2015
	 * License: BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
	 */

package de.tum.in.db.recodev.dataCollection;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQL_DB {
	
	private BugRepository bugRepository = null;
	private SourceCodeRepository sourceCodeRepository = null;
	
	public MySQL_DB(BugRepository bugRepository, SourceCodeRepository sourceCodeRepository) {
		this.bugRepository = bugRepository;
		this.sourceCodeRepository = sourceCodeRepository;
	}
	
	public MySQL_DB(SourceCodeRepository sourceCodeRepository) {
		this.sourceCodeRepository = sourceCodeRepository;
	}

	public void createIssueDeveloperProfiles() throws SQLException {
		
		Connection dbConnection = null;
		Statement sqlStatement = null;
		ResultSet sqlResultSet = null;
		
		try {
			dbConnection = DriverManager.getConnection(Main.mysqlDbUrl, Main.mysqlDbUserName, Main.mysqlDbPassword);
			sqlStatement = dbConnection.createStatement();			
			sqlStatement.executeUpdate("CREATE DATABASE IF NOT EXISTS recodev");
			sqlStatement.executeUpdate("USE recodev");
			
			sqlStatement.executeUpdate("CREATE TABLE IF NOT EXISTS " + Main.projectName + "_developers (dev_user_name varchar(100) not null primary key, issue_count int, is_invalid tinyint, is_inactive tinyint, web_url varchar(1000))ENGINE=InnoDB");
			sqlStatement.executeUpdate("CREATE TABLE IF NOT EXISTS " + Main.projectName + "_issues (bug_id int not null primary key, summary text, resolver_dev varchar(100), resolver_dev_email varchar(100), cc_list longtext, keywords text, reported DATETIME, modified DATETIME, priority text, severity text, FOREIGN KEY fk_resolver_dev(resolver_dev) REFERENCES " + Main.projectName + "_developers(dev_user_name) ON UPDATE CASCADE ON DELETE RESTRICT)ENGINE=InnoDB");
			sqlStatement.executeUpdate("CREATE TABLE IF NOT EXISTS " + Main.projectName + "_issues_relation (bug_id1 int REFERENCES eclipse_jdt_issues(bug_id), bug_id2 int REFERENCES " + Main.projectName + "_issues(bug_id), PRIMARY KEY (bug_id1, bug_id2))");
						
			//Insert all developers inside the developers table
			ArrayList<String> developersUserNames = this.getBugRepository().getDevelopersUserNames();
			for(int i=0; i<developersUserNames.size(); i++) {
				sqlStatement.executeUpdate("INSERT INTO " + Main.projectName + "_developers (dev_user_name, issue_count) VALUES ('" + developersUserNames.get(i).substring(1, developersUserNames.get(i).length()-1) + "', 1) ON DUPLICATE KEY UPDATE issue_count = issue_count + 1");				
			}
			
			//Insert all issues inside the issues table
			ArrayList<Integer> issuesIDs = this.getBugRepository().getIssuesIDs();
			for(int i=0; i<issuesIDs.size(); i++) {
				Issue issue = this.getBugRepository().getIssues().get(issuesIDs.get(i));
				try {
					String summary = issue.getSummary().replace("\\\'", "\'");
					summary = issue.getSummary().replace("\'", "\\'");
					sqlStatement.executeUpdate("INSERT INTO " + Main.projectName + "_issues (bug_id, summary, resolver_dev, resolver_dev_email, cc_list, keywords, reported, modified, priority, severity) VALUES (" + issue.getBugID() + ", '" + summary + "', '" + issue.getResolver_dev_user_name() + "', '" + issue.getResolver_dev_email() + "', '" + issue.getCc_list() + "', '" + issue.getKeywords() + "', '" + issue.getReported() + "', '" + issue.getModified() + "', '" + issue.getPriority() + "', '" + issue.getSeverity() + "')");
					for(int j = 0; j < issue.getRelatedIssues().size(); j++) {
					sqlStatement.executeUpdate("INSERT INTO " + Main.projectName + "_issues_relation (bug_id1, bug_id2) VALUES (" + issue.getBugID() + ", " + issue.getRelatedIssues().get(j) + ")");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			
		} catch(SQLException ex) {
			Logger logger = Logger.getLogger(MySQL_DB.class.getName());
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {
			try {
	            if (sqlResultSet != null) {
	            	sqlResultSet.close();
	            }
	            if (sqlStatement != null) {
	            	sqlStatement.close();
	            }
	            if (dbConnection != null) {
	            	dbConnection.close();
	            }
	        } catch (SQLException ex) {
	            Logger lgr = Logger.getLogger(MySQL_DB.class.getName());
	            lgr.log(Level.WARNING, ex.getMessage(), ex);
	        }
		}
	}
	
	public void createCommitLogsTable() throws SQLException {
		Connection dbConnection = null;
		Statement sqlStatement = null;
		ResultSet sqlResultSet = null;
		
		try {
			dbConnection = DriverManager.getConnection(Main.mysqlDbUrl, Main.mysqlDbUserName, Main.mysqlDbPassword);
			sqlStatement = dbConnection.createStatement();			
			sqlStatement.executeUpdate("CREATE DATABASE IF NOT EXISTS recodev");
			sqlStatement.executeUpdate("USE recodev");
			
			sqlStatement.executeUpdate("CREATE TABLE IF NOT EXISTS " + Main.projectName + "_commit_logs (log_id int not null AUTO_INCREMENT primary key, log_desc varchar(1500), dev_name varchar(100), dev_email varchar(100))ENGINE=InnoDB");
						
			//Insert all commit logs inside the commit logs table
			Map<Integer, CommitLog> commitLogsMap = this.getSourceCodeRepository().getCommitLogsMap();
			for(int i=1; i<=commitLogsMap.size(); i++) {
				String commitLogDesc = commitLogsMap.get(new Integer(i)).getLogDescription().replace("\\\'", "\'");
				commitLogDesc = commitLogDesc.replace("\'", "\\'");			
				String commitLogAuthor = commitLogsMap.get(new Integer(i)).getLogAuthor().replace("\\\'", "\'");
				commitLogAuthor = commitLogAuthor.replace("\'", "\\'");
				String commitLogAuthorEmail = commitLogsMap.get(new Integer(i)).getLogAuthorEmail().replace("\\\'", "\'");
				commitLogAuthorEmail = commitLogAuthorEmail.replace("\'", "\\'");
				sqlStatement.executeUpdate("INSERT INTO " + Main.projectName + "_commit_logs (log_desc, dev_name, dev_email) VALUES ('" + commitLogDesc + "', '" + commitLogAuthor + "', '" + commitLogAuthorEmail + "')");				
			}
					
		} catch(SQLException ex) {
			Logger logger = Logger.getLogger(MySQL_DB.class.getName());
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {
			try {
	            if (sqlResultSet != null) {
	            	sqlResultSet.close();
	            }
	            if (sqlStatement != null) {
	            	sqlStatement.close();
	            }
	            if (dbConnection != null) {
	            	dbConnection.close();
	            }
	        } catch (SQLException ex) {
	            Logger lgr = Logger.getLogger(MySQL_DB.class.getName());
	            lgr.log(Level.WARNING, ex.getMessage(), ex);
	        }
		}

	}

	public BugRepository getBugRepository() {
		return bugRepository;
	}

	public void setBugRepository(BugRepository bugRepository) {
		this.bugRepository = bugRepository;
	}

	public SourceCodeRepository getSourceCodeRepository() {
		return sourceCodeRepository;
	}

	public void setSourceCodeRepository(SourceCodeRepository sourceCodeRepository) {
		this.sourceCodeRepository = sourceCodeRepository;
	}	
	
}
