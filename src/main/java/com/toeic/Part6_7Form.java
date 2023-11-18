package com.toeic;

import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXToggleButton;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.*;

public class Part6_7Form extends PartForm{
    public static final String MENU_HEADER = "TOEIC PART7";

    // 10 minutes for 16 questions -> approx 37.5 seconds per question
    private static final double PART6_TIME = 37500.0;
    // 55 minutes for 54 questions -> approx 1 min per question (60 * 1000)
    private static final double PART7_TIME = 60000.0;

    private static final int PART7_HEIGHT = 780;
    private static final int PART7_WIDTH = 1360;

    private static final String TAB_0 = "Single";
    private static final String TAB_01 = "Double";
    private static final String TAB_02 = "Triple";
    public static final String ARE_YOU_SURE_TO_CHANGE_THE_PAGE = "Are you sure to change the page?";

    private Background bg;

    private boolean m_stageShowing = false;

    private boolean isExpanded = false;
    private boolean isNoWarningDialog = true;

    private boolean isSliderHidden = true;
    private boolean isTail = false;

    private boolean isPart7showAnswer = false;
    private boolean isInitialPrevSourceId = true;
    private boolean skipRenewQuestions = false;

    private BorderPane border;
    private BorderPane rightLayout;

    private ComboBox sourceComboBox;

    private ContextMenu popupMenu =null;

    private CustomDialog warningDialog;

    private double sliderWidth = 550.0; // Initial width of the sidebar

    private String previousSourceId = "";

    private Properties property = null;

    private ImageView expand;
    private ImageView shrink;
    private ImageView bookMark;
    private ImageView review1;
    private ImageView review2;
    private ImageView review3;
    private ImageView review4;
    private ImageView review5;
    private ImageView result1;
    private ImageView result2;
    private ImageView result3;
    private ImageView result4;
    private ImageView result5;

    private ImageView singleTabImage;

    private Label resultDate1;
    private Label resultDate2;
    private Label resultDate3;
    private Label resultDate4;
    private Label resultDate5;

    private Label examinationType;

    private SortedMap<String, Integer> sourceHeadMap;
    private SortedMap<String, Integer> sourceTailMap;

    private SortedMap<String, HistoryRecord> historyRecordSortedMap;

    private SplitPane splitPane;

    private JFXTabPane tabPaneLayout;

    private Media media;
    private MediaPlayer mediaPlayer;

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

    private RadioButton radioButton4A;
    private RadioButton radioButton4B;
    private RadioButton radioButton4C;
    private RadioButton radioButton4D;

    private RadioButton radioButton5A;
    private RadioButton radioButton5B;
    private RadioButton radioButton5C;
    private RadioButton radioButton5D;

    private SplitMenuButton splitMenuButton;

    private String part;

    private Tab singleTab;
    private Tab doubleTab;
    private Tab tripleTab;

    private TimerLayout timerLayout;
    private VBox topLeftLayout;
    private VBox topRightLayout;

    private VBox buttonAttached;

    Scene scene;

    /**
     * Constructor
     * @param stage
     * @param root
     * @param numberOfRow
     * @param parts
     * @param lastRowNum
     */
    public Part6_7Form(Stage stage, Pane root, int numberOfRow, List<Part> parts, int lastRowNum,
                       HistoryRecordFile historyRecordFile, ResultManagement resultManagement,
                       ReviewManagement reviewManagement) {
        super(numberOfRow, parts, lastRowNum, stage, root, historyRecordFile, resultManagement, reviewManagement);

        sourceHeadMap = new TreeMap<>();
        sourceTailMap = new TreeMap<>();
        for(Part part : parts)
        {
            sourceHeadMap.putIfAbsent(part.getSource(), Integer.valueOf(part.getPartListNumber()));
            sourceTailMap.put(part.getSource(), Integer.valueOf(part.getPartListNumber()));

            reviewManagement.updateResultRecord(part.getItemNum(), part.getItemNum(), part.getPart(), part.getReview(),true);
        }

        popupMenu = new ContextMenu();
        part = parts.get(0).getPart();

        questionImgView1 = new ImageView();
        questionImgView1.setPreserveRatio(true);
        questionImgView1.setFitHeight(180);
        questionImgView1.setFitWidth(400);
        questionImgView2 = new ImageView();
        questionImgView2.setPreserveRatio(true);
        questionImgView2.setFitHeight(180);
        questionImgView2.setFitWidth(400);
        questionImgView3 = new ImageView();
        questionImgView3.setPreserveRatio(true);
        questionImgView3.setFitHeight(180);
        questionImgView3.setFitWidth(400);
        questionImgView4 = new ImageView();
        questionImgView4.setPreserveRatio(true);
        questionImgView4.setFitHeight(180);
        questionImgView4.setFitWidth(400);
        questionImgView5 = new ImageView();
        questionImgView5.setPreserveRatio(true);
        questionImgView5.setFitHeight(180);
        questionImgView5.setFitWidth(400);

        question1 = new Label();
        question2 = new Label();
        question3 = new Label();
        question4 = new Label();
        question5 = new Label();
    }

    /**
     * Getters and Setters
     * @return
     */
    public BorderPane getLayout() {
        return border;
    }

    public TimerLayout getTimerLayout() { return timerLayout; }

    public boolean isSkipRenewQuestions() {
        return skipRenewQuestions;
    }

    public void setSkipRenewQuestions(boolean skipRenewQuestions) {
        this.skipRenewQuestions = skipRenewQuestions;
    }


    public boolean isTail() { return isTail; }

    public void setTail(boolean tail) { isTail = tail; }

