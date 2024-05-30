package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * 單擊音符
 */
public class Single extends Note {
    /**
     * @param track 音符軌道
     * @param hitTime 音符抵達判定點的時間
     */
    Single(int track, int hitTime) {
        super(track, hitTime);

        //設定音符圖片
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
