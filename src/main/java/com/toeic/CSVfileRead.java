package com.toeic;

import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * CSVfileRead class
 */
public class CSVfileRead {
    private int partPos;
    private int pronunciationPos;
    private int audioFilePos;
    private int filePathPos;
    private int sourcePos;
    private int dayPos;
    private int examplePos;
    private int exampleFilePos;
    private int itemNumPos;
    private int meaningPos;
    private int priorityPos;
    private int statusPos;
    private int tittlePos;
    private int wordPos;

    private int part1Number = 0;
    private int part2Number = 0;
    private int part3Number = 0;
    private int part4Number = 0;
    private int part5Number = 0;
    private int part6Number = 0;
    private int part7Number = 0;
    private int vocaNumber = 0;

    private int vocab1 = 0;

    private List<Part> vocabularyList;

    private String fileName;

    /**
     * constructor
     * @param properties
     */
    public CSVfileRead(Properties properties, String fileName) {

        setFileName(fileName);

        if (properties.containsKey("partPos"))
            partPos = Integer.parseInt(properties.getProperty("partPos"));
        if (properties.containsKey("audioFilePos"))
            audioFilePos = Integer.parseInt(properties.getProperty("audioFilePos"));
        if (properties.containsKey("filePathPos"))
            filePathPos = Integer.parseInt(properties.getProperty("filePathPos"));

        if (properties.containsKey("sourcePos"))
            sourcePos = Integer.parseInt(properties.getProperty("sourcePos"));

        if (properties.containsKey("itemNumPos"))
            itemNumPos = Integer.parseInt(properties.getProperty("itemNumPos"));
        if (properties.containsKey("statusPos"))
            statusPos = Integer.parseInt(properties.getProperty("statusPos"));
        if (properties.containsKey("priority"))
            priorityPos = Integer.parseInt(properties.getProperty("priority"));
        if (properties.containsKey("dayPos"))
            dayPos = Integer.parseInt(properties.getProperty("dayPos"));
        if (properties.containsKey("wordPos"))
            wordPos = Integer.parseInt(properties.getProperty("wordPos"));
        if (properties.containsKey("tittle"))
            tittlePos = Integer.parseInt(properties.getProperty("tittle"));
        if (properties.containsKey("meaningPos"))
            meaningPos = Integer.parseInt(properties.getProperty("meaningPos"));
        if (properties.containsKey("examplePos"))
            examplePos = Integer.parseInt(properties.getProperty("examplePos"));
        if (properties.containsKey("exampleFilePos"))
            exampleFilePos = Integer.parseInt(properties.getProperty("exampleFilePos"));
        if (properties.containsKey("pronunciationPos"))
            pronunciationPos = Integer.parseInt(properties.getProperty("pronunciationPos"));
    }

    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public int getLastRowNum(String part) {

        int lastRowNumber = 0;
        switch (part) {
            case "1" :
                lastRowNumber =part1Number;
                break;
            case "2" :
                lastRowNumber =part2Number;
                break;
            case "3" :
                lastRowNumber =part3Number;
                break;
            case "4" :
                lastRowNumber = part4Number;
                break;
            case "5" :
                lastRowNumber= part5Number;
                break;
            case "6" :
                lastRowNumber = part6Number;
                break;
            case "7" :
                lastRowNumber = part7Number;
                break;
            case "VOL1" :
                lastRowNumber = vocab1;
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + part);
        }
        return lastRowNumber;
    }

    public List<Part> getVocabularyList() {
        return vocabularyList;
    }

    protected void createVocabularyList() throws FileNotFoundException {
        List<String> lines = new ArrayList<>();

        boolean isVocabulary = true;
        String audioFile = null;
        String day = null;
        String example = null;
        String exampleFile = null;
        String filePath = null;
        String itemNum = null;
        String meaning = null;
        String priority = null;
        String part = null;
        String pronunciation = null;
        String source = null;
        String status = null;
        String tittle = null;
        String word = null;

        int rowNumber = 0;

        // create scanner instance
        try (Scanner scanner = new Scanner(new FileInputStream(getFileName()))) {

            // CSV file delimiter
            String DELIMITER = "\t";

            // set comma as delimiter
            scanner.useDelimiter(DELIMITER);

            // read all fields
            while (scanner.hasNext()) {
                lines.add(scanner.next());
            }

        }

        vocabularyList = new ArrayList<>();

        int colNum = 0;

        for (int i = 0; i < lines.size(); ++i) {
            if (colNum == this.sourcePos) {
                source = lines.get(i);
            } else if (colNum == this.itemNumPos) {
                itemNum = lines.get(i);
            } else if (colNum == this.statusPos) {
                status = lines.get(i);
            } else if (colNum == this.priorityPos) {
                priority = lines.get(i);
            } else if (colNum == this.dayPos) {
                day = lines.get(i);
            } else if (colNum == this.partPos) {
                part = lines.get(i);
            } else if (colNum == this.pronunciationPos) {
                pronunciation = lines.get(i);
            } else if (colNum == this.wordPos) {
                word = lines.get(i);
            } else if (colNum == this.tittlePos) {
                tittle = lines.get(i);
            } else if (colNum == this.audioFilePos) {
                audioFile = lines.get(i);
            } else if (colNum == this.meaningPos) {
                meaning = lines.get(i);
            } else if (colNum == this.examplePos) {
                example = lines.get(i);
            } else if (colNum == this.filePathPos) {
                filePath = lines.get(i);
            } else if (colNum == this.exampleFilePos) {
                exampleFile = lines.get(i);
            }

            /**
             * By eol (\r\n), store the column records to the partList.
             * Note that as the \r\n does also contain 1st column data,
             * we need to reset the colNum as 1 (not 0) to subtract 1st column
             * field.
             */
            if (lines.get(i).contains("\r\n")) {
                colNum = 1;

                if (source.contains("TOEIC TEST SCORE 990")) {
                        vocabularyList.add(new Part(String.valueOf(vocaNumber), source, itemNum, status, priority, day, part,
                                           word, tittle, audioFile, meaning, example, filePath, exampleFile,
                                           pronunciation, isVocabulary));
                        ++vocaNumber;
                }
            } else {
                ++colNum;
            }
        }
    }

}