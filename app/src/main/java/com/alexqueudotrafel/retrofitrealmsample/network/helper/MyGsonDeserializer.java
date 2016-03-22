package com.alexqueudotrafel.retrofitrealmsample.network.helper;

import com.alexqueudotrafel.retrofitrealmsample.model.Question;
import com.alexqueudotrafel.retrofitrealmsample.network.serializers.QuestionSerializer;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.realm.QuestionRealmProxy;
import io.realm.RealmObject;


/**
 * Created by alexqueudotrafel on 16/03/16.
 */
public class MyGsonDeserializer implements JsonDeserializer<List<Question>> {

    @Override
    public List<Question> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        // Initialize GsonBuilder
        Gson gson = new GsonBuilder()
                // Add ExclusionStrategy for compatibility with Realm
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                // Add RegisterTypeAdapter for Serialization Compatibility with Realm (for every model)
                .registerTypeAdapter(QuestionRealmProxy.class, new QuestionSerializer())
                .create();

        // Custom deserialization Logic
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("items");

        // Return deserialized object from Json
        return gson.fromJson(jsonArray, new TypeToken<List<Question>>() {}.getType());
    }

}

