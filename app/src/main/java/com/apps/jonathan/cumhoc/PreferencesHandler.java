package com.apps.jonathan.cumhoc;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jonathan on 23/02/2016.
 */
public class PreferencesHandler {

    public static int getDefaultId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        return sharedPreferences.getInt(context.getString(R.string.defaultId), 0);
    }

    public static void setDefaultId(int id, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> s = new HashSet<>();

        Set<String> f_set = sharedPreferences.getStringSet(context.getString(R.string.favoriteCorrelations), s);

        editor.clear();
        editor.putInt(context.getString(R.string.defaultId), id);
        editor.putStringSet(context.getString(R.string.favoriteCorrelations), f_set);
        editor.apply();

    }

    public static ArrayList<FavoritesActivity.FavoriteCorrelation> getFavoriteCorrelations(Context context) {
        ArrayList<FavoritesActivity.FavoriteCorrelation> r = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        Set<String> corrs = sharedPreferences.getStringSet(context.getString(R.string.favoriteCorrelations), null);
        for(String s : corrs) {
            FavoritesActivity.FavoriteCorrelation c = getCorrelationFromString(s);
            if(c.getCorrelationId() == MainActivity.def_id) {
                c.setIsDefault(true);
                r.add(0, c);
            }
            else
                r.add(c);
        }
        return r;
    }

    public static FavoritesActivity.FavoriteCorrelation getCorrelationFromString(String string) {
        // "id~arg1;arg2"
        String arg1 = string.substring(string.indexOf("~") + 1, string.indexOf(";"));
        String arg2 = string.substring(string.indexOf(";") + 1);
        int id = Integer.parseInt(string.substring(0, string.indexOf("~")));
        return new FavoritesActivity.FavoriteCorrelation(id, arg1, arg2);
    }

    public static void addFavoriteCorrelation(FavoritesActivity.FavoriteCorrelation favoriteCorrelation, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String corr = favoriteCorrelation.getCorrelationId() + "~" + favoriteCorrelation.getArg1() + ";" + favoriteCorrelation.getArg2();

        Set<String> s = new HashSet<>();

        int d_id = sharedPreferences.getInt(context.getString(R.string.defaultId), 0);
        Set<String> f_set = sharedPreferences.getStringSet(context.getString(R.string.favoriteCorrelations), s);
        f_set.add(corr);

        editor.clear();
        editor.putInt(context.getString(R.string.defaultId), d_id);
        editor.putStringSet(context.getString(R.string.favoriteCorrelations), f_set);
        editor.apply();
    }

    public static void clearPrefs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


}
