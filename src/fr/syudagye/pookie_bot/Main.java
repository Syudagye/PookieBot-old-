package fr.syudagye.pookie_bot;

import fr.syudagye.pookie_bot.commands.*;
import fr.syudagye.pookie_bot.commands.mute.Mute;
import fr.syudagye.pookie_bot.commands.mute.MutesList;
import fr.syudagye.pookie_bot.commands.mute.UnMute;
import fr.syudagye.pookie_bot.commands.poll.Poll;
import fr.syudagye.pookie_bot.xml.ConfigFile;
import fr.syudagye.pookie_bot.xml.mutes.Mutes;
import fr.syudagye.pookie_bot.xml.poll.PollsFile;
import fr.syudagye.pookie_bot.xml.reports.ReportsFile;
import net.dv8tion.jda.core.entities.Game;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
	
	public static JDAManager Bot;
	public static EventListener eventListener;
	static final String JAR_LOCATION = getJarLocation();
	
	private ConfigFile configFile;
	private Mutes mutesFile;
	private ReportsFile reportsFile;
	private PollsFile pollsFile;
	private ConsoleManager console;
	private String token = null;
	private String prefix = "p!";
	private String rulesMessageID;
	public HashMap<String, String> channels = new HashMap<>();
	public HashMap<String, String> roles = new HashMap<>();
	public ArrayList<Command> commands = new ArrayList<>();

	public static void main(String[] args) {
		Bot = new JDAManager(new Main());
	}
	
	void init(JDAManager jda) {
		LogSystem.init();
		eventListener = new EventListener(jda);
		configFile = new ConfigFile(jda, new File(JAR_LOCATION, "config.xml"));
		mutesFile = new Mutes(jda, new File(JAR_LOCATION, "mutes.xml"));
		reportsFile = new ReportsFile(jda, new File(JAR_LOCATION, "reports.xml"));
		pollsFile = new PollsFile(jda, new File(JAR_LOCATION, "polls.xml"));
		console = new ConsoleManager(jda);
		initCommands(jda);
		if(token == null || token.equals("")) {
	        try {
	            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	            System.out.print("Enter token here : ");
	            token = br.readLine();	            
			} catch (IOException e) {
				e.printStackTrace();
			}
	        configFile.writeFile();
		}
		jda.buildJDA(eventListener);
	}
	
	private void initCommands(JDAManager jda) {
		commands.add(new Config(jda));
		commands.add(new Help(jda));
		commands.add(new PookieYT(jda));
		commands.add(new Infos(jda));
		commands.add(new Calinou(jda));
		commands.add(new Mute(jda));
		commands.add(new Cmdinfo(jda));
		commands.add(new UnMute(jda));
		commands.add(new MutesList(jda));
		commands.add(new Report(jda));
		commands.add(new CheckReports(jda));
		commands.add(new Poll(jda));
	}
	
	public static String updateGame() {
		String activity = "Fortnite (ptdr non), " + Bot.getMain().getPrefix() + "help";
		Bot.getJda().getPresence().setGame(Game.playing(activity));
		return activity;
	}

	private static String getJarLocation(){
		String dev_path = Main.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		path = path.substring(0, path.indexOf("Pookie_Bot"));
		//dev_path = quand on développe
        //path = quand le code est compilé sous forme de jar
		return dev_path;
	}

	public ConfigFile getConfigFile() {
		return configFile;
	}

	public void setConfigFile(ConfigFile configFile) {
		this.configFile = configFile;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public ConsoleManager getConsole() {
		return console;
	}

	public void setConsole(ConsoleManager console) {
		this.console = console;
	}

	public String getRulesMessageID() {
		return rulesMessageID;
	}

	public void setRulesMessageID(String rulesMessageID) {
		this.rulesMessageID = rulesMessageID;
	}

	public Mutes getMutesFile() {
		return mutesFile;
	}

	public void setMutesFile(Mutes mutesFile) {
		this.mutesFile = mutesFile;
	}

	public ReportsFile getReportsFile() {
		return reportsFile;
	}

	public void setReportsFile(ReportsFile reportsFile) {
		this.reportsFile = reportsFile;
	}

	public PollsFile getPollsFile() {
		return pollsFile;
	}

	public void setPollsFile(PollsFile pollsFile) {
		this.pollsFile = pollsFile;
	}
}
