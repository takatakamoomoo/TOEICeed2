package com.toeic;

import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.SplitMenuButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

public class MenuButton {

    private ErrataPieChart errataPieChart;
    private String header;

    public MenuButton(String header) {
        this.header = header;
    }

    public SplitMenuButton createSplitMenuButton(String ImageFileName, ErrataPieChart errataPieChart) {
        this.errataPieChart = errataPieChart;

        File imageFile = new File(ImageFileName);
        Image img = new Image(imageFile.toURI().toString(), 800, 500, false, false);

        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);

        //Creating a splitMenuButton
        SplitMenuButton splitMenuButton = new SplitMenuButton();

        //Setting an image to the button
        splitMenuButton.setGraphic(imageView);

        //Creating splitMenuButton Items
        splitMenuButton.setMnemonicParsing(true);
        MenuItem item1 = new MenuItem("Score PieChart");
        MenuItem item2 = new MenuItem("No Score PieChart");

        //Setting text to the SplitMenuButton
        splitMenuButton.setText(item2.getText());

        //Adding all the splitMenuButton items to the splitMenuButton
        splitMenuButton.getItems().addAll(item1, item2);

        item1.setOnAction((e)-> {
            splitMenuButton.setText(item2.getText());
            errataPieChart.setHeader(header);
            errataPieChart.display();
        });
        return splitMenuButton;
    }
}
