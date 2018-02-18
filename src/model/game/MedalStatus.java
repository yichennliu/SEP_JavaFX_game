package model.game;

/**
 * Achieved medals for a level
 */
public class MedalStatus {
    boolean bronze;
    boolean silver;
    boolean gold;

    public boolean isBronze() {
        return bronze;
    }

    public void setBronze(boolean bronze) {
        this.bronze = bronze;
    }

    public boolean isSilver() {
        return silver;
    }

    public void setSilver(boolean silver) {
        this.silver = silver;
    }

    public boolean isGold() {
        return gold;
    }

    public void setGold(boolean gold) {
        this.gold = gold;
    }

}
