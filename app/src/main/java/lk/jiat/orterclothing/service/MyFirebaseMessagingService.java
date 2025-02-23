package lk.jiat.orterclothing.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import lk.jiat.orterclothing.MainActivity;
import lk.jiat.orterclothing.OrderDetailedActivity;
import lk.jiat.orterclothing.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        try {
            Log.d(TAG, "Message data: " + message.getData());

            // Handle data message
            if (message.getData().size() > 0) {
                Map<String, String> data = message.getData();
                String type = data.get("type");

                if ("order_update".equals(type)) {
                    // Handle order update notification
                    createOrderNotification(

                            message.getNotification().getTitle(),
                            message.getNotification().getBody(),
                            data.get("orderId"),
                            data.get("orderNumber"),
                            data.get("status")
                    );
                } else {
                    // Handle regular notification
                    createDefaultNotification(
                            message.getNotification().getTitle(),
                            message.getNotification().getBody()
                    );
                }
            } else if (message.getNotification() != null) {
                // Handle simple notification
                createDefaultNotification(
                        message.getNotification().getTitle(),
                        message.getNotification().getBody()
                );
            }
        } catch (Exception e) {
           Log.e(TAG, "Error processing message: " + e.getMessage());
        }
    }

    private void createOrderNotification(String title, String body, String orderId, String orderNumber, String status) {
        try {
            String channelId = "ORTER_ORDER_UPDATES";
            int notificationId = orderId != null ? orderId.hashCode() : (int) System.currentTimeMillis();

            // Create intent for notification tap action
            Intent intent = new Intent(this, OrderDetailedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


            if (orderId != null) {
                intent.putExtra("id", orderId);
            }
            if (orderNumber != null) {
                intent.putExtra("orderNumber", orderNumber);
            }
            if (status != null) {
                intent.putExtra("status", status);
            }


            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle(title != null ? title : "Order Update")
                    .setContentText(body != null ? body : "")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(ContextCompat.getColor(this, R.color.black));

            NotificationManager manager = getSystemService(NotificationManager.class);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Order Updates",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Notifications for order updates");
                channel.enableVibration(true);
                manager.createNotificationChannel(channel);
            }

            // Show notification
            manager.notify(notificationId, builder.build());

        } catch (Exception e) {
            Log.e(TAG, "Error creating order notification: " + e.getMessage());
        }
    }

    private void createDefaultNotification(String title, String body) {
        String channelId = "ORTER_NOTIFICATION";
        int notificationId = 0;

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                notificationId,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(title != null ? title : "Notification")
                .setContentText(body != null ? body : "")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(ContextCompat.getColor(this, R.color.black));

        NotificationManager manager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("General app notifications");
            manager.createNotificationChannel(channel);
        }

        manager.notify(notificationId, builder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM token: " + token);
        // TODO: Send token to your server
    }
}