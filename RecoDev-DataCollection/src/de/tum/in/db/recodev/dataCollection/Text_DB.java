	/*
	 * RecoDev
	 * Text_DB.java
	 * <OWNER> = Amir H. Moin (moin@in.tum.de)
	 * <ORGANIZATION> = Tehnische Universität München
	 * <YEAR> = 2015
	 * License: BSD 3-Clause (https://opensource.org/licenses/BSD-3-Clause)
	 */

package de.tum.in.db.recodev.dataCollection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Text_DB {
	
	private BugRepository bugRepository = null;
	
	public Text_DB(BugRepository bugRepository) {
		this.bugRepository = bugRepository;
	}
	
	public void createIssueProfiles() {
		File dir = new File("issues");
		dir.mkdir();
		
		for (int i = 0; i < this.getBugRepository().getIssuesIDs().size(); i++) {
			try {
				File file = new File(Main.path +  "/issues-" + Main.projectName + "/" + this.getBugRepository().getIssuesIDs().get(i).toString() + ".txt");
				FileWriter fileWriter = new FileWriter(file);	
				fileWriter.write(this.getBugRepository().getIssues().get(this.getBugRepository().getIssuesIDs().get(i)).getDescription() + "\r\n\r\n" + this.getBugRepository().getIssues().get(this.getBugRepository().getIssuesIDs().get(i)).getComments());
				fileWriter.close();
			} catch (IOException ioException) {
				System.err.println("Please make sure that the directory named \"issues-projectName\" exists...For more info, please refer to the readme file!");
				ioException.printStackTrace();
			}
		}
		
	}

	public BugRepository getBugRepository() {
		return bugRepository;
	}

	public void setBugRepository(BugRepository bugRepository) {
		this.bugRepository = bugRepository;
	}
}
