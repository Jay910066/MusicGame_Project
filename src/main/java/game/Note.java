package game;

import javafx.scene.image.ImageView;

public abstract class Note extends ImageView{
    private int track;
    private int bornTime;
    private double delayTime;
    private int hitTime;
    private boolean hit = false;
    private boolean miss = false;
    private ImageView noteImage;

    Note(int track, int hitTime) {
        this.track = track;
        this.hitTime = hitTime;
        delayTime = RhythmGame.defaultFlowTime * 1000;
        bornTime = hitTime - (int)(delayTime / Settings.flowSpeed);
        this.setScaleX(0.01);
        this.setScaleY(0.01);
        this.setTranslateX(-100);
        this.setTranslateY(-100);
    }

    public int getTrack(){
        return track;
    };
    public int getHitTime(){
        return hitTime;
    };

    public int getBornTime(){
        return bornTime;
    };

    public Judge OnHitCheck(int pressTime){
        int deltaTime = pressTime - hitTime;
        if(deltaTime <= RhythmGame.acceptableRange) {
            hit();
            if(deltaTime > 100){
                return Judge.Fast_BAD;
            }else if(deltaTime > 70){
                return Judge.Fast_GOOD;
            }else if(deltaTime > 37){
                return Judge.Fast_GREAT;
            }else if(deltaTime > 16) {
                return Judge.PERFECT;
            }else if(deltaTime >= -16){
                return Judge.PERFECT_PLUS;
            }else if(deltaTime > -37) {
                return Judge.PERFECT;
            }else if(deltaTime > -70){
                return Judge.Late_GREAT;
            }else if(deltaTime > -100){
                return Judge.Late_GOOD;
            }else if(deltaTime > -RhythmGame.acceptableRange) {
                return Judge.Late_BAD;
            }
        }
        return Judge.NONE;
    };

    public void hit(){
        hit = true;
    };

    public boolean isHit(){
        return hit;
    };

    public void miss(){
        Judgement.judge(Judge.MISS);
        miss = true;
    };

    public boolean isMiss(){
        return miss;
    };

    public ImageView getImageView(){
        return noteImage;
    };
}
