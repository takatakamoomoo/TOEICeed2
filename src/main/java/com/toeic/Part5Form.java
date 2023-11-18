package com.toeic;

import com.jfoenix.controls.JFXToggleButton;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class Part5Form extends PartForm{

    public static final String MENU_HEADER = "TOEIC PART5";
    private static final int PART5_HEIGHT = 820;
    private static final int PART5_WIDTH = 1250;
    private static final int NUM_OF_PART5_QUESTIONS = 3;

    protected List<Part> allPart5;
    protected List<Part> markPart5;
    JFXToggleButton markedToggleButton = new JFXToggleButton();

    private boolean isInitial = false;
    private boolean isTail = false;
    private boolean skipRenewQuestions = false;
    private ComboBox categoryComboBox;
    private ComboBox sourceComboBox;
    private ContextMenu popupMenu =null;
    private ImageView bookMark;
    private ImageView review1;
    private ImageView review2;
    private ImageView review3;
    private ImageView result1;
    private ImageView result2;
    private ImageView result3;
    private Label resultDate1;
    private Label resultDate2;
    private Label resultDate3;
    private HashMap<String, Integer> categoryHashMap;
    private SortedMap<String, Integer> markedSourceHeadMap;
    private SortedMap<String, Integer> markedSourceTailMap;
    private List<Part> categories;
    private List<Part> sources;
    private LinkedList<String> part5List;
    private ObservableList<String> categoryList = FXCollections.observableArrayList();
    private ObservableList<String> sourceList = FXCollections.observableArrayList();
    private boolean mediaFile = false;
    private boolean media2File = false;
    private boolean media3File = false;
    private MediaPlayer mediaPlayer;
    private MenuButton menuButton;

    private RadioButton radioButton1A;
    private RadioButton radioButton1B;
    private RadioButton radioButton1C;
    private RadioButton radioButton1D;

    private RadioButton radioButton2A;
    private RadioButton radioButton2B;
    private RadioButton radioButton2C;
    private RadioButton radioButton2D;

    private RadioButton radioButton3A;
    private RadioButton radioButton3B;
    private RadioButton radioButton3C;
    private RadioButton radioButton3D;

    private SplitMenuButton splitMenuButton;
    private String currentSource;

    private TimerLayout timerLayout;

    /**
     * Constructor
     * @param numberOfRow
     * @param part5s
     * @param categoryList
     * @param lastRowNum
     * @param resultManagement
     */
    public Part5Form(Stage stage, Pane root, int numberOfRow, List<Part> part5s, List<Part> categoryList, int lastRowNum,
                     HistoryRecordFile historyRecordFile, ResultManagement resultManagement,
                     ReviewManagement reviewManagement, List<Part> markPart5, int lastMarkRow) {
        super(numberOfRow, part5s, lastRowNum, stage, root, historyRecordFile, resultManagement, reviewManagement);

        sourceHeadMap = new TreeMap<>();
        sourceTailMap = new TreeMap<>();

        part5List = new LinkedList<>();

        for(Part part : parts)
        {
            sourceHeadMap.putIfAbsent(part.getSource(), Integer.valueOf(part.getPartListNumber()));
            sourceTailMap.put(part.getSource(), Integer.valueOf(part.getPartListNumber()));
            part5List.add(part.getSource());

            reviewManagement.updateResultRecord(part.getItemNum(), part.getItemNum(), part.getPart(), part.getReview(), true);
        }

        markedSourceHeadMap = new TreeMap<>();
        markedSourceTailMap = new TreeMap<>();
        for(Part part : markPart5)
        {
            markedSourceHeadMap.putIfAbsent(part.getSource(), Integer.valueOf(part.getPartListNumber()));
            markedSourceTailMap.put(part.getSource(), Integer.valueOf(part.getPartListNumber()));
        }

        this.allPart5 = part5s;
        this.markPart5 = markPart5;

        popupMenu = new ContextMenu();
        sources = part5s;

        // Create a category list for Part5
        categoryHashMap = new HashMap<>();
        for(Part part : categoryList)
        {
            categoryHashMap.putIfAbsent(part.getCategory(), Integer.valueOf(part.getItemNum()));
        }
       // Assign the List sorted by category
        categories = categoryList;

        questionImgView1 = new ImageView();
        questionImgView1.setPreserveRatio(true);
//        questionImgView1.setFitHeight(150);
        questionImgView1.setFitWidth(400);
        questionImgView2 = new ImageView();
        questionImgView2.setPreserveRatio(true);
//        questionImgView2.setFitHeight(150);
        questionImgView2.setFitWidth(400);
        questionImgView3 = new ImageView();
        questionImgView3.setPreserveRatio(true);
//        questionImgView3.setFitHeight(150);
        questionImgView3.setFitWidth(400);
        question1 = new Label();
        question2 = new Label();
        question3 = new Label();

        isInitial = true;
    }

    public boolean isMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(boolean mediaFile) {
        this.mediaFile = mediaFile;
    }

    public boolean isMedia2File() {
        return media2File;
    }

    public void setMedia2File(boolean media2File) {
        this.media2File = media2File;
    }

    public boolean isMedia3File() {
        return media3File;
    }

    public void setMedia3File(boolean media3File) {
        this.media3File = media3File;
    }

    public boolean isTail() { return isTail; }

    public void setTail(boolean tail) { isTail = tail; }

    public boolean isSkipRenewQuestions() {
        return skipRenewQuestions;
    }

    public void setSkipRenewQuestions(boolean skipRenewQuestions) {
        this.skipRenewQuestions = skipRenewQuestions;
    }

    public String getCurrentSource() { return currentSource; }

    public void setCurrentSource(String currentSource) { this.currentSource = currentSource; }

    @Override
    protected void assignActionEvent() {
        backButton.setOnAction(e -> {
            goBackToPrevious();
        });

        bookMark.setOnMousePressed(e -> {
            historyRecordFile.updateHistoryRecord(parts.get(numberOfRow).getItemNum(),
                    numberOfRow, parts.get(numberOfRow).getPart(), markedToggleButton.isSelected());

            if (checkBookMark(numberOfRow))
                startSound("resources/Click.mp3");
        });

        nextButton.setOnAction(e -> {
            goToNextPage();
        });

        homeButton.setOnAction(e -> {
            makeFadeOut();
        });

        answer1Button.setOnAction(e -> {
            showExplanation(numberOfRow);
        });

        answer2Button.setOnAction( e-> {
            showExplanation(numberOfRow +1);
        });

        answer3Button.setOnAction( e-> {
            showExplanation(numberOfRow +2);
        });

        categoryComboBox.setOnAction(e -> {
            super.setParts(categories);
            int itemNumber;
            if(categoryHashMap.get(categoryComboBox.getValue()) < lastRowNum) {
                itemNumber = categoryHashMap.get(categoryComboBox.getValue());

                for(int i = 0; i < parts.size(); i++) {
                    if(parts.get(i).getPartListNumber().equals(String.valueOf(itemNumber))) {
                        numberOfRow = i;
                    }
                }

                setupQuestions(numberOfRow);
                rebuildTextsAndButtons(numberOfRow);
                restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2);
                checkBookMark(numberOfRow);
            }
        });

        sourceComboBox.setOnAction(e -> {
            if (!skipRenewQuestions)
                renewQuestionsMarkedRecord();
        });

        document1Button.setOnAction(e -> {
            this.showFolderContentsDialog(numberOfRow);
        });

        document2Button.setOnAction(e -> {
            this.showFolderContentsDialog(numberOfRow+1);
        });

        document3Button.setOnAction(e -> {
            this.showFolderContentsDialog(numberOfRow+2);
        });

        listening1Button.setOnAction(e -> {
            mediaPlayer = new MediaPlayer(this.getMedia());
            mediaPlayer.play();
            listening1Button.disableProperty().set(true);

            // Once the player is finished
            mediaPlayer.setOnEndOfMedia( () -> {
                // Enable the startButton for the media-play
                if (listening1Button.disableProperty().get()) {
                    listening1Button.disableProperty().set(false);
                }
            });
        });

        listening2Button.setOnAction(e -> {
            mediaPlayer = new MediaPlayer(this.getMedia2());
            mediaPlayer.play();
            listening2Button.disableProperty().set(true);

            // Once the player is finished
            mediaPlayer.setOnEndOfMedia( () -> {
                // Enable the startButton for the media-play
                if (listening2Button.disableProperty().get()) {
                    listening2Button.disableProperty().set(false);
                }
            });
        });

        listening3Button.setOnAction(e -> {
            mediaPlayer = new MediaPlayer(this.getMedia3());
            mediaPlayer.play();
            listening3Button.disableProperty().set(true);

            // Once the player is finished
            mediaPlayer.setOnEndOfMedia( () -> {
                // Enable the startButton for the media-play
                if (listening3Button.disableProperty().get()) {
                    listening3Button.disableProperty().set(false);
                }
            });
        });

        markedToggleButton.setOnAction(e -> {
            if (markedSourceHeadMap.containsKey(sourceComboBox.getValue()))
                renewQuestionsMarkedRecord();
            else
                markedToggleButton.setSelected(false);
        });

        //ポップアップメニューがリクエストされた場合の処理
        question1.setOnContextMenuRequested((ContextMenuEvent event) -> {
            popupMenu.hide();
            popupMenu = createPopupMenu(question1, "question1");
            popupMenu.show(question1, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        //マウスクリックの場合、ポップアップメニューを不可視にする
        question1.setOnMouseClicked((MouseEvent event) -> { popupMenu.hide(); });

        //ポップアップメニューがリクエストされた場合の処理
        question2.setOnContextMenuRequested((ContextMenuEvent event) -> {
            popupMenu.hide();
            popupMenu = createPopupMenu(question2, "question2");
            popupMenu.show(question2, event.getScreenX(), event.getScreenY());
            event.consume();
        });

        //マウスクリックの場合、ポップアップメニューを不可視にする
        question2.setOnMouseClicked((MouseEvent event) -> { popupMenu.hide(); });

        //ポップアップメニューがリクエストされた場合の処理
        question3.setOnContextMenuRequested((ContextMenuEvent event) -> {
            popupMenu.hide();
            popupMenu = createPopupMenu(question3, "question3");
            popupMenu.show(question3, event.getScreenX(), event.getScreenY());
            event.consume();
        });

        //マウスクリックの場合、ポップアップメニューを不可視にする
        question3.setOnMouseClicked((MouseEvent event) -> { popupMenu.hide(); });
    }

    private void buildUpListViews() {
        for(Map.Entry mapEntry : categoryHashMap.entrySet()) {
            categoryList.add(mapEntry.getKey().toString());
        }

        for(Map.Entry mapEntry : sourceHeadMap.entrySet()) {
            sourceList.add(mapEntry.getKey().toString());
        }
    }

    private void changeRadioButtonProperties(Toggle oldVal, Toggle newVal, String answer, int numberOfRow) {
        String result;
        if(newVal != null) {
            // Cast object to radio button
            RadioButton chk = (RadioButton) newVal.getToggleGroup().getSelectedToggle();

            if (chk.getText().contains(answer)) {
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
            RadioButton oldChk = (RadioButton) oldVal;
            oldChk.setStyle("-fx-background-color:null; -fx-background-radius: 15px; -fx-background-width: 2px;");
        }
    }

    private boolean checkBookMark(int numberOfRow) {
        boolean marked = false;

        // Reset the part5List in the historyRecordFile class
        historyRecordFile.setPart5List(sources);

        if (historyRecordFile.isBookMarkRecord(numberOfRow, parts.get(numberOfRow).getPart(), markedToggleButton.isSelected())) {
            bookMark.setOpacity(1.0);
            marked = true;
        } else {
            bookMark.setOpacity(0.0);
        }
        String item = parts.get(numberOfRow).getItemNum();
        return marked;
    }

    @Override
    protected void createGroupControls() {
        changeImageOrTextField(this.numberOfRow);
        radioButton1A.setToggleGroup(group1);
        radioButton1B.setToggleGroup(group1);
        radioButton1C.setToggleGroup(group1);
        radioButton1D.setToggleGroup(group1);

        group1.selectedToggleProperty().addListener((ObservableValue<? extends Toggle>
                                                             observ, Toggle oldVal, Toggle newVal)->{
            changeRadioButtonProperties(oldVal, newVal, this.answer1, numberOfRow);
        });

        radioButton2A.setToggleGroup(group2);
        radioButton2B.setToggleGroup(group2);
        radioButton2C.setToggleGroup(group2);
        radioButton2D.setToggleGroup(group2);

        group2.selectedToggleProperty().addListener((ObservableValue<? extends Toggle>
                                                             observ, Toggle oldVal, Toggle newVal)->{
            changeRadioButtonProperties(oldVal, newVal, this.answer2, numberOfRow+1);
        });

        radioButton3A.setToggleGroup(group3);
        radioButton3B.setToggleGroup(group3);
        radioButton3C.setToggleGroup(group3);
        radioButton3D.setToggleGroup(group3);

        group3.selectedToggleProperty().addListener((ObservableValue<? extends Toggle>
                                                             observ, Toggle oldVal, Toggle newVal)->{
            changeRadioButtonProperties(oldVal, newVal, this.answer3, numberOfRow+2);
        });

    }

    ContextMenu createPopupMenu(Label question, String name) {
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
                        updateViewImages(name, selection);
                    });
        }
        return menu;
    }

    @Override
    protected void createRadioButtons() {
        group1 = new ToggleGroup();
        radioButton1A = new RadioButton("A");
        radioButton1B = new RadioButton("B");
        radioButton1C = new RadioButton("C");
        radioButton1D = new RadioButton("D");

        group2 = new ToggleGroup();
        radioButton2A = new RadioButton("A");
        radioButton2B = new RadioButton("B");
        radioButton2C = new RadioButton("C");
        radioButton2D = new RadioButton("D");

        group3 = new ToggleGroup();
        radioButton3A = new RadioButton("A");
        radioButton3B = new RadioButton("B");
        radioButton3C = new RadioButton("C");
        radioButton3D = new RadioButton("D");
    }

    @Override
    protected void createToggleButtons() {
        answer1Button = new ToggleButton();
        answer2Button = new ToggleButton();
        answer3Button = new ToggleButton();
        backButton = new ToggleButton();
        nextButton = new ToggleButton();
        homeButton = new ToggleButton();
        document1Button = new ToggleButton();
        document2Button = new ToggleButton();
        document3Button = new ToggleButton();
        listening1Button = new ToggleButton();
        listening2Button = new ToggleButton();
        listening3Button = new ToggleButton();

        //Toggle button group
        ToggleGroup toggleGroup = new ToggleGroup();
        backButton.setToggleGroup(toggleGroup);
        nextButton.setToggleGroup(toggleGroup);
        homeButton.setToggleGroup(toggleGroup);
    }

    @Override
    protected void createImageViews() {
        ImageView view;
        view = createImageView("resources/BACK_64.png");

        //Setting the size of the button
        backButton.setPrefSize(70, 30);
        //Setting a graphic to the button
        backButton.setGraphic(view);

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
        //Setting the size of the button
        answer1Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        answer1Button.setGraphic(view1);

        ImageView view2;
        view2 = createImageView("resources/LIGHTBULBO_64.png");
        //Setting the size of the button
        answer2Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        answer2Button.setGraphic(view2);

        ImageView view3;
        view3 = createImageView("resources/LIGHTBULBO_64.png");
        //Setting the size of the button
        answer3Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        answer3Button.setGraphic(view3);

        ImageView view4;
        view4 = createImageView("resources/BOOK_96.png");
        //Setting the size of the button
        document1Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        document1Button.setGraphic(view4);

        ImageView view5;
        view5 = createImageView("resources/BOOK_96.png");
        //Setting the size of the button
        document2Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        document2Button.setGraphic(view5);

        ImageView view6;
        view6 = createImageView("resources/BOOK_96.png");
        //Setting the size of the button
        document3Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        document3Button.setGraphic(view6);

        ImageView view7;
        view7 = createImageView("resources/HEADPHONE2_64.png");
        //Setting the size of the button
        listening1Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        listening1Button.setGraphic(view7);

        ImageView view8;
        view8 = createImageView("resources/HEADPHONE2_64.png");
        //Setting the size of the button
        listening2Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        listening2Button.setGraphic(view8);

        ImageView view9;
        view9 = createImageView("resources/HEADPHONE2_64.png");
        //Setting the size of the button
        listening3Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        listening3Button.setGraphic(view9);

        review1 = new ImageView();
        review1.setFitHeight(50);
        review1.setFitWidth(40);
        Image image = createImage("resources/PIN2_512.png");
        review1.setImage(image);
        review1.setOpacity(0.0);

        review2 = new ImageView();
        review2.setFitHeight(50);
        review2.setFitWidth(40);
        Image image2 = createImage("resources/PIN2_512.png");
        review2.setImage(image2);
        review2.setOpacity(0.0);

        review3 = new ImageView();
        review3.setFitHeight(50);
        review3.setFitWidth(40);
        Image image3 = createImage("resources/PIN2_512.png");
        review3.setImage(image3);
        review3.setOpacity(0.0);
    }

    @Override
    protected VBox createLayout() {
        buildUpListViews();
        categoryComboBox = new ComboBox(categoryList);
        categoryComboBox.setPrefHeight(30);
        categoryComboBox.setPrefWidth(100);

        markedToggleButton.setText("Marked");
        markedToggleButton.setStyle("-jfx-toggle-color: #6d41c5;");
        markedToggleButton.setSelected(false);

        sourceComboBox = new ComboBox(sourceList);
        // Set to the 1st parts-record's source id
        if(!rebuildComboBoxForMark())
            sourceComboBox.setValue(parts.get(0).getSource());

        result1 = new ImageView();
        result1.setFitHeight(20);
        result1.setFitWidth(20);
        final Image image1 = createImage("resources/CROSS_512.png");
        result1.setImage(image1);
        result1.setOpacity(0);
        resultDate1 = new Label();

        result2 = new ImageView();
        result2.setFitHeight(20);
        result2.setFitWidth(20);
        final Image image2 = createImage("resources/CROSS_512.png");
        result2.setImage(image2);
        result2.setOpacity(0);
        resultDate2 = new Label();

        result3 = new ImageView();
        result3.setFitHeight(20);
        result3.setFitWidth(20);
        final Image image3 = createImage("resources/CROSS_512.png");
        result3.setImage(image3);
        result3.setOpacity(0);
        resultDate3 = new Label();

/*        setupQuestions(this.numberOfRow);
        rebuildTextsAndButtons();
        restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2);*/

        sourceComboBox.setPrefHeight(30);
        sourceComboBox.setPrefWidth(550);

        bookMark = new ImageView();
        bookMark.setFitHeight(50);
        bookMark.setFitWidth(50);
        final Image image = createImage("resources/BOOKMARK3_512.png");
        bookMark.setImage(image);

        renewQuestionsMarkedRecord();

        timerLayout = new TimerLayout(Duration.seconds(20.0), false, parts.get(numberOfRow).getPart());
        timerLayout.createTimerLayout();

        menuButton = new MenuButton(MENU_HEADER);
        splitMenuButton = menuButton.createSplitMenuButton("resources/PIECHART_32.png",
                errataPieChart);
        HBox hBox0 = new HBox();
        hBox0.setSpacing(5);
        hBox0.getChildren().addAll(bookMark, sourceComboBox, markedToggleButton, timerLayout.getTimerLayout(), splitMenuButton);
        hBox0.setAlignment(Pos.TOP_CENTER);

        HBox hBox1 = new HBox();
        hBox1.getChildren().addAll(radioButton1A, radioButton1B, radioButton1C, radioButton1D, answer1Button,
                document1Button, listening1Button, result1, resultDate1, review1);
        hBox1.setSpacing(20);
        hBox1.setAlignment(Pos.TOP_LEFT);
        hBox1.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(radioButton2A, radioButton2B, radioButton2C, radioButton2D, answer2Button,
                document2Button, listening2Button, result2, resultDate2, review2);
        hBox2.setSpacing(20);
        hBox2.setAlignment(Pos.TOP_LEFT);
        hBox2.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox3 = new HBox();
        hBox3.getChildren().addAll(radioButton3A, radioButton3B, radioButton3C, radioButton3D, answer3Button,
                document3Button, listening3Button, result3, resultDate3, review3);
        hBox3.setSpacing(20);
        hBox3.setAlignment(Pos.TOP_LEFT);
        hBox3.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox4 = new HBox();
        hBox4.getChildren().addAll(backButton, nextButton, homeButton);
        hBox4.setSpacing(5);
        hBox4.setAlignment(Pos.TOP_LEFT);
        hBox4.setPadding(new Insets(10, 10, 10, 10));

        questionPane1 = new StackPane();
        questionPane2 = new StackPane();
        questionPane3 = new StackPane();

        questionPane1.getChildren().addAll(question1, questionImgView1);
        questionPane2.getChildren().addAll(question2, questionImgView2);
        questionPane3.getChildren().addAll(question3, questionImgView3);

        questionPane1.setAlignment(Pos.TOP_LEFT);
        questionPane2.setAlignment(Pos.TOP_LEFT);
        questionPane3.setAlignment(Pos.TOP_LEFT);

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().addAll(hBox0, questionPane1, hBox1, questionPane2, hBox2, questionPane3, hBox3, hBox4);

        return vBox;

    }

    @Override
    protected void changeImageOrTextField(int row) {
        if(parts.get(row).getQuestAttrib().matches("F")) {
            questionImgView1.setImage(createImage(parts.get(row).getFilePath()+
                    parts.get(row).getQuestion()));
            changeQestionOpacity(0.0, 1.0, question1, questionImgView1);
        } else {
            question1.setText(this.questionText1);
            questionImgView1.setImage(null);
            changeQestionOpacity(1.0, 0.0, question1, questionImgView1);
        }

        if((row+1) >= parts.size()) {
            question2.setText("");
            questionImgView2.setImage(null);
            changeQestionOpacity(0.0, 0.0, question2, questionImgView2);

        } else if(parts.get(row+1).getQuestAttrib().matches("F")) {
            questionImgView2.setImage(createImage(parts.get(row+1).getFilePath()+
                    parts.get(row+1).getQuestion()));
            changeQestionOpacity(0.0, 1.0, question2, questionImgView2);
        } else {
            question2.setText(this.questionText2);
            questionImgView2.setImage(null);
            changeQestionOpacity(1.0, 0.0, question2, questionImgView2);
        }

        if((row+2) >= parts.size()) {
            question3.setText("");
            questionImgView3.setImage(null);
            changeQestionOpacity(0.0, 0.0, question3, questionImgView3);

        } else if(parts.get(row+2).getQuestAttrib().matches("F")) {
            questionImgView3.setImage(createImage(parts.get(row+2).getFilePath()+
                    parts.get(row+2).getQuestion()));
            changeQestionOpacity(0.0, 1.0, question3, questionImgView3);
        } else {
            question3.setText(this.questionText3);
            questionImgView3.setImage(null);
            changeQestionOpacity(1.0, 0.0, question3, questionImgView3);
        }
    }

    private void changeRadioAnswerButtons(boolean status) {
        this.radioButton1A.setDisable(status);
        this.radioButton1B.setDisable(status);
        this.radioButton1C.setDisable(status);
        this.radioButton1D.setDisable(status);
        this.radioButton2A.setDisable(status);
        this.radioButton2B.setDisable(status);
        this.radioButton2C.setDisable(status);
        this.radioButton2D.setDisable(status);
        this.radioButton3A.setDisable(status);
        this.radioButton3B.setDisable(status);
        this.radioButton3C.setDisable(status);
        this.radioButton3D.setDisable(status);

        this.answer1Button.setDisable(status);
        this.answer2Button.setDisable(status);
        this.answer3Button.setDisable(status);
    }

    private void goToNextPage() {
        if(numberOfRow < lastRowNum) {
            if(numberOfRow <= lastRowNum - 4) {
                // Increase rowNum to move to the next question set
                if (!sourceComboBox.getValue().equals(parts.get(numberOfRow +1).getSource()) ) {
                    numberOfRow = numberOfRow + 1;
                } else if (!sourceComboBox.getValue().equals(parts.get(numberOfRow +2).getSource()) ) {
                    numberOfRow = numberOfRow + 2;
                } else
                    numberOfRow = numberOfRow + 3;

                if(numberOfRow <= lastRowNum - 1) {
                    startSound("resources/bookPageFlip.mp3");
                }
            }
        } else {
            numberOfRow = lastRowNum - 1;
        }

        if (!sourceComboBox.getValue().equals(parts.get(numberOfRow).getSource())) {
            setSkipRenewQuestions(true);
            sourceComboBox.setValue((parts.get(numberOfRow).getSource()));
            setSkipRenewQuestions(false);
        }

        setupQuestions(numberOfRow);
        rebuildTextsAndButtons(numberOfRow);
        restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2);
        checkBookMark(numberOfRow);

    }

    private void goBackToPrevious() {
        // if not 1st Row item
        if(numberOfRow > 0) {

            if (!sourceComboBox.getValue().equals(parts.get(numberOfRow).getSource())) {
                setTail(true);
                sourceComboBox.setValue(parts.get(calculatePreviousRow(part5List, NUM_OF_PART5_QUESTIONS)).getSource());
                setTail(false);
            } else { // perform the lines only if no sourceCombox value changes
                // Decrease rowNum to move to the next question set
                if (numberOfRow - 3 > 0) {
                    numberOfRow = calculatePreviousRow(part5List, NUM_OF_PART5_QUESTIONS);

                    if (!sourceComboBox.getValue().equals(parts.get(numberOfRow).getSource())) {
                        setSkipRenewQuestions(true);
                        sourceComboBox.setValue(parts.get(numberOfRow).getSource());
                        setSkipRenewQuestions(false);
                    }

                } else
                    numberOfRow = 0;

                setupQuestions(numberOfRow);
                rebuildTextsAndButtons(numberOfRow);
                restoreRadioButtons(numberOfRow, numberOfRow + 1, numberOfRow + 2);
                checkBookMark(numberOfRow);
            }
            startSound("resources/bookPageFlip.mp3");
        }
    }

    private boolean lessThanLastRow(int row) {
        boolean isLessThanLastRow;

        if(row < parts.size())
            isLessThanLastRow = true;
        else
            isLessThanLastRow = false;
        return isLessThanLastRow;
    }

    private boolean rebuildComboBoxForMark() {
        boolean isReady = false;
        historyRecordSortedMap = historyRecordFile.getHistoryRecordMap();
        for (Map.Entry rec : historyRecordSortedMap.entrySet()) {
            HistoryRecord historyRecord = (HistoryRecord) rec.getValue();
            if (historyRecord.getBookMarked().matches("Y") &&
                    historyRecord.getPart().matches(parts.get(0).getPart())) {
                String itemNumber = historyRecord.getItemNumber();
                for (int i = 0; i < parts.size(); ++i) {
                    if (parts.get(i).getItemNum().equals(itemNumber)) {
                        this.numberOfRow = i;
                    }
                }
                sourceComboBox.setValue(parts.get(numberOfRow).getSource());

                if(historyRecord.getToggledBookMark().matches("Y"))
                    markedToggleButton.setSelected(true);
                isReady = true;
            }
        }
        return isReady;
    }

    private void rebuildComboBoxForMark(String source) {
        boolean isBookMarked = false;
        int i = 0;
        historyRecordSortedMap = historyRecordFile.getHistoryRecordMap();
        for (Map.Entry rec : historyRecordSortedMap.entrySet()) {
            HistoryRecord historyRecord = (HistoryRecord) rec.getValue();
            if (historyRecord.getBookMarked().matches("Y") &&
                    historyRecord.getPart().matches(parts.get(0).getPart())) {
                String itemNumber = historyRecord.getItemNumber();
                for (; i < parts.size(); ++i) {
                    if (parts.get(i).getItemNum().equals(itemNumber) &&
                    parts.get(i).getSource().equals(source)) {
                        this.numberOfRow = i;
                        isBookMarked = true;
                    }
                }
            }
        }
        if (isBookMarked) {
            sourceComboBox.setValue(parts.get(numberOfRow).getSource());
        } else {
            // Restore the previous source value
            sourceComboBox.setValue(source);
        }
    }

    /**
     * Override the parent implemented method.
     * @return
     */
    protected Scene rebuildForm(){
        createRadioButtons();
        createToggleButtons();
        createImageViews();
        createGroupControls();
        layout = createLayout();
        Image image = createImage("resources/whitePaper2.jpg");

        BackgroundImage backgroundImage= new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        // Background creation
        Background bg = new Background(backgroundImage);
        // set background
        layout.setBackground(bg);
        assignActionEvent();

        scene = new Scene(layout, PART5_WIDTH, PART5_HEIGHT);
        scene.getStylesheets().add("Viper.css");

        // Assign the  Swipe Left action
        scene.setOnSwipeLeft(e-> {
            goBackToPrevious();
        });

        // Assign the  Swipe Right action
        scene.setOnSwipeRight(e-> {
            goToNextPage();
        });
        setupDialogDocumentButtons(numberOfRow);

        setupListeningButtons();

        return scene;
    }

    @Override
    void rebuildTextsAndButtons(int row) {
        // Renew the question texts
        changeImageOrTextField(row);

        // Reset the background fill color on radio-buttons
        resetRadiobuttonBackground(radioButton1A);
        resetRadiobuttonBackground(radioButton1B);
        resetRadiobuttonBackground(radioButton1C);
        resetRadiobuttonBackground(radioButton1D);

        resetRadiobuttonBackground(radioButton2A);
        resetRadiobuttonBackground(radioButton2B);
        resetRadiobuttonBackground(radioButton2C);
        resetRadiobuttonBackground(radioButton2D);

        resetRadiobuttonBackground(radioButton3A);
        resetRadiobuttonBackground(radioButton3B);
        resetRadiobuttonBackground(radioButton3C);
        resetRadiobuttonBackground(radioButton3D);

        setupDialogDocumentButtons(row);
        setupListeningButtons();
    }

   private void renewQuestionsMarkedRecord() {
        int itemNumber = -1;

        if (markedToggleButton.isSelected()) {
            sources = markPart5;
            if(markedSourceHeadMap.containsKey(sourceComboBox.getValue()))
                itemNumber = markedSourceHeadMap.get(sourceComboBox.getValue());

        } else {
            sources =allPart5;
            if(isInitial) {
                historyRecordSortedMap = historyRecordFile.getHistoryRecordMap();
                for (Map.Entry rec: historyRecordSortedMap.entrySet() ) {
                    HistoryRecord historyRecord = (HistoryRecord) rec.getValue();
                    if (historyRecord.getBookMarked().matches("Y") &&
                            historyRecord.getPart().matches(parts.get(0).getPart())) {
                         itemNumber = Integer.parseInt(historyRecord.getItemNumber());
                    }
                }

            } else {
                // Get the sorted itemNumber based on the head or tail
                if (isTail()) {
                    if (sourceTailMap.containsKey(sourceComboBox.getValue()))
                        itemNumber = sourceTailMap.get(sourceComboBox.getValue());
                } else {
                    if (sourceHeadMap.containsKey(sourceComboBox.getValue()))
                        itemNumber = sourceHeadMap.get(sourceComboBox.getValue());
                }
            }
        }

        if (itemNumber != -1) {
            this.setParts(sources);
            this.setLastRowNum(sources.size());

            for (int i = 0; i < parts.size(); i++) {
                if (isInitial && !markedToggleButton.isSelected()) {
                    if (parts.get(i).getItemNum().equals(String.valueOf(itemNumber))) {
                        this.numberOfRow = i;
                        break;
                    }
                } else {
                    if (parts.get(i).getPartListNumber().equals(String.valueOf(itemNumber))) {
                        this.numberOfRow = i;
                        break;
                    }
                }
            }

            if (isTail()) {
                numberOfRow = calculatePreviousRow(part5List, NUM_OF_PART5_QUESTIONS);
            }
        }

        if(markedToggleButton.isSelected()) {
            rebuildComboBoxForMark(parts.get(numberOfRow).getSource());
        }

        setupQuestions(this.numberOfRow);
        rebuildTextsAndButtons(numberOfRow);
        restoreRadioButtons(numberOfRow, numberOfRow + 1, numberOfRow + 2);
        checkBookMark(numberOfRow);

       if(isInitial) isInitial = false;
    }

    private void resetAnswerQuestions(boolean nextQuestion) {
        if(nextQuestion) {
            this.setQuestionText2("");
            this.setAnswer2("");
        }
        this.setQuestionText3("");
        this.setAnswer3("");
    }

    private void restoreRadioButtons(int firstRow, int secondRow, int thirdRow) {
        HashMap<String, ErrorRecord> record;

        record = errataPieChart.getRecord();

        //If there is any error record on HashMap
        if (!record.isEmpty()) {
            boolean result1 = false;
            boolean result2 = false;
            boolean result3 = false;
            String answer1 = null;
            String answer2 = null;
            String answer3 = null;

            if (record.containsKey(parts.get(firstRow).getItemNum())) {
                answer1 = record.get(parts.get(firstRow).getItemNum()).getAnswer();
                result1 = record.get(parts.get(firstRow).getItemNum()).isResult();
            }

            if (record.containsKey(parts.get(secondRow).getItemNum())) {
                answer2 = record.get(parts.get(secondRow).getItemNum()).getAnswer();
                result2 = record.get(parts.get(secondRow).getItemNum()).isResult();
            }

            if (record.containsKey(parts.get(thirdRow).getItemNum())) {
                answer3 = record.get(parts.get(thirdRow).getItemNum()).getAnswer();
                result3 = record.get(parts.get(thirdRow).getItemNum()).isResult();
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

            if (answer2 != null) {
                if (answer2.matches("A")) {
                    rebuildRadiobuttonBackground(radioButton2A, result2);
                } else if (answer2.matches("B")) {
                    rebuildRadiobuttonBackground(radioButton2B, result2);
                } else if (answer2.matches("C")) {
                    rebuildRadiobuttonBackground(radioButton2C, result2);
                } else if (answer2.matches("D")) {
                    rebuildRadiobuttonBackground(radioButton2D, result2);
                }
            }

            if (answer3 != null) {
                if (answer3.matches("A")) {
                    rebuildRadiobuttonBackground(radioButton3A, result3);
                } else if (answer3.matches("B")) {
                    rebuildRadiobuttonBackground(radioButton3B, result3);
                } else if (answer3.matches("C")) {
                    rebuildRadiobuttonBackground(radioButton3C, result3);
                } else if (answer3.matches("D")) {
                    rebuildRadiobuttonBackground(radioButton3D, result3);
                }
            }

            // call to enable/disable dialogButton
            //setupDialogButton(firstRow);

            // Restore the previous question texts
            question1.setText(questionText1);
            question2.setText(questionText2);
            question3.setText(questionText3);
        }
    }

    private void setupDialogDocumentButtons(int row) {
        if (parts.get(row).getTextbookAttrib().matches("D"))
            document1Button.setDisable(false);
        else
            document1Button.setDisable(true);
        if(lessThanLastRow(row + 1)) {
            if (parts.get(row + 1).getTextbookAttrib().matches("D"))
                document2Button.setDisable(false);
            else
                document2Button.setDisable(true);
        } else {
            document2Button.setDisable(true);
        }
        if(lessThanLastRow(row + 2)) {
            if (parts.get(row + 2).getTextbookAttrib().matches("D"))
                document3Button.setDisable(false);
            else
                document3Button.setDisable(true);
        } else {
            document3Button.setDisable(true);
        }
    }

    private void setupListeningButtons() {

        if(isMediaFile())
            listening1Button.setDisable(false);
        else
            listening1Button.setDisable(true);

        if(isMedia2File())
            listening2Button.setDisable(false);
        else
            listening2Button.setDisable(true);

        if(isMedia3File())
            listening3Button.setDisable(false);
        else
            listening3Button.setDisable(true);
    }

    private void setAudioFile(int row) {
        // Create & assign the audio file name.
        String fileName = parts.get(row).getAudioFile();
        String fileFullPath = parts.get(row).getFilePath()+fileName;
        this.setMediaFile(false);
        if(!fileName.isBlank() && (!fileFullPath.isEmpty() || !fileFullPath.isBlank())) {
            File f = new File(fileFullPath);
            try {
                this.setMediaPlayer(new Media(f.toURI().toString()));
                this.setMediaFile(true);
            } catch (MediaException e) {
                e.printStackTrace();
            }
        }

        if(lessThanLastRow(row + 1)) {
            fileName = parts.get(row + 1).getAudioFile();
            fileFullPath = parts.get(row + 1).getFilePath() + fileName;
            this.setMedia2File(false);
            if (!fileName.isBlank() && (!fileFullPath.isEmpty() || !fileFullPath.isBlank())) {
                File f = new File(fileFullPath);
                try {
                    this.setMedia2Player(new Media(f.toURI().toString()));
                    this.setMedia2File(true);
                } catch (MediaException e) {
                    e.printStackTrace();
                }
            }
        }

        if(lessThanLastRow(row + 2)) {
            fileName = parts.get(row + 2).getAudioFile();
            fileFullPath = parts.get(row + 2).getFilePath() + fileName;
            this.setMedia3File(false);
            if (!fileName.isBlank() && (!fileFullPath.isEmpty() || !fileFullPath.isBlank())) {
                File f = new File(fileFullPath);
                try {
                    this.setMedia3Player(new Media(f.toURI().toString()));
                    this.setMedia3File(true);
                } catch (MediaException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setResultImage(int row) {
        String result = parts.get(row).getResult();
        String resultDate = parts.get(row).getResultDate();
        switchResultImage(result, resultDate, result1, resultDate1);

        if(lessThanLastRow(row + 1)) {
            if (currentSource.contains(parts.get(row + 1).getSource())) {
                result = parts.get(row + 1).getResult();
                resultDate = parts.get(row + 1).getResultDate();
                switchResultImage(result, resultDate, result2, resultDate2);

            } else {
                result2.setOpacity(0);
                resultDate2.setText("");
            }
        } else {
            result2.setOpacity(0);
            resultDate2.setText("");
        }

        if(lessThanLastRow(row + 2)) {
            if (currentSource.contains(parts.get(row + 2).getSource())) {
                result = parts.get(row + 2).getResult();
                resultDate = parts.get(row + 2).getResultDate();
                switchResultImage(result, resultDate, result3, resultDate3);

            } else {
                result3.setOpacity(0);
                resultDate3.setText("");
            }
        } else {
            result3.setOpacity(0);
            resultDate3.setText("");
        }
    }

    private void setReviewImage(int row) {
        String review = reviewManagement.returnReviewRecord(parts.get(row).getItemNum());
        if(review.matches("Mark")) {
            review1.setOpacity(1.0);
        } else {
            review1.setOpacity(0);;
        }

        if(lessThanLastRow(row + 1)) {
            review = reviewManagement.returnReviewRecord(parts.get(row + 1).getItemNum());
            if (review.matches("Mark")) {
                review2.setOpacity(1.0);
            } else {
                review2.setOpacity(0);
            }
        } else {
            review2.setOpacity(0);
        }

        if(lessThanLastRow(row + 2)) {
            review = reviewManagement.returnReviewRecord(parts.get(row + 2).getItemNum());
            if (review.matches("Mark")) {
                review3.setOpacity(1.0);
            } else {
                review3.setOpacity(0);
            }
        } else {
            review3.setOpacity(0);
        }
    }

    /*
    In order to handle Japanese characters to compare, took the
    contentEquals() method.
     */
    @Override
    void setupQuestions(int row) {
        changeRadioAnswerButtons(true);

        this.setQuestionText1(parts.get(row).getQuestion());
        this.setAnswer1(parts.get(row).getAnswer());
        this.setCurrentSource(parts.get(row).getSource());
        this.radioButton1A.setDisable(false);
        this.radioButton1B.setDisable(false);
        this.radioButton1C.setDisable(false);
        this.radioButton1D.setDisable(false);
        this.answer1Button.setDisable(false);

        if(lessThanLastRow(row + 1)) {
            if (getCurrentSource().contentEquals(parts.get(row+1).getSource())) {
                this.setQuestionText2(parts.get(row + 1).getQuestion());
                this.setAnswer2(parts.get(row + 1).getAnswer());
                this.radioButton2A.setDisable(false);
                this.radioButton2B.setDisable(false);
                this.radioButton2C.setDisable(false);
                this.radioButton2D.setDisable(false);
                this.answer2Button.setDisable(false);

                if(lessThanLastRow(row + 2)) {
                    if (getCurrentSource().contentEquals(parts.get(row + 2).getSource())) {
                        this.setQuestionText3(parts.get(row + 2).getQuestion());
                        this.setAnswer3(parts.get(row + 2).getAnswer());
                        this.radioButton3A.setDisable(false);
                        this.radioButton3B.setDisable(false);
                        this.radioButton3C.setDisable(false);
                        this.radioButton3D.setDisable(false);
                        this.answer3Button.setDisable(false);
                    } else {
                        resetAnswerQuestions(false);
                    }
                } else {
                    resetAnswerQuestions(false);
                }
            } else {
                resetAnswerQuestions(true);
            }
        } else {
            resetAnswerQuestions(true);
        }

        setAudioFile(row);
        setResultImage(row);
        setReviewImage(row);
    }

    private void updateViewImages(String name, String selection) {
        String itemNumber = null;
        String groupItemNumber;

        groupItemNumber= parts.get(numberOfRow).getItemNum();

        if(name.matches("question1")) {
            if (selection.matches("Mark"))
                review1.setOpacity(1.0);
            else
                review1.setOpacity(0.0);

            itemNumber = parts.get(numberOfRow).getItemNum();
        } else if(name.matches("question2")) {
            if (selection.matches("Mark"))
                review2.setOpacity(1.0);
            else
                review2.setOpacity(0.0);

            itemNumber = parts.get(numberOfRow+1).getItemNum();
        } else if(name.matches("question3")) {
            if (selection.matches("Mark"))
                review3.setOpacity(1.0);
            else
                review3.setOpacity(0.0);

            itemNumber = parts.get(numberOfRow+2).getItemNum();
        }

        reviewManagement.updateResultRecord(itemNumber, groupItemNumber, parts.get(numberOfRow).getPart(), selection.toString(), false);
    }
}