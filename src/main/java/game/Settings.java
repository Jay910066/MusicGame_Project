package game;

import com.almasb.fxgl.core.collection.grid.Grid;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

/**
 * 設定畫面
 */
public class Settings extends VBox {
    private ScreenManager screenManager;
    public static double flowSpeed = 1.0;
    public static double volume = 50.0;
    public static int offset = 0;
    private String previousScreen;
    private int previousIndex;
    private File[] songList;
    /**
     * 設定畫面
     * @param screenManager 畫面管理器
     * @param previousScreen 前一畫面的名稱，確認要回到哪個畫面
     * @param previousIndex 歌曲索引，紀錄之前停留在哪首歌曲
     */
    public Settings(ScreenManager screenManager, String previousScreen, int previousIndex) {
        this.screenManager = screenManager;
        this.previousScreen = previousScreen;
        this.previousIndex = previousIndex;

        GridPane settings = new GridPane();
        getChildren().add(settings);
        settings.setHgap(10);
        settings.setVgap(10);
        Label flowSpeedLabel = new Label("Flow Speed:");
        TextField flowSpeedField = new TextField();
        Text flowSpeedText = new Text("1.0");
        settings.add(flowSpeedLabel, 0, 0);
        settings.add(flowSpeedField, 1, 0);
        settings.add(flowSpeedText, 2, 0);

        flowSpeedField.setOnAction(e -> {
            try {
                flowSpeed = Double.parseDouble(flowSpeedField.getText());
                flowSpeedText.setText(flowSpeedField.getText());
            } catch (NumberFormatException ex) {
                flowSpeedField.setText(flowSpeedText.getText());
            }
        });

        
        Label offsetLabel = new Label("Offset:");
        TextField offsetField = new TextField();
        Text offsetText = new Text("0");
        settings.add(offsetLabel, 0, 1);
        settings.add(offsetField, 1, 1);
        settings.add(offsetText, 2, 1);
        

        offsetField.setOnAction(e -> {
            try {
                offset = Integer.parseInt(offsetField.getText());
                offsetText.setText(offsetField.getText());
            } catch (NumberFormatException ex) {
                offsetField.setText(offsetText.getText());
            }
        });
        
        File songFolder = new File("Resources/Songs");
        songList = songFolder.listFiles();
        Media backgroundSong = new Media(new File(songList[0], "song.mp3").toURI().toString());
        MediaPlayer backgroundSongPlayer = new MediaPlayer(backgroundSong);
        Label volumeLabel = new Label("Volume:");
        Slider volumeSlider = new Slider();
        volumeSlider.setPrefWidth(200);
        volumeSlider.setValue(50);
        backgroundSongPlayer.volumeProperty().bind(volumeSlider.valueProperty().devide(100));
        Text volumeText = new Text(volumeSlider.valueProperty().toString());
        settings.add(volumeLabel, 0, 2);
        settings.add(volumeSlider, 1, 2);
        settings.add(volumeText, 2, 2);
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            goBack();
        });

        this.setOnKeyPressed(e ->{
            if(e.getCode() == KeyCode.ESCAPE)
                goBack();
        });

        getChildren().add(backButton);
    }
    /**
     * 回到前一畫面
     */
    private void goBack() {
        if(previousScreen.equals("MainMenu")) {
            screenManager.switchToMainMenu();
            backgroundSongPlayer.stop();
        } else if(previousScreen.equals("SongListMenu")) {
            screenManager.switchToSongListMenu();
            backgroundSongPlayer.stop();
        }
    }

    public void setPreviousScreen(String previousScreen) {
        this.previousScreen = previousScreen;
    }

    public void setPreviousIndex(int previousIndex) {
        this.previousIndex = previousIndex;
    }
}
