	/*
	 * RecoDev
	 * Main.java
	 * This application makes 3 parts out of the 6 parts of the dataset. See the accompanying readme file for more info.
	 * <OWNER> = Amir H. Moin (moin@in.tum.de)
	 * <ORGANIZATION> = Tehnische Universität München
	 * <YEAR> = 2015
	 * License: BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
	 */

package de.tum.in.db.recodev.dataCollection;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	
	public static String mysqlDbUrl = "jdbc:mysql://localhost:3306";
	public static String mysqlDbUserName = "recodev";
	public static String mysqlDbPassword = "ahm20kdd16";

//	public static String projectName = "eclipse_jdt";
//	public static String projectName = "mozilla_firefox";
//	public static String bugzillaURL = "https://bugs.eclipse.org/bugs/xmlrpc.cgi";
//	public static String bugzillaURL = "https://bugzilla.mozilla.org/xmlrpc.cgi";	
	public static String db_config_code = "";
	public static String projectName = "";
	public static String bugzillaURL = "";
	public static String path = "";
	public static String commitLogsFilePath = "./commitLogs-" + projectName + ".txt";
	public static String csvFilePath = "./CSV-" + projectName + ".csv";


	public static void main(String[] args) throws Exception {		
		if(args.length != 3) {
			db_config_code = "00000";
			projectName = "eclipse_jdt";
			bugzillaURL = "https://bugs.eclipse.org/bugs/xmlrpc.cgi";
			System.err.println("Please specify the db-config-code, the project name and the Bugzilla repository URL. Please refer to the Readme file for more info.");
		} else {
			db_config_code = args[0];
			projectName = args[1];
			bugzillaURL = args[2];
		}		
		
		//Assumptions:
		//A directory called "issues-X", where X equals projectName already exists in the path.
		//A text file called "commitLogs-X.txt", where X equals projectName already exists in the path.
		//A CSV (Comma Separated Values) file called "CSV-X.csv", where X equals projectName already exists in the path.
		
		//The dataset created by this application has 5 parts:
		//Part 1: Issues-profiles-mysql (i.e., issues' profiles (mysql) excluding the description, comments, and related commit logs).
		//Part 2: Developers-profiles-mysql (i.e., developers' profiles (mysql) excluding the commit logs of developers and the info of social nets/blogs).
		//Part 3: Issues-profiles-text (i.e., issues' profiles (unstructured textual data) including the description and comments of issues).
		//Part 4: The commit logs of the revision control system (used for both issues profiles and developers profiles).
		
		if(db_config_code.charAt(0)=='1' || db_config_code.charAt(1)=='1' || db_config_code.charAt(2)=='1' || db_config_code.charAt(3)=='1') {
			
			BugRepository bugRepository = null;
			SourceCodeRepository sourceCodeRepository = null;
			
			if(db_config_code.charAt(0)=='1' || db_config_code.charAt(1)=='1' || db_config_code.charAt(2)=='1') {
			bugRepository = new BugRepository(csvFilePath);			
			}
			
			sourceCodeRepository = new SourceCodeRepository(commitLogsFilePath);
			
			MySQL_DB dao = null;
			boolean flag = false;
			if(db_config_code.charAt(0)=='1' || db_config_code.charAt(1)=='1') {				
				dao = new MySQL_DB(bugRepository, sourceCodeRepository);
				dao.createIssueDeveloperProfiles(); //Parts 1 & 2: Issues-profiles-mysql & Developers-profiles-mysql
				flag = true;
			}
			
			if(db_config_code.charAt(3)=='1' && !flag)
				dao = new MySQL_DB(sourceCodeRepository);
			
			if(db_config_code.charAt(3)=='1') {
				dao.createCommitLogsTable(); //Part 4: Commit logs (mysql)
			}
		
			if(db_config_code.charAt(2)=='1') {
				Text_DB tdao = new Text_DB(bugRepository);	
				tdao.createIssueProfiles(); //Part3: Issues-profiles-text
			}		
		
		}
				
		if(db_config_code.charAt('4') == '1')
			System.err.println("Part 5 of the dataset should be created manually...The current version of this program can only create parts 1 to 4.");;		
			//Part 5: Developers-profiles-text (i.e., textual info of in social networks and personal blogs of developers).
			
	}
}
