package com.daracul.android.wordstest.network;

import android.support.annotation.NonNull;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TranslateEndpoint {
    @GET("tr.json/translate")
    Single<WordDTO> translationObject(@Query("lang") @NonNull String lang, @Query("text") @NonNull String text);
}

