package model.game;
import model.enums.Direction;
import model.enums.InputDirection;
import model.enums.Situation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static model.enums.Situation.ANY;

public class Rule {

    private Situation situation;
    private Direction direction;
    private List<RuleElementOriginal> original;
    private List<RuleElementResult> result;
    private Integer sparsity;

    public Rule(Situation situation, Direction direction, List<RuleElementOriginal> original,
                List<RuleElementResult> result, Integer sparsity) {
        this.situation = situation; //Tastendruck
        this.direction = direction;
        this.original = original;
        this.result = result;
        this.sparsity = sparsity;
    }

    private int inc(int i) {
        return i++;
    }

    public void execute(Feld[][] map, InputDirection inputD) {

        Situation inputSituation = (inputD != null) ? inputD.getSituation() : null;


        if (!rightSituation(inputSituation)) {
            return;
        }

        int ruleLength = original.size();
        int i = 0;

        while (insideBoundsOfI(map, i)) {

            int j = ((direction == Direction.SOUTH || direction == Direction.EAST) ? 0 : (direction == Direction.NORTH) ? map.length : map[0].length);

            while (insideBoundsOfJ(map, j)) {
                System.out.println("CHECKING " + i + " " + j);
                List<Feld> feldList = new ArrayList();
                try {
                    /*if(isNotMatchable(inputSituation)) return;*/
                    feldList.add(map[i][j]);
                    map[i][j].getNeighboursRecursive(direction, ruleLength, feldList);
                    System.out.println("Comparing found list: \n" + printFeldList(feldList) + "\ncounter=" + ruleLength);
                    if (matchFeldListWithOriginalList(feldList)) {
                        System.out.println("Passt!: ");
                        for (int z = 0; z <= ruleLength; z++) {
                            this.result.get(z).replace(feldList.get(z), feldList);
                        }

                        j = (direction == Direction.NORTH || direction == Direction.WEST) ? j - ruleLength - 1 : j + ruleLength + 1;
                        if (exceedBoundsOfJ(map, j))
                            throw new IndexOutOfBoundsException();
                    } else {
                        System.out.println("Passt nicht");
                        j++;
                    }

                } catch (IndexOutOfBoundsException e) {
                    if (direction == Direction.WEST) j = map[0].length;
                    else if (direction == Direction.NORTH) j = map.length;
                    else j = 0;
                    break;
                }
            }
            i++;
        }

        if (inputSituation == Situation.RARE && this.situation == Situation.RARE && this.sparsity != null) {
            this.sparsity -= 1;
        }
    }

    private boolean matchFeldListWithOriginalList(List<Feld> list) {
        for (int k = 0; k < list.size(); k++) {
            RuleElementOriginal ruleElmnt = original.get(k);
            if (!ruleElmnt.matches(list.get(k))) return false;
        }
        return true;
    }

    /*checkt, ob die richtige Situation vorliegt */
    private boolean rightSituation(Situation inputSituation) {
        if (this.situation == Situation.ANY) return true;
        if (this.situation == Situation.RARE && this.sparsity != null && this.sparsity.intValue() > 0) return true;
        if (inputSituation != null && inputSituation.equals(this.situation)) return true;
        return false;
    }

    private boolean exceedBoundsOfJ(Feld[][] map, int j) {
        if (((direction == Direction.NORTH || direction == Direction.WEST) && j < 0) ||
                ((direction == Direction.EAST) && j >= map[0].length) ||
                (direction == Direction.SOUTH && j >= map.length))
            return true;
        return false;
    }

    private boolean insideBoundsOfJ(Feld[][] map, int j) {
        if (((direction == Direction.NORTH || direction == Direction.WEST) && j >= 0)
                || ((direction == Direction.EAST) && j < map[0].length)
                || ((direction == Direction.SOUTH) && j < map.length))
            return true;
        return false;
    }

    private boolean insideBoundsOfI(Feld[][] map, int i) {
        if (i < ((direction == Direction.WEST || direction == Direction.EAST) ? map.length : map[0].length))
            return true;
        return false;
    }

    private boolean isNotMatchable(Situation inputSituation) {
        if (inputSituation == null) {
            return true;
        }
        if ((this.sparsity == null)) {

            return true;
        }
        if (inputSituation == ANY) {
            return false;
        }

        return true;
    }

//&& inputSituation == situation
    //situation == Situation.RARE
    //inputSituation == Situation.RARE &&
    //Alive mit if statement prüfen

    public int getSparsity() {
        return this.sparsity = (int) (Math.random());
    }


    private String printFeldList(List<Feld> feldList) {
        String resultString = "[";
        for (Feld feld : feldList) {
            resultString = resultString + " " + feld.getToken().toString();
        }
        resultString = resultString + "]";
        return resultString;
    }
}