package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.repository.EventRepository;
import com.example.eventmanagerapp.domain.model.Event;

import java.util.List;

/**
 * Use Case - Lấy danh sách Events
 */
public class GetEventsUseCase {

    private final EventRepository repository;

    public GetEventsUseCase(Context context) {
        this.repository = EventRepository.getInstance(context);
    }

    /**
     * Lấy tất cả events
     */
    public List<Event> getAllEvents() {
        return repository.getAllEvents();
    }

    /**
     * Lấy event theo ID
     */
    public Event getEventById(int eventId) {
        return repository.getEventById(eventId);
    }
}