package fr.syudagye.pookie_bot.xml;

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
import java.util.List;
import java.util.function.BiConsumer;

public class ConfigFile extends XML_File {
	
	public ConfigFile(JDAManager jda, File file) {
		super(jda, file);
	}
	
	@Override
	public void createFile() {
		Document doc = new Document();
		Element root = new Element("root");
		doc.setRootElement(root);
		
		Element token = new Element("token");
		Element prefix = new Element("prefix");
		Element rulesmsg = new Element("rulesmsg");
		Element channels = new Element("channels");
		Element roles = new Element("roles");
		
		root.addContent(token);
		root.addContent(prefix);
		root.addContent(rulesmsg);
		root.addContent(channels);
		root.addContent(roles);
		
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
		Element root = new Element("root");
		doc.setRootElement(root);
		
		Element token = new Element("token");
		token.addContent(getJda().getMain().getToken());
		root.addContent(token);
		Element prefix = new Element("prefix");
		prefix.addContent(getJda().getMain().getPrefix());
		root.addContent(prefix);
		Element rulesmsg = new Element("rulesmsg");
		rulesmsg.addContent(getJda().getMain().getRulesMessageID());
		root.addContent(rulesmsg);
		
		Element channels = new Element("channels");
		getJda().getMain().channels.forEach(new BiConsumer<String, String>() {

			@Override
			public void accept(String name, String id) {
				Element channel = new Element("channel");
				channel.setAttribute("name", name);
				channel.addContent(new Element("id").addContent(id));
				channels.addContent(channel);
			}
		});
		root.addContent(channels);
		
		Element roles = new Element("roles");
		getJda().getMain().roles.forEach(new BiConsumer<String, String>() {

			@Override
			public void accept(String name, String id) {
				Element role = new Element("role");
				role.setAttribute("name", name);
				role.addContent(new Element("id").addContent(id));
				roles.addContent(role);
			}
		});
		root.addContent(roles);
		
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		try {
			out.output(doc, new FileOutputStream(getFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void readFile() {		
		try {
			Element root = new SAXBuilder().build(getFile()).getRootElement();
			
			getJda().getMain().setToken(root.getChildText("token"));
			String prefixtmp = "";
			prefixtmp = root.getChildText("prefix");
			if(!prefixtmp.isEmpty()) getJda().getMain().setPrefix(prefixtmp);
			getJda().getMain().setRulesMessageID(root.getChildText("rulesmsg"));
			
			List<Element> channels = root.getChild("channels").getChildren();			
			for(Element channel : channels) {
				getJda().getMain().channels.put(channel.getAttributeValue("name"), channel.getChildText("id"));
			}
			
			List<Element> roles = root.getChild("roles").getChildren();
			for(Element role : roles) {
				getJda().getMain().roles.put(role.getAttributeValue("name"), role.getChildText("id"));
			}
			
			
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
		
	}

}
