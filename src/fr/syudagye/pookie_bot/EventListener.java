package fr.syudagye.pookie_bot;

import fr.syudagye.pookie_bot.commands.CheckReports;
import fr.syudagye.pookie_bot.commands.Config;
import fr.syudagye.pookie_bot.commands.mute.Mute;
import fr.syudagye.pookie_bot.commands.poll.Poll;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.function.Consumer;

public class EventListener extends ListenerAdapter{
	
	private JDAManager jda;
	
	private String lastConfMsgId, lastConfChannelId, lastLilPopupId;
	private long lastConfMsgTime;
	private boolean lastConfigDead = true;
	
	public EventListener(JDAManager jda) {
		this.jda = jda;
	}
	
	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("Bot ready");
		LogSystem.log("Bot ready");
		System.out.println("Connected to " + event.getGuildAvailableCount() + " server(s)");
		LogSystem.log("Connected to " + event.getGuildAvailableCount() + " server(s)");
		LogSystem.log("Game Activity set to : " + Main.updateGame());
		jda.getMain().getConsole().launch();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return;
		
		String[] args = event.getMessage().getContentRaw().split(" ");
		
		//Commands
		for(Command cmd : jda.getMain().commands) {
			if(args[0].contains(jda.getMain().getPrefix() + cmd.getName())) {
				if(cmd.getAccess() == Command.STAFF_ONLY) {
					if(event.getMember().hasPermission(Permission.MESSAGE_MANAGE) || event.getMember().isOwner()) {
						if(args.length > cmd.getArgsTotal()) {
							cmd.run(event, args);
						}else {
							event.getChannel().sendMessage(":x: La commande ne contiens pas un les bons arguments. La syntaxe attendue est : \n `" + jda.getMain().getPrefix() + cmd.getName() + " " + cmd.getArgs() + "`").queue();
						}
					}else {
						event.getChannel().sendMessage(":x: **Dommage**, tu ne fait pas partie du STAFF !").queue();
					}
				}else if(cmd.getAccess() == Command.ADMIN_ONLY){
					if(event.getMember().hasPermission(Permission.ADMINISTRATOR) || event.getMember().isOwner()) {
						if(args.length > cmd.getArgsTotal()) {
							cmd.run(event, args);
						}else {
							event.getChannel().sendMessage(":x: La commande ne contiens pas un les bons arguments. La syntaxe attendue est : \n `" + jda.getMain().getPrefix() + cmd.getName() + " " + cmd.getArgs() + "`").queue();
						}
					}else {
						event.getChannel().sendMessage(":x: **Dommage**, tu n'est pas ADMINISTRATEUR ;D !").queue();
					}
				}else if(cmd.getAccess() == Command.PUBLIC){
					if(args.length > cmd.getArgsTotal()) {
						cmd.run(event, args);
					}else {
						event.getChannel().sendMessage(":x: La commande ne contiens pas un les bons arguments. La syntaxe attendue est : \n `" + jda.getMain().getPrefix() + cmd.getName() + " " + cmd.getArgs() + "`").queue();
					}
				}
			}
		}
		
		Config.configSubCmd(event, args);
		
		Mute.mutesCheck(event, args);

		Poll poll = null;
		for(Command cmd : jda.getMain().commands){
			if(cmd instanceof Poll) poll = (Poll) cmd;
		}
		assert poll != null;
		poll.bodyUpdate(event, args);
		poll.reactionsUpdate(event, args);
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if(event.getMember().getUser().isBot()) return;
		
		if(event.getReactionEmote().getName().equals("✅")) {
			if(!jda.getMain().getRulesMessageID().isEmpty()) {
				if(event.getMessageId().equals(jda.getMain().getRulesMessageID())) {
					event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRoleById(jda.getMain().roles.get("member").substring(3, 21))).queue();
					event.getGuild().getController().removeSingleRoleFromMember(event.getMember(), event.getGuild().getRoleById(jda.getMain().roles.get("unverified").substring(3, 21))).queue();
					Consumer<PrivateChannel> callback = (channel) -> channel.sendMessage("**Merci pour ta coopération !**\n> Bonne aventure a toi sur le Pookie serv !").queue();
					event.getMember().getUser().openPrivateChannel().queue(callback);
				}
			}
		}
		CheckReports cp = null;
		Poll poll = null;
		for(Command cmd : jda.getMain().commands) {
			if (cmd instanceof CheckReports) cp = (CheckReports) cmd;
			if (cmd instanceof Poll) poll = (Poll) cmd;
		}
		assert cp != null;
		cp.reactionEvents(event);
		assert poll != null;
		poll.reactionEvent(event);
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(new Color(0xFF0000));
		embed.setTitle("Bienvenue sur le **Pookie Serv** !");
		embed.setDescription("Pour acceder au reste du serveur, tu dois lire et valider les règles avec :white_check_mark: dans <#501474116080238602>");
		Consumer<PrivateChannel> callback = (channel) -> channel.sendMessage(embed.build()).queue();
		event.getMember().getUser().openPrivateChannel().queue(callback);
		event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRoleById(jda.getMain().roles.get("unverified").substring(3, 21))).queue();
		event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRoleById(jda.getMain().roles.get("levels").substring(3, 21))).queue();
		LogSystem.log("Nouveau membre : " + event.getMember().getUser().getAsTag());
	}

	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		LogSystem.log(event.getMember().getUser().getAsTag() + " a quitter le server");
	}

	public JDAManager getJda() {
		return jda;
	}

	public void setJda(JDAManager jda) {
		this.jda = jda;
	}

	public String getLastConfMsgId() {
		return lastConfMsgId;
	}

	public void setLastConfMsgId(String lastConfMsgId) {
		this.lastConfMsgId = lastConfMsgId;
	}

	public String getLastConfChannelId() {
		return lastConfChannelId;
	}

	public void setLastConfChannelId(String lastConfChannelId) {
		this.lastConfChannelId = lastConfChannelId;
	}

	public boolean isLastConfigDead() {
		return lastConfigDead;
	}

	public void setLastConfigDead(boolean lastConfigDead) {
		this.lastConfigDead = lastConfigDead;
	}

	public long getLastConfMsgTime() {
		return lastConfMsgTime;
	}

	public void setLastConfMsgTime(long lastConfMsgTime) {
		this.lastConfMsgTime = lastConfMsgTime;
	}

	public String getLastLilPopupId() {
		return lastLilPopupId;
	}

	public void setLastLilPopupId(String lastLilPopupId) {
		this.lastLilPopupId = lastLilPopupId;
	}
	
}