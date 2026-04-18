import ui.PlannerGUI;

/**
 * Main application launcher.
 * By placing the main class separately from the Application subclass, 
 * we bypass the strict module path requirements of newer Java versions.
 */
public class Main {
    public static void main(String[] args) {
        javafx.application.Application.launch(PlannerGUI.class, args);
    }
}
