package com.toeic;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;

public class SwitchScene extends Application {

    Stage window;
    Scene scene1;
    Scene scene2;

    VBox layout1;
    VBox layout2;

    double xpos;
    double ypos;

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

        // create a background fill
        BackgroundFill background_fill = new BackgroundFill(Color.BLUE,
                CornerRadii.EMPTY, Insets.EMPTY);

        // create Background
        Background background = new Background(background_fill);

        Text text1 = new Text("Scene 1");
        text1.setFont(new Font("Arial", 20));
        text1.setFill(Color.WHITE);

        Button button1 = new Button("Go to scene2");
        button1.setOnAction(e -> makeFadeOut(layout1, scene2, layout2));

        layout1 = new VBox(20);
        // set background
        layout1.setBackground(background);
        layout1.getChildren().addAll(text1, button1);
        layout1.setAlignment(Pos.BOTTOM_CENTER);
        scene1 = new Scene(layout1, 400, 200);

        xpos = layout1.getLayoutX();
        ypos = layout1.getLayoutY();

        Button button2 = new Button("Go back to scene1");
        button2.setOnAction(e -> makeFadeIn(scene1, layout1));

        // create a background fill
        BackgroundFill background_fill2 = new BackgroundFill(Color.GREEN,
                CornerRadii.EMPTY, Insets.EMPTY);

        // create Background
        Background background2 = new Background(background_fill2);

        Text text = new Text("Scene 2");
        text.setFont(new Font("Arial", 20));
        text.setFill(Color.WHITE);

        layout2 = new VBox();
        layout2.setBackground(background2);
        layout2.setAlignment(Pos.BOTTOM_CENTER);
        layout2.getChildren().addAll(text,button2);
        scene2 = new Scene(layout2, 400, 200);

        AtomicBoolean scene1Lost = new AtomicBoolean(false);
        AtomicBoolean scene2Lost = new AtomicBoolean(true);

        layout1.setOnSwipeRight(e-> {
            System.out.println("e.getX() = " + e.getX());
            layout1.setLayoutX(e.getX() + layout1.getLayoutX());

        });

        layout1.setOnTouchReleased(event ->{
            if(layout1.getLayoutX() > 100) {
                window.setScene(scene2);
                //    layout2.setLayoutX(0);
                scene1Lost.set(true);
                scene2Lost.set(false);
            }
        });

        layout2.setOnSwipeRight(e-> {
            System.out.println("layout2 - e.getX() = " + e.getX());
            layout2.setLayoutX(e.getX() + layout2.getLayoutX());


        });


        layout2.setOnTouchReleased(e-> {
            System.out.println("layout2.getLayoutX() = " + layout2.getLayoutX());
            if(layout2.getLayoutX() > 100) {
                window.setScene(scene1);
              //  layout1.setLayoutX(0);
                scene2Lost.set(true);
                scene1Lost.set(false);
            }
        });



        window.setScene(scene1);
        window.show();
    }

    /**
     * Prior to the FadeTranscation, need to switch the scene.
     * And then we need to set the layout's (i.e. StackPane) opacity to 0
     * in order to start from the invisible state for the target layout.
     */
    private void makeFadeIn(Scene scene, VBox layout) {
        window.setScene(scene);
        layout2.setOpacity(0);
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(5000));
        fadeTransition.setNode(layout);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }


    private void makeFadeOut(VBox layoutOut, Scene sceneIn, VBox layoutIn) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(1000));
        fadeTransition.setNode(layoutOut);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e->makeFadeIn(sceneIn, layoutIn));
        fadeTransition.play();
    }

    public static void main(String[] args) {
        launch();
    }
}