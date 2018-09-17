package com.daracul.android.wordstest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class WordsPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Word> mWordsList;

    public WordsPagerAdapter(FragmentManager fm, ArrayList<Word> wordsList) {
        super(fm);
        mWordsList = wordsList;
    }

    @Override
    public Fragment getItem(int position) {
        return WordFragment.newInstance(mWordsList.get(position));
    }

    @Override
    public int getCount() {
        return mWordsList.size();
    }
}
