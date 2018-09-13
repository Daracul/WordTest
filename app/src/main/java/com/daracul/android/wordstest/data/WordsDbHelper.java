package com.daracul.android.wordstest.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by amalakhov on 15.05.2018.
 */

public class WordsDbHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "myLogs";
    private static final String DB_NAME = "words.db";
    private static final int DB_VESION = 1;
    private static final String DB_CREATE = "CREATE TABLE "+ WordsContract.WordsEntry.TABLE_NAME +"("+
            WordsContract.WordsEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            WordsContract.WordsEntry.COLUMN_WORD + " TEXT, " +
            WordsContract.WordsEntry.COLUMN_TRANLATION+ " TEXT" +
            ");";


    public WordsDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VESION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
