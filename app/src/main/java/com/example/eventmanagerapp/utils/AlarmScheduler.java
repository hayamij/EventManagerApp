package com.example.eventmanagerapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.example.eventmanagerapp.utils.AlarmReceiver;

/**
 * AlarmScheduler - Chỉ lo scheduling và cancel alarm
 */
public class AlarmScheduler {

    private final Context context;
    private final AlarmManager alarmManager;

    public AlarmScheduler(Context context) {
        this.context = context.getApplicationContext();
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Đặt alarm cho event
     * @param eventId ID của event
     * @param title Tiêu đề event (hiển thị trong notification)
     * @param triggerAtMillis Thời điểm trigger alarm
     * @return true nếu đặt thành công, false nếu cần quyền
     */
    public boolean scheduleAlarm(int eventId, String title, long triggerAtMillis) {
        if (alarmManager == null) {
            return false;
        }

        // Kiểm tra quyền trên Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Cần mở settings để bật quyền
                return false;
            }
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("event_id", eventId);
        intent.putExtra("title", title);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                eventId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Set alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
            );
        }

        return true;
    }

    /**
     * Huỷ alarm
     */
    public void cancelAlarm(int eventId) {
        if (alarmManager == null) {
            return;
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                eventId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    /**
     * Check xem có quyền schedule exact alarm không (Android 12+)
     */
    public boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return alarmManager != null && alarmManager.canScheduleExactAlarms();
        }
        return true; // Trên Android < 12 không cần quyền này
    }

    /**
     * Mở settings để bật quyền exact alarm (Android 12+)
     */
    public void openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * Reschedule alarm (dùng khi update event)
     */
    public boolean rescheduleAlarm(int eventId, String title, long triggerAtMillis) {
        cancelAlarm(eventId);
        return scheduleAlarm(eventId, title, triggerAtMillis);
    }
}