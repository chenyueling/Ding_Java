package cn.jpush.alertme.factory.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonUtil {
    private static Gson gson = new Gson();
    private static GsonBuilder gsonBuilder = new GsonBuilder();

    public static Gson getGson() {
        return gson;
    }

    public static <T> T format(String data, Class<T> cls) {
        if (StringUtil.isEmpty(data)) {
            throw new JsonSyntaxException("Invalid JSON string.");
        }
        return gson.fromJson(data, cls);
    }

    public static <T> List<T> formatToList(String data, Class<T> cls) {
        return gson.fromJson(data, new TypeToken<List<T>>() {}.getType());
    }

    public static <T> Set<T> formatToSet(String data, Class<T> cls) {
        return gson.fromJson(data, new TypeToken<Set<T>>() {}.getType());
    }

    public static List<?> formatToList(String data) {
        return gson.fromJson(data, new TypeToken<List<?>>() {}.getType());
    }


    public static Map<String, String> formatToMap(String data) throws JsonSyntaxException {
        GsonBuilder gb = new GsonBuilder();
        Gson g = gb.create();
        Map<String, String> map = g.fromJson(data, new TypeToken<Map<String, String>>() {}.getType());
        return map;
    }

    public static List<Map<String, String>> formatToArrayMap(String data) throws IOException {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        JsonReader reader = null;
        reader = new JsonReader(new StringReader(data));
        reader.setLenient(true);
        reader.beginArray();
        while(reader.hasNext()) {
            reader.beginObject();
            Map<String, String> mapObj = new HashMap<String, String>();
            while (reader.hasNext()) {
                mapObj.put(reader.nextName(), reader.nextString());
            }
            result.add(mapObj);
            reader.endObject();
        }
        reader.endArray();
        return result;
    }


    public static String toJson(Object data) {
        return JsonUtil.gson.toJson(data);
    }

    public static JsonBuilder newBuilder() {
        return new JsonBuilder();
    }

    public static Map<String, List<String>> jsonToMap(String json) {
        GsonBuilder gb = new GsonBuilder();
        Gson g = gb.create();
        Map<String, List<String>> map = g.fromJson(json, new TypeToken<Map<String, List<String>>>() {
        }.getType());
        return map;
    }

    public static class JsonBuilder {
        Map<String, Object> data = new HashMap<String, Object>();

        public JsonBuilder addItem(String key, Object value) {
            data.put(key, value);
            return this;
        }

        public String build() {
            return JsonUtil.toJson(data);
        }
    }


    public static <T> List<T> jsonToList(String jsonStr, Type typeToken) {
        Gson gson = new Gson();
        ArrayList<T> list = gson.fromJson(jsonStr, typeToken);
        return list;
    }



    private JsonUtil() {}
}
