package com.example.eventmanagerapp.domain.usecase;

import android.content.Context;

import com.example.eventmanagerapp.data.repository.EventRepository;
import com.example.eventmanagerapp.domain.model.Event;
import com.example.eventmanagerapp.utils.AlarmScheduler;
import com.example.eventmanagerapp.utils.DateTimeHelper;
import com.example.eventmanagerapp.utils.Validator;

import java.util.Calendar;

/**
 * Use Case - Tạo Event
 * Chứa toàn bộ business logic cho việc tạo event
 */
public class CreateEventUseCase {

    private final EventRepository repository;
    private final AlarmScheduler alarmScheduler;

    public CreateEventUseCase(Context context) {
        this.repository = EventRepository.getInstance(context);
        this.alarmScheduler = new AlarmScheduler(context);
    }

    /**
     * Tạo event mới
     * @param remindBefore Nhắc trước bao nhiêu phút (0 = đúng giờ)
     * @return Result object chứa thông tin kết quả
     */
    public Result execute(String title, String note, String dateTag,
                          int startHour, int startMinute,
                          int endHour, int endMinute,
                          int remindBefore) {

        // 1. Validate input
        String error = validateInput(title, dateTag, startHour, startMinute,
                endHour, endMinute);
        if (error != null) {
            return Result.error(error);
        }

        try {
            // 2. Tạo Calendar objects
            Calendar startCal = DateTimeHelper.createDateTime(dateTag, startHour, startMinute);
            Calendar endCal = DateTimeHelper.createDateTime(dateTag, endHour, endMinute);

            long startMillis = startCal.getTimeInMillis();
            long endMillis = endCal.getTimeInMillis();

            // 3. Validate time range
            error = Validator.validateTimeRange(startMillis, endMillis);
            if (error != null) {
                return Result.error(error);
            }

            // 4. ✅ CHECK THỜI GIAN TRƯỚC KHI LƯU DB
            error = Validator.validateFutureTime(startMillis);
            if (error != null) {
                // ❌ KHÔNG LƯU DB nếu thời gian đã qua
                return Result.error(error);
            }

            // 5. Tạo Event object
            Event event = new Event(0, title, note, startMillis, endMillis, remindBefore);

            // 6. Lưu vào DB (chỉ khi validation pass hết)
            long eventId = repository.createEvent(event);
            if (eventId <= 0) {
                return Result.error("Không thể lưu sự kiện");
            }

            // 7. Tính thời điểm nhắc nhở
            long alarmTime = startMillis - (remindBefore * 60 * 1000L);

            // 8. Schedule alarm
            boolean alarmSet = alarmScheduler.scheduleAlarm(
                    (int) eventId,
                    title,
                    alarmTime
            );

            if (!alarmSet) {
                // ⚠️ Đã lưu DB nhưng không schedule được alarm
                // Có thể xử lý: xóa event hoặc để user biết
                return Result.errorNeedPermission("Cần quyền 'Báo chính xác' để đặt nhắc nhở");
            }

            return Result.success((int) eventId);

        } catch (Exception e) {
            return Result.error("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Validate input
     */
    private String validateInput(String title, String dateTag,
                                 int startHour, int startMinute,
                                 int endHour, int endMinute) {

        String error = Validator.validateTitle(title);
        if (error != null) return error;

        error = Validator.validateDateFormat(dateTag);
        if (error != null) return error;

        error = Validator.validateStartTime(startHour, startMinute);
        if (error != null) return error;

        error = Validator.validateEndTime(endHour, endMinute);
        if (error != null) return error;

        error = Validator.validateTimeRange(startHour, startMinute, endHour, endMinute);
        if (error != null) return error;

        return null;
    }

    /**
     * Result class - Kết quả của use case
     */
    public static class Result {
        private final boolean success;
        private final String errorMessage;
        private final int eventId;
        private final boolean needPermission;

        private Result(boolean success, String errorMessage, int eventId, boolean needPermission) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.eventId = eventId;
            this.needPermission = needPermission;
        }

        public static Result success(int eventId) {
            return new Result(true, null, eventId, false);
        }

        public static Result error(String message) {
            return new Result(false, message, -1, false);
        }

        public static Result errorNeedPermission(String message) {
            return new Result(false, message, -1, true);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public int getEventId() {
            return eventId;
        }

        public boolean needPermission() {
            return needPermission;
        }
    }
}