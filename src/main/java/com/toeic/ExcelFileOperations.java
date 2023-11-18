package com.toeic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static java.time.LocalTime.now;

public class ExcelFileOperations {
    public static final String OPENING_WORKBOOK = "Opening Worksheet...";
    private FileInputStream file;

    private int totalPos = 0;
    private int answerPos;
    private int answeredPos;
    private int audioFilePos;
    private int bookMarkedPos;
    private int categoryPos;
    private int filePathPos;
    private int dialogFilePos;
    private int dialogAttribPos;
    private int expAttribPos;
    private int explanationPos;
    private int partPos;
    private int pronunciationPos;
    private int questionPos;
    private int questAttribPos;
    private int questionNumPos;
    private int fullTimePos;
    private int resultPos;
    private int resultDatePos;
    private int reviewPos;
    private int scrAttribPos;
    private int scrPreserveRatioPos;
    private int scriptPos;
    private int sourcePos;
    private int timePos;
    private int triplePassage;
    private int triplePassAttrib;
    private int textbookPos;
    private int textbookAttrib;
    private int textbookFolderPos;

    private int toggledBookMarkPos;

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
    private int markPart5Number = 0;
    private int vocaNumber = 0;

    private HistoryRecordFile historyRecordFile;
    private ResultManagement resultManagement;
    private ReviewManagement reviewManagement;
    private List<Part6_7Form> partFormList;

    private List<Part> part1List;
    private List<Part> part2List;
    private List<Part> part3List;
    private List<Part> part4List;
    private List<Part> part5List;
    private List<Part> part6List;
    private List<Part> part7List;
    private List<Part> markPart5List;
    private List<Part> categoryList;
    private List<Part> vocabularyList;

    private Sheet sheet;
    private String fileName;
    private Workbook workbook;

    public ExcelFileOperations(Properties properties, ResultManagement resultManagement,
                               ReviewManagement reviewManagement) {
        this.reviewManagement = reviewManagement;
        this.resultManagement = resultManagement;
        assignProperties(properties);
        partFormList = new ArrayList<>();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Part> getPart1List() {
        part1List.sort(Part.BY_SOURCE);
        return part1List;
    }

    public List<Part> getPart2List() {
        part2List.sort(Part.BY_SOURCE);
        return part2List;
    }

    public List<Part> getPart3List() {
        part3List.sort(Part.BY_SOURCE);
        return part3List;
    }

    public List<Part> getPart4List() {
        part4List.sort(Part.BY_SOURCE);
        return part4List;
    }

    public List<Part> getPart5List() {
        part5List.sort(Part.BY_SOURCE);
        return part5List;
    }

    public List<Part> getPart6List() {
        part6List.sort(Part.BY_SOURCE);
        return part6List;
    }

    public List<Part> getPart7List() {
        part7List.sort(Part.BY_SOURCE);
        return part7List;
    }

    public List<Part> getPart5ByCategoryListList() {
        categoryList.sort(Part.BY_CATEGORY);
        return categoryList;
    }

    public List<Part> getMarkPart5List() {
        markPart5List.sort(Part.BY_SOURCE);
        return markPart5List;
    }

    public int getLastRowNum(String part) {

        int lastRowNumber = 0;
        switch (part) {
            case "1":
                lastRowNumber = part1Number;
                break;
            case "2":
                lastRowNumber = part2Number;
                break;
            case "3":
                lastRowNumber = part3Number;
                break;
            case "4":
                lastRowNumber = part4Number;
                break;
            case "5":
                lastRowNumber = part5Number;
                break;
            case "6":
                lastRowNumber = part6Number;
                break;
            case "7":
                lastRowNumber = part7Number;
                break;
            case "M5":
                lastRowNumber = markPart5Number;
                break;
            case "VOL1":
                lastRowNumber = vocaNumber;
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + part);
        }
        return lastRowNumber;
    }

    public List<Part> getVocabularyList() {
        return vocabularyList;
    }

    public HistoryRecordFile getHistoryRecordFile() {
        return historyRecordFile;
    }

    public int getLastRow() {
        return sheet.getLastRowNum();
    }

    public void addPartFormLayout(Part6_7Form timerLayout) {
        partFormList.add(timerLayout);
    }

