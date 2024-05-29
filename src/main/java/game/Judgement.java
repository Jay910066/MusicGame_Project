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
    public static int totalNotes = 0;
    public static double accuracy = 0;
    public static int combo = 0;
    public static int maxCombo = 0;
    public static int score = 0;

    public static void judge(Judge judge) {
        switch(judge) {
            case PERFECT_PLUS:
                perfectPlus++;
                combo++;
                totalNotes++;
                score += 300;
                break;
            case PERFECT:
                perfect++;
                combo++;
                totalNotes++;
                score += 300;
                break;
            case Fast_GREAT:
                great++;
                combo++;
                totalNotes++;
                fast++;
                score += 200;
                break;
            case Fast_GOOD:
                good++;
                combo++;
                totalNotes++;
                fast++;
                score += 100;
                break;
            case Fast_BAD:
                bad++;
                combo++;
                totalNotes++;
                fast++;
                score += 50;
                break;
            case Late_GREAT:
                great++;
                combo++;
                totalNotes++;
                late++;
                score += 200;
                break;
            case Late_GOOD:
                good++;
                combo++;
                totalNotes++;
                late++;
                score += 100;
                break;
            case Late_BAD:
                bad++;
                combo++;
                totalNotes++;
                late++;
                score += 50;
                break;
            case MISS:
                miss++;
                totalNotes++;
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
        totalNotes = 0;
        combo = 0;
        maxCombo = 0;
        accuracy = 0;
        score = 0;
    }

    public static String ranking() {
        if(accuracy == 1) {
            return "X";
        }else if(accuracy >= 0.95) {
            return "S";
        }else if(accuracy >= 0.9) {
            return "A";
        }else if(accuracy >= 0.8) {
            return "B";
        }else if(accuracy >= 0.7) {
            return "C";
        }else{
            return "D";
        }
    }
}
