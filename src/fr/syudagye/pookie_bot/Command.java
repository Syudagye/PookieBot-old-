package fr.syudagye.pookie_bot;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Command {
	
	public final static int PUBLIC = 0;
	public final static int STAFF_ONLY = 1;
	public final static int ADMIN_ONLY = 2;
	
	private JDAManager jda;
	
	private int access;
	private int argsTotal;
	private String name;
	private String args;
	private String description;
	
	public Command(JDAManager jda, int access, String name, String args) {
		this.jda = jda;
		setAccess(access);
		setName(name);
		setArgs(args);
		argsTotal = args.split("<").length - 1;
	}
	
	public abstract void run(MessageReceivedEvent event, String[] args);
	

	public int getAccess() {
		return access;
	}

	public void setAccess(int access) {
		this.access = access;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JDAManager getJda() {
		return jda;
	}

	public void setJda(JDAManager jda) {
		this.jda = jda;
	}

	public int getArgsTotal() {
		return argsTotal;
	}

	public void setArgsTotal(int argsTotal) {
		this.argsTotal = argsTotal;
	}
	
}
