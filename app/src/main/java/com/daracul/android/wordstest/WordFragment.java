package com.daracul.android.wordstest;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Random;

public class WordFragment extends Fragment {
    private static final String KEY_GET_WORD = "key_get_word";

    private Word mWord;
    private int mBackColor;

    public static WordFragment newInstance(Word word) {
        WordFragment wordFragment = new WordFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_GET_WORD, word);
        wordFragment.setArguments(bundle);
        return wordFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWord = getArguments().getParcelable(KEY_GET_WORD);

        // to see difference between fragments
        Random rnd = new Random();
        mBackColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);
        TextView wordTextView = (TextView) view.findViewById(R.id.tvWord);
        final TextView translationTextView = (TextView) view.findViewById(R.id.tvTranslation);
        wordTextView.setText(mWord.getName());
        translationTextView.setText("");
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    translationTextView.setText(mWord.getTranslation());
                }
                return true;
            }
        });

//        View cardView = view.findViewById(R.id.card_view);
//        cardView.setBackgroundColor(mBackColor);

        return view;

    }
}
