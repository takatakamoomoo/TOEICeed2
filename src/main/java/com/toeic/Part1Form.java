package com.toeic;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class Part1Form extends PartForm{
    private ComboBox sourceComboBox;

    private ContextMenu popupMenu =null;

    private ImageView bookMark;
    private Image bookMarkImage;
    private ImageView result1;
    private ImageView review1;
    private Label resultDate1;

    private Media media;
    private MediaPlayer mediaPlayer;

    private MenuButton menuButton;

    private ObservableList<String> sourceList = FXCollections.observableArrayList();

    private RadioButton radioButton1A;
    private RadioButton radioButton1B;
    private RadioButton radioButton1C;
    private RadioButton radioButton1D;

    private SortedMap<String, HistoryRecord> historyRecordSortedMap;

    private ImageView imageLayout;

    private ScrollPane scrollPane;
    private SortedMap<String, Integer> sourceMap;
    private SplitMenuButton splitMenuButton;
    private String result;

    /**
     * Constructor
     *  @param stage
     * @param root
     * @param numberOfRow
     * @param parts
     * @param lastRowNum
     * @param resultManagement
     */
    public Part1Form(Stage stage, Pane root, int numberOfRow, List<Part> parts, int lastRowNum,
                     HistoryRecordFile historyRecordFile, ResultManagement resultManagement,
                     ReviewManagement reviewManagement) {
        super(numberOfRow, parts, lastRowNum, stage, root, historyRecordFile, resultManagement, reviewManagement);

        sourceMap = new TreeMap<>();
        for(Part part : parts)
        {
            sourceMap.putIfAbsent(part.getSource(), Integer.valueOf(part.getPartListNumber()));
            reviewManagement.updateResultRecord(part.getItemNum(), part.getItemNum(), part.getPart(), part.getReview(), true);
        }

        popupMenu = new ContextMenu();
    }

    @Override
    protected void assignActionEvent() {
        answer1Button.setOnAction(e -> {
            showExplanation(numberOfRow);
        });

        backButton.setOnAction(e -> {
            goBackToPrevious();
        });

        bookMark.setOnMousePressed(e -> {
            historyRecordFile.updateHistoryRecord(parts.get(numberOfRow).getItemNum(),
                    numberOfRow, parts.get(numberOfRow).getPart());

            if (checkBookMark(numberOfRow))
                startSound("resources/Click.mp3");
        });

        nextButton.setOnAction(e -> {
            goToNextPage();
        });

        homeButton.setOnAction(e -> {
            makeFadeOut();
        });

        script1Button.setOnAction( e-> {
            showScript(numberOfRow, Modality.APPLICATION_MODAL);
        });

        startButton.setOnAction(e -> {
            mediaPlayer = new MediaPlayer(this.getMedia());
            mediaPlayer.play();
            startButton.disableProperty().set(true);

            // Once the player is finished
            mediaPlayer.setOnEndOfMedia( () -> {
                // Enable the startButton for the media-play
                if (startButton.disableProperty().get()) {
                    startButton.disableProperty().set(false);
                }
            });
        });

        sourceComboBox.setOnAction(e -> {
            int itemNumber;
            if(sourceMap.get(sourceComboBox.getValue()) < lastRowNum) {
                itemNumber = sourceMap.get(sourceComboBox.getValue());

                for(int i = 0; i < parts.size(); ++i) {
                    if(parts.get(i).getPartListNumber().equals(String.valueOf(itemNumber))) {
                        numberOfRow = i;
                    }
                }

                Image img = alertBox.getImage(parts.get(this.numberOfRow).getFilePath() +
                        parts.get(this.numberOfRow).getDialog());
                imageLayout.setImage(img);

                setupQuestions(numberOfRow);
                rebuildTextsAndButtons(numberOfRow);
                restoreRadioButtons(numberOfRow);
                checkBookMark(numberOfRow);
            }
        });

        textbookButton.setOnAction(e-> {
            this.showFolderContentsDialog(numberOfRow);
        });

        //ポップアップメニューがリクエストされた場合の処理
        layout.setOnContextMenuRequested((ContextMenuEvent event) -> {
            popupMenu.hide();
            popupMenu = createPopupMenu(layout);
            popupMenu.show(layout, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        //マウスクリックの場合、ポップアップメニューを不可視にする
        layout.setOnMouseClicked((MouseEvent event) -> { popupMenu.hide(); });
    }

    private void buildUpPartListView() {
        for(Map.Entry mapEntry : sourceMap.entrySet()) {
            sourceList.add(mapEntry.getKey().toString());
        }
    }

    private boolean checkBookMark(int numberOfRow) {
        boolean marked = false;
        if (!historyRecordFile.isBookMarkRecord(numberOfRow, parts.get(numberOfRow).getPart())) {
            bookMark.setOpacity(0.0);
        } else {
            bookMark.setOpacity(1.0);
            marked = true;
        }
        return marked;
    }

    ContextMenu createPopupMenu(VBox layout) {
        ContextMenu menu = new  ContextMenu();
        MenuItem[] menui =null;

        menui = new MenuItem[2];
        menui[0] = new MenuItem("Mark");
        menui[1] = new MenuItem("Unmark");
        menu.getItems().addAll(menui);

        for(int i=0;i<menui.length;i++){
            menui[i].addEventHandler( ActionEvent.ACTION ,
                    e -> {
                        String selection =((MenuItem) e.getSource()).getText();
                        updateViewImages(selection);
                    });
        }
        return menu;
    }

    @Override
    protected void createGroupControls() {
        radioButton1A.setToggleGroup(group1);
        radioButton1B.setToggleGroup(group1);
        radioButton1C.setToggleGroup(group1);
        radioButton1D.setToggleGroup(group1);

        group1.selectedToggleProperty().addListener((ObservableValue<? extends Toggle>
                                                             observ, Toggle oldVal, Toggle newVal)->{
            // Cast object to radio button
            if(newVal != null) {
                RadioButton chk = (RadioButton) newVal.getToggleGroup().getSelectedToggle();
                if (chk.getText().contains(this.answer1)) {
                    errataPieChart.setRecord(parts.get(numberOfRow).getItemNum(), true, chk.getText(),
                            parts.get(numberOfRow).getPart(), parts.get(numberOfRow).getSource());
                    chk.setStyle("-fx-background-color:GREEN; -fx-background-radius: 15px; -fx-background-width: 2px;");
                    result = PASS;
                } else {
                    errataPieChart.setRecord(parts.get(numberOfRow).getItemNum(), false, chk.getText(),
                            parts.get(numberOfRow).getPart(), parts.get(numberOfRow).getSource());
                    chk.setStyle("-fx-background-color:RED; -fx-background-radius: 15px; -fx-background-width: 2px;");
                    result = FAIL;
                }
                resultManagement.updateResultRecord(parts.get(numberOfRow).getItemNum(),
                        numberOfRow, parts.get(numberOfRow).getPart(), result, chk.getText());
            }

            // Reset the background to transparent
            if( oldVal != null) { // Avoid to access to the null object
                // Cast object to radio button
                RadioButton oldChk = (RadioButton)oldVal;
                oldChk.setStyle("-fx-background-color:null; -fx-background-radius: 15px; -fx-background-width: 2px;");
           }
        });

    }

    @Override
    protected void createRadioButtons() {
        group1 = new ToggleGroup();
        radioButton1A = new RadioButton("A");
        radioButton1B = new RadioButton("B");
        radioButton1C = new RadioButton("C");
        radioButton1D = new RadioButton("D");
    }

    @Override
    protected void createToggleButtons() {
        answer1Button = new ToggleButton();
        script1Button = new ToggleButton();
        textbookButton = new ToggleButton();

        backButton = new ToggleButton();
        startButton = new ToggleButton();
        nextButton = new ToggleButton();
        homeButton = new ToggleButton();

        //Toggle button group
        ToggleGroup toggleGroup = new ToggleGroup();
        backButton.setToggleGroup(toggleGroup);
        startButton.setToggleGroup(toggleGroup);
        homeButton.setToggleGroup(toggleGroup);
        nextButton.setToggleGroup(toggleGroup);
    }

    @Override
    protected void createImageViews() {
        ImageView view;
        view = createImageView("resources/BACK_64.png");

        //Setting the size of the button
        backButton.setPrefSize(70, 30);
        //Setting a graphic to the button
        backButton.setGraphic(view);

        view = createImageView("resources/MUSIC_64.png");

        //Setting the size of the button
        startButton.setPrefSize(70, 30);
        //Setting a graphic to the button
        startButton.setGraphic(view);

        view = createImageView("resources/NEXT_64.png");

        //Setting the size of the button
        nextButton.setPrefSize(70, 30);
        //Setting a graphic to the button
        nextButton.setGraphic(view);

        view = createImageView("resources/HOME_64.png");

        //Setting the size of the button
        homeButton.setPrefSize(70, 30);
        //Setting a graphic to the button
        homeButton.setGraphic(view);

        ImageView view1;
        view1 = createImageView("resources/LIGHTBULBO_64.png");
        view1.setFitWidth(25);
        view1.setFitHeight(25);

        //Setting a graphic to the button
        answer1Button.setGraphic(view1);

        ImageView view2;
        view2 = createImageView("resources/NOTE_64.png");
        view2.setFitWidth(25);
        view2.setFitHeight(25);

        //Setting a graphic to the button
        script1Button.setGraphic(view2);

        ImageView view3;
        view3 = createImageView("resources/RedBook_96.png");
        view3.setFitWidth(25);
        view3.setFitHeight(25);
        //Setting a graphic to the button
        textbookButton.setGraphic(view3);

        review1 = new ImageView();
        review1.setFitHeight(35);
        review1.setFitWidth(35);
        Image image = createImage("resources/PIN2_512.png");
        review1.setImage(image);
        review1.setOpacity(0.0);
        review1.setStyle("-fx-background-color:null;");
    }

    @Override
    protected VBox createLayout() {

        buildUpPartListView();
        sourceComboBox = new ComboBox(sourceList);

        boolean isReady = false;
        historyRecordSortedMap = historyRecordFile.getHistoryRecordMap();
        for (Map.Entry rec: historyRecordSortedMap.entrySet() ) {
            HistoryRecord historyRecord = (HistoryRecord)rec.getValue();
            if(historyRecord.getBookMarked().matches("Y") &&
               historyRecord.getPart().matches(parts.get(0).getPart())) {
                String itemNumber = historyRecord.getItemNumber();
                for(int i = 0; i < parts.size(); ++i) {
                    if(parts.get(i).getItemNum().equals(itemNumber)) {
                        this.numberOfRow = i;
                    }
                }
                sourceComboBox.setValue(parts.get(numberOfRow).getSource());
                isReady = true;
            }
        }

        // Set to the 1st parts-record's source id
        if(!isReady)
            sourceComboBox.setValue(parts.get(0).getSource());

        result1 = new ImageView();
        result1.setFitHeight(20);
        result1.setFitWidth(20);
        final Image image1 = createImage("resources/CROSS_512.png");
        result1.setImage(image1);
        result1.setOpacity(0);
        resultDate1 = new Label();

        setupQuestions(this.numberOfRow);

        sourceComboBox.setMaxHeight(30);
        sourceComboBox.setPrefHeight(30);
        sourceComboBox.setPrefWidth(480);
        menuButton = new MenuButton(MENU_HEADER);
        splitMenuButton = menuButton.createSplitMenuButton("resources/PIECHART_32.png",
                errataPieChart);

        bookMark = new ImageView();
        bookMark.setFitHeight(30);
        bookMark.setFitWidth(30);
        bookMarkImage = createImage("resources/BOOKMARK3_512.png");
        bookMark.setImage(bookMarkImage);

        HBox hBox0 = new HBox();
        hBox0.getChildren().addAll(bookMark, sourceComboBox, splitMenuButton);
        hBox0.setAlignment(Pos.TOP_RIGHT);

        HBox hBox1 = new HBox();
        imageLayout = alertBox.handleImageView(parts.get(this.numberOfRow).getFilePath() +
                parts.get(this.numberOfRow).getDialog(), true);

        imageLayout.setX(10);
        imageLayout.setY(10);
        imageLayout.setFitHeight(760);
        imageLayout.setFitWidth(780);
        imageLayout.setPreserveRatio(true);

        scrollPane = new ScrollPane();
        scrollPane.setPannable(true);
        scrollPane.setPrefSize(800, 760);
        scrollPane.setContent(imageLayout);

        hBox1.getChildren().addAll(imageLayout, scrollPane);
        hBox1.setAlignment(Pos.TOP_LEFT);

        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(radioButton1A, radioButton1B, radioButton1C, radioButton1D ,answer1Button, script1Button,
                                   textbookButton, result1, resultDate1, review1);
        hBox2.setSpacing(20);
        hBox2.setAlignment(Pos.TOP_LEFT);
        hBox2.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox3 = new HBox();
        hBox3.getChildren().addAll(backButton, startButton, nextButton, homeButton);
        hBox3.setSpacing(5);
        hBox3.setAlignment(Pos.TOP_LEFT);
        hBox3.setPadding(new Insets(10, 10, 10, 10));

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().addAll(hBox0, hBox1, hBox2, hBox3);
        return vBox;
    }

    @Override
    protected void changeImageOrTextField(int row) {

    }

    void goToNextPage() {
        if(numberOfRow < lastRowNum -1) {
            // Increase rowNum to move to the next question set
            numberOfRow = numberOfRow + 1;
            setupQuestions(numberOfRow);
            renewSourceCombo();

            Image img = alertBox.getImage(parts.get(this.numberOfRow).getFilePath() +
                    parts.get(this.numberOfRow).getDialog());
            imageLayout.setImage(img);

            rebuildTextsAndButtons(numberOfRow);
            restoreRadioButtons(numberOfRow);
            checkBookMark(numberOfRow);
            startSound("resources/bookPageFlip.mp3");
        }
    }

    void goBackToPrevious() {
        // if not 1st Row item
        if(numberOfRow > 0) {
            // Decrease rowNum to move to the next question set
            if (numberOfRow - 1 > 0)
                numberOfRow = numberOfRow - 1;
            else
                numberOfRow = 0;

            setupQuestions(numberOfRow);
            renewSourceCombo();

            Image img = alertBox.getImage(parts.get(this.numberOfRow).getFilePath() +
                    parts.get(this.numberOfRow).getDialog());
            imageLayout.setImage(img);

            rebuildTextsAndButtons(numberOfRow);
            restoreRadioButtons(numberOfRow);
            checkBookMark(numberOfRow);
            startSound("resources/bookPageFlip.mp3");
        }
    }

    protected Scene rebuildForm(){
        createToggleButtons();
        createRadioButtons();
        createImageViews();
        createGroupControls();
        layout = createLayout();
        assignActionEvent();

        scene = new Scene(layout, FORM_WIDTH, FORM_HEIGHT);
        scene.getStylesheets().add("Viper.css");

        // Assign the Swipe Left action
        scene.setOnSwipeLeft(e-> {
            goBackToPrevious();
        });

        // Assign the Swipe Right action
        scene.setOnSwipeRight(e-> {
            goToNextPage();
        });

        return scene;
    }

    @Override
    void rebuildTextsAndButtons(int row) {
        // Reset the background fill color on radio-buttons
        resetRadiobuttonBackground(radioButton1A);
        resetRadiobuttonBackground(radioButton1B);
        resetRadiobuttonBackground(radioButton1C);
        resetRadiobuttonBackground(radioButton1D);

        setupScriptButton(row);
    }

    private void renewSourceCombo() {
        String source = sourceComboBox.getValue().toString();
        String record = parts.get(numberOfRow).getSource();

        if (!source.matches(record))
            sourceComboBox.setValue(record);
    }

    private void restoreRadioButtons(int firstRow) {
        HashMap<String, ErrorRecord> record;

        record = errataPieChart.getRecord();

        //If there is any error record on HashMap
        if (!record.isEmpty()) {
            boolean result1 = false;
            String answer1 = null;


            if (record.containsKey(parts.get(firstRow).getItemNum())) {
                answer1 = record.get(parts.get(firstRow).getItemNum()).getAnswer();
                result1 = record.get(parts.get(firstRow).getItemNum()).isResult();
            }

            if(answer1 != null) {
                if (answer1.matches("A")) {
                    rebuildRadiobuttonBackground(radioButton1A, result1);
                } else if (answer1.matches("B")) {
                    rebuildRadiobuttonBackground(radioButton1B, result1);
                } else if (answer1.matches("C")) {
                    rebuildRadiobuttonBackground(radioButton1C, result1);
                } else if (answer1.matches("D")) {
                    rebuildRadiobuttonBackground(radioButton1D, result1);
                }
            }
        }

    }

    private void setResultImage(int row) {
        String result = parts.get(row).getResult();
        String resultDate = parts.get(row).getResultDate();

        if(result.matches("fail") || result.matches("pass")) {
            result1.setOpacity(1.0);
            result1.setImage(validationImage(result));
            resultDate1.setText(resultDate);
        } else {
            result1.setOpacity(0);
            resultDate1.setText("");
        }
    }

    private void setReviewImage(int row) {
        String review = reviewManagement.returnReviewRecord(parts.get(row).getItemNum());
        if (review.matches("Mark")) {
            review1.setOpacity(1.0);
        } else {
            review1.setOpacity(0);
        }
    }

    @Override
    void setupQuestions(int row) {
        this.setAnswer1(parts.get(row).getAnswer());

        // Create & assign the audio file name.
        String fileFullPath = parts.get(row).getFilePath()+parts.get(row).getAudioFile();
        if(!fileFullPath.isEmpty() || !fileFullPath.isBlank()) {
            File f = new File(fileFullPath);
            try {
                this.setMediaPlayer(new Media(f.toURI().toString()));
            } catch (MediaException e) {
                e.printStackTrace();
            }
        }

        setResultImage(row);
        setReviewImage(row);
    }

    private void updateViewImages(String selection) {
        String itemNumber = null;
        String groupItemNumber;

        groupItemNumber= parts.get(numberOfRow).getItemNum();

        if (selection.matches("Mark")) {
            review1.setOpacity(1.0);
        } else {
            review1.setOpacity(0.0);
        }

        itemNumber = parts.get(numberOfRow).getItemNum();
        reviewManagement.updateResultRecord(itemNumber, groupItemNumber, parts.get(numberOfRow).getPart(), selection.toString(), false);
    }
}
