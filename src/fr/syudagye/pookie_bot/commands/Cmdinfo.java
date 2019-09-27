package fr.syudagye.pookie_bot.commands;

import fr.syudagye.pookie_bot.Command;
import fr.syudagye.pookie_bot.JDAManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class Cmdinfo extends Command{

	public Cmdinfo(JDAManager jda) {
		super(jda, PUBLIC, "cmdinfo", "<commande>");
		setDescription("Permet de voir les details d'une commande");
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		Command tmp = null;
		for(Command cmd : getJda().getMain().commands) {
			if(cmd.getName().equals(args[1])) {
				tmp = cmd;
			}
		}
		
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(new Color(0xFF0000));
		embed.setTitle(":information_source: Informations Commande");
		embed.setDescription(tmp.getName());
		embed.addField("Arguments", tmp.getArgs(), false);
		embed.addField("Description", tmp.getDescription(), false);
		String[] perms = {"Public", "Staff", "Admin"};
		embed.addField("Permission" , perms[tmp.getAccess()], false);
		
		event.getChannel().sendMessage(embed.build()).queue();
	}
	

}
