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

public class Part2Form extends PartForm{
    public static final String MENU_HEADER = "TOEIC PART2";
    private static final int NUM_OF_PART2_QUESTIONS = 6;
    public static final String QUESTION = ". Mark your answer on your answer sheet.";
    private static final int PART2_HEIGHT = 680;
    private static final int PART2_WIDTH = 890;

    private ContextMenu popupMenu =null;

    private boolean isTail = false;

    private boolean mediaFile = false;
    private boolean media2File = false;
    private boolean media3File = false;

    private boolean media4File = false;
    private boolean media5File = false;
    private boolean media6File = false;
    private boolean skipRenewQuestions = false;

    private ComboBox sourceComboBox;

    private HBox hBox4;

    private ImageView bookMark;
    private ImageView review1;
    private ImageView review2;
    private ImageView review3;
    private ImageView review4;
    private ImageView review5;
    private ImageView review6;
    private ImageView result1;
    private ImageView result2;
    private ImageView result3;
    private ImageView result4;
    private ImageView result5;
    private ImageView result6;

    private Label resultDate1;
    private Label resultDate2;
    private Label resultDate3;
    private Label resultDate4;
    private Label resultDate5;
    private Label resultDate6;

    private Label question6;

    private LinkedList<String> part2List;

    private Media media;
    private Media media4;
    private Media media5;
    private Media media6;
    private MediaPlayer mediaPlayer;
    private MediaControl mediaControl;
    private MenuButton menuButton;
    private ObservableList<String> sourceList = FXCollections.observableArrayList();
    private RadioButton radioButton1A;
    private RadioButton radioButton1B;
    private RadioButton radioButton1C;
    private RadioButton radioButton2A;
    private RadioButton radioButton2B;
    private RadioButton radioButton2C;
    private RadioButton radioButton3A;
    private RadioButton radioButton3B;
    private RadioButton radioButton3C;
    private RadioButton radioButton4A;
    private RadioButton radioButton4B;
    private RadioButton radioButton4C;
    private RadioButton radioButton5A;
    private RadioButton radioButton5B;
    private RadioButton radioButton5C;
    private RadioButton radioButton6A;
    private RadioButton radioButton6B;
    private RadioButton radioButton6C;

    private Scene scene;
    private SplitMenuButton splitMenuButton;

    private String currentMediaFile = null;
    private String currentSource;

    private String answer6;
    private String questionText6;

    private ToggleButton answer6Button;
    private ToggleButton listening4Button;
    private ToggleButton listening5Button;
    private ToggleButton listening6Button;
    private ToggleButton script6Button;

    private ToggleGroup group6;

    /**
     * Constructor
     * In order to sort the part records by the source, implement a treeMap.
     * @param stage
     * @param root
     * @param numberOfRow
     * @param parts
     * @param lastRowNum
     * @param resultManagement
     */
    public Part2Form(Stage stage, Pane root, int numberOfRow, List<Part> parts, int lastRowNum,
                     HistoryRecordFile historyRecordFile, ResultManagement resultManagement,
                     ReviewManagement reviewManagement) {
        super(numberOfRow, parts, lastRowNum, stage, root, historyRecordFile, resultManagement, reviewManagement);
        sourceHeadMap = new TreeMap<>();
        sourceTailMap = new TreeMap<>();
        part2List = new LinkedList<>();

        currentMediaFile = parts.get(0).getAudioFile();
        for(Part part : parts)
        {
            sourceHeadMap.putIfAbsent(part.getSource(), Integer.valueOf(part.getPartListNumber()));
            sourceTailMap.put(part.getSource(), Integer.valueOf(part.getPartListNumber()));
            part2List.add(part.getSource());

            reviewManagement.updateResultRecord(part.getItemNum(), part.getItemNum(), part.getPart(), part.getReview(), true);
        }
        popupMenu = new ContextMenu();
    }

    public void setAnswer6(String answer6) { this.answer6 = answer6; }

    public String getCurrentSource() { return currentSource; }

    public void setCurrentSource(String currentSource) { this.currentSource = currentSource; }

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

    public boolean isMedia4File() { return media4File; }

    public void setMedia4File(boolean media4File) { this.media4File = media4File; }

    public boolean isMedia5File() { return media5File; }

    public void setMedia5File(boolean media5File) { this.media5File = media5File; }

    public boolean isMedia6File() { return media6File; }

    public void setMedia6File(boolean media6File) { this.media6File = media6File; }

    public Media getMedia4() { return media4; }

    public void setMedia4Player(Media media4) {
        this.media4 = media4;
    }

    public Media getMedia5() { return media5; }

    public void setMedia5Player(Media media5) {
        this.media5 = media5;
    }

    public Media getMedia6() { return media6; }

    public void setMedia6Player(Media media6) {
        this.media6 = media6;
    }

    public void setQuestionText6(String questionText6) { this.questionText6 = questionText6; }

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

        answer4Button.setOnAction( e-> {
            showExplanation(numberOfRow +3);
        });

        answer5Button.setOnAction( e-> {
            showExplanation(numberOfRow +4);
        });

        answer6Button.setOnAction( e-> {
            showExplanation(numberOfRow +5);
        });

        document1Button.setOnAction(e -> {
            this.showFolderContentsDialog(numberOfRow);
        });

        script1Button.setOnAction( e-> {
            showScript(numberOfRow, Modality.WINDOW_MODAL);
        });

        script2Button.setOnAction( e-> {
            showScript(numberOfRow+1, Modality.WINDOW_MODAL);
        });

        script3Button.setOnAction( e-> {
            showScript(numberOfRow+2, Modality.WINDOW_MODAL);
        });

        script4Button.setOnAction( e-> {
            showScript(numberOfRow+3, Modality.WINDOW_MODAL);
        });

        script5Button.setOnAction( e-> {
            showScript(numberOfRow+4, Modality.WINDOW_MODAL);
        });

