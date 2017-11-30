package williamroocka.tut.onboard

import android.app.Application
import android.support.multidex.MultiDex

import com.android.volley.RequestQueue

import williamroocka.tut.onboard.network.VolleySingleton

open class MyApplication : Application() {
    companion object {
        lateinit var requestQueue: RequestQueue
        lateinit var mInstance: MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        requestQueue = VolleySingleton.getInstance(getInstance()).getRequestQueue()
        MultiDex.install(this)
    }

    fun getInstance(): MyApplication {
        synchronized(this) {
            return mInstance
        }
    }

    fun getVolleyRequestQueue(): RequestQueue = requestQueue

}


