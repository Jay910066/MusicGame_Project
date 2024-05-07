package game;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

public class Settings extends VBox {
    public Settings(ScreenManager screenManager, String previousScreen) {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            goBack(screenManager, previousScreen);
        });

        this.setOnKeyPressed(e ->{
            if(e.getCode() == KeyCode.ESCAPE)
                goBack(screenManager, previousScreen);
        });

        getChildren().add(backButton);
    }

    private void goBack(ScreenManager screenManager, String previousScreen) {
        if(previousScreen.equals("MainMenu")) {
            screenManager.switchToMainMenu();
        } else if(previousScreen.equals("SongListMenu")) {
            screenManager.switchToSongListMenu();
        }
    }
}