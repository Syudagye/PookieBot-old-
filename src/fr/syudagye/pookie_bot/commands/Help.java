package fr.syudagye.pookie_bot.commands;

import fr.syudagye.pookie_bot.Command;
import fr.syudagye.pookie_bot.JDAManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class Help extends Command{

	public Help(JDAManager jda) {
		super(jda, PUBLIC, "help", "");
		setDescription("Liste des commands et fonctionalités");
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(new Color(0xFF0000));
		embed.setTitle(":information_source: **Menu d'aide du PookieBot**");
		embed.setDescription("Le prefix que vous devez mettre devant les commandes est `" + getJda().getMain().getPrefix() + "`");
		StringBuffer admins = new StringBuffer();
		StringBuffer staff = new StringBuffer();
		StringBuffer publics = new StringBuffer();
		for(Command cmd : getJda().getMain().commands) {
			if(cmd.getAccess() == Command.ADMIN_ONLY) {
				admins.append("`" + cmd.getName() + "` - " + cmd.getDescription() + "\n");
			}else if(cmd.getAccess() == Command.STAFF_ONLY) {
				staff.append("`" + cmd.getName() + "` - " + cmd.getDescription() + "\n");
			}else if(cmd.getAccess() == Command.PUBLIC) {
				publics.append("`" + cmd.getName() + "` - " + cmd.getDescription() + "\n");
			}
		}
		embed.addField("__Commandes Membres :__", publics.toString() + "--=+=--", false);
		embed.addField("__Commandes Staff :__", staff.toString() + "--=+=--", false);
		embed.addField("__Commandes Admins :__", admins.toString() + "--=+=--", false);
		embed.addField("__AH et aussi :__", "- Si vous trouvez des bugs ou des photes dortaugraffe, faites le moi savoir par mp (<@464119931282391061>) ou sur GitHub (https://github.com/Syudagye/PookieBot) \n"
				+ "- Si vous avez des suggestions pour le bot, donnez les dans <#589553198453489674>", false);
		
		event.getChannel().sendMessage(embed.build()).queue();
	}

}
