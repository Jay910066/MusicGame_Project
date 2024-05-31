package game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 設定畫面
 */
public class Settings extends VBox {
    private ScreenManager screenManager;
    public static double flowSpeed;//音符下落速度
    public static double volume;//歌曲音效
    public static double effectVolume;//音效音量
    public static int offset;
    private String previousScreen;//前一個螢幕
    private File[] songList;//讀取歌用
    private Media backgroundSong;//播放背景歌曲
    public MediaPlayer backgroundSongPlayer;
    private MediaPlayer SoundEffectPlayer;

    /**
     * 設定畫面
     *
     * @param screenManager 畫面管理器
     * @param previousScreen 前一畫面的名稱，確認要回到哪個畫面
     */
    public Settings(ScreenManager screenManager, String previousScreen) {
        this.screenManager = screenManager;
        this.previousScreen = previousScreen;

        try {
            flowSpeed = getConfig("FlowSpeed", Double.class);
            volume = getConfig("Volume", Double.class);
            offset = getConfig("Offset", Integer.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GridPane settings = new GridPane();
        getChildren().add(settings);
        settings.setHgap(10);
        settings.setVgap(10);
        Label flowSpeedLabel = new Label("Flow Speed:");
        TextField flowSpeedField = new TextField();
        Text flowSpeedText = new Text(String.valueOf(flowSpeed));
        settings.add(flowSpeedLabel, 0, 0);
        settings.add(flowSpeedField, 1, 0);
        settings.add(flowSpeedText, 2, 0);

        flowSpeedField.setOnAction(e -> {
            try {
                flowSpeed = Double.parseDouble(flowSpeedField.getText());
                flowSpeedText.setText(flowSpeedField.getText());
                setConfig("FlowSpeed",flowSpeed);
            }catch(NumberFormatException ex) {
                flowSpeedField.setText(flowSpeedText.getText());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });


        Label offsetLabel = new Label("Offset:");
        TextField offsetField = new TextField();
        Text offsetText = new Text(String.valueOf(offset));
        settings.add(offsetLabel, 0, 1);
        settings.add(offsetField, 1, 1);
        settings.add(offsetText, 2, 1);


        offsetField.setOnAction(e -> {
            try {
                offset = Integer.parseInt(offsetField.getText());
                offsetText.setText(offsetField.getText());
                setConfig("Offset", offset);
            }catch(NumberFormatException ex) {
                offsetField.setText(offsetText.getText());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        File songFolder = new File("Resources/Songs");
        songList = songFolder.listFiles();
        backgroundSong = new Media(new File(songList[0], "song.mp3").toURI().toString());
        backgroundSongPlayer = new MediaPlayer(backgroundSong);
        Label volumeLabel = new Label("Volume:");
        Slider volumeSlider = new Slider();
        volumeSlider.setPrefWidth(200);
        volumeSlider.setValue(50);
        Text volumeText = new Text(String.valueOf(volume));
        backgroundSongPlayer.volumeProperty().bind(volumeSlider.valueProperty().divide(100));
        volumeSlider.valueProperty().addListener(ov -> {
            volume = volumeSlider.getValue();
            volumeText.setText(String.valueOf(Math.round(volume)));
            try {
                setConfig("Volume",volume);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        settings.add(volumeLabel, 0, 2);
        settings.add(volumeSlider, 1, 2);
        settings.add(volumeText, 2, 2);

        Media hitSound = new Media(new File("Resources/Audio/Hit.wav").toURI().toString());
        SoundEffectPlayer = new MediaPlayer(hitSound);
        Label effectVolumeLabel = new Label("EffectVolume:");
        Slider effectVolumeSlider = new Slider();
        effectVolumeSlider.setPrefWidth(200);
        effectVolumeSlider.setValue(50);
        Text effectVolumeText = new Text(String.valueOf(effectVolume));
        Button effectVolumeButton = new Button("Play");
        SoundEffectPlayer.volumeProperty().bind(effectVolumeSlider.valueProperty().divide(100));
        effectVolumeSlider.valueProperty().addListener(ov -> {
            effectVolume = effectVolumeSlider.getValue();
            effectVolumeText.setText(String.valueOf(Math.round(effectVolume)));
            try {
                setConfig("EffectVolume",effectVolume);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        effectVolumeButton.setOnAction(new EventHandler<ActionEvent>() {//播音效
            @Override
            public void handle(ActionEvent actionEvent) {
                SoundEffectPlayer.play();
                SoundEffectPlayer.stop();
            }
        });

        settings.add(effectVolumeLabel, 0, 3);
        settings.add(effectVolumeSlider, 1, 3);
        settings.add(effectVolumeText, 2, 3);
        settings.add(effectVolumeButton, 3, 3);


        ImageView quitButton = new ImageView("file:Resources/Images/QuitButton.png");
        quitButton.setOnMouseEntered(e -> quitButton.setImage(new ImageView("file:Resources/Images/QuitButton_Selected.png").getImage()));
        quitButton.setOnMouseExited(e -> quitButton.setImage(new ImageView("file:Resources/Images/QuitButton.png").getImage()));
        quitButton.setOnMouseClicked(e -> goBack());

        this.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE)
                goBack();
        });

        getChildren().add(quitButton);
    }

    /**
     * 回到前一畫面
     */
    private void goBack() {
        if(previousScreen.equals("MainMenu")) {
            screenManager.switchToMainMenu();
            backgroundSongPlayer.stop();
        }else if(previousScreen.equals("SongListMenu")) {
            screenManager.switchToSongListMenu();
            backgroundSongPlayer.stop();
        }
    }

    public void setPreviousScreen(String previousScreen) {
        this.previousScreen = previousScreen;
    }

    private void setConfig(String Function, double value) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.readValue(new File("./Resources/config.json"), ObjectNode.class);
        if(Function.equals("Offset")) {
            jsonNode.put("Offset", (int)value);
        }
        else {
            jsonNode.put(Function, value);
        }
        try (FileWriter file = new FileWriter("./Resources/config.json")) {
            file.write(jsonNode.toString());
        }
    }

    private <T> T getConfig(String Function, Class<T> valueType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File("./Resources/config.json"));
        return objectMapper.convertValue(jsonNode.get(Function), valueType);
    }
}
