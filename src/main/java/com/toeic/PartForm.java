package com.toeic;

import com.jfoenix.controls.JFXToggleButton;
import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;

public abstract class PartForm {
    protected static final int FORM_HEIGHT = 700;
    protected static final int FORM_WIDTH = 740;
    protected static final String FAIL ="fail";
    protected static final String MENU_HEADER = "TOEIC PART1";
    protected static final String PASS = "pass";

    private boolean firstTime = true;

    protected AlertBox alertBox;

    protected Fireworks errataPieChart;

    protected int numberOfRow;
    protected int lastRowNum;
    protected List<Part> parts;

    protected Label question1;
    protected Label question2;
    protected Label question3;
    protected Label question4;
    protected Label question5;

    protected ImageView questionImgView1;
    protected ImageView questionImgView2;
    protected ImageView questionImgView3;
    protected ImageView questionImgView4;
    protected ImageView questionImgView5;

    protected StackPane questionPane1;
    protected StackPane questionPane2;
    protected StackPane questionPane3;
    protected StackPane questionPane4;
    protected StackPane questionPane5;

    private Media media;
    private Media media2;
    private Media media3;

    private static MediaPlayer mediaPlayer;

    private static Pane root;

    protected static ResultManagement resultManagement;
    protected static ReviewManagement reviewManagement;

    protected SortedMap<String, HistoryRecord> historyRecordSortedMap;
    protected SortedMap<String, Integer> sourceHeadMap;
    protected SortedMap<String, Integer> sourceTailMap;

    private static Stage stage;

    protected String answer1;
    protected String answer2;
    protected String answer3;
    protected String answer4;
    protected String answer5;
    protected String dialog;
    private String explanation;
    private String expAttrib;
    protected String questionText1;
    protected String questionText2;
    protected String questionText3;
    protected String questionText4;
    protected String questionText5;
    private String script;
    private String scrAttrib;

    private SplitMenuButton splitMenuButton;

    protected JFXToggleButton toggleShowResult;
    protected ToggleButton answer1Button;
    protected ToggleButton answer2Button;
    protected ToggleButton answer3Button;
    protected ToggleButton answer4Button;
    protected ToggleButton answer5Button;
    protected ToggleButton backButton;
    protected ToggleButton dictation1Button;
    protected ToggleButton dictation2Button;
    protected ToggleButton dictation3Button;
    protected ToggleButton dialog1Button;
    protected ToggleButton dialog2Button;
    protected ToggleButton dialog3Button;
    protected ToggleButton document1Button;
    protected ToggleButton document2Button;
    protected ToggleButton document3Button;
    protected ToggleButton resizeButton;
    protected ToggleButton listening1Button;
    protected ToggleButton listening2Button;
    protected ToggleButton listening3Button;
    protected ToggleButton textbookButton;

    protected ToggleButton homeButton;
    protected ToggleButton nextButton;
    protected ToggleButton script1Button;
    protected ToggleButton script2Button;
    protected ToggleButton script3Button;
    protected ToggleButton script4Button;
    protected ToggleButton script5Button;
    protected ToggleButton startButton;

    protected ToggleGroup group1;
    protected ToggleGroup group2;
    protected ToggleGroup group3;
    protected ToggleGroup group4;
    protected ToggleGroup group5;

    protected VBox layout;

    protected static HistoryRecordFile historyRecordFile = null;

    private static final Image PASS_IMG = createStaticImage("resources/OK_512.png");
    private static final Image FAIL_IMG = createStaticImage("resources/CROSS_512.png");

    Scene scene;

    /**
     * Constructor
     */
    public PartForm() {
    }

    public PartForm(List<Part> parts, int lastRowNum, Stage stage, Pane root) {
        this.parts = parts;

        this.lastRowNum = lastRowNum;
        this.root = root;
        this.stage = stage;
        alertBox = new AlertBox();
        errataPieChart = new Fireworks();
        }

