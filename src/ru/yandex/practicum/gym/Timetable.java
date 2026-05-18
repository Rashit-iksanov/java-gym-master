package ru.yandex.practicum.gym;

import java.util.*;
import java.util.stream.Collectors;

public class Timetable {

    private Map<DayOfWeek, TreeMap<TimeOfDay, List<TrainingSession>>> timetable;

    public Timetable() {
        timetable = new HashMap<>();
    }

    public void addNewTrainingSession(TrainingSession trainingSession) {
        DayOfWeek day = trainingSession.getDayOfWeek();
        TimeOfDay time = trainingSession.getTimeOfDay();
        Coach newCoach = trainingSession.getCoach();

        // Получаем или создаём расписание на день
        TreeMap<TimeOfDay, List<TrainingSession>> daySchedule =
                timetable.computeIfAbsent(day, k -> new TreeMap<>());

        // Проверяем, есть ли уже тренировки в это время
        List<TrainingSession> sessionsAtTime = daySchedule.get(time);
        if (sessionsAtTime != null) {
            // Ищем, не ведёт ли этот тренер уже занятие в это время
            boolean coachBusy = sessionsAtTime.stream()
                    .anyMatch(session -> session.getCoach().equals(newCoach));

            if (coachBusy) {
                throw new IllegalArgumentException(
                        "Тренер " + newCoach.getSurname() + " уже ведёт тренировку в "
                                + day + " в " + time.getHours() + ":" + String.format("%02d", time.getMinutes())
                );
            }
        }

        // Если тренер свободен — добавляем тренировку
        daySchedule.computeIfAbsent(time, k -> new ArrayList<>()).add(trainingSession);
    }

    public TreeMap<TimeOfDay, List<TrainingSession>> getTrainingSessionsForDay(DayOfWeek dayOfWeek) {
        TreeMap<TimeOfDay, List<TrainingSession>> daySchedule = timetable.get(dayOfWeek);

        if (daySchedule == null || daySchedule.isEmpty()) {
            return new TreeMap<>(); // Возвращаем пустой TreeMap, если расписание пустое
        }

        return daySchedule;
    }

    public List<TrainingSession> getTrainingSessionsForDayAndTime(DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        //как реализовать, тоже непонятно, но сложность должна быть О(1)
        TreeMap<TimeOfDay, List<TrainingSession>> daySchedule = timetable.get(dayOfWeek);

        if (daySchedule == null) {
            return new ArrayList<>();
        }

        List<TrainingSession> sessions = daySchedule.get(timeOfDay);
        // Возвращаем копию, чтобы защитить внутреннее состояние
        return sessions != null ? new ArrayList<>(sessions) : new ArrayList<>();
    }

    public Map<Coach, Integer> getCountByCoaches() {
        Map<Coach, Integer> coachCounts = new HashMap<>();

        // Проходим по всем дням
        for (TreeMap<TimeOfDay, List<TrainingSession>> daySchedule : timetable.values()) {
            // Проходим по всем временным слотам
            for (List<TrainingSession> sessions : daySchedule.values()) {
                // Проходим по всем тренировкам в слоте
                for (TrainingSession session : sessions) {
                    Coach coach = session.getCoach();
                    coachCounts.merge(coach, 1, Integer::sum);
                }
            }
        }

        // Сортируем по количеству тренировок
        return coachCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<Coach, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }
}
