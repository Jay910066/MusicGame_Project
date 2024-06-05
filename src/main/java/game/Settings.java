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
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
public class Settings extends Pane {
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
            effectVolume = getConfig("EffectVolume", Double.class);
        }catch(IOException e) {
            throw new RuntimeException(e);
        }


        ImageView settingsPanel = new ImageView("file:Resources/Images/setting_panel.png");
        settingsPanel.setEffect(new Bloom(0.5));
        getChildren().add(settingsPanel);

        setFlowSpeedInput();
        setOffsetInput();
        setVolumeInput();
        setEffectVolumeInput();


        SoundEffect selectsoundEffect = new SoundEffect();
        ImageView quitButton = new ImageView("file:Resources/Images/QuitButton.png");
        quitButton.setLayoutY(980);

        quitButton.setOnMouseEntered(e -> {
            quitButton.setImage(new ImageView("file:Resources/Images/QuitButton_Selected.png").getImage());
            selectsoundEffect.playSelectSound();
        });
        quitButton.setOnMouseExited(e -> quitButton.setImage(new ImageView("file:Resources/Images/QuitButton.png").getImage()));
        quitButton.setOnMouseClicked(e -> goBack());

        this.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE)
                goBack();
        });

        getChildren().add(quitButton);
    }

    private void setFlowSpeedInput() {
        Text flowSpeedText = new Text(String.valueOf(flowSpeed));
        flowSpeedText.setLayoutX(980);
        flowSpeedText.setLayoutY(355);
        flowSpeedText.setStyle("-fx-font-size: 52px;-fx-font-weight: bold;");
        flowSpeedText.setFill(Color.WHITE);
        flowSpeedText.setEffect(new Glow(1));
        getChildren().add(flowSpeedText);

        TextField flowSpeedField = new TextField();
        flowSpeedField.setLayoutX(740);
        flowSpeedField.setLayoutY(370);
        flowSpeedField.setPrefWidth(170);
        flowSpeedField.setPrefHeight(70);
        flowSpeedField.setStyle("-fx-font-size: 36px;-fx-font-weight: bold;" +
                                "-fx-background-color:#404040;-fx-text-inner-color: white;");
        getChildren().add(flowSpeedField);


        flowSpeedField.setOnAction(e -> {
            try {
                flowSpeed = Double.parseDouble(flowSpeedField.getText());
                flowSpeedText.setText(flowSpeedField.getText());
                setConfig("FlowSpeed", flowSpeed);
            }catch(NumberFormatException ex) {
                flowSpeedField.setText(flowSpeedText.getText());
            }catch(IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void setOffsetInput() {
        Text offsetText = new Text(String.valueOf(offset));
        offsetText.setLayoutX(900);
        offsetText.setLayoutY(500);
        offsetText.setStyle("-fx-font-size: 52px;-fx-font-weight: bold;");
        offsetText.setFill(Color.WHITE);
        offsetText.setEffect(new Glow(1));
        getChildren().add(offsetText);

        TextField offsetField = new TextField();
        offsetField.setLayoutX(740);
        offsetField.setLayoutY(515);
        offsetField.setPrefWidth(170);
        offsetField.setPrefHeight(70);
        offsetField.setStyle("-fx-font-size: 36px;-fx-font-weight: bold;" +
                                "-fx-background-color:#404040;-fx-text-inner-color: white;");
        getChildren().add(offsetField);

        offsetField.setOnAction(e -> {
            try {
                offset = Integer.parseInt(offsetField.getText());
                offsetText.setText(offsetField.getText());
                setConfig("Offset", offset);
            }catch(NumberFormatException ex) {
                offsetField.setText(offsetText.getText());
            }catch(IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void setVolumeInput(){
        File songFolder = new File("Resources/Songs");
        songList = songFolder.listFiles();
        backgroundSong = new Media(new File(songList[0], "song.mp3").toURI().toString());
        backgroundSongPlayer = new MediaPlayer(backgroundSong);

        Slider volumeSlider = new Slider();
        volumeSlider.setLayoutX(740);
        volumeSlider.setLayoutY(700);
        volumeSlider.setPrefWidth(375);
        volumeSlider.setValue(volume);
        getChildren().add(volumeSlider);

        Text volumeText = new Text(String.valueOf(Math.round(volume)));
        volumeText.setLayoutX(900);
        volumeText.setLayoutY(651);
        volumeText.setStyle("-fx-font-size: 52px;-fx-font-weight: bold;");
        volumeText.setFill(Color.WHITE);
        volumeText.setEffect(new Glow(1));
        getChildren().add(volumeText);

        backgroundSongPlayer.volumeProperty().bind(volumeSlider.valueProperty().divide(100));

        volumeSlider.valueProperty().addListener(ov -> {
            volume = volumeSlider.getValue();
            volumeText.setText(String.valueOf(Math.round(volume)));
            try {
                setConfig("Volume", volume);
            }catch(IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setEffectVolumeInput() {
        Media hitSound = new Media(new File("Resources/Audio/Hit.wav").toURI().toString());
        SoundEffectPlayer = new MediaPlayer(hitSound);

        Slider effectVolumeSlider = new Slider();
        effectVolumeSlider.setLayoutX(740);
        effectVolumeSlider.setLayoutY(850);
        effectVolumeSlider.setPrefWidth(375);
        effectVolumeSlider.setValue(effectVolume);
        getChildren().add(effectVolumeSlider);

        Text effectVolumeText = new Text((String.valueOf(Math.round(effectVolume))));
        effectVolumeText.setLayoutX(980);
        effectVolumeText.setLayoutY(803);
        effectVolumeText.setStyle("-fx-font-size: 52px;-fx-font-weight: bold;");
        effectVolumeText.setFill(Color.WHITE);
        effectVolumeText.setEffect(new Glow(1));
        getChildren().add(effectVolumeText);

        SoundEffectPlayer.volumeProperty().bind(effectVolumeSlider.valueProperty().divide(100));

        effectVolumeSlider.valueProperty().addListener(ov -> {
            effectVolume = effectVolumeSlider.getValue();
            effectVolumeText.setText(String.valueOf(Math.round(effectVolume)));
            try {
                setConfig("EffectVolume", effectVolume);
            }catch(IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button effectVolumeButton = new Button("Play");
        effectVolumeButton.setLayoutX(1100);
        effectVolumeButton.setLayoutY(758);
        effectVolumeButton.setStyle("-fx-font-size: 24px;-fx-font-weight: bold;-fx-font:Microsoft YaHei UI Bold;" +
                                    "-fx-background-color: #404040;-fx-text-fill: #f0e0bc;");
        getChildren().add(effectVolumeButton);

        //播音效
        effectVolumeButton.setOnAction(e -> {
            SoundEffectPlayer.seek(Duration.ZERO);
            SoundEffectPlayer.play();
        });
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
            jsonNode.put("Offset", (int) value);
        }else {
            jsonNode.put(Function, value);
        }
        try(FileWriter file = new FileWriter("./Resources/config.json")) {
            file.write(jsonNode.toString());
        }
    }

    private <T> T getConfig(String Function, Class<T> valueType) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File("./Resources/config.json"));
        return objectMapper.convertValue(jsonNode.get(Function), valueType);
    }
}
