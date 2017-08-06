package info.puzz.a10000sentences.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    private static SentencesService instance;

    public static final String BASE_URL = "https://storage.googleapis.com/10000sentences/5/";

    public static SentencesService instance() {
        if (instance == null) {
            instance = initInstance();
        }
        return instance;
    }

    private static SentencesService initInstance() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(SentencesService.class);
    }

}

