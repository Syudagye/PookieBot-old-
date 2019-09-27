package fr.syudagye.pookie_bot.commands;

import fr.syudagye.pookie_bot.Command;
import fr.syudagye.pookie_bot.JDAManager;
import fr.syudagye.pookie_bot.xml.reports.ReportObject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class Report extends Command{

	public Report(JDAManager jda) {
		super(jda, PUBLIC, "report", "<@membre> <raison (peut etre sous forme de phrase)>");
		setDescription("Report un membre qui fait des betises");
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		getJda().getMain().getReportsFile().readFile();
		
		if(!(args.length >= 3)) return;
		
		String reason = "";
		for(int i = 2; i < args.length; i++) {
			reason = reason + " " + args[i];
		}
		ReportObject report = new ReportObject();
		report.setAuthorId(event.getAuthor().getAsMention());
		report.setId(args[1]);
		report.setName(event.getGuild().getMemberById(args[1].substring(2, 20)).getUser().getName());
		report.setReason(reason);
		
		getJda().getMain().getReportsFile().reports.add(report);
		getJda().getMain().getReportsFile().writeFile();
		
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(new Color(0xFF0000));
		embed.setTitle(":warning: Report");
		embed.setDescription(report.getId());
		embed.addField("Report par", report.getAuthorId(), true);
		embed.addField("Raison", report.getReason(), true);
		
		event.getChannel().sendMessage(embed.build()).queue();
		
	}

}
