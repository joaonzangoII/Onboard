package williamroocka.tut.onboard.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import williamroocka.tut.onboard.models.Time;
import williamroocka.tut.onboard.models.User;
import williamroocka.tut.onboard.utils.Constant;

public class Session {
    private static String TAG = Session.class.getSimpleName();
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    @SuppressLint("CommitPrefEdits")
    public Session(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(Constant.PREF_NAME, Constant.PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(final boolean isLoggedIn) {
        editor.putBoolean(Constant.KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public void setLoggedinUser(final String user) {
        editor.putString(Constant.KEY_LOGGED_IN_USER, user);
        editor.apply();
    }

    public User getLoggedInUser() {
        final Gson gson = new GsonBuilder().create();
        final Type type = new TypeToken<User>() {
        }.getType();

        final String data = pref.getString(Constant.KEY_LOGGED_IN_USER, null);
        return gson.fromJson(data, type);
    }

    public void setTime(final String user) {
        editor.putString(Constant.KEY_TIME, user);
        editor.apply();
    }

    public Time getTime() {
        final Gson gson = new GsonBuilder().create();
        final Type type = new TypeToken<Time>() {
        }.getType();

        final String data = pref.getString(Constant.KEY_TIME, null);
        return gson.fromJson(data, type);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(Constant.KEY_IS_LOGGED_IN, false);
    }

    public void loggoutUser() {
        editor.putBoolean(Constant.KEY_IS_LOGGED_IN, false);
        editor.putString(Constant.KEY_LOGGED_IN_USER, null);
        editor.apply();
    }


    public void setServerUrl(final String serverUrl) {
        editor.putString(Constant.KEY_SERVER_URL, serverUrl);
        editor.apply();
    }

    public String getServerUrl() {
        return pref.getString(Constant.KEY_SERVER_URL, Constant.KEY_DEFAULT_SERVER_URL);
    }
}
