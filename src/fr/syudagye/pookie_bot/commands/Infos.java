package fr.syudagye.pookie_bot.commands;

import fr.syudagye.pookie_bot.Command;
import fr.syudagye.pookie_bot.JDAManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class Infos extends Command{

	public Infos(JDAManager jda) {
		super(jda, PUBLIC, "infos", "");
		setDescription("Informations sur le PookieBot");
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(new Color(0xFF0000));
		embed.setTitle(":information_source: **Informations sur le PookieBot**");
		embed.addField("Créateur", "<@464119931282391061>", true);
		embed.addField("Version", "v1.1", true);
		embed.addField("GitHub", "lien", true);
		
		event.getChannel().sendMessage(embed.build()).queue();
	}
	
}