    public PartForm(int numberOfRow, List<Part> parts, int lastRowNum, Stage stage, Pane root,
                    HistoryRecordFile historyRecordFile, ResultManagement resultManagement,
                    ReviewManagement reviewManagement) {
        this.parts = parts;
        this.numberOfRow = numberOfRow;
        this.lastRowNum = lastRowNum;
        this.root = root;
        this.stage = stage;
        this.resultManagement = resultManagement;
        this.reviewManagement = reviewManagement;
        alertBox = new AlertBox();
        errataPieChart = new Fireworks();
        this.historyRecordFile = historyRecordFile;

        switch (parts.get(0).getPart()) {
            case "1":
                historyRecordFile.setPart1List(parts);
                break;
            case "2":
                historyRecordFile.setPart2List(parts);
                break;
            case "3":
                historyRecordFile.setPart3List(parts);
                break;
            case "4":
                historyRecordFile.setPart4List(parts);
                break;
            case "5":
                historyRecordFile.setPart5List(parts);
                break;
            case "6":
                historyRecordFile.setPart6List(parts);
                break;
            case "7":
                historyRecordFile.setPart7List(parts);
                break;
        }
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }

    // Getter and Setter properties
    public Media getMedia() {
        return media;
    }

    public void setMediaPlayer(Media media) {
        this.media = media;
    }

    public Media getMedia2() {
        return media2;
    }

    public void setMedia2Player(Media media2) {
        this.media2 = media2;
    }

    public Media getMedia3() { return media3; }

