package ru.yandex.practicum.gym;

import java.util.*;
import java.util.stream.Collectors;

public class Timetable {

    private Map<DayOfWeek, TreeMap<TimeOfDay, List<TrainingSession>>> timetable;

    public Timetable() {
        timetable = new HashMap<>();
    }

    public void addNewTrainingSession(TrainingSession trainingSession) {
        //сохраняем занятие в расписании
        DayOfWeek day = trainingSession.getDayOfWeek();
        TimeOfDay time = trainingSession.getTimeOfDay();

        // Получаем или создаём TreeMap для дня
        TreeMap<TimeOfDay, List<TrainingSession>> daySchedule =
                timetable.computeIfAbsent(day, k -> new TreeMap<>());

        // Получаем или создаём список для времени
        List<TrainingSession> sessionsAtTime =
                daySchedule.computeIfAbsent(time, k -> new ArrayList<>());

        // Добавляем тренировку
        sessionsAtTime.add(trainingSession);
    }

    public List<TrainingSession> getTrainingSessionsForDay(DayOfWeek dayOfWeek) {
        //как реализовать, тоже непонятно, но сложность должна быть О(1)
        TreeMap<TimeOfDay, List<TrainingSession>> daySchedule = timetable.get(dayOfWeek);

        if (daySchedule == null || daySchedule.isEmpty()) {
            return new ArrayList<>();
        }

        // TreeMap уже отсортирован по времени — просто собираем все значения
        List<TrainingSession> result = new ArrayList<>();
        for (List<TrainingSession> sessions : daySchedule.values()) {
            result.addAll(sessions);
        }
        return result;
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
