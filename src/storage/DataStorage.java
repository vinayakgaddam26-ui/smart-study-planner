package storage;

import models.StudySession;
import models.Subject;
import models.Topic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStorage {

    // Collections used to store our data
    private List<Subject> subjects;
    private List<Topic> topics;
    private List<StudySession> studySessions;

    // Fast lookups using HashMaps
    // Maps a Subject to a list of its Topics
    private Map<Subject, List<Topic>> subjectTopicsMap;
    // Maps a specific Date to all StudySessions occurring on that day
    private Map<LocalDate, List<StudySession>> dailyProgressMap;

    public DataStorage() {
        this.subjects = new ArrayList<>();
        this.topics = new ArrayList<>();
        this.studySessions = new ArrayList<>();
        this.subjectTopicsMap = new HashMap<>();
        this.dailyProgressMap = new HashMap<>();
    }

    // --- Core Data Methods ---

    public void addSubject(Subject subject) {
        subjects.add(subject);
        subjectTopicsMap.putIfAbsent(subject, new ArrayList<>());
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
        // Ensure subject exists in our map
        subjectTopicsMap.putIfAbsent(topic.getSubject(), new ArrayList<>());
        subjectTopicsMap.get(topic.getSubject()).add(topic);
    }

    // --- Functionality: Mark topics as completed ---

    /**
     * Marks a specific topic as completed.
     */
    public void markTopicCompleted(Topic topic) {
        // Find the topic and update its status
        if (topics.contains(topic)) {
            topic.setStatus("Completed");
            System.out.println("Topic '" + topic.getName() + "' is marked as Completed!");
        }
    }

    // --- Functionality: Track daily study progress ---

    /**
     * Adds a study session to the tracker and groups it by date.
     */
    public void trackDailyStudyProgress(StudySession session) {
        studySessions.add(session);
        
        LocalDate sessionDate = session.getDate();
        dailyProgressMap.putIfAbsent(sessionDate, new ArrayList<>());
        dailyProgressMap.get(sessionDate).add(session);

        // If the session itself was completed, we could optionally complete the topic
        if (session.isCompleted()) {
            markTopicCompleted(session.getTopic());
        }
    }

    /**
     * Retrieves all study sessions for a specific day.
     */
    public List<StudySession> getDailyProgress(LocalDate date) {
        return dailyProgressMap.getOrDefault(date, new ArrayList<>());
    }
    
    /**
     * Calculates total minutes studied on a given day.
     */
    public int getTotalStudyMinutesForDay(LocalDate date) {
        List<StudySession> sessions = getDailyProgress(date);
        int totalMinutes = 0;
        for (StudySession session : sessions) {
            if (session.isCompleted()) {
                totalMinutes += session.getDurationMinutes();
            }
        }
        return totalMinutes;
    }

    // --- Functionality: Show completion percentage per subject ---

    /**
     * Calculates the completion percentage for a given subject.
     * Completes = 100 * (Completed Topics / Total Topics)
     */
    public double getCompletionPercentage(Subject subject) {
        List<Topic> subjectTopics = subjectTopicsMap.get(subject);
        
        // Return 0.0 if subject doesn't exist or has no topics
        if (subjectTopics == null || subjectTopics.isEmpty()) {
            return 0.0;
        }

        int totalTopics = subjectTopics.size();
        int completedCount = 0;

        for (Topic topic : subjectTopics) {
            if ("Completed".equalsIgnoreCase(topic.getStatus())) {
                completedCount++;
            }
        }

        return ((double) completedCount / totalTopics) * 100;
    }
    
    // --- Getters ---
    public List<Subject> getSubjects() { return subjects; }
    public List<Topic> getTopics() { return topics; }
    public List<StudySession> getStudySessions() { return studySessions; }
}
