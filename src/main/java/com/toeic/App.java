package com.toeic;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

public class App extends Application {

    private static final int WIDTH = 380;
    private static final int HEIGHT = 570;

    public static final String PART1 = "1";
    public static final String PART2 = "2";
    public static final String PART3 = "3";
    public static final String PART4 = "4";
    public static final String PART5 = "5";
    public static final String PART6 = "6";
    public static final String PART7 = "7";

    public static final String VOC = "VOC";
    public static final String TITTLE = "TOEICeed\n Ver.1";

    private BooleanProperty isPart1FormReady = new SimpleBooleanProperty(false);
    private BooleanProperty isPart2FormReady = new SimpleBooleanProperty(false);
    private BooleanProperty isPart3FormReady = new SimpleBooleanProperty(false);
    private BooleanProperty isPart4FormReady = new SimpleBooleanProperty(false);
    private BooleanProperty isPart5FormReady = new SimpleBooleanProperty(false);
    private BooleanProperty isPart6FormReady = new SimpleBooleanProperty(false);
    private BooleanProperty isPart7FormReady = new SimpleBooleanProperty(false);
    private boolean isVocabularyReady = false;

    private ConfigurationProperties configProp;
    private CSVfileRead csVfileRead;
    private int numberOfRow = 0;
    private int totalRecords =0;
    private Pane root = new Pane();

