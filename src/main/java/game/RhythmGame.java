package game;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * 啟動遊戲
 */
public class RhythmGame extends Application {
    public static final int defaultFlowTime = 3; //預設音符抵達判定點的時間(秒)
    public static final int acceptableRange = 124; //判定時間的誤差範圍(毫秒)
    private Settings settings; //設定畫面

    public void start(Stage primaryStage) {
        //畫面管理器
        ScreenManager screenManager = new ScreenManager(primaryStage, settings); // 建立畫面管理器
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
