package com.daracul.android.wordstest.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by amalakhov on 14.05.2018.
 */

public class WordsProvider extends ContentProvider {
    /** Tag for the log messages */
    public static final String LOG_TAG = WordsProvider.class.getSimpleName();

    private WordsDbHelper wordsDbHelper;

    private static final int WORDS = 100;
    private static final int WORD_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {

        sUriMatcher.addURI(WordsContract.CONTENT_AUTHORITY, WordsContract.PATH_WORDS, WORDS);

        sUriMatcher.addURI(WordsContract.CONTENT_AUTHORITY, WordsContract.PATH_WORDS + "/#", WORD_ID);
    }

    @Override
    public boolean onCreate() {
        wordsDbHelper = new WordsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = wordsDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match){
            case WORDS:
                cursor = database.query(WordsContract.WordsEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case WORD_ID:
                selection = WordsContract.WordsEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(WordsContract.WordsEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw  new IllegalArgumentException("Cannot query unknown URI "+ uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORDS:
                return WordsContract.WordsEntry.CONTENT_LIST_TYPE;
            case WORD_ID:
                return WordsContract.WordsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case WORDS:
                return insertWord(uri, values);
            default:
                throw  new IllegalArgumentException("Insertion is not supported for "+ uri);
        }
    }

    private Uri insertWord(Uri uri, ContentValues values) {
        //check that name is not null
        String name = values.getAsString(WordsContract.WordsEntry.COLUMN_WORD);
        if (TextUtils.isEmpty(name)){
            throw new IllegalArgumentException("Word needs name");
        }
        String translation = values.getAsString(WordsContract.WordsEntry.COLUMN_TRANLATION);
        if (TextUtils.isEmpty(translation)){
            throw new IllegalArgumentException("Word needs translation");
        }

        SQLiteDatabase database  = wordsDbHelper.getWritableDatabase();
        long id = database.insert(WordsContract.WordsEntry.TABLE_NAME, null, values);

        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case WORDS:
                return deleteWord(uri, selection, selectionArgs);
            case WORD_ID:
                selection = WordsContract.WordsEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteWord(uri, selection, selectionArgs);
            default:
                throw  new IllegalArgumentException("Delete is not supported for "+ uri);
        }
    }

    private int deleteWord(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = wordsDbHelper.getWritableDatabase();

        int rowsDeleted = database.delete(WordsContract.WordsEntry.TABLE_NAME, selection, selectionArgs);
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case WORDS:
                return updateWord(uri, values, selection, selectionArgs);
            case WORD_ID:
                selection = WordsContract.WordsEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateWord(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateWord(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(WordsContract.WordsEntry.COLUMN_WORD)){
            String name = values.getAsString(WordsContract.WordsEntry.COLUMN_WORD);
            if (name == null){
                throw new IllegalArgumentException("Word requires a name");

            }
        }
        if (values.containsKey(WordsContract.WordsEntry.COLUMN_TRANLATION)){
            String translation = values.getAsString(WordsContract.WordsEntry.COLUMN_TRANLATION);
            if (translation == null){
                throw new IllegalArgumentException("Word requires a translation");

            }
        }
        if (values.size() == 0){
            return 0;
        }
        SQLiteDatabase database = wordsDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(WordsContract.WordsEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
