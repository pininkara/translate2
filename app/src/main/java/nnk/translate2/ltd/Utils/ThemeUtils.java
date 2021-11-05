package nnk.translate2.ltd.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import nnk.translate2.ltd.R;

/**
 * Classname ThemeUtils
 * Time: 2021/8/12 12:19
 **/

public class ThemeUtils {
    public static void set_theme(Context context) {
        SharedPreferences setting_sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String theme;
        theme = setting_sp.getString("theme", "blue");
        switch (theme) {
            case "orange":
                context.setTheme(R.style.orange);
                break;
            case "pink":
                context.setTheme(R.style.pink);
                break;
            case "red":
                context.setTheme(R.style.red);
                break;
            case "green":
                context.setTheme(R.style.green);
                break;
            case "yellow":
                context.setTheme(R.style.yellow);
                break;
            case "indigo":
                context.setTheme(R.style.indigo);
                break;
            case "blue_gray":
                context.setTheme(R.style.blue_gray);
                break;
            default:
                context.setTheme(R.style.day_default);
                break;
        }
    }
}
