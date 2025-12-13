package com.example.eventmanagerapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * DateTimeHelper - Chỉ lo format date/time
 * Tất cả format logic tập trung ở đây
 */
public class DateTimeHelper {

    // Các format patterns
    private static final SimpleDateFormat FORMAT_DISPLAY_DATE =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private static final SimpleDateFormat FORMAT_TAG_DATE =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /**
     * Format date để hiển thị: 13/12/2025
     */
    public static String formatDisplayDate(Calendar calendar) {
        return FORMAT_DISPLAY_DATE.format(calendar.getTime());
    }

    /**
     * Format date để hiển thị từ millis
     */
    public static String formatDisplayDate(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return formatDisplayDate(cal);
    }

    /**
     * Format date tag: 2025-12-13
     */
    public static String formatTagDate(Calendar calendar) {
        return FORMAT_TAG_DATE.format(calendar.getTime());
    }

    /**
     * Format date tag từ millis: 2025-12-13
     */
    public static String formatTagDate(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return formatTagDate(cal);
    }

    /**
     * Format time: 07:30 - 09:00
     */
    public static String formatTimeRange(long startMillis, long endMillis) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTimeInMillis(startMillis);
        end.setTimeInMillis(endMillis);

        return String.format(
                Locale.getDefault(),
                "%02d:%02d - %02d:%02d",
                start.get(Calendar.HOUR_OF_DAY),
                start.get(Calendar.MINUTE),
                end.get(Calendar.HOUR_OF_DAY),
                end.get(Calendar.MINUTE)
        );
    }

    /**
     * Format time đơn: 07:30
     */
    public static String formatTime(int hour, int minute) {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    /**
     * Format time từ millis: 07:30
     */
    public static String formatTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return formatTime(
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
        );
    }

    /**
     * Parse date tag thành Calendar: "2025-12-13" -> Calendar
     */
    public static Calendar parseTagDate(String dateTag) throws Exception {
        String[] parts = dateTag.split("-");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid date format");
        }

        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1; // Calendar month starts from 0
        int day = Integer.parseInt(parts[2]);

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal;
    }

    /**
     * Tạo Calendar từ date tag + time
     */
    public static Calendar createDateTime(String dateTag, int hour, int minute)
            throws Exception {
        Calendar cal = parseTagDate(dateTag);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * Lấy tên ngày: Thứ 2, Thứ 3, ...
     */
    public static String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY: return "Thứ 2";
            case Calendar.TUESDAY: return "Thứ 3";
            case Calendar.WEDNESDAY: return "Thứ 4";
            case Calendar.THURSDAY: return "Thứ 5";
            case Calendar.FRIDAY: return "Thứ 6";
            case Calendar.SATURDAY: return "Thứ 7";
            case Calendar.SUNDAY: return "Chủ nhật";
            default: return "";
        }
    }

    /**
     * Check xem event có phải buổi sáng không (< 12:00)
     */
    public static boolean isMorning(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return cal.get(Calendar.HOUR_OF_DAY) < 12;
    }

    /**
     * Lấy đầu tuần (Monday) từ một ngày bất kỳ
     */
    public static Calendar getWeekStart(Calendar date) {
        Calendar weekStart = (Calendar) date.clone();
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return weekStart;
    }

    /**
     * Tạo header text cho week calendar: "Thứ 2\n13/12/2025"
     */
    public static String formatWeekHeader(Calendar date) {
        return getDayName(date.get(Calendar.DAY_OF_WEEK)) +
                "\n" + formatDisplayDate(date);
    }
}