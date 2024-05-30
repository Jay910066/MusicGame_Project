package game;

import javafx.scene.image.Image;

/**
 * 長按音符
 */
public class Hold extends Note {
    private int endTime; //音符結束時間
    private boolean startNote = false; //是否為開始音符
    private boolean endNote = false; //是否為結尾音符
    private boolean bodyNote = false; //是否為中間音符

    /**
     * 開始音符
     *
     * @param track 音符軌道
     * @param hitTime 音符擊中時間
     * @param endTime 音符結束時間
     */
    Hold(int track, int hitTime, int endTime) {
        super(track, hitTime);
        startNote = true;
        this.endTime = endTime;
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

    /**
     * 結尾音符
     *
     * @param track 音符軌道
     * @param endTime 音符結束時間
     */
    Hold(int track, int endTime) {
        super(track, endTime);
        endNote = true;
        this.endTime = endTime;
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

    /**
     * 中間音符
     *
     * @param track 音符軌道
     */
    Hold(int track){
        super(track, -1);
        bodyNote = true;
        if(track == 0) {
            this.setImage(new Image("file:Resources/Images/Left_side_hold_body.png"));
        }else if(track == 1) {
            this.setImage(new Image("file:Resources/Images/Left_middle_hold_body.png"));
        }else if(track == 2) {
            this.setImage(new Image("file:Resources/Images/Right_middle_hold_body.png"));
        }else if(track == 3) {
            this.setImage(new Image("file:Resources/Images/Right_side_hold_body.png"));
        }
    }

    /**
     * 取得音符結束時間
     *
     * @return 音符結束時間
     */
    public int getEndTime() {
        return endTime;
    }

    /**
     * 是否為開始音符
     *
     * @return 若為開始音符，則回傳true
     */
    public boolean isStartNote() {
        return startNote;
    }

    /**
     * 是否為結尾音符
     *
     * @return 若為結尾音符，則回傳true
     */
    public boolean isEndNote() {
        return endNote;
    }

    /**
     * 是否為中間音符
     *
     * @return 若為中間音符，則回傳true
     */
    public boolean isBodyNote() {
        return bodyNote;
    }
}
