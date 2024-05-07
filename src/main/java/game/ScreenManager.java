package game;

import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class ScreenManager {
    private Stage stage;

    public ScreenManager(Stage stage) {
        this.stage = stage;
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    }

    public void switchToMainMenu() {
        MainMenu mainMenu = new MainMenu(this);
        Scene scene = new Scene(mainMenu);
        stage.setScene(scene);
        stage.setFullScreen(true);
    }

    public void switchToSettings(String previousScreen) {
        Settings settings = new Settings(this, previousScreen);
        Scene scene = new Scene(settings);
        stage.setScene(scene);
        stage.setFullScreen(true);
    }

    public void switchToSongListMenu() {
        SongListMenu songListMenu = new SongListMenu(this);
        Scene scene = new Scene(songListMenu);
        stage.setScene(scene);
        stage.setFullScreen(true);
    }
}
