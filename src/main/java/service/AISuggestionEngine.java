package service;

import model.StudySession;
import model.Subject;
import storage.DataStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AISuggestionEngine {

    private DataStorage dataStorage;

    public AISuggestionEngine(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    public List<String> generateSuggestions() {
        List<String> suggestions = new ArrayList<>();

        analyzeMissedSessions(suggestions);
        analyzeWeakSubjects(suggestions);
        analyzeLowCompletionRates(suggestions);

        if (suggestions.isEmpty()) {
            suggestions.add("Keep up the great work! You're on track with your study plan.");
        }

        return suggestions;
    }

    private void analyzeMissedSessions(List<String> suggestions) {
        LocalDate today = LocalDate.now();
        int missedCount = 0;
        List<String> missedTopics = new ArrayList<>();

        for (StudySession session : dataStorage.getStudySessions()) {
            if (session.getDate().isBefore(today) && !session.isCompleted()) {
                missedCount++;
                if (!missedTopics.contains(session.getTopic().getName())) {
                    missedTopics.add(session.getTopic().getName());
                }
            }
        }

        if (missedCount > 0) {
            String topicList = String.join(", ", missedTopics);
            suggestions.add("Warning: You have " + missedCount + " missed study sessions. " +
                    "Try to catch up on these topics: " + topicList + ".");
        }
    }

    private void analyzeWeakSubjects(List<String> suggestions) {
        for (Subject subject : dataStorage.getSubjects()) {
            String difficulty = subject.getDifficultyLevel();
            if (difficulty != null) {
                if (difficulty.equalsIgnoreCase("Hard") || difficulty.equalsIgnoreCase("High")) {
                    suggestions.add("Focus more on '" + subject.getName() + "' as it's marked as a difficult subject. " +
                            "Consider revising difficult topics first.");
                }
            }
        }
    }

    private void analyzeLowCompletionRates(List<String> suggestions) {
        for (Subject subject : dataStorage.getSubjects()) {
            double completionRate = dataStorage.getCompletionPercentage(subject);
            
            // Only trigger alert if completion is poor, but do it safely logic-wise
            if (completionRate < 40.0 && dataStorage.getTopics().stream().anyMatch(t -> t.getSubject().equals(subject))) {
                String formattedRate = String.format("%.1f", completionRate);
                suggestions.add("Low completion alert! You have only completed " + formattedRate + 
                        "% of the topics in '" + subject.getName() + "'. Consider dedicating more daily hours to it.");
            }
        }
    }
}
