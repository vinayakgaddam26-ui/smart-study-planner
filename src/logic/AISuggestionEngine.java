package logic;

import models.StudySession;
import models.Subject;
import storage.DataStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AISuggestionEngine {

    private DataStorage dataStorage;

    public AISuggestionEngine(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Recommends actions based on hard-coded heuristics (rules).
     * Simulates an AI system before integrating a real API.
     */
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

    /**
     * Rule 1: Checks for Missed Study Sessions.
     * Logic: Any session scheduled before today that is NOT marked as completed is a "missed" session.
     */
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

    /**
     * Rule 2: Checks for Weak / Difficult Subjects.
     * Logic: Suggests spending more time on subjects with 'Hard' or 'High' difficulty.
     */
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

    /**
     * Rule 3: Checks for Low Completion Rates.
     * Logic: Calculates the completion rate for each subject. Suggests urgent attention if it falls below 40%.
     */
    private void analyzeLowCompletionRates(List<String> suggestions) {
        for (Subject subject : dataStorage.getSubjects()) {
            double completionRate = dataStorage.getCompletionPercentage(subject);
            
            // Only warn if they haven't completed at least 40%
            if (completionRate < 40.0) {
                // Formatting to 1 decimal place
                String formattedRate = String.format("%.1f", completionRate);
                suggestions.add("Low completion alert! You have only completed " + formattedRate + 
                        "% of the topics in '" + subject.getName() + "'. Consider dedicating more daily hours to it.");
            }
        }
    }
}
