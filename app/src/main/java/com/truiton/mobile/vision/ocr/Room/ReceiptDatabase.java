package com.truiton.mobile.vision.ocr.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {ReceiptInfo.class},version = 1)
@TypeConverters({Converters.class})
public abstract class ReceiptDatabase extends RoomDatabase {

    public abstract ReceiptDao getDao();

    public static ReceiptDatabase INSTANCE;

    public static ReceiptDatabase getInstance(Context context){
        if(INSTANCE == null)
        {
            INSTANCE = Room.databaseBuilder(context, ReceiptDatabase.class,"ReceiptDatabase")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
