package com.toeic;

import com.jfoenix.controls.JFXTabPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class FileVisitor extends SimpleFileVisitor<Path> {
    List<Path> files;

    public FileVisitor() {
        super();
        files = new ArrayList<>();
    }

    public List<Path> getFiles() {
        return files;
    }

    public FileVisitResult visitFile(Path path, BasicFileAttributes
            attributes) throws IOException {
        files.add(path);
        return FileVisitResult.CONTINUE;
    }
}

public class AlertBox {
    public static final String PAGE = "page";
    private int pageIndex;
    private List<Path> fileList;
    private FileVisitor fileVisitor;
    private ImageView imageView;
    private ImageView page;
    private int size = 20;
    private double screenHeight = 1400;
    private double screenWidth = 780;
    private Text text;
    private TextField pageNumber;

    private double windowHeight;
    private double windowWidth;

    private final static double MIN_SCALE = 0.2;
    private final static double MAX_SCALE = 2.0;

    private double scale = 1.0;

    public void setSize(int size) { this.size = size; }

    public double getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(double screenHeight) {
        this.screenHeight = screenHeight;
    }

    public double getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(double screenWidth) {
        this.screenWidth = screenWidth;
    }

    public void setWindowHeight(double windowHeight) {
        this.windowHeight = windowHeight;
    }

    public void setWindowWidth(double windowWidth) {
        this.windowWidth = windowWidth;
    }

    private void addEvent(Node node) {
        node.setOnZoom(e -> {
            node.setScaleX(node.getScaleX() * e.getZoomFactor());
            node.setScaleY(node.getScaleY() * e.getZoomFactor());
            e.consume();
        });

        node.setOnScroll(e -> {
            if (!e.isInertia()) {
                node.setTranslateX(node.getTranslateX() + e.getDeltaX());
                node.setTranslateY(node.getTranslateY() + e.getDeltaY());
            }
            e.consume();
        });
    }

    private void changePage(Group root, boolean preserveRatio ) throws IOException {
        Image image = getImage(fileList.get(pageIndex).toString());
        //renewPageNumber();

        page = new ImageView(image);
        page.setPreserveRatio(true);
        addEvent(page);

        FlowPane pane = new FlowPane(page);
        root.getChildren().add(pane);

        // 前のページが存在している場合は、削除する
        if (root.getChildren().size() > 1) {
            root.getChildren().remove(0);
        }
    }

    /**
     * The display method could manage to display a text, image or pdf format.
     * @param modality
     * @param attribute
     * @param message
     * @param filePath
     * @param preserveRatio
     */
    public void display(Modality modality, String attribute, String message, String filePath, boolean
                        preserveRatio) {
        Stage window = new Stage();

        window.toFront();
        window.initModality(modality);
        window.setMinWidth(250);
        window.setHeight(windowHeight);
        window.setWidth(windowWidth);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());

        VBox layout = getImageTextContents(attribute, message, filePath, preserveRatio, closeButton);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    public void display(Modality modality, String attribute, String message, String filePath, boolean
            preserveRatio, ToggleButton button) {
        Stage window = new Stage();

        window.toFront();
        window.initModality(modality);
        window.setMinWidth(250);
        window.setHeight(windowHeight);
        window.setWidth(windowWidth);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> {
                    window.close();
                    button.setDisable(false);
                });

        VBox layout = getImageTextContents(attribute, message, filePath, preserveRatio, closeButton);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    public void displayProgressIndicator(Modality modality, int percent, int numOfCompleted,
                                         int numOfIncomplete, boolean preserveRatio) {
        BackgroundFill bf;
        Background bk;
        Label completed;
        Label insufficient;

        bf = new BackgroundFill(Color.rgb(0, 147, 131),
                new CornerRadii(5) , Insets.EMPTY);
        bk = new Background(bf);

        completed = new Label();
        completed.setPadding(new Insets(5));
        completed.setFont(new Font("Arial", 12));
        completed.setTextFill(Color.WHITE);
        completed.setBackground(bk);
        completed.setText("Completed(" +numOfCompleted +")");

        insufficient = new Label();
        insufficient.setPadding(new Insets(5));
        insufficient.setFont(new Font("Arial", 12));
        insufficient.setTextFill(Color.WHITE);
        insufficient.setBackground(bk);
        insufficient.setText("Insufficient(" + numOfIncomplete +")");

        BorderPane topLayout = new BorderPane();
        topLayout.setLeft(completed);
        topLayout.setRight(insufficient);

        Stage window = new Stage();
        window.toFront();
        window.initModality(modality);
        window.setMinWidth(250);
        window.setHeight(windowHeight);
        window.setWidth(windowWidth);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());

        RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
        ringProgressIndicator.setRingWidth(200);
        ringProgressIndicator.setProgress(percent);

        StackPane root = new StackPane();
        root.getChildren().add(ringProgressIndicator);

        BorderPane layout = new BorderPane();

        layout.setTop(topLayout);
        layout.setCenter(ringProgressIndicator);
        BorderPane.setAlignment(closeButton, Pos.CENTER);
        layout.setBottom(closeButton);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    VBox getImageTextContents(String attribute, String message, String filePath, boolean preserveRatio, Button closeButton) {
        VBox layout = new VBox();

        if(attribute.matches("F")) {
            imageView = this.handleImageView(filePath + message, preserveRatio);
            layout.getChildren().addAll(imageView, closeButton);
            addEvent(imageView);
        } else {
            text = this.handleTextContents(message);
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setPannable(true);
            scrollPane.setPrefSize(800, 760);
            scrollPane.setContent(text);
            layout.getChildren().addAll(scrollPane, closeButton);
        }

        layout.setAlignment(Pos.CENTER);
        return layout;
    }


    VBox getImageTextContents(String attribute, String message, String filePath, boolean preserveRatio) {
        VBox layout = new VBox();

        if(attribute.matches("F")) {
            imageView = this.handleFlexibleImageView(filePath + message, preserveRatio);
            layout.getChildren().addAll(imageView);
            addEvent(imageView);
        } else {
            text = this.handleTextContents(message);
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setPannable(true);
            scrollPane.setPrefSize(800, 760);
            scrollPane.setContent(text);
            layout.getChildren().addAll(scrollPane);
        }

        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    VBox getImageTextContents(String attribute, String message, String filePath, boolean preserveRatio,
                              ImageView imageView, JFXTabPane jfxTabPane) {
        VBox layout = new VBox();

        if(attribute.matches("F")) {
            Image img = getImage(filePath + message);
            imageView = new ImageView(img);
            imageView.setPreserveRatio(preserveRatio);
            layout.getChildren().addAll(imageView);

            // Bind the fitWidth properties of the ImageView to the width of the JFXTabPane
            imageView.fitWidthProperty().bind(jfxTabPane.widthProperty());
       //     imageView.fitHeightProperty().bind(jfxTabPane.heightProperty());

            addEvent(imageView);
        } else {
            text = this.handleTextContents(message);
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setPannable(true);
            scrollPane.setPrefSize(800, 760);
            scrollPane.setContent(text);
            layout.getChildren().addAll(scrollPane);
        }

        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    public void displayFolder(Modality modality, String attribute, String message,
                              String filePath, String textbookFolder, boolean preserveRatio, PartForm partForm) {
        Scene scene;
        Group root;

        pageIndex = 0;
        root = new Group();
        Stage window = new Stage();
        window.initModality(modality);
        window.setMinWidth(250);
        window.setHeight(windowHeight);
        window.setWidth(windowWidth);

        prepareFileList(filePath+message);

        // Comparatorを実装した匿名クラス
        Comparator<Path> comparator = new Comparator<Path>() {
            @Override
            public int compare(Path o1, Path o2) {
                int page1Pos;
                int page2Pos;
                int addedPos;

                String o1String = o1.toString();
                String o2String = o2.toString();

                if (textbookFolder.isEmpty()) {
                    page1Pos = o1String.indexOf(PAGE);
                    page2Pos = o2String.indexOf(PAGE);
                    addedPos = PAGE.length() + 1;
                } else {
                    page1Pos = o1String.indexOf(textbookFolder);
                    page2Pos = o2String.indexOf(textbookFolder);
                    addedPos = textbookFolder.length() + 1;
                }

                String o1FileName = o1String.substring(page1Pos+addedPos);
                String o2FileName = o2String.substring(page2Pos+addedPos);

                int i1 = Integer.valueOf(o1FileName.substring(0,(o1FileName.indexOf("."))));
                int i2 = Integer.valueOf(o2FileName.substring(0,(o2FileName.indexOf("."))));
                return Integer.valueOf(i1).compareTo(Integer.valueOf(i2));
            }
        };

        // Perform to sort the fileList
        Collections.sort(fileList, comparator);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());

/*        VBox numberButtonArea = new VBox();

        // Reset the pageIndex
        pageIndex = 0;
        pageNumber = new TextField();
        pageNumber.setFont(new Font("Roboto Regular", 18));
        pageNumber.setPrefHeight(20);
        pageNumber.setPrefWidth(45);
        pageNumber.setDisable(true);

        numberButtonArea.getChildren().addAll(pageNumber, closeButton);

        renewPageNumber();*/

        // 最初のページを表示する
        try {
            changePage(root, preserveRatio);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StackPane pageArea = new StackPane();
        pageArea.setAlignment(Pos.CENTER);
        pageArea.getChildren().addAll(root, closeButton);

        pageArea.setAlignment(Pos.CENTER);
        scene = new Scene(pageArea);

        scene.setOnSwipeRight(e-> {
            try {
                // ページインデックスを進める
                // 最後までいったら最初に戻す
                pageIndex++;
                if (pageIndex >= fileList.size()) {
                    pageIndex = 0;
                }
                // マウスクリックされたら、次のページへ
                changePage(root, preserveRatio);
                partForm.startSound("resources/bookPageFlip.mp3");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        scene.setOnSwipeLeft(e-> {
            try {
                // ページインデックスを進める
                // 最後までいったら最初に戻す
                pageIndex--;
                if (pageIndex < 0) {
                    pageIndex = fileList.size() - 1;
                }
                // マウスクリックされたら、次のページへ
                changePage(root, preserveRatio);
                partForm.startSound("resources/bookPageFlip.mp3");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        window.setScene(scene);
        window.showAndWait();
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

    private ImageView handleFlexibleImageView(String filename, boolean preserveRatio) {
        double scaleX;
        double scaleY;
        double translationY = 0;

        // Calculate the scaling factor to fit the image within the screen
        Image img = getImage(filename);
        scaleX = screenWidth / img.getWidth();

        if(img.getHeight() > screenHeight) {
            scaleY = img.getHeight();
        } else {
            scaleY = screenHeight / img.getHeight();
        }

        double scale = Math.min(scaleX, scaleY);

        // Calculate Y-axis translation to ensure the top part of the image is visible
        if (img.getHeight() * scale > windowHeight) {
            translationY = (img.getHeight() * scale - windowHeight ) / 2;
        }

        ImageView imageView = new ImageView(img);
        // Apply initial scaling to fit the image within the screen
        imageView.setScaleX(scale);
        imageView.setScaleY(scale);
        imageView.setTranslateY(translationY);
        imageView.setPreserveRatio(preserveRatio);

        return imageView;
    }

    private ImageView handleFlexibleImageView(String filename, boolean preserveRatio, JFXTabPane jfxTabPane) {

        // Calculate the scaling factor to fit the image within the screen
        Image img = getImage(filename);

        ImageView imageView = new ImageView(img);
        imageView.setPreserveRatio(preserveRatio);
        return imageView;
    }
    ImageView handleImageView(String filename, boolean preserveRatio) {
        Image img = getImage(filename);

        ImageView imageView = new ImageView(img);
        imageView.setFitHeight(660);
        imageView.setFitWidth(780);
        imageView.setPreserveRatio(preserveRatio);
        return imageView;
    }

    Text handleTextContents(String sentence) {
        //Creating a text object
        Text text = new Text();

        //Setting the basic properties of text
        text.setText(sentence);
        text.setX(0);
        text.setY(0);
        text.setWrappingWidth(this.windowWidth - 50);

        /*
          Creating the font object
          On this computer environment, Font.getFamilies.get(0) returns
          Arial.
         */
        String font_name = Font.getFamilies().get(0);
        Font font = Font.font(font_name, FontWeight.NORMAL, FontPosture.REGULAR, size);

        //Setting font to the text
        text.setFont(font);

        //Setting the stage
        return text;
    }

    private void prepareFileList(String path) {
        try {
            fileVisitor = new FileVisitor();
            Files.walkFileTree(Paths.get(path), fileVisitor);
            fileList =  fileVisitor.getFiles();
        }
        catch (IOException e) {
            System.out.println("Error walking directory tree.");
        }
    }

    private void renewPageNumber() {
        pageNumber.setText(String.valueOf(pageIndex));
    }
}