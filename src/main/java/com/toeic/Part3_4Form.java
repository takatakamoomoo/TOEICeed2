package com.toeic;

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ChangeListener;
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
import java.util.*;

public class Part3_4Form extends PartForm{
    public static final String MENU_HEADER = "TOEIC PART3/4";
    public static final String _5EMPTY_ROWS = "\n\n\n\n\n";

    // 18 min for 39 questions -> 28 seconds per question
    private static final double PART3_TIME = 28000;
    // 15 min for 30 questions -> 15 seconds per question
    private static final double PART4_TIME = 15000;

    private static final int PART3_4_HEIGHT = 780;
    private static final int PART3_4_WIDTH = 1360;

    private static final String TAB_0 = "";
    private static final String TAB_01 = "Dialog";
    private static final String TAB_02 = "Script";
    private static final int NUM_OF_PART3_4_QUESTIONS = 3;
    private static final int DEFAULT_NUM_OF_QUESTION = 3;

    private Background bg;

    private boolean m_stageShowing = false;
    private boolean isTail = false;
    private boolean skipRenewQuestions = false;

    private AnchorPane pane;
    private BorderPane basePane;
    private BorderPane border;
    private BorderPane rightLayout;

    private ComboBox sourceComboBox;

    private ContextMenu popupMenu =null;

    private HBox hBox5;

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

    private Label examinationType;

    private LinkedList<String> part3_4List;

    private SortedMap<String, Integer> sourceHeadMap;
    private SortedMap<String, Integer> sourceTailMap;

    private SortedMap<String, HistoryRecord> historyRecordSortedMap;

    private JFXTabPane tabPaneLayout;

    private Media media;
    private MediaPlayer mediaPlayer;
    private MediaControl mediaControl;

    private MenuButton menuButton;

    private ObservableList<String> sourceList = FXCollections.observableArrayList();

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

    private String part;
    private String currentMediaFile = null;

    private Tab firstTab;
    private Tab secondTab;
    private Tab thirdTab;
    private TimerLayout timerLayout;
    private VBox topLeftLayout;
    private VBox topRightLayout;

    Scene scene;


    /**
     * Constructor
     * @param stage
     * @param root
     * @param numberOfRow
     * @param parts
     * @param lastRowNum
     */
    public Part3_4Form(Stage stage, Pane root, int numberOfRow, List<Part> parts, int lastRowNum,
                       HistoryRecordFile historyRecordFile, ResultManagement resultManagement,
                       ReviewManagement reviewManagement) {
        super(numberOfRow, parts, lastRowNum, stage, root, historyRecordFile, resultManagement, reviewManagement);

        sourceHeadMap = new TreeMap<>();
        sourceTailMap = new TreeMap<>();

        part3_4List = new LinkedList<>();

        for(Part part : parts)
        {
            sourceHeadMap.putIfAbsent(part.getSource(), Integer.valueOf(part.getPartListNumber()));
            sourceTailMap.put(part.getSource(), Integer.valueOf(part.getPartListNumber()));
            part3_4List.add(part.getSource());

            reviewManagement.updateResultRecord(part.getItemNum(), part.getItemNum(), part.getPart(), part.getReview(), true);
        }

        popupMenu = new ContextMenu();
        part = parts.get(0).getPart();
        currentMediaFile = parts.get(0).getAudioFile();

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
    }

    /**
     * Getters and Setters
     * @return
     */
    public BorderPane getLayout() {
        return border;
    }

    public TimerLayout getTimerLayout() { return timerLayout; }

    public boolean isTail() { return isTail; }

    public void setTail(boolean tail) { isTail = tail; }

    public boolean isSkipRenewQuestions() {
        return skipRenewQuestions;
    }

    public void setSkipRenewQuestions(boolean skipRenewQuestions) {
        this.skipRenewQuestions = skipRenewQuestions;
    }