    void assignProperties(Properties properties) {
        if (properties.containsKey("partPos"))
            partPos = Integer.parseInt(properties.getProperty("partPos"));
        if (properties.containsKey("fullTimePos"))
            fullTimePos = Integer.parseInt(properties.getProperty("fullTimePos"));
        if (properties.containsKey("questionPos"))
            questionPos = Integer.parseInt(properties.getProperty("questionPos"));
        if (properties.containsKey("questAttribPos"))
            questAttribPos = Integer.parseInt(properties.getProperty("questAttribPos"));
        if (properties.containsKey("audioFilePos"))
            audioFilePos = Integer.parseInt(properties.getProperty("audioFilePos"));
        if (properties.containsKey("filePathPos"))
            filePathPos = Integer.parseInt(properties.getProperty("filePathPos"));
        if (properties.containsKey("dialogFilePos"))
            dialogFilePos = Integer.parseInt(properties.getProperty("dialogFilePos"));
        if (properties.containsKey("dialogAttribPos"))
            dialogAttribPos = Integer.parseInt(properties.getProperty("dialogAttribPos"));
        if (properties.containsKey("answerPos"))
            answerPos = Integer.parseInt(properties.getProperty("answerPos"));
        if (properties.containsKey("expAttribPos"))
            expAttribPos = Integer.parseInt(properties.getProperty("expAttribPos"));
        if (properties.containsKey("explanationPos"))
            explanationPos = Integer.parseInt(properties.getProperty("explanationPos"));
        if (properties.containsKey("scrAttribPos"))
            scrAttribPos = Integer.parseInt(properties.getProperty("scrAttribPos"));
        if (properties.containsKey("scriptPreserveRatio"))
            scrPreserveRatioPos = Integer.parseInt(properties.getProperty("scriptPreserveRatio"));
        if (properties.containsKey("scriptPos"))
            scriptPos = Integer.parseInt(properties.getProperty("scriptPos"));
        if (properties.containsKey("sourcePos"))
            sourcePos = Integer.parseInt(properties.getProperty("sourcePos"));
        if (properties.containsKey("categoryPos"))
            categoryPos = Integer.parseInt(properties.getProperty("categoryPos"));
        if (properties.containsKey("questionNumPos"))
            questionNumPos = Integer.parseInt(properties.getProperty("questionNumPos"));
        if (properties.containsKey("triplePassage"))
            triplePassage = Integer.parseInt(properties.getProperty("triplePassage"));
        if (properties.containsKey("triplePassAttrib"))
            triplePassAttrib = Integer.parseInt(properties.getProperty("triplePassAttrib"));
        if (properties.containsKey("textbookPos"))
            textbookPos = Integer.parseInt(properties.getProperty("textbookPos"));
        if (properties.containsKey("textbookAttrib"))
            textbookAttrib = Integer.parseInt(properties.getProperty("textbookAttrib"));
        if (properties.containsKey("textbookFolderPos"))
            textbookFolderPos = Integer.parseInt(properties.getProperty("textbookFolderPos"));
        if (properties.containsKey("bookMarked"))
            bookMarkedPos = Integer.parseInt(properties.getProperty("bookMarked"));
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
        if (properties.containsKey("bookMarked"))
            bookMarkedPos = Integer.parseInt(properties.getProperty("bookMarked"));
        if (properties.containsKey("resultPos"))
            resultPos = Integer.parseInt(properties.getProperty("resultPos"));
        if (properties.containsKey("resultDatePos"))
            resultDatePos = Integer.parseInt(properties.getProperty("resultDatePos"));
        if (properties.containsKey("timePos"))
            timePos = Integer.parseInt(properties.getProperty("timePos"));
        if (properties.containsKey("reviewPos"))
            reviewPos = Integer.parseInt(properties.getProperty("reviewPos"));
        if (properties.containsKey("toggledBookMarkPos"))
            toggledBookMarkPos = Integer.parseInt(properties.getProperty("toggledBookMarkPos"));
        if (properties.containsKey("answeredPos"))
            answeredPos = Integer.parseInt(properties.getProperty("answeredPos"));

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

    /*
     * This method reads data for all row from an Excel Sheet
     * @parameter:
     * @filePath: Complete path of the Excel File along with file name
     * @SheetName: Sheet Name to read
     * @return:Excel Sheet Cell values
     */
    protected void createPartList(String excelFile, String sheetName, Label labelStatus) throws IOException {
        List<String> lines = new ArrayList<>();

        historyRecordFile = new HistoryRecordFile();

        String answer;
        String answered;
        String bookMarked;
        String dialogFile;
        String dialogAttrib;
        String fileName;
        String filePath;
        String explanation;
        String expAttrib;
        String itemNum;
        String partPos;
        String question;
        String questAttrib;
        String fullTime;
        String script;
        String scrAttrib;
        String scrPreserveRatio;
        String source;
        String category;
        String questionNum;
        String triplePassage;
        String triplePassAttrib;
        String textbookPos;
        String textbookAttrib;
        String textbookFolder;
        String toggledBookMark;
        String result;
        String resultDate;
        String review;

        categoryList = new ArrayList<>();
        markPart5List = new ArrayList<>();

        part1List = new ArrayList<>();
        part2List = new ArrayList<>();
        part3List = new ArrayList<>();
        part4List = new ArrayList<>();
        part5List = new ArrayList<>();
        part6List = new ArrayList<>();
        part7List = new ArrayList<>();

        this.createWorkbookandSheet(excelFile, sheetName, labelStatus);

        //Get total number of rows in the particular Excel Sheet
        int rowCount = sheet.getLastRowNum();
        String cellValue = null;
        int i = 0;

        int colNum;
        //Creating an object of FormulaEvaluator class to get the cell type information
        //This is quite helpful to understand which cell contains what type of data like String,Integer,double etc
        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        //Create a loop to iterate through all the rows of the Excel sheet to read it
        for (; i < rowCount + 1; i++) {


            // Update the progress indicator with the new progress
            int numOfrow = i;
            Platform.runLater(() -> {
                labelStatus.setText("Completed: " + numOfrow + " / " + rowCount);
            });

            // re-set variables
            answer = "";
            answered= "";
            bookMarked = "";
            dialogFile = "";
            dialogAttrib = "";
            fileName = "";
            filePath = "";
            fullTime = "";
            explanation = "";
            expAttrib = "";
            itemNum = "";
            partPos = "";
            question = "";
            questAttrib = "";
            script = "";
            scrAttrib = "";
            scrPreserveRatio = "";
            source = "";
            category = "";
            questionNum = "";
            triplePassage = "";
            triplePassAttrib = "";
            textbookPos = "";
            textbookAttrib = "";
            textbookFolder = "";
            toggledBookMark = "";
            result = "";
            resultDate = "";
            review = "";

            Row row = sheet.getRow(i);

            //Create a loop to print all cell values in a row
            for (colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                //Printing Excel data for the current row in the console

                Cell cell;
                cell = row.getCell(colNum);

                if (cell != null) {
                    switch (formulaEvaluator.evaluateInCell(row.getCell(colNum)).getCellType()) {
                        case NUMERIC: //If the cell has numeric values is number or Date
                            //Check if the cell contains Date Value
                            if (DateUtil.isCellDateFormatted(row.getCell(colNum))) {
                                //formatting cell using DataFormatter class
                                DataFormatter dataFormatter = new DataFormatter();
                                cellValue = dataFormatter.formatCellValue(row.getCell(colNum));

                            } else//If the cell contains integer or double value
                            {
                                Double doubleNumber = row.getCell(colNum).getNumericCellValue();

                                //Converting double value to String
                                String doubleNumAsString = String.valueOf(doubleNumber);

                                //Separating integer and decimal part if the numeric value contains decimal values
                                int indexOfDecimal = doubleNumAsString.indexOf(".");
                                String intPart = doubleNumAsString.substring(0, indexOfDecimal);
                                String decimalPart = doubleNumAsString.substring(indexOfDecimal);

                                double decimalValue = Double.parseDouble(decimalPart);
                                if (decimalValue == 0)
                                    cellValue = intPart;
                                else
                                    cellValue = doubleNumAsString;
                            }
                            break;

                        case STRING://If the cell has plain text value
                            cellValue = row.getCell(colNum).getStringCellValue();
                            break;
                        case BLANK: //If the cell has empty value
                            cellValue = "";
                            break;
                        case _NONE:
                            cellValue = "";
                            break;
                        case BOOLEAN://If the cell has boolean value
                            cellValue = String.valueOf(row.getCell(colNum).getBooleanCellValue());
                            break;
                        default:
                            break;
                    }

                    if (colNum == this.itemNumPos) {
                        itemNum = cellValue;
                    } else if (colNum == this.partPos) {
                        partPos = cellValue;
                    } else if (colNum == this.questionPos) {
                        question = cellValue;
                    } else if (colNum == this.questAttribPos) {
                        questAttrib = cellValue;
                    } else if (colNum == audioFilePos) {
                        fileName = cellValue;
                    } else if (colNum == this.filePathPos) {
                        filePath = cellValue;
                    } else if (colNum == this.dialogFilePos) {
                        dialogFile = cellValue;
                    } else if (colNum == this.dialogAttribPos) {
                        dialogAttrib = cellValue;
                    } else if (colNum == this.answerPos) {
                        answer = cellValue;
                    } else if (colNum == this.explanationPos) {
                        explanation = cellValue;
                    } else if (colNum == this.expAttribPos) {
                        expAttrib = cellValue;
                    } else if (colNum == this.scriptPos) {
                        script = cellValue;
                    } else if (colNum == this.scrAttribPos) {
                        scrAttrib = cellValue;
                    } else if (colNum == this.sourcePos) {
                        source = cellValue;
                    } else if (colNum == this.fullTimePos) {
                        fullTime = cellValue;
                    } else if (colNum == this.categoryPos) {
                        category = cellValue;
                    } else if (colNum == this.questionNumPos) {
                        questionNum = cellValue;
                    } else if (colNum == this.triplePassage) {
                        triplePassage = cellValue;
                    } else if (colNum == this.triplePassAttrib) {
                        triplePassAttrib = cellValue;
                    } else if (colNum == this.scrPreserveRatioPos) {
                        scrPreserveRatio = cellValue;
                    } else if (colNum == this.textbookPos) {
                        textbookPos = cellValue;
                    } else if (colNum == this.textbookAttrib) {
                        textbookAttrib = cellValue;
                    } else if (colNum == this.textbookFolderPos) {
                        textbookFolder = cellValue;
                    } else if (colNum == this.bookMarkedPos) {
                        bookMarked = cellValue;
                    } else if (colNum == this.resultPos) {
                        result = cellValue;
                    } else if (colNum == this.resultDatePos) {
                        resultDate = cellValue;
                    } else if (colNum == this.reviewPos) {
                        review = cellValue;
                    } else if (colNum == this.toggledBookMarkPos) {
                        toggledBookMark = cellValue;
                    } else if (colNum == this.answeredPos) {
                        answered = cellValue;
                    }
                }
            }

            if (!fullTime.contains("skip")) {

                if (bookMarked.matches("Y")) {
                    historyRecordFile.addHistoryRecord(itemNum, partPos, bookMarked, toggledBookMark);
                }

                if (partPos.contains("1")) {
                    part1List.add(new Part(itemNum, partPos, question, answer, fileName, filePath, expAttrib,
                            explanation, scrAttrib, script, String.valueOf(part1Number), source, dialogFile,
                            dialogAttrib, questionNum, triplePassage, triplePassAttrib,
                            textbookPos, textbookAttrib, textbookFolder,
                            scrPreserveRatio, category, result, resultDate, review, questAttrib,
                            fullTime));
                    ++part1Number;
                } else if (partPos.contains("2")) {
                    part2List.add(new Part(itemNum, partPos, question, answer, fileName, filePath, expAttrib,
                            explanation, scrAttrib, script, String.valueOf(part2Number), source, dialogFile,
                            dialogAttrib, questionNum, triplePassage, triplePassAttrib,
                            textbookPos, textbookAttrib, textbookFolder,
                            scrPreserveRatio, category, result, resultDate, review, questAttrib,
                            fullTime));
                    ++part2Number;
                } else if (partPos.contains("3")) {
                    part3List.add(new Part(itemNum, partPos, question, answer, fileName, filePath, expAttrib,
                            explanation, scrAttrib, script, String.valueOf(part3Number), source, dialogFile,
                            dialogAttrib, questionNum, triplePassage, triplePassAttrib,
                            textbookPos, textbookAttrib, textbookFolder,
                            scrPreserveRatio, category, result, resultDate, review, questAttrib,
                            fullTime));
                    ++part3Number;
                } else if (partPos.contains("4")) {
                    part4List.add(new Part(itemNum, partPos, question, answer, fileName, filePath, expAttrib,
                            explanation, scrAttrib, script, String.valueOf(part4Number), source, dialogFile,
                            dialogAttrib, questionNum, triplePassage, triplePassAttrib,
                            textbookPos, textbookAttrib, textbookFolder,
                            scrPreserveRatio, category, result, resultDate, review, questAttrib,
                            fullTime));
                    ++part4Number;
                } else if (partPos.contains("5")) {
                    part5List.add(new Part(itemNum, partPos, question, answer, fileName, filePath, expAttrib,
                            explanation, scrAttrib, script, String.valueOf(part5Number), source, dialogFile,
                            dialogAttrib, questionNum, triplePassage, triplePassAttrib,
                            textbookPos, textbookAttrib, textbookFolder,
                            scrPreserveRatio, category, result, resultDate, review, questAttrib,
                            fullTime));

                    if (review.matches("Mark")) {
                        markPart5List.add(new Part(itemNum, partPos, question, answer, fileName, filePath, expAttrib,
                                explanation, scrAttrib, script, String.valueOf(part5Number), source, dialogFile,
                                dialogAttrib, questionNum, triplePassage, triplePassAttrib,
                                textbookPos, textbookAttrib, textbookFolder,
                                scrPreserveRatio, category, result, resultDate, review, questAttrib,
                                fullTime));
                        ++markPart5Number;
                    }

                    categoryList.add(new Part(itemNum, partPos, question, answer, fileName, filePath, expAttrib,
                            explanation, scrAttrib, script, String.valueOf(part5Number), source, dialogFile,
                            dialogAttrib, questionNum, triplePassage, triplePassAttrib,
                            textbookPos, textbookAttrib, textbookFolder,
                            scrPreserveRatio, category, result, resultDate, review, questAttrib,
                            fullTime));
                    ++part5Number;
                } else if (partPos.contains("6")) {
                    part6List.add(new Part(itemNum, partPos, question, answer, fileName, filePath, expAttrib,
                            explanation, scrAttrib, script, String.valueOf(part6Number), source, dialogFile,
                            dialogAttrib, questionNum, triplePassage, triplePassAttrib,
                            textbookPos, textbookAttrib, textbookFolder,
                            scrPreserveRatio, category, result, resultDate, review, questAttrib,
                            fullTime));
                    ++part6Number;
                } else if (partPos.contains("7")) {
                    part7List.add(new Part(itemNum, partPos, question, answer, fileName, filePath, expAttrib,
                            explanation, scrAttrib, script, String.valueOf(part7Number), source, dialogFile,
                            dialogAttrib, questionNum, triplePassage, triplePassAttrib,
                            textbookPos, textbookAttrib, textbookFolder,
                            scrPreserveRatio, category, result, resultDate, review, questAttrib,
                            fullTime));
                    ++part7Number;
                }
            }
        }
    }

    protected void createVocabularyList(String excelFile, String sheetName) throws IOException {
        List<String> lines = new ArrayList<>();

        boolean isVocabulary = true;
        String audioFile;
        String day;
        String example;
        String exampleFile;
        String filePath;
        String itemNum;
        String meaning;
        String priority;
        String part;
        String pronunciation;
        String source;
        String status;
        String tittle;
        String word;

        vocabularyList = new ArrayList<>();
        Label dummy = new Label();

        this.createWorkbookandSheet(excelFile, sheetName, dummy);

        //Get total number of rows in the particular Excel Sheet
        int rowCount = sheet.getLastRowNum();
        String cellValue = null;

        //Creating an object of FormulaEvaluator class to get the cell type information
        //This is quite helpful to understand which cell contains what type of data like String,Integer,double etc
        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        //Create a loop to iterate through all the rows of the Excel sheet to read it
        for (int i = 0; i < rowCount + 1; i++) {
            audioFile = "";
            day = "";
            example = "";
            exampleFile = "";
            filePath = "";
            itemNum = "";
            meaning = "";
            priority = "";
            part = "";
            pronunciation = "";
            source = "";
            status = "";
            tittle = "";
            word = "";

            Row row = sheet.getRow(i);

            //Create a loop to print all cell values in a row
            for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                //Printing Excel data for the current row in the console

                Cell cell;
                cell = row.getCell(colNum);

                if (cell != null) {
                    switch (formulaEvaluator.evaluateInCell(row.getCell(colNum)).getCellType()) {
                        case NUMERIC: //If the cell has numeric values is number or Date
                            //Check if the cell contains Date Value
                            if (DateUtil.isCellDateFormatted(row.getCell(colNum))) {
                                //formatting cell using DataFormatter class
                                DataFormatter dataFormatter = new DataFormatter();
                                cellValue = dataFormatter.formatCellValue(row.getCell(colNum));

                            } else//If the cell contains integer or double value
                            {
                                Double doubleNumber = row.getCell(colNum).getNumericCellValue();

                                //Converting double value to String
                                String doubleNumAsString = String.valueOf(doubleNumber);

                                //Separating integer and decimal part if the numeric value contains decimal values
                                int indexOfDecimal = doubleNumAsString.indexOf(".");
                                String intPart = doubleNumAsString.substring(0, indexOfDecimal);
                                String decimalPart = doubleNumAsString.substring(indexOfDecimal);

                                double decimalValue = Double.parseDouble(decimalPart);
                                if (decimalValue == 0)
                                    cellValue = intPart;
                                else
                                    cellValue = doubleNumAsString;
                            }
                            break;

                        case STRING://If the cell has plain text value
                            cellValue = row.getCell(colNum).getStringCellValue();
                            break;
                        case BLANK: //If the cell has empty value
                            cellValue = "";
                            break;
                        case _NONE:
                            cellValue = "";
                            break;
                        case BOOLEAN://If the cell has boolean value
                            cellValue = String.valueOf(row.getCell(colNum).getBooleanCellValue());
                            break;
                        default:
                            break;
                    }

                    if (colNum == this.sourcePos) {
                        source = cellValue;
                    } else if (colNum == this.itemNumPos) {
                        itemNum = cellValue;
                    } else if (colNum == this.statusPos) {
                        status = cellValue;
                    } else if (colNum == this.priorityPos) {
                        priority = cellValue;
                    } else if (colNum == this.dayPos) {
                        day = cellValue;
                    } else if (colNum == this.partPos) {
                        part = cellValue;
                    } else if (colNum == this.pronunciationPos) {
                        pronunciation = cellValue;
                    } else if (colNum == this.wordPos) {
                        word = cellValue;
                    } else if (colNum == this.tittlePos) {
                        tittle = cellValue;
                    } else if (colNum == this.audioFilePos) {
                        audioFile = cellValue;
                    } else if (colNum == this.meaningPos) {
                        meaning = cellValue;
                    } else if (colNum == this.examplePos) {
                        example = cellValue;
                    } else if (colNum == this.filePathPos) {
                        filePath = cellValue;
                    } else if (colNum == this.exampleFilePos) {
                        exampleFile = cellValue;
                    }
                }
            }

            if (source.contains("TOEIC TEST SCORE 990")) {
                vocabularyList.add(new Part(String.valueOf(vocaNumber), source, itemNum, status, priority, day, part,
                        word, tittle, audioFile, meaning, example, filePath, exampleFile,
                        pronunciation, isVocabulary));
                ++vocaNumber;
            }
        }
    }

