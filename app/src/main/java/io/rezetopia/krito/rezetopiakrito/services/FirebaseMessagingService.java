package io.rezetopia.krito.rezetopiakrito.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AkshayeJH on 13/07/17.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String SERVER_NOTIFICATION = "rezetopia_app_fcm";
    private static final String SERVER_STORE_NOTIFICATION = "rezetopia_app_fcm_store";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i("message service", "msg received");

        JSONObject jsonObject = new JSONObject(remoteMessage.getData());
        try {
            String title = jsonObject.getString("title");
            if (title != null && title.contentEquals(SERVER_NOTIFICATION)){

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(io.rezetopia.krito.rezetopiakrito.R.mipmap.ic_launcher)
                                .setContentTitle(jsonObject.getString("username"))
                                .setContentText(jsonObject.getString("message"));


                Intent resultIntent = new Intent();


                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                mBuilder.setContentIntent(resultPendingIntent);

                int mNotificationId = (int) System.currentTimeMillis();
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            } else if (title != null && title.contentEquals(SERVER_STORE_NOTIFICATION)){
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(io.rezetopia.krito.rezetopiakrito.R.mipmap.ic_launcher)
                                .setContentTitle(jsonObject.getString("username"))
                                .setContentText(jsonObject.getString("message"));


                Intent resultIntent = new Intent();


                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                mBuilder.setContentIntent(resultPendingIntent);

                int mNotificationId = (int) System.currentTimeMillis();
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }

            else {
                String notification_title = remoteMessage.getNotification().getTitle();
                String notification_message = remoteMessage.getNotification().getBody();

                String click_action = remoteMessage.getNotification().getClickAction();

                //String from_user_id = remoteMessage.getData().get("from_user_id");

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(io.rezetopia.krito.rezetopiakrito.R.mipmap.ic_launcher)
                                .setContentTitle(notification_title)
                                .setContentText(notification_message);


                Intent resultIntent = new Intent(click_action);
                //resultIntent.putExtra("user_id", from_user_id);


                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                mBuilder.setContentIntent(resultPendingIntent);

                int mNotificationId = (int) System.currentTimeMillis();
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