    @Override
    protected void assignActionEvent() {
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

        answer1Button.setOnAction(e -> {
            showExplanation(numberOfRow);
        });

        answer2Button.setOnAction( e-> {
            showExplanation(numberOfRow +1);
        });

        answer3Button.setOnAction( e-> {
            showExplanation(numberOfRow +2);
        });


       sourceComboBox.setOnAction(e -> {
           if (!skipRenewQuestions)
               renewQuestions();
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

        textbookButton.setOnAction(e-> {
            this.showFolderContentsDialog(numberOfRow);
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

        //ポップアップメニューがリクエストされた場合の処理
        questionImgView1.setOnContextMenuRequested((ContextMenuEvent event) -> {
            popupMenu.hide();
            popupMenu = createPopupMenu(question1, "question1");
            popupMenu.show(questionImgView1, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        //マウスクリックの場合、ポップアップメニューを不可視にする
        questionImgView1.setOnMouseClicked((MouseEvent event) -> { popupMenu.hide(); });

        //ポップアップメニューがリクエストされた場合の処理
        questionImgView2.setOnContextMenuRequested((ContextMenuEvent event) -> {
            popupMenu.hide();
            popupMenu = createPopupMenu(question2, "question2");
            popupMenu.show(questionImgView2, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        //マウスクリックの場合、ポップアップメニューを不可視にする
        questionImgView2.setOnMouseClicked((MouseEvent event) -> { popupMenu.hide(); });

        //ポップアップメニューがリクエストされた場合の処理
        questionImgView3.setOnContextMenuRequested((ContextMenuEvent event) -> {
            popupMenu.hide();
            popupMenu = createPopupMenu(question3, "question3");
            popupMenu.show(questionImgView3, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        //マウスクリックの場合、ポップアップメニューを不可視にする
        questionImgView3.setOnMouseClicked((MouseEvent event) -> { popupMenu.hide(); });
    }

    private void buildUpPartListView() {
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
        if(oldVal != null) { // Avoid to access to the null object
            // Cast object to radio button
            RadioButton oldChk = (RadioButton) oldVal;
            oldChk.setStyle("-fx-background-color:null; -fx-background-radius: 15px; -fx-background-width: 2px;");
        }
    }

    private boolean checkBookMark(int numberOfRow) {
        boolean marked = false;
        if (historyRecordFile.isBookMarkRecord(numberOfRow, parts.get(numberOfRow).getPart())) {
            bookMark.setOpacity(1.0);
            marked = true;
        } else {  // if not BookMarkedRecord
            bookMark.setOpacity(0.0);
        }
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
        listening1Button = new ToggleButton();
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
        view2 = createImageView("resources/LIGHTBULBO_64.png");
        view2.setFitWidth(25);
        view2.setFitHeight(25);

        //Setting a graphic to the button
        answer2Button.setGraphic(view2);

        ImageView view3;
        view3 = createImageView("resources/LIGHTBULBO_64.png");
        view3.setFitWidth(25);
        view3.setFitHeight(25);

        //Setting a graphic to the button
        answer3Button.setGraphic(view3);

        ImageView view9;
        view9 = createImageView("resources/HEADPHONE2_64.png");
        view9.setFitWidth(70);
        view9.setFitHeight(30);
        //Setting a graphic to the button
        listening1Button.setGraphic(view9);

        ImageView view10;
        view10 = createImageView("resources/BOOK3_96.png");
        view10.setFitWidth(25);
        view10.setFitHeight(25);
        //Setting a graphic to the button
        textbookButton.setGraphic(view10);
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

        sourceComboBox.setMaxHeight(30);
        sourceComboBox.setPrefHeight(30);
        sourceComboBox.setPrefWidth(450);

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

        review1 = new ImageView();
        review1.setFitHeight(40);
        review1.setFitWidth(40);
        Image image10 = createImage("resources/PIN2_512.png");
        review1.setImage(image10);
        review1.setOpacity(0.0);

        review2 = new ImageView();
        review2.setFitHeight(40);
        review2.setFitWidth(40);
        Image image6 = createImage("resources/PIN2_512.png");
        review2.setImage(image6);
        review2.setOpacity(0.0);

        review3 = new ImageView();
        review3.setFitHeight(40);
        review3.setFitWidth(40);
        Image image7 = createImage("resources/PIN2_512.png");
        review3.setImage(image7);
        review3.setOpacity(0.0);

        setupQuestions(this.numberOfRow);
        rebuildTextsAndButtons(numberOfRow);
        restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2);

        bookMark = new ImageView();
        bookMark.setFitHeight(50);
        bookMark.setFitWidth(50);

        String part = this.parts.get(0).getPart();
        final Image image;
        image = createImage("resources/BOOKMARK3_512.png");

        bookMark.setImage(image);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(bookMark, sourceComboBox);

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10, 10, 30, 10));
        vBox.getChildren().addAll(hBox);
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
            changeQestionOpacity(1.0, 0.0, question1, questionImgView1);
        }

        if(parts.get(row+1).getQuestAttrib().matches("F")) {
            questionImgView2.setImage(createImage(parts.get(row+1).getFilePath()+
                    parts.get(row+1).getQuestion()));
            changeQestionOpacity(0.0, 1.0, question2, questionImgView2);
        } else {
            question2.setText(this.questionText2);
            changeQestionOpacity(1.0, 0.0, question2, questionImgView2);
        }

        if(parts.get(row+2).getQuestAttrib().matches("F")) {
            questionImgView3.setImage(createImage(parts.get(row+2).getFilePath()+
                    parts.get(row+2).getQuestion()));
            changeQestionOpacity(0.0, 1.0, question3, questionImgView3);
        } else {
            question3.setText(this.questionText3);
            changeQestionOpacity(1.0, 0.0, question3, questionImgView3);
        }
    }

    protected HBox createBottomLayout() {
        hBox5 = new HBox();
        // Set up the initial media control
        hBox5.getChildren().addAll(setUpMediaControl(numberOfRow));
        // Set up the initial currentMediaFile variable
        currentMediaFile = parts.get(numberOfRow).getAudioFile();
        return hBox5;
    }

    protected ScrollPane createCenterLayout() {
        HBox hBox1 = new HBox();
        hBox1.getChildren().addAll(radioButton1A, radioButton1B, radioButton1C, radioButton1D ,answer1Button,
                                   textbookButton, result1, resultDate1, review1);
        hBox1.setSpacing(20);
        hBox1.setAlignment(Pos.TOP_LEFT);
        hBox1.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox2 = new HBox();
        hBox2.getChildren().addAll(radioButton2A, radioButton2B, radioButton2C, radioButton2D ,answer2Button,
                                   result2, resultDate2, review2);
        hBox2.setSpacing(20);
        hBox2.setAlignment(Pos.TOP_LEFT);
        hBox2.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox3 = new HBox();
        hBox3.getChildren().addAll(radioButton3A, radioButton3B, radioButton3C, radioButton3D ,answer3Button,
                                   result3, resultDate3, review3);
        hBox3.setSpacing(20);
        hBox3.setAlignment(Pos.TOP_LEFT);
        hBox3.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox4 = new HBox();
        hBox4.setSpacing(20);
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
        vBox.getChildren().addAll(topLeftLayout, questionPane1, hBox1, questionPane2, hBox2, questionPane3, hBox3, hBox4);

        vBox.setBackground(bg);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setBackground(bg);
        scrollPane.setPannable(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(vBox);

        return scrollPane;
    }

    private void createScriptTabPane(int numberOfRow) {

        secondTab.setText(TAB_0);
        firstTab.setContent(null);
        tabPaneLayout.getTabs().add(firstTab);

        secondTab.setText(TAB_01);
        if(!parts.get(numberOfRow).getDialogAttrib().isEmpty()) {
            secondTab.setDisable(false);
            secondTab.setContent(createDocumentVBox(numberOfRow, parts.get(numberOfRow).getDialogAttrib(),
                    parts.get(numberOfRow).getDialog(), parts.get(numberOfRow).getFilePath()));
        } else {
            secondTab.setDisable(true);
            secondTab.setContent(null);
        }
        tabPaneLayout.getTabs().add(secondTab);

        thirdTab.setText(TAB_02);
        thirdTab.setContent(createDocumentVBox(numberOfRow, parts.get(numberOfRow).getScriAttrib(),
                parts.get(numberOfRow).getScript(), parts.get(numberOfRow).getFilePath()));
        tabPaneLayout.getTabs().add(thirdTab);

        tabPaneLayout.getStylesheets().add("jfoenix-components.css");

        timerLayout.createTimerLayout();
        timerLayout.setItemNum(parts.get(numberOfRow).getItemNum());
        timerLayout.performResetButtonAction();

        menuButton = new MenuButton(MENU_HEADER);
        splitMenuButton = menuButton.createSplitMenuButton("resources/PIECHART_32.png",
                errataPieChart);

        Label filler = new Label();
        HBox hBox0 = new HBox();
        hBox0.setSpacing(15);
        hBox0.getChildren().addAll(examinationType, filler, timerLayout.getTimerLayout(), splitMenuButton);
        hBox0.setAlignment(Pos.TOP_RIGHT);

        rightLayout.setTop(hBox0);
        rightLayout.setCenter(tabPaneLayout);

        timerLayout.setItemNum(parts.get(numberOfRow).getItemNum());
    }

    private void goToNextPage() {
        if(numberOfRow < lastRowNum - 4) {
            // Increase rowNum to move to the next question set
            int questionNubmer = getQuestionNubmer(numberOfRow);
            numberOfRow = numberOfRow + questionNubmer;

            if (!sourceComboBox.getValue().equals(parts.get(numberOfRow).getSource())) {
                sourceComboBox.setValue((parts.get(numberOfRow).getSource()));
            }

            if (!currentMediaFile.contentEquals(parts.get(numberOfRow).getAudioFile())) {
                setUpMediaControlLayout(numberOfRow);
            }

            createScriptTabPane(numberOfRow);
            setupQuestions(numberOfRow);
            setUpTimeLimit(numberOfRow);

            renewSourceCombo();
            rebuildTextsAndButtons(numberOfRow);
            restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2);
            checkBookMark(numberOfRow);
            startSound("resources/bookPageFlip.mp3");
        }
    }

    private void goBackToPrevious() {
        // if not 1st Row item
        if(numberOfRow > 0) {
            int questionNubmer = getQuestionNubmer(numberOfRow-1);

            if (!sourceComboBox.getValue().equals(parts.get(numberOfRow).getSource())) {
                setTail(true);
                sourceComboBox.setValue(parts.get(calculatePreviousRow(part3_4List, questionNubmer)).getSource());
                setTail(false);
            } else { // perform the lines only if no sourceCombox value changes

                // Decrease rowNum to move to the next question set
                if (numberOfRow - questionNubmer > 0) {
                    numberOfRow = calculatePreviousRow(part3_4List, questionNubmer);

                    if (!sourceComboBox.getValue().equals(parts.get(numberOfRow).getSource())) {
                        setSkipRenewQuestions(true);
                        sourceComboBox.setValue(parts.get(numberOfRow).getSource());
                        setSkipRenewQuestions(false);
                    }

                } else {
                    numberOfRow = 0;
                }

                if (!currentMediaFile.contentEquals(parts.get(numberOfRow).getAudioFile())) {
                    setUpMediaControlLayout(numberOfRow);
                }
                createScriptTabPane(numberOfRow);
                setupQuestions(numberOfRow);
                setUpTimeLimit(numberOfRow);

                renewSourceCombo();
                rebuildTextsAndButtons(numberOfRow);
                restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2);
                checkBookMark(numberOfRow);
            }
            startSound("resources/bookPageFlip.mp3");
        }
    }

    private int getQuestionNubmer(int numberOfRow) {
        int questionNubmer;
        if(parts.get(numberOfRow).getQuestionNum().isEmpty())
            questionNubmer = DEFAULT_NUM_OF_QUESTION;
        else
            questionNubmer = Integer.valueOf(parts.get(numberOfRow).getQuestionNum());
        return questionNubmer;
    }

    /**
     * Override the parent implemented method.
     * @return
     */
    protected Scene rebuildForm(){
        Image image;
        createToggleButtons();
        createRadioButtons();
        enableDisableRadioAnswerButtons();
        createImageViews();
        createGroupControls();

        border = new BorderPane();

        rightLayout = new BorderPane();
        tabPaneLayout = new JFXTabPane();
        firstTab = new Tab();
        secondTab = new Tab();
        thirdTab = new Tab();
        examinationType = new Label();

        // Get the PART from the 1st record
        String part = this.parts.get(0).getPart();

        if (part.matches("3"))
            image = createImage("resources/A3paper.jpg");
        else
            image = createImage("resources/whitePaper.jpg");

        BackgroundImage backgroundImage= new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        // Background creation
        bg = new Background(backgroundImage);

        topLeftLayout = createLayout();

        // set background
        border.setBackground(bg);
        border.setCenter(createCenterLayout());

        assignActionEvent();

        setupDialogDocumentButtons(numberOfRow);
        timerLayout = new TimerLayout(Duration.seconds(0.0), true, parts.get(numberOfRow).getPart());
        createScriptTabPane(numberOfRow);
           setUpTimeLimit(numberOfRow);

        //Creating a SplitPane
        SplitPane splitPane = new SplitPane();
        //Creating stack panes holding the ImageView objects
        StackPane stackPane1 = new StackPane(border);
        StackPane stackPane2 = new StackPane(rightLayout);
        stackPane2.setBackground(bg);

        //Adding the stackpanes to the splitpane
        splitPane.getItems().addAll(stackPane1, stackPane2);
        basePane = new BorderPane();
        basePane.setCenter(splitPane);
        basePane.setBottom(createBottomLayout());
        basePane.setBackground(bg);

        //Setting anchor pane as the layout
        pane = new AnchorPane();
        AnchorPane.setTopAnchor(basePane, 15.0);
        AnchorPane.setRightAnchor(basePane, 5.0);
        AnchorPane.setBottomAnchor(basePane, 15.0);
        AnchorPane.setLeftAnchor(basePane, 5.0);
        pane.getChildren().add(basePane);
        pane.setBackground(bg);

        scene = new Scene(pane, PART3_4_WIDTH, PART3_4_HEIGHT);
        scene.getStylesheets().add("Viper.css");

        // Assign the Swipe Left action
        scene.setOnSwipeLeft(e-> {
            goBackToPrevious();
        });

        // Assign the Swipe Right action
        scene.setOnSwipeRight(e-> {
            goToNextPage();
        });

        ChangeListener<Number> changeListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                splitPane.setDividerPositions(0.42);
                if (m_stageShowing) {
                    observable.removeListener(this);
                }
            }
        };
        splitPane.widthProperty().addListener(changeListener);
        splitPane.heightProperty().addListener(changeListener);

        m_stageShowing = true;
        return scene;
    }

    @Override
    protected void rebuildTextsAndButtons(int row) {
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

        enableDisableRadioAnswerButtons();

        setupDialogDocumentButtons(row);
    }

    private void enableDisableRadioAnswerButtons() {
        String questionNum = String.valueOf(getQuestionNubmer(numberOfRow));

        if (questionNum.matches("1")) {
            radioButton1A.setDisable(false);
            radioButton1B.setDisable(false);
            radioButton1C.setDisable(false);
            radioButton1D.setDisable(false);
            answer1Button.setDisable(false);

            radioButton2A.setDisable(true);
            radioButton2B.setDisable(true);
            radioButton2C.setDisable(true);
            radioButton2D.setDisable(true);
            answer2Button.setDisable(true);

            radioButton3A.setDisable(true);
            radioButton3B.setDisable(true);
            radioButton3C.setDisable(true);
            radioButton3D.setDisable(true);
            answer3Button.setDisable(true);

        } else if (questionNum.matches("2")) {
            radioButton1A.setDisable(false);
            radioButton1B.setDisable(false);
            radioButton1C.setDisable(false);
            radioButton1D.setDisable(false);
            answer1Button.setDisable(false);

            radioButton2A.setDisable(false);
            radioButton2B.setDisable(false);
            radioButton2C.setDisable(false);
            radioButton2D.setDisable(false);
            answer2Button.setDisable(false);

            radioButton3A.setDisable(true);
            radioButton3B.setDisable(true);
            radioButton3C.setDisable(true);
            radioButton3D.setDisable(true);
            answer3Button.setDisable(true);

        } else if (questionNum.matches("3")) {
            radioButton1A.setDisable(false);
            radioButton1B.setDisable(false);
            radioButton1C.setDisable(false);
            radioButton1D.setDisable(false);
            answer1Button.setDisable(false);

            radioButton2A.setDisable(false);
            radioButton2B.setDisable(false);
            radioButton2C.setDisable(false);
            radioButton2D.setDisable(false);
            answer2Button.setDisable(false);

            radioButton3A.setDisable(false);
            radioButton3B.setDisable(false);
            radioButton3C.setDisable(false);
            radioButton3D.setDisable(false);
            answer3Button.setDisable(false);
        }
    }

    private void renewSourceCombo() {
        String source = sourceComboBox.getValue().toString();
        String record = parts.get(numberOfRow).getSource();

        if (!source.matches(record))
            sourceComboBox.setValue(record);
    }

    private void renewQuestions() {
        int itemNumber;
        if(sourceHeadMap.get(sourceComboBox.getValue()) < lastRowNum) {
            itemNumber = sourceHeadMap.get(sourceComboBox.getValue());


            // Get the sorted itemNumber based on the head or tail
            if(isTail()) {
                itemNumber = sourceTailMap.get(sourceComboBox.getValue());
            } else {
                itemNumber = sourceHeadMap.get(sourceComboBox.getValue());
            }

            for(int i = 0; i < parts.size(); ++i) {
                if(parts.get(i).getPartListNumber().equals(String.valueOf(itemNumber))) {
                    numberOfRow = i;
                }
            }

            if (!currentMediaFile.contentEquals(parts.get(numberOfRow).getAudioFile())) {
                setUpMediaControlLayout(numberOfRow);
            }

            createScriptTabPane(numberOfRow);
            setupQuestions(numberOfRow);
            setUpTimeLimit(numberOfRow);
            rebuildTextsAndButtons(numberOfRow);
            restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2);
            checkBookMark(numberOfRow);
        }
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

            if (answer1 != null) {
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
            setupDialogDocumentButtons(firstRow);

            // Restore the previous question texts
            question1.setText(questionText1);
            question2.setText(questionText2);
            question3.setText(questionText3);

        }
    }

    private void setupDialogDocumentButtons(int row) {
        if(parts.get(row).getAudioFile().isEmpty())
            listening1Button.setDisable(true);
        else
            listening1Button.setDisable(false);
    }

    private HBox setUpMediaControl(int row) {
        String fileFullPath = parts.get(row).getFilePath()+parts.get(row).getAudioFile();
        if(!fileFullPath.isEmpty() || !fileFullPath.isBlank()) {
            File f = new File(fileFullPath);
            try {
                if (this.isFirstTime()) {
                    this.setFirstTime(false);
                } else {
                    mediaPlayer.stop();
                }
                mediaPlayer = new MediaPlayer(new Media(f.toURI().toString()));
                mediaControl = new MediaControl();
            } catch (MediaException e) {
                e.printStackTrace();
            }
        }
        return mediaControl.createMediaControl(backButton, nextButton, homeButton, mediaPlayer);
    }

    private void setUpMediaControlLayout(int row) {
        // Create & assign the audio file name.
        if (!currentMediaFile.contains(parts.get(row).getAudioFile())) {
            currentMediaFile = parts.get(row).getAudioFile();
            // Remove & restore the media controls to renew the media file
            hBox5.getChildren().remove(0);
            hBox5.getChildren().add(setUpMediaControl(row));
        }
    }

    @Override
    protected void setupQuestions(int row) {
        String result = parts.get(row).getResult();
        String resultDate = parts.get(row).getResultDate();
        String questionNum = String.valueOf(getQuestionNubmer(row));

        this.setQuestionText1(parts.get(row).getQuestion());
        this.setAnswer1(parts.get(row).getAnswer());
        switchResultImage(result, resultDate, result1, resultDate1, review1);

        String review = reviewManagement.returnReviewRecord(parts.get(row).getItemNum());
        if(review.matches("Mark")) {
            review1.setOpacity(1.0);
        } else {
            review1.setOpacity(0);;
        }

        // Set the questions for 2 items
        if (questionNum.matches("1")) {
            this.setQuestionText2(_5EMPTY_ROWS);
            this.question2.setOpacity(1);
            this.setAnswer2("");

            this.questionImgView2.setImage(null);
            this.questionImgView2.setOpacity(0);
            result2.setOpacity(0);
            resultDate2.setText("");
            review2.setOpacity(0);

        } else {
            result = parts.get(row + 1).getResult();
            resultDate = parts.get(row + 1).getResultDate();

            this.setQuestionText2(parts.get(row + 1).getQuestion());
            this.setAnswer2(parts.get(row + 1).getAnswer());
            switchResultImage(result, resultDate, result2, resultDate2, review2);

            review = reviewManagement.returnReviewRecord(parts.get(row + 1).getItemNum());
            if(review.matches("Mark")) {
                review2.setOpacity(1.0);
            } else {
                review2.setOpacity(0);
            }
        }

        // Set the questions for 3 items
        switch (questionNum) {
            case "1": case "2":
                this.setQuestionText3(_5EMPTY_ROWS);
                this.question3.setOpacity(1);
                this.setAnswer3("");

                this.questionImgView3.setImage(null);
                this.questionImgView3.setOpacity(0);

                result3.setOpacity(0);
                resultDate3.setText("");
                review3.setOpacity(0);
                break;
            case "3":
                result = parts.get(row + 2).getResult();
                resultDate = parts.get(row + 2).getResultDate();
                switchResultImage(result, resultDate, result3, resultDate3, review3);

                review = reviewManagement.returnReviewRecord(parts.get(row + 2).getItemNum());
                if(review.matches("Mark")) {
                    review3.setOpacity(1.0);
                } else {
                    review3.setOpacity(0);
                }

                this.setQuestionText3(parts.get(row + 2).getQuestion());
                this.setAnswer3(parts.get(row + 2).getAnswer());
                break;
        }

        // Set the questions for 4 items
        // Set the questions for 5 items


        // Create & assign the audio file name.
        String autoFileName = parts.get(row).getAudioFile();
        if (!autoFileName.isEmpty()) {
            String fileFullPath = parts.get(row).getFilePath()+autoFileName;
            if(!fileFullPath.isEmpty() || !fileFullPath.isBlank()) {
                File f = new File(fileFullPath);
                try {
                    this.setMediaPlayer(new Media(f.toURI().toString()));
                } catch (MediaException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setUpTimeLimit(int row) {
        if (part.matches("3"))
                timerLayout.setTimelimit(PART3_TIME);
        else // PART 4
                timerLayout.setTimelimit(PART4_TIME);
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