package model;

import java.io.Serializable;
import java.time.LocalDate;

public class StudySession implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate date;
    private Topic topic;
    private int durationMinutes;
    private boolean isCompleted;

    public StudySession(LocalDate date, Topic topic, int durationMinutes, boolean isCompleted) {
        this.date = date;
        this.topic = topic;
        this.durationMinutes = durationMinutes;
        this.isCompleted = isCompleted;
    }

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
