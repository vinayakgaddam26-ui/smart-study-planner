package models;

import java.time.LocalDate;

public class StudySession {
    // Encapsulation: fields are private
    private LocalDate date;
    private Topic topic;
    private int durationMinutes;
    private boolean isCompleted;

    // Constructor to initialize a StudySession object
    public StudySession(LocalDate date, Topic topic, int durationMinutes, boolean isCompleted) {
        this.date = date;
        this.topic = topic;
        this.durationMinutes = durationMinutes;
        this.isCompleted = isCompleted;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
