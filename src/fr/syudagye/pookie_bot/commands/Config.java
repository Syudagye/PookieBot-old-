package fr.syudagye.pookie_bot.commands;

import fr.syudagye.pookie_bot.Command;
import fr.syudagye.pookie_bot.JDAManager;
import fr.syudagye.pookie_bot.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Config extends Command {	

	public Config(JDAManager jda) {
		super(jda, ADMIN_ONLY, "config", "");
		setDescription("Permet de modifier les parametres généraux du bot");
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		Consumer<Message> callback = (response) -> Main.eventListener.setLastLilPopupId(response.getId());
		event.getChannel().sendMessage(":white_check_mark: Le panneau de configuration est ouvert").queue(callback);
		EmbedBuilder embed = new EmbedBuilder();
		embed.setColor(new Color(0xFF0000));
		embed.setTitle(":gear: **Configuration du PookieBot**");
		embed.setDescription(":warning: Pour quitter ce menu tapez `close`\n__Attention :__ Ce menu se fermera automatiquement au bout de 60 secondes sans interaction \n"
				+ "Pour rafraichir ce menu, tapez `refresh`");
		embed.addField("__Prefix__", "Voici le prefix actuellement utilisé : `" + getJda().getMain().getPrefix()
				+ "`\n --=+=-- \n"
				+ "Pour changer la prefix, tapez \n `setprefix <prefix>`", false);
	
		embed.addField("__Message de règlement__", "ID du message : " + getJda().getMain().getRulesMessageID()
				+ "\n --=+=-- \n"
				+ "pour mettre en place un message de réglement tapez \n"
				+ ":warning: le channel `rules` et les roles `member` et `unverified` doivent etre spécifiés \n"
				+ "`setrulesmessage <ID du message>`\n"
				+ "pour envoyer un nouveau message de règlement tapez \n"
				+ "`newrulesmessage`", false);
		//channels
		StringBuffer channelstr = new StringBuffer();
		getJda().getMain().channels.forEach(new BiConsumer<String, String>() {

			@Override
			public void accept(String k, String v) {
				if(k.contains("null")) {
					return;
				}else {
					channelstr.append(k + " : " + v + "\n");
				}
			}
		});		
		embed.addField("__Channels__", channelstr.toString()
				+ "--=+=-- \n"
				+ "Pour changer un channel tapez \n"
				+ "` setchannel <fonction du channel> <#nom du channel>` \n"
				+ "Pour Ajouter un channel tapez \n"
				+ "`addchannel <fonction du channel> <#nom du channel>` \n"
				+ "Poiur supprimer un channel tapez \n"
				+ "`delchannel <fonction du channel>`", false);
		//roles
		StringBuffer rolestr = new StringBuffer();
		getJda().getMain().roles.forEach(new BiConsumer<String, String>() {

			@Override
			public void accept(String k, String v) {
				if(k.contains("null")) {
					return;
				}else {
					rolestr.append(k + " : " + v + "\n");
				}
			}
		});
		embed.addField("__Roles__", rolestr.toString()
				+ "--=+=-- \n"
				+ "Pour changer un role tapez \n"
				+ "`setrole <fonction du role> <@nome du role>` \n"
				+ "Pour Ajouter un role tapez \n"
				+ "`addrole <fonction du role> <@nome du role>` \n"
				+ "Pour supprimer un role tapez \n"
				+ "`delrole <fonction du role>`", false);
		
		callback = (response) -> confEmbedCallback(response);
		event.getChannel().sendMessage(embed.build()).queue(callback);
	}
	
	private void confEmbedCallback(Message msg) {
		Main.eventListener.setLastConfigDead(false);
		Main.eventListener.setLastConfMsgId(msg.getId());
		Main.eventListener.setLastConfChannelId(msg.getChannel().getId());
		Main.eventListener.setLastConfMsgTime(System.nanoTime() / 1000000000);
	}
	
	private static void rulesmsgCallback(Message msg) {
		msg.addReaction("✅").queue();
		Main.Bot.getMain().setRulesMessageID(msg.getId());
		System.out.println(msg.getId());
	}
	
	public static void configSubCmd(MessageReceivedEvent event, String[] args) {
		
		if(Main.eventListener.isLastConfigDead()) return;
		if(!event.getMember().hasPermission(Permission.ADMINISTRATOR)) return;		


		if(Main.eventListener.getLastConfMsgTime() + 60 < System.nanoTime() / 1000000000 || args[0].contains("close")) {
			event.getGuild().getTextChannelById(Main.eventListener.getLastConfChannelId()).deleteMessageById(Main.eventListener.getLastConfMsgId()).queue();
			event.getGuild().getTextChannelById(Main.eventListener.getLastConfChannelId()).editMessageById(Main.eventListener.getLastLilPopupId(), ":x: Le panneau de confiuration a été fermé").queue();
			Main.eventListener.setLastConfigDead(true);
			Main.Bot.getMain().getConfigFile().writeFile();
			Main.updateGame();
		}else if(args[0].contains("refresh")){
			event.getGuild().getTextChannelById(Main.eventListener.getLastConfChannelId()).deleteMessageById(Main.eventListener.getLastConfMsgId()).queue();
			event.getGuild().getTextChannelById(Main.eventListener.getLastConfChannelId()).deleteMessageById(Main.eventListener.getLastLilPopupId()).queue();
			for(Command cmd : Main.Bot.getMain().commands) {
				if(cmd.getName() == "config") {
					cmd.run(event, args);
				}
			}
		}else if(args[0].contains("setprefix")) {		
			Main.Bot.getMain().setPrefix(args[1]);
			event.getMessage().delete().queue();
			event.getChannel().sendMessage(":gear: Le prefix est maintenant `" + Main.Bot.getMain().getPrefix() + "`").queue();
		}else if(args[0].contains("newrulesmessage")){
			if(!(Main.Bot.getMain().channels.containsKey("rules") && Main.Bot.getMain().roles.containsKey("member") && Main.Bot.getMain().roles.containsKey("unverified"))) {
				event.getChannel().sendMessage(":x: Vous devez d'abord spécifier un channel de règlement nommé `rules` et les roles `member` et `unverified`").queue();
			}else {
				Consumer<Message> callback = (response) -> rulesmsgCallback(response);;
				event.getGuild().getTextChannelById(Main.Bot.getMain().channels.get("rules").substring(2, 20)).sendMessage("**Veiller vocher si dessous __après avoir lu les règles__**").queue(callback);
				event.getMessage().delete().queue();
				event.getChannel().sendMessage(":gear: Le message de règlement est maintenant `" + Main.Bot.getMain().getRulesMessageID() + "` dans le channel `" + Main.Bot.getMain().channels.get("rules")+ "`").queue();
			}			
		}else if(args[0].contains("setrulesmessage")) {
			if(!Main.Bot.getMain().channels.containsKey("rules")) {
				event.getChannel().sendMessage(":x: Vous devez d'abord spécifier un channel de règlement nommé `rules`").queue();
			}else {
				Main.Bot.getMain().setRulesMessageID(args[1]);
				event.getGuild().getTextChannelById(Main.Bot.getMain().channels.get("rules").substring(2, 20)).addReactionById(Main.Bot.getMain().getRulesMessageID(), "✅").queue();
				event.getMessage().delete().queue();
				event.getChannel().sendMessage(":gear: Le message de règlement est maintenant `" + Main.Bot.getMain().getRulesMessageID() + "` dans le channel " + Main.Bot.getMain().channels.get("rules")).queue();
			}			
		}else if(args[0].contains("setchannel")) {
			if(!Main.Bot.getMain().channels.containsKey(args[1])) {
				event.getChannel().sendMessage(":x: Le channel `" + args[1] + "` n'est pas spécifié").queue();
			}else {
				Main.Bot.getMain().channels.replace(args[1], args[2]);
				event.getMessage().delete().queue();
				event.getChannel().sendMessage(":gear: Le channel `" + args[1] + "` correspond désomais avec `" + args[2] + "`").queue();
			}
		}else if(args[0].contains("addchannel")) {
			Main.Bot.getMain().channels.put(args[1], args[2]);
			event.getMessage().delete().queue();
			event.getChannel().sendMessage(":gear: Le channel `" + args[1] + "` correspond désomais avec `" + args[2] + "`").queue();
		}else if(args[0].contains("delchannel")) {
			if(!Main.Bot.getMain().channels.containsKey(args[1])) {
				event.getChannel().sendMessage(":x: Le channel `" + args[1] + "` n'est pas spécifié").queue();
			}else {
				Main.Bot.getMain().channels.remove(args[1]);
				event.getMessage().delete().queue();
				event.getChannel().sendMessage(":gear: Le channel `" + args[1] + "` a été supprimé de la liste").queue();
			}
		}else if(args[0].contains("setrole")) {
			if(!Main.Bot.getMain().roles.containsKey(args[1])) {
				event.getChannel().sendMessage(":x: Le role `" + args[1] + "` n'est pas spécifié").queue();
			}else {
				Main.Bot.getMain().roles.replace(args[1], args[2]);
				event.getMessage().delete().queue();
				event.getChannel().sendMessage(":gear: Le role `" + args[1] + "` correspond désomais avec " + args[2]).queue();
			}
		}else if(args[0].contains("addrole")) {
			Main.Bot.getMain().roles.put(args[1], args[2]);
			event.getMessage().delete().queue();
			event.getChannel().sendMessage(":gear: Le role `" + args[1] + "` correspond désomais avec " + args[2] ).queue();
		}else if(args[0].contains("delrole")) {
			if(!Main.Bot.getMain().roles.containsKey(args[1])) {
				event.getChannel().sendMessage(":x: Le role `" + args[1] + "` n'est pas spécifié").queue();
			}else {
				Main.Bot.getMain().roles.remove(args[1]);
				event.getMessage().delete().queue();
				event.getChannel().sendMessage(":gear: Le role `" + args[1] + "` a été supprimé de la liste").queue();
			}
		}
		Main.eventListener.setLastConfMsgTime(System.nanoTime() / 1000000000);
	}
	
}
