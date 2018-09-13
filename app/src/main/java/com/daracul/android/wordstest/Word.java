package com.daracul.android.wordstest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by amalakhov on 02.04.2018.
 */

public class Word implements Parcelable{
    private int id;
    private String name;
    private String translation;

    public Word(int id, String name, String translation) {
        this.id=id;
        this.name = name;
        this.translation = translation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public Word (Parcel in){
        String[] data = new String [3];
        in.readStringArray(data);

        this.id = Integer.valueOf(data[0]);
        this.name=data[1];
        this.translation=data[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{String.valueOf(this.id), this.name,this.translation});

    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Word createFromParcel(Parcel source) {
            return new Word(source);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };
}
