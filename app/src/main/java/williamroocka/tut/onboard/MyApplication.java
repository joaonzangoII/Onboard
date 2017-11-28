package williamroocka.tut.onboard;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.android.volley.RequestQueue;

import williamroocka.tut.onboard.network.VolleySingleton;


public class MyApplication extends Application {
    private RequestQueue requestQueue;
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        MultiDex.install(this);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getVolleyRequestQueue() {
        return requestQueue;
    }
}
