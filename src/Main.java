import logic.AISuggestionEngine;
import models.Subject;
import models.Topic;
import storage.DataStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static DataStorage dataStorage = new DataStorage();
    private static AISuggestionEngine aiEngine = new AISuggestionEngine(dataStorage);
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exit = false;

        seedDummyData(); // Add some initial data so it's not empty right away

        System.out.println("=========================================");
        System.out.println("  Smart Study Planner with AI Suggestion ");
        System.out.println("=========================================");

        while (!exit) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Add Subject");
            System.out.println("2. Add Topic");
            System.out.println("3. Generate Study Plan");
            System.out.println("4. View Progress");
            System.out.println("5. Get AI Suggestions");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    addSubject();
                    break;
                case "2":
                    addTopic();
                    break;
                case "3":
                    generateStudyPlan();
                    break;
                case "4":
                    viewProgress();
                    break;
                case "5":
                    getAiSuggestions();
                    break;
                case "6":
                    exit = true;
                    System.out.println("Exiting Smart Study Planner. Good luck with your studies!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option (1-6).");
            }
        }
        
        scanner.close();
    }

    private static void addSubject() {
        System.out.println("\n--- Add New Subject ---");
        System.out.print("Enter Subject Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Difficulty Level (Low, Medium, High): ");
        String difficulty = scanner.nextLine();

        Subject subject = new Subject(name, difficulty);
        dataStorage.addSubject(subject);
        System.out.println("Subject '" + name + "' added successfully!");
    }

    private static void addTopic() {
        System.out.println("\n--- Add New Topic ---");
        if (dataStorage.getSubjects().isEmpty()) {
            System.out.println("Error: No subjects exist yet. Please add a subject first.");
            return;
        }

        System.out.println("Select a Subject:");
        List<Subject> subjects = dataStorage.getSubjects();
        for (int i = 0; i < subjects.size(); i++) {
            System.out.println((i + 1) + ". " + subjects.get(i).getName());
        }
        
        System.out.print("Enter Subject Number: ");
        int subjectIndex;
        try {
            subjectIndex = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }

        if (subjectIndex < 0 || subjectIndex >= subjects.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Subject selectedSubject = subjects.get(subjectIndex);
        
        System.out.print("Enter Topic Name: ");
        String topicName = scanner.nextLine();
        
        System.out.print("Enter Priority (Low, Medium, High): ");
        String priority = scanner.nextLine();
        
        System.out.print("Enter Status (Not Started, In Progress, Completed): ");
        String status = scanner.nextLine();

        Topic newTopic = new Topic(topicName, selectedSubject, status, priority);
        dataStorage.addTopic(newTopic);
        System.out.println("Topic '" + topicName + "' added successfully to " + selectedSubject.getName() + "!");
    }

    private static void generateStudyPlan() {
        System.out.println("\n--- Daily Study Plan ---");
        List<Topic> topics = dataStorage.getTopics();
        
        if (topics.isEmpty()) {
            System.out.println("No topics available to create a study plan.");
            return;
        }

        System.out.println("Based on your priority and difficulty, focus on these topics today (Simulated):");
        
        int count = 0;
        // Basic simulation: Pick 'High' priority or topics in 'High'/'Hard' subjects
        for (Topic topic : topics) {
            if ("Completed".equalsIgnoreCase(topic.getStatus())) continue;
            
            String subDiff = topic.getSubject().getDifficultyLevel();
            boolean isHardSubject = "High".equalsIgnoreCase(subDiff) || "Hard".equalsIgnoreCase(subDiff);
            boolean isHighPriority = "High".equalsIgnoreCase(topic.getPriority());

            if (isHardSubject || isHighPriority) {
                System.out.println(" - [Urgent] " + topic.getName() + " (Subject: " + topic.getSubject().getName() + ")");
                count++;
            }
        }
        
        if (count == 0) {
            System.out.println("No urgent topics today. Excellent!");
            // Print remaining topics
            for (Topic topic : topics) {
                if (!"Completed".equalsIgnoreCase(topic.getStatus())) {
                    System.out.println(" - " + topic.getName() + " (Subject: " + topic.getSubject().getName() + ")");
                }
            }
        }
    }

    private static void viewProgress() {
        System.out.println("\n--- Subject Progress ---");
        List<Subject> subjects = dataStorage.getSubjects();
        
        if (subjects.isEmpty()) {
            System.out.println("No subjects to track yet.");
            return;
        }

        for (Subject subject : subjects) {
            double percentage = dataStorage.getCompletionPercentage(subject);
            System.out.printf("%s: %.1f%% Complete\n", subject.getName(), percentage);
        }
    }

    private static void getAiSuggestions() {
        System.out.println("\n--- AI Engine Suggestions ---");
        List<String> suggestions = aiEngine.generateSuggestions();
        for (int i = 0; i < suggestions.size(); i++) {
            System.out.println("* " + suggestions.get(i));
        }
    }

    // A helper method to pre-load some dummy data to make testing easier
    private static void seedDummyData() {
        Subject math = new Subject("Mathematics", "Hard");
        Subject history = new Subject("History", "Medium");
        
        dataStorage.addSubject(math);
        dataStorage.addSubject(history);

        dataStorage.addTopic(new Topic("Calculus II", math, "Not Started", "High"));
        dataStorage.addTopic(new Topic("Linear Algebra", math, "Completed", "High"));
        dataStorage.addTopic(new Topic("World War II", history, "In Progress", "Medium"));
    }
}
