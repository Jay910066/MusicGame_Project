module game.musicgame_project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires com.almasb.fxgl.all;
    requires javafx.media;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;

    opens game to javafx.fxml;
    exports game;
}