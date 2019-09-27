package fr.syudagye.pookie_bot.xml.mutes;

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

public class Mutes extends XML_File {
	
	public ArrayList<MuteObject> mutes;

	public Mutes(JDAManager jda, File file) {
		super(jda, file);
	}

	@Override
	public void createFile() {
		Document doc = new Document();
		Element root = new Element("mutes");
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
		Element root = new Element("mutes");
		doc.setRootElement(root);
		
		for(MuteObject mo : mutes) {
			Element mute = new Element("mute");
			
			Element name = new Element("name");
			name.addContent(mo.getName());
			mute.addContent(name);
			Element id = new Element("id");
			id.addContent(mo.getId());
			mute.addContent(id);
			Element time = new Element("time");
			time.addContent(mo.getTime());
			mute.addContent(time);
			Element since = new Element("since");
			since.addContent(mo.getSince());
			mute.addContent(since);
			Element reason = new Element("reason");
			reason.addContent(mo.getReason());
			mute.addContent(reason);
			
			root.addContent(mute);
			
		}
		
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		try {
			out.output(doc, new FileOutputStream(getFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		mutes.clear();
	}

	@Override
	public void readFile() {
		mutes = new ArrayList<>();
		try {
			Element root = new SAXBuilder().build(getFile()).getRootElement();
			
			List<Element> mutes = root.getChildren();
			for(Element e : mutes) {
				MuteObject mo = new MuteObject();
				mo.setName( e.getChildText("name"));
				mo.setId(e.getChildText("id"));
				mo.setTime(e.getChildText("time"));
				mo.setSince(e.getChildText("since"));
				mo.setReason(e.getChildText("reason"));
				this.mutes.add(mo);
			}
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}

}
