package com.daracul.android.wordstest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class Training extends AppCompatActivity implements View.OnTouchListener {
    public static final String KEY_TO_GET_LIST = "myList";
    private static final String LOG_TAG = "myLogs";
    ArrayList<Word> myWordsList;
    TextView wordTextView;
    TextView translationTextView;
    private int currentSelection ;
    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        wordTextView=(TextView)findViewById(R.id.tvWord);
        translationTextView=(TextView)findViewById(R.id.tvTranslation);
        linearLayout=(LinearLayout)findViewById(R.id.linearLayout);
        myWordsList = getIntent().getExtras().getParcelableArrayList(KEY_TO_GET_LIST);
        currentSelection=(int) (Math.random() * myWordsList.size());
            wordTextView.setText(myWordsList.get(currentSelection).getName());
        linearLayout.setOnTouchListener(this);



    }


    public void onForwardButtonClick(View view) {
        if (myWordsList!=null) {
            int random = currentSelection;
            while (random == currentSelection) {
                random = (int) (Math.random() * myWordsList.size());
            }
            currentSelection =random;
            wordTextView.setText(myWordsList.get(currentSelection).getName());
            translationTextView.setText("");
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN){
            translationTextView.setText(myWordsList.get(currentSelection).getTranslation());
        }
        return true;
    }
}
