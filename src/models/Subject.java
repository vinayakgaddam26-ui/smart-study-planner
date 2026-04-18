package models;

public class Subject {
    // Encapsulation: fields are private
    private String name;
    private String difficultyLevel;

    // Constructor to initialize a Subject object
    public Subject(String name, String difficultyLevel) {
        this.name = name;
        this.difficultyLevel = difficultyLevel;
    }

    // Getters and Setters for accessing and modifying private variables safely
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
}
