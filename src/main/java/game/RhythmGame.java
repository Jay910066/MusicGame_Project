package game;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.util.Random;

/**
 * 啟動遊戲
 */
public class RhythmGame extends Application {
    public static final int defaultFlowTime = 3; //預設音符抵達判定點的時間(秒)
    public static final int acceptableRange = 124; //判定時間的誤差範圍(毫秒)
    public static MediaPlayer BGMPlayer; //背景音樂

    private Settings settings; //設定畫面

    public void start(Stage primaryStage) {
        //畫面管理器
        ScreenManager screenManager = new ScreenManager(primaryStage, settings); // 建立畫面管理器
        settings = new Settings(screenManager, "MainMenu"); // 建立設定畫面
        setBGMPlayer(); // 設定背景音樂
        screenManager.setSettings(settings);
        screenManager.switchToMainMenu(); // 進入主畫面
        primaryStage.setTitle("Rhythm Game");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * 設定背景音樂
     */
    public static void setBGMPlayer() {
        File songFolder = new File("Resources/Songs");
        File[] songList = songFolder.listFiles();
        Random random = new Random();

        if(songList != null) {
            File song = new File(songList[random.nextInt(songList.length)], "song.mp3");
            BGMPlayer = new MediaPlayer(new Media(song.toURI().toString()));
            BGMPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            BGMPlayer.setVolume(Settings.volume);
            BGMPlayer.volumeProperty().bind(Settings.volumeSlider.valueProperty().divide(100));
            BGMPlayer.play();
        }
    }
}
