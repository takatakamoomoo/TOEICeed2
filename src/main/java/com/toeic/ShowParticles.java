package com.toeic;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ShowParticles extends Application {

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        final int width = 500;
        final int height = 300;
        Scene scene = new Scene(root, width, height);

        int x, y, r, w, h, t;

        x = 10;
        y = 10;
        r = 5;

        w = 500;
        h = 300;
        t = 60;

        ArrayList<Particles> particlesArrayList;

        particlesArrayList = new ArrayList<>();

        int min = -1000;
        int max = 1500;

        //Generate random int value from -1000 to 1500
        for(int i=0; i < 60; ++i) {
            y = (int)(Math.random()*(max-min+1)+min);
            x = (int)(Math.random()*(max-min+1)+min);
            particlesArrayList.add(new Particles(x, y, r, Color.RED, w, h, t));
        }

        for (int i = 0; i < particlesArrayList.size(); i++) {
            root.getChildren().add(particlesArrayList.get(i));
        }

        stage.setTitle("Rectangle with Shadow");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
