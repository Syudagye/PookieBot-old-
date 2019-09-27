package fr.syudagye.pookie_bot.xml.mutes;

public class MuteObject {
	
	private String name;
	private String id;
	private String time;
	private String since;
	private String reason;
	
	public MuteObject(String name, String id, String time, String since, String reason) {
		this.name = name;
		this.id = id;
		this.time = time;
		this.since = since;
		this.reason = reason;
	}
	
	public MuteObject() {
	}
	
	public int getTimeAsInt() {
		return Integer.parseInt(time);
	}
	
	public int getSinceAsInt() {
		return Integer.parseInt(since);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSince() {
		return since;
	}

	public void setSince(String since) {
		this.since = since;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
