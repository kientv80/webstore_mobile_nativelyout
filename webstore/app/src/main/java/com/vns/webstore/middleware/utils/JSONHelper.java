package com.vns.webstore.middleware.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vns.webstore.middleware.entity.NotifyInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LAP10572-local on 8/29/2016.
 */
public class JSONHelper {
    public static String toJSON(Object obj){
        Gson g = new Gson();
        return g.toJson(obj);
    }
    public static Object toObject(String json, Class clazz){
        Gson g = new Gson();
        return g.fromJson(json,clazz);
    }
    public static <T> List toObjects(String json, Class<T> clazz){
        Gson g = new Gson();
        JsonParser p = new JsonParser();
        JsonArray items = p.parse(json).getAsJsonArray();
        List<T> result = new ArrayList<>();
        for(int i =0;i< items.size();i++){
            result.add(g.fromJson(items.get(i),clazz));
        }
        return result;
    }
}
