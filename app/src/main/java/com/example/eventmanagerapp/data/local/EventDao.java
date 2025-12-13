package com.example.eventmanagerapp.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.eventmanagerapp.domain.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object - Chỉ lo truy cập database
 * Không có logic nghiệp vụ
 */
public class EventDao {

    private final AppDatabase dbHelper;

    // Tên cột
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_NOTE = "note";
    private static final String COL_START_TIME = "start_time";
    private static final String COL_END_TIME = "end_time";
    private static final String COL_REMIND = "remind_before";
    private static final String TABLE_EVENT = "events";

    public EventDao(Context context) {
        this.dbHelper = AppDatabase.getInstance(context);
    }

    /**
     * Thêm event mới
     * @return ID của event vừa tạo
     */
    public long insert(Event event) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_TITLE, event.getTitle());
        values.put(COL_NOTE, event.getNote());
        values.put(COL_START_TIME, event.getStartTime());
        values.put(COL_END_TIME, event.getEndTime());
        values.put(COL_REMIND, event.getRemindBefore());

        long id = db.insert(TABLE_EVENT, null, values);
        db.close();
        return id;
    }

    /**
     * Cập nhật event
     * @return số dòng bị ảnh hưởng
     */
    public int update(Event event) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_TITLE, event.getTitle());
        values.put(COL_NOTE, event.getNote());
        values.put(COL_START_TIME, event.getStartTime());
        values.put(COL_END_TIME, event.getEndTime());
        values.put(COL_REMIND, event.getRemindBefore());

        int rows = db.update(
                TABLE_EVENT,
                values,
                COL_ID + "=?",
                new String[]{String.valueOf(event.getId())}
        );

        db.close();
        return rows;
    }

    /**
     * Xoá event theo ID
     * @return số dòng bị xoá
     */
    public int delete(int eventId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(
                TABLE_EVENT,
                COL_ID + "=?",
                new String[]{String.valueOf(eventId)}
        );
        db.close();
        return rows;
    }

    /**
     * Lấy event theo ID
     * @return Event hoặc null nếu không tìm thấy
     */
    public Event getById(int eventId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_EVENT,
                null,
                COL_ID + "=?",
                new String[]{String.valueOf(eventId)},
                null, null, null
        );

        Event event = null;
        if (cursor != null && cursor.moveToFirst()) {
            event = cursorToEvent(cursor);
            cursor.close();
        }

        db.close();
        return event;
    }

    /**
     * Lấy tất cả events, sắp xếp theo thời gian bắt đầu
     */
    public List<Event> getAll() {
        List<Event> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_EVENT,
                null, null, null, null, null,
                COL_START_TIME + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToEvent(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    /**
     * Xoá toàn bộ events (dùng cho debug/testing)
     */
    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_EVENT, null, null);
        db.close();
    }

    /**
     * Chuyển Cursor thành Event object
     */
    private Event cursorToEvent(Cursor cursor) {
        Event event = new Event();
        event.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        event.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
        event.setNote(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE)));
        event.setStartTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_START_TIME)));
        event.setEndTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_END_TIME)));
        event.setRemindBefore(cursor.getInt(cursor.getColumnIndexOrThrow(COL_REMIND)));
        return event;
    }
}