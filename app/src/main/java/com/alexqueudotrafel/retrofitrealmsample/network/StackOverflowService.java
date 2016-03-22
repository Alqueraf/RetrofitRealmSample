package com.alexqueudotrafel.retrofitrealmsample.network;

import com.alexqueudotrafel.retrofitrealmsample.model.Question;
import com.alexqueudotrafel.retrofitrealmsample.network.helper.MyGsonConverterFactory;


import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by alexqueudotrafel on 16/03/16.
 */
public interface StackOverflowService {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URL.API_STACKOVERFLOW)
            .addConverterFactory(MyGsonConverterFactory.create()) // Instead of default: GsonConverterFactory.create()
            .build();

    @GET("2.2/questions?order=desc&sort=creation&site=stackoverflow")
    Call<List<Question>> getQuestions(@Query("tagged") String tags);

    @GET("2.2/questions?order=desc&sort=creation&site=stackoverflow")
    Call<List<Question>> getQuestions();
}
