package williamroocka.tut.onboard.utils;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import williamroocka.tut.onboard.MainActivity;
import williamroocka.tut.onboard.MyApplication;
import williamroocka.tut.onboard.R;

public class GeofenceTrasitionService extends IntentService {

    private static final String TAG = GeofenceTrasitionService.class.getSimpleName();
    public static final int GEOFENCE_NOTIFICATION_ID = 0;

    public static Intent makeNotificationIntent(final Context applicationContext,
                                                final String msg) {
        return new Intent(MyApplication.mInstance, MainActivity.class);
    }


    public GeofenceTrasitionService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve the Geofencing intent
        final GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Handling errors
        if (geofencingEvent.hasError()) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMsg);
            return;
        }

        // Retrieve GeofenceTrasition
        final int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Get the geofence that were triggered
            final List<Geofence> triggeringGeofences =
                    geofencingEvent.getTriggeringGeofences();
            // Create a detail message with Geofences received
            final String geofenceTransitionDetails =
                    getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences);
            // Send notification details as a String
            sendNotification(geofenceTransitionDetails);
        }
    }

    // Create a detail message with Geofences received
    private String getGeofenceTrasitionDetails(final int geoFenceTransition,
                                               final List<Geofence> triggeringGeofences) {
        // get the ID of each geofence triggered
        final ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for (final Geofence geofence : triggeringGeofences) {
            triggeringGeofencesList.add(geofence.getRequestId());
        }

        String status = null;
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
            status = "Entering ";
        else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
            status = "Exiting ";
        return status + TextUtils.join(", ", triggeringGeofencesList);
    }

    // Send a notification
    private void sendNotification(final String msg) {
        Log.i(TAG, "sendNotification: " + msg);

        // Intent to start the main Activity
        final Intent notificationIntent = MainActivity.makeNotificationIntent(
                getApplicationContext(), msg
        );

        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificatioMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));
    }

    // Create a notification
    private Notification createNotification(final String msg,
                                            final PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.drawable.ic_cast_dark)
                .setColor(Color.RED)
                .setContentTitle(msg)
                .setContentText("Geofence Notification!")
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }

    // Handle errors
    private static String getErrorString(final int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }
}
