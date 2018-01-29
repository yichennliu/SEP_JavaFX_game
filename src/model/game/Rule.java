package model.game;

import model.enums.Direction;
import model.enums.Situation;

import java.util.List;

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

    public void
    execute(){
        // TODO: implement
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

    public Integer getSparsity() {
        return this.sparsity;
    }
}
