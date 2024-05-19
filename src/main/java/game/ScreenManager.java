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
     * @param previousIndex 歌曲索引，紀錄之前停留在哪首歌曲
     */
    public void switchToMainMenu(int previousIndex) {
        MainMenu mainMenu = new MainMenu(this, previousIndex);
        root.getChildren().clear();
        root.getChildren().add(mainMenu);
    }

    /**
     * 切換到設定畫面
     * @param previousScreen 前一畫面的名稱，確認要回到哪個畫面
     * @param previousIndex 歌曲索引，紀錄之前停留在哪首歌曲
     */
    public void switchToSettings(String previousScreen, int previousIndex) {
        settings.setPreviousScreen(previousScreen);
        settings.setPreviousIndex(previousIndex);
        root.getChildren().clear();
        root.getChildren().add(settings);
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    /**
     * 切換到歌曲列表畫面
     * @param previousIndex 歌曲索引，紀錄之前停留在哪首歌曲
     */
    public void switchToSongListMenu(int previousIndex) {
        SongListMenu songListMenu = new SongListMenu(this, previousIndex);
        root.getChildren().clear();
        root.getChildren().add(songListMenu);
    }

    /**
     * 切換到遊戲畫面
     * @param selectedSong 選擇的歌曲
     * @param previousIndex 歌曲索引，紀錄之前停留在哪首歌曲
     */
    public void switchToGamePlay(File selectedSong, int previousIndex) {
        GamePlay gamePlay = new GamePlay(this, previousIndex, selectedSong);
        root.getChildren().clear();
        root.getChildren().add(gamePlay);
    }
}
