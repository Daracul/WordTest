package com.daracul.android.wordstest;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daracul.android.wordstest.data.WordsContract;
import com.daracul.android.wordstest.network.RestApi;
import com.daracul.android.wordstest.network.WordDTO;

import java.util.ArrayList;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "myLogs";

    public static final int CONTEXT_MENU_RENAME = 1;
    public static final int CONTEXT_MENU_DELETE = 2;
    private static final int OPTIONS_MENU_TRAINING = 4;
    private static final int OPTIONS_MENU_DELETE_ALL = 5;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private ArrayList<Word> myWordsList;
    private WordsRecyclerAdapter mWordsAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        emptyView = (TextView) findViewById(R.id.empty);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
        mWordsAdapter = new WordsRecyclerAdapter(this, null);
        recyclerView.setAdapter(mWordsAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndShowDialogWindow(-1, getString(R.string.dialog_new_word),
                        getString(R.string.dialog_new_word_enter),
                        getString(R.string.dialog_your_word),
                        getString(R.string.dialog_your_translation));
            }
        });


        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, OPTIONS_MENU_TRAINING, 0, R.string.training);
        menu.add(0, OPTIONS_MENU_DELETE_ALL, 0, R.string.options_delete_all);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == OPTIONS_MENU_TRAINING) {
            TrainingCards.start(this, myWordsList);
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
            Toast.makeText(this, R.string.dialog_toast_empty, Toast.LENGTH_SHORT).show();
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
    public boolean onContextItemSelected(MenuItem item) {
        //getting id of record
//        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = myWordsList.get(mWordsAdapter.getPosition()).getId();
        Log.d(LOG_TAG, "position= " + position + "\n menuitem id = " + item.getItemId());
        if (item.getItemId() == CONTEXT_MENU_DELETE) {
            Uri currentWordUri = Uri.withAppendedPath(WordsContract.WordsEntry.CONTENT_URI, String.valueOf(position));
            getContentResolver().delete(currentWordUri, null, null);
        }
        if (item.getItemId() == CONTEXT_MENU_RENAME) {
            renameWordWithDialog(position);
        }
        return super.onContextItemSelected(item);
    }

    private void renameWordWithDialog(long id) {
        createAndShowDialogWindow(id, getString(R.string.dialog_rename_word),
                getString(R.string.dialog_rename_input),
                getString(R.string.dialog_your_word),
                getString(R.string.dialog_your_translation));
    }


    //if id = -1 than we create "add" dialog, in other cases we create rename
    public void createAndShowDialogWindow(long id, String title, String message, String hint1, String hint2) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText editWord = new EditText(this);
        editWord.setHint(hint1);
        final EditText editTranslation = new EditText(this);
        editTranslation.setHint(hint2);
        editTranslation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    translateWord(editWord.getText().toString(), editTranslation);
                }
            }
        });
        alert.setMessage(message);
        alert.setTitle(title);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(60, 0, 60, 0);
        editWord.setLayoutParams(lp);
        editTranslation.setLayoutParams(lp);
        layout.addView(editWord);
        layout.addView(editTranslation);
        alert.setView(layout);
        if (id == -1) {
            alert.setPositiveButton(R.string.dialog_add_button, new DialogInterface.OnClickListener() {
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
            alert.setPositiveButton(R.string.dialog_rename_button, new DialogInterface.OnClickListener() {
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
        if (myWordsList.size()<1){
            emptyView.setText(R.string.text_view_no_words);
        } else emptyView.setText("");
        mWordsAdapter.swapCursor(myWordsList);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mWordsAdapter.swapCursor(null);

    }

    private void translateWord(String word, final EditText translatedText){

        Disposable disposable = RestApi.getInstance()
                .translateEndpoint()
                .translationObject("en-ru", word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WordDTO>() {
                    @Override
                    public void accept(WordDTO wordDTO) throws Exception {
                        translatedText.setText(wordDTO.getText().get(0));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(LOG_TAG,throwable.toString());
                    }
                });
    }
}
