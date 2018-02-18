package model.game;
import model.enums.Direction;
import model.enums.InputDirection;
import model.enums.Situation;
import java.util.ArrayList;
import java.util.List;

import static model.enums.Direction.*;
import static model.enums.Situation.ANY;

public class Rule {

    private Situation situation;
    private Direction direction;
    private List<RuleElementOriginal> original;
    private List<RuleElementResult> result;
    private Integer sparsity;

    public Rule(Situation situation, Direction direction, List<RuleElementOriginal> original,
                List<RuleElementResult> result, Integer sparsity) {
        this.situation = situation;
        this.direction = direction;
        this.original = original;
        this.result = result;
        this.sparsity = sparsity;
    }

    public void execute(Feld[][] map, InputDirection inputD) {

        int i = 0;
        int startJ = getStartJ(map, direction);
        int   endJ = getEndJ(map, direction);
        int startI = 0;
        int   endI = getEndI(map, direction);
        int ruleLength = original.size();

        Situation inputSituation = (inputD != null) ? inputD.getSituation() : null;

        if (!isRightSituation(inputSituation)) {return;}

        while (between(startI,i,endI)) {

            int j = ((direction == SOUTH || direction == EAST) ? 0 : (direction == NORTH) ? map.length-1 : map[0].length-1);

            while (between(startJ,j,endJ)) {
                List<Feld> feldList = new ArrayList();
                try {
                    int y = (direction == EAST || direction == WEST) ? i : j;
                    int x = (direction == NORTH || direction == SOUTH) ? i : j;
                    feldList.add(map[y][x]);
                    map[y][x].getNeighboursRecursive(direction, ruleLength, feldList);

                    if (matchFeldListWithOriginalList(feldList)) {
                        for (int z = 0; z <= ruleLength; z++) {
                            this.result.get(z).replace(feldList.get(z), feldList);
                        }

                        j = (direction == NORTH || direction == WEST) ? j - ruleLength - 1 : j + ruleLength + 1;
                        if (!between(startJ,j,endJ))
                            throw new IndexOutOfBoundsException();
                    } else {

                        j = (direction == NORTH || direction == WEST) ? j-1  : j+1;
                    }

                } catch (IndexOutOfBoundsException e) {
                    break;
                }
            }
            i++;
        }

        if (inputSituation == Situation.RARE && this.situation == Situation.RARE && this.sparsity != null) {
            this.sparsity -= 1;
        }
    }

    private int getStartJ(Feld[][] map, Direction direction) {

        if (direction == NORTH) {
            return map.length - 1;
        } else if(direction == WEST){
            return map[0].length - 1;
        } else {
            return 0;
        }
    }

    private int getEndJ(Feld[][] map, Direction direction) {

        if (direction == EAST) {
            return map[0].length-1;
        } else if(direction == SOUTH) {
            return map.length - 1;
        } else return 0;
    }

    private int getEndI(Feld[][] map, Direction direction){

        if(direction == NORTH||direction == SOUTH)
            return map[0].length -1;
        else
            return map.length -1;
    }

    private boolean isRightSituation(Situation inputSituation) {
        if (this.situation == Situation.ANY) return true;
        if (this.situation == Situation.RARE && this.sparsity != null && this.sparsity.intValue() > 0) return true;
        if (inputSituation != null && inputSituation.equals(this.situation)) return true;
        return false;
    }

    /*überprüft, ob b zwischen a und c ist*/
    private boolean between(int a, int b, int c){
        if(c>a) {
            return (b >= a && b <= c);
        }
        if(a>c){
            return (b>=c && b<=a);
        }

        return(b==c);
    }

    private boolean matchFeldListWithOriginalList(List<Feld> list) {
        for (int k = 0; k < list.size(); k++) {
            RuleElementOriginal ruleElmnt = original.get(k);
            if (!ruleElmnt.matches(list.get(k))) return false;
        }
        return true;
    }

    public int getSparsity() {
        return this.sparsity = (int) (Math.random());
    }

    public Situation getSituation() {
        return this.situation;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public List<RuleElementOriginal> getOriginal() {
        return this.original;
    }

    public List<RuleElementResult> getResult() {
        return this.result;
    }
    }