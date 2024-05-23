package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Single extends Note {
    private int track;
    private int hitTime;
    private boolean hit = false;
    private boolean kill = false;

    Single(int track, int hitTime) {
        super(track, hitTime);
        if(track == 0) {
            this.setImage(new Image("file:Resources/Images/Left_side.png"));
        }else if(track == 1) {
            this.setImage(new Image("file:Resources/Images/Left_middle.png"));
        }else if(track == 2) {
            this.setImage(new Image("file:Resources/Images/Right_middle.png"));
        }else if(track == 3) {
            this.setImage(new Image("file:Resources/Images/Right_side.png"));
        }
    }
}
