package com.toeic;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.toeic.Vocabulary.startSound;

public class Vocabulary extends PartForm {

    static final int FORM_HEIGHT = 520;
    static final int FORM_WIDTH = 700;

    static final int IMAGE_HEIGHT = 300;
    static final int IMAGE_WIDTH = 650;

    private JFXComboBox dayComboBox;
    private SortedMap<Integer, Integer> dayHeadMap;
    private SortedMap<Integer, Integer> dayTailMap;

    private Label lastDate;

    private List<Part> day;
    private ObservableList<Integer> dayList = FXCollections.observableArrayList();

    private SortedMap<String, String> compStatusRecord;

    private boolean mediaFile = false;
    private boolean mediaFile2 = false;

    boolean isTail = false;

    AlertBox alertBox;

    ComprehensionStatusFile comprehensionStatusFile;

    private ImageView bookMark;
    ImageView icon;
    ImageView imageView;
    ImageView pronunciationView;

    MediaPlayer mediaPlayer;
    MediaPlayer mediaPlayer2;

    JFXToggleButton saveButton;

    Label num;
    Label word;
    Label part;

    RadioButton radio_100;
    RadioButton radio_50;
    RadioButton radio_zero;

    String filePath;
    String meaningFile;
    Scene scene;
    ToggleGroup group;

    ToggleArea toggle;

    ToggleButton backButton;
    ToggleButton homeButton;
    ToggleButton listening1Button;
    ToggleButton nextButton;
    ToggleButton startButton;
    ToggleButton statusButton;

    VBox layout;

    /**
     * Constructor
     */
    public Vocabulary(Stage stage, Pane root, int numberOfRow, List<Part> vocabulary, int lastRowNum) {
        super(vocabulary, lastRowNum, stage, root);

        dayHeadMap = new TreeMap<>();
        dayTailMap = new TreeMap<>();
        for(Part part : parts)
        {
            if(!part.getDay().isEmpty() && !part.getRowNumber().isEmpty()) {
                dayHeadMap.putIfAbsent(Integer.valueOf(part.getDay()), Integer.valueOf(part.getRowNumber()));
                dayTailMap.put(Integer.valueOf(part.getDay()), Integer.valueOf(part.getRowNumber()));
            }
        }
        // Assign the List sorted by day
        day = vocabulary;

        comprehensionStatusFile = new ComprehensionStatusFile();
        comprehensionStatusFile.setFileName("ComprehensionStatus.txt");
        comprehensionStatusFile.read();

        super.numberOfRow = comprehensionStatusFile.getBookMarkedItemNumber();

        alertBox = new AlertBox();
        alertBox.setWindowHeight(300);
        alertBox.setWindowWidth(250);
    }

    public VBox getLayout() {
        return layout;
    }
    public boolean isMediaFile() {
        return mediaFile;
    }
    public boolean isMediaFile2() {
        return mediaFile2;
    }
    public void setMediaFile(boolean mediaFile) {
        this.mediaFile = mediaFile;
    }
    public void setMediaFile2(boolean mediaFile2) {
        this.mediaFile2 = mediaFile2;
    }
    public boolean isTail() { return isTail; }
    public void setTail(boolean tail) { isTail = tail; }

