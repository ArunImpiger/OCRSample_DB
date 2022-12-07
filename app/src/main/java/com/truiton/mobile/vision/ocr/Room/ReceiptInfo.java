package com.truiton.mobile.vision.ocr.Room;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ReceiptInfo {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;

    @TypeConverters(Converters.class)
    ArrayList<String> textList ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        date = date;
    }

    public ArrayList<String> getTextList() {
        return textList;
    }

    public void setTextList(ArrayList<String> textList) {
        this.textList = textList;
    }

    public ReceiptInfo(int id, String date, ArrayList<String> textList) {
        this.id = id;
        this.date = date;
        this.textList = textList;
    }
}
