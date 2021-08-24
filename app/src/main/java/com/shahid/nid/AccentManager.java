package com.shahid.nid;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class AccentManager {

    private SharedPreferences.Editor editorAccent;

    public AccentManager() {
    }

    public void checkColor(Context context, String color) {

        if (color.equals("color1")) {
            editorAccent = context.getSharedPreferences(context.getResources().getString(R.string.MY_PREFS_ACCENT), MODE_PRIVATE).edit();
            editorAccent.putString("accent", color);
            editorAccent.apply();
            context.getTheme().applyStyle(R.style.color1, true);
            Toast.makeText(context, "The Default Accent Color has been changed",
                    Toast.LENGTH_SHORT).show();
        } else if (color.equals("color2")) {
            editorAccent = context.getSharedPreferences(context.getResources().getString(R.string.MY_PREFS_ACCENT), MODE_PRIVATE).edit();
            editorAccent.putString("accent", color);
            editorAccent.apply();
            context.getTheme().applyStyle(R.style.color2, true);
            Toast.makeText(context, "The Default Accent Color has been changed",
                    Toast.LENGTH_SHORT).show();
        } else if (color.equals("color3")) {
            editorAccent = context.getSharedPreferences(context.getResources().getString(R.string.MY_PREFS_ACCENT), MODE_PRIVATE).edit();
            editorAccent.putString("accent", color);
            editorAccent.apply();
            context.getTheme().applyStyle(R.style.color3, true);
            Toast.makeText(context, "The Default Accent Color has been changed",
                    Toast.LENGTH_SHORT).show();
        } else if (color.equals("color4")) {
            editorAccent = context.getSharedPreferences(context.getResources().getString(R.string.MY_PREFS_ACCENT), MODE_PRIVATE).edit();
            editorAccent.putString("accent", color);
            editorAccent.apply();
            context.getTheme().applyStyle(R.style.color4, true);
            Toast.makeText(context, "The Default Accent Color has been changed",
                    Toast.LENGTH_SHORT).show();
        } else if (color.equals("color5")) {
            editorAccent = context.getSharedPreferences(context.getResources().getString(R.string.MY_PREFS_ACCENT), MODE_PRIVATE).edit();
            editorAccent.putString("accent", color);
            editorAccent.apply();
            context.getTheme().applyStyle(R.style.color5, true);
            Toast.makeText(context, "The Default Accent Color has been changed",
                    Toast.LENGTH_SHORT).show();
        } else if (color.equals("color6")) {
            editorAccent = context.getSharedPreferences(context.getResources().getString(R.string.MY_PREFS_ACCENT), MODE_PRIVATE).edit();
            editorAccent.putString("accent", color);
            editorAccent.apply();
            context.getTheme().applyStyle(R.style.color6, true);
            Toast.makeText(context, "The Default Accent Color has been changed",
                    Toast.LENGTH_SHORT).show();
        } else if (color.equals("color7")) {
            editorAccent = context.getSharedPreferences(context.getResources().getString(R.string.MY_PREFS_ACCENT), MODE_PRIVATE).edit();
            editorAccent.putString("accent", color);
            editorAccent.apply();
            context.getTheme().applyStyle(R.style.color7, true);
            Toast.makeText(context, "The Default Accent Color has been changed",
                    Toast.LENGTH_SHORT).show();
        } else if (color.equals("color8")) {
            editorAccent = context.getSharedPreferences(context.getResources().getString(R.string.MY_PREFS_ACCENT), MODE_PRIVATE).edit();
            editorAccent.putString("accent", color);
            editorAccent.apply();
            context.getTheme().applyStyle(R.style.color8, true);
            Toast.makeText(context, "The Default Accent Color has been changed",
                    Toast.LENGTH_SHORT).show();
        }
        else if(color.equals("color9")){
            editorAccent = context.getSharedPreferences(context.getResources().getString(R.string.MY_PREFS_ACCENT), MODE_PRIVATE).edit();
            editorAccent.putString("accent", color);
            editorAccent.apply();
            context.getTheme().applyStyle(R.style.color9, true);
            Toast.makeText(context, "The Default Accent Color has been changed",
                    Toast.LENGTH_SHORT).show();
        }  else if(color.equals("color10")){
            editorAccent = context.getSharedPreferences(context.getResources().getString(R.string.MY_PREFS_ACCENT), MODE_PRIVATE).edit();
            editorAccent.putString("accent", color);
            editorAccent.apply();
            context.getTheme().applyStyle(R.style.color10, true);
            Toast.makeText(context, "The Default Accent Color has been changed",
                    Toast.LENGTH_SHORT).show();
        }

    }
}
