package fr.syudagye.pookie_bot.commands.mute;

import fr.syudagye.pookie_bot.Command;
import fr.syudagye.pookie_bot.JDAManager;
import fr.syudagye.pookie_bot.LogSystem;
import fr.syudagye.pookie_bot.Main;
import fr.syudagye.pookie_bot.xml.mutes.MuteObject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class Mute extends Command{

	private String tagtmp;
	
	public Mute(JDAManager jda) {
		super(jda, STAFF_ONLY, "mute", "<@membre> <raison> <durée en heure>");
		setDescription("Mute une personne specifique pendant un temps donné");
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		getJda().getMain().getMutesFile().readFile();
		
		if(!getJda().getMain().roles.containsKey("muted")){
			event.getChannel().sendMessage(":warning: Le role `muted` n'est pas spécifié dans la liste des roles").queue();
			return;
		}
		
		boolean nameContained = false;
		for(MuteObject mo : getJda().getMain().getMutesFile().mutes) {
			if(mo.getName().equals(event.getGuild().getMemberById(args[1].substring(2, 20)).getUser().getName())) {
				nameContained = true;
			}
		}
		if(nameContained) {
			MuteObject mute = null;
			int count = 0;
			for(MuteObject mo : getJda().getMain().getMutesFile().mutes) {
				if(mo.getName().equals(event.getGuild().getMemberById(args[1].substring(2, 20)).getUser().getName())) {
					mute = mo;
				}
				count++;
			}
			getJda().getMain().getMutesFile().mutes.remove(count);

			assert mute != null;
			mute.setTime(Integer.toString(mute.getTimeAsInt() + (Integer.parseInt(args[3]) * 3600)));
			mute.setReason(mute.getReason() + " | " + args[2]);

			getJda().getMain().getMutesFile().mutes.add(mute);

			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(new Color(0xFF0000));
			embed.setTitle(":mute: Mute");
			embed.setDescription(args[1]);
			embed.addField("Muté par", event.getAuthor().getAsMention(), true);
			embed.addField("Raisons", mute.getReason(), true);
			embed.addField("Temps", args[3] + "h de plus", true);
			embed.addField("Temps Total", mute.getTimeAsInt() / 3600 + "h", true);
			
			event.getChannel().sendMessage(embed.build()).queue();
			LogSystem.log("[MUTE] " + event.getGuild().getMemberById(mute.getId().substring(2, 20)).getUser().getAsTag() + " muté par " + event.getAuthor().getAsTag() + " pour " + mute.getReason() + " pendant " + mute.getTime());
		}else {
			event.getGuild().getController().addSingleRoleToMember(event.getGuild().getMemberById(args[1].substring(2, 20)), event.getGuild().getRoleById(getJda().getMain().roles.get("muted").substring(3, 21))).queue();
			
			MuteObject mute = new MuteObject(event.getGuild().getMemberById(args[1].substring(2, 20)).getUser().getName(), args[1], Integer.toString(Integer.parseInt(args[3]) * 3600), Integer.toString((int) (System.nanoTime() / 1000000000)), args[2]);
			getJda().getMain().getMutesFile().mutes.add(mute);
			
			EmbedBuilder embed = new EmbedBuilder();
			embed.setColor(new Color(0xFF0000));
			embed.setTitle(":mute: Mute");
			embed.setDescription(args[1]);
			embed.addField("Muté par", event.getAuthor().getAsMention(), true);
			embed.addField("Raison", args[2], true);
			embed.addField("Temps", args[3] + "h", true);
			
			event.getChannel().sendMessage(embed.build()).queue();
			LogSystem.log("[MUTE] " + event.getGuild().getMemberById(mute.getId().substring(2, 20)).getUser().getAsTag() + " muté par " + event.getAuthor().getAsTag() + " pour " + mute.getReason() + " pendant " + mute.getTime());
		}
		getJda().getMain().getMutesFile().writeFile();
	}
	
	public static void mutesCheck(MessageReceivedEvent event, String[] args) {
		Main.Bot.getMain().getMutesFile().readFile();
		
		int count = 0;
		for(MuteObject mo : Main.Bot.getMain().getMutesFile().mutes) {
			if((mo.getSinceAsInt() + mo.getTimeAsInt()) < (System.nanoTime() / 1000000000)) {
				event.getGuild().getController().removeSingleRoleFromMember(event.getGuild().getMemberById(mo.getId().substring(2, 20)), event.getGuild().getRoleById(Main.Bot.getMain().roles.get("muted").substring(3, 21))).queue();
				
				Main.Bot.getMain().getMutesFile().mutes.remove(count);
				
				event.getChannel().sendMessage(":white_check_mark: " + event.getGuild().getMemberById(mo.getId().substring(2, 20)).getAsMention() + " n'est plus muté").queue();
				LogSystem.log("[MUTE] " + event.getGuild().getMemberById(mo.getId().substring(2, 20)).getUser().getAsTag() + " a été unmute");
			}
			count++;
		}
		Main.Bot.getMain().getMutesFile().writeFile();
	}

	public String getTagtmp() {
		return tagtmp;
	}

	public void setTagtmp(String tagtmp) {
		this.tagtmp = tagtmp;
	}

}
