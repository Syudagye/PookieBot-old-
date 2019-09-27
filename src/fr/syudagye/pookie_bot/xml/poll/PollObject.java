package fr.syudagye.pookie_bot.xml.poll;

import java.util.ArrayList;

public class PollObject {
    private String name;
    private String author;
    private String msgID;
    private String channelID;
    private ArrayList<String> choicesReactions;

    public PollObject(String name, String author, String msgID, String channelID, ArrayList<String> choicesReactions){
        this.name = name;
        this.author = author;
        this.msgID = msgID;
        this.channelID = channelID;
        this.choicesReactions = choicesReactions;
    }

    public PollObject(){}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public ArrayList<String> getChoicesReactions() {
        return choicesReactions;
    }

    public void setChoicesReactions(ArrayList<String> choicesReactions) {
        this.choicesReactions = choicesReactions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
