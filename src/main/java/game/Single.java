package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Single extends Note{
    private int track;
    private int hitTime;
    private boolean hit = false;
    private boolean kill = false;

    Single(int track, int hitTime) {
        super(track, hitTime);
        if (track == 0 || track == 3) {
            this.setImage(new Image("file:Resources/Images/middle.png"));
        } else if (track == 1 || track == 2) {
            this.setImage(new Image("file:Resources/Images/middle.png"));
        }
    }
}
