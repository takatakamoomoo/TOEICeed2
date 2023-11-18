package com.toeic;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ComprehensionStatusFile {

    private String bookMarkedDate;
    private int bookMarkedItemNumber;
    private double numberOfComp;
    private int numberOfIncomp;
    private String fileName;
    private TreeMap<String, String> compStatusMap;

    public ComprehensionStatusFile() {
        compStatusMap = new TreeMap<>();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public TreeMap<String, String> getCompStatusMap() {
        return compStatusMap;
    }

    public void setCompStatusMap(TreeMap<String, String> compStatusMap) {
        this.compStatusMap = compStatusMap;
    }

    public int getNumberOfComp() { return (int)numberOfComp; }

    public int getNumberOfIncompleted() { return numberOfIncomp; }

    public int getBookMarkedItemNumber() { return bookMarkedItemNumber; }

    public void setBookMarkedItemNumber(int bookMarkedItemNumber) { this.bookMarkedItemNumber = bookMarkedItemNumber; }

    public String getBookMarkedDate() { return bookMarkedDate; }

    public void setBookMarkedDate(String bookMarkedDate) { this.bookMarkedDate = bookMarkedDate; }

    public int consolidateComprehensionStatus(int lastRowNum) {
        int percent = 0;
        numberOfComp = 0;
        numberOfIncomp = 0;

        // getting entrySet() into Set
        Set<Map.Entry<String, String>> entrySet1 = compStatusMap.entrySet();

        // for-each loop
        for(Map.Entry<String, String> entry1 : entrySet1) {
            if(entry1.getValue().matches("100%"))
                ++numberOfComp;
            else if (entry1.getValue().matches("50%") ||
                     entry1.getValue().matches("0%"))
                ++numberOfIncomp;
        }
        if (numberOfComp != 0)
            percent = (int) Math.round((numberOfComp / (double)lastRowNum) * 100);

        return percent;
    }

    public String retrieveStatus(String itemNumber) {
        String status = null;
        if (compStatusMap.containsKey(itemNumber)) {
            status = compStatusMap.get(itemNumber).toString();
        }
        return status;
    }

    public void read() {
        boolean isHeader = true;
        String line = "";
        String splitBy = ",";
        try
        {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader(this.fileName));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] record = line.split(splitBy);    // use comma as separator
                compStatusMap.put(record[0],  record[1]);

                if(isHeader) {
                    this.setBookMarkedItemNumber(Integer.valueOf(record[2]));
                    this.setBookMarkedDate(record[3]);
                    isHeader = false;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void write() {
        boolean isHeader = true;
        try {
            File file = new File(fileName);
            FileWriter filewriter = new FileWriter(file);

            // Sort the RadioButtonRecord by number
            for (Map.Entry item:compStatusMap.entrySet()) {

                if(isHeader) {
                    filewriter.write(item.getKey() + "," + item.getValue() + "," + bookMarkedItemNumber
                                   + "," +  bookMarkedDate + "\n");
                    isHeader = false;
                } else {
                    filewriter.write(item.getKey() + "," + item.getValue()  + "\n");
                }
            }
            filewriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
