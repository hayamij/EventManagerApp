package com.example.eventmanagerapp.data.repository;

import android.content.Context;

import com.example.eventmanagerapp.data.local.EventDao;
import com.example.eventmanagerapp.domain.model.Event;

import java.util.List;

/**
 * Repository Pattern - Trung gian giữa Data Layer và Domain Layer
 * Có thể mở rộng: lấy data từ API, cache, ...
 */
public class EventRepository {

    private final EventDao eventDao;

    // Singleton
    private static EventRepository instance;

    private EventRepository(Context context) {
        this.eventDao = new EventDao(context);
    }

    public static synchronized EventRepository getInstance(Context context) {
        if (instance == null) {
            instance = new EventRepository(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Tạo event mới
     * @return ID của event vừa tạo
     */
    public long createEvent(Event event) {
        return eventDao.insert(event);
    }

    /**
     * Cập nhật event
     * @return true nếu thành công
     */
    public boolean updateEvent(Event event) {
        return eventDao.update(event) > 0;
    }

    /**
     * Xoá event
     * @return true nếu thành công
     */
    public boolean deleteEvent(int eventId) {
        return eventDao.delete(eventId) > 0;
    }

    /**
     * Lấy event theo ID
     */
    public Event getEventById(int eventId) {
        return eventDao.getById(eventId);
    }

    /**
     * Lấy tất cả events
     */
    public List<Event> getAllEvents() {
        return eventDao.getAll();
    }

    /**
     * Xoá tất cả events (for testing)
     */
    public void clearAll() {
        eventDao.deleteAll();
    }
}