package com.toeic;

import java.io.*;
import java.util.Properties;


/**
 *  As you know, Java Properties API doesn’t support update properties file dynamically.
 *  While properties files are a popular mean of configuring applications.
 *  Fortunately, Commons Configuration API supports this format and enhances significantly
 *  the basic Java Properties class. Here we will use Commons Configuration API to update
 *  properties file dynamically(src/main/resources/config.properties).
 */
public class PropertiesFile {

    private String fileName;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Properties readPropertiesFile() throws IOException {
        FileInputStream fileInputStream = null;
        Properties properties = null;
        try {
            fileInputStream = new FileInputStream(this.fileName);
            // create Properties class object
            properties = new java.util.Properties();
            // load properties file into it
            properties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileInputStream.close();
        }
        return properties;
    }

    public Properties getPropValues() throws IOException {
        InputStream inputStream = null;
        Properties properties = null;

        try {
            properties = new Properties();
            inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
            properties.load(inputStream);
        } catch (FileNotFoundException ex) {
            System.err.println("Property file '" + fileName + "' not found in the classpath");
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return properties;
    }

    public void writePropertiesFile(String name, String value) {
        try (OutputStream fileOutputStream = new FileOutputStream(this.fileName)) {
            Properties prop = new Properties();
            // set the properties value
            prop.setProperty(name, value);
            // save properties to project root folder.
            prop.store(fileOutputStream, null);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