    private void createWorkbookandSheet(String excelFile, String sheetName, Label labelStatus) throws IOException {
        // Open the specified excel file.


        file = new FileInputStream(excelFile);
        workbook = null;


        //Find the file extension by splitting file name in substring and get only extension name
        String fileExtensionName = excelFile.substring(excelFile.indexOf("."));

        // The program could cause the zipbomb error due to exceeding the max. ratio of compressed
        // file size to the size of the expanded data. To cope with the error,  set the MinInflateRatio.
        ZipSecureFile.setMinInflateRatio(0.0d);

        //If the file is xlsx file
        if (fileExtensionName.equals(".xlsx") || fileExtensionName.equals(".xlsm")) {
            Platform.runLater(() -> {
                labelStatus.setText(OPENING_WORKBOOK);
            });
            //For xlsx file create object of XSSFWorkbook class
            workbook = new XSSFWorkbook(file);
        }
        //if the file is xls file
        else if (fileExtensionName.equals(".xls")) {
            Platform.runLater(() -> {
                labelStatus.setText(OPENING_WORKBOOK);
            });
            //For xls file create object of HSSFWorkbook class
            workbook = new HSSFWorkbook(file);
        }

        //Creating an object of FormulaEvaluator class to get the cell type information
        //This is quite helpful to understand which cell contains what type of data like String,Integer,double etc
        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

        //Read sheet inside the workbook by its name
        sheet = workbook.getSheet(sheetName);
    }

