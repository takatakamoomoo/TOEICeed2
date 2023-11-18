package com.toeic;


import javafx.scene.image.Image;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationProperties {

    private String fileName;

    public ConfigurationProperties(String fileName) {
        this.fileName = fileName;
    }

    public Properties configureProperties() {

        Properties prop = new Properties();
        try {
            FileReader reader = new FileReader(fileName);
            prop.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

}