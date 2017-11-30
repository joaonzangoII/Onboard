package williamroocka.tut.onboard.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import williamroocka.tut.onboard.models.Time;
import williamroocka.tut.onboard.models.User;
import williamroocka.tut.onboard.utils.Constant;

public class Session {
    private Context _context;
    public static String TAG = "Session";
    private static SharedPreferences.Editor editor;
    private static SharedPreferences pref;

    @SuppressLint("CommitPrefEdits")
    public Session(final Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(Constant.PREF_NAME, Constant.PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(final Boolean isLoggedIn) {
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
        return gson.fromJson(pref.getString(Constant.KEY_LOGGED_IN_USER, null), type);
    }

    public static void setTime(final String user) {
        editor.putString(Constant.KEY_TIME, user);
        editor.apply();
    }

    public Time getTime() {
        final Gson gson = new GsonBuilder().create();
        final Type type = new TypeToken<Time>() {
        }.getType();
        return gson.fromJson(pref.getString(Constant.KEY_TIME, null), type);
    }

    public void setTimeEntries(final String timeEntries) {
        editor.putString(Constant.KEY_TIME_ENTRIES, timeEntries);
        editor.apply();
    }

    public List<Time> getTimeEntries() {
        final Gson gson = new GsonBuilder().create();
        final Type type = new TypeToken<List<Time>>() {
        }.getType();
        return gson.fromJson(pref.getString(Constant.KEY_TIME_ENTRIES, null), type);
    }

    public Boolean isLoggedIn() {
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

    public String getServerUrl()

    {
        return pref.getString(Constant.KEY_SERVER_URL, Constant.KEY_DEFAULT_SERVER_URL);
    }
}
