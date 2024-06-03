package game;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌曲列表畫面
 */
public class SongListMenu extends StackPane {
    public static int selectedIndex = 0; //選擇的歌曲索引
    public static int[] highScores;//高分

    private BorderPane root; //根節點
    private VBox selector; //選擇器
    private int totalItems; //總歌曲數
    private HBox[] songBoxes; //歌曲選項
    private File[] songList; //歌曲列表
    private ReadOsu readOsu; //讀取.osu檔案
    private ImageView background; //背景圖片
    private ImageView songCover; //歌曲封面
    private Text songNameText; //歌曲名稱
    private Text artistText; //歌曲演出者
    private Text creatorText; //圖譜作者
    private Text highScoreText;
    private Media selectedSong; //選擇的歌曲
    private MediaPlayer previewSongPlayer; //預覽歌曲播放器


    /**
     * 歌曲列表畫面
     *
     * @param screenManager 畫面管理器
     */
    public SongListMenu(ScreenManager screenManager) {
        //讀取歌曲資料
        readSongData();

        //設定畫面元素
        setBackground();
        setSelector();
        setDetailBox(screenManager);

        //設定選擇器的初始選擇
        selectedSong = new Media(new File(songList[selectedIndex], "song.mp3").toURI().toString());
        previewSongPlayer = new MediaPlayer(selectedSong);
        selectItem(selectedIndex);

        //選擇器的點擊事件
        songBoxes[selectedIndex].setOnMouseClicked(e -> {
            //如果是4K鍵盤模式，則切換到遊戲畫面
            if(readOsu.is4K_Mania()) {
                screenManager.switchToGamePlay(songList[selectedIndex]);
                previewSongPlayer.stop();
            }
            //否則顯示不支援模式的訊息
            else {
                showModeNotSupportedMessage();
            }
        });

        //按鍵事件
        this.setOnKeyPressed(e -> {
            //按下ESC鍵，切換到主畫面
            if(e.getCode() == KeyCode.ESCAPE) {
                screenManager.switchToMainMenu();
                previewSongPlayer.stop();
            }
            //按下Enter鍵，如果是4K鍵盤模式，則切換到遊戲畫面
            else if(e.getCode() == KeyCode.ENTER) {
                if(readOsu.is4K_Mania()) {
                    SoundEffect confirmSound = new SoundEffect();
                    confirmSound.playComfirmSound();
                    screenManager.switchToGamePlay(songList[selectedIndex]);
                    previewSongPlayer.stop();
                }
                //否則顯示不支援模式的訊息
                else {
                    showModeNotSupportedMessage();
                }
            }
        });
    }

    /**
     * 讀取歌曲資料
     */
    private void readSongData() {
        File songFolder = new File("Resources/Songs");
        songList = songFolder.listFiles();
        totalItems = songList.length;
        songBoxes = new HBox[totalItems];
        highScores = new int[totalItems];
        readOsu = new ReadOsu();

        for(int i = 0; i < totalItems; i++) {
            readOsu.setSong(songList[i].getPath() + "/Info.osu");
            Label songNameLabel = new Label(readOsu.getTitle());
            songNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 60;");
            HBox songBox = new HBox();
            songBox.setMinHeight(100);
            songBox.setMinWidth(600);
            songBox.setStyle("-fx-background-color: darkgray;");
            songBox.setAlignment(Pos.CENTER);
            songBox.setPadding(new Insets(10));
            songBox.getChildren().add(songNameLabel);
            songBoxes[i] = songBox;
        }
    }

    /**
     * 讀highScore
     * 若無則寫
     */
    public int ReadHighScore(int selectedSongIndex){
        try{
            File songFolder = new File("Resources/Songs");
            songList = songFolder.listFiles();
            File file = new File(songList[selectedSongIndex].getPath() +"/highScore.txt");
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            return raf.readInt();
        }catch (IOException e){
            highScores[selectedSongIndex] = 0;
            return 0;
        }
    }

    /**
     * 設置背景
     */
    private void setBackground() {
        background = new ImageView();
        getChildren().add(background);

        //調整背景亮度
        ColorAdjust brightness = new ColorAdjust();
        brightness.setBrightness(-0.1);
        background.setEffect(brightness);

        //設定背景大小，使背景填滿螢幕
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double maxDimension = Math.max(screenBounds.getWidth(), screenBounds.getHeight());
        background.setPreserveRatio(true);
        if(background.getFitHeight() < background.getFitWidth())
            background.setFitHeight(maxDimension * 1);
        else
            background.setFitWidth(maxDimension * 1);

        //設定背景圖片，如果有background.jpg則使用background.jpg，否則使用cover.jpg
        if(new File(songList[selectedIndex].getPath() + "/background.jpg").exists()) {
            background.setImage(new Image("file:" + songList[selectedIndex].getPath() + "/background.jpg"));
        }else {
            background.setImage(new Image("file:" + songList[selectedIndex].getPath() + "/cover.jpg"));
        }

        root = new BorderPane();
        getChildren().add(root);
    }

