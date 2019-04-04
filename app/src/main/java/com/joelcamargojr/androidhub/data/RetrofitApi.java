package com.joelcamargojr.androidhub.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApi {

    private static final long CONNECT_TIMEOUT_MILLI = 5000;
    private static Retrofit retrofit = null;

    public static Retrofit getPodcastClient() {

        if (retrofit == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT_MILLI, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build();

            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://listen-api.listennotes.com")
                    .addConverterFactory(GsonConverterFactory
                            .create(gson))
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}
