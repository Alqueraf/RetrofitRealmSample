package com.alexqueudotrafel.retrofitrealmsample.network.helper;

import com.alexqueudotrafel.retrofitrealmsample.model.Question;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by alexqueudotrafel on 16/03/16.
 */
public class MyGsonConverterFactory {

    public static GsonConverterFactory create() {
        // Initialize GsonBuilder
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Add custom deserializers
        gsonBuilder.registerTypeAdapter(new TypeToken<List<Question>>() {}.getType(), new MyGsonDeserializer());

        // Create Gson with our builder
        Gson myGson = gsonBuilder.create();

        // Return custom GsonConverter
        return GsonConverterFactory.create(myGson);
    }
}
