package dev.dadowl.testtask.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonBuilder {

    private JsonObject json = new JsonObject();

    public JsonBuilder() {}

    public JsonBuilder(JsonObject json){
        this.json = json;
    }

    public JsonBuilder add(String key, String value) {
        json.addProperty(key, value);
        return this;
    }

    public JsonBuilder add(String key, int value) {
        json.addProperty(key, value);
        return this;
    }

    public JsonBuilder add(String key, boolean value) {
        json.addProperty(key, value);
        return this;
    }

    public JsonBuilder add(String key, JsonObject value) {
        json.add(key, value);
        return this;
    }

    public JsonBuilder add(String key, JsonArray array){
        json.add(key, array);
        return this;
    }

    public String toString() {
        return json.toString();
    }

    public JsonObject build() {
        return json;
    }

}
