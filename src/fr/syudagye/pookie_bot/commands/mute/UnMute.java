package fr.syudagye.pookie_bot.commands.mute;

import fr.syudagye.pookie_bot.Command;
import fr.syudagye.pookie_bot.JDAManager;
import fr.syudagye.pookie_bot.LogSystem;
import fr.syudagye.pookie_bot.Main;
import fr.syudagye.pookie_bot.xml.mutes.MuteObject;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class UnMute extends Command{
	
	private String tagtmp;

	public UnMute(JDAManager jda) {
		super(jda, STAFF_ONLY, "unmute", "<@membre>");
		setDescription("Unmute une personne mutée");
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		getJda().getMain().getMutesFile().readFile();
		
		MuteObject mute = null;
		boolean nameContained = false;
		int index = 0;
		int count = 0;
		for(MuteObject mo : getJda().getMain().getMutesFile().mutes) {
			if(mo.getName().contains(event.getGuild().getMemberById(args[1].substring(2, 20)).getUser().getName())) {
				nameContained = true;
				mute = getJda().getMain().getMutesFile().mutes.get(count);
				index = count;
			}
			count++;
		}
		if(nameContained) {	
			event.getGuild().getController().removeSingleRoleFromMember(event.getGuild().getMemberById(mute.getId().substring(2, 20)), event.getGuild().getRoleById(Main.Bot.getMain().roles.get("muted").substring(3, 21))).queue();
			event.getChannel().sendMessage(":white_check_mark: " + event.getGuild().getMemberById(args[1].substring(2, 20)).getAsMention() + " à été unmute par " + event.getAuthor().getAsMention()).queue();
			
			getJda().getMain().getMutesFile().mutes.remove(index);
			LogSystem.log("[MUTE] " + event.getGuild().getMemberById(mute.getId().substring(2, 20)).getUser().getAsTag() + " a été unmute par " + event.getAuthor().getAsTag());
		}else {
			event.getChannel().sendMessage(":x: " + args[1] + " n'est pas muté").queue();
		}
		
		getJda().getMain().getMutesFile().writeFile();
	}

	public String getTagtmp() {
		return tagtmp;
	}

	public void setTagtmp(String tagtmp) {
		this.tagtmp = tagtmp;
	}

}
