package com.toeic;

import java.util.Comparator;

/**
 * Part class
 */
public class Part {
    private String answer;
    private String category;
    private String dialog;
    private String dialogAttrib;
    private String explAttrib;
    private String explanation;
    private String explanation1;
    private String explanation2;
    private String audioFile;
    private String filePath;

    private String fullTime;
    private String review;
    private String result;
    private String resultDate;
    private String question;
    private String questionNum;
    private String questAttrib;
    private String scriAttrib;
    private String scrPreserveRatio;
    private String script;
    private String source;
    private String testResult;
    private String triplePassage;
    private String triplePassAttrib;
    private String textbookPos;
    private String textbookAttrib;

    private String textbookFolder;

    private String itemNum;
    private String part;
    private String partListNumber;
    private int total;

    private String day;
    private String example;
    private String exampleFile;
    private String meaning;
    private String pronunciation;
    private String rowNumber;
    private String status;
    private String word;

    /**
     * Constructor
     */
    public Part(String itemNum, String part, String question, String answer, String fileName, String filePath, String expAttrib,
                String explanation, String scrAttrib, String script, String partListNumber, String source,
                String dialogFile, String dialogAttrib, String questionNum, String triplePassage,
                String triplePassAttrib, String textbookPos, String textbookAttrib, String textbookFolder,
                String scrPreserveRatio, String category, String result, String resultDate,
                String review, String questAttrib, String fullTime) {
        this.setItemNum(itemNum);
        this.setPart(part);
        this.setQuestion(question);
        this.setQuestAttrib(questAttrib);
        this.setAnswer(answer);
        this.setAudioFile(fileName);
        this.setFilePath(filePath);
        this.setExplAttrib(expAttrib);
        this.setExplanation(explanation);
        this.setScriAttrib(scrAttrib);
        this.setScrPreserveRatio(scrPreserveRatio);
        this.setScript(script);
        this.setPartListNumber(partListNumber);
        this.setSource(source);
        this.setDialog(dialogFile);
        this.setDialogAttrib(dialogAttrib);
        this.setQuestionNum(questionNum);
        this.setTriplePassage(triplePassage);
        this.setTriplePassAttrib(triplePassAttrib);
        this.setTextbookPos(textbookPos);
        this.setTextbookAttrib(textbookAttrib);
        this.setTextbookFolder(textbookFolder);
        this.setCategory(category);
        this.setResult(result);
        this.setResultDate(resultDate);
        this.setReview(review);
        this.setFullTime(fullTime);
    }

    public Part(String rowNumber, String source, String itemNum, String status, String priority, String day,
                String part, String word, String tittle, String audioFile, String meaning,
                String example, String filePath, String exampleFile, String pronunciation, boolean isVocabulary ) {
        this.setRowNumber(rowNumber);
        this.setItemNum(itemNum);
        this.setStatus(status);
        this.setDay(day);
        this.setPart(part);
        this.setWord(word);
        this.setAudioFile(audioFile);
        this.setFilePath(filePath);
        this.setExample(example);
        this.setExampleFile(exampleFile);
        this.setMeaning(meaning);
        this.setSource(source);
        this.setPronunciation(pronunciation);
    }

    public String getDay() { return day; }

    public void setDay(String day) { this.day = day; }

    public String getExample() { return example; }

    public void setExample(String example) { this.example = example; }

    public String getExampleFile() { return exampleFile; }

    public void setExampleFile(String exampleFile) { this.exampleFile = exampleFile; }

    public String getMeaning() { return meaning; }

    public void setMeaning(String meaning) { this.meaning = meaning; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getWord() { return word; }

    public void setWord(String word) { this.word = word; }

    public String getExplAttrib() {
        return explAttrib;
    }

    public void setExplAttrib(String explAttrib) {
        this.explAttrib = explAttrib;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getDialog() { return dialog; }

    public void setDialog(String dialog) { this.dialog = dialog; }

    public String getDialogAttrib() {
        return dialogAttrib;
    }

    public void setDialogAttrib(String dialogAttrib) {
        this.dialogAttrib = dialogAttrib;
    }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public String getItemNum() {
        return itemNum;
    }

    public void setItemNum(String itemNum) {
        this.itemNum = itemNum;
    }

    public String getPartListNumber() {
        return partListNumber;
    }

    public void setPartListNumber(String partListNumber) {
        this.partListNumber = partListNumber;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getFullTime() {
        return fullTime;
    }

    public void setFullTime(String fullTime) {
        this.fullTime = fullTime;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestAttrib() { return questAttrib; }

    public void setQuestAttrib(String questAttrib) { this.questAttrib = questAttrib; }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getScriAttrib() {
        return scriAttrib;
    }

    public void setScriAttrib(String scriAttrib) {
        this.scriAttrib = scriAttrib;
    }

    public String getScrPreserveRatio() { return scrPreserveRatio; }

    public void setScrPreserveRatio(String scrPreserveRatio) { this.scrPreserveRatio = scrPreserveRatio; }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getExplanation1() {
        return explanation1;
    }

    public void setExplanation1(String explanation1) {
        this.explanation1 = explanation1;
    }

    public String getExplanation2() {
        return explanation2;
    }

    public void setExplanation2(String explanation2) {
        this.explanation2 = explanation2;
    }

    public String getQuestionNum() { return questionNum; }

    public void setQuestionNum(String questionNum) { this.questionNum = questionNum; }

    public String getReview() { return review; }

    public void setReview(String review) { this.review = review; }

    public String getResult() { return result; }

    public void setResult(String result) { this.result = result; }

    public String getResultDate() { return resultDate; }

    public void setResultDate(String resultDate) { this.resultDate = resultDate; }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getTextbookPos() { return textbookPos; }

    public void setTextbookPos(String textbookPos) { this.textbookPos = textbookPos; }

    public String getTextbookAttrib() { return textbookAttrib; }

    public void setTextbookAttrib(String textbookAttrib) { this.textbookAttrib = textbookAttrib; }

    public String getTextbookFolder() { return textbookFolder; }

    public void setTextbookFolder(String textbookFolder) { this.textbookFolder = textbookFolder; }

    public String getTriplePassage() { return triplePassage; }

    public void setTriplePassage(String triplePassage) { this.triplePassage = triplePassage; }

    public String getTriplePassAttrib() { return triplePassAttrib; }

    public void setTriplePassAttrib(String triplePassAttrib) { this.triplePassAttrib = triplePassAttrib; }

    public String getPronunciation() { return pronunciation; }

    public void setPronunciation(String pronunciation) { this.pronunciation = pronunciation; }

    public String getRowNumber() { return rowNumber; }

    public void setRowNumber(String rowNumber) { this.rowNumber = rowNumber; }

    // A Key to sort the List via source
    public static final Comparator<Part> BY_SOURCE
            = new Comparator<Part>() {
        public int compare(final Part p1, final Part p2) {
            return p1.getSource().compareTo(p2.getSource());
        }
    };

    public static final Comparator<Part> BY_ITEM_NUM
            = new Comparator<Part>() {
        public int compare(final Part p1, final Part p2) {
            return p1.getItemNum().compareTo(p2.getItemNum());
        }
    };

    public static final Comparator<Part> BY_CATEGORY
            = new Comparator<Part>() {
        public int compare(final Part p1, final Part p2) {
            return p1.getCategory().compareTo(p2.getCategory());
        }
    };
}