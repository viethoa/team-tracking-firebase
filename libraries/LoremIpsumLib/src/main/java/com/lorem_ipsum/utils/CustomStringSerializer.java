package com.lorem_ipsum.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by Originally.US on 11/12/14.
 */
public class CustomStringSerializer implements JsonSerializer<String> {
    @Override
    public JsonElement serialize(String s, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(escapeJS(s));
    }

    public static String escapeJS(String string) {
        String escapes[][] = new String[][]{
                {"\\", "\\\\"},
                {"\"", "\\\""},
                {"\n", "\\n"},
                {"\r", "\\r"}
        };
        for (String[] esc : escapes) {
            string = string.replace(esc[0], esc[1]);
        }
        return string;
    }
}
