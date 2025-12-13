package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.repository.EventRepository;
import com.example.eventmanagerapp.utils.AlarmScheduler;

/**
 * Use Case - Xoá Event
 */
public class DeleteEventUseCase {

    private final EventRepository repository;
    private final AlarmScheduler alarmScheduler;

    public DeleteEventUseCase(Context context) {
        this.repository = EventRepository.getInstance(context);
        this.alarmScheduler = new AlarmScheduler(context);
    }

    /**
     * Xoá event
     */
    public Result execute(int eventId) {
        // 1. Cancel alarm trước
        alarmScheduler.cancelAlarm(eventId);

        // 2. Xoá khỏi DB
        boolean deleted = repository.deleteEvent(eventId);

        if (!deleted) {
            return Result.error("Không thể xoá sự kiện");
        }

        return Result.success();
    }

    /**
     * Result class
     */
    public static class Result {
        private final boolean success;
        private final String errorMessage;

        private Result(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public static Result success() {
            return new Result(true, null);
        }

        public static Result error(String message) {
            return new Result(false, message);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}