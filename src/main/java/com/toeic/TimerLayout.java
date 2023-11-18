package com.toeic;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class TimerLayout {

    private boolean increment;
    private boolean play;

    private Button addingPlayButton;
    private Button resetButton;
    private Button subtractPlayButton;
    private File file;
    private Duration startTime;
    private Duration time = null;
    private HBox timerLayout;
    private Label timerLabel = new Label();
    private MediaPlayer mediaPlayer;
    private String itemNum;
    private String part;
    private StringProperty timeSeconds;
    private Timeline timeline;
    private Double timelimit;

    private TimerRecord timerRecord;
    private SortedMap<String, TimerRecord> timerRecordSortedMap;

    public TimerLayout(Duration startTime, boolean increment, String part) {
        timerRecordSortedMap = new TreeMap<>();
        this.startTime = startTime;
        this.time = startTime;
        this.play = false;
        this.increment = increment;
        this.timelimit = 60000.0;
        this.part = part;
    }

    public SortedMap<String, TimerRecord> getTimerRecordSortedMap() {
        return timerRecordSortedMap;
    }

    public boolean isPlay() {
        return play;
    }

    public Double getTimelimit() { return timelimit; }

    public void setTimelimit(Double timelimit) { this.timelimit = timelimit; }

    public HBox getTimerLayout() {
        return timerLayout;
    }

    public void setTimerLayout(HBox timerLayout) {
        this.timerLayout = timerLayout;
    }

    public void setItemNum(String itemNum) { this.itemNum = itemNum; }

    public Duration getTime() {
        return time;
    }

    public void createTimerLayout() {
        timeSeconds = new SimpleStringProperty();

        // Bind the timerLabel text property to the timeSeconds property
        timerLabel.textProperty().bind(timeSeconds);

        File  fontFile = new File("resources/CLOSCA.ttf");

        timerLabel.setFont(Font.loadFont(fontFile.toURI().toString(), 28));
        timeSeconds.set(makeText(startTime));
        timerLabel.setTextFill(Color.DARKGRAY);

        addingPlayButton = new Button();
        ImageView imageView;
        imageView = createImageView("resources/PLAY_64.png");
        imageView.setFitWidth(15);
        imageView.setFitHeight(15);

        //Setting a graphic to the button
        addingPlayButton.setGraphic(imageView);

        subtractPlayButton = new Button();
        imageView = createImageView("resources/PLAY_64.png");
        imageView.setFitWidth(15);
        imageView.setFitHeight(15);

        //Setting a graphic to the button
        subtractPlayButton.setGraphic(imageView);

        Button stopButton = new Button();
        imageView = createImageView("resources/STOP_64.png");
        imageView.setFitWidth(15);
        imageView.setFitHeight(15);
        stopButton.setGraphic(imageView);

        resetButton =new Button();
        imageView = createImageView("resources/RESET2_64.png");
        imageView.setFitWidth(15);
        imageView.setFitHeight(15);
        resetButton.setGraphic(imageView);
        resetButton.setDisable(true);

        resetButton.setOnAction( e -> {
            performResetButtonAction();
        });

        stopButton.setOnAction( e -> {
            processStopTimer(true);
        });

        addingPlayButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (timeline == null) {
                    timeline = new Timeline(new KeyFrame(Duration.millis(100),
                        e -> {
                            manageTimeProgress(e);
                        })
                    );
                }
                addingPlayButton.setDisable(true);
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
            }
        });

        subtractPlayButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (timeline == null) {
                    timeline = new Timeline(new KeyFrame(Duration.millis(100),
                        e -> {
                            manageTimeProgress(e);
                        })
                    );
                }
                subtractPlayButton.setDisable(true);
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
            }
        });

        timerLayout = new HBox(3);      // gap between components is 3
        timerLayout.setAlignment(Pos.CENTER);        // center the components within VBox

        if(increment) {
            timerLayout.getChildren().addAll(timerLabel, addingPlayButton, stopButton, resetButton);
        } else {
            timerLayout.getChildren().addAll(timerLabel, subtractPlayButton, stopButton, resetButton);
        }
    }

    private void manageTimeProgress(ActionEvent e) {
        final Duration duration = ((KeyFrame) e.getSource()).getTime();
        if (increment) {
            time = time.add(duration);
            // if it is >= 60 seconds
            if (time.greaterThanOrEqualTo(Duration.millis(timelimit))) {
                timerLabel.setTextFill(Color.RED);
                if (time.compareTo(Duration.millis(timelimit)) == 0)
                    playSound();
            }
        } else {  // subtract second
            time = time.subtract(duration);
            if(time.lessThanOrEqualTo(Duration.millis(0))) {
                timeline.stop();
                timerLabel.setTextFill(Color.RED);
                playSound();
                resetButton.setDisable(false);
            }
        }
        timeSeconds.set(makeText(time));
    }

    private ImageView createImageView(String fileName) {
        File imageFile = new File(fileName);
        Image image = new Image(imageFile.toURI().toString());
        ImageView view = new ImageView(image);
        view.setFitHeight(30);
        view.setPreserveRatio(true);
        return view;
    }

    private TimerRecord getTimerRecord(String itemNum, Duration duration) {
        timerRecord = new TimerRecord();
        timerRecord.setItemNum(itemNum);
        timerRecord.setFinishTime(makeText(duration));
        return timerRecord;
    }

    /*
   Converts the duration to seconds by first converting it to milliseconds and
   then dividing by 1000. This method can then be used to calculate the
   total seconds in the duration
*/    private long getSeconds(Duration duration) {
        return (long) duration.toMillis() / 1000;
    }

    /*
       Convert the Double value to an integer representing the total number of
       seconds and then calculate the minutes from that value.
       Calculate the total seconds of the duration using getSeconds(),
       and then compute the minutes and remaining seconds by dividing and modulo
       by 60, respectively.
       This should correctly handle the case where the duration is 60 minutes
       and display "60:00".
     */
    private String makeText(final Duration duration) {
        String returnTime = null;

        if (timelimit != 0) {
            int minutes = (int) (timelimit / 60000.0);
            long totalSeconds = getSeconds(duration);
            long durationMinutes = totalSeconds / 60;
            long remainingSeconds = totalSeconds % 60;
            returnTime = String.format("%02d:%02d", durationMinutes, remainingSeconds) + "/" + String.format("%02d:00", minutes);
        } else {
            long totalSeconds = getSeconds(duration);
            long durationMinutes = totalSeconds / 60;
            long remainingSeconds = totalSeconds % 60;
            returnTime = String.format("%02d:%02d", durationMinutes, remainingSeconds) + "/" + String.format("%02d:%02d", durationMinutes, remainingSeconds);
        }

        return returnTime;
    }

    public void performResetButtonAction() {
        if(increment)
            addingPlayButton.setDisable(false);
        else
            subtractPlayButton.setDisable(false);

        time = this.startTime;
        timeSeconds.set(makeText(time));
        resetButton.setDisable(true);
        timerLabel.setTextFill(Color.GRAY);
    }

    private void playSound() {
        // Create & assign the audio file name.
        File mediaFile = new File("resources/timeUp.mp3");
        mediaPlayer = new MediaPlayer(new Media(mediaFile.toURI().toString()));
        mediaPlayer.play();
        play = true;
    }

    public void processStopTimer(boolean isUpdateTimerRecord) {
        //For Part3, 4, 6 or 7 only, update the TimerRecord
        if (this.part.matches("3") || this.part.matches("4") ||
                this.part.matches("7") || this.part.matches("6")) {
            if (isUpdateTimerRecord)
                processUpdateTimerRecord();
        }

        if(timeline != null) {
            timeline.stop();
            resetButton.setDisable(false);
            addingPlayButton.setDisable(false);
        }
    }

    public void processUpdateTimerRecord() {
        updateTimerRecord(time);
    }

    public void updateTimeLimit() {
        // Update the displayed time
        timeSeconds.set(makeText(time));
    }

    private void updateTimerRecord(Duration duration) {
        boolean newData = true;
        TimerRecord timerRecord = null;

        if(timerRecordSortedMap.size() != 0) {
            for (Map.Entry item: timerRecordSortedMap.entrySet()) {
                timerRecord = (TimerRecord) item.getValue();
                if(this.itemNum.matches(timerRecord.getItemNum())) {
                    timerRecord.setFinishTime(makeText(duration));
                    newData = false;
                }
            }
            if(newData) {
                timerRecord = getTimerRecord(this.itemNum, duration);
            }
        } else {
            timerRecord = getTimerRecord(this.itemNum, duration);
        }

        if (timerRecordSortedMap.size() == 0 || !timerRecordSortedMap.containsKey(this.itemNum)) {
            timerRecordSortedMap.put(itemNum, timerRecord);
        } else {
            timerRecordSortedMap.replace(itemNum, timerRecord);
        }
    }
}

class TimerRecord {
    String itemNum;
    String finishTime;

    public String getItemNum() { return itemNum; }

    public void setItemNum(String itemNum) { this.itemNum = itemNum; }

    public String getFinishTime() { return finishTime; }

    public void setFinishTime(String finishTime) { this.finishTime = finishTime; }
}