package com.maromvz.spaserver.utils;

import com.maromvz.spaserver.entities.WorkSchedule;

public class GeneralUtils {
    public static WorkSchedule.WeekDay getDayOfWeek(int weekDayNumber) {
        return switch (weekDayNumber) {
            case 2 -> WorkSchedule.WeekDay.TUESDAY;
            case 3 -> WorkSchedule.WeekDay.WEDNESDAY;
            case 4 -> WorkSchedule.WeekDay.THURSDAY;
            case 5 -> WorkSchedule.WeekDay.FRIDAY;
            case 6 -> WorkSchedule.WeekDay.SATURDAY;
            case 7 -> WorkSchedule.WeekDay.SUNDAY;
            default -> WorkSchedule.WeekDay.MONDAY;
        };
    }
}
