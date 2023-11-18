package com.toeic;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.chart.*;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class draws a pieChart for the errata.
 */
public abstract class ErrataPieChart {

    private String nameOne = "Pass";
    private int valueOne;
    private String nameTwo = "Fail";
    private int valueTwo;
    private String header;
    private HashMap<String, ErrorRecord> record;

    protected GraphicsContext graphicContext;

    private Paint backgroundColor = Color.BLACK;
    private Timeline timeline = new Timeline();
    private int frames = 30;
    private StackPane root;
    private Stage stage;
    private static MediaPlayer mediaPlayer;
    private AtomicBoolean fullMark;

    /**
     * Constructor
     */
    public ErrataPieChart()
    {
        record = new HashMap<>();
    }

    // Getter and Setter
    public HashMap<String, ErrorRecord> getRecord() {
        return record;
    }

    public void setRecord(String itemNumber, Boolean result, String answer, String part, String source) {
        ErrorRecord errorRecord;
        if(record.containsKey(itemNumber)) {
            errorRecord = record.get(itemNumber);
            if(!errorRecord.answer.matches(answer)) {
                setDataSet(result, answer, part, source, errorRecord);
                this.record.replace(itemNumber, errorRecord);
            }
        } else {
            errorRecord = new ErrorRecord();
            setDataSet(result, answer, part, source, errorRecord);
            this.record.put(itemNumber, errorRecord);
        }

    }

    private static void setDataSet(Boolean result, String answer, String part, String source, ErrorRecord errorRecord) {
        errorRecord.setResult(result);
        errorRecord.setAnswer(answer);
        errorRecord.setPart(part);
        errorRecord.setSource(source);
    }

    public void setHeader(String header) {
        this.header = header;
    }

    private void applyCustomColors(ObservableList<PieChart.Data> pieChartData, String... pieColors) {
        int i = 0;
        for (PieChart.Data data : pieChartData) {
            data.getNode().setStyle("-fx-pie-color: " + pieColors[i % pieColors.length] + ";");
            i++;
        }
    }

    private void assignChartData() {

        fullMark = new AtomicBoolean(true);

        AtomicInteger numberOfPass = new AtomicInteger();
        AtomicInteger numberOfFail = new AtomicInteger();
        record.forEach(
                (K,V) ->{
                    if (V.isResult())
                        numberOfPass.set(numberOfPass.get() + 1);
                    else {
                        numberOfFail.set(numberOfFail.get() + 1);
                        fullMark.set(false);
                    }
                });
        valueOne = numberOfPass.get();
        valueTwo = numberOfFail.get();
    }

    public void display() {
        Stage stage = new Stage();
        stage.setTitle(header);
        stage.setWidth(500);
        stage.setHeight(500);
        assignChartData();

        Canvas canvas = new Canvas(500, 500);
        graphicContext = canvas.getGraphicsContext2D();
        canvas.requestFocus();
        graphicContext.fillRect(10, 10, 600, 600);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> stage.close());

        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(drawPieChart(), closeButton);
        layout.setPrefHeight(300);
        layout.setPrefWidth(300);

        StackPane root = new StackPane();
        if (fullMark.get()) {
            root.getChildren().addAll(canvas, layout);

            setup();
            canvas.setWidth(500);
            canvas.setHeight(500);
            startDrawing();
            internalDraw();
        } else {
            root.getChildren().add(layout);
        }
        stage.setScene(new Scene(root));
        stage.show();

    }

    private Chart drawPieChart() {
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data(nameOne+"("+valueOne+")", valueOne),
                        new PieChart.Data(nameTwo+"("+valueTwo+")", valueTwo));
        final PieChart chart = new PieChart(pieChartData);

        if (fullMark.get())
            chart.getStylesheets().add("Viper.css");

        chart.setTitle(header);
        chart.setLegendVisible(false);

        applyCustomColors(
                pieChartData,
                "limegreen",
                "orangered"
        );
        return chart;
    }

    public abstract void setup();

    public abstract void draw();

    public void title(String title) {
        stage.setTitle(title);
    }

    public void background(Paint color) {
        backgroundColor = color;
    }

    public void frames(int frames) {
        this.frames = frames;
        startDrawing();
    }

     private void internalDraw() {
        graphicContext.setFill(backgroundColor);
        graphicContext.setStroke(Color.WHITE);
        graphicContext.fillRect(0, 0, 500, 500);
        draw();
    }

    private void startDrawing() {
        timeline.stop();
        if (frames > 0) {
            timeline.getKeyFrames().clear();
            KeyFrame frame = new KeyFrame(Duration.millis(1000 / frames), e -> internalDraw());
            timeline.getKeyFrames().add(frame);
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }

    public double map(double value, double start1, double stop1, double start2, double stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }

    protected static void startSound(String fileName) {
        // Create & assign the audio file name.
        if (!fileName.isBlank() || !fileName.isEmpty()) {
            URL url = ErrataPieChart.class.getResource(fileName);
            mediaPlayer = new MediaPlayer(new Media(url.toString()));
            mediaPlayer.play();
        }
    }
}