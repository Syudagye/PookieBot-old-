package fr.syudagye.pookie_bot.xml.poll;

import fr.syudagye.pookie_bot.JDAManager;
import fr.syudagye.pookie_bot.XML_File;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PollsFile extends XML_File {

    public ArrayList<PollObject> polls;

    public PollsFile(JDAManager jda, File file) {
        super(jda, file);
    }

    @Override
    public void createFile() {
        Document doc = new Document();
        Element root = new Element("polls");
        doc.setRootElement(root);

        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        try {
            out.output(doc, new FileOutputStream(getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeFile() {
        Document doc = new Document();
        Element root = new Element("polls");
        doc.setRootElement(root);

        for(PollObject po : polls) {
            Element poll = new Element("poll");

            Element name = new Element("name");
            name.addContent(po.getName());
            poll.addContent(name);
            Element author = new Element("author");
            author.addContent(po.getAuthor());
            poll.addContent(author);
            Element channel = new Element("channel");
            channel.addContent(po.getChannelID());
            poll.addContent(channel);
            Element msgid = new Element("msgid");
            msgid.addContent(po.getMsgID());
            poll.addContent(msgid);
            String choicesReactions = "";
            for (String str : po.getChoicesReactions()){
                choicesReactions += " " + str;
            }
            Element reactions = new Element("reactions");
            reactions.addContent(choicesReactions);
            poll.addContent(reactions);

            root.addContent(poll);

        }

        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        try {
            out.output(doc, new FileOutputStream(getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        polls.clear();
    }

    @Override
    public void readFile() {
        polls = new ArrayList<>();
        try {
            Element root = new SAXBuilder().build(getFile()).getRootElement();

            List<Element> polls = root.getChildren();
            for(Element e : polls) {
                PollObject po = new PollObject();
                po.setName(e.getChildText("name"));
                po.setAuthor(e.getChildText("author"));
                po.setChannelID(e.getChildText("channel"));
                po.setMsgID(e.getChildText("msgid"));
                String[] tmp = e.getChildText("reactions").split(" ");
                ArrayList<String> reactions = new ArrayList<>();
                for (String str : tmp){
                    reactions.add(str);
                }
                po.setChoicesReactions(reactions);
                this.polls.add(po);
            }
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }
}
