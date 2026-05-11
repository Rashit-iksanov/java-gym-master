package ru.yandex.practicum.gym;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class TimetableTest {

    @Test
    void testGetTrainingSessionsForDaySingleSession() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        //Проверить, что за понедельник вернулось одно занятие
        List<TrainingSession> monday = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        //Проверить, что за вторник не вернулось занятий
        List<TrainingSession> tuesday = timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY);

        assertEquals(1, monday.size());
        assertEquals(singleTrainingSession, monday.get(0));
        assertTrue(tuesday.isEmpty());
    }

    @Test
    void testGetTrainingSessionsForDayMultipleSessions() {
        Timetable timetable = new Timetable();

        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");

        Group groupAdult = new Group("Акробатика для взрослых", Age.ADULT, 90);
        TrainingSession thursdayAdultTrainingSession = new TrainingSession(groupAdult, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(20, 0));

        timetable.addNewTrainingSession(thursdayAdultTrainingSession);

        Group groupChild = new Group("Акробатика для детей", Age.CHILD, 60);
        TrainingSession mondayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        TrainingSession thursdayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(13, 0));
        TrainingSession saturdayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.SATURDAY, new TimeOfDay(10, 0));

        timetable.addNewTrainingSession(mondayChildTrainingSession);
        timetable.addNewTrainingSession(thursdayChildTrainingSession);
        timetable.addNewTrainingSession(saturdayChildTrainingSession);

        // Понедельник: 1 тренировка
        assertEquals(1, timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY).size());

        // Четверг: 2 тренировки, отсортированные по времени
        List<TrainingSession> thursday = timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY);
        assertEquals(2, thursday.size());
        assertEquals(13, thursday.get(0).getTimeOfDay().getHours()); // сначала 13:00
        assertEquals(20, thursday.get(1).getTimeOfDay().getHours()); // потом 20:00

        // Вторник: пусто
        assertTrue(timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY).isEmpty());
    }

    @Test
    void testGetTrainingSessionsForDayAndTime() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        // Точное совпадение времени
        List<TrainingSession> found = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        assertEquals(1, found.size());
        assertEquals(singleTrainingSession, found.get(0));

        // Другое время — пусто
        assertTrue(timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(14, 0)).isEmpty());
    }

    @Test
    void testMultipleSessionsAtSameTime() {
        Timetable timetable = new Timetable();
        Coach coach1 = new Coach("Иванов", "Иван", "Иванович");
        Coach coach2 = new Coach("Петров", "Пётр", "Петрович");
        Group group = new Group("Гимнастика", Age.CHILD, 45);

        TrainingSession s1 = new TrainingSession(group, coach1,
                DayOfWeek.WEDNESDAY, new TimeOfDay(17, 0));
        TrainingSession s2 = new TrainingSession(group, coach2,
                DayOfWeek.WEDNESDAY, new TimeOfDay(17, 0));

        timetable.addNewTrainingSession(s1);
        timetable.addNewTrainingSession(s2);

        List<TrainingSession> at17 = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.WEDNESDAY, new TimeOfDay(17, 0));

        assertEquals(2, at17.size());
        assertTrue(at17.contains(s1));
        assertTrue(at17.contains(s2));
    }

    @Test
    void testGetCountByCoaches() {
        Timetable timetable = new Timetable();
        Coach coach1 = new Coach("Сидоров", "Алексей", "Дмитриевич");
        Coach coach2 = new Coach("Васильев", "Николай", "Сергеевич");
        Group group = new Group("Борьба", Age.ADULT, 60);

        // Coach1: 3 тренировки
        timetable.addNewTrainingSession(new TrainingSession(group, coach1,
                DayOfWeek.MONDAY, new TimeOfDay(10, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach1,
                DayOfWeek.WEDNESDAY, new TimeOfDay(10, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach1,
                DayOfWeek.FRIDAY, new TimeOfDay(10, 0)));

        // Coach2: 1 тренировка
        timetable.addNewTrainingSession(new TrainingSession(group, coach2,
                DayOfWeek.TUESDAY, new TimeOfDay(18, 0)));

        Map<Coach, Integer> counts = timetable.getCountByCoaches();

        assertEquals(2, counts.size());

        // Проверяем порядок: сначала тренер с большим количеством
        List<Coach> coaches = new ArrayList<>(counts.keySet());
        assertEquals(coach1, coaches.get(0));
        assertEquals(3, counts.get(coach1));
        assertEquals(coach2, coaches.get(1));
        assertEquals(1, counts.get(coach2));
    }

    @Test
    void testGetCountByCoachesEmpty() {
        Timetable timetable = new Timetable();
        Map<Coach, Integer> counts = timetable.getCountByCoaches();
        assertTrue(counts.isEmpty());
    }

    @Test
    void testGetCountByCoachesSameCoachDifferentTimes() {
        Timetable timetable = new Timetable();
        Coach coach = new Coach("Один", "Тренер", "Один");
        Group group = new Group("Плавание", Age.CHILD, 30);

        // Один тренер, 5 разных времён
        for (int hour = 9; hour < 14; hour++) {
            timetable.addNewTrainingSession(new TrainingSession(group, coach,
                    DayOfWeek.MONDAY, new TimeOfDay(hour, 0)));
        }

        Map<Coach, Integer> counts = timetable.getCountByCoaches();
        assertEquals(1, counts.size());
        assertEquals(5, counts.get(coach));
    }
}

