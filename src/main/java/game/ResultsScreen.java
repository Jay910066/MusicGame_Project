package game;

import javafx.geometry.Rectangle2D;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;

/**
 * 結果畫面
 */
public class ResultsScreen extends Pane {
    public ResultsScreen(ScreenManager screenManager, ImageView background, ReadOsu readOsu) {
        //設定畫面
        this.setFocusTraversable(true);
        this.requestFocus();

        //設置背景，結果面板，歌曲資訊，Rank圖片，離開按鈕
        setBackground(background);
        setResultsPanel();
        setSongInfoText(readOsu);
        setRankImage();
        setQuitButton(screenManager);

        //按下ESC返回歌曲列表
        this.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE) {
                screenManager.switchToSongListMenu();
            }
        });
    }

    /**
     * 設置背景
     */
    private void setBackground(ImageView background) {
        getChildren().add(background);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double maxDimension = Math.max(screenBounds.getWidth(), screenBounds.getHeight());
        background.setPreserveRatio(true);
        if(background.getFitHeight() < background.getFitWidth())
            background.setFitHeight(maxDimension * 1);
        else
            background.setFitWidth(maxDimension * 1);
    }

    /**
     * 設置歌曲資訊
     */
    private void setSongInfoText(ReadOsu readOsu) {
        //歌曲名稱
        Text titleText = new Text(readOsu.getArtist() + " - " + readOsu.getTitle());
        titleText.setStyle("-fx-font-size: 36;-fx-font-weight: bold;");
        titleText.setFill(Color.WHITE);
        titleText.setEffect(new Glow(1));
        titleText.setLayoutX(10);
        titleText.setLayoutY(46);
        getChildren().add(titleText);

        //圖譜作者
        Text creatorText = new Text("Beatmap by " + readOsu.getCreator());
        creatorText.setStyle("-fx-font-size: 28;-fx-font-weight: bold;");
        creatorText.setFill(Color.WHITE);
        creatorText.setEffect(new Glow(1));
        creatorText.setLayoutX(10);
        creatorText.setLayoutY(84);
        getChildren().add(creatorText);
    }

    /**
     * 設置結果面板
     */
    private void setResultsPanel() {
        //結果面板
        ImageView resultsPanel = new ImageView("file:Resources/Images/ResultsPanel.png");
        resultsPanel.setEffect(new Bloom(0.5));
        getChildren().add(resultsPanel);

        //分數
        Text scoreText = new Text(String.valueOf(Judgement.score));
        scoreText.setStyle("-fx-font-size: 40;-fx-font-weight: bold;");
        scoreText.setFill(Color.WHITE);
        scoreText.setEffect(new Glow(1));
        scoreText.setLayoutX(260);
        scoreText.setLayoutY(217);
        getChildren().add(scoreText);

        //評價面板
        GridPane judgementGrid = new GridPane();
        judgementGrid.setLayoutX(325);
        judgementGrid.setLayoutY(302.5);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPrefWidth(362.5);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPrefWidth(362.5);
        judgementGrid.getColumnConstraints().addAll(column1, column2);

        RowConstraints row1 = new RowConstraints();
        row1.setPrefHeight(144);
        RowConstraints row2 = new RowConstraints();
        row2.setPrefHeight(144);
        RowConstraints row3 = new RowConstraints();
        row3.setPrefHeight(144);
        judgementGrid.getRowConstraints().addAll(row1, row2, row3);

        //設置各評價數量
        Text perfectPlusText = setJudgementGrid(Judgement.perfectPlus);
        Text perfectText = setJudgementGrid(Judgement.perfect);
        Text greatText = setJudgementGrid(Judgement.great);
        Text goodText = setJudgementGrid(Judgement.good);
        Text badText = setJudgementGrid(Judgement.bad);
        Text missText = setJudgementGrid(Judgement.miss);
        judgementGrid.add(perfectPlusText, 1, 0);
        judgementGrid.add(perfectText, 0, 0);
        judgementGrid.add(greatText, 0, 1);
        judgementGrid.add(goodText, 1, 1);
        judgementGrid.add(badText, 0, 2);
        judgementGrid.add(missText, 1, 2);
        getChildren().add(judgementGrid);

        //Combo數量
        Text comboText = new Text(String.valueOf(Judgement.maxCombo));
        comboText.setStyle("-fx-font-size: 40;-fx-font-weight: bold;");
        comboText.setFill(Color.WHITE);
        comboText.setEffect(new Glow(1));
        comboText.setLayoutX(260);
        comboText.setLayoutY(779);

        //準確率
        Text accuracyText = new Text(String.format("%.2f", Judgement.accuracy * 100) + "%");
        accuracyText.setStyle("-fx-font-size: 40;-fx-font-weight: bold;");
        accuracyText.setFill(Color.WHITE);
        accuracyText.setEffect(new Glow(1));
        accuracyText.setLayoutX(700);
        accuracyText.setLayoutY(779);
        getChildren().addAll(comboText, accuracyText);
    }

    /**
     * 設置評價數量
     *
     * @param judgement 評價數量
     * @return 評價數量Text
     */
    private Text setJudgementGrid(int judgement) {
        Text judgementText = new Text(String.valueOf(judgement));
        judgementText.setStyle("-fx-font-size: 40;-fx-font-weight: bold;");
        judgementText.setFill(Color.WHITE);
        judgementText.setEffect(new Glow(1));
        return judgementText;
    }

    /**
     * 設置Rank圖片
     */
    private void setRankImage() {
        ImageView rankImage = new ImageView();
        rankImage.setLayoutX(1540);
        rankImage.setLayoutY(480);
        rankImage.setScaleX(7);
        rankImage.setScaleY(7);
        rankImage.setEffect(new Glow(1));
        switch(Judgement.ranking()) {
            case "X":
                rankImage.setImage(new Image("file:Resources/Images/Rankings/ranking-X-small@2x.png"));
            case "S":
                rankImage.setImage(new Image("file:Resources/Images/Rankings/ranking-S-small@2x.png"));
                break;
            case "A":
                rankImage.setImage(new Image("file:Resources/Images/Rankings/ranking-A-small@2x.png"));
                break;
            case "B":
                rankImage.setImage(new Image("file:Resources/Images/Rankings/ranking-B-small@2x.png"));
                break;
            case "C":
                rankImage.setImage(new Image("file:Resources/Images/Rankings/ranking-C-small@2x.png"));
                break;
            case "D":
                rankImage.setImage(new Image("file:Resources/Images/Rankings/ranking-D-small@2x.png"));
                break;
        }
        getChildren().add(rankImage);
    }

    /**
     * 設置離開按鈕
     *
     * @param screenManager 畫面管理器
     */
    private void setQuitButton(ScreenManager screenManager) {
        ImageView quitButton = new ImageView("file:Resources/Images/QuitButton.png");
        quitButton.setTranslateX(0);
        quitButton.setTranslateY(980);
        quitButton.setOnMouseEntered(e -> quitButton.setImage(new Image("file:Resources/Images/QuitButton_Selected.png")));
        quitButton.setOnMouseExited(e -> quitButton.setImage(new Image("file:Resources/Images/QuitButton.png")));
        getChildren().add(quitButton);

        quitButton.setOnMouseClicked(e -> {
            screenManager.switchToSongListMenu();
        });
    }
}