    @Override
    protected void assignActionEvent() {
        bookMark.setOnMousePressed(e -> {
             if (numberOfRow != comprehensionStatusFile.getBookMarkedItemNumber()) {
                 startSound("resources/Click.mp3");
                 bookMark.setOpacity(1.0);
                 lastDate.setOpacity(1.0);
                 LocalDate now = LocalDate.now();
                 comprehensionStatusFile.setBookMarkedDate(now.toString());
                 lastDate.setText(comprehensionStatusFile.getBookMarkedDate());
                 comprehensionStatusFile.setBookMarkedItemNumber(numberOfRow);
             }
        });

        backButton.setOnAction(e -> {
            goBackToPrevious();
        });

        homeButton.setOnAction(e -> {
            if(saveButton.isSelected()) {
                comprehensionStatusFile.setFileName("ComprehensionStatus.txt");
                comprehensionStatusFile.write();
            }
            makeFadeOut();
        });

        nextButton.setOnAction(e -> {
            goToNextPage();
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

        statusButton.setOnAction(e -> {
            int percent;
            int numOfComp;
            int numOfIncomp;

            percent = comprehensionStatusFile.consolidateComprehensionStatus(lastRowNum);

            numOfComp = comprehensionStatusFile.getNumberOfComp();
            numOfIncomp = comprehensionStatusFile.getNumberOfIncompleted();
            alertBox.displayProgressIndicator(Modality.APPLICATION_MODAL, percent, numOfComp,
                                              numOfIncomp, true);
        });

        listening1Button.setOnAction(e -> {
            mediaPlayer2 = new MediaPlayer(this.getMedia2());
            mediaPlayer2.play();
            listening1Button.disableProperty().set(true);

            // Once the player is finished
            mediaPlayer2.setOnEndOfMedia( () -> {
                // Enable the startButton for the media-play
                if (listening1Button.disableProperty().get()) {
                    listening1Button.disableProperty().set(false);
                }
            });
        });

        dayComboBox.setOnAction(e -> {
            super.setParts(day);
            int rowNumber;
            if(dayHeadMap.get(dayComboBox.getValue()) < lastRowNum) {

                //Get the stored rowNumber for the day value
                if(isTail()) {  // For the tail
                    rowNumber = dayTailMap.get(dayComboBox.getValue());
                } else {  // For the head
                    rowNumber = dayHeadMap.get(dayComboBox.getValue());
                }

                for(int i = 0; i < parts.size(); i++) {
                    if(parts.get(i).getRowNumber().equals(String.valueOf(rowNumber))) {
                        numberOfRow = i;
                    }
                }
                updatePageSection(numberOfRow);
            }
        });
    }

    private void buildUpListViews() {
        for(Map.Entry mapEntry : dayHeadMap.entrySet()) {
            dayList.add(Integer.valueOf(mapEntry.getKey().toString()));
        }
    }

    private boolean checkBookMark(int numberOfRow) {
        boolean marked = false;
        if (numberOfRow != comprehensionStatusFile.getBookMarkedItemNumber()) {
            bookMark.setOpacity(0.0);
            lastDate.setOpacity(0.0);
        } else {
            bookMark.setOpacity(1.0);
            lastDate.setOpacity(1.0);
            marked = true;
        }
        return marked;
    }

    @Override
    protected void createGroupControls() {
    }

    @Override
    protected void createRadioButtons() {
    }

    protected void createImageViews() {
        ImageView view;
        view = createImageView("resources/BACK2.png");

        //Setting the size of the button
        backButton.setPrefSize(60, 25);
        //Setting a graphic to the button
        backButton.setGraphic(view);

        view = createImageView("resources/HEADPHONE.png");

        //Setting the size of the button
        startButton.setPrefSize(60, 25);
        //Setting a graphic to the button
        startButton.setGraphic(view);

        view = createImageView("resources/PLAY6.png");

        //Setting the size of the button
        listening1Button.setPrefSize(60, 25);
        //Setting a graphic to the button
        listening1Button.setGraphic(view);

        view = createImageView("resources/NEXT2.png");

        //Setting the size of the button
        nextButton.setPrefSize(60, 25);
        //Setting a graphic to the button
        nextButton.setGraphic(view);

        view = createImageView("resources/BackDoor96.png");

        //Setting the size of the button
        homeButton.setPrefSize(60, 25);
        //Setting a graphic to the button
        homeButton.setGraphic(view);

        view = createImageView("resources/GOAL3_96.png");
        statusButton.setPrefSize(60, 25);
        statusButton.setGraphic(view);

        bookMark = new ImageView();
        bookMark.setFitHeight(50);
        bookMark.setFitWidth(50);
        final Image image = getImage("resources/BOOKMARK3_512.png");
        bookMark.setImage(image);
    }

    protected VBox createLayout() {
        HBox hBox0 = new HBox();
        hBox0.getChildren().addAll(backButton, startButton, listening1Button, nextButton,
                statusButton, homeButton, saveButton);
        hBox0.setSpacing(5);
        hBox0.setAlignment(Pos.TOP_LEFT);
        hBox0.setPadding(new Insets(10, 10, 10, 10));
        BorderPane borderPane = new BorderPane();

        // Border pane Left
        buildUpListViews();
        dayComboBox = new JFXComboBox(dayList);
        dayComboBox.setPrefHeight(30);
        dayComboBox.setPrefWidth(125);
        dayComboBox.setPromptText("Select Day");

        Label itemNum = new Label("Item:");
        itemNum.setPadding(new Insets(0, 20, 10, 20));
        num = new Label();
        num.setAlignment(Pos.TOP_LEFT);

        lastDate = new Label(comprehensionStatusFile.getBookMarkedDate());
        lastDate.setFont(new Font("Arial", 8));

        HBox hBox1 = new HBox();
        hBox1.getChildren().addAll(dayComboBox, itemNum, num, bookMark, lastDate);
        hBox1.setSpacing(5);
        hBox1.setAlignment(Pos.TOP_LEFT);
        hBox1.setPadding(new Insets(0, 20, 10, 20));

        word = new Label();
        Font font = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 30);
        word.setFont(font);
        word.setAlignment(Pos.TOP_LEFT);
        word.setPadding(new Insets(0, 10, 0, 10));

        part = new Label();
        part.setTextFill(Color.web(String.valueOf(Color.RED)));
        part.setAlignment(Pos.TOP_LEFT);
        part.setPadding(new Insets(0, 10, 0, 10));

        pronunciationView = new ImageView();

        VBox vBox1 = new VBox();
        vBox1.getChildren().addAll(pronunciationView, part);
        vBox1.setSpacing(5);
        vBox1.setAlignment(Pos.TOP_LEFT);
        vBox1.setPadding(new Insets(0, 10, 0, 10));

        VBox vBoxBorderLeft = new VBox();
        vBoxBorderLeft.getChildren().setAll(hBox1, word, vBox1);
        borderPane.setLeft(vBoxBorderLeft);

        // Border pane Right
        Label comprehensionLevel = new Label("Comprehension Level:");
        group = new ToggleGroup();

        radio_100 = new RadioButton("100%");
        radio_100.setToggleGroup(group);
        radio_100.setTooltip(new Tooltip("Select the Comprehension level"));

        radio_50 = new RadioButton("50%");
        radio_50.setTooltip(new Tooltip("Select the Comprehension level"));
        radio_50.setToggleGroup(group);

        radio_zero = new RadioButton("0%");
        radio_zero.setTooltip(new Tooltip("Select the Comprehension level"));
        radio_zero.setToggleGroup(group);

        radio_100.setUserData("100%");
        radio_50.setUserData("50%");
        radio_zero.setUserData("0%");

        icon = new ImageView();
        icon.setFitHeight(30);
        icon.setFitWidth(30);

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle old_toggle, Toggle new_toggle) {
                String selection;
                if (group.getSelectedToggle() != null) {

                    if(!icon.isVisible()) icon.setVisible(true);

                    selection = group.getSelectedToggle().getUserData().toString();
                    final Image image = new Image(getClass().getResourceAsStream("/"+selection +".png")
                    );

                   updateComprehensionStatus(selection);
                    icon.setImage(image);
                }
            }
        });

        HBox hboxBorderRight = new HBox();
        VBox vbox = new VBox();

        vbox.getChildren().add(comprehensionLevel);
        vbox.getChildren().addAll(radio_100, radio_50, radio_zero);
        vbox.setSpacing(8);
        vbox.getStylesheets().add("radio.css");

        hboxBorderRight.getChildren().add(vbox);
        hboxBorderRight.getChildren().add(icon);
        hboxBorderRight.setSpacing(15);
        hboxBorderRight.setPadding(new Insets(0, 10, 0, 20));

        borderPane.setRight(hboxBorderRight);

        imageView = new ImageView();
        toggle = new ToggleArea(IMAGE_WIDTH,IMAGE_HEIGHT, imageView);

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(0, 10, 10, 10));
        vBox.getChildren().addAll(hBox0, borderPane, toggle);
        vBox.setAlignment(Pos.TOP_LEFT);

        createMediaPlayer(numberOfRow);

        updateRadioButtons(comprehensionStatusFile.retrieveStatus(parts.get(numberOfRow).getItemNum()));

        dayComboBox.setValue(Integer.valueOf(parts.get(numberOfRow).getDay()));

        updatePageSection(numberOfRow);

        return vBox;
    }

    @Override
    protected void changeImageOrTextField(int row) {

    }

    private void goToNextPage() {
        numberOfRow = ++numberOfRow;

        if(dayComboBox.isShowing()) {
            if (!dayComboBox.getValue().equals(parts.get(numberOfRow).getDay())) {
                dayComboBox.setValue(Integer.valueOf(parts.get(numberOfRow).getDay()));
            }
        } else {
            // As default, show 1st record for none combBox selected
            dayComboBox.setValue(Integer.valueOf(parts.get(numberOfRow).getDay()));
        }
        updatePageSection(numberOfRow);
        checkBookMark(numberOfRow);
        startSound("resources/bookPageFlip.mp3");
    }

    private void goBackToPrevious() {
        if (numberOfRow>0) {
            numberOfRow = --numberOfRow;

            if (!dayComboBox.getValue().equals(parts.get(numberOfRow).getDay())) {
                setTail(true);
                dayComboBox.setValue(Integer.valueOf(parts.get(numberOfRow).getDay()));
                setTail(false);
            }
            updatePageSection(numberOfRow);
            checkBookMark(numberOfRow);
            startSound("resources/bookPageFlip.mp3");
        }
    }

    private boolean switchView(Rectangle cover, boolean isShowed) {
        if (isShowed) {
            cover.setOpacity(0);
            startSound("resources/PlaySound.mp3");
            isShowed = false;
        } else {
            cover.setOpacity(1);
            isShowed = true;
        }
        return isShowed;
    }

    Image getImage(String filename) {
        FileInputStream input = null;
        try {
            input = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            System.out.println("filename = " + filename);
            e.printStackTrace();
        }
        return new Image(input);
    }

    void createMediaPlayer(int pageNumber) {
        String fileName = parts.get(pageNumber).getFilePath()+parts.get(pageNumber).getAudioFile();

        // Create & assign the audio file name.
        this.setMediaFile(false);
        if(!fileName.isEmpty() || !fileName.isBlank()) {
            File f = new File(fileName);
            try {
                this.setMediaPlayer(new Media(f.toURI().toString()));
                this.setMediaFile(true);
            } catch (MediaException e) {
                e.printStackTrace();
            }
        }

        // Create & assign the audio file name.
        String fileFullPath = parts.get(pageNumber).getFilePath()+parts.get(pageNumber).getExampleFile();
        this.setMediaFile2(false);
        if(!fileFullPath.isEmpty() || !fileFullPath.isBlank()) {
            File f = new File(fileFullPath);
            try {
                this.setMedia2Player(new Media(f.toURI().toString().stripTrailing()));
                this.setMediaFile2(true);
            } catch (MediaException e) {
                e.printStackTrace();
            }
        }
    }

     ImageView createImageViewByPath(String fileName, double height, double width, boolean preserveRatio) {
        Image image = getImage(fileName);
        ImageView view = new ImageView(image);
        view.setFitHeight(height);
        view.setFitWidth(width);
        view.setPreserveRatio(preserveRatio);
        return view;
    }

    protected void createToggleButtons() {
        backButton = new ToggleButton();
        homeButton = new ToggleButton();
        listening1Button = new ToggleButton();
        nextButton = new ToggleButton();
        startButton = new ToggleButton();
        statusButton = new ToggleButton();

        saveButton = new JFXToggleButton();
        saveButton.setText("File-Save");
        saveButton.setStyle("-jfx-toggle-color: #DAA520;");
        //Set the saving file option as default
        saveButton.setSelected(true);

        //Toggle button group
        ToggleGroup toggleGroup = new ToggleGroup();
        backButton.setToggleGroup(toggleGroup);
        startButton.setToggleGroup(toggleGroup);
        statusButton.setToggleGroup(toggleGroup);
        listening1Button.setToggleGroup(toggleGroup);
        homeButton.setToggleGroup(toggleGroup);
        nextButton.setToggleGroup(toggleGroup);
    }

    protected Scene rebuildForm(){
        createToggleButtons();
        createImageViews();

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
    }

    @Override
    void setupQuestions(int row) {
    }

    protected static void startSound(String fileName) {
        // Create & assign the audio file name.
        if (!fileName.isBlank() || !fileName.isEmpty()) {
            File mediaFile = new File(fileName);
            MediaPlayer mediaPlayer = new MediaPlayer(new Media(mediaFile.toURI().toString()));
            mediaPlayer.play();
        }
    }

    void updatePageSection(int pageNumber) {
        num.setText(parts.get(pageNumber).getItemNum());
        part.setText("Part:" +parts.get(pageNumber).getPart());
        pronunciationView.setImage(getImage(parts.get(pageNumber).getFilePath()+parts.get(pageNumber).getPronunciation()));
        pronunciationView.setPreserveRatio(true);
        pronunciationView.setFitWidth(200);
        pronunciationView.setFitHeight(30);

        word.setText(parts.get(pageNumber).getWord());
        imageView = createImageViewByPath(parts.get(pageNumber).getFilePath()+parts.get(pageNumber).getMeaning(),
                IMAGE_HEIGHT, IMAGE_WIDTH,true);
        toggle.setOn(false);
        // Renew the added previous image via index number(i.e. 0)
        toggle.getToggleArea().set(0, imageView);
        createMediaPlayer(pageNumber);

        updateRadioButtons(comprehensionStatusFile.retrieveStatus(parts.get(pageNumber).getItemNum()));
    }

    private void updateRadioButtons(String status) {
        if(status == null) {
            radio_100.setSelected(false);
            radio_50.setSelected(false);
            radio_zero.setSelected(false);
            icon.setVisible(false);
        } else {
            if(!icon.isVisible()) icon.setVisible(true);
            switch (status) {
                case "100%":
                    radio_100.setSelected(true);
                    break;
                case "50%":
                    radio_50.setSelected(true);
                    break;
                case "0%":
                    radio_zero.setSelected(true);
                    break;
            }
        }
    }

    private void updateComprehensionStatus(String selection) {
        compStatusRecord = comprehensionStatusFile.getCompStatusMap();
        if(!compStatusRecord.containsKey(parts.get(numberOfRow).getRowNumber())) {
            compStatusRecord.put(parts.get(numberOfRow).getItemNum(), selection);
        } else {
            compStatusRecord.replace(parts.get(numberOfRow).getItemNum(), selection);
        }
    }
}

