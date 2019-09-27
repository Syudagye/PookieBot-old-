package fr.syudagye.pookie_bot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;

public class JDAManager {
	
	private Main main;
	private JDA jda;
	
	public JDAManager(Main main)  {
		this.main = main;
		main.init(this);
	}
	
	public void buildJDA(EventListener eventer) {
		try {
			jda = new JDABuilder(main.getToken()).build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
		jda.addEventListener(eventer);
	}

	public Main getMain() {
		return main;
	}

	public void setMain(Main main) {
		this.main = main;
	}

	public JDA getJda() {
		return jda;
	}

	public void setJda(JDA jda) {
		this.jda = jda;
	}
	
}
