package game;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * 啟動遊戲
 */
public class RhythmGame extends Application {
    public static final int defaultFlowTime = 3;
    public static final int acceptableRange = 124;
    private Settings settings;

    ScreenManager screenManager;

    public void start(Stage primaryStage) {
        screenManager = new ScreenManager(primaryStage, settings); // 建立畫面管理器
        settings = new Settings(screenManager, "MainMenu"); // 建立設定畫面
        screenManager.setSettings(settings);
        screenManager.switchToMainMenu(); // 進入主畫面
        primaryStage.setTitle("Rhythm Game");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
