package fr.syudagye.pookie_bot.xml.reports;

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

public class ReportsFile extends XML_File {
	
	public ArrayList<ReportObject> reports;

	public ReportsFile(JDAManager jda, File file) {
		super(jda, file);
	}

	@Override
	public void createFile() {
		Document doc = new Document();
		Element root = new Element("reports");
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
		Element root = new Element("reports");
		doc.setRootElement(root);
		
		for(ReportObject ro : reports) {
			Element report = new Element("report");
			
			Element name = new Element("name");
			name.addContent(ro.getName());
			report.addContent(name);
			Element id = new Element("id");
			id.addContent(ro.getId());
			report.addContent(id);
			Element authorId = new Element("authorid");
			authorId.addContent(ro.getAuthorId());
			report.addContent(authorId);
			Element reason = new Element("reason");
			reason.addContent(ro.getReason());
			report.addContent(reason);
			
			root.addContent(report);
		}
		
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		try {
			out.output(doc, new FileOutputStream(getFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		reports.clear();
	}

	@Override
	public void readFile() {
		reports = new ArrayList<>();
		try {
			Element root = new SAXBuilder().build(getFile()).getRootElement();
			
			List<Element> raw = root.getChildren();
			for(Element e : raw) {
				ReportObject report = new ReportObject();
				report.setName(e.getChildText("name"));
				report.setId(e.getChildText("id"));
				report.setAuthorId(e.getChildText("authorid"));
				report.setReason(e.getChildText("reason"));
				reports.add(report);
			}
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}

}
