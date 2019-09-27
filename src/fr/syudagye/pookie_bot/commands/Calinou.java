package fr.syudagye.pookie_bot.commands;

import fr.syudagye.pookie_bot.Command;
import fr.syudagye.pookie_bot.JDAManager;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Calinou extends Command {

	public Calinou(JDAManager jda) {
		super(jda, PUBLIC, "calinou", "");
		setDescription("<:owogrr:615654243558359278>");
	}

	@Override
	public void run(MessageReceivedEvent event, String[] args) {
		event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", moa jaim lé cal1 :hugging:").queue();
	}

}
