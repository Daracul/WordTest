package com.daracul.android.wordstest.network;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class ApiKeyIntercepter implements Interceptor {
    private static final String PARAM_API_KEY = "key";

    private final String apiKey;

    public ApiKeyIntercepter(String apiKey) {
        this.apiKey = apiKey;
    }


    public static Interceptor create(@NonNull String apiKey) {
        return new ApiKeyIntercepter(apiKey);
    }


    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        final Request requestWithoutApiKey = chain.request();

        final HttpUrl url = requestWithoutApiKey.url()
                .newBuilder()
                .addQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        Request requestWithAttachedApiKey = requestWithoutApiKey.newBuilder()
                .url(url)
                .build();

        return chain.proceed(requestWithAttachedApiKey);
    }
}
