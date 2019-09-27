package fr.syudagye.pookie_bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleManager implements Runnable{
	
	private JDAManager jda;
	private Thread thread;

	public ConsoleManager(JDAManager jda) {
		this.jda = jda;
	}
	
	public void launch() {
		thread = new Thread(this);
		thread.start();
	}
	
	public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        boolean tmp = true;
		while(tmp) {
	        try {
	        	System.out.print("PookieBot Console > ");
	        	String entry = br.readLine().toLowerCase();
		        if(entry.contains("stop")) {
		        	jda.getJda().shutdown();
		        	System.out.println("Bot succesfully disconnected and shutted down !");
		        	tmp = false;
		        }else if(entry.contains("help")) {
		        	System.out.println();
		        	System.out.println("All PookieBot's Console commands are here :");
		        	System.out.println();
		        	System.out.println("    STOP : Stop the bot");
		        	System.out.println("    HELP : Shows all the consoles commands");
		        	System.out.println("    TOKEN : Return the bot actual token");
		        	System.out.println("    SAVE : Save the bot's data");
		        	System.out.println();
		        }else if(entry.contains("token")) {
		        	System.out.println(jda.getJda().getToken());
		        }else if(entry.contains("save")) {
		        	jda.getMain().getConfigFile().writeFile();
		        	System.out.println();
		        	System.out.println("Bot's data have been succesfully saved at the following directory : " + jda.getMain().getConfigFile().getFile().getAbsolutePath());
		        	System.out.println();
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
		}
	}
}