    private String retrieveCellValue(CellType cellType, Row row, int colNum) {
        String cellValue = null;

        switch (cellType) {
            case NUMERIC: //If the cell has numeric values is number or Date
                //Check if the cell contains Date Value
                if (DateUtil.isCellDateFormatted(row.getCell(colNum))) {
                    //formatting cell using DataFormatter class
                    DataFormatter dataFormatter = new DataFormatter();
                    cellValue = dataFormatter.formatCellValue(row.getCell(colNum));

                } else//If the cell contains integer or double value
                {
                    Double doubleNumber = row.getCell(colNum).getNumericCellValue();

                    //Converting double value to String
                    String doubleNumAsString = String.valueOf(doubleNumber);

                    //Separating integer and decimal part if the numeric value contains decimal values
                    int indexOfDecimal = doubleNumAsString.indexOf(".");
                    String intPart = doubleNumAsString.substring(0, indexOfDecimal);
                    String decimalPart = doubleNumAsString.substring(indexOfDecimal);

                    double decimalValue = Double.parseDouble(decimalPart);
                    if (decimalValue == 0)
                        cellValue = intPart;
                    else
                        cellValue = doubleNumAsString;
                }
                break;

            case STRING://If the cell has plain text value
                cellValue = row.getCell(colNum).getStringCellValue();
                break;
            case BLANK: //If the cell has empty value
            case _NONE:
                cellValue = "";
                break;
            case BOOLEAN://If the cell has boolean value
                cellValue = String.valueOf(row.getCell(colNum).getBooleanCellValue());
                break;
            default:
                break;
        }
        return cellValue;
    }