    // Menu List data structure
    private List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("PART 1", () -> managePart1Creation()),
            new Pair<String, Runnable>("PART 2", () -> managePart2Creation()),
            new Pair<String, Runnable>("PART 3", () -> managePart3Creation()),
            new Pair<String, Runnable>("PART 4", () -> managePart4Creation()),
            new Pair<String, Runnable>("PART 5", () -> managePart5Creation()),
            new Pair<String, Runnable>("PART 6", () -> managePart6Creation()),
            new Pair<String, Runnable>("PART 7", () -> managePart7Creation()),
            new Pair<String, Runnable>("Vocabulary", () -> manageVocabulary()),
            new Pair<String, Runnable>("Exit", () -> callExit())
    );

    private MediaPlayer mediaPlayer;

    private PropertiesFile propFile;
    private Part1Form part1Form;
    private Part2Form part2Form;
    private Part3_4Form part3Form;
    private Part3_4Form part4Form;
    private Part5Form part5Form;
    private Part6_7Form part6Form;
    private Part6_7Form part67Form;

    private ToeiceedTitle title;

    private Vocabulary vocabulary;

    private VBox menuBox = new VBox(-5);
    private Line line;

    private Scene scene;
    private String partformNumber;
    private String fileName = "config.properties";

    private static Scene partScene;
    protected static Pane partLayout;

    private static HashMap<String, Scene> partSceneList;
    private static HashMap<String, Pane> partLayoutList;

    private ExcelFileOperations excelFileOperationt;

    private int numRecords = 0;
    private Properties property = null;
    private ProgressIndicator progressIndicator;
    private ResultManagement resultManagement;
    private ReviewManagement reviewManagement;

    private static Stage stage;

    private static Scene scene2;
    private static StackPane layout2;
    private Label labelStatus;

    public App() {
        partSceneList = new HashMap<>();
        partLayoutList = new HashMap<>();
    }

    /**
     * Getter/Setter
     * @return
     */
    public String getPartformNumber() {
        return partformNumber;
    }

    public void setPartformNumber(String partformNumber) {
        this.partformNumber = partformNumber;
    }

    public Pane getRoot() {
        return root;
    }

    private void addBackground() {
        //Generate a random number between 1 and 8
        int max = 8;
        int min = 1;
        int i = (int)(Math.random()*(max-min+1)+min);

        File imageFile = new File("resources/image" +i +".jpg");
        Image image = new Image(imageFile.toURI().toString());

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);

        root.getChildren().add(imageView);
    }

    private void addLine(double x, double y) {
        line = new Line(x, y, x, y + 300);
        line.setStrokeWidth(3);
        line.setStroke(Color.color(1, 1, 1, 0.75));
        line.setEffect(new DropShadow(5, Color.BLACK));
        line.setScaleY(0);
        root.getChildren().add(line);
    }

    private void addMenu(double x, double y) {
        menuBox.setTranslateX(x);
        menuBox.setTranslateY(y);
        menuData.forEach(data -> {
            ToeiceedMenuItem item = new ToeiceedMenuItem(data.getKey());
            item.setOnAction(data.getValue());
            item.setTranslateX(-300);

            Rectangle clip = new Rectangle(300, 30);
            clip.translateXProperty().bind(item.translateXProperty().negate());

            item.setClip(clip);
            menuBox.getChildren().addAll(item);
        });
        root.getChildren().add(menuBox);
    }

    private void addTitle() {
        title = new ToeiceedTitle(TITTLE);
        title.setTranslateX(WIDTH / 2 - title.getTitleWidth() / 2);
        title.setTranslateY(HEIGHT / 3);
        root.getChildren().add(title);
    }

    private final void callExit() {
        double lineY = HEIGHT / 3 + 50;
        VBox vbox = new VBox();
        // Add a spacer node to push the progressIndicator to the center
        vbox.setVgrow(new Region(), Priority.ALWAYS);
        Task<Void> task = new ExitProcess();
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        // Create a BorderPane to hold the VBox
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vbox);
        if(!thread.isAlive()) {
            thread.start();
            progressIndicator = new ProgressIndicator();
            progressIndicator.setPrefWidth(350);
            progressIndicator.progressProperty().bind(task.progressProperty());
            vbox.setTranslateX(WIDTH / 2 - 17);
            vbox.setTranslateY(lineY);
            vbox.getChildren().addAll(new Region(), progressIndicator);
            // Set the alignment of the border pane within the root
            borderPane.setAlignment(vbox, Pos.CENTER);
            root.getChildren().add(vbox);
        }
    }

    private class ExitProcess extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            menuBox.setDisable(true);
            try {
                excelFileOperationt.updateExcelFile("過去問.xlsm", "過去問");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Platform.exit();
            return null;
        }

        @Override
        protected void failed() {
            System.out.println("failed");
        }

        @Override
        protected void succeeded() {
            System.out.println("downloaded");
            menuBox.setDisable(false);
            progressIndicator.setVisible(false);
        }
    }

    private Parent createContents() {
        partScene = null;
        addBackground();
        addTitle();
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        // Add a spacer node to push the progressIndicator to the center
        vbox.setVgrow(new Region(), Priority.ALWAYS);

        // Create a BorderPane to hold the VBox
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vbox);

        double lineX = WIDTH / 2 - 100;
        double lineY = HEIGHT / 3 + 50;

        addLine(lineX, lineY + 20);

        labelStatus = new Label();
        Font font = new Font("Arial", 14); // create a new font with size 24
        font = Font.font(font.getFamily(), FontWeight.BOLD, font.getSize()); // make the font bold
        labelStatus.setFont(font); // set the font of the label
        // Set the label text color to #039ED3
        labelStatus.setStyle("-fx-text-fill: #039ED3;");

        Task<Void> task = new ExcelFileRead();
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        if(!thread.isAlive()) {
            thread.start();
            progressIndicator = new ProgressIndicator();
            progressIndicator.setPrefWidth(350);
            progressIndicator.progressProperty().bind(task.progressProperty());
            labelStatus.setText("Read: " + numRecords + " / " + totalRecords);
            vbox.getChildren().addAll(new Region(), progressIndicator, labelStatus);

            // Set the alignment of the border pane within the root
            borderPane.setAlignment(vbox, Pos.CENTER);

            // Translate the border pane to the desired position
            borderPane.setTranslateX(WIDTH / 2 - 65);
            borderPane.setTranslateY(lineY);
            root.getChildren().add(borderPane);
            addMenu(lineX + 5, lineY + 5);
            startAnimation();
        }
        stage.setResizable(false);
        return root;
    }

    private class ExcelFileRead extends Task<Void>  {
        public ExcelFileRead() {
            menuBox.setDisable(true);
        }

        @Override
        protected Void call() throws Exception {
            configProp = new ConfigurationProperties("resources/" +fileName);
            property = configProp.configureProperties();
            numberOfRow = Integer.parseInt(property.getProperty("rowNumber"));

            partformNumber = property.getProperty("partForm");
            resultManagement = new ResultManagement();
            reviewManagement = new ReviewManagement();
            excelFileOperationt = new ExcelFileOperations(property, resultManagement, reviewManagement);

            try {
                excelFileOperationt.createPartList("過去問.xlsm", "過去問", labelStatus);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void failed() {
            System.out.println("failed");
        }

        @Override
        protected void succeeded() {
            System.out.println("downloaded");
            menuBox.setDisable(false);
            progressIndicator.setVisible(false);
            labelStatus.setVisible(false);
        }
    }
     private class CreateVocabulary extends Task<Void> {

        public CreateVocabulary() {
            menuBox.setDisable(true);
        }

        @Override
        protected Void call() throws Exception {
            Properties property2 = null;
            configProp = new ConfigurationProperties("resources/vocabulary.properties");
            property2 = configProp.configureProperties();

            try {
                excelFileOperationt.assignProperties(property2);
                excelFileOperationt.createVocabularyList("キクタン.xlsm", "キクタン990");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(!excelFileOperationt.getVocabularyList().isEmpty()) {
                vocabulary = new Vocabulary(stage, root, numberOfRow, excelFileOperationt.getVocabularyList(), excelFileOperationt.getLastRowNum("VOL1"));

                partSceneList.put(VOC, vocabulary.rebuildForm());
                partLayoutList.put(VOC, vocabulary.getLayout());
            }
            return null;
        }
        @Override
        protected void succeeded() {
            isVocabularyReady = true;
            makeFadeOut(VOC, root);
            progressIndicator.setVisible(false);
            menuBox.setDisable(false);
        }
    }

    /**
     * prior to the FadeTranscation, need to switch the scene.
     * And then we need to set the layout's (i.e. StackPane) opacity to 0
     * in order to start from the invisible state for the target layout.
     */
    private static void makeFadeIn(String selectedItem) {
        partScene = partSceneList.get(selectedItem);
        stage.setScene(partScene);
        partLayout = partLayoutList.get(selectedItem);
        partLayout.setOpacity(0);

        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(1000));
        fadeTransition.setNode(partLayout);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    private void makeFadeOut(String selectedItem, Pane targetPane) {
        switch (selectedItem) {
            case PART6:
            case PART7:
                stage.setResizable(true);
                break;
            default:
                stage.setResizable(false);
        }
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(1000));
        fadeTransition.setNode(targetPane);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(e->makeFadeIn(selectedItem));
        fadeTransition.play();
    }

    /*
      manageFormCreation method takes advantage of Java's functional interfaces and
      the Callable interface to pass a block of code that performs the form creation
      and customization.

      Method accepts three parameters:
         formType: A string that represents the type of form you're creating.
         isFormReady: A BooleanProperty that tracks whether the form is ready.
         formCreationFunction: A Callable<Void> functional interface that encapsulates
         the code for creating the form.
     */
    private void manageFormCreation(String formType, BooleanProperty isFormReady, Callable<Void> formCreationFunction) {
        if (!isFormReady.get()) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    formCreationFunction.call();
                    return null;
                }

                @Override
                protected void succeeded() {
                    isFormReady.set(true);
                    makeFadeOut(formType, root);
                    progressIndicator.setVisible(false);
                    menuBox.setDisable(false);
                }
            };

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            progressIndicator.setVisible(true);

            if (!thread.isAlive()) {
                thread.start();
            }
        } else {
            makeFadeOut(formType, root);
        }
    }
    private void managePart1Creation() {
        manageFormCreation(
                PART1,
                isPart1FormReady,
                () -> {
                    if (!excelFileOperationt.getPart1List().isEmpty()) {
                        // Create and set up the Part1Form
                        part1Form = new Part1Form(stage, root, numberOfRow, excelFileOperationt.getPart1List(),
                                excelFileOperationt.getLastRowNum(PART1), excelFileOperationt.getHistoryRecordFile(), resultManagement, reviewManagement);

                        // Rebuild the Part1 Form contents
                        partSceneList.put(PART1, part1Form.rebuildForm());
                        partLayoutList.put(PART1, part1Form.getLayout());
                    }
                    return null;
                }
        );
    }
    private void managePart2Creation() {
        manageFormCreation(
                PART2,
                isPart2FormReady,
                () -> {
                    if (!excelFileOperationt.getPart2List().isEmpty()) {
                        // Create and set up the Part2Form
                        part2Form = new Part2Form(stage, root, numberOfRow, excelFileOperationt.getPart2List(),
                                excelFileOperationt.getLastRowNum(PART2), excelFileOperationt.getHistoryRecordFile(), resultManagement, reviewManagement);

                        // Rebuild the Part2 Form contents
                        partSceneList.put(PART2, part2Form.rebuildForm());
                        partLayoutList.put(PART2, part2Form.getLayout());
                    }
                    return null;
                }
        );
    }
    private void managePart3Creation() {
        manageFormCreation(
                PART3,
                isPart3FormReady,
                () -> {
                    if (!excelFileOperationt.getPart3List().isEmpty()) {
                        // Create and set up the Part3Form
                        part3Form = new Part3_4Form(stage, root, numberOfRow, excelFileOperationt.getPart3List(),
                                excelFileOperationt.getLastRowNum(PART3), excelFileOperationt.getHistoryRecordFile(), resultManagement, reviewManagement);

                        // Rebuild the Part3 Form contents
                        partSceneList.put(PART3, part3Form.rebuildForm());
                        partLayoutList.put(PART3, part3Form.getLayout());
                    }
                    return null;
                }
        );
    }
    private void managePart4Creation() {
        manageFormCreation(
                PART4,
                isPart4FormReady,
                () -> {
                    if (!excelFileOperationt.getPart4List().isEmpty()) {
                        // Create and set up the Part4Form
                        part4Form = new Part3_4Form(stage, root, numberOfRow, excelFileOperationt.getPart4List(),
                                excelFileOperationt.getLastRowNum(PART4), excelFileOperationt.getHistoryRecordFile(), resultManagement, reviewManagement);

                        // Rebuild the Part4 Form contents
                        partSceneList.put(PART4, part4Form.rebuildForm());
                        partLayoutList.put(PART4, part4Form.getLayout());
                    }
                    return null;
                }
        );
    }
    private void managePart5Creation() {
        manageFormCreation(
                PART5,
                isPart5FormReady,
                () -> {
                    if (!excelFileOperationt.getPart5List().isEmpty()) {
                        // Create and set up the Part5Form
                        part5Form = new Part5Form(stage, root, numberOfRow, excelFileOperationt.getPart5List(),
                                excelFileOperationt.getPart5ByCategoryListList(), excelFileOperationt.getLastRowNum(PART5),
                                excelFileOperationt.getHistoryRecordFile(), resultManagement, reviewManagement,
                                excelFileOperationt.getMarkPart5List(), excelFileOperationt.getLastRowNum("M5"));

                        // Rebuild the Part5 Form contents
                        partSceneList.put(PART5, part5Form.rebuildForm());
                        partLayoutList.put(PART5, part5Form.getLayout());
                    }
                    return null;
                }
        );
    }
    private void managePart6Creation() {
        manageFormCreation(
                PART6,
                isPart6FormReady,
                () -> {
                    if(!excelFileOperationt.getPart6List().isEmpty()) {
                        // Instant & set up the Part6Form
                        part6Form = new Part6_7Form(stage, root, numberOfRow, excelFileOperationt.getPart6List(),
                                excelFileOperationt.getLastRowNum(PART6), excelFileOperationt.getHistoryRecordFile(), resultManagement, reviewManagement);

                        // Rebuild the Part6 Form contents
                        partSceneList.put(PART6, part6Form.rebuildForm());
                        partLayoutList.put(PART6, part6Form.getLayout());
                        excelFileOperationt.addPartFormLayout(part6Form);
                    }
                    return null;
                }
        );
    }

    private void managePart7Creation() {
        manageFormCreation(
                PART7,
                isPart7FormReady,
                () -> {
                    if(!excelFileOperationt.getPart7List().isEmpty()) {
                        // Instant & set up the Part7Form
                        part67Form = new Part6_7Form(stage, root, numberOfRow, excelFileOperationt.getPart7List(),
                                excelFileOperationt.getLastRowNum(PART7), excelFileOperationt.getHistoryRecordFile(), resultManagement, reviewManagement);

                        // Rebuild the Part7 Form contents
                        part67Form.setProperty(property);
                        if (property.containsKey("part7ShowAnswer")) {
                            if (property.getProperty("part7ShowAnswer").matches("Y"))
                                part67Form.setPart7showAnswer(true);
                            else
                                part67Form.setPart7showAnswer(false);
                        }
                        partSceneList.put(PART7, part67Form.rebuildForm());
                        partLayoutList.put(PART7, part67Form.getLayout());
                        excelFileOperationt.addPartFormLayout(part67Form);
                    }
                    return null;
                }
        );
    }

    private void manageVocabulary() {
        if(!isVocabularyReady) {
            Task<Void> task = new CreateVocabulary();
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            progressIndicator.setVisible(true);
            if (!thread.isAlive()) {
                thread.start();
            }
        } else {
            makeFadeOut(VOC, root);
        }
    }

    private void startAnimation() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), line);
        scaleTransition.setToY(1);
        scaleTransition.setOnFinished(e -> {
            for (int i = 0; i < menuBox.getChildren().size(); i++) {
                Node node = menuBox.getChildren().get(i);
                TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1 + i * 0.15), node);
                translateTransition.setToX(0);
                translateTransition.setOnFinished(e2 -> {
                    node.setClip(null);
                    PartForm.startSound("resources/MechineMove00.mp3");
                });
                PartForm.startSound("resources/MechineMove01.mp3");
                translateTransition.play();
            }
        });
        scaleTransition.play();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        scene = new Scene(createContents());

        primaryStage.setTitle("Toeiceed Ver.1 Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setX(0.0);
        primaryStage.setY(0.0);

        File imageFile = new File("resources/saint_icon.png");
        Image image = new Image(imageFile.toURI().toString());
        primaryStage.getIcons().add(image);
    }

    public static void main(String[] args) {
        launch(args);
    }
}