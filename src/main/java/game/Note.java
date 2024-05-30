package game;

import javafx.scene.image.ImageView;

/**
 * 音符
 */
public abstract class Note extends ImageView {
    private final int track; //軌道
    private final int bornTime; //音符生成時間(毫秒)
    private final double delayTime; //音符抵達判定點的時間(毫秒)
    private final int hitTime; //音符擊中時間(毫秒)

    Note(int track, int hitTime) {
        this.track = track;
        this.hitTime = hitTime;

        delayTime = RhythmGame.defaultFlowTime * 1000;

        //依照流速計算音符生成時間
        bornTime = hitTime - (int) (delayTime / Settings.flowSpeed);

        this.setScaleX(0.01);
        this.setScaleY(0.01);
        this.setTranslateX(-100);
        this.setTranslateY(-100);
    }

    /**
     * 音符擊中判定
     * 計算擊中時間與抵達時間的誤差
     */
    public Judge OnHitCheck(int pressTime) {
        int deltaTime = pressTime - hitTime;
        if(deltaTime <= RhythmGame.acceptableRange) {
            if(deltaTime > 100) {
                return Judge.Fast_BAD;
            }else if(deltaTime > 70) {
                return Judge.Fast_GOOD;
            }else if(deltaTime > 37) {
                return Judge.Fast_GREAT;
            }else if(deltaTime > 16) {
                return Judge.PERFECT;
            }else if(deltaTime >= -16) {
                return Judge.PERFECT_PLUS;
            }else if(deltaTime > -37) {
                return Judge.PERFECT;
            }else if(deltaTime > -70) {
                return Judge.Late_GREAT;
            }else if(deltaTime > -100) {
                return Judge.Late_GOOD;
            }else if(deltaTime > -RhythmGame.acceptableRange) {
                return Judge.Late_BAD;
            }
        }
        return Judge.NONE;
    }

    /**
     * 音符未擊中
     */
    public void miss() {
        Judgement.judge(Judge.MISS);
    }

    /**
     * 取得音符軌道
     */
    public int getTrack() {
        return track;
    }

    /**
     * 取得音符擊中時間
     */
    public int getHitTime() {
        return hitTime;
    }

    /**
     * 取得音符生成時間
     */
    public int getBornTime() {
        return bornTime;
    }

    /**
     * 取得音符抵達判定點的時間
     */
    public double getDelayTime() {
        return delayTime;
    }

    ;
}
