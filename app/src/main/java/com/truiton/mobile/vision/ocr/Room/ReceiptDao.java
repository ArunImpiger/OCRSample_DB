package com.truiton.mobile.vision.ocr.Room;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface ReceiptDao {

    @Insert
    void insert(ReceiptInfo receiptInfo);
}
