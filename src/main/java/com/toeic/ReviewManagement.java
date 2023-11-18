package com.toeic;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ReviewManagement {
    private static boolean recordUpdated;
    private ReviewRecord reviewRecord;
    private SortedMap<String, ReviewRecord> reviewRecordSortedMap;

    public ReviewManagement() {
        reviewRecordSortedMap = new TreeMap<>();
        recordUpdated = false;
        reviewRecord = new ReviewRecord();
    }

    public SortedMap<String, ReviewRecord> getReviewRecordSortedMap() {
        return reviewRecordSortedMap;
    }

    public void setReviewRecordSortedMap(SortedMap<String, ReviewRecord> reviewRecordSortedMap) {
        this.reviewRecordSortedMap = reviewRecordSortedMap;
    }

    private ReviewRecord getReviewRecord(String itemNum, String groupItemNumber, String part, String review) {
        reviewRecord = new ReviewRecord();
        reviewRecord.setPart(part);
        reviewRecord.setGroupItemNumber(groupItemNumber);
        reviewRecord.setItemNumber(itemNum);
        reviewRecord.setReview(review);
        return reviewRecord;
    }

    public static boolean isRecordUpdated() {
        return recordUpdated;
    }

    public String returnReviewRecord(String itemNum) {
        String review = "";
        if(reviewRecordSortedMap.size() != 0 ) {
            for (Map.Entry item : reviewRecordSortedMap.entrySet()) {

                reviewRecord = (ReviewRecord) item.getValue();
                if (itemNum.matches(reviewRecord.getItemNumber()))
                    review = reviewRecord.getReview();
            }
        }
        return review;
    }

    public void updateResultRecord(String itemNum, String groupItemNumber, String part, String review,
                                   boolean skipInitialize) {
        ReviewRecord reviewRecord = null;
        boolean newData = true;

        if(!skipInitialize) {
            if (!recordUpdated) recordUpdated = true;
        }

        if(reviewRecordSortedMap.size() != 0 ) {
            for (Map.Entry item : reviewRecordSortedMap.entrySet()) {

                if (newData) {
                    reviewRecord = (ReviewRecord) item.getValue();

                    if (itemNum.matches(reviewRecord.getItemNumber())) {
                        reviewRecord.setReview(review);
                        newData = false;
                    }
                }
            }
            if (newData) {
                reviewRecord = getReviewRecord(itemNum, groupItemNumber, part, review);
            }
        } else {
            reviewRecord = getReviewRecord(itemNum, groupItemNumber, part, review);

        }
        if (reviewRecordSortedMap.size() == 0 || !reviewRecordSortedMap.containsKey(itemNum)) {
            reviewRecordSortedMap.put(itemNum, reviewRecord);

        } else {
            reviewRecordSortedMap.replace(itemNum, reviewRecord);
        }
    }

}

class ReviewRecord {
    private String itemNumber;
    private String part;
    private String groupItemNumber;
    private String review;

    public String getItemNumber() { return itemNumber; }

    public void setItemNumber(String itemNumber) { this.itemNumber = itemNumber; }

    public String getPart() { return part; }

    public void setPart(String part) { this.part = part; }

    public String getGroupItemNumber() { return groupItemNumber; }

    public void setGroupItemNumber(String groupItemNumber) { this.groupItemNumber = groupItemNumber; }

    public String getReview() { return review; }

    public void setReview(String review) { this.review = review; }
}
