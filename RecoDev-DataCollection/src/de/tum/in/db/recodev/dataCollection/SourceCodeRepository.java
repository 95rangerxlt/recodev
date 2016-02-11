package de.tum.in.db.recodev.dataCollection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SourceCodeRepository {
	private Map<Integer, CommitLog> commitLogsMap;

	public SourceCodeRepository(String filePath) {
		
		BufferedReader br = null;
		String line = "";
		String delimiter = ";#;";
		this.commitLogsMap = new HashMap<Integer, CommitLog>();
		try  {
			File logFile = new File(filePath);			
			br = new BufferedReader(new FileReader(logFile));
			int logCounter = 0;

			while ((line = br.readLine()) != null) {				
				String[] logInfo = line.split(delimiter);
				if(logInfo.length != 3) continue;
				
				String logDesc = ""; 
				logDesc = logInfo[0];
				if(logDesc.length()>1500) logDesc = logDesc.substring(0, 1499);
				
				String logAuthor = "";
				logAuthor = logInfo[1];
				if(logAuthor.length()>100) logAuthor = logAuthor.substring(0, 99);
				
				String logAuthorEmail = "";
				logAuthorEmail = logInfo[2];
				if(logAuthorEmail.length()>100) logAuthorEmail = logAuthorEmail.substring(0, 99);
				
				this.commitLogsMap.put(new Integer(++logCounter), new CommitLog(logDesc, logAuthor, logAuthorEmail));				
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

	public Map<Integer, CommitLog> getCommitLogsMap() {
		return commitLogsMap;
	}

	public void setCommitLogsMap(Map<Integer, CommitLog> commitLogsMap) {
		this.commitLogsMap = commitLogsMap;
	}
	
	
}
