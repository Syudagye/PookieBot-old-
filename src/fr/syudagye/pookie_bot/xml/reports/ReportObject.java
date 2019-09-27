package fr.syudagye.pookie_bot.xml.reports;

public class ReportObject {
	
	private String name;
	private String id;
	private String reason;
	private String authorId;
	
	public ReportObject(String name, String id, String reason, String authorId) {
		this.name = name;
		this.id = id;
		this.reason = reason;
		this.authorId = authorId;
	}
	
	public ReportObject() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}
}
