package game;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

/**
 * 切換不同的畫面
 */
public class ScreenManager {
    private VBox root = new VBox();
    private Scene scene = new Scene(root);
    private Settings settings;

    public ScreenManager(Stage stage, Settings settings) {
        this.settings = settings;
        root.setAlignment(Pos.CENTER);
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    }

    /**
     * 切換到主畫面
     */
    public void switchToMainMenu() {
        MainMenu mainMenu = new MainMenu(this);
        root.getChildren().clear();
        root.getChildren().add(mainMenu);
    }

    /**
     * 切換到設定畫面
     *
     * @param previousScreen 前一畫面的名稱，確認要回到哪個畫面
     */
    public void switchToSettings(String previousScreen) {
        settings.setPreviousScreen(previousScreen);
        root.getChildren().clear();
        root.getChildren().add(settings);
        settings.backgroundSongPlayer.play();
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    /**
     * 切換到歌曲列表畫面
     */
    public void switchToSongListMenu() {
        SongListMenu songListMenu = new SongListMenu(this);
        root.getChildren().clear();
        root.getChildren().add(songListMenu);
    }

    /**
     * 切換到遊戲畫面
     *
     * @param selectedSong 選擇的歌曲
     */
    public void switchToGamePlay(File selectedSong) {
        GamePlay gamePlay = new GamePlay(this, selectedSong);
        root.getChildren().clear();
        root.getChildren().add(gamePlay);
    }
}
