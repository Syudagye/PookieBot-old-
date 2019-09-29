package fr.syudagye.pookie_bot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class LogSystem {

    private static File logFile;

    static void init(){
        logFile = new File(Main.JAR_LOCATION, "logs.log");
    }

    public static void log(String text) {
        if(!logFile.exists()) init();
        Date date = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        String logDate = "[" + dateFormat.format(date) + "] ";
        try {
            BufferedWriter bf = new BufferedWriter(new FileWriter(logFile, true));
            bf.write(logDate + text);
            bf.newLine();
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
