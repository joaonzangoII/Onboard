package williamroocka.tut.onboard.requests;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import williamroocka.tut.onboard.managers.Route;
import williamroocka.tut.onboard.managers.Session;
import williamroocka.tut.onboard.network.VolleySingleton;
import williamroocka.tut.onboard.utils.Constant;
import williamroocka.tut.onboard.utils.Function;
import williamroocka.tut.onboard.utils.Util;

public class AuthRequest {
    private static final String TAG = AuthRequest.class.getSimpleName();

    public static void login(final Session session,
                             final Context context,
                             final String email,
                             final String password,
                             final Handler requestHandler) {
        final Message msg = requestHandler.obtainMessage();
        final Bundle bundle = new Bundle();
        final String tag_string_req = "req_login";
        Function.setLoading(context, "Logging  in...", true);
        final String url = session.getServerUrl() + Route.LOGIN;
        final StringRequest strReq = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.d(TAG, "Login Response: " + response);
                        // Function.setLoading(false);
                        try {
                            final JSONObject jObj = new JSONObject(response);
                            // Check for error node in json
                            if (jObj.has(Constant.KEY_USER)) {
                                final JSONObject user = jObj.getJSONObject(Constant.KEY_USER);
                                session.setLogin(true);
                                session.setLoggedinUser(user.toString());
                                Log.e("SESSION", "LOGGED IN");
                                bundle.putBoolean(Constant.KEY_IS_LOGGED_IN, true);
                                bundle.putBoolean(Constant.KEY_SUCCESS, true);
                                msg.setData(bundle);
                                requestHandler.sendMessage(msg);
                            } else {
                                session.setLogin(false);
                                session.setLoggedinUser(null);
                                bundle.putBoolean(Constant.KEY_IS_LOGGED_IN, false);
                                bundle.putBoolean(Constant.KEY_SUCCESS, false);
                                msg.setData(bundle);
                                requestHandler.sendMessage(msg);
                                final String errorMsg = jObj.getString(Constant.KEY_MESSAGES);
                                Toast.makeText(Function.getApplicationContext(),
                                        errorMsg,
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Function.getApplicationContext(),
                                    "Json error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        Function.setLoading(context, false);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(final VolleyError error) {
                Function.setLoading(context, false);
                String json = null;
                final NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    switch (response.statusCode) {
                        case 422:
                            json = new String(response.data);
                            json = Util.trimMessage(json, Constant.KEY_ERRORS);
                            if (json != null) Util.displayMessage(context, json);
                            break;
                    }
                }

                session.setLogin(false);
                session.setLoggedinUser(null);
                bundle.putBoolean(Constant.KEY_IS_LOGGED_IN, false);
                bundle.putBoolean(Constant.KEY_SUCCESS, false);
                msg.setData(bundle);
                requestHandler.sendMessage(msg);
            }
        }) {
            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                final String str = "{\""
                        + Constant.KEY_EMAIL
                        + "\":\""
                        + email
                        + "\",\""
                        + Constant.KEY_PASSWORD
                        + "\":\""
                        + password
                        + "\"}";
                Log.e("STRING", str);
                return str.getBytes();
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(strReq);
    }

