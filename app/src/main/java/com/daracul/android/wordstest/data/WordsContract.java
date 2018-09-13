package com.daracul.android.wordstest.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by amalakhov on 14.05.2018.
 */

public final class WordsContract {
    /**
     * Uri constants for WordsProvider
     */
    public static final String CONTENT_AUTHORITY = "com.daracul.android.wordstest";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WORDS = "words";

    private WordsContract(){}

    public static class WordsEntry implements BaseColumns {
        public static final String TABLE_NAME = "myWords";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_TRANLATION = "translation";
        /**
         * full words
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WORDS);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of words.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORDS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single word.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORDS;
    }
}
