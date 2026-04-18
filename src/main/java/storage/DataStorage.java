package storage;

import model.StudySession;
import model.Subject;
import model.Topic;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStorage {
    private List<Subject> subjects;
    private List<Topic> topics;
    private List<StudySession> studySessions;

    // Transient means these won't be serialized to the file, we rebuild them via initMaps() on load
    private transient Map<Subject, List<Topic>> subjectTopicsMap;
    private transient Map<LocalDate, List<StudySession>> dailyProgressMap;

    private static final String DATA_FILE = "study_planner_data.dat";

    public DataStorage() {
        subjects = new ArrayList<>();
        topics = new ArrayList<>();
        studySessions = new ArrayList<>();
        initMaps();
        loadData();
    }

    private void initMaps() {
        subjectTopicsMap = new HashMap<>();
        dailyProgressMap = new HashMap<>();
        
        for (Subject s : subjects) {
            subjectTopicsMap.putIfAbsent(s, new ArrayList<>());
        }
        for (Topic t : topics) {
            subjectTopicsMap.putIfAbsent(t.getSubject(), new ArrayList<>());
            subjectTopicsMap.get(t.getSubject()).add(t);
        }
        for (StudySession sess : studySessions) {
            LocalDate d = sess.getDate();
            dailyProgressMap.putIfAbsent(d, new ArrayList<>());
            dailyProgressMap.get(d).add(sess);
        }
    }

    public void addSubject(Subject subject) {
        if (!subjects.contains(subject)) {
            subjects.add(subject);
            subjectTopicsMap.putIfAbsent(subject, new ArrayList<>());
            saveData();
        }
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
        subjectTopicsMap.putIfAbsent(topic.getSubject(), new ArrayList<>());
        subjectTopicsMap.get(topic.getSubject()).add(topic);
        saveData();
    }

    public void trackDailyStudyProgress(StudySession session) {
        studySessions.add(session);
        LocalDate date = session.getDate();
        dailyProgressMap.putIfAbsent(date, new ArrayList<>());
        dailyProgressMap.get(date).add(session);
        if (session.isCompleted()) {
            session.getTopic().setStatus("Completed");
        }
        saveData();
    }

    public double getCompletionPercentage(Subject subject) {
        List<Topic> subTopics = subjectTopicsMap.get(subject);
        if (subTopics == null || subTopics.isEmpty()) return 0.0;
        int completedCount = 0;
        for (Topic topic : subTopics) {
            if ("Completed".equalsIgnoreCase(topic.getStatus())) {
                completedCount++;
            }
        }
        return ((double) completedCount / subTopics.size()) * 100;
    }

    public List<Subject> getSubjects() { return subjects; }
    public List<Topic> getTopics() { return topics; }
    public List<StudySession> getStudySessions() { return studySessions; }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(subjects);
            oos.writeObject(topics);
            oos.writeObject(studySessions);
        } catch (IOException e) {
            System.err.println("Error saving data to " + DATA_FILE + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            subjects = (List<Subject>) ois.readObject();
            topics = (List<Topic>) ois.readObject();
            studySessions = (List<StudySession>) ois.readObject();
            initMaps();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to parse local dat file: " + e.getMessage());
            subjects = new ArrayList<>();
            topics = new ArrayList<>();
            studySessions = new ArrayList<>();
            initMaps();
        }
    }
}
