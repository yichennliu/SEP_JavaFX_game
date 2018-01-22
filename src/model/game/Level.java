package model.game;

import model.enums.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Level {
    private String name;
    /** Aufbau von oben links: map[rowNum][colNum] */
    private Feld[][] map;
    /** laut Doku genau 3 Einträge */
    private int[] gemGoals;
    /** laut Doku genau 3 Einträge */
    private int[] tickGoals;
    private List<Rule> pre;
    private List<Rule> post;
    /** optional */
    private Integer maxslime;
    /** globale properties: GEMS, TICKS, X, Y, Z */
    private Map<Property, Integer> properties = new HashMap<>();
    /** Derzeitige Anzahl der Felder mit Schleim */
    public int slimeCount;
    /** vorbereitete Änderungen */
    private Map<Feld, Token> tokensToChange = new HashMap<>();
    /** vorbereitete Änderungen */
    private Map<Feld, Map<Property, Integer>> propertiesToChange = new HashMap<>();
    private InputDirection inputDirection = null;


    public Level(String name, Feld[][] map, int[] gemGoals, int[] tickGoals, List<Rule> pre, List<Rule> post, Integer maxslime) {
        this.name = name;
        this.map = map;
        this.gemGoals = gemGoals;
        this.tickGoals = tickGoals;
        this.pre = pre;
        this.post = post;
        this.maxslime = maxslime;

        for(int row = 0; row < this.getHeight();  row++){
            for (int column = 0; column < this.getWidth(); column++){
                Feld feld = this.getFeld(row, column);
                // set backlink to level
                feld.setLevel(this);
                // count slime
                if (feld.isToken(Token.SLIME)) this.slimeCount++;
            }
        }
    }

    public String getName() {
        return name;
    }

    /** @return Feld, or null */
    public Feld getFeld(int row, int col) {
        if (row >= 0 && row < this.getHeight() && col >= 0 && col < this.getWidth()) {
            return this.map[row][col];
        } else {
            return null;
        }
    }

    public Feld[][] getMap(){
        return this.map;
    }

    public int getWidth() {
        return this.map[0].length;
    }

    public int getHeight() {
        return this.map.length;
    }

    public int[] getGemGoals() {
        return gemGoals;
    }

    public int[] getTickGoals() {
        return tickGoals;
    }

    public List<Rule> getPre() {
        return pre;
    }

    public List<Rule> getPost() {
        return post;
    }

    public Integer getMaxslime() {
        return maxslime;
    }

    public boolean isSlimeMaximumReached() {
        return this.getMaxslime() != null && this.getMaxslime() < this.slimeCount;
    }

    private InputDirection getInputDirection() {
        return inputDirection;
    }

    /**
     * Buffer for input directions
     *
     * @param inputDirection where to go or dig
     */
    public void setInputDirection(InputDirection inputDirection) {
        this.inputDirection = inputDirection;
    }

    /** @return global value associated to property */
    public Integer getPropertyValue(Property property){
        return this.properties.get(property) == null
                ? 0
                : this.properties.get(property);
    }

    /**
     * @param property Global property
     * @param value value
     */
    public void setPropertyValue(Property property, int value){
        if (property.isGlobal()) {
            this.properties.put(property, value);
        } else {
            throw new IllegalArgumentException("Property must be global");
        }
    }

    /** Buffer a token change on a field, but do not apply it yet */
    public void bufferChangeToken(Feld field, Token newToken) {
        this.tokensToChange.put(field, newToken);
    }

    /** Buffer a property change on a field, but do not apply it yet */
    public void bufferChangeProperty(Feld field, Property property, int newValue) {
        Map<Property, Integer> properties = this.propertiesToChange.get(field);
        if (properties != null) {
            properties.put(property, newValue);
        } else {
            properties = new HashMap<Property, Integer>();
            properties.put(property, newValue);
            this.propertiesToChange.put(field, new HashMap<Property, Integer>());
        }
    }

    /**
     * Buffer to increase (or decrease through negative value)
     * a possibly already bufferd property.
     */
    public void bufferIncreaseProperty(Feld field, Property property, int by) {
        Map<Property, Integer> propMap = this.propertiesToChange.get(field);
        if (propMap != null) {
            Integer intVal = propMap.get(property);
            if (intVal != null) {
                this.bufferChangeProperty(field, property, intVal+by);
                return;
            }
        }
        // else: property not set
        this.bufferChangeProperty(field, property, /* 0 + */ by);
    }

    public void applyBufferedChanges() {
        // apply token changes
        for (Map.Entry entry : this.tokensToChange.entrySet()) {
            ((Feld) entry.getKey()).setToken((Token) entry.getValue());
        }

        // apply property value changes
        for (Map.Entry entry : this.propertiesToChange.entrySet()) {
            Feld field = (Feld) entry.getKey();
            Map<Property, Integer> properties = (Map<Property, Integer>) entry.getValue();
            for (Map.Entry entry2 : properties.entrySet()) {
                field.setPropertyValue((Property) entry2.getKey(), (Integer) entry2.getValue());
            }
        }

        // clear change buffer
        this.tokensToChange.clear();
        this.propertiesToChange.clear();
    }

    public void executeMainRules() {
        for (int row = 0; row < this.getHeight(); row++) {
            for (int col = 0; col < this.getWidth(); col++) {
                Feld current = this.getFeld(row, col);
                Feld top = current.getNeighbour(FieldDirection.TOP);
                Feld rightTop = current.getNeighbour(FieldDirection.RIGHTTOP);
                Feld right = current.getNeighbour(FieldDirection.RIGHT);
                Feld rightBottom = current.getNeighbour(FieldDirection.RIGHTBOTTOM);
                Feld bottom = current.getNeighbour(FieldDirection.BOTTOM);
                Feld leftBottom = current.getNeighbour(FieldDirection.LEFTBOTTOM);
                Feld left = current.getNeighbour(FieldDirection.LEFT);
                Feld leftTop = current.getNeighbour(FieldDirection.LEFTTOP);

                /* Spielerbewegung */
                if (current.isToken(Token.ME)) {
                    // nichts tun
                    if (this.getInputDirection() == null) {}

                    // nur graben
                    else if (this.getInputDirection().isDigOnly()) {
                        Feld toDig = current.getNeighbour(this.getInputDirection());
                        if (toDig != null && toDig.isToken(Token.MUD)) toDig.bufferSetToken(Token.PATH);
                        if (toDig != null && toDig.isToken(Token.GEM) && !toDig.hasProperty(Property.FALLING)) {
                            toDig.bufferSetToken(Token.PATH);
                            current.bufferIncreasePropertyValue(Property.GEMS, 1);
                        }
                    }

                    // bewegen + graben
                    else if (this.getInputDirection().isGo()) {
                        Feld next = current.getNeighbour(this.getInputDirection());
                        if (next != null) {
                            // bewegen
                            if (next.isToken(Token.PATH, Token.MUD, Token.GEM)) {
                                // Edelstein einsammeln
                                if (next.isToken(Token.GEM)) {
                                    current.bufferIncreasePropertyValue(Property.GEMS, 1);
                                }
                                // bewegen (+graben/einsammeln)
                                current.bufferMoveTo(next);
                            }

                            // schieben
                            if ((this.getInputDirection() == InputDirection.GOLEFT
                                        || this.getInputDirection() == InputDirection.GORIGHT)
                                    && next.hasProperty(Property.PUSHABLE)) {

                                Feld behindNext = next.getNeighbour(this.getInputDirection());
                                if (behindNext != null && behindNext.isToken(Token.PATH)) {
                                    // Objekt auf freies Feld verschieben
                                    next.bufferMoveTo(behindNext);
                                    // Spielfigur auf Platz von Objekt bewegen
                                    current.bufferMoveTo(next);
                                }
                            }
                        }
                    }
                }


                /* Gravitation */

                // Lose Gegenstände fallen lassen
                if (current.hasProperty(Property.LOOSE)) {
                    // bottom muss frei sein
                    if (bottom != null && bottom.isToken(Token.PATH)) {
                        // Objekt nach unten bewegen
                        current.bufferMoveTo(bottom);
                        bottom.bufferSetProperty(Property.FALLING);
                    } else {
                        // Falls bottom nicht frei ist, dafür aber SLIPPERY
                        if (bottom != null && bottom.hasProperty(Property.SLIPPERY)) {
                            // wenn right und rightBottom frei sind
                            if (right != null && right.isToken(Token.PATH)
                                    && rightBottom != null && rightBottom.isToken(Token.PATH)) {
                                // Objekt nach rechts unten bewegen
                                current.bufferMoveTo(rightBottom);
                                rightBottom.bufferSetProperty(Property.FALLING);
                            }
                            // falls nicht, und left und bottomleft sind frei
                            else if (left != null && left.isToken(Token.PATH)
                                    && leftBottom != null && leftBottom.isToken(Token.PATH)) {
                                // Objekt nach links unten bewegen
                                current.bufferMoveTo(leftBottom);
                                leftBottom.bufferSetProperty(Property.FALLING);
                            }
                        }
                    }
                }
                // Spielfigur/Gegner durch herabfallende Gegenstände erschlagen
                if (current.hasProperty(Property.FALLING)) {
                    if (bottom.isToken(Token.ME, Token.SWAPLING, Token.XLING, Token.BLOCKLING)) {
                        bottom.bufferBam(!bottom.isToken(Token.BLOCKLING));
                    }
                }

                /* Gegnerbewegungen */
                if (current.isToken(Token.SWAPLING, Token.XLING, Token.BLOCKLING)) {
                    // Derzeitige Ausrichtung des Gegeners, ohne Ausrichtung keine Bewegung
                    int curDirInt = current.getPropertyValue(Property.DIRECTION);
                    if (curDirInt == 1 || curDirInt == 2 || curDirInt == 3 || curDirInt == 4) {
                        FieldDirection curDirection = FieldDirection.getFromDirection(curDirInt);

                        // Felder aus Sicht des Gegners
                        Feld forward        = current.getNeighbourRelative(curDirection, FieldDirection.TOP);
                        Feld rightSide      = current.getNeighbourRelative(curDirection, FieldDirection.RIGHT);
                        Feld leftSide       = current.getNeighbourRelative(curDirection, FieldDirection.LEFT);
                        Feld backwards      = current.getNeighbourRelative(curDirection, FieldDirection.BOTTOM);
                        Feld backwardsRight = current.getNeighbourRelative(curDirection, FieldDirection.RIGHTBOTTOM);
                        Feld backwardsLeft  = current.getNeighbourRelative(curDirection, FieldDirection.LEFTBOTTOM);

                        // horizontale / vertikale Bewegungen für Swapling
                        if (current.isToken(Token.SWAPLING)) {
                            if (forward != null && forward.isToken(Token.PATH, Token.ME)) {
                                // move forward
                                current.bufferMoveEnemyTo(FieldDirection.TOP);
                            } else if (backwards != null && backwards.isToken(Token.PATH, Token.ME)) {
                                // umkehren
                                current.bufferMoveEnemyTo(FieldDirection.BOTTOM);
                            } else {
                                // eingeklemmt
                            }
                        // Rechte-Hand-Regel für Xling
                        } else if (current.isToken(Token.XLING)) {
                            // Rechts frei und hinten rechts nicht frei
                            if (rightSide != null && rightSide.isToken(Token.PATH, Token.ME) &&
                                    (backwardsRight == null || !backwardsRight.isToken(Token.PATH, Token.ME))) {
                                // nach rechts gehen
                                current.bufferMoveEnemyTo(FieldDirection.RIGHT);
                            // Sonst wenn voraus frei
                            } else if (forward != null && forward.isToken(Token.PATH, Token.ME)) {
                                // geradeaus gehen
                                current.bufferMoveEnemyTo(FieldDirection.TOP);
                            // sonst wenn links frei
                            } else if (leftSide != null && leftSide.isToken(Token.PATH, Token.ME)) {
                                // nach links gehen
                                current.bufferMoveEnemyTo(FieldDirection.LEFT);
                            // sonst wenn hinten frei
                            } else if (backwards != null && backwards.isToken(Token.PATH, Token.ME)) {
                                // umkehren
                                current.bufferMoveEnemyTo(FieldDirection.BOTTOM);
                            } else {
                                // eingeschlossen
                            }
                        // Linke-Hand-Regel für Blockling
                        } else if (current.isToken(Token.BLOCKLING)) {
                            // Links frei und hinten links nicht frei
                            if (leftSide != null && leftSide.isToken(Token.PATH, Token.ME) &&
                                    (backwardsLeft == null || !backwardsLeft.isToken(Token.PATH, Token.ME))) {
                                // nach links gehen
                                current.bufferMoveEnemyTo(FieldDirection.LEFT);
                            // Sonst wenn voraus frei
                            } else if (forward != null && forward.isToken(Token.PATH, Token.ME)) {
                                // geradeaus gehen
                                current.bufferMoveEnemyTo(FieldDirection.TOP);
                            // sonst wenn rechts frei
                            } else if (rightSide != null && rightSide.isToken(Token.PATH, Token.ME)) {
                                // nach rechts gehen
                                current.bufferMoveEnemyTo(FieldDirection.RIGHT);
                            // sonst wenn hinten frei
                            } else if (backwards != null && backwards.isToken(Token.PATH, Token.ME)) {
                                // umkehren
                                current.bufferMoveEnemyTo(FieldDirection.BOTTOM);
                            } else {
                                // eingeschlossen
                            }
                        }
                    }
                }

                /* Schleim */
                if (current.isToken(Token.SLIME)) {
                    // Zufällige Ausbreitung
                    for (Feld nb : current.getNeighboursDirect()) {
                        if (Math.random() <= 0.03) {
                            if (nb.isToken(Token.PATH, Token.MUD)) {
                                nb.bufferSetToken(Token.SLIME);
                            } else if (nb.isToken(Token.SWAPLING, Token.XLING)) {
                                nb.bufferBam(true);
                            } else if (nb.isToken(Token.BLOCKLING)) {
                                nb.bufferBam(false);
                            }
                        }
                    }

                    // Eingeschlossene Schleimfelder umwandeln
                    List<Feld> slimeFields = new ArrayList<>();
                    if (current.isInSlimeArea(slimeFields)) {
                        for (Feld slime : slimeFields) {
                            slime.bufferSetToken(this.isSlimeMaximumReached() ? Token.STONE : Token.GEM);
                        }
                    }
                }

                /* Explosionen */
                if (current.isToken(Token.EXPLOSION)) {
                    current.bufferSetToken(Token.PATH);
                }
                if (current.hasProperty(Property.BAM)) {
                    current.bufferBam(false);
                }
                if (current.hasProperty(Property.BAMRICH)) {
                    current.bufferBam(true);
                }
            }
        }

        // apply main rules
        this.applyBufferedChanges();
    }

    /**
     * reset / set properties where necessary
     */
    public void resetProperties() {
        for (Feld[] row : this.getMap()) {
            for (Feld field : row) {
                field.resetProperty(Property.MOVED);
                field.resetProperty(Property.FALLING);
                field.resetProperty(Property.BAM);
                field.resetProperty(Property.BAMRICH);

                // LOOSE
                if (field.isToken(Token.STONE, Token.GEM)) {
                    field.setProperty(Property.LOOSE);
                } else {
                    field.resetProperty(Property.LOOSE);
                }

                // SLIPPERY
                if (field.isToken(Token.STONE, Token.GEM, Token.BRICKS)) {
                    field.setProperty(Property.SLIPPERY);
                } else {
                    field.resetProperty(Property.SLIPPERY);
                }

                // PUSHABLE
                if (field.isToken(Token.STONE)) {
                    field.setProperty(Property.PUSHABLE);
                } else {
                    field.resetProperty(Property.PUSHABLE);
                }
            }
        }
    }

    /**
     * increase ticks
     */
    public void tick() {
        Integer ticks = this.getPropertyValue(Property.TICKS);
        this.setPropertyValue(Property.TICKS, ticks == null ? 1 : ticks+1);
    }
}