package model;

import model.enums.Direction;
import model.enums.Situation;

import java.util.List;

public class Rule {

    private Situation situation;
    private Direction direction;
    private List<RuleElement> original;
    private List<RuleElement> result;
    private Integer sparsity;

    public Rule(Situation situation, Direction direction, List<RuleElement> original, List<RuleElement> result, Integer sparsity){
        this.situation = situation;
        this.direction = direction;
        this.original = original;
        this.result = result;
        this.sparsity = sparsity;
    }

    public void execute(){
        // TODO: implement
    }
}
