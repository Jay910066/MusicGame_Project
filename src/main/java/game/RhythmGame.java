package game;

import javafx.application.Application;
import javafx.stage.Stage;

public class RhythmGame extends Application {
    public void start(Stage primaryStage) {
        ScreenManager screenManager = new ScreenManager(primaryStage);
        screenManager.switchToMainMenu(0);
        primaryStage.setTitle("Rhythm Game");
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
