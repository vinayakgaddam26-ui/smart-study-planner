package ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import service.AISuggestionEngine;
import model.Subject;
import model.Topic;
import storage.DataStorage;

import java.util.List;

public class PlannerGUI extends Application {

    private DataStorage dataStorage;
    private AISuggestionEngine aiEngine;

    private BorderPane root;
    private StackPane contentArea;

    private VBox dashboardView;
    private VBox addFormView;
    private VBox plannerView;

    @Override
    public void start(Stage primaryStage) {
        dataStorage = new DataStorage();
        aiEngine = new AISuggestionEngine(dataStorage);

        root = new BorderPane();
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));

        createAddFormView();
        createDashboardView();
        
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2c3e50;");
        sidebar.setPrefWidth(200);

        Label title = new Label("Smart\nPlanner");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        Button btnDashboard = createSidebarButton("Dashboard");
        Button btnAdd = createSidebarButton("Add Subject / Topic");
        Button btnPlan = createSidebarButton("Study Plan");

        btnDashboard.setOnAction(e -> switchView(dashboardView));
        btnAdd.setOnAction(e -> switchView(addFormView));
        btnPlan.setOnAction(e -> switchView(plannerView));

        sidebar.getChildren().addAll(title, new Separator(), btnDashboard, btnAdd, btnPlan);
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        switchView(dashboardView);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Smart Study Planner - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #3b5998; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;"));
        return btn;
    }

    private void switchView(VBox view) {
        contentArea.getChildren().clear();
        if (view == dashboardView) {
            createDashboardView();
            contentArea.getChildren().add(dashboardView);
        } else if (view == plannerView) {
            createPlannerView();
            contentArea.getChildren().add(plannerView);
        } else if (view == addFormView) {
            contentArea.getChildren().add(addFormView);
            // Quick force refresh of dropdown subjects
            createAddFormView(); 
            contentArea.getChildren().add(addFormView);
        }
    }

    private void createDashboardView() {
        dashboardView = new VBox(20);
        
        Label lblHeader = new Label("Dashboard & AI Suggestions");
        lblHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));

        VBox progressBox = new VBox(15);
        progressBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 20; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        Label lblProgress = new Label("Subject Progress Details");
        lblProgress.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        progressBox.getChildren().add(lblProgress);

        if (dataStorage.getSubjects().isEmpty()) {
            progressBox.getChildren().add(new Label("No subjects tracked yet. Add one in the sidebar!"));
        }

        for (Subject subject : dataStorage.getSubjects()) {
            HBox subjectRow = new HBox(15);
            subjectRow.setAlignment(Pos.CENTER_LEFT);
            
            Label subName = new Label(subject.getName());
            subName.setPrefWidth(120);
            subName.setFont(Font.font("Segoe UI", 14));
            
            ProgressBar pBar = new ProgressBar();
            double pct = dataStorage.getCompletionPercentage(subject) / 100.0;
            pBar.setProgress(Double.isNaN(pct) ? 0 : pct);
            pBar.setPrefWidth(350);
            pBar.setPrefHeight(18);

            Label pctLbl = new Label(String.format("%.0f%%", pct * 100));
            pctLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            
            subjectRow.getChildren().addAll(subName, pBar, pctLbl);
            progressBox.getChildren().add(subjectRow);
        }

        VBox suggestionsBox = new VBox(10);
        suggestionsBox.setStyle("-fx-background-color: #f4f6f6; -fx-padding: 20; -fx-border-color: #d1d5db; -fx-border-radius: 8; -fx-background-radius: 8;");
        Label lblSuggest = new Label("AI Priority Insights");
        lblSuggest.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        suggestionsBox.getChildren().add(lblSuggest);

        List<String> suggestions = aiEngine.generateSuggestions();
        ListView<String> suggestList = new ListView<>();
        suggestList.getItems().addAll(suggestions);
        suggestList.setPrefHeight(150);
        suggestList.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        suggestionsBox.getChildren().add(suggestList);

        dashboardView.getChildren().addAll(lblHeader, progressBox, suggestionsBox);
    }

    private void createAddFormView() {
        addFormView = new VBox(30);
        Label lblHeader = new Label("Add Subject & Topic Data");
        lblHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));

        GridPane subjectForm = new GridPane();
        subjectForm.setHgap(15); subjectForm.setVgap(15);
        subjectForm.setStyle("-fx-background-color: #ffffff; -fx-padding: 20; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        
        Label lblSubjTitle = new Label("New Subject");
        lblSubjTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        TextField txtSubjName = new TextField();
        txtSubjName.setPromptText("Enter Subject Name");
        ComboBox<String> cmbDifficulty = new ComboBox<>(FXCollections.observableArrayList("Low", "Medium", "High", "Hard"));
        cmbDifficulty.setPromptText("Difficulty");
        
        Button btnAddSubject = new Button("Add Subject");
        btnAddSubject.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        subjectForm.add(lblSubjTitle, 0, 0, 2, 1);
        subjectForm.add(new Label("Name:"), 0, 1);
        subjectForm.add(txtSubjName, 1, 1);
        subjectForm.add(new Label("Difficulty:"), 0, 2);
        subjectForm.add(cmbDifficulty, 1, 2);
        subjectForm.add(btnAddSubject, 1, 3);

        GridPane topicForm = new GridPane();
        topicForm.setHgap(15); topicForm.setVgap(15);
        topicForm.setStyle("-fx-background-color: #ffffff; -fx-padding: 20; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label lblTopicTitle = new Label("New Topic");
        lblTopicTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        ComboBox<String> cmbSubject = new ComboBox<>();
        cmbSubject.setPromptText("Select Subject");
        for (Subject s : dataStorage.getSubjects()) {
            cmbSubject.getItems().add(s.getName());
        }

        TextField txtTopicName = new TextField();
        txtTopicName.setPromptText("Enter Topic Name");
        ComboBox<String> cmbPriority = new ComboBox<>(FXCollections.observableArrayList("Low", "Medium", "High"));
        cmbPriority.setPromptText("Priority");
        ComboBox<String> cmbStatus = new ComboBox<>(FXCollections.observableArrayList("Not Started", "In Progress", "Completed"));
        cmbStatus.setPromptText("Status");
        
        Button btnAddTopic = new Button("Add Topic");
        btnAddTopic.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        topicForm.add(lblTopicTitle, 0, 0, 2, 1);
        topicForm.add(new Label("Subject:"), 0, 1);
        topicForm.add(cmbSubject, 1, 1);
        topicForm.add(new Label("Name:"), 0, 2);
        topicForm.add(txtTopicName, 1, 2);
        topicForm.add(new Label("Priority:"), 0, 3);
        topicForm.add(cmbPriority, 1, 3);
        topicForm.add(new Label("Status:"), 0, 4);
        topicForm.add(cmbStatus, 1, 4);
        topicForm.add(btnAddTopic, 1, 5);

        Label lblMessage = new Label();
        lblMessage.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        btnAddSubject.setOnAction(e -> {
            if (txtSubjName.getText().isEmpty() || cmbDifficulty.getValue() == null) {
                lblMessage.setTextFill(Color.web("#e74c3c"));
                lblMessage.setText("Error: Fill all fields for Subject.");
                return;
            }
            dataStorage.addSubject(new Subject(txtSubjName.getText(), cmbDifficulty.getValue()));
            lblMessage.setTextFill(Color.web("#2ecc71"));
            lblMessage.setText("Subject saved. Check the Dashboard!");
            txtSubjName.clear();
            cmbDifficulty.getSelectionModel().clearSelection();
            createAddFormView(); // Refresh the subject dropdown dynamically
            contentArea.getChildren().setAll(addFormView);
        });

        btnAddTopic.setOnAction(e -> {
            if (cmbSubject.getValue() == null || txtTopicName.getText().isEmpty() || 
                cmbPriority.getValue() == null || cmbStatus.getValue() == null) {
                lblMessage.setTextFill(Color.web("#e74c3c"));
                lblMessage.setText("Error: Fill all fields for Topic.");
                return;
            }
            Subject selectedSubj = null;
            for (Subject s : dataStorage.getSubjects()) {
                if (s.getName().equals(cmbSubject.getValue())) {
                    selectedSubj = s;
                    break;
                }
            }
            if (selectedSubj != null) {
                dataStorage.addTopic(new Topic(txtTopicName.getText(), selectedSubj, cmbStatus.getValue(), cmbPriority.getValue()));
                lblMessage.setTextFill(Color.web("#2ecc71"));
                lblMessage.setText("Topic mapped to " + selectedSubj.getName() + ".");
                txtTopicName.clear();
                cmbPriority.getSelectionModel().clearSelection();
                cmbStatus.getSelectionModel().clearSelection();
            }
        });

        addFormView.getChildren().addAll(lblHeader, subjectForm, topicForm, lblMessage);
    }

    private void createPlannerView() {
        plannerView = new VBox(20);
        Label lblHeader = new Label("Daily Study Plan Engine");
        lblHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));

        ListView<Topic> planList = new ListView<>();
        planList.setPrefHeight(450);
        
        ObservableList<Topic> urgentTopics = FXCollections.observableArrayList();
        ObservableList<Topic> normalTopics = FXCollections.observableArrayList();

        for (Topic t : dataStorage.getTopics()) {
            if (!"Completed".equalsIgnoreCase(t.getStatus())) {
                String subDiff = t.getSubject().getDifficultyLevel();
                boolean isUrgent = "High".equalsIgnoreCase(subDiff) || "Hard".equalsIgnoreCase(subDiff) || "High".equalsIgnoreCase(t.getPriority());
                if (isUrgent) {
                    urgentTopics.add(t);
                } else {
                    normalTopics.add(t);
                }
            }
        }
        
        urgentTopics.addAll(normalTopics);
        planList.setItems(urgentTopics);
        
        planList.setCellFactory(lv -> new ListCell<Topic>() {
            @Override
            protected void updateItem(Topic item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    String subDiff = item.getSubject().getDifficultyLevel();
                    boolean isUrgent = "High".equalsIgnoreCase(subDiff) || "Hard".equalsIgnoreCase(subDiff) || "High".equalsIgnoreCase(item.getPriority());
                    
                    setText(String.format("▸ %s (Subject: %s)  [Priority: %s] | Status: %s",
                            item.getName(), item.getSubject().getName(), item.getPriority(), item.getStatus()));
                    
                    setFont(Font.font("Segoe UI", 15));
                    if (isUrgent) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #2c3e50;");
                    }
                }
            }
        });

        plannerView.getChildren().addAll(lblHeader, planList);
    }
}