        script6Button.setOnAction( e-> {
            showScript(numberOfRow+5, Modality.WINDOW_MODAL);
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

        listening4Button.setOnAction(e -> {
            mediaPlayer = new MediaPlayer(this.getMedia4());
            mediaPlayer.play();
            listening4Button.disableProperty().set(true);

            // Once the player is finished
            mediaPlayer.setOnEndOfMedia( () -> {
                // Enable the startButton for the media-play
                if (listening4Button.disableProperty().get()) {
                    listening4Button.disableProperty().set(false);
                }
            });
        });

        listening5Button.setOnAction(e -> {
            mediaPlayer = new MediaPlayer(this.getMedia5());
            mediaPlayer.play();
            listening5Button.disableProperty().set(true);

            // Once the player is finished
            mediaPlayer.setOnEndOfMedia( () -> {
                // Enable the startButton for the media-play
                if (listening5Button.disableProperty().get()) {
                    listening5Button.disableProperty().set(false);
                }
            });
        });

        listening6Button.setOnAction(e -> {
            mediaPlayer = new MediaPlayer(this.getMedia6());
            mediaPlayer.play();
            listening6Button.disableProperty().set(true);

            // Once the player is finished
            mediaPlayer.setOnEndOfMedia( () -> {
                // Enable the startButton for the media-play
                if (listening6Button.disableProperty().get()) {
                    listening6Button.disableProperty().set(false);
                }
            });
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

        //ポップアップメニューがリクエストされた場合の処理
        question6.setOnContextMenuRequested((ContextMenuEvent event) -> {
            popupMenu.hide();
            popupMenu = createPopupMenu(question6, "question6");
            popupMenu.show(question6, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        //マウスクリックの場合、ポップアップメニューを不可視にする
        question6.setOnMouseClicked((MouseEvent event) -> { popupMenu.hide(); });
    }

    private void renewQuestionsMarkedRecord() {
        int itemNumber;
        if(sourceHeadMap.get(sourceComboBox.getValue()) < lastRowNum) {

            // Get the sorted itemNumber based on the head or tail
            if(isTail()) {
                // Set the itemNumber as tail - 2
                itemNumber = (sourceTailMap.get(sourceComboBox.getValue()) - 2);
            } else {
                itemNumber = sourceHeadMap.get(sourceComboBox.getValue());
            }

            for(int i = 0; i < parts.size(); ++i) {
                if(parts.get(i).getPartListNumber().equals(String.valueOf(itemNumber))) {
                    numberOfRow = i;
                }
            }

            setupQuestions(numberOfRow);
            rebuildTextsAndButtons(numberOfRow);
            restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2,
                    numberOfRow+4, numberOfRow+5, numberOfRow+6);
            checkBookMark(numberOfRow);
        }
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
        if( oldVal != null) { // Avoid to access to the null object
            // Cast object to radio button
            RadioButton oldChk = (RadioButton) oldVal;
            oldChk.setStyle("-fx-background-color:null; -fx-background-radius: 15px; -fx-background-width: 2px;");
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
    protected void createGroupControls() {
        question1 = new Label();
        question1.setText(this.questionText1);
        radioButton1A.setToggleGroup(group1);
        radioButton1B.setToggleGroup(group1);
        radioButton1C.setToggleGroup(group1);

        group1.selectedToggleProperty().addListener((ObservableValue<? extends Toggle>
                                                             observ, Toggle oldVal, Toggle newVal)->{
            changeRadioButtonProperties(oldVal, newVal, this.answer1, numberOfRow);
        });

        question2 = new Label();
        question2.setText(this.questionText2);
        radioButton2A.setToggleGroup(group2);
        radioButton2B.setToggleGroup(group2);
        radioButton2C.setToggleGroup(group2);

        group2.selectedToggleProperty().addListener((ObservableValue<? extends Toggle>
                                                             observ, Toggle oldVal, Toggle newVal)->{
            changeRadioButtonProperties(oldVal, newVal, this.answer2, numberOfRow+1);
        });

        question3 = new Label();
        question3.setText(this.questionText3);
        radioButton3A.setToggleGroup(group3);
        radioButton3B.setToggleGroup(group3);
        radioButton3C.setToggleGroup(group3);

        group3.selectedToggleProperty().addListener((ObservableValue<? extends Toggle>
                                                             observ, Toggle oldVal, Toggle newVal)->{
            changeRadioButtonProperties(oldVal, newVal, this.answer3, numberOfRow+2);
        });

        question4 = new Label();
        question4.setText(this.questionText4);
        radioButton4A.setToggleGroup(group4);
        radioButton4B.setToggleGroup(group4);
        radioButton4C.setToggleGroup(group4);

        group4.selectedToggleProperty().addListener((ObservableValue<? extends Toggle>
                                                             observ, Toggle oldVal, Toggle newVal)->{
            changeRadioButtonProperties(oldVal, newVal, this.answer4, numberOfRow+3);
        });

        question5 = new Label();
        question5.setText(this.questionText5);
        radioButton5A.setToggleGroup(group5);
        radioButton5B.setToggleGroup(group5);
        radioButton5C.setToggleGroup(group5);

        group5.selectedToggleProperty().addListener((ObservableValue<? extends Toggle>
                                                             observ, Toggle oldVal, Toggle newVal)->{
            changeRadioButtonProperties(oldVal, newVal, this.answer5, numberOfRow+4);
        });

        question6 = new Label();
        question6.setText(this.questionText6);
        radioButton6A.setToggleGroup(group6);
        radioButton6B.setToggleGroup(group6);
        radioButton6C.setToggleGroup(group6);

        group6.selectedToggleProperty().addListener((ObservableValue<? extends Toggle>
                                                             observ, Toggle oldVal, Toggle newVal)->{
            changeRadioButtonProperties(oldVal, newVal, this.answer6, numberOfRow+5);
        });
    }

    @Override
    protected void createRadioButtons() {
        group1 = new ToggleGroup();
        radioButton1A = new RadioButton("A");
        radioButton1B = new RadioButton("B");
        radioButton1C = new RadioButton("C");

        group2 = new ToggleGroup();
        radioButton2A = new RadioButton("A");
        radioButton2B = new RadioButton("B");
        radioButton2C = new RadioButton("C");

        group3 = new ToggleGroup();
        radioButton3A = new RadioButton("A");
        radioButton3B = new RadioButton("B");
        radioButton3C = new RadioButton("C");

        group4 = new ToggleGroup();
        radioButton4A = new RadioButton("A");
        radioButton4B = new RadioButton("B");
        radioButton4C = new RadioButton("C");

        group5 = new ToggleGroup();
        radioButton5A = new RadioButton("A");
        radioButton5B = new RadioButton("B");
        radioButton5C = new RadioButton("C");

        group6 = new ToggleGroup();
        radioButton6A = new RadioButton("A");
        radioButton6B = new RadioButton("B");
        radioButton6C = new RadioButton("C");
    }

    @Override
    protected void createToggleButtons() {
        answer1Button = new ToggleButton();
        answer2Button = new ToggleButton();
        answer3Button = new ToggleButton();
        answer4Button = new ToggleButton();
        answer5Button = new ToggleButton();
        answer6Button = new ToggleButton();

        document1Button = new ToggleButton();

        script1Button = new ToggleButton();
        script2Button = new ToggleButton();
        script3Button = new ToggleButton();
        script4Button = new ToggleButton();
        script5Button = new ToggleButton();
        script6Button = new ToggleButton();

        listening1Button = new ToggleButton();
        listening2Button = new ToggleButton();
        listening3Button = new ToggleButton();
        listening4Button = new ToggleButton();
        listening5Button = new ToggleButton();
        listening6Button = new ToggleButton();

        backButton = new ToggleButton();
        nextButton = new ToggleButton();
        homeButton = new ToggleButton();
        startButton = new ToggleButton();

        //Toggle button group
        ToggleGroup toggleGroup = new ToggleGroup();
        backButton.setToggleGroup(toggleGroup);
        homeButton.setToggleGroup(toggleGroup);
        nextButton.setToggleGroup(toggleGroup);
        startButton.setToggleGroup(toggleGroup);
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

        ImageView view6;
        view6 = createImageView("resources/LIGHTBULBO_64.png");
        view6.setFitWidth(25);
        view6.setFitHeight(25);

        //Setting a graphic to the button
        answer6Button.setGraphic(view6);

        ImageView view7;
        view7 = createImageView("resources/NOTE_64.png");
        view7.setFitWidth(25);
        view7.setFitHeight(25);
        //Setting a graphic to the button
        script1Button.setGraphic(view7);

        ImageView view8;
        view8 = createImageView("resources/NOTE_64.png");
        view8.setFitWidth(25);
        view8.setFitHeight(25);
        //Setting a graphic to the button
        script2Button.setGraphic(view8);

        ImageView view9;
        view9 = createImageView("resources/NOTE_64.png");
        view9.setFitWidth(25);
        view9.setFitHeight(25);
        //Setting a graphic to the button
        script3Button.setGraphic(view9);

        ImageView view10;
        view10 = createImageView("resources/NOTE_64.png");
        view10.setFitWidth(25);
        view10.setFitHeight(25);
        //Setting a graphic to the button
        script3Button.setGraphic(view10);

        ImageView view11;
        view11 = createImageView("resources/NOTE_64.png");
        view11.setFitWidth(25);
        view11.setFitHeight(25);
        //Setting a graphic to the button
        script4Button.setGraphic(view11);

        ImageView view12;
        view12 = createImageView("resources/NOTE_64.png");
        view12.setFitWidth(25);
        view12.setFitHeight(25);
        //Setting a graphic to the button
        script5Button.setGraphic(view12);

        ImageView view13;
        view13 = createImageView("resources/NOTE_64.png");
        view13.setFitWidth(25);
        view13.setFitHeight(25);
        //Setting a graphic to the button
        script6Button.setGraphic(view13);

        ImageView view14;
        view14 = createImageView("resources/HEADPHONE2_64.png");
        //Setting the size of the button
        listening1Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        listening1Button.setGraphic(view14);

        ImageView view15;
        view15 = createImageView("resources/HEADPHONE2_64.png");
        //Setting the size of the button
        listening2Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        listening2Button.setGraphic(view15);

        ImageView view16;
        view16 = createImageView("resources/HEADPHONE2_64.png");
        //Setting the size of the button
        listening3Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        listening3Button.setGraphic(view16);

        ImageView view17;
        view17 = createImageView("resources/HEADPHONE2_64.png");
        //Setting the size of the button
        listening4Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        listening4Button.setGraphic(view17);

        ImageView view18;
        view18 = createImageView("resources/HEADPHONE2_64.png");
        //Setting the size of the button
        listening5Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        listening5Button.setGraphic(view18);

        ImageView view19;
        view19 = createImageView("resources/HEADPHONE2_64.png");
        //Setting the size of the button
        listening6Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        listening6Button.setGraphic(view19);

        review1 = new ImageView();
        review1.setFitHeight(40);
        review1.setFitWidth(40);
        Image image = createImage("resources/BLUE_PIN_512.png");
        review1.setImage(image);
        review1.setOpacity(0.0);

        review2 = new ImageView();
        review2.setFitHeight(40);
        review2.setFitWidth(40);
        Image image2 = createImage("resources/BLUE_PIN_512.png");
        review2.setImage(image2);
        review2.setOpacity(0.0);

        review3 = new ImageView();
        review3.setFitHeight(40);
        review3.setFitWidth(40);
        Image image3 = createImage("resources/BLUE_PIN_512.png");
        review3.setImage(image3);
        review3.setOpacity(0.0);

        review4 = new ImageView();
        review4.setFitHeight(40);
        review4.setFitWidth(40);
        Image image4 = createImage("resources/BLUE_PIN_512.png");
        review4.setImage(image4);
        review4.setOpacity(0.0);

        review5 = new ImageView();
        review5.setFitHeight(40);
        review5.setFitWidth(40);
        Image image5 = createImage("resources/BLUE_PIN_512.png");
        review5.setImage(image5);
        review5.setOpacity(0.0);

        review6 = new ImageView();
        review6.setFitHeight(40);
        review6.setFitWidth(40);
        Image image6 = createImage("resources/BLUE_PIN_512.png");
        review6.setImage(image5);
        review6.setOpacity(0.0);

        ImageView view20;
        view20 = createImageView("resources/book_notes__6203.png");
        //Setting the size of the button
        document1Button.setPrefSize(50, 30);
        //Setting a graphic to the button
        document1Button.setGraphic(view20);
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

        result6 = new ImageView();
        result6.setFitHeight(20);
        result6.setFitWidth(20);
        final Image image6 = createImage("resources/CROSS_512.png");
        result6.setImage(image6);
        result6.setOpacity(0);
        resultDate6 = new Label();

        setupQuestions(this.numberOfRow);
        rebuildTextsAndButtons(numberOfRow);
        restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2,
                numberOfRow+4, numberOfRow+5, numberOfRow+6);

        sourceComboBox.setMaxHeight(30);
        sourceComboBox.setPrefHeight(30);
        sourceComboBox.setPrefWidth(480);
        menuButton = new MenuButton(MENU_HEADER);
        splitMenuButton = menuButton.createSplitMenuButton("resources/PIECHART_32.png",
                errataPieChart);

        bookMark = new ImageView();
        bookMark.setFitHeight(50);
        bookMark.setFitWidth(50);

        final Image image = createImage("resources/BOOKMARK3_512.png");
        bookMark.setImage(image);

        HBox hBox0 = new HBox();
        hBox0.getChildren().addAll(bookMark, sourceComboBox, splitMenuButton);
        hBox0.setAlignment(Pos.TOP_RIGHT);
        hBox0.setSpacing(5);
        hBox0.setPadding(new Insets(0, 10, 25, 10));

        HBox hBox1 = new HBox();
        HBox hBox1_5 = new HBox();
        hBox1_5.getChildren().addAll(answer1Button, script1Button, listening1Button, document1Button, result1, resultDate1, review1);
        hBox1_5.setSpacing(20);
        hBox1_5.setPadding(new Insets(0, 40, 0, 40));
        hBox1.getChildren().addAll(radioButton1A, radioButton1B, radioButton1C ,hBox1_5);
        hBox1.setSpacing(20);
        hBox1.setAlignment(Pos.TOP_LEFT);
        hBox1.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox2 = new HBox();
        HBox hBox2_5 = new HBox();
        hBox2_5.getChildren().addAll(answer2Button, script2Button, listening2Button, result2, resultDate2, review2);
        hBox2_5.setSpacing(20);
        hBox2_5.setPadding(new Insets(0, 40, 0, 40));
        hBox2.getChildren().addAll(radioButton2A, radioButton2B, radioButton2C ,hBox2_5);
        hBox2.setSpacing(20);
        hBox2.setAlignment(Pos.TOP_LEFT);
        hBox2.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox3 = new HBox();
        HBox hBox3_5 = new HBox();
        hBox3_5.getChildren().addAll(answer3Button, script3Button, listening3Button, result3, resultDate3, review3);
        hBox3_5.setSpacing(20);
        hBox3_5.setPadding(new Insets(0, 40, 0, 40));
        hBox3.getChildren().addAll(radioButton3A, radioButton3B, radioButton3C ,hBox3_5);
        hBox3.setSpacing(20);
        hBox3.setAlignment(Pos.TOP_LEFT);
        hBox3.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox4 = new HBox();
        HBox hBox4_5 = new HBox();
        hBox4_5.getChildren().addAll(answer4Button, script4Button, listening4Button, result4, resultDate4, review4);
        hBox4_5.setSpacing(20);
        hBox4_5.setPadding(new Insets(0, 40, 0, 40));
        hBox4.getChildren().addAll(radioButton4A, radioButton4B, radioButton4C ,hBox4_5);
        hBox4.setSpacing(20);
        hBox4.setAlignment(Pos.TOP_LEFT);
        hBox4.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox5 = new HBox();
        HBox hBox5_5 = new HBox();
        hBox5_5.getChildren().addAll(answer5Button, script5Button, listening5Button, result5, resultDate5, review5);
        hBox5_5.setSpacing(20);
        hBox5_5.setPadding(new Insets(0, 40, 0, 40));
        hBox5.getChildren().addAll(radioButton5A, radioButton5B, radioButton5C ,hBox5_5);
        hBox5.setSpacing(20);
        hBox5.setAlignment(Pos.TOP_LEFT);
        hBox5.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox6 = new HBox();
        HBox hBox6_5 = new HBox();
        hBox6_5.getChildren().addAll(answer6Button, script6Button, listening6Button, result6, resultDate6, review6);
        hBox6_5.setSpacing(20);
        hBox6_5.setPadding(new Insets(0, 40, 0, 40));
        hBox6.getChildren().addAll(radioButton6A, radioButton6B, radioButton6C ,hBox6_5);
        hBox6.setSpacing(20);
        hBox6.setAlignment(Pos.TOP_LEFT);
        hBox6.setPadding(new Insets(10, 10, 10, 10));

        HBox hBox7 = new HBox();
        hBox7.getChildren().addAll(backButton, nextButton, homeButton);
        hBox7.setSpacing(10);
        hBox7.setAlignment(Pos.TOP_LEFT);
        hBox7.setPadding(new Insets(10, 10, 10, 10));

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().addAll(hBox0, question1, hBox1, question2, hBox2, question3,
                hBox3, question4, hBox4, question5, hBox5, question6, hBox6, hBox7);
        return vBox;
    }

    @Override
    protected void changeImageOrTextField(int row) {

    }

    private void changeRadioAnswerButtons(boolean status) {
        this.radioButton1A.setDisable(status);
        this.radioButton1B.setDisable(status);
        this.radioButton1C.setDisable(status);
        this.radioButton2A.setDisable(status);
        this.radioButton2B.setDisable(status);
        this.radioButton2C.setDisable(status);
        this.radioButton3A.setDisable(status);
        this.radioButton3B.setDisable(status);
        this.radioButton3C.setDisable(status);

        this.radioButton4A.setDisable(status);
        this.radioButton4B.setDisable(status);
        this.radioButton4C.setDisable(status);
        this.radioButton5A.setDisable(status);
        this.radioButton5B.setDisable(status);
        this.radioButton5C.setDisable(status);
        this.radioButton6A.setDisable(status);
        this.radioButton6B.setDisable(status);
        this.radioButton6C.setDisable(status);

        this.answer1Button.setDisable(status);
        this.answer2Button.setDisable(status);
        this.answer3Button.setDisable(status);
        this.answer4Button.setDisable(status);
        this.answer5Button.setDisable(status);
        this.answer6Button.setDisable(status);
    }

    void goToNextPage() {
        if(numberOfRow <= lastRowNum - 7) {
            // Increase rowNum to move to the next question set
            numberOfRow = numberOfRow + 6;

            if (!sourceComboBox.getValue().equals(parts.get(numberOfRow).getSource())) {
                sourceComboBox.setValue((parts.get(numberOfRow).getSource()));
            }
            setupQuestions(numberOfRow);

            renewSourceCombo();
            rebuildTextsAndButtons(numberOfRow);
            restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2,
                      numberOfRow+3, numberOfRow+4, numberOfRow+5);
            checkBookMark(numberOfRow);
            startSound("resources/bookPageFlip.mp3");
       }
   }

    private void goBackToPrevious() {
        // if not 1st Row item
        if(numberOfRow > 0) {

            if (!sourceComboBox.getValue().equals(parts.get(numberOfRow).getSource())) {
                setTail(true);
                sourceComboBox.setValue(parts.get(calculatePreviousRow(part2List, NUM_OF_PART2_QUESTIONS)).getSource());
                setTail(false);
            } else { // perform the lines only if no sourceCombox value changes
                // Decrease rowNum to move to the next question set
                if (numberOfRow - NUM_OF_PART2_QUESTIONS > 0) {
                    numberOfRow = calculatePreviousRow(part2List, NUM_OF_PART2_QUESTIONS);

                    if (!sourceComboBox.getValue().equals(parts.get(numberOfRow).getSource())) {
                        setSkipRenewQuestions(true);
                        sourceComboBox.setValue(parts.get(numberOfRow).getSource());
                        setSkipRenewQuestions(false);
                    }

                } else
                    numberOfRow = 0;

                setupQuestions(numberOfRow);
                rebuildTextsAndButtons(numberOfRow);
                restoreRadioButtons(numberOfRow, numberOfRow+1, numberOfRow+2,
                        numberOfRow+3, numberOfRow+4, numberOfRow+5);
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

    /**
     * Override the parent implemented method.
     * @return
     */
    protected Scene rebuildForm(){
        Image image;

        createRadioButtons();
        createToggleButtons();
        createImageViews();
        createGroupControls();
        layout = createLayout();

        // Get the PART from the 1st record
        String part = this.parts.get(0).getPart();
        image = createImage("resources/vintagePaper.jpg");

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

        setupScriptButton(numberOfRow, script1Button);
        setupScriptButton(numberOfRow+1, script2Button);
        setupScriptButton(numberOfRow+2, script3Button);
        setupScriptButton(numberOfRow+3, script4Button);
        setupScriptButton(numberOfRow+4, script5Button);
        setupScriptButton(numberOfRow+5, script6Button);

        scene = new Scene(layout, PART2_WIDTH, PART2_HEIGHT);
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
        // Renew the question texts
        question1.setText(questionText1);
        question2.setText(questionText2);
        question3.setText(questionText3);
        question4.setText(questionText4);
        question5.setText(questionText5);
        question6.setText(questionText6);

        // Reset the background fill color on radio-buttons
        resetRadiobuttonBackground(radioButton1A);
        resetRadiobuttonBackground(radioButton1B);
        resetRadiobuttonBackground(radioButton1C);

        resetRadiobuttonBackground(radioButton2A);
        resetRadiobuttonBackground(radioButton2B);
        resetRadiobuttonBackground(radioButton2C);

        resetRadiobuttonBackground(radioButton3A);
        resetRadiobuttonBackground(radioButton3B);
        resetRadiobuttonBackground(radioButton3C);

        resetRadiobuttonBackground(radioButton4A);
        resetRadiobuttonBackground(radioButton4B);
        resetRadiobuttonBackground(radioButton4C);

        resetRadiobuttonBackground(radioButton5A);
        resetRadiobuttonBackground(radioButton5B);
        resetRadiobuttonBackground(radioButton5C);

        resetRadiobuttonBackground(radioButton6A);
        resetRadiobuttonBackground(radioButton6B);
        resetRadiobuttonBackground(radioButton6C);

        setupScriptButton(row, script1Button);
        setupScriptButton(row+1, script2Button);
        setupScriptButton(row+2, script3Button);
        setupScriptButton(row+3, script4Button);
        setupScriptButton(row+4, script5Button);
        setupScriptButton(row+5, script6Button);
    }

     private void renewSourceCombo() {
        String source = sourceComboBox.getValue().toString();
        String record = parts.get(numberOfRow).getSource();

        if (!source.matches(record))
            sourceComboBox.setValue(record);
    }

    private void resetAnswerQuestions(int row) {
        if(row == 1) {
            this.setQuestionText1("");
            this.setAnswer1("");
            this.setQuestionText2("");
            this.setAnswer2("");
            this.setQuestionText3("");
            this.setAnswer3("");
            this.setQuestionText4("");
            this.setAnswer4("");
            this.setQuestionText5("");
            this.setAnswer5("");
            this.setQuestionText6("");
            this.setAnswer6("");
        } else if(row == 2) {
            this.setQuestionText2("");
            this.setAnswer2("");
            this.setQuestionText3("");
            this.setAnswer3("");
            this.setQuestionText4("");
            this.setAnswer4("");
            this.setQuestionText5("");
            this.setAnswer5("");
            this.setQuestionText6("");
            this.setAnswer6("");
        } else if(row == 3) {
            this.setQuestionText3("");
            this.setAnswer3("");
            this.setQuestionText4("");
            this.setAnswer4("");
            this.setQuestionText5("");
            this.setAnswer5("");
            this.setQuestionText6("");
            this.setAnswer6("");
        } else if(row == 4) {
            this.setQuestionText4("");
            this.setAnswer4("");
            this.setQuestionText5("");
            this.setAnswer5("");
            this.setQuestionText6("");
            this.setAnswer6("");
        } else if(row == 5) {
            this.setQuestionText5("");
            this.setAnswer5("");
            this.setQuestionText6("");
            this.setAnswer6("");
        } else if(row == 6) {
            this.setQuestionText6("");
            this.setAnswer6("");
        }
    }

    private void restoreRadioButtons(int firstRow, int secondRow, int thirdRow,
                                     int fourthRow, int fifthRow, int sixthRow) {
        HashMap<String, ErrorRecord> record;

        record = errataPieChart.getRecord();

        //If there is any error record on HashMap
        if (!record.isEmpty()) {
            boolean result1 = false;
            boolean result2 = false;
            boolean result3 = false;
            boolean result4 = false;
            boolean result5 = false;
            boolean result6 = false;

            String answer1 = null;
            String answer2 = null;
            String answer3 = null;
            String answer4 = null;
            String answer5 = null;
            String answer6 = null;

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

            if (record.containsKey(parts.get(sixthRow).getItemNum())) {
                answer6 = record.get(parts.get(sixthRow).getItemNum()).getAnswer();
                result6 = record.get(parts.get(sixthRow).getItemNum()).isResult();
            }

            if(answer1 != null) {
                if (answer1.matches("A")) {
                    rebuildRadiobuttonBackground(radioButton1A, result1);
                } else if (answer1.matches("B")) {
                    rebuildRadiobuttonBackground(radioButton1B, result1);
                } else if (answer1.matches("C")) {
                    rebuildRadiobuttonBackground(radioButton1C, result1);
                }
            }

            if (answer2 != null) {
                if (answer2.matches("A")) {
                    rebuildRadiobuttonBackground(radioButton2A, result2);
                } else if (answer2.matches("B")) {
                    rebuildRadiobuttonBackground(radioButton2B, result2);
                } else if (answer2.matches("C")) {
                    rebuildRadiobuttonBackground(radioButton2C, result2);
                }
            }

            if (answer3 != null) {
                if (answer3.matches("A")) {
                    rebuildRadiobuttonBackground(radioButton3A, result3);
                } else if (answer3.matches("B")) {
                    rebuildRadiobuttonBackground(radioButton3B, result3);
                } else if (answer3.matches("C")) {
                    rebuildRadiobuttonBackground(radioButton3C, result3);
                }
            }

            if (answer4 != null) {
                if (answer4.matches("A")) {
                    rebuildRadiobuttonBackground(radioButton4A, result4);
                } else if (answer4.matches("B")) {
                    rebuildRadiobuttonBackground(radioButton4B, result4);
                } else if (answer4.matches("C")) {
                    rebuildRadiobuttonBackground(radioButton4C, result4);
                }
            }

            if (answer5 != null) {
                if (answer5.matches("A")) {
                    rebuildRadiobuttonBackground(radioButton5A, result5);
                } else if (answer5.matches("B")) {
                    rebuildRadiobuttonBackground(radioButton5B, result5);
                } else if (answer5.matches("C")) {
                    rebuildRadiobuttonBackground(radioButton5C, result5);
                }
            }

            if (answer6 != null) {
                if (answer6.matches("A")) {
                    rebuildRadiobuttonBackground(radioButton6A, result6);
                } else if (answer6.matches("B")) {
                    rebuildRadiobuttonBackground(radioButton6B, result6);
                } else if (answer6.matches("C")) {
                    rebuildRadiobuttonBackground(radioButton6C, result6);
                }
            }
            // call to enable/disable dialogButton & scriptButton
            setupScriptButton(firstRow, script1Button);
            setupScriptButton(firstRow+1, script2Button);
            setupScriptButton(firstRow+2, script3Button);
            setupScriptButton(firstRow+3, script4Button);
            setupScriptButton(firstRow+4, script5Button);
            setupScriptButton(firstRow+5, script6Button);

            // Restore the previous question texts
            question1.setText(questionText1);
            question2.setText(questionText2);
            question3.setText(questionText3);
            question4.setText(questionText4);
            question5.setText(questionText5);
            question6.setText(questionText6);
        }
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

    private void setUpMadiaControlLayout(int row) {
        // Create & assign the audio file name.
        if (!currentMediaFile.contains(parts.get(row).getAudioFile())) {
            currentMediaFile = parts.get(row).getAudioFile();
            // Remove & restore the media controls to renew the media file
            hBox4.getChildren().remove(0);
            hBox4.getChildren().add(setUpMediaControl(row));
        }
    }

    private void setResultImage(int row) {
        String result;
        String resultDate;
        if(row < part2List.size()) {
            result = parts.get(row).getResult();
            resultDate = parts.get(row).getResultDate();

            switchResultImage(result, resultDate, result1, resultDate1);
        }
        if(row+1 < part2List.size()) {
            result = parts.get(row + 1).getResult();
            resultDate = parts.get(row + 1).getResultDate();

            switchResultImage(result, resultDate, result2, resultDate2);
        }
        if(row+2 < part2List.size()) {
            result = parts.get(row + 2).getResult();
            resultDate = parts.get(row + 2).getResultDate();

            switchResultImage(result, resultDate, result3, resultDate3);
        }
        if(row+3 < part2List.size()) {
            result = parts.get(row + 3).getResult();
            resultDate = parts.get(row + 3).getResultDate();

            switchResultImage(result, resultDate, result4, resultDate4);
        }
        if(row+4 < part2List.size()) {
            result = parts.get(row + 4).getResult();
            resultDate = parts.get(row + 4).getResultDate();

            switchResultImage(result, resultDate, result5, resultDate5);
        }
        if(row+5 < part2List.size()) {
            result = parts.get(row + 5).getResult();
            resultDate = parts.get(row + 5).getResultDate();

            switchResultImage(result, resultDate, result6, resultDate6);
        }
    }

    private void setReviewImage(int row) {
        String review = null;
        if(row < part2List.size()) {
            review = reviewManagement.returnReviewRecord(parts.get(row).getItemNum());
        }
        if (review.matches("Mark")) {
            review1.setOpacity(1.0);
        } else {
            review1.setOpacity(0);
        }
        if(row+1 < part2List.size()) {
            review = reviewManagement.returnReviewRecord(parts.get(row + 1).getItemNum());
        }
        if(review.matches("Mark")) {
            review2.setOpacity(1.0);
        } else {
            review2.setOpacity(0);
        }
        if(row+2 < part2List.size()) {
            review = reviewManagement.returnReviewRecord(parts.get(row + 2).getItemNum());
        }
        if(review.matches("Mark")) {
            review3.setOpacity(1.0);
        } else {
            review3.setOpacity(0);
        }
        if(row+3 < part2List.size()) {
            review = reviewManagement.returnReviewRecord(parts.get(row + 3).getItemNum());
        }
        if(review.matches("Mark")) {
            review4.setOpacity(1.0);
        } else {
            review4.setOpacity(0);
        }
        if(row+4 < part2List.size()) {
            review = reviewManagement.returnReviewRecord(parts.get(row + 4).getItemNum());
        }
        if(review.matches("Mark")) {
            review5.setOpacity(1.0);
        } else {
            review5.setOpacity(0);
        }
        if(row+5 < part2List.size()) {
            review = reviewManagement.returnReviewRecord(parts.get(row + 5).getItemNum());
        }
        if(review.matches("Mark")) {
            review6.setOpacity(1.0);
        } else {
            review6.setOpacity(0);
        }
    }

    private void setupMediaFiles(int row) {
        String fileName;
        String fileFullPath;
        // Create & assign the audio file name.
        if(row < part2List.size()) {
            fileName = parts.get(row).getAudioFile();
            fileFullPath = parts.get(row).getFilePath() + fileName;
            this.setMediaFile(false);
            if (fileName.isBlank()) {
                if (!listening1Button.disableProperty().get()) {
                    listening1Button.disableProperty().set(true);
                }
            } else if (!fileName.isBlank() && (!fileFullPath.isEmpty() || !fileFullPath.isBlank())) {
                File f = new File(fileFullPath);
                try {
                    this.setMediaPlayer(new Media(f.toURI().toString()));
                    this.setMediaFile(true);
                    if (listening1Button.disableProperty().get()) {
                        listening1Button.disableProperty().set(false);
                    }
                } catch (MediaException e) {
                    e.printStackTrace();
                }
            }
        } else {
            this.setMediaFile(false);
        }

        // 2nd media player
        if(row+1 < part2List.size()) {
            fileName = parts.get(row + 1).getAudioFile();
            fileFullPath = parts.get(row + 1).getFilePath() + fileName;

            if (fileName.isBlank()) {
                if (!listening2Button.disableProperty().get()) {
                    listening2Button.disableProperty().set(true);
                }
            } else if (!fileName.isBlank() && (!fileFullPath.isEmpty() || !fileFullPath.isBlank())) {
                File f = new File(fileFullPath);
                try {
                    this.setMedia2Player(new Media(f.toURI().toString()));
                    if (listening2Button.disableProperty().get()) {
                        listening2Button.disableProperty().set(false);
                    }
                } catch (MediaException e) {
                    e.printStackTrace();
                }
            }
        } else {
            this.setMedia2File(false);
        }

        // 3rd media player
        if(row+2 < part2List.size()) {
            fileName = parts.get(row + 2).getAudioFile();
            fileFullPath = parts.get(row + 2).getFilePath() + fileName;

            if (fileName.isBlank()) {
                if (!listening3Button.disableProperty().get()) {
                    listening3Button.disableProperty().set(true);
                }
            } else if (!fileName.isBlank() && (!fileFullPath.isEmpty() || !fileFullPath.isBlank())) {
                File f = new File(fileFullPath);
                try {
                    this.setMedia3Player(new Media(f.toURI().toString()));
                    if (listening3Button.disableProperty().get()) {
                        listening3Button.disableProperty().set(false);
                    }
                } catch (MediaException e) {
                    e.printStackTrace();
                }
            } else {
                this.setMedia3File(false);
            }
        } else {
            this.setMedia3File(false);
        }

        // 4th media player
        if(row+3 < part2List.size()) {
            fileName = parts.get(row+3).getAudioFile();
            fileFullPath = parts.get(row+3).getFilePath()+fileName;
            if (fileName.isBlank()) {
                if (!listening4Button.disableProperty().get()) {
                    listening4Button.disableProperty().set(true);
                }
            } else if (!fileName.isBlank() && (!fileFullPath.isEmpty() || !fileFullPath.isBlank())) {
                File f = new File(fileFullPath);
                try {
                    this.setMedia4Player(new Media(f.toURI().toString()));
                    if (listening4Button.disableProperty().get()) {
                        listening4Button.disableProperty().set(false);
                    }
                } catch (MediaException e) {
                    e.printStackTrace();
                }
            } else {
                this.setMedia4File(false);
            }
        } else {
            this.setMedia4File(false);
        }

        // 5th media player
        if(row+4 < part2List.size()) {
            fileName = parts.get(row + 4).getAudioFile();
            fileFullPath = parts.get(row + 4).getFilePath() + fileName;
            if (fileName.isBlank()) {
                if (!listening5Button.disableProperty().get()) {
                    listening5Button.disableProperty().set(true);
                }
            } else if (!fileName.isBlank() && (!fileFullPath.isEmpty() || !fileFullPath.isBlank())) {
                File f = new File(fileFullPath);
                try {
                    this.setMedia5Player(new Media(f.toURI().toString()));
                    if (listening5Button.disableProperty().get()) {
                        listening5Button.disableProperty().set(false);
                    }
                } catch (MediaException e) {
                    e.printStackTrace();
                }
            } else {
                this.setMedia5File(false);
            }
        } else {
            this.setMedia5File(false);
        }

        // 6th media player
        if(row+5 < part2List.size()) {
            fileName = parts.get(row + 5).getAudioFile();
            fileFullPath = parts.get(row + 5).getFilePath() + fileName;
            if (fileName.isBlank()) {
                if (!listening6Button.disableProperty().get()) {
                    listening6Button.disableProperty().set(true);
                }
            } else if (!fileName.isBlank() && (!fileFullPath.isEmpty() || !fileFullPath.isBlank())) {
                File f = new File(fileFullPath);
                try {
                    this.setMedia6Player(new Media(f.toURI().toString()));
                    if (listening6Button.disableProperty().get()) {
                        listening6Button.disableProperty().set(false);
                    }
                } catch (MediaException e) {
                    e.printStackTrace();
                }
            } else {
                this.setMedia6File(false);
            }
        } else {
            this.setMedia6File(false);
        }
    }
    @Override
    protected void setupQuestions(int row) {

        changeRadioAnswerButtons(true);

        this.setQuestionText1(parts.get(row).getQuestion());
        this.setAnswer1(parts.get(row).getAnswer());
        this.setCurrentSource(parts.get(row).getSource());
        this.radioButton1A.setDisable(false);
        this.radioButton1B.setDisable(false);
        this.radioButton1C.setDisable(false);
        this.answer1Button.setDisable(false);

        if(lessThanLastRow(row + 1)) {
            if (getCurrentSource().contentEquals(parts.get(row+1).getSource())) {
                this.setQuestionText2(parts.get(row + 1).getQuestion());
                this.setAnswer2(parts.get(row + 1).getAnswer());
                this.radioButton2A.setDisable(false);
                this.radioButton2B.setDisable(false);
                this.radioButton2C.setDisable(false);
                this.answer2Button.setDisable(false);

                if(lessThanLastRow(row + 2)) {
                    if (getCurrentSource().contentEquals(parts.get(row + 2).getSource())) {
                        this.setQuestionText3(parts.get(row + 2).getQuestion());
                        this.setAnswer3(parts.get(row + 2).getAnswer());
                        this.radioButton3A.setDisable(false);
                        this.radioButton3B.setDisable(false);
                        this.radioButton3C.setDisable(false);
                        this.answer3Button.setDisable(false);
                    } else {
                        resetAnswerQuestions(3);
                    }
                }
                if(lessThanLastRow(row + 3)) {
                    if (getCurrentSource().contentEquals(parts.get(row + 3).getSource())) {
                        this.setQuestionText4(parts.get(row + 3).getQuestion());
                        this.setAnswer4(parts.get(row + 3).getAnswer());
                        this.radioButton4A.setDisable(false);
                        this.radioButton4B.setDisable(false);
                        this.radioButton4C.setDisable(false);
                        this.answer4Button.setDisable(false);
                    } else {
                        resetAnswerQuestions(4);
                    }
                }
                if(lessThanLastRow(row + 4)) {
                    if (getCurrentSource().contentEquals(parts.get(row + 4).getSource())) {
                        this.setQuestionText5(parts.get(row + 4).getQuestion());
                        this.setAnswer5(parts.get(row + 4).getAnswer());
                        this.radioButton5A.setDisable(false);
                        this.radioButton5B.setDisable(false);
                        this.radioButton5C.setDisable(false);
                        this.answer5Button.setDisable(false);
                    } else {
                        resetAnswerQuestions(5);
                    }
                }
                if(lessThanLastRow(row + 5)) {
                    if (getCurrentSource().contentEquals(parts.get(row + 5).getSource())) {
                        this.setQuestionText6(parts.get(row + 5).getQuestion());
                        this.setAnswer6(parts.get(row + 5).getAnswer());
                        this.radioButton6A.setDisable(false);
                        this.radioButton6B.setDisable(false);
                        this.radioButton6C.setDisable(false);
                        this.answer6Button.setDisable(false);
                    } else {
                        resetAnswerQuestions(6);
                    }
                }
            } else {
                resetAnswerQuestions(2);
            }
        } else {
            resetAnswerQuestions(2);
        }

        setupMediaFiles(row);
        setResultImage(row);
        setReviewImage(row);
    }

    protected void setupScriptButton(int row, ToggleButton scriptButton) {
        if(row < part2List.size()) {
            if (parts.get(row).getScript().isEmpty())
                scriptButton.setDisable(true);
            else
                scriptButton.setDisable(false);
        } else {
            scriptButton.setDisable(false);
        }
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
        } else if(name.matches("question6")) {
            if (selection.matches("Mark"))
                review6.setOpacity(1.0);
            else
                review6.setOpacity(0.0);

            itemNumber = parts.get(numberOfRow+5).getItemNum();
        }

        reviewManagement.updateResultRecord(itemNumber, groupItemNumber, parts.get(numberOfRow).getPart(), selection.toString(), false);
    }
}