class ToggleArea extends Parent {

    private boolean isBusy;
    private boolean isOn;
    private Rectangle background;

    private BooleanProperty switchedOn = new SimpleBooleanProperty(false);

    private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.9));
    private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.9));

    private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

    public BooleanProperty switchedOnProperty() {
        return switchedOn;
    }

    public ToggleArea(double width, double height, ImageView imageArea) {
        isBusy = false;
        background = new Rectangle(width,height);
        background.setFill(Color.rgb(210,0,0, 0.80));
        background.setStroke(Color.WHITE);

        Rectangle trigger = new Rectangle(0.5, height);
        trigger.setArcHeight(20);
        trigger.setArcWidth(20);
        trigger.setFill(Color.WHITE);
        trigger.setStroke(Color.TRANSPARENT);

        imageArea.setFitHeight(height);
        imageArea.setFitWidth(width);

        translateAnimation.setNode(trigger);
        fillAnimation.setShape(background);

        getChildren().addAll(imageArea, background, trigger);
        switchedOn.addListener((obs, oldState, newState) -> {
            boolean isOn = newState.booleanValue();
            translateAnimation.setToX(isOn ? width - 1 : 0);
            fillAnimation.setFromValue(isOn ? Color.rgb(210,0,0, 0.80) : Color.rgb(196,205,207, 0.3));
            fillAnimation.setToValue(isOn ? Color.rgb(196,205,207, 0.3) : Color.rgb(210,0,0, 0.80));

            animation.play();
            isBusy = true;
            startSound("resources/オートドア01.mp3");
        });

        animation.setOnFinished( e-> {
            isBusy = false;
        });

        setOnMouseClicked(event -> {
            if(!isBusy) {
                switchedOn.set(!switchedOn.get());
            }
        });
    }

    public void setOn(boolean off) {
        if(!isBusy) {
            switchedOn.set(off);
        }
    }

    public ObservableList<Node> getToggleArea() {
        return this.getChildren();
    }

}
