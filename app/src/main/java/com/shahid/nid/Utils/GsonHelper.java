package com.shahid.nid.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.shahid.nid.Categories.Category;
import com.shahid.nid.Note;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Junaid Gandhi on 08/27/2021.
 *
 * A simple Gson helper class for easy access to gson serialization and deserialization
 * across the whole application preventing multiple gson objects instantiation with default
 * configuration.
 *
 */
public class GsonHelper {
    private static final Object LOCK = new Object();
    private static GsonHelper sInstance;
    private final Gson gson;
    private final Type categoryType;
    private final Type noteType;

    private GsonHelper(){
        this.gson = new Gson();
        this.categoryType = new TypeToken<ArrayList<Category>>(){}.getType();
        this.noteType = new TypeToken<ArrayList<Note>>(){}.getType();
    }


    public static GsonHelper getInstance(){
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new GsonHelper();
            }
        }
        return sInstance;
    }

    /**
     * Parses the json string into Arraylist of Category objects
     * @param categoryJson - Json string of array of category objects
     * @return - ArrayList of Category objects
     */
    public ArrayList<Category> getCategoryListFromJson(String categoryJson){
        return gson.fromJson(categoryJson, categoryType);
    }

    /**
     * Parses the json string into Arraylist of Note objects
     * @param noteJson - Json string of array of Note objects
     * @return - ArrayList of Note objects
     */
    public ArrayList<Note> getNoteListFromJson(String noteJson){
        return gson.fromJson(noteJson, noteType);
    }

    public Note getNoteObject(String json){
        return gson.fromJson(json, Note.class);
    }

    public Category getCategoryObject(String json){
        return gson.fromJson(json, Category.class);
    }

    public JsonObject getJsonObject(String json){
        return gson.fromJson(json, JsonObject.class);
    }

    public String toJson(Object obj){
        return gson.toJson(obj);
    }
}
