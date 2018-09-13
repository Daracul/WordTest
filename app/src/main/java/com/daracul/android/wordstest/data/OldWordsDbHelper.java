package com.daracul.android.wordstest.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.daracul.android.wordstest.data.WordsContract;

/**
 * Created by amalakhov on 26.03.2018.
 */

public class OldWordsDbHelper {
    private static final String LOG_TAG = "myLogs";
    private static final String DB_NAME = "words.db";
    private static final int DB_VESION = 1;
    private static final String DB_CREATE = "CREATE TABLE "+ WordsContract.WordsEntry.TABLE_NAME +"("+
            WordsContract.WordsEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            WordsContract.WordsEntry.COLUMN_WORD + " TEXT, " +
            WordsContract.WordsEntry.COLUMN_TRANLATION+ " TEXT" +
            ");";

    private final Context context;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public OldWordsDbHelper(Context context) {
        this.context = context;
    }

    //open connection
    public void open(){
        dbHelper = new DBHelper(context,DB_NAME, null, DB_VESION);
        db = dbHelper.getWritableDatabase();
    }

    //close connection
    public void close (){
        if (dbHelper!=null){
            dbHelper.close();
        }
    }

    //get all data from table DB_TABLE
    public Cursor getAllData(){
        return db.query(WordsContract.WordsEntry.TABLE_NAME, null, null, null, null, null, null);
    }


    public void addWord (String word, String translation){
        Log.d(LOG_TAG,"Inserting "+word+" : "+translation);
        ContentValues wordValues = new ContentValues();
        wordValues.put(WordsContract.WordsEntry.COLUMN_WORD, word);
        wordValues.put(WordsContract.WordsEntry.COLUMN_TRANLATION, translation);
        db.insert(WordsContract.WordsEntry.TABLE_NAME,null,wordValues);
    }

    public void deleteWord (long id){
        db.delete(WordsContract.WordsEntry.TABLE_NAME, WordsContract.WordsEntry.COLUMN_ID + " = " +id, null);
    }
    public void renameWord (long id, String newName, String newTranslation){
        ContentValues wordValues = new ContentValues();
        wordValues.put(WordsContract.WordsEntry.COLUMN_WORD,newName);
        wordValues.put(WordsContract.WordsEntry.COLUMN_TRANLATION,newTranslation);
        db.update(WordsContract.WordsEntry.TABLE_NAME,wordValues, WordsContract.WordsEntry.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }
    public void deleteAllRows(){
        db.execSQL("DELETE FROM "+ WordsContract.WordsEntry.TABLE_NAME);
    }




    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG,"onCreate DataBase");
            db.execSQL(DB_CREATE);
        }



        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }




}
