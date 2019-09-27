package fr.syudagye.pookie_bot.commands.mute;

import fr.syudagye.pookie_bot.Command;
import fr.syudagye.pookie_bot.JDAManager;
import fr.syudagye.pookie_bot.xml.mutes.MuteObject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class MutesList extends Command {

	public MutesList(JDAManager jda) {
		super(jda, STAFF_ONLY, "muteslist", "");
		setDescription("Liste des personnes mutées");
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		getJda().getMain().getMutesFile().readFile();
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(new Color(0xFF0000));
		embed.setTitle(":mute: Liste des Mutes");
		if(getJda().getMain().getMutesFile().mutes.size() == 0) {
			embed.setDescription(":x: Il n'y a aucun mute a relever");
		}else {
			for(MuteObject mo : getJda().getMain().getMutesFile().mutes) {
				int secs = (int) (((mo.getSinceAsInt() + mo.getTimeAsInt()) - (System.nanoTime() / 1000000000)));
				embed.addField(mo.getName(), "Membre : " + mo.getId() + "\n"
						+ "Temps total : " + mo.getTimeAsInt() / 3600 + "\n"
						+ "Temps restant : " + String.format("%02d:%02d:%02d", secs / 3600, (secs % 3600) / 60, secs % 60) + "\n"
						+ "Raison(s) : " + mo.getReason() + "\n"
						+ "--=+=--", false);
			}
			embed.setDescription("Total de mutes : " + getJda().getMain().getMutesFile().mutes.size());
		}
		
		event.getChannel().sendMessage(embed.build()).queue();

	}

}