    public boolean isPart7showAnswer() { return isPart7showAnswer; }

    public void setPart7showAnswer(boolean part7showAnswer) { isPart7showAnswer = part7showAnswer; }

    public void setProperty(Properties property) { this.property = property; }

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
            ProcessToGoBackToPrevious();
        });

        bookMark.setOnMousePressed(e -> {
            historyRecordFile.updateHistoryRecord(parts.get(numberOfRow).getItemNum(),
                    numberOfRow, parts.get(numberOfRow).getPart());

            if (checkBookMark(numberOfRow))
                startSound("resources/Click.mp3");
        });

        nextButton.setOnAction(e -> {
            ProcessToGoToNextPage();
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

        answer4Button.setOnAction( e-> {
            showExplanation(numberOfRow +3);
        });

        answer5Button.setOnAction( e-> {
            showExplanation(numberOfRow +4);
        });


       sourceComboBox.setOnAction(e -> {
           if (!skipRenewQuestions)
               renewQuestionsMarkedRecord();
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

        resizeButton.setOnAction(e -> {
            if(isExpanded)
                  resizeButton.setGraphic(expand);
            else resizeButton.setGraphic(shrink);

            toggleSidebar();
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
        question4.setOnContextMenuRequested((ContextMenuEvent event) -> {
            popupMenu.hide();
            popupMenu = createPopupMenu(question4, "question4");
            popupMenu.show(question4, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        //マウスクリックの場合、ポップアップメニューを不可視にする
        question4.setOnMouseClicked((MouseEvent event) -> { popupMenu.hide(); });

        //ポップアップメニューがリクエストされた場合の処理
        question5.setOnContextMenuRequested((ContextMenuEvent event) -> {
            popupMenu.hide();
            popupMenu = createPopupMenu(question5, "question5");
            popupMenu.show(question5, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        //マウスクリックの場合、ポップアップメニューを不可視にする
        question5.setOnMouseClicked((MouseEvent event) -> { popupMenu.hide(); });

        toggleShowResult.setOnAction(e -> {
            if(toggleShowResult.isSelected())
                this.setPart7showAnswer(true);
            else
                this.setPart7showAnswer(false);

            restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2,
                    numberOfRow+3, numberOfRow+4);
        });

    }

    private void buildUpPartListView() {
        for(Map.Entry mapEntry : sourceHeadMap.entrySet()) {
            sourceList.add(mapEntry.getKey().toString());
        }
    }

    @Override
    protected void changeImageOrTextField(int row) {
        String questionNum = parts.get(row).getQuestionNum();

        // single question
        if(parts.get(row).getQuestAttrib().matches("F")) {
            questionImgView1.setImage(createImage(parts.get(row).getFilePath()+
                    parts.get(row).getQuestion()));
            changeQestionOpacity(0.0, 1.0, question1, questionImgView1);
        } else {
            question1.setText(this.questionText1);
            changeQestionOpacity(1.0, 0.0, question1, questionImgView1);
        }

        // 2 - 5 questions
        if(Integer.valueOf(questionNum) > 1) {
            if (parts.get(row + 1).getQuestAttrib().matches("F")) {
                questionImgView2.setImage(createImage(parts.get(row + 1).getFilePath() +
                        parts.get(row + 1).getQuestion()));
                changeQestionOpacity(0.0, 1.0, question2, questionImgView2);
            } else {
                question2.setText(this.questionText2);
                changeQestionOpacity(1.0, 0.0, question2, questionImgView2);
            }
        }

        // 3 - 5 questions
        if(Integer.valueOf(questionNum) > 2) {
            if (parts.get(row + 2).getQuestAttrib().matches("F")) {
                questionImgView3.setImage(createImage(parts.get(row + 2).getFilePath() +
                        parts.get(row + 2).getQuestion()));
                changeQestionOpacity(0.0, 1.0, question3, questionImgView3);
            } else {
                question3.setText(this.questionText3);
                changeQestionOpacity(1.0, 0.0, question3, questionImgView3);
            }
        }

        // 4 - 5 questions
        if(Integer.valueOf(questionNum) > 3) {
            if (parts.get(row + 3).getQuestAttrib().matches("F")) {
                questionImgView4.setImage(createImage(parts.get(row + 3).getFilePath() +
                        parts.get(row + 3).getQuestion()));
                changeQestionOpacity(0.0, 1.0, question4, questionImgView4);
            } else {
                question4.setText(this.questionText4);
                changeQestionOpacity(1.0, 0.0, question4, questionImgView4);
            }
        }

        // 5 questions
        if(Integer.valueOf(questionNum) > 4) {
            if (parts.get(row + 4).getQuestAttrib().matches("F")) {
                questionImgView5.setImage(createImage(parts.get(row + 4).getFilePath() +
                        parts.get(row + 4).getQuestion()));
                changeQestionOpacity(0.0, 1.0, question5, questionImgView5);
            } else {
                question5.setText(this.questionText5);
                changeQestionOpacity(1.0, 0.0, question5, questionImgView5);
            }
        }
    }

    private void changeRadioButtonProperties(Toggle oldVal, Toggle newVal, String answer, int numberOfRow) {
        String result;
        if(newVal != null) {
            // Cast object to radio button
            RadioButton chk = (RadioButton) newVal.getToggleGroup().getSelectedToggle();

            if (chk.getText().matches(answer)) {
                errataPieChart.setRecord(parts.get(numberOfRow).getItemNum(), true, chk.getText(),
                        parts.get(numberOfRow).getPart(), parts.get(numberOfRow).getSource());
                if(this.isPart7showAnswer)
                    chk.setStyle("-fx-background-color:GREEN; -fx-background-radius: 15px; -fx-background-width: 2px;");
                result = PASS;
            } else {
                errataPieChart.setRecord(parts.get(numberOfRow).getItemNum(), false, chk.getText(),
                        parts.get(numberOfRow).getPart(), parts.get(numberOfRow).getSource());
                if(this.isPart7showAnswer)
                    chk.setStyle("-fx-background-color:RED; -fx-background-radius: 15px; -fx-background-width: 2px;");
                result = FAIL;
            }
            resultManagement.updateResultRecord(parts.get(numberOfRow).getItemNum(),
                    numberOfRow, parts.get(numberOfRow).getPart(), result, chk.getText());
        }

        // Reset the background to transparent
        if( oldVal != null && oldVal instanceof RadioButton) { // Avoid to access to the null object
            // Cast object to radio button
            RadioButton oldChk = (RadioButton) oldVal;
            if(this.isPart7showAnswer)
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

    protected VBox createDocumentVBox(int row, String attrib, String fileName, String path, double screenHeight,
                                      double screenWidth) {
        singleTabImage = null;
        alertBox.setWindowHeight(820);
        alertBox.setWindowWidth(830);
        alertBox.setScreenHeight(screenHeight);
        alertBox.setScreenWidth(screenWidth);
        return alertBox.getImageTextContents(attrib, fileName, path, true, singleTabImage, tabPaneLayout);
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

        radioButton4A.setToggleGroup(group4);
        radioButton4B.setToggleGroup(group4);
        radioButton4C.setToggleGroup(group4);
        radioButton4D.setToggleGroup(group4);

        group4.selectedToggleProperty().addListener((ObservableValue<? extends Toggle>
                                                             observ, Toggle oldVal, Toggle newVal)->{
            changeRadioButtonProperties(oldVal, newVal, this.answer4, numberOfRow+3);
        });

        radioButton5A.setToggleGroup(group5);
        radioButton5B.setToggleGroup(group5);
        radioButton5C.setToggleGroup(group5);
        radioButton5D.setToggleGroup(group5);

        group5.selectedToggleProperty().addListener((ObservableValue<? extends Toggle>
                                                             observ, Toggle oldVal, Toggle newVal)->{
            changeRadioButtonProperties(oldVal, newVal, this.answer5, numberOfRow+4);
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

        group4 = new ToggleGroup();
        radioButton4A = new RadioButton("A");
        radioButton4B = new RadioButton("B");
        radioButton4C = new RadioButton("C");
        radioButton4D = new RadioButton("D");

        group5 = new ToggleGroup();
        radioButton5A = new RadioButton("A");
        radioButton5B = new RadioButton("B");
        radioButton5C = new RadioButton("C");
        radioButton5D = new RadioButton("D");
    }

    @Override
    protected void createToggleButtons() {
        answer1Button = new ToggleButton();
        answer2Button = new ToggleButton();
        answer3Button = new ToggleButton();
        answer4Button = new ToggleButton();
        answer5Button = new ToggleButton();
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

        toggleShowResult = new JFXToggleButton();
        toggleShowResult.setText("Show results");
        toggleShowResult.setStyle("-jfx-toggle-color: #DAA520;");
        //Set the saving file option as default
        toggleShowResult.setSelected(false);

        resizeButton = new ToggleButton();
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

        ImageView view4;
        view4 = createImageView("resources/LIGHTBULBO_64.png");
        view4.setFitWidth(25);
        view4.setFitHeight(25);

        //Setting a graphic to the button
        answer4Button.setGraphic(view4);

        ImageView view5;
        view5 = createImageView("resources/LIGHTBULBO_64.png");
        view5.setFitWidth(25);
        view5.setFitHeight(25);

        //Setting a graphic to the button
        answer5Button.setGraphic(view5);

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

        expand = createImageView("resources/EXPAND2_512.png");
        expand.setFitWidth(40);
        expand.setFitHeight(25);
        shrink = createImageView("resources/SHRINK2_512.png");
        shrink.setFitWidth(40);
        shrink.setFitHeight(25);
        resizeButton.setGraphic(expand);
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
                previousSourceId = parts.get(numberOfRow).getSource();
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

        result4 = new ImageView();
        result4.setFitHeight(20);
        result4.setFitWidth(20);
        final Image image4 = createImage("resources/CROSS_512.png");
        result4.setImage(image4);
        result4.setOpacity(0);
        resultDate4 = new Label();

        result5 = new ImageView();
        result5.setFitHeight(20);
        result5.setFitWidth(20);
        final Image image5 = createImage("resources/CROSS_512.png");
        result5.setImage(image5);
        result5.setOpacity(0);
        resultDate5 = new Label();

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

        review4 = new ImageView();
        review4.setFitHeight(40);
        review4.setFitWidth(40);
        Image image8 = createImage("resources/PIN2_512.png");
        review4.setImage(image8);
        review4.setOpacity(0.0);

        review5 = new ImageView();
        review5.setFitHeight(40);
        review5.setFitWidth(40);
        Image image9 = createImage("resources/PIN2_512.png");
        review5.setImage(image9);
        review5.setOpacity(0.0);

        setupQuestions(this.numberOfRow);
        rebuildTextsAndButtons(numberOfRow);
        restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2,
                numberOfRow+3, numberOfRow+4);

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
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().addAll(hBox);
        return vBox;
    }

    protected HBox createBottomLayout() {
        HBox hBox5 = new HBox();
        hBox5.getChildren().addAll(backButton, nextButton, listening1Button, homeButton, toggleShowResult);
        hBox5.setSpacing(5);
        hBox5.setAlignment(Pos.TOP_LEFT);
        hBox5.setPadding(new Insets(10, 10, 10, 10));
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
        hBox4.getChildren().addAll(radioButton4A, radioButton4B, radioButton4C, radioButton4D,answer4Button,
                                   result4, resultDate4, review4);
        hBox4.setSpacing(20);
        hBox4.setAlignment(Pos.TOP_LEFT);
        hBox4.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox5 = new HBox();
        hBox5.getChildren().addAll(radioButton5A, radioButton5B, radioButton5C, radioButton5D ,answer5Button,
                                   result5, resultDate5, review5);
        hBox5.setSpacing(20);
        hBox5.setAlignment(Pos.TOP_LEFT);
        hBox5.setPadding(new Insets(10, 10, 10, 10));

        questionPane1 = new StackPane();
        questionPane2 = new StackPane();
        questionPane3 = new StackPane();
        questionPane4 = new StackPane();
        questionPane5 = new StackPane();

        questionPane1.getChildren().addAll(question1, questionImgView1);
        questionPane2.getChildren().addAll(question2, questionImgView2);
        questionPane3.getChildren().addAll(question3, questionImgView3);
        questionPane4.getChildren().addAll(question4, questionImgView4);
        questionPane5.getChildren().addAll(question5, questionImgView5);

        questionPane1.setAlignment(Pos.TOP_LEFT);
        questionPane2.setAlignment(Pos.TOP_LEFT);
        questionPane3.setAlignment(Pos.TOP_LEFT);
        questionPane4.setAlignment(Pos.TOP_LEFT);
        questionPane5.setAlignment(Pos.TOP_LEFT);

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().addAll(questionPane1, hBox1, questionPane2, hBox2, questionPane3, hBox3, questionPane4, hBox4, questionPane5, hBox5);

        vBox.setBackground(bg);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setBackground(bg);
        scrollPane.setPannable(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(vBox);

        return scrollPane;
    }

    private void createScriptTabPane(int numberOfRow) {
        singleTab.setText(TAB_0);
        singleTab.setContent(createDocumentVBox(numberOfRow, parts.get(numberOfRow).getScriAttrib(),
                parts.get(numberOfRow).getScript(), parts.get(numberOfRow).getFilePath(), 820, 830));
        tabPaneLayout.getTabs().add(singleTab);

        doubleTab.setText(TAB_01);
        if(!parts.get(numberOfRow).getDialogAttrib().isEmpty()) {
            doubleTab.setContent(createDocumentVBox(numberOfRow, parts.get(numberOfRow).getDialogAttrib(),
                    parts.get(numberOfRow).getDialog(), parts.get(numberOfRow).getFilePath(), 820, 830));
        } else {
            doubleTab.setContent(new Label(TAB_01));
        }
        tabPaneLayout.getTabs().add(doubleTab);

        tripleTab.setText(TAB_02);
        if(!parts.get(numberOfRow).getTriplePassAttrib().isEmpty()) {
            tripleTab.setContent(createDocumentVBox(numberOfRow, parts.get(numberOfRow).getTriplePassAttrib(),
                    parts.get(numberOfRow).getTriplePassage(), parts.get(numberOfRow).getFilePath(), 820, 830));
        } else {
            tripleTab.setContent(new Label(TAB_02));
        }
        tabPaneLayout.getTabs().add(tripleTab);
        tabPaneLayout.getStylesheets().add("jfoenix-components.css");

        timerLayout.createTimerLayout();
        timerLayout.setItemNum(parts.get(numberOfRow).getItemNum());
        this.setUpTimeLimit(numberOfRow);

        // Only update the tab-contents for the different sourceID
        if (!parts.get(numberOfRow).getSource().equals(previousSourceId)) {
            timerLayout.processUpdateTimerRecord();
            timerLayout.performResetButtonAction();
        }

        timerLayout.updateTimeLimit();

        previousSourceId = parts.get(numberOfRow).getSource();
        menuButton = new MenuButton(MENU_HEADER);
        splitMenuButton = menuButton.createSplitMenuButton("resources/PIECHART_32.png",
                errataPieChart);

        Label filler = new Label();
        HBox hBox0 = new HBox();
        hBox0.setSpacing(15);
        hBox0.getChildren().addAll(resizeButton, examinationType, filler, timerLayout.getTimerLayout(), splitMenuButton);
        hBox0.setAlignment(Pos.TOP_RIGHT);

        rightLayout.setTop(hBox0);
        rightLayout.setCenter(tabPaneLayout);
    }

    private void enableDisableRadioAnswerButtons() {
        String questionNum = parts.get(numberOfRow).getQuestionNum();

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

            radioButton4A.setDisable(true);
            radioButton4B.setDisable(true);
            radioButton4C.setDisable(true);
            radioButton4D.setDisable(true);
            answer4Button.setDisable(true);

            radioButton5A.setDisable(true);
            radioButton5B.setDisable(true);
            radioButton5C.setDisable(true);
            radioButton5D.setDisable(true);
            answer5Button.setDisable(true);
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

            radioButton4A.setDisable(true);
            radioButton4B.setDisable(true);
            radioButton4C.setDisable(true);
            radioButton4D.setDisable(true);
            answer4Button.setDisable(true);

            radioButton5A.setDisable(true);
            radioButton5B.setDisable(true);
            radioButton5C.setDisable(true);
            radioButton5D.setDisable(true);
            answer5Button.setDisable(true);
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

            radioButton4A.setDisable(true);
            radioButton4B.setDisable(true);
            radioButton4C.setDisable(true);
            radioButton4D.setDisable(true);
            answer4Button.setDisable(true);

            radioButton5A.setDisable(true);
            radioButton5B.setDisable(true);
            radioButton5C.setDisable(true);
            radioButton5D.setDisable(true);
            answer5Button.setDisable(true);
        } else if (questionNum.matches("4")) {
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

            radioButton4A.setDisable(false);
            radioButton4B.setDisable(false);
            radioButton4C.setDisable(false);
            radioButton4D.setDisable(false);
            answer4Button.setDisable(false);

            radioButton5A.setDisable(true);
            radioButton5B.setDisable(true);
            radioButton5C.setDisable(true);
            radioButton5D.setDisable(true);
            answer5Button.setDisable(true);
        }  else if (questionNum.matches("5")) {
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

            radioButton4A.setDisable(false);
            radioButton4B.setDisable(false);
            radioButton4C.setDisable(false);
            radioButton4D.setDisable(false);
            answer4Button.setDisable(false);

            radioButton5A.setDisable(false);
            radioButton5B.setDisable(false);
            radioButton5C.setDisable(false);
            radioButton5D.setDisable(false);
            answer5Button.setDisable(false);
        }
    }

    private void goToNextPage() {
        if(numberOfRow < lastRowNum - 4) {
            // Increase rowNum to move to the next question set
            numberOfRow = numberOfRow + Integer.valueOf(parts.get(numberOfRow).getQuestionNum());

            if (!sourceComboBox.getValue().equals(parts.get(numberOfRow).getSource())) {
                previousSourceId = sourceComboBox.getValue().toString();
                sourceComboBox.setValue((parts.get(numberOfRow).getSource()));
            }

            createScriptTabPane(numberOfRow);
            setupQuestions(numberOfRow);

            renewSourceCombo();
            rebuildTextsAndButtons(numberOfRow);
            restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2,
                    numberOfRow+3, numberOfRow+4);
            checkBookMark(numberOfRow);
            startSound("resources/bookPageFlip.mp3");
        }
    }

    private void goBackToPrevious() {
        // if not 1st Row item
        if(numberOfRow > 0) {
            int questionNumber = Integer.valueOf(parts.get(numberOfRow-1).getQuestionNum());
            // Decrease rowNum to move to the previous question set
            if (numberOfRow - questionNumber > 0) {
                numberOfRow = numberOfRow - questionNumber;
            } else {
                numberOfRow = 0;
            }

            if (!sourceComboBox.getValue().equals(parts.get(numberOfRow).getSource())) {
                previousSourceId = sourceComboBox.getValue().toString();
                setSkipRenewQuestions(true);
                sourceComboBox.setValue(parts.get(numberOfRow).getSource());
                setSkipRenewQuestions(false);
                timerLayout.processStopTimer(false);
            }

            createScriptTabPane(numberOfRow);
            setupQuestions(numberOfRow);

            renewSourceCombo();
            rebuildTextsAndButtons(numberOfRow);

            restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2,
                    numberOfRow + 3, numberOfRow + 4);
            checkBookMark(numberOfRow);
            startSound("resources/bookPageFlip.mp3");
        }
    }

    private void ProcessToGoToNextPage() {
        if(isNoWarningDialog) {
            warningDialog = new CustomDialog("WARNING", ARE_YOU_SURE_TO_CHANGE_THE_PAGE, "resources/WARNING_512.png");
            isNoWarningDialog = false;
        }
        if(numberOfRow < lastRowNum - 4) {
            // Increase rowNum to move to the next question set
            int curNumberOfRow = numberOfRow + Integer.valueOf(parts.get(numberOfRow).getQuestionNum());
            if (!sourceComboBox.getValue().equals(parts.get(curNumberOfRow).getSource())) {
                Optional<ButtonType> result = warningDialog.showAndAnimate();
                if (result.isPresent() && result.get() == CustomDialog.YES_BUTTON)
                    goToNextPage();
            } else {
                goToNextPage();
            }
        }
    }

    private void ProcessToGoBackToPrevious() {
        if(isNoWarningDialog) {
            warningDialog = new CustomDialog("WARNING", ARE_YOU_SURE_TO_CHANGE_THE_PAGE, "resources/WARNING_512.png");
            isNoWarningDialog = false;
        }
        if(numberOfRow > 0) {
            int questionNumber = Integer.valueOf(parts.get(numberOfRow-1).getQuestionNum());
            // Decrease rowNum to move to the previous question set
            int curNumberOfRow;
            if (numberOfRow - questionNumber > 0) {
                curNumberOfRow = numberOfRow - questionNumber;
            } else {
                curNumberOfRow = 0;
            }
            if (!sourceComboBox.getValue().equals(parts.get(curNumberOfRow).getSource())) {
                Optional<ButtonType> result = warningDialog.showAndAnimate();
                if (result.isPresent() && result.get() == CustomDialog.YES_BUTTON)
                    goBackToPrevious();
            } else {
                goBackToPrevious();
            }
        }
    }

    /**
     * Override the parent implemented method.
     * @return
     */
    protected Scene rebuildForm(){
        createToggleButtons();
        createRadioButtons();
        enableDisableRadioAnswerButtons();
        createImageViews();
        createGroupControls();

    //    setupQuestions(this.numberOfRow);
        border = new BorderPane();

        rightLayout = new BorderPane();
        tabPaneLayout = new JFXTabPane();
        singleTab = new Tab();
        doubleTab = new Tab();
        tripleTab = new Tab();

        examinationType = new Label();

        // Get the PART from the 1st record
        String part = this.parts.get(0).getPart();

        File file = null;
        if (part.matches("6"))
            file = new File("resources/A4paper.jpg");
        else
            file = new File("resources/A2paper.jpg");

        Image image = new Image(file.toURI().toString());

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
        border.setTop(topLeftLayout);
        border.setCenter(createCenterLayout());
        border.setBottom(createBottomLayout());

        assignActionEvent();

        setupDialogDocumentButtons(numberOfRow);
        setupLabelText(numberOfRow);

        timerLayout = new TimerLayout(Duration.seconds(0.0), true, parts.get(numberOfRow).getPart());

        createScriptTabPane(numberOfRow);

        //setUpTimeLimit(numberOfRow);

        //Creating a SplitPane
        splitPane = new SplitPane();
        splitPane.setDividerPositions(sliderWidth / (sliderWidth + 800)); // Initial divider position
        //Creating stack panes holding the ImageView objects
        StackPane stackPane1 = new StackPane(border);
        StackPane stackPane2 = new StackPane(rightLayout);
        stackPane2.setBackground(bg);

        //Adding the stackpanes to the splitpane
        splitPane.getItems().addAll(stackPane1, stackPane2);
        splitPane.setBackground(bg);

        //Setting anchor pane as the layout
        AnchorPane pane = new AnchorPane();
        AnchorPane.setTopAnchor(splitPane, 15.0);
        AnchorPane.setRightAnchor(splitPane, 5.0);
        AnchorPane.setBottomAnchor(splitPane, 15.0);
        AnchorPane.setLeftAnchor(splitPane, 5.0);
        pane.getChildren().addAll(splitPane);
        pane.setBackground(bg);

        scene = new Scene(pane, PART7_WIDTH, PART7_HEIGHT);
        scene.getStylesheets().add("Viper.css");

        // Assign the Swipe Left action
        scene.setOnSwipeLeft(e-> {
            ProcessToGoBackToPrevious();
        });

        // Assign the Swipe Right action
        scene.setOnSwipeRight(e-> {
            ProcessToGoToNextPage();
        });

//        ChangeListener<Number> changeListener = new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                splitPane.setDividerPositions(0.42);
//                if (m_stageShowing) {
//                    observable.removeListener(this);
//                }
//            }
//        };
//        splitPane.widthProperty().addListener(changeListener);
//        splitPane.heightProperty().addListener(changeListener);

        m_stageShowing = true;
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

        resetRadiobuttonBackground(radioButton4A);
        resetRadiobuttonBackground(radioButton4B);
        resetRadiobuttonBackground(radioButton4C);
        resetRadiobuttonBackground(radioButton4D);

        resetRadiobuttonBackground(radioButton5A);
        resetRadiobuttonBackground(radioButton5B);
        resetRadiobuttonBackground(radioButton5C);
        resetRadiobuttonBackground(radioButton5D);

        enableDisableRadioAnswerButtons();

        setupDialogDocumentButtons(row);
        setupTextbookButton(row);

        setupLabelText(row);
    }

    private void renewSourceCombo() {
        String source = sourceComboBox.getValue().toString();
        String record = parts.get(numberOfRow).getSource();

        if (!source.matches(record))
            sourceComboBox.setValue(record);
    }

    private void renewQuestionsMarkedRecord() {
        int itemNumber = -1;

        if(sourceHeadMap.get(sourceComboBox.getValue()) < lastRowNum) {
            itemNumber = sourceHeadMap.get(sourceComboBox.getValue());

            // Get the sorted itemNumber based on the head or tail
            if(isTail()) {
                if (sourceTailMap.containsKey(sourceComboBox.getValue()))
                    itemNumber = sourceTailMap.get(sourceComboBox.getValue());
            } else {
                if (sourceHeadMap.containsKey(sourceComboBox.getValue()))
                    itemNumber = sourceHeadMap.get(sourceComboBox.getValue());
            }
        }

        if (itemNumber != -1) {
            for (int i = 0; i < parts.size(); i++) {
                if (parts.get(i).getPartListNumber().equals(String.valueOf(itemNumber))) {
                    this.numberOfRow = i;
                }
            }

            if (isTail()) {
                // if not 1st Row item
                if(numberOfRow > 0) {
                    int previousQuestionNum = Integer.valueOf(parts.get(numberOfRow-1).getQuestionNum());
                    // Decrease rowNum to move to the previous question set
                    if (numberOfRow - previousQuestionNum > 0) {
                        numberOfRow = numberOfRow - previousQuestionNum;
                    } else {
                        numberOfRow = 0;
                    }
                }
            }
      //      setUpTimeLimit(numberOfRow);
            createScriptTabPane(numberOfRow);
            setupQuestions(numberOfRow);
            rebuildTextsAndButtons(numberOfRow);
            restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2, numberOfRow+3, numberOfRow+4);
            checkBookMark(numberOfRow);
        }
    }

    @Override
    protected void rebuildRadiobuttonBackground(RadioButton radioButton, boolean reassign) {

        if(this.isPart7showAnswer()) {
            if (reassign) {
                radioButton.setStyle("-fx-background-color:GREEN; -fx-background-radius: 15px; -fx-background-width: 2px;");
            } else {
                radioButton.setStyle("-fx-background-color:RED; -fx-background-radius: 15px; -fx-background-width: 2px;");
            }
        } else {
            radioButton.setStyle("-fx-background-color:null; -fx-background-radius: 15px; -fx-background-width: 2px;");
        }
        radioButton.setSelected(true);
    }

    private void restoreRadioButtons(int firstRow, int secondRow, int thirdRow, int fourthRow, int fifthRow) {
        HashMap<String, ErrorRecord> record;

        record = errataPieChart.getRecord();

        //If there is any error record on HashMap
        if (!record.isEmpty()) {
            boolean result1 = false;
            boolean result2 = false;
            boolean result3 = false;
            boolean result4 = false;
            boolean result5 = false;
            String answer1 = null;
            String answer2 = null;
            String answer3 = null;
            String answer4 = null;
            String answer5 = null;

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

            if (record.containsKey(parts.get(fourthRow).getItemNum())) {
                answer4 = record.get(parts.get(fourthRow).getItemNum()).getAnswer();
                result4 = record.get(parts.get(fourthRow).getItemNum()).isResult();
            }

            if (record.containsKey(parts.get(fifthRow).getItemNum())) {
                answer5 = record.get(parts.get(fifthRow).getItemNum()).getAnswer();
                result5 = record.get(parts.get(fifthRow).getItemNum()).isResult();
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

            if (answer4 != null) {
                if (answer4.matches("A")) {
                    rebuildRadiobuttonBackground(radioButton4A, result4);
                } else if (answer4.matches("B")) {
                    rebuildRadiobuttonBackground(radioButton4B, result4);
                } else if (answer4.matches("C")) {
                    rebuildRadiobuttonBackground(radioButton4C, result4);
                } else if (answer4.matches("D")) {
                    rebuildRadiobuttonBackground(radioButton4D, result4);
                }
            }

            if (answer5 != null) {
                if (answer5.matches("A")) {
                    rebuildRadiobuttonBackground(radioButton5A, result5);
                } else if (answer5.matches("B")) {
                    rebuildRadiobuttonBackground(radioButton5B, result5);
                } else if (answer5.matches("C")) {
                    rebuildRadiobuttonBackground(radioButton5C, result5);
                } else if (answer5.matches("D")) {
                    rebuildRadiobuttonBackground(radioButton5D, result5);
                }
            }

            // call to enable/disable dialogButton
            setupDialogDocumentButtons(firstRow);

            // Restore the previous question texts
            question1.setText(questionText1);
            question2.setText(questionText2);
            question3.setText(questionText3);
            question4.setText(questionText4);
            question5.setText(questionText5);

            setupLabelText(firstRow);

        }
    }

    private void setupDialogDocumentButtons(int row) {
        if(parts.get(row).getAudioFile().isEmpty())
            listening1Button.setDisable(true);
        else
            listening1Button.setDisable(false);
    }

    private void setupLabelText(int row) {
        BackgroundFill bf;
        Background bk;

        examinationType.setPadding(new Insets(5));
        examinationType.setFont(new Font("Arial", 18));
        examinationType.setTextFill(Color.WHITE);

        switch (parts.get(row).getCategory().trim()) {
            case "single":
                bf = new BackgroundFill(Color.PURPLE,
                        new CornerRadii(5) , Insets.EMPTY);
                bk = new Background(bf);
                examinationType.setBackground(bk);
                examinationType.setText(parts.get(row).getCategory().toString());
                break;
            case "double":
                bf = new BackgroundFill(Color.rgb(50,205,50),
                        new CornerRadii(5) , Insets.EMPTY);
                bk = new Background(bf);
                examinationType.setBackground(bk);
                examinationType.setText(parts.get(row).getCategory().toString());
                break;
            case "triple":
                bf = new BackgroundFill(Color.rgb(0,206,209),
                        new CornerRadii(5) , Insets.EMPTY);
                bk = new Background(bf);
                examinationType.setBackground(bk);
                examinationType.setText(parts.get(row).getCategory().toString());
                break;
            default:
                bf = new BackgroundFill(null, null, Insets.EMPTY);
                bk = new Background(bf);
                examinationType.setBackground(bk);
                examinationType.setText("");
        }
    }

    @Override
    protected void setupQuestions(int row) {
        String result = parts.get(row).getResult();
        String resultDate = parts.get(row).getResultDate();
        String questionNum = parts.get(row).getQuestionNum();

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
            this.setQuestionText2("");
            this.question2.setOpacity(0);
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
                this.setQuestionText3("");
                this.question3.setOpacity(0);
                this.setAnswer3("");

                this.questionImgView3.setImage(null);
                this.questionImgView3.setOpacity(0);

                result3.setOpacity(0);
                resultDate3.setText("");
                review3.setOpacity(0);
                break;
            case "3": case "4": case "5":
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
        switch (questionNum) {
            case "1": case "2": case "3":
                this.setQuestionText4("");
                this.question4.setOpacity(0);
                this.setAnswer4("");
                this.questionImgView4.setImage(null);
                this.questionImgView4.setOpacity(0);

                result4.setOpacity(0);
                resultDate4.setText("");
                review4.setOpacity(0);
                break;
            case "4": case "5":
                result = parts.get(row + 3).getResult();
                resultDate = parts.get(row + 3).getResultDate();
                switchResultImage(result, resultDate, result4, resultDate4, review4);

                review = reviewManagement.returnReviewRecord(parts.get(row + 3).getItemNum());
                if(review.matches("Mark")) {
                    review4.setOpacity(1.0);
                } else {
                    review4.setOpacity(0);
                }

                this.setQuestionText4(parts.get(row + 3).getQuestion());
                this.setAnswer4(parts.get(row + 3).getAnswer());
                break;
        }

        // Set the questions for 5 items
        if (questionNum.matches("5")) {
            result = parts.get(row + 4).getResult();
            resultDate = parts.get(row + 4).getResultDate();
            switchResultImage(result, resultDate, result5, resultDate5, review5);

            review = reviewManagement.returnReviewRecord(parts.get(row + 4).getItemNum());
            if(review.matches("Mark")) {
                review5.setOpacity(1.0);
            } else {
                review5.setOpacity(0);
            }

            this.setQuestionText5(parts.get(row + 4).getQuestion());
            this.setAnswer5(parts.get(row + 4).getAnswer());
        } else {
            this.setQuestionText5("");
            this.question5.setOpacity(0);
            this.setAnswer5("");
            this.questionImgView5.setImage(null);
            this.questionImgView5.setOpacity(0);

            result5.setOpacity(0);
            resultDate5.setText("");
            review5.setOpacity(0);
        }

        // Create & assign the audio file name.
        String autoFileName = parts.get(row).getAudioFile();
        if (!autoFileName.isEmpty()) {
            String fileFullPath = parts.get(row).getFilePath()+autoFileName;
            if(!fileFullPath.isEmpty() || !fileFullPath.isBlank()) {
                File f = new File(fileFullPath);
                try {
                    this.setMediaPlayer(new Media(f.toURI().toString()));
                } catch (MediaException e) {
                    System.out.println("fileFullPath = " + fileFullPath);
                    e.printStackTrace();
                }
            }
        }
    }

    private void setUpTimeLimit(int row) {
        if (parts.get(row).getFullTime().matches("f1")) {
            if (part.matches("6"))
                timerLayout.setTimelimit(PART6_TIME);
            else { //PART 7
                double part7Full1 = (double) Integer.parseInt(property.getProperty("part7time1"));
                if (!parts.get(row).getSource().matches(sourceComboBox.getValue().toString())) {
                    timerLayout.setTimelimit(PART7_TIME * part7Full1);
                } else {
                    if (timerLayout.getTimelimit() != part7Full1)
                        timerLayout.setTimelimit(PART7_TIME * part7Full1);
                }
            }
        } else if (parts.get(row).getFullTime().matches("f2")) {
            if (part.matches("6"))
                timerLayout.setTimelimit(PART6_TIME);
            else { // PART 7
                double part7Full2 = (double) Integer.parseInt(property.getProperty("part7time2"));
                if (!parts.get(row).getSource().matches(sourceComboBox.getValue().toString())) {
                    timerLayout.setTimelimit(PART7_TIME * part7Full2);
                } else {
                    if (timerLayout.getTimelimit() != part7Full2)
                        timerLayout.setTimelimit(PART7_TIME * part7Full2);
                }
            }
        } else if (parts.get(row).getFullTime().matches("h1")) {
            if (part.matches("6"))
                timerLayout.setTimelimit(PART6_TIME);
            else { // PART 7
                double part7Full2 = (double) Integer.parseInt(property.getProperty("part7half1"));
                if (!parts.get(row).getSource().matches(sourceComboBox.getValue().toString())) {
                    timerLayout.setTimelimit(PART7_TIME * part7Full2);
                } else {
                    if (timerLayout.getTimelimit() != part7Full2)
                        timerLayout.setTimelimit(PART7_TIME * part7Full2);
                }
            }
        } else if (parts.get(row).getFullTime().matches("h2")) {
            if (part.matches("6"))
                timerLayout.setTimelimit(PART6_TIME);
            else { // PART 7
                double part7Full2 = (double) Integer.parseInt(property.getProperty("part7half2"));
                if (!parts.get(row).getSource().matches(sourceComboBox.getValue().toString())) {
                    timerLayout.setTimelimit(PART7_TIME * part7Full2);
                } else {
                    if (timerLayout.getTimelimit() != part7Full2)
                        timerLayout.setTimelimit(PART7_TIME * part7Full2);
                }
            }
        } else { // Not f1 or f2 specified
            switch (parts.get(row).getQuestionNum()) {
                case "1":
                    if (part.matches("6"))
                        timerLayout.setTimelimit(PART6_TIME);
                    else // PART 7
                        timerLayout.setTimelimit(PART7_TIME);
                    break;
                case "2":
                    if (part.matches("6"))
                        timerLayout.setTimelimit(PART6_TIME * 2);
                    else // PART 7
                        timerLayout.setTimelimit(PART7_TIME * 2);
                    break;
                case "3":
                    if (part.matches("6"))
                        timerLayout.setTimelimit(PART6_TIME * 3);
                    else // PART 7
                        timerLayout.setTimelimit(PART7_TIME * 3);
                    break;
                case "4":
                    if (part.matches("6"))
                        timerLayout.setTimelimit(PART6_TIME * 4);
                    else // PART 7
                        timerLayout.setTimelimit(PART7_TIME * 4);
                    break;
                case "5":
                    if (part.matches("6"))
                        timerLayout.setTimelimit(PART6_TIME * 5);
                    else // PART 7
                        timerLayout.setTimelimit(PART7_TIME * 5);
                    break;
            }
        }
    }

    private void toggleSidebar() {
        isExpanded= !isExpanded;
        double targetWidth = isExpanded ? 0.0 : sliderWidth;

        KeyValue keyValue = new KeyValue(splitPane.getDividers().get(0).positionProperty(), targetWidth / (targetWidth + 800));
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.8), keyValue);

        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
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

        } else if(name.matches("question4")) {
            if (selection.matches("Mark"))
                review4.setOpacity(1.0);
            else
                review4.setOpacity(0.0);

            itemNumber = parts.get(numberOfRow+3).getItemNum();

        } else if(name.matches("question5")) {
            if (selection.matches("Mark"))
                review5.setOpacity(1.0);
            else
                review5.setOpacity(0.0);

            itemNumber = parts.get(numberOfRow+4).getItemNum();
        }

        reviewManagement.updateResultRecord(itemNumber, groupItemNumber, parts.get(numberOfRow).getPart(), selection.toString(), false);
    }

}