    /**
     * 設置選擇器
     */
    private void setSelector() {
        selector = new VBox();
        root.setRight(selector);
        selector.setFocusTraversable(true);
        selector.requestFocus();
        selector.setAlignment(Pos.CENTER);
        selector.setPadding(new Insets(100));
        selector.setSpacing(50);

        selector.setOnKeyPressed(event -> {
            SoundEffect selectsongsound = new SoundEffect();
            if(event.getCode() == KeyCode.UP) {
                selectsongsound.playSelectSongSound();
                moveUp();
            }else if(event.getCode() == KeyCode.DOWN) {
                selectsongsound.playSelectSongSound();
                moveDown();
            }
        });
    }

    /**
     * 設置詳細資訊區域
     *
     * @param screenManager 畫面管理器
     */
    private void setDetailBox(ScreenManager screenManager) {
        //詳細資訊區域
        VBox detailBox = new VBox();
        root.setLeft(detailBox);
        detailBox.setMinWidth(480);
        detailBox.setMinHeight(1080);
        detailBox.setStyle("-fx-background-color: rgba(100, 100, 100, 0.6);");
        detailBox.setPadding(new Insets(50));
        detailBox.setAlignment(Pos.CENTER);

        //歌曲封面
        songCover = new ImageView("file:" + songList[selectedIndex].getPath() + "/cover.jpg");
        detailBox.getChildren().add(songCover);
        songCover.setPreserveRatio(true);
        if(songCover.getFitHeight() < songCover.getFitWidth())
            songCover.setFitHeight(384);
        else
            songCover.setFitWidth(384);

        //歌曲資訊
        GridPane songInfo = new GridPane();
        detailBox.getChildren().add(songInfo);
        songInfo.setVgap(10);
        songInfo.setHgap(20);

        //歌曲名稱
        Label songNameLabel = new Label("Song Name:");
        songInfo.add(songNameLabel, 0, 0);
        songNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20; -fx-text-fill: white");
        songNameText = new Text(readOsu.getTitle());
        songInfo.add(songNameText, 1, 0);
        songNameText.setStyle("-fx-font-weight: bold; -fx-font-size: 20");
        songNameText.setFill(Color.WHITE);

        //歌曲演出者
        Label artistLabel = new Label("Artist:");
        songInfo.add(artistLabel, 0, 1);
        artistLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20; -fx-text-fill: white");
        artistText = new Text(readOsu.getArtist());
        songInfo.add(artistText, 1, 1);
        artistText.setStyle("-fx-font-weight: bold; -fx-font-size: 20");
        artistText.setFill(Color.WHITE);

        //圖譜作者
        Label creatorLabel = new Label("Creator:");
        songInfo.add(creatorLabel, 0, 2);
        creatorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20; -fx-text-fill: white");
        creatorText = new Text(readOsu.getCreator());
        songInfo.add(creatorText, 1, 2);
        creatorText.setStyle("-fx-font-weight: bold; -fx-font-size: 20");
        creatorText.setFill(Color.WHITE);

        //高分
        Label highScoreLabel = new Label("HighScore:");
        songInfo.add(highScoreLabel, 0, 3);
        highScoreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20; -fx-text-fill: white");
        highScores[selectedIndex] = ReadHighScore(selectedIndex);
        highScoreText = new Text(String.valueOf(highScores[selectedIndex]));
        songInfo.add(highScoreText, 1, 3);
        highScoreText.setStyle("-fx-font-weight: bold; -fx-font-size: 20");
        highScoreText.setFill(Color.WHITE);

        //按鈕區域
        HBox buttonBox = new HBox();
        detailBox.getChildren().add(buttonBox);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setSpacing(10);
        SoundEffect selectsoundEffect = new SoundEffect();

        //退出選歌頁面按鈕
        ImageView quitButton = new ImageView("file:Resources/Images/QuitButton.png");
        quitButton.setOnMouseEntered(e -> {quitButton.setImage(new Image("file:Resources/Images/QuitButton_Selected.png"));selectsoundEffect.playSelectSound();});
        quitButton.setOnMouseExited(e -> quitButton.setImage(new Image("file:Resources/Images/QuitButton.png")));
        quitButton.setOnMouseClicked(e -> {
            screenManager.switchToMainMenu();
            previewSongPlayer.stop();
        });
        buttonBox.getChildren().add(quitButton);

        //進入設定頁面按鈕
        ImageView settingsButton = new ImageView("file:Resources/Images/SettingsButton.png");
        settingsButton.setOnMouseEntered(e -> {settingsButton.setImage(new Image("file:Resources/Images/SettingsButton_Selected.png"));selectsoundEffect.playSelectSound();});
        settingsButton.setOnMouseExited(e -> settingsButton.setImage(new Image("file:Resources/Images/SettingsButton.png")));
        settingsButton.setOnMouseClicked(e -> {
            screenManager.switchToSettings("SongListMenu");
            previewSongPlayer.stop();
        });
        buttonBox.getChildren().add(settingsButton);
    }

