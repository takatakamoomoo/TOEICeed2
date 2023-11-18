package com.toeic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ResultManagement {
    private static boolean recordUpdated;

    private Calendar calendar;
    private Date date;
    private DateFormat dateFormat;
    private ResultRecord resultRecord;
    private SortedMap<String, ResultRecord> resultRecordSortedMap;

    public ResultManagement() {
        resultRecordSortedMap = new TreeMap<>();
        recordUpdated = false;
        resultRecord = new ResultRecord();
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        calendar = Calendar.getInstance();
        date = calendar.getTime();
    }

    public SortedMap<String, ResultRecord> getResultRecordSortedMap() { return this.resultRecordSortedMap; }

    public void setResultRecordSortedMap(SortedMap<String, ResultRecord> resultRecordSortedMap) {
        this.resultRecordSortedMap = resultRecordSortedMap;
    }

    public static boolean isRecordUpdated() { return recordUpdated; }

    public void updateResultRecord(String itemNum, int numberOfRow, String part, String result,
                                   String answered) {
        ResultRecord resultRecord = null;
        boolean newData = true;

        if(!recordUpdated) recordUpdated = true;

        if(resultRecordSortedMap.size() != 0 ) {
            for (Map.Entry item : resultRecordSortedMap.entrySet()) {

                if (newData) {
                    resultRecord = (ResultRecord) item.getValue();

                    if (itemNum.matches(resultRecord.getItemNumber())) {
                        resultRecord.setResult(result);
                        resultRecord.setResultDate(dateFormat.format(date));
                        newData = false;
                    }
                }
            }
            if (newData) {
                resultRecord = getResultRecord(itemNum, part, result, answered);
            }
        } else {
            resultRecord = getResultRecord(itemNum, part, result, answered);

        }
        if (resultRecordSortedMap.size() == 0 || !resultRecordSortedMap.containsKey(itemNum)) {
            resultRecordSortedMap.put(itemNum, resultRecord);

        } else {
            resultRecordSortedMap.replace(itemNum, resultRecord);
        }

    }

    private ResultRecord getResultRecord(String itemNum, String part, String result, String answered) {
        resultRecord = new ResultRecord();
        resultRecord.setAnswered(answered);
        resultRecord.setPart(part);
        resultRecord.setItemNumber(itemNum);
        resultRecord.setResult(result);
        resultRecord.setResultDate(dateFormat.format(date));
        return resultRecord;
    }
}

class ResultRecord {
    private String answered;
    private String itemNumber;
    private String part;
    private String result;
    private String resultDate;

    public String getAnswered() { return answered; }

    public void setAnswered(String answered) { this.answered = answered; }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultDate() {
        return resultDate;
    }

    public void setResultDate(String resultDate) {
        this.resultDate = resultDate;
    }
}