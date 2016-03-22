package com.alexqueudotrafel.retrofitrealmsample.network.serializers;

import com.alexqueudotrafel.retrofitrealmsample.model.Question;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by alexqueudotrafel on 17/03/16.
 */
public class QuestionSerializer implements JsonSerializer<Question> {

    @Override
    public JsonElement serialize(Question src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("title", src.getTitle());
        jsonObject.addProperty("link", src.getLink());
        jsonObject.addProperty("creation_date", src.getUnixDate());
        return jsonObject;
    }

}
