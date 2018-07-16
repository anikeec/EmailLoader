package com.apu.emailloader.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {

    private static final String PROPERTIES_FILE_PATH = "src/main/resources/email.properties";
    public static final String LAST_MESSAGE_ID = "email.lastMessageId";
    public static final String LAST_MESSAGE_DATE_TIME = "email.lastMessageDateTime";
    
    public static Properties loadPropertiesFromFile() throws IOException {
        Properties properties = new Properties();
        FileInputStream in;
        in = new FileInputStream(PROPERTIES_FILE_PATH);
        properties.load(in);
        in.close();    
        return properties;
    }
    
    public static void savePropertiesToFile(Properties properties) throws IOException {
        FileOutputStream propFile = new FileOutputStream(PROPERTIES_FILE_PATH);
        properties.store(propFile, null);
        propFile.close();
    }
    
}
