package de.tum.in.db.recodev.dataCollection;

public class CommitLog {
	private String logDescription;
	private String logAuthor;
	private String logAuthorEmail;
	
	public CommitLog(String logDescription, String logAuthor, String logAuthorEmail) {
		this.logDescription = logDescription;
		this.logAuthor = logAuthor;
		this.logAuthorEmail = logAuthorEmail;
	}
	
	public String getLogDescription() {
		return logDescription;
	}
	public void setLogDescription(String logDescription) {
		this.logDescription = logDescription;
	}
	public String getLogAuthor() {
		return logAuthor;
	}
	public void setLogAuthor(String logAuthor) {
		this.logAuthor = logAuthor;
	}
	public String getLogAuthorEmail() {
		return logAuthorEmail;
	}
	public void setLogAuthorEmail(String logAuthorEmail) {
		this.logAuthorEmail = logAuthorEmail;
	}
	

}