    /*
     * Method to update cell a value in Excel Sheet
     * @parameter:
     * @filePath: Path of the Excel File along with file name
     * @SheetName: Sheet Name to update
     * @rowNo: Row number of the cell
     * @colNo: Column number of the cell
     * @return:
     *
     */
    public void updateExcelFile(String excelFile, String sheetName) throws IOException {
        Row row = null;
        Cell callAnswered;
        Cell cellResult;
        Cell cellResultDate;
        Cell cellReview;

        if (historyRecordFile.isRecordUpdated()) {
            //Creating an object of FormulaEvaluator class to get the cell type information
            //This is quite helpful to understand which cell contains what type of data like String,Integer,double etc
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            Cell cellBookmarked;
            Cell cellItemNum;
            Cell cellToggledBookMark;
            int colNum;
            String bookMarkedValue;
            String itemNumValue;
            String toggledBookMarkValue;

            SortedMap<String, HistoryRecord> historyRecordMap;
            historyRecordMap = historyRecordFile.getHistoryRecordMap();

            //Get the last row containing the ItemNum for Bookmarked
            int maxValueInMap = Integer.parseInt(historyRecordMap.lastKey());

            if (historyRecordMap.size() != 0) {
                for (Map.Entry item : historyRecordMap.entrySet()) {
                    HistoryRecord histRecord = (HistoryRecord) item.getValue();
                    // Retrieve the row (a row-index starts from 0)
                    row = sheet.getRow(Integer.parseInt(histRecord.getItemNumber()));

                    bookMarkedValue = null;
                    toggledBookMarkValue = null;
                    cellBookmarked = row.getCell(bookMarkedPos);
                    cellItemNum = row.getCell(itemNumPos);
                    cellToggledBookMark = row.getCell(toggledBookMarkPos);

                    if (cellItemNum != null) {
                        //Reset the current BookMarked value in the excelsheet
                        if (cellBookmarked != null) {
                            bookMarkedValue = retrieveCellValue(formulaEvaluator.evaluateInCell(
                                            row.getCell(bookMarkedPos)).getCellType(),
                                    row, bookMarkedPos);
                            if (bookMarkedValue != null) {
                                if (bookMarkedValue.matches("Y"))
                                    row.getCell(bookMarkedPos).setCellValue("N");
                            }
                        }

                        if (cellToggledBookMark != null) {
                            toggledBookMarkValue = retrieveCellValue(formulaEvaluator.evaluateInCell(
                                            row.getCell(toggledBookMarkPos)).getCellType(),
                                    row, toggledBookMarkPos);
                            if (toggledBookMarkValue != null) {
                                if (toggledBookMarkValue.matches("Y"))
                                    row.getCell(toggledBookMarkPos).setCellValue("N");
                            }
                        }

                        // Get the ItemNum
                        itemNumValue = retrieveCellValue(formulaEvaluator.evaluateInCell(
                                        row.getCell(itemNumPos)).getCellType(),
                                row, itemNumPos);

                        if (itemNumValue != null) {
                            //By the ItemNum, update the value of cell for bookMarked as Y
                            if (itemNumValue.matches(histRecord.getItemNumber()) &&
                                    histRecord.getBookMarked().matches("Y")) {
                                if (cellBookmarked == null) {
                                    //Creating a new cell in the row of the current sheet
                                    Cell cell = row.createCell(bookMarkedPos);
                                    cell.setCellValue("Y");
                                } else {
                                    row.getCell(bookMarkedPos).setCellValue("Y");
                                }

                                if (cellToggledBookMark == null) {
                                    //Creating a new cell in the row of the current sheet
                                    Cell cell = row.createCell(toggledBookMarkPos);
                                    cell.setCellValue(histRecord.getToggledBookMark());
                                } else {
                                    row.getCell(toggledBookMarkPos).setCellValue(histRecord.getToggledBookMark());
                                }
                            }
                        }
                    }
                }
            }
        }

        if (resultManagement.isRecordUpdated()) {
            SortedMap<String, ResultRecord> resultRecordSortedMap;
            resultRecordSortedMap = resultManagement.getResultRecordSortedMap();

            for (Map.Entry itemData : resultRecordSortedMap.entrySet()) {
                // Retrieve the row via stored ItemNum
                row = sheet.getRow(Integer.parseInt(itemData.getKey().toString()));

                callAnswered = row.getCell(answeredPos);
                cellResult = row.getCell(resultPos);
                cellResultDate = row.getCell(resultDatePos);
                ResultRecord resultRecord = (ResultRecord) itemData.getValue();

                if (cellResult == null) {
                    Cell cell = row.createCell(resultPos);
                    cell.setCellValue(resultRecord.getResult());
                } else {
                    row.getCell(resultPos).setCellValue(resultRecord.getResult());
                }

                if (cellResultDate == null) {
                    Cell cell = row.createCell(resultDatePos);
                    cell.setCellValue(resultRecord.getResultDate());
                } else {
                    row.getCell(resultDatePos).setCellValue(resultRecord.getResultDate());
                }

                if (callAnswered == null) {
                    Cell cell = row.createCell(answeredPos);
                    cell.setCellValue(resultRecord.getAnswered());
                } else {
                    row.getCell(answerPos).setCellValue(resultRecord.getAnswered());
                }
            }

            Cell cellTimer = null;
            SortedMap<String, TimerRecord> timerRecordSortedMap;
            for (int i = 0; i < partFormList.size(); ++i) {
                timerRecordSortedMap = partFormList.get(i).getTimerLayout().getTimerRecordSortedMap();
                updateTimerRecord(timerRecordSortedMap, row, cellTimer);
            }
        }

        if (reviewManagement.isRecordUpdated()) {
            SortedMap<String, ReviewRecord> reviewRecordSortedMap;
            reviewRecordSortedMap = reviewManagement.getReviewRecordSortedMap();

            for (Map.Entry itemData : reviewRecordSortedMap.entrySet()) {
                // Retrieve the row via stored ItemNum
                row = sheet.getRow(Integer.parseInt(itemData.getKey().toString()));

                if (row != null) {
                    cellReview = row.getCell(reviewPos);

                    ReviewRecord reviewRecord = (ReviewRecord) itemData.getValue();

                    if (cellReview == null) {
                        Cell cell = row.createCell(reviewPos);
                        cell.setCellValue(reviewRecord.getReview());
                    } else {
                        row.getCell(reviewPos).setCellValue(reviewRecord.getReview());
                    }
                }
            }
        }

        if (reviewManagement.isRecordUpdated() || historyRecordFile.isRecordUpdated() || resultManagement.isRecordUpdated()){
            //Write the cell data onto the excel workbook
            FileOutputStream outFile = new FileOutputStream(excelFile);
            workbook.write(outFile);
            outFile.close();
        }
    }

    private void updateTimerRecord(SortedMap<String, TimerRecord> timerRecordSortedMap, Row row, Cell cellTimer) {
        for (Map.Entry itemData : timerRecordSortedMap.entrySet()) {
            // Retrieve the row via stored ItemNum
            row = sheet.getRow(Integer.parseInt(itemData.getKey().toString()));

            cellTimer = row.getCell(timePos);
            TimerRecord timerRecord = (TimerRecord) itemData.getValue();

            if (cellTimer == null) {
                Cell cell = row.createCell(timePos);
                cell.setCellValue(timerRecord.getFinishTime());
            } else {
                row.getCell(timePos).setCellValue(timerRecord.getFinishTime());
            }
        }
    }
}
