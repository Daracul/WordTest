package com.daracul.android.wordstest;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.daracul.android.wordstest.data.WordsContract;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "myLogs";

    private static final int CONTEXT_MENU_RENAME = 1;
    private static final int CONTEXT_MENU_DELETE = 2;
    private static final int OPTIONS_MENU_NEW_WORD = 3;
    private static final int OPTIONS_MENU_TRAINING = 4;
    private static final int OPTIONS_MENU_DELETE_ALL = 5;
    ListView listView;
    SimpleCursorAdapter simpleCursorAdapter;
    ArrayList<Word> myWordsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);


        // forming columns to set data to each other
        String[] from = new String[]{WordsContract.WordsEntry.COLUMN_WORD, WordsContract.WordsEntry.COLUMN_TRANLATION};
        int[] to = new int[]{android.R.id.text1, android.R.id.text2};

        simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, from, to, 0);
        listView.setAdapter(simpleCursorAdapter);
        registerForContextMenu(listView);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, OPTIONS_MENU_NEW_WORD, 0, R.string.newword);
        menu.add(0, OPTIONS_MENU_TRAINING, 0, R.string.training);
        menu.add(0, OPTIONS_MENU_DELETE_ALL, 0, R.string.options_delete_all);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == OPTIONS_MENU_NEW_WORD) {
            createAndShowDialogWindow(-1, getString(R.string.dialog_new_word),
                    getString(R.string.dialog_new_word_enter),
                    getString(R.string.dialog_your_word),
                    getString(R.string.dialog_your_translation));
        }
        if (item.getItemId() == OPTIONS_MENU_TRAINING) {
            Intent intent = new Intent(this, Training.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Training.KEY_TO_GET_LIST, myWordsList);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        if (item.getItemId() == OPTIONS_MENU_DELETE_ALL) {
            getContentResolver().delete(WordsContract.WordsEntry.CONTENT_URI, null, null);
            getSupportLoaderManager().getLoader(0).forceLoad();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addWord(String word, String translation) {
        //adding new word to db and getting new loader
        if (word.isEmpty() || translation.isEmpty()) {
            Toast.makeText(this, "You should enter word and translation!", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(WordsContract.WordsEntry.COLUMN_WORD, word);
        values.put(WordsContract.WordsEntry.COLUMN_TRANLATION, translation);
        Uri uri = getContentResolver().insert(WordsContract.WordsEntry.CONTENT_URI, values);
        if (uri == null) {
            Toast.makeText(this, R.string.word_saved_error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.word_saved, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CONTEXT_MENU_RENAME, 0, R.string.context_rename);
        menu.add(0, CONTEXT_MENU_DELETE, 0, R.string.context_delete);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //getting id of record
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == CONTEXT_MENU_DELETE) {
            Uri currentWordUri = Uri.withAppendedPath(WordsContract.WordsEntry.CONTENT_URI, String.valueOf(acmi.id));
            getContentResolver().delete(currentWordUri, null, null);
        }
        if (item.getItemId() == CONTEXT_MENU_RENAME) {
            renameWordWithDialog(acmi.id);
            Log.d(LOG_TAG, "acmi.id= " + acmi.id);


        }
        return super.onContextItemSelected(item);
    }

    private void renameWordWithDialog(long id) {
        createAndShowDialogWindow(id, "Rename word", "Enter new name and translation", "Your word", "Your translation");
    }


    //if id = -1 than we create "add" dialog, in other cases we create rename
    public void createAndShowDialogWindow(long id, String title, String message, String hint1, String hint2) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText editWord = new EditText(this);
        editWord.setHint(hint1);
        final EditText editTranslation = new EditText(this);
        editTranslation.setHint(hint2);
        alert.setMessage(message);
        alert.setTitle(title);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editWord);
        layout.addView(editTranslation);
        alert.setView(layout);
        if (id == -1) {
            alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //What ever you want to do with the value
                    addWord(editWord.getText().toString(), editTranslation.getText().toString());
                }
            });
        } else {
            final long wordId = id;
            for (int i = 0; i < myWordsList.size(); i++) {
                if (myWordsList.get(i).getId() == id) {
                    Log.d(LOG_TAG, "list word = " + myWordsList.get(i).getName());
                    editWord.setText(myWordsList.get(i).getName());
                    editTranslation.setText(myWordsList.get(i).getTranslation());
                }
            }
            alert.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    renameWord(wordId, editWord.getText().toString(), editTranslation.getText().toString());

                }
            });
        }

        alert.show();
    }

    private int renameWord(long id, String newName, String newTranslation) {
        Uri currentWordUri = Uri.withAppendedPath(WordsContract.WordsEntry.CONTENT_URI, String.valueOf(id));
        ContentValues values = new ContentValues();
        values.put(WordsContract.WordsEntry.COLUMN_WORD, newName);
        values.put(WordsContract.WordsEntry.COLUMN_TRANLATION, newTranslation);
        int updatedRow = getContentResolver().update(currentWordUri, values, null, null);
        if (updatedRow <= 0) {
            Toast.makeText(this, R.string.word_update_error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.word_update, Toast.LENGTH_SHORT).show();
        }
        return updatedRow;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, WordsContract.WordsEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        simpleCursorAdapter.swapCursor(data);

        //create help list to easy get name of rows
        myWordsList = new ArrayList<>();
        if (data.moveToFirst()) {
            int idColIndex = data.getColumnIndex(WordsContract.WordsEntry.COLUMN_ID);
            int nameColIndex = data.getColumnIndex(WordsContract.WordsEntry.COLUMN_WORD);
            int transColIndex = data.getColumnIndex(WordsContract.WordsEntry.COLUMN_TRANLATION);
            do {
                Log.d(LOG_TAG,
                        "ID = " + data.getInt(idColIndex) +
                                ", name = " + data.getString(nameColIndex) +
                                ", translation = " + data.getString(transColIndex));
                myWordsList.add(new Word(data.getInt(idColIndex), data.getString(nameColIndex), data.getString(transColIndex)));
            } while (data.moveToNext());
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleCursorAdapter.swapCursor(null);

    }


}
