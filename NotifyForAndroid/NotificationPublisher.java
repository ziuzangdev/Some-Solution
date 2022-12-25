/**

This class represents a broadcast receiver for handling notifications in Android.
It receives an intent containing a notification and a notification ID, and uses a notification manager
to display the notification to the user.
*/
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class NotificationPublisher extends BroadcastReceiver {
public static final String NOTIFICATION_ID = "1";
public static final String NOTIFICATION = "notification";
/**
 * This method is called when the broadcast receiver receives an intent.
 * It retrieves the notification and notification ID from the intent and uses a notification manager
 * to display the notification to the user.
 * @param context The context in which the receiver is running.
 * @param intent The intent being received.
 */
public void onReceive(Context context, Intent intent) {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    Notification notification = intent.getParcelableExtra(NOTIFICATION);
    int id = intent.getIntExtra(NOTIFICATION_ID, 0);
    notificationManager.notify(id, notification);
  }
}
