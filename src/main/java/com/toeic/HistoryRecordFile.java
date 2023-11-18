package com.toeic;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class HistoryRecordFile {

    private static boolean recordUpdated;
    private HistoryRecord historyRecord;
    private String fileName;
    private SortedMap<String, HistoryRecord> historyRecordMap;

    private List<Part> part1List;
    private List<Part> part2List;
    private List<Part> part3List;
    private List<Part> part4List;
    private List<Part> part5List;
    private List<Part> part6List;
    private List<Part> part7List;

    public HistoryRecordFile() {
        historyRecordMap = new TreeMap<>();
        recordUpdated = false;
    }

    public void setPart1List(List<Part> part1List) { this.part1List = part1List; }

    public void setPart2List(List<Part> part2List) { this.part2List = part2List; }

    public void setPart3List(List<Part> part3List) { this.part3List = part3List; }

    public void setPart4List(List<Part> part4List) { this.part4List = part4List; }

    public void setPart5List(List<Part> part5List) { this.part5List = part5List; }

    public void setPart6List(List<Part> part6List) { this.part6List = part6List; }

    public void setPart7List(List<Part> part7List) { this.part7List = part7List; }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public SortedMap<String, HistoryRecord> getHistoryRecordMap() {
        return historyRecordMap;
    }

    public static boolean isRecordUpdated() { return recordUpdated; }

    public void setHistoryRecordMap(TreeMap<String, HistoryRecord> historyRecordMap) {
        this.historyRecordMap = historyRecordMap;
    }

    private List<Part> getPartList(String part) {
        List<Part> partList;
        switch (part) {
            case "1":
                partList = part1List;
                break;
            case "2":
                partList = part2List;
                break;
            case "3":
                partList = part3List;
                break;
            case "4":
                partList = part4List;
                break;
            case "5":
                partList = part5List;
                break;
            case "6":
                partList = part6List;
                break;
            case "7":
                partList = part7List;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + part);
        }
        return partList;
    }

    public boolean isBookMarkRecord(int numberOfRow, String part) {
        List<Part> partList = null;
        partList = getPartList(part);
        boolean bookMarkRecord = false;

        if (historyRecordMap.size() != 0) {
            if (historyRecordMap.containsKey(partList.get(numberOfRow).getItemNum()) &&
                    historyRecordMap.get(partList.get(numberOfRow).getItemNum()).getBookMarked().matches("Y")) {
                bookMarkRecord = true;
            }

            if (historyRecordMap.containsKey(partList.get(numberOfRow).getItemNum())) {
                String marked = historyRecordMap.get(partList.get(numberOfRow).getItemNum()).getBookMarked();
            } 
        }
        return bookMarkRecord;
    }

    public boolean isBookMarkRecord(int numberOfRow, String part, boolean selected) {
        List<Part> partList = null;
        partList = getPartList(part);
        boolean bookMarkRecord = false;

        String selection = null;

        if (selected)
            selection = "Y";
        else
            selection = "N";

        if (historyRecordMap.size() != 0) {
            if (historyRecordMap.containsKey(partList.get(numberOfRow).getItemNum()) &&
                historyRecordMap.get(partList.get(numberOfRow).getItemNum()).getBookMarked().matches("Y") &&
                historyRecordMap.get(partList.get(numberOfRow).getItemNum()).getToggledBookMark().matches(selection)) {
                bookMarkRecord = true;
            }

            if (historyRecordMap.containsKey(partList.get(numberOfRow).getItemNum())) {
                String marked = historyRecordMap.get(partList.get(numberOfRow).getItemNum()).getBookMarked();
            }
        }
        return bookMarkRecord;
    }

    public void addHistoryRecord(String itemNumber, String part, String bookmarked, String toggledBookMark) {
        historyRecord = new HistoryRecord();
        historyRecord.setItemNumber(itemNumber);
        historyRecord.setPart(part);
        historyRecord.setBookMarked(bookmarked);
        historyRecord.setOriginal(true);
        historyRecord.setToggledBookMark(toggledBookMark);
        historyRecordMap.put(itemNumber, historyRecord);
    }

    public void read() {
        String line = "";
        String splitBy = ",";
        try
        {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader(this.fileName));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] record = line.split(splitBy);    // use comma as separator
                historyRecord = new HistoryRecord();
                historyRecord.setItemNumber(record[0]);
                historyRecord.setPart(record[1]);
                historyRecord.setBookMarked(record[2]);
                historyRecord.setToggledBookMark(record[3]);
                historyRecordMap.put(record[0], historyRecord);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void updateHistoryRecord(String itemNum, int numberOfRow, String part) {
        List<Part> partList = null;

        if(!recordUpdated) recordUpdated = true;

        //Reset the historyRecord lists for the bookMarked
        if(historyRecordMap.size() != 0 ) {
            for (Map.Entry item : historyRecordMap.entrySet()) {
                HistoryRecord histRecord = (HistoryRecord) item.getValue();
                if(part.matches(histRecord.getPart()))
                    histRecord.setBookMarked("N");
            }
        }

        HistoryRecord historyRecord = new HistoryRecord();
        historyRecord.setPart(part);
        historyRecord.setItemNumber(itemNum);
        historyRecord.setBookMarked("Y");
        historyRecord.setOriginal(false);

        partList = getPartList(part);

        if (partList != null) {
            if (historyRecordMap.size() == 0 ||
                    !historyRecordMap.containsKey(partList.get(numberOfRow).getItemNum())) {
                historyRecordMap.put(partList.get(numberOfRow).getItemNum(), historyRecord);
            } else {
                if (part.matches(partList.get(numberOfRow).getPart()))
                    historyRecordMap.replace(partList.get(numberOfRow).getItemNum(), historyRecord);
            }
        }
    }

    public void updateHistoryRecord(String itemNum, int numberOfRow, String part, boolean selected) {
        List<Part> partList = null;

        if(!recordUpdated) recordUpdated = true;

        //Reset the historyRecord lists for the bookMarked
        if(historyRecordMap.size() != 0 ) {
            for (Map.Entry item : historyRecordMap.entrySet()) {
                HistoryRecord histRecord = (HistoryRecord) item.getValue();
                if(part.matches(histRecord.getPart()))
                    histRecord.setBookMarked("N");
            }
        }

        HistoryRecord historyRecord = new HistoryRecord();
        historyRecord.setPart(part);
        historyRecord.setItemNumber(itemNum);
        historyRecord.setBookMarked("Y");
        historyRecord.setOriginal(false);
        if(selected)
            historyRecord.setToggledBookMark("Y");
        else
            historyRecord.setToggledBookMark("N");

        partList = getPartList(part);

        if (partList != null) {
            if (historyRecordMap.size() == 0 ||
                    !historyRecordMap.containsKey(partList.get(numberOfRow).getItemNum())) {
                historyRecordMap.put(partList.get(numberOfRow).getItemNum(), historyRecord);
            } else {
                if (part.matches(partList.get(numberOfRow).getPart()))
                    historyRecordMap.replace(partList.get(numberOfRow).getItemNum(), historyRecord);
            }
        }
    }

    public void write() {
        try {
            File file = new File(fileName);
            FileWriter filewriter = new FileWriter(file);

            // Sort the RadioButtonRecord by number
            for (Map.Entry item: historyRecordMap.entrySet()) {
                HistoryRecord histRecord = (HistoryRecord)item.getValue();
                filewriter.write(item.getKey() +","
                        +histRecord.getPart() + ","
                        +histRecord.getBookMarked() +","
                        +histRecord.getToggledBookMark()
                        +"\n");
            }
            filewriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class HistoryRecord {
    private String bookMarked;
    private String toggledBookMark;
    private String itemNumber;
    private String part;
    private boolean original;

    public String getBookMarked() { return bookMarked; }

    public void setBookMarked(String bookMarked) { this.bookMarked = bookMarked; }

    public String getToggledBookMark() { return toggledBookMark; }

    public void setToggledBookMark(String toggledBookMark) { this.toggledBookMark = toggledBookMark; }

    public String getItemNumber() { return itemNumber; }

    public void setItemNumber(String  itemNumber) { this.itemNumber = itemNumber; }

    public boolean isOriginal() { return original; }

    public void setOriginal(boolean original) { this.original = original; }

    public String getPart() { return part; }

    public void setPart(String part) { this.part = part; }

}