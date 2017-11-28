package williamroocka.tut.onboard.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import williamroocka.tut.onboard.MyApplication;

public class Function {

    private static ProgressDialog pDialog;

    public static void showToast(final Context context,
                                 final String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void setLoading(final Context context,
                                  final boolean isLoading) {
        setLoading(context, "Loading...", isLoading);
    }


    public static void setLoading(final Context context,
                                  final String message,
                                  final boolean isLoading) {
        if (isLoading) {
            pDialog = new ProgressDialog(context);
            pDialog.show();
            pDialog.setMessage(message);
        } else {
            pDialog.dismiss();
        }
    }

    public static Context getApplicationContext() {
        return MyApplication.getInstance();
    }
}