    public void setMedia3Player(Media media3) {
        this.media3 = media3;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public void setAnswer4(String answer4) { this.answer4 = answer4; }

    public void setAnswer5(String answer5) { this.answer5 = answer5; }

    public void setQuestionText1(String questionText) { this.questionText1 = questionText; }

    public void setQuestionText2(String questionText) {
        this.questionText2 = questionText;
    }

    public void setQuestionText3(String questionText3) { this.questionText3 = questionText3; }

    public void setQuestionText4(String questionText4) { this.questionText4 = questionText4; }

    public void setQuestionText5(String questionText5) { this.questionText5 = questionText5; }

    public void setDialog(String dialog) { this.dialog = dialog; }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setExpAttrib(String expAttrib) {
        this.expAttrib = expAttrib;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setScrAttrib(String scrAttrib) {
        this.scrAttrib = scrAttrib;
    }

    public int getNumberOfRow() {
        return numberOfRow;
    }

    public void setLastRowNum(int lastRowNum) { this.lastRowNum = lastRowNum; }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public Pane getLayout() {
        return layout;
    }

    public static Pane getRoot() {
        return root;
    }

    public static void setRoot(Pane root) {
        PartForm.root = root;
    }

    public ToggleButton getDocument1Button() { return document1Button; }

    public void setDocument1Button(ToggleButton document1Button) { this.document1Button = document1Button; }

    public Image validationImage(String token) {
        return isValid(token) ? PASS_IMG : FAIL_IMG;
    }

    private static boolean isValid(String token) {
        if(token.matches("pass"))
            return true;
        else
            return false;
    }

    protected int calculatePreviousRow(LinkedList<String> partList, int numberOfQuestions) {
        int row = this.numberOfRow;
        int start;
        int last;
        int remainder = 0;

        String previousSource = parts.get(row-1).getSource();

        if (!previousSource.contentEquals(parts.get(row).getSource())) {
            start = partList.indexOf(previousSource);
            last = partList.lastIndexOf(previousSource);
            if (last > start) {
                int result = ((last - start) + 1) % numberOfQuestions;
                if (result == 0)
                    remainder = numberOfQuestions;
                else
                    remainder = result;
            } else if (last == start) {
                remainder = 1;
            }
            row = row - remainder;
        } else {
            row = row - numberOfQuestions;
        }
        return row;
    }

    protected abstract void assignActionEvent();

    protected abstract void createGroupControls();

    protected abstract void createRadioButtons();

    protected abstract void createToggleButtons();

    protected abstract void createImageViews();

    protected abstract VBox createLayout();

    protected void changeQestionOpacity(double questOpacity, double imageOpacity, Label questionLabel, ImageView questionImage) {
        if (questionLabel != null) questionLabel.setOpacity(questOpacity);
        if (questionImage != null) questionImage.setOpacity(imageOpacity);
    }

    protected abstract void changeImageOrTextField(int row);

    protected VBox createDocumentVBox(int row, String attrib, String fileName, String path) {
        alertBox.setWindowHeight(820);
        alertBox.setWindowWidth(830);
        return alertBox.getImageTextContents(attrib, fileName, path, true);
    }

    protected VBox createDialog(int row) {
        alertBox.setWindowHeight(820);
        alertBox.setWindowWidth(830);
        return alertBox.getImageTextContents(parts.get(row).getDialogAttrib(), parts.get(row).getDialog(),
                parts.get(row).getFilePath(), true);
    }

    protected ImageView createImageView(String fileName) {
        File imageFile = new File(fileName);
        Image image = new Image(imageFile.toURI().toString());
        ImageView view = new ImageView(image);
        view.setFitHeight(30);
        view.setPreserveRatio(true);
        return view;
    }

    protected ImageView createImageView(String fileName, double height) {
        Image image = new Image(getClass().getResourceAsStream(fileName));
        ImageView view = new ImageView(image);
        view.setFitHeight(height);
        view.setPreserveRatio(true);
        return view;
    }

    static Image createStaticImage(String fileName) {
        File imageFile = new File(fileName);
        Image image = new Image(imageFile.toURI().toString());
        //     Image image = new Image(fileName);
        return image;
    }

    protected Image createImage(String fileName) {
        File imageFile = new File(fileName);
        Image image = new Image(imageFile.toURI().toString());
        return image;
    }

    protected ImageView createImageView(String fileName, double height, boolean preserveRatio) {
        Image image = new Image(getClass().getResourceAsStream(fileName));
        ImageView view = new ImageView(image);
        view.setFitHeight(height);
        view.setPreserveRatio(preserveRatio);
        return view;
    }

    Image getImage(String filename) {
        FileInputStream input = null;
        try {
            input = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new Image(input);
    }
    protected static void makeFadeIn() {
        stage.setScene(root.getScene());
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(1000));
        fadeTransition.setNode(root);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    protected void makeFadeOut() {
        stage.setResizable(false);
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(1000));
        fadeTransition.setNode(this.getLayout());
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e->makeFadeIn());
        fadeTransition.play();
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

        return scene;
    }

    protected void resetRadiobuttonBackground(RadioButton radioButton) {
        radioButton.setStyle("-fx-background-color:null; -fx-background-radius: 15px; -fx-background-width: 2px;");

        if(radioButton.isSelected()) {
            radioButton.setSelected(false);
        }
    }

    protected void rebuildRadiobuttonBackground(RadioButton radioButton, boolean reassign) {

        if (reassign) {
            radioButton.setStyle("-fx-background-color:GREEN; -fx-background-radius: 15px; -fx-background-width: 2px;");
        } else {
            radioButton.setStyle("-fx-background-color:RED; -fx-background-radius: 15px; -fx-background-width: 2px;");
        }
        radioButton.setSelected(true);
    }

    abstract void rebuildTextsAndButtons(int row);

    protected void resetGroupRadiobuttonBackground(RadioButton chk, RadioButton radioButtonA, RadioButton radioButtonB,
                                                 RadioButton radioButtonC, RadioButton radioButtonD) {
        if (chk.getText().matches("A")) {
            resetRadiobuttonBackground(radioButtonB);
            resetRadiobuttonBackground(radioButtonC);
            resetRadiobuttonBackground(radioButtonD);
        } else if (chk.getText().matches("B")) {
            resetRadiobuttonBackground(radioButtonA);
            resetRadiobuttonBackground(radioButtonC);
            resetRadiobuttonBackground(radioButtonD);
        } else if (chk.getText().matches("C")) {
            resetRadiobuttonBackground(radioButtonA);
            resetRadiobuttonBackground(radioButtonB);
            resetRadiobuttonBackground(radioButtonD);
        } else if (chk.getText().matches("D")) {
            resetRadiobuttonBackground(radioButtonA);
            resetRadiobuttonBackground(radioButtonB);
            resetRadiobuttonBackground(radioButtonC);
        }
    }

    protected void setupDialogButton(int row) {

        if(parts.get(row).getDialogAttrib().matches("F"))
            dialog1Button.setDisable(false);
        else
            dialog1Button.setDisable(true);
    }

    protected void setupScriptButton(int row) {
        if(parts.get(row).getScript().isEmpty())
            script1Button.setDisable(true);
        else
            script1Button.setDisable(false);
    }

    abstract void setupQuestions(int row);

    protected void setupTextbookButton(int row) {
        if (parts.get(row).getTextbookPos().isEmpty() ||
                parts.get(row).getFilePath().isEmpty() ||
                parts.get(row).getTextbookFolder().isEmpty() ||
                parts.get(row).getTextbookAttrib().isEmpty())
            textbookButton.setDisable(true);
        else
            textbookButton.setDisable(false);
    }


    protected void showExplanation(int row) {
        alertBox.setWindowHeight(820);
        alertBox.setWindowWidth(880);

        alertBox.display(Modality.APPLICATION_MODAL, parts.get(row).getExplAttrib(),
                parts.get(row).getExplanation(), parts.get(row).getFilePath(), true);
    }

    /**
     * Unlike both of answer or explanation window, the dialog window won't be modal.
     * @param row
     * @param modality
     */
    protected void showDialog(int row, Modality modality) {
        alertBox.setWindowHeight(820);
        alertBox.setWindowWidth(830);
        alertBox.display(modality, parts.get(row).getDialogAttrib(), parts.get(row).getDialog(),
                parts.get(row).getFilePath(), true);
    }

    protected void showFolderContentsDialog(int row) {

        if (!parts.get(row).getTextbookPos().isEmpty() &&
            !parts.get(row).getFilePath().isEmpty() &&
            !parts.get(row).getTextbookFolder().isEmpty() &&
            !parts.get(row).getTextbookAttrib().isEmpty()) {
            alertBox.setWindowHeight(860);
            alertBox.setWindowWidth(950);
            alertBox.displayFolder(Modality.WINDOW_MODAL, parts.get(row).getTextbookAttrib(),
                    parts.get(row).getTextbookPos(), parts.get(row).getFilePath(), parts.get(row).getTextbookFolder(),
                    false, this);
        }
    }

    protected void showTriplePassage(int row, Modality modality) {
        alertBox.setWindowHeight(820);
        alertBox.setWindowWidth(830);
        alertBox.display(modality, parts.get(row).getTriplePassAttrib(),
                parts.get(row).getTriplePassage(), parts.get(row).getFilePath(), true);
    }

    protected void showScript(int row, Modality modality) {
        boolean isPreserveRatio;

        if(parts.get(row).getScrPreserveRatio().matches("N")) {
            isPreserveRatio = false;
        } else {
            isPreserveRatio = true;
        }

        alertBox.setWindowHeight(820);
        alertBox.setWindowWidth(830);
        alertBox.display(modality, parts.get(row).getScriAttrib(), parts.get(row).getScript(),
                parts.get(row).getFilePath(), isPreserveRatio);
    }

    protected static void startSound(String fileName) {
        // Create & assign the audio file name.
        if(!fileName.isBlank() || !fileName.isEmpty()) {
            File mediaFile = new File(fileName);

            mediaPlayer = new MediaPlayer(new Media(mediaFile.toURI().toString()));
            mediaPlayer.play();
        }
    }

    protected void switchResultImage(String result, String resultDate, ImageView resultImage,
                                   Label resultDateLabel) {
        if(result.matches("fail") || result.matches("pass")) {
            resultImage.setOpacity(1.0);
            resultImage.setImage(validationImage(result));
            resultDateLabel.setText(resultDate);
        } else {
            resultImage.setOpacity(0);
            resultDateLabel.setText("");
        }
    }

    protected void switchResultImage(String result, String resultDate, ImageView resultImage,
                                     Label resultDateLabel, ImageView reviewImage) {
        if(result.matches("fail") || result.matches("pass")) {
            resultImage.setOpacity(1.0);
            resultImage.setImage(validationImage(result));
            resultDateLabel.setText(resultDate);
        } else {
            resultImage.setOpacity(0);
            resultDateLabel.setText("");
            reviewImage.setOpacity(0);
        }
    }
}