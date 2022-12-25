import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.project.diary.Control.PreferencesManager.PreferencesManagerForStasticMineActivity;
import com.project.diary.Control.PreferencesManager.PreferencesManagerNotify;
import com.project.diary.R;
import com.project.diary.View.Activity.MainActivity;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NotificationScheduler {
    public static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
    private Context mContext;
    private Map<String, String> mSchedule;
    private int mNotificationId;

    public Context getmContext() {
        return mContext;
    }

    public NotificationScheduler(Context context, Map<String, String> schedule) {
        mContext = context;
        mSchedule = schedule;
        mNotificationId = 234;
    }

    public void scheduleNotifications() {
        PreferencesManagerNotify.initializeInstance(getmContext());
        PreferencesManagerNotify prefManager = PreferencesManagerNotify.getInstance();
        if(!prefManager.readSetup()){
            for (Map.Entry<String, String> entry : mSchedule.entrySet()) {
                mNotificationId += 7;
                String content = entry.getKey();
                String time = entry.getValue();

                // Parse the scheduled time from the schedule data
                String[] scheduledTimeParts = time.split(":");
                int scheduledHour = Integer.parseInt(scheduledTimeParts[0]);
                int scheduledMinute = Integer.parseInt(scheduledTimeParts[1]);

                // Set the scheduled time to the calendar object
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, scheduledHour);
                calendar.set(Calendar.MINUTE, scheduledMinute);
                calendar.set(Calendar.SECOND, 0);

                // Check if the scheduled time has already passed for today
                Calendar currentTime = Calendar.getInstance();
                if (calendar.before(currentTime)) {
                    // If the scheduled time has passed, schedule the notification for the next day
                    calendar.add(Calendar.DATE, 1);
                }
                // Set up the notification with the scheduled time
                Notification.Builder builder = new Notification.Builder(mContext, NOTIFICATION_CHANNEL_ID)
                        .setContentTitle(content)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                        .setAutoCancel(true);

                // Set up the notification action
                Intent actionIntent = new Intent(mContext, MainActivity.class);
                PendingIntent actionPendingIntent = PendingIntent.getActivity(mContext, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(actionPendingIntent);

                // Set up the notification channel (required for Android 8.0 and higher)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.createNotificationChannel(channel);
                }

                // Set up the notification alarm
                Notification notification = builder.build();
                Intent notificationIntent = new Intent(mContext, NotificationPublisher.class);
                notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, mNotificationId);
                notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
                PendingIntent notificationPendingIntent = PendingIntent.getBroadcast(mContext, mNotificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getmContext().getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, notificationPendingIntent);
            }
            prefManager.saveSetup(true);
        }
        }
    }
