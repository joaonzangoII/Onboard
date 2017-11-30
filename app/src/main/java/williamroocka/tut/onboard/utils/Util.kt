package williamroocka.tut.onboard.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.support.annotation.NonNull
import android.util.Log
import android.widget.Toast

import com.android.volley.VolleyError

import org.json.JSONException
import org.json.JSONObject

import java.io.IOException
import java.util.List
import java.util.Locale

open class Util {
    companion object {
        fun trimMessage(json: String,
                        key: String): String {
            var trimmedString: String
            try {
                var obj = JSONObject(json)
                trimmedString = obj.getString(key)
            } catch (e: JSONException) {
                e.printStackTrace()
                return ""
            }

            return trimmedString
        }

        //Somewhere that has access to a context
        fun displayMessage(context: Context,
                           toastString: String) {
            Toast.makeText(context, toastString, Toast.LENGTH_LONG).show()
        }

        fun logVolleyMessage(error: VolleyError,
                             TAG: String) {
            Log.e(TAG, if (error.message != null) error.message else error.toString())
        }

        fun getVolleyMessage(error: VolleyError): String {
            var body: String = ""
            //get status code here
            if (error.message != null) {
                body = error.message.toString()
                //  if (error.networkResponse != null) {
                //           final String statusCode = String.valueOf(error.networkResponse.statusCode)
                //            //get response body and parse with appropriate encoding
                //            if (error.networkResponse.data != null) {
                //                try {
                //                    body = new String(error.networkResponse.data, "UTF-8")
                //                } catch (final UnsupportedEncodingException e) {
                //                    e.printStackTrace()
                //                }
                //            }
            } else {
                body = error.toString()
            }

            return body
        }

        fun capitalize(line: String): String = Character.toUpperCase(line[0]) + line.substring(1)

        fun getAddress(context: Context,
                       @NonNull latitude: Double,
                       @NonNull longitude: Double): String {
            var geocoder: Geocoder
            val addresses: kotlin.collections.MutableList<Address>
            geocoder = Geocoder(context, Locale.getDefault())
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1)
                // If any additional address line present than only, check with max available address lines
                // by getMaxAddressLineIndex()
                if (addresses.size > 0) {
                    val address: String = addresses.get(0).getAddressLine(0)
                    val city: String = addresses.get(0).getLocality()
                    val state: String = addresses.get(0).getAdminArea()
                    val country: String = addresses.get(0).getCountryName()
                    val postalCode: String = addresses.get(0).getPostalCode()
                    // Only if available else return NULL
                    val knownName: String = addresses.get(0).getFeatureName()
                    return address
                }

                return "No Address found"
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return "No Address found"
        }
    }
}
