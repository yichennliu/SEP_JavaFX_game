package model.game;

import model.enums.Medal;

import java.util.HashSet;
import java.util.Set;

/**
 * Achieved medals for a level
 */
public class MedalStatus {
    boolean bronze = false;
    boolean silver = false;
    boolean gold = false;

    public void set(Medal medal) {
        if (medal == Medal.BRONZE) {
            this.bronze = true;
        } else if (medal == Medal.SILVER) {
            this.silver = true;
        } else if (medal == Medal.GOLD)  {
            this.gold = true;
        }
    }

    public boolean has(Medal medal) {
        if (medal == Medal.BRONZE && this.bronze) {
            return true;
        } else if (medal == Medal.SILVER && this.silver) {
            return true;
        } else if (medal == Medal.GOLD && this.gold)  {
            return true;
        }
        return false;
    }

    public int getPoints(){
        if(bronze) return 1;
        if(silver) return 2;
        if(gold) return 3;
        return 0;
    }
}
