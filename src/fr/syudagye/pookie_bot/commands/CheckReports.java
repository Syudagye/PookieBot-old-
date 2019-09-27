package fr.syudagye.pookie_bot.commands;

import fr.syudagye.pookie_bot.Command;
import fr.syudagye.pookie_bot.JDAManager;
import fr.syudagye.pookie_bot.xml.reports.ReportObject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.function.Consumer;

public class CheckReports extends Command {
	
	private String userTag;
	private String msgID;
	private String msgChannel;
	private boolean opened;
	private boolean firstPage;
	private int page = 0;
	
	public CheckReports(JDAManager jda) {
		super(jda, ADMIN_ONLY, "checkreports", "");
		setDescription("Liste des reports");
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		getJda().getMain().getReportsFile().readFile();
		
		if(opened) {
			event.getChannel().sendMessage(":x: La liste est déjà ouverte par " + userTag).queue();
			return;
		}else if(getJda().getMain().getReportsFile().reports.size() == 0) {
			event.getChannel().sendMessage(":x: La liste de reports est vide").queue();
			return;
		}
		
		userTag = event.getAuthor().getAsMention();
		msgChannel = event.getChannel().getId();
		opened = true;
		firstPage = true;
		
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(new Color(0xFF0000));
		embed.setTitle(":triangular_flag_on_post: Liste des Reports");
		embed.setDescription("Nombres de reports : " + getJda().getMain().getReportsFile().reports.size());
		embed.addField("Inteface", ":rewind: Revenir au report precedent \n :no_entry_sign: Supprimer le report \n :fast_forward: Report suivant \n :x: Quitter", false);
		embed.setFooter("Ouvert par " + event.getGuild().getMemberById(userTag.substring(2, 20)).getUser().getName(), event.getGuild().getMemberById(userTag.substring(2, 20)).getUser().getAvatarUrl());
		
		Consumer<Message> callback = (msg) -> reactions(msg);
		event.getChannel().sendMessage(embed.build()).queue(callback);
		
	}
	
	public void reactionEvents(MessageReactionAddEvent event) {
		if(!opened) return;
		if(!event.getMember().getAsMention().equals(userTag)) return;
		getJda().getMain().getReportsFile().readFile();
		event.getReaction().removeReaction(event.getUser()).queue();
		if((event.getReactionEmote().getName().equals("⏪") || event.getReactionEmote().getName().equals("🚫")) && firstPage) return;
		
		if(event.getReactionEmote().getName().equals("⏪")){
			if(page > 1) {
				page--;
				ReportObject rp = getJda().getMain().getReportsFile().reports.get(page - 1);
				event.getGuild().getTextChannelById(msgChannel).editMessageById(msgID, writeEmbed(rp, event).build()).queue();				
			}
		}else if(event.getReactionEmote().getName().equals("⏩")) {
			if(firstPage) firstPage = false;
			if(page < getJda().getMain().getReportsFile().reports.size()) {
				page++;
				ReportObject rp = getJda().getMain().getReportsFile().reports.get(page - 1);
				event.getGuild().getTextChannelById(msgChannel).editMessageById(msgID, writeEmbed(rp, event).build()).queue();	
			}else {
				page++;
				EmbedBuilder embed = new EmbedBuilder();
				embed.setColor(new Color(0xFF0000));
				embed.setTitle(":triangular_flag_on_post: Liste des Reports");
				embed.setDescription("Fin des reports");
				embed.setFooter("Ouvert par " + event.getGuild().getMemberById(userTag.substring(2, 20)).getUser().getName(), event.getGuild().getMemberById(userTag.substring(2, 20)).getUser().getAvatarUrl());
				event.getGuild().getTextChannelById(msgChannel).editMessageById(msgID, embed.build()).queue();
			}
		}else if(event.getReactionEmote().getName().equals("🚫")) {
			getJda().getMain().getReportsFile().reports.remove(page - 1);
			page--;
			if(page == 0) page++;
			if(getJda().getMain().getReportsFile().reports.size() == 0) {
				EmbedBuilder embed = new EmbedBuilder();
				embed.setColor(new Color(0xFF0000));
				embed.setTitle(":triangular_flag_on_post: Liste des Reports");
				embed.setDescription("Fin des reports");
				embed.setFooter("Ouvert par " + event.getGuild().getMemberById(userTag.substring(2, 20)).getUser().getName(), event.getGuild().getMemberById(userTag.substring(2, 20)).getUser().getAvatarUrl());
				event.getGuild().getTextChannelById(msgChannel).editMessageById(msgID, embed.build()).queue();				
			}else {
				ReportObject rp = getJda().getMain().getReportsFile().reports.get(page - 1);
				event.getGuild().getTextChannelById(msgChannel).editMessageById(msgID, writeEmbed(rp, event).build()).queue();
			}
		}else if(event.getReactionEmote().getName().equals("❌")) {
			event.getGuild().getTextChannelById(msgChannel).deleteMessageById(msgID).queue();
			opened = false;
		}
		getJda().getMain().getReportsFile().writeFile();
	}
	
	private EmbedBuilder writeEmbed(ReportObject report, MessageReactionAddEvent event) {
		
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(new Color(0xFF0000));
		embed.setTitle(":triangular_flag_on_post: Liste des Reports");
		embed.setDescription(page + "/" + getJda().getMain().getReportsFile().reports.size() + "\n --=+=-- \n" + report.getId());
		embed.addField("Reporté par", report.getAuthorId(), false);
		embed.addField("Raison", report.getReason(), false);
		embed.setFooter("Ouvert par " + event.getGuild().getMemberById(userTag.substring(2, 20)).getUser().getName(), event.getGuild().getMemberById(userTag.substring(2, 20)).getUser().getAvatarUrl());
		
		return embed;
	}
	
	private void reactions(Message msg) {
		msgID = msg.getId();
		msg.addReaction("⏪").queue();
		msg.addReaction("🚫").queue();
		msg.addReaction("⏩").queue();
		msg.addReaction("❌").queue();
	}
	

	public String getUserTag() {
		return userTag;
	}

	public void setUserTag(String userTag) {
		this.userTag = userTag;
	}

	public String getMsgID() {
		return msgID;
	}

	public void setMsgID(String msgID) {
		this.msgID = msgID;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getMsgChannel() {
		return msgChannel;
	}

	public void setMsgChannel(String msgChannel) {
		this.msgChannel = msgChannel;
	}

	public boolean isFirstPage() {
		return firstPage;
	}

	public void setFirstPage(boolean firstPage) {
		this.firstPage = firstPage;
	}

}
