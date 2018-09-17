package com.daracul.android.wordstest;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;

public class TrainingCards extends AppCompatActivity {
    private static final String KEY_TO_GET_LIST = "myList";

    private ViewPager viewPager;
    private ArrayList<Word> myWordsList;
    private PagerAdapter mPagerAdapter;

    public static void start (Activity activity, ArrayList<Word> wordList){
        Intent intent = new Intent(activity, TrainingCards.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_TO_GET_LIST, wordList);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_cards);

        myWordsList = getIntent().getExtras().getParcelableArrayList(KEY_TO_GET_LIST);
        Collections.shuffle(myWordsList);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new WordsPagerAdapter(getSupportFragmentManager(), myWordsList);
        viewPager.setAdapter(mPagerAdapter);
    }
}
