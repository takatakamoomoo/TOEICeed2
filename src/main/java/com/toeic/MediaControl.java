package com.toeic;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class MediaControl {

    private MediaPlayer mediaPlayer;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;
    private HBox mediaBar;
    private ToggleButton playButton;
    private ToggleButton pauseButton;
    private ImageView view;
    private ImageView view2;

    public MediaPlayer getMeiaPlayer() {
        return mediaPlayer;
    }

    public void DisablePlayButton() {
        playButton.setDisable(false);
    }

    public HBox createMediaControl(ToggleButton backButton, ToggleButton nextButton, ToggleButton homeButton,
                                   MediaPlayer mp) {
        mediaPlayer = mp;
        //setStyle("-fx-background-color: #bfc2c7;");

        mediaBar = new HBox();
        mediaBar.setAlignment(Pos.CENTER);
        mediaBar.setPadding(new Insets(5, 10, 5, 10));
        BorderPane.setAlignment(mediaBar, Pos.CENTER);

        playButton = new ToggleButton();
        view = createImageView("resources/PLAY4.png");

        //Setting the size of the button
        playButton.setPrefSize(70, 30);
        //Setting a graphic to the button
        playButton.setGraphic(view);

        pauseButton = new ToggleButton();
        view2 = createImageView("resources/PAUSE4.png");

        //Setting the size of the button
        pauseButton.setPrefSize(70, 30);
        //Setting a graphic to the button
        pauseButton.setGraphic(view2);
        // Disable pauseButton
        pauseButton.setDisable(true);

        mediaBar.getChildren().addAll(backButton, nextButton, homeButton, playButton, pauseButton);
        // Add spacer
        Label spacer = new Label("   ");
        mediaBar.getChildren().add(spacer);

        // Add Time label
        Label timeLabel = new Label("Time: ");
        mediaBar.getChildren().add(timeLabel);

        // Add time slider
        timeSlider = new Slider();
        timeSlider.setMinWidth(50);
        timeSlider.setStyle("-fx-control-inner-background: mediumpurple;");
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
            }
        });
        mediaBar.getChildren().add(timeSlider);

        // Add Play label
        playTime = new Label();
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        mediaBar.getChildren().add(playTime);

        // Add the volume label
        Label volumeLabel = new Label("Vol: ");
        mediaBar.getChildren().add(volumeLabel);

        // Add Volume slider
        volumeSlider = new Slider();
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging()) {
                    mp.setVolume(volumeSlider.getValue() / 100.0);
                }
            }
        });

        createEventActions();

        mediaBar.getChildren().add(volumeSlider);
        mediaBar.setSpacing(5);
        mediaBar.setAlignment(Pos.TOP_LEFT);
        mediaBar.setPadding(new Insets(10, 10, 10, 10));

        return mediaBar;
    }

    private void createEventActions() {

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Status status = mediaPlayer.getStatus();

                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    // don't do anything in these states
                    System.out.println("status = " + status);
                    return;
                }

                if (status == Status.PAUSED
                        || status == Status.READY
                        || status == Status.STOPPED
                        || status == Status.PLAYING) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mediaPlayer.seek(mediaPlayer.getStartTime());
                        atEndOfMedia = false;
                    }
                    pauseButton.setDisable(false);
                    playButton.setDisable(true);
                    playButton.setGraphic(createImageView("resources/STOP4.png"));
                    mediaPlayer.play();
                }
            }
        });

        pauseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Status status = mediaPlayer.getStatus();

                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    // don't do anything in these states
                    System.out.println("status = " + status);
                    return;
                }

                if (status == Status.READY
                        || status == Status.STOPPED
                        || status == Status.PLAYING
                        || status == Status.PAUSED) {

                    pauseButton.setDisable(true);
                    playButton.setDisable(false);
                    playButton.setGraphic(createImageView("resources/PLAY4.png"));
                    mediaPlayer.pause();
                }
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                duration = mediaPlayer.getMedia().getDuration();
                updateValues();
            }
        });

        mediaPlayer.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    mediaPlayer.pause();
                    stopRequested = false;
                } else {
                    playButton.setGraphic(createImageView("resources/STOP4.png"));
                }
            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            public void run() {
                playButton.setGraphic(view);
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            public void run() {
                duration = mediaPlayer.getMedia().getDuration();
                updateValues();
            }
        });

        mediaPlayer.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);

        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                if (!repeat) {
                    playButton.setDisable(false);
                    playButton.setGraphic(view);
                    pauseButton.setDisable(true);
                    stopRequested = true;
                    atEndOfMedia = true;
                    timeSlider.setDisable(true);
                }
            }
        });
    }

    protected ImageView createImageView(String fileName) {
        File imageFile = new File(fileName);
        Image image = new Image(imageFile.toURI().toString());
        ImageView view = new ImageView(image);
        view.setFitHeight(30);
        view.setPreserveRatio(true);
        return view;
    }

    protected void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration).toMillis()
                                * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int) Math.round(mediaPlayer.getVolume()
                                * 100));
                    }
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }

}