package com.toeic;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.util.Optional;

class CustomDialog extends Stage {

    public static final ButtonType YES_BUTTON = new ButtonType("Yes");
    public static final ButtonType NO_BUTTON = new ButtonType("No");
    private boolean userChoice;
    private ScaleTransition scale1 = new ScaleTransition();
    private ScaleTransition scale2 = new ScaleTransition();
    private SequentialTransition anim = new SequentialTransition(scale1, scale2);
    private static final Interpolator EXP_IN = new Interpolator() {
        @Override
        protected double curve(double t) {
            return (t == 1.0) ? 1.0 : 1 - Math.pow(2.0, -10 * t);
        }
    };

    private static final Interpolator EXP_OUT = new Interpolator() {
        @Override
        protected double curve(double t) {
            return (t == 0.0) ? 0.0 : Math.pow(2.0, 10 * (t - 1));
        }
    };

    public CustomDialog(String header, String content, String imageFilePath) {
        Pane root = new Pane();

        scale1.setFromX(0.01);
        scale1.setFromY(0.01);
        scale1.setToY(1.0);
        scale1.setDuration(Duration.seconds(0.33));
        scale1.setInterpolator(EXP_IN);
        scale1.setNode(root);

        scale2.setFromX(0.01);
        scale2.setToX(1.0);
        scale2.setDuration(Duration.seconds(0.33));
        scale2.setInterpolator(EXP_OUT);
        scale2.setNode(root);

        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.APPLICATION_MODAL);

        Rectangle bg = new Rectangle(350, 150, Color.WHITESMOKE);
        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(1.5);

        File imageFile = new File(imageFilePath);
        Image img = new Image(imageFile.toURI().toString());

        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        Text headerText = new Text(header);
        headerText.setFont(Font.font(20));

        Text contentText = new Text(content);
        contentText.setFont(Font.font(16));

        VBox box = new VBox(10,
                imageView,
                headerText,
                new Separator(Orientation.HORIZONTAL),
                contentText
        );
        box.setPadding(new Insets(15));

        Button btn = new Button("No");
        btn.setTranslateX(bg.getWidth() - 100);
        btn.setTranslateY(bg.getHeight() - 50);
        btn.setOnAction(e -> closeDialog(false));

        Button btn2 = new Button("Yes");
        btn2.setTranslateX(bg.getWidth() - 60);
        btn2.setTranslateY(bg.getHeight() - 50);
        btn2.setOnAction(e -> closeDialog(true));

        root.getChildren().addAll(bg, box, btn, btn2);

        setScene(new Scene(root, null));
    }

    public Optional<ButtonType> showAndAnimate() {
        anim.play();
        showAndWait();
        return userChoice ? Optional.of(YES_BUTTON) : Optional.empty();
    }

    private void closeDialog(boolean closing) {
        userChoice = closing;
        close();
    }
}