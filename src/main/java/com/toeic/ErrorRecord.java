package com.toeic;

public class ErrorRecord {
    String answer;
    String itemNumber;
    String part;
    String source;
    boolean result;

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getPart() { return part; }

    public void setPart(String part) { this.part = part; }

    public String getSource() { return source; }

    public void setSource(String source) { this.source = source; }
}
