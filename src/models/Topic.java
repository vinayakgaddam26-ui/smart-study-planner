package models;

public class Topic {
    // Encapsulation: fields are private
    private String name;
    private Subject subject;
    private String status;
    private String priority;

    // Constructor to initialize a Topic object
    public Topic(String name, Subject subject, String status, String priority) {
        this.name = name;
        this.subject = subject;
        this.status = status;
        this.priority = priority;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
