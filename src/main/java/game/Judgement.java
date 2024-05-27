package game;

public class Judgement {
    public static int perfectPlus = 0;
    public static int perfect = 0;
    public static int great = 0;
    public static int good = 0;
    public static int bad = 0;
    public static int miss = 0;
    public static int fast = 0;
    public static int late = 0;
    public static double accuracy = 0;
    public static int combo = 0;
    public static int maxCombo = 0;
    public static int score = 0;

    public static void judge(Judge judge) {
        switch(judge) {
            case PERFECT_PLUS:
                perfectPlus++;
                combo++;
                score += 300;
                break;
            case PERFECT:
                perfect++;
                combo++;
                score += 300;
                break;
            case Fast_GREAT:
                great++;
                combo++;
                fast++;
                score += 200;
                break;
            case Fast_GOOD:
                good++;
                combo++;
                fast++;
                score += 100;
                break;
            case Fast_BAD:
                bad++;
                combo++;
                fast++;
                score += 50;
                break;
            case Late_GREAT:
                great++;
                combo++;
                late++;
                score += 200;
                break;
            case Late_GOOD:
                good++;
                combo++;
                late++;
                score += 100;
                break;
            case Late_BAD:
                bad++;
                combo++;
                late++;
                score += 50;
                break;
            case MISS:
                miss++;
                combo = 0;
                break;
        }
        accuracy = (300 * (perfectPlus + perfect) + 200 * great + 100 * good + 50 * bad) / (double) (300 * (perfectPlus + perfect + great + good + bad + miss));
        GamePlay.updateComboText(judge);
        if(combo > maxCombo) {
            maxCombo = combo;
        }
    }

    public static void reset() {
        perfectPlus = 0;
        perfect = 0;
        great = 0;
        good = 0;
        bad = 0;
        miss = 0;
        fast = 0;
        late = 0;
        combo = 0;
        maxCombo = 0;
        accuracy = 0;
        score = 0;
    }
}
