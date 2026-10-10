package com.shahid.nid;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class AccentManager {

    public AccentManager() { }

    public static String getSelectedColor(Context context) {
        return context.getSharedPreferences(context.getString(R.string.MY_PREFS_ACCENT), MODE_PRIVATE)
                .getString("accent", "color1");
    }

    public static int getColorResource(String color) {
        switch (color) {
            case "color2": return R.color.accent2;
            case "color3": return R.color.accent3;
            case "color4": return R.color.accent4;
            case "color5": return R.color.accent5;
            case "color6": return R.color.accent6;
            case "color7": return R.color.accent7;
            case "color8": return R.color.accent8;
            case "color9": return R.color.accent9;
            case "color10": return R.color.accent10;
            default: return R.color.accent1;
        }
    }

    public static void applySavedAccent(Context context) {
        int style;
        switch (getSelectedColor(context)) {
            case "color2": style = R.style.color2; break;
            case "color3": style = R.style.color3; break;
            case "color4": style = R.style.color4; break;
            case "color5": style = R.style.color5; break;
            case "color6": style = R.style.color6; break;
            case "color7": style = R.style.color7; break;
            case "color8": style = R.style.color8; break;
            case "color9": style = R.style.color9; break;
            case "color10": style = R.style.color10; break;
            default: style = R.style.color1;
        }
        context.getTheme().applyStyle(style, true);
    }

    public void checkColor(Context context, String color) {
        if (!color.matches("color(?:10|[1-9])")) return;
        context.getSharedPreferences(context.getString(R.string.MY_PREFS_ACCENT), MODE_PRIVATE)
                .edit().putString("accent", color).apply();
        applySavedAccent(context);
        Toast.makeText(context, R.string.accent_color_changed, Toast.LENGTH_SHORT).show();
    }
}
