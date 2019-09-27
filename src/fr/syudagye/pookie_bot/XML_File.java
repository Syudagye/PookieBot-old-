package fr.syudagye.pookie_bot;

import java.io.File;

public abstract class XML_File {
	
	private File file;
	private JDAManager jda;
	
	public XML_File(JDAManager jda, File file) {
		this.jda = jda;
		this.file = file;
		if(!file.exists()) {
			createFile();
		}else {
			readFile();
		}
	}
	
	public abstract void createFile();
	public abstract void writeFile();
	public abstract void readFile();	

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public JDAManager getJda() {
		return jda;
	}

	public void setJda(JDAManager jda) {
		this.jda = jda;
	}
	
}