    /**
     * 顯示不支援模式的訊息
     */
    private void showModeNotSupportedMessage() {
        //訊息標籤
        Label message = new Label("This mode is not supported yet.");
        message.setStyle("-fx-font-weight: bold; -fx-font-size: 30; -fx-text-fill: white; -fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 10;");
        getChildren().add(message);
        PauseTransition pause = new PauseTransition(Duration.seconds(0.2));
        pause.setOnFinished(e -> getChildren().remove(message));
        pause.play();
    }

    /**
     * 選擇歌曲
     *
     * @param index 歌曲索引
     */
    private void selectItem(int index) {
        //讀取歌曲資訊
        readOsu.setSong(songList[index].getPath() + "/Info.osu");

        //設定背景圖片
        if(new File(songList[selectedIndex].getPath() + "/background.jpg").exists()) {
            background.setImage(new Image("file:" + songList[selectedIndex].getPath() + "/background.jpg"));
        }else {
            background.setImage(new Image("file:" + songList[selectedIndex].getPath() + "/cover.jpg"));
        }

        //設定歌曲封面
        songCover.setImage(new Image("file:" + songList[index].getPath() + "/cover.jpg"));

        //設定歌曲資訊
        songNameText.setText(readOsu.getTitle());
        artistText.setText(readOsu.getArtist());
        creatorText.setText(readOsu.getCreator());
        highScores[index] = ReadHighScore(index);
        highScoreText.setText(String.valueOf(highScores[index]));

        //播放預覽歌曲
        previewSongPlayer.stop();
        selectedSong = new Media(new File(songList[index], "song.mp3").toURI().toString());
        previewSongPlayer = new MediaPlayer(selectedSong);
        previewSongPlayer.setVolume(Settings.volume / 100.0);
        previewSongPlayer.setStartTime(Duration.millis(readOsu.getPreviewTime()));
        previewSongPlayer.play();

        //設定選擇器顯示的歌曲
        selector.getChildren().clear();
        HBox songBox1 = songBoxes[(index - 2 + totalItems) % totalItems];
        HBox songBox2 = songBoxes[(index - 1 + totalItems) % totalItems];
        HBox songBox3 = songBoxes[index];
        HBox songBox4 = songBoxes[(index + 1) % totalItems];
        HBox songBox5 = songBoxes[(index + 2) % totalItems];

        songBox1.setStyle("-fx-background-color: rgba(100,100,100,1);");
        songBox1.setScaleX(0.8);
        songBox1.setScaleY(0.8);
        songBox2.setStyle("-fx-background-color: darkgray;");
        songBox2.setScaleX(0.9);
        songBox2.setScaleY(0.9);
        songBox3.setStyle("-fx-background-color: lightblue;");
        songBox3.setScaleX(1);
        songBox3.setScaleY(1);
        songBox4.setStyle("-fx-background-color: darkgray;");
        songBox4.setScaleX(0.9);
        songBox4.setScaleY(0.9);
        songBox5.setStyle("-fx-background-color: rgba(100,100,100,1);");
        songBox5.setScaleX(0.8);
        songBox5.setScaleY(0.8);
        selector.getChildren().addAll(songBox1, songBox2, songBox3, songBox4, songBox5);
    }

    /**
     * 移動選擇器向上
     */
    private void moveUp() {
        selectedIndex = (selectedIndex - 1 + totalItems) % totalItems;
        selectItem(selectedIndex);
    }

    /**
     * 移動選擇器向下
     */
    private void moveDown() {
        selectedIndex = (selectedIndex + 1) % totalItems;
        selectItem(selectedIndex);
    }
}