    public static void register(final Session session,
                                final Context context,
                                final String name,
                                final String id_number,
                                final String email,
                                final String password,
                                final String confirmPassword,
                                final Handler requestHandler) {
        final Message msg = requestHandler.obtainMessage();
        final Bundle bundle = new Bundle();
        final String tag_string_req = "req_gegister";
        Function.setLoading(context, "Signing Up...", true);
        final String url = session.getServerUrl() + Route.REGISTER;
        final StringRequest strReq = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.d(TAG, "Registration Response: " + response);
                        // Function.setLoading(false);
                        try {
                            final JSONObject jObj = new JSONObject(response);
                            // Check for error node in json
                            if (jObj.has(Constant.KEY_USER)) {
                                session.setLogin(false);
                                session.setLoggedinUser(null);
                                bundle.putBoolean(Constant.KEY_SUCCESS, true);
                                msg.setData(bundle);
                                requestHandler.sendMessage(msg);
                            } else {
                                session.setLogin(false);
                                session.setLoggedinUser(null);
                                bundle.putBoolean(Constant.KEY_SUCCESS, false);
                                msg.setData(bundle);
                                requestHandler.sendMessage(msg);
                                final String errorMsg = jObj.getString(Constant.KEY_MESSAGES);
                                Toast.makeText(Function.getApplicationContext(),
                                        errorMsg,
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Function.getApplicationContext(),
                                    "Json error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        Function.setLoading(context, false);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Function.setLoading(context, false);
                String json = null;
                final NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    switch (response.statusCode) {
                        case 422:
                            json = new String(response.data);
                            json = Util.trimMessage(json, Constant.KEY_ERRORS);
                            if (json != null) Util.displayMessage(context, json);
                            break;
                    }
                }

                session.setLogin(false);
                session.setLoggedinUser(null);
                bundle.putBoolean(Constant.KEY_SUCCESS, false);
                msg.setData(bundle);
                requestHandler.sendMessage(msg);
            }
        }) {
            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                final String str = "{\""
                        + Constant.KEY_NAME
                        + "\":\""
                        + name
                        + "\",\""
                        + Constant.KEY_EMAIL
                        + "\":\""
                        + email
                        + "\",\""
                        + Constant.KEY_ID_NUMBER
                        + "\":\""
                        + id_number
                        + "\",\""
                        + Constant.KEY_PASSWORD
                        + "\":\""
                        + password
                        + "\",\""
                        + Constant.KEY_CONFIRM_PASSWORD
                        + "\":\""
                        + confirmPassword
                        + "\"}";
                Log.e("STRING", str);
                return str.getBytes();
            }

            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };

        VolleySingleton.getInstance(context).addToRequestQueue(strReq);
    }


    public static void clockIn(final Session session,
                               final Context context,
                               final Handler requestHandler) {
        final Message msg = requestHandler.obtainMessage();
        final Bundle bundle = new Bundle();
        final String tag_string_req = "req_clock_in";
        Function.setLoading(context, "Clock In ...", true);
        final String url = session.getServerUrl()
                + Route.CLOCK
                + "/"
                + session.getLoggedInUser().id
                +  "/entries";
        final StringRequest strReq = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.d(TAG, "Clock In Response: " + response);
                        // Function.setLoading(false);
                        try {
                            final JSONObject jObj = new JSONObject(response);
                            // Check for error node in json
                            if (!jObj.has(Constant.KEY_ERRORS)) {
                                session.setTime(response);
                                bundle.putBoolean(Constant.KEY_SUCCESS, true);
                                bundle.putInt(Constant.KEY_CLOCK, 1);
                                msg.setData(bundle);
                                requestHandler.sendMessage(msg);
                            } else {
                                session.setTime(null);
                                bundle.putBoolean(Constant.KEY_SUCCESS, false);
                                msg.setData(bundle);
                                requestHandler.sendMessage(msg);
                                final String errorMsg = jObj.getString(Constant.KEY_MESSAGES);
                                Toast.makeText(Function.getApplicationContext(),
                                        errorMsg,
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Function.getApplicationContext(),
                                    "Json error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        Function.setLoading(context, false);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(final VolleyError error) {
                Function.setLoading(context, false);
                String json = null;
                final NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    switch (response.statusCode) {
                        case 401:
                            Util.displayMessage(context, response.data.toString());
                            break;
                        case 422:
                            json = new String(response.data);
                            json = Util.trimMessage(json, Constant.KEY_ERRORS);
                            if (json != null) Util.displayMessage(context, json);
                            break;
                    }
                }

                session.setTime(null);
                bundle.putBoolean(Constant.KEY_SUCCESS, false);
                msg.setData(bundle);
                requestHandler.sendMessage(msg);
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(strReq);
    }


    public static void clockOut(final Session session,
                               final Context context,
                               final Long time_id,
                               final Handler requestHandler) {
        final Message msg = requestHandler.obtainMessage();
        final Bundle bundle = new Bundle();
        final String tag_string_req = "req_clock_out";
        Function.setLoading(context, "Clock Out...", true);
        final String url = session.getServerUrl()
                + Route.CLOCK
                + "/"
                + session.getLoggedInUser().id
                +  "/entries/"
                + time_id;
        final StringRequest strReq = new StringRequest(
                Request.Method.PUT,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.d(TAG, "Clock Out  Response: " + response);
                        // Function.setLoading(false);
                        try {
                            final JSONObject jObj = new JSONObject(response);
                            // Check for error node in json
                            if (!jObj.has(Constant.KEY_ERRORS)) {
                                session.setTime(null);
                                bundle.putInt(Constant.KEY_CLOCK, 2);
                                bundle.putBoolean(Constant.KEY_SUCCESS, true);
                                msg.setData(bundle);
                                requestHandler.sendMessage(msg);
                            } else {
                                session.setTime(null);
                                bundle.putBoolean(Constant.KEY_SUCCESS, false);
                                msg.setData(bundle);
                                requestHandler.sendMessage(msg);
                                final String errorMsg = jObj.getString(Constant.KEY_MESSAGES);
                                Toast.makeText(Function.getApplicationContext(),
                                        errorMsg,
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Function.getApplicationContext(),
                                    "Json error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        Function.setLoading(context, false);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(final VolleyError error) {
                Function.setLoading(context, false);
                String json = null;
                final NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    switch (response.statusCode) {
                        case 401:
                            Util.displayMessage(context, response.data.toString());
                            break;
                        case 422:
                            json = new String(response.data);
                            json = Util.trimMessage(json, Constant.KEY_ERRORS);
                            if (json != null) Util.displayMessage(context, json);
                            break;
                    }
                }

                session.setTime(null);
                bundle.putBoolean(Constant.KEY_IS_LOGGED_IN, false);
                bundle.putBoolean(Constant.KEY_SUCCESS, false);
                msg.setData(bundle);
                requestHandler.sendMessage(msg);
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(strReq);
    }
}
