package com.toeic;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ToeiceedTitle extends Pane {
    private Text text;

    public ToeiceedTitle(String name) {
        String spread = "";
        for (char c : name.toCharArray()) {
            spread += c + " ";
        }

        text = new Text(spread);

        //Create the font file to load
        File fontFile = new File("resources/Penumbra-HalfSerif-Std_35114.ttf");

        text.setFont(Font.loadFont(fontFile.toURI().toString(), 48));
        text.setFill(Color.WHITE);
        text.setEffect(new DropShadow(30, Color.BLACK));

        getChildren().addAll(text);
    }

    public double getTitleWidth() {
        return text.getLayoutBounds().getWidth();
    }

    public double getTitleHeight() {
        return text.getLayoutBounds().getHeight();
    }
}
