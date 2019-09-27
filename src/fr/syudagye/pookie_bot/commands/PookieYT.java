package fr.syudagye.pookie_bot.commands;

import fr.syudagye.pookie_bot.Command;
import fr.syudagye.pookie_bot.JDAManager;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PookieYT extends Command{

	public PookieYT(JDAManager jda) {
		super(jda, PUBLIC, "pookieyt", "");
		setDescription("__***LA***__ chaine youtube !");
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		event.getChannel().sendMessage(":white_check_mark: https://www.youtube.com/channel/UCBae7ZZjmhRpMY0KIVkWg9Q").queue();
	}

}
