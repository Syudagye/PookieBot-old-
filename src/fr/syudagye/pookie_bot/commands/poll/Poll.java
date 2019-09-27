package fr.syudagye.pookie_bot.commands.poll;

import fr.syudagye.pookie_bot.Command;
import fr.syudagye.pookie_bot.JDAManager;
import fr.syudagye.pookie_bot.xml.poll.PollObject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Poll extends Command {

    private PollStates pollStates = PollStates.FREE;
    private String name;
    private String userID;
    private String pollChannelID;
    private String body = "null";
    private String msgID, msgChannelID;
    private ArrayList<String> choicesReactions;
    private String resultsMsgID, getResultsMsgChannelID;


    public Poll(JDAManager jda) {
        super(jda, ADMIN_ONLY, "poll", "<create|results> [channel] [name]");
        setDescription("Outils de sondage");
        choicesReactions = new ArrayList<>();
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {
        getJda().getMain().getPollsFile().readFile();
        if(args[1].equals("create")){
            if(pollStates == PollStates.CREATING || pollStates == PollStates.WRITING_BODY || pollStates == PollStates.WRITING_REACTION){
                event.getChannel().sendMessage(":warning: Un sondage est deja en cours de creation par " + userID);
                return;
            }
            if(args.length < 4){
                event.getChannel().sendMessage(":x: La commande ne contiens pas un les bons arguments. La syntaxe attendue est : \n `" + getJda().getMain().getPrefix() + "poll create [channel] [name]`" ).queue();
                return;
            }
            userID = event.getAuthor().getAsMention();
            pollChannelID = args[2];
            pollStates = PollStates.CREATING;
            name = args[3];

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(0xFF0000));
            embed.setTitle(":chart_with_upwards_trend: Sondages");
            embed.setAuthor(event.getAuthor().getName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getAvatarUrl());
            embed.setDescription(":white_check_mark: Valider et poster les sondage \n" +
                    ":arrows_counterclockwise: Réécrire le corps du sondage \n" +
                    ":x: Annuler le sondage \n" +
                    ":heavy_plus_sign: Gerer les reactions");
            embed.addField("Nom du sondage", name, false);
            embed.addField("Corps", body, false);
            String choices = "";
            if(choicesReactions.size() == 0){
                choices = "Aucun choix présent";
            }else{
                for (String str : choicesReactions){
                    choices = choices + " " + str;
                }
            }
            embed.addField("Choix", choices, false);

            Consumer<Message> callback = msg -> creationReactions(msg);
            event.getChannel().sendMessage(embed.build()).queue(callback);

        }else if(args[1].equals("results")){
            if(getJda().getMain().getPollsFile().polls.size() == 0){
                event.getChannel().sendMessage(":x: Aucun sondage actif").queue();
                return;
            }
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(0xFF0000));
            embed.setTitle(":chart_with_upwards_trend: Sondages");
            for(PollObject po : getJda().getMain().getPollsFile().polls){
                AtomicReference<Message> msg = new AtomicReference<>();
                Consumer<Message> callback = msgCallback -> msg.set(msgCallback);
                event.getGuild().getTextChannelById(po.getChannelID().substring(2, 20)).getMessageById(po.getMsgID()).queue(callback);
                String results = "";
                while (msg.get() == null){
                    //Do nothing lmao
                }
                System.out.println(msg.get().getContentRaw());
                List<MessageReaction> reactions = msg.get().getReactions();
                for(MessageReaction mr : reactions){
                    results += mr.getReactionEmote().getName() + " -> " + (mr.getCount() - 1) + "\n";
                }
                embed.addField("Sondage de " + event.getGuild().getMemberById(po.getAuthor().substring(2, 20)).getUser().getName(), " \n Nom : " + po.getName() +
                        "\n Channel : " + po.getChannelID() + "\n Résultats : \n" + reactions, false);
            }
            Consumer<Message> callback = msg -> resultsCallback(msg);
            event.getChannel().sendMessage(embed.build()).queue(callback);


        }else if(args[1].equals("delete")){
            if(args.length < 4){
                event.getChannel().sendMessage(":x: La commande ne contiens pas un les bons arguments. La syntaxe attendue est : \n `" + getJda().getMain().getPrefix() + "poll delete [channel] [name]`" ).queue();
                return;
            }
            for (int i = 0; i < getJda().getMain().getPollsFile().polls.size(); i++){
                if(getJda().getMain().getPollsFile().polls.get(i).getName().equals(args[3])){
                    getJda().getMain().getPollsFile().polls.remove(i);
                    break;
                }
            }

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(0xFF0000));
            embed.setTitle(":chart_with_upwards_trend: Sondages");
            for(PollObject po : getJda().getMain().getPollsFile().polls){
                AtomicReference<Message> msg = new AtomicReference<>();
                Consumer<Message> callback = msgCallback -> msg.set(msgCallback);
                event.getGuild().getTextChannelById(po.getChannelID().substring(2, 20)).getMessageById(po.getMsgID()).queue(callback);
                String results = "";
                while (msg.get() == null){
                    //Do nothing lmao
                }
                System.out.println(msg.get().getContentRaw());
                List<MessageReaction> reactions = msg.get().getReactions();
                for(MessageReaction mr : reactions){
                    results += mr.getReactionEmote().getName() + " -> " + (mr.getCount() - 1) + "\n";
                }
                embed.addField("Sondage de " + event.getGuild().getMemberById(po.getAuthor().substring(2, 20)).getUser().getName(), " \n Nom : " + po.getName() +
                        "\n Channel : " + po.getChannelID() + "\n Résultats : \n" + reactions, false);
            }

            event.getGuild().getTextChannelById(getResultsMsgChannelID).editMessageById(resultsMsgID, embed.build()).queue();
        }
    }

    public void bodyUpdate(MessageReceivedEvent event, String[] args){
        if(!(pollStates == PollStates.WRITING_BODY)) return;
        event.getMessage().delete().queue();
        if (args[0].equals("setbody")){
            String body = "";
            for(int i = 1; i < args.length; i++){
                body = body + " " + args[i];
            }
            this.body = body;

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(0xFF0000));
            embed.setTitle(":chart_with_upwards_trend: Sondages");
            embed.setAuthor(event.getAuthor().getName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getAvatarUrl());
            embed.setDescription(":white_check_mark: Valider et poster les sondage \n" +
                    ":arrows_counterclockwise: Réécrire le corps du sondage" +
                    ":x: Annuler le sondage" +
                    ":heavy_plus_sign: Gerer les reactions");
            embed.addField("Nom du sondage", name, false);
            embed.addField("Corps", body, false);
            String choices = "";
            if(choicesReactions == null || choicesReactions.size() == 0){
                choices = "Aucun choix présent";
            }else{
                for (String str : choicesReactions){
                    choices = choices + " " + str;
                }
            }
            embed.addField("Choix", choices, false);

            event.getGuild().getTextChannelById(msgChannelID).editMessageById(msgID, embed.build()).queue();

            pollStates = PollStates.CREATING;
        }else if (args[0].equals("setname")){
            this.name = body;

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(0xFF0000));
            embed.setTitle(":chart_with_upwards_trend: Sondages");
            embed.setAuthor(event.getAuthor().getName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getAvatarUrl());
            embed.setDescription(":white_check_mark: Valider et poster les sondage \n" +
                    ":arrows_counterclockwise: Réécrire le corps du sondage" +
                    ":x: Annuler le sondage" +
                    ":heavy_plus_sign: Gerer les reactions");
            embed.addField("Nom du sondage", name, false);
            embed.addField("Corps", body, false);
            String choices = "";
            if(choicesReactions == null || choicesReactions.size() == 0){
                choices = "Aucun choix présent";
            }else{
                for (String str : choicesReactions){
                    choices = choices + " " + str;
                }
            }
            embed.addField("Choix", choices, false);

            event.getGuild().getTextChannelById(msgChannelID).editMessageById(msgID, embed.build()).queue();

            pollStates = PollStates.CREATING;
        }
    }

    public void reactionsUpdate(MessageReceivedEvent event, String[] args){
        if(!(pollStates == PollStates.WRITING_REACTION)) return;
        event.getMessage().delete().queue();
        if (args[0].equals("addreaction")){
            for (int i = 1; i < args.length; i++){
                choicesReactions.add(args[i]);
            }

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(0xFF0000));
            embed.setTitle(":chart_with_upwards_trend: Sondages");
            embed.setAuthor(event.getAuthor().getName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getAvatarUrl());
            embed.setDescription(":white_check_mark: Valider et poster les sondage \n" +
                    ":arrows_counterclockwise: Réécrire le corps du sondage" +
                    ":x: Annuler le sondage" +
                    ":heavy_plus_sign: Gerer les reactions");
            embed.addField("Nom du sondage", name, false);
            embed.addField("Corps", body, false);
            String choices = "";
            if(choicesReactions == null || choicesReactions.size() == 0){
                choices = "Aucun choix présent";
            }else{
                for (String str : choicesReactions){
                    choices = choices + " " + str;
                }
            }
            embed.addField("Choix", choices, false);

            event.getGuild().getTextChannelById(msgChannelID).editMessageById(msgID, embed.build()).queue();

            pollStates = PollStates.CREATING;

        }else if (args[0].equals("removereaction")){
            for (int i = 1; i < args.length; i++){
                if(choicesReactions.contains(args[i])){
                    choicesReactions.remove(args[i]);
                }
            }

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(0xFF0000));
            embed.setTitle(":chart_with_upwards_trend: Sondages");
            embed.setAuthor(event.getAuthor().getName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getAvatarUrl());
            embed.setDescription(":white_check_mark: Valider et poster les sondage \n" +
                    ":arrows_counterclockwise: Réécrire le corps du sondage" +
                    ":x: Annuler le sondage" +
                    ":heavy_plus_sign: Gerer les reactions");
            embed.addField("Nom du sondage", name, false);
            embed.addField("Corps", body, false);
            String choices = "";
            if(choicesReactions == null || choicesReactions.size() == 0){
                choices = "Aucun choix présent";
            }else{
                for (String str : choicesReactions){
                    choices = choices + " " + str;
                }
            }
            embed.addField("Choix", choices, false);

            event.getGuild().getTextChannelById(msgChannelID).editMessageById(msgID, embed.build()).queue();

            pollStates = PollStates.CREATING;
        }
    }

    public void reactionEvent(MessageReactionAddEvent event){
        if(pollStates == PollStates.FREE) return;
        if(!event.getMember().getAsMention().equals(userID)) return;
        event.getReaction().removeReaction(event.getUser()).queue();

        if (event.getReactionEmote().getName().equals("✅")){
            if(choicesReactions.size() == 0){
                event.getChannel().sendMessage(":x: Aucun choix prédéfini !").queue();
                return;
            }
            Consumer<Message> callback = msg -> pollReactions(msg);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(0xFF0000));
            embed.setAuthor(name + " par " + event.getMember().getUser().getName(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl());

            event.getGuild().getTextChannelById(pollChannelID.substring(2, 20)).sendMessage(embed.build()).queue();
            event.getGuild().getTextChannelById(pollChannelID.substring(2, 20)).sendMessage(body).queue(callback);
            event.getChannel().sendMessage(":white_check_mark: Le sondage a été envoyé").queue();
            event.getGuild().getTextChannelById(msgChannelID).deleteMessageById(msgID).queue();
            pollStates = PollStates.FREE;
        }else if (event.getReactionEmote().getName().equals("🔄")){
            pollStates = PollStates.WRITING_BODY;

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(0xFF0000));
            embed.setTitle(":chart_with_upwards_trend: Sondages");
            embed.setAuthor(event.getMember().getUser().getName(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl());
            embed.addField("Ecriture du corp en cours...", "Ecrivez `setbody ` suivi de votre message ou `setname` suivi du nom de votre sondage" ,false);

            event.getGuild().getTextChannelById(msgChannelID).editMessageById(msgID, embed.build()).queue();

        }else if (event.getReactionEmote().getName().equals("❌")){
            event.getGuild().getTextChannelById(msgChannelID).deleteWebhookById(msgID).queue();
            event.getGuild().getTextChannelById(msgChannelID).sendMessage(":x: Sondage annulé").queue();
            pollStates = PollStates.FREE;
        }else if (event.getReactionEmote().getName().equals("➕")){
            pollStates = PollStates.WRITING_REACTION;

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(new Color(0xFF0000));
            embed.setTitle(":chart_with_upwards_trend: Sondages");
            embed.setAuthor(event.getMember().getUser().getName(), event.getMember().getUser().getAvatarUrl(), event.getMember().getUser().getAvatarUrl());
            embed.addField("Choix des reactions en cours...", "Ecrivez `addreaction` ou `removereaction` suivi de votre ou vos emotes pour les ajouter/supprimer a la liste des choix" ,false);

            event.getGuild().getTextChannelById(msgChannelID).editMessageById(msgID, embed.build()).queue();

        }
    }

    private void creationReactions(Message msg){
        msg.addReaction("✅").queue();
        msg.addReaction("🔄").queue();
        msg.addReaction("❌").queue();
        msg.addReaction("➕").queue();
        msgChannelID = msg.getChannel().getId();
        msgID = msg.getId();
    }

    private void resultsCallback(Message msg){
        resultsMsgID = msg.getId();
        getResultsMsgChannelID = msg.getChannel().getId();
    }

    private void pollReactions(Message msg){
        for (String str : choicesReactions){
            msg.addReaction(str).queue();
        }
        PollObject pollObject = new PollObject();
        pollObject.setName(name);
        pollObject.setAuthor(userID);
        pollObject.setChannelID(pollChannelID);
        pollObject.setChoicesReactions(choicesReactions);
        pollObject.setMsgID(msg.getId());
        getJda().getMain().getPollsFile().polls.add(pollObject);
        getJda().getMain().getPollsFile().writeFile();
        body = "null";
        msgID = null;
        msgChannelID = null;
        choicesReactions = new ArrayList<>();
        pollChannelID = null;
        userID = null;

    }
}
