package model.game;

import javafx.scene.control.Label;
import javafx.util.Pair;
import model.enums.*;

import java.util.*;
import java.util.List;

public class Level {
    private String name;
    /** Aufbau von oben links: map[rowNum][colNum] */
    private Feld[][] map;
    /** laut Doku genau 3 Einträge */
    private int[] difficult;
    /** laut Doku genau 3 Einträge */
    private int[] gemGoals;
    /** aut Doku genau 3 Einträge */
    private int[] tickGoals;
    private List<Rule> pre;
    private List<Rule> post;
    /** optional */
    private Integer maxslime;
    /** globale properties: GEMS, TICKS, X, Y, Z */
    private Map<Property, Integer> properties;
    /** Path to this level */
    private String jsonPath;
    /** Derzeitige Anzahl der Felder mit Schleim */
    public int slimeCount;
    /** vorbereitete Änderungen */
    private Map<Feld, Token> tokensToChange = new HashMap<>();
    /** vorbereitete Änderungen */
    private Map<Feld, Map<Property, Integer>> propertiesToChange = new HashMap<>();
    /** buffer for control keys */
    private InputDirection inputDirection = null;
    /** whether the game is ongoing, lost, or won */
    private WinningStatus winningStatus = WinningStatus.PLAYING;
    private int difficulty;


    public Level(String name, Feld[][] map, int[] gemGoals, int[] tickGoals, List<Rule> pre, List<Rule> post,
                 Integer maxslime, Map<Property, Integer> globalProperties, int difficulty, String jsonPath) {
        this.name = name;
        this.map = map;
        this.gemGoals = gemGoals;
        this.tickGoals = tickGoals;
        this.pre = pre;
        this.post = post;
        this.maxslime = maxslime;
        this.properties = globalProperties;
        this.jsonPath = jsonPath;
        this.difficulty = difficulty;
        setNeighbours();
    }

    /*sets level property on all fields*/
    private void setNeighbours() {
        for (int row = 0; row < this.getHeight(); row++) {
            for (int column = 0; column < this.getWidth(); column++) {
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


    /**
     * @return Feld, or null
     */
    public Feld getFeld(int row, int col) {
        if (row >= 0 && row < this.getHeight() && col >= 0 && col < this.getWidth()) {
            return this.map[row][col];
        } else {
            return null;
        }
    }

    public Pair<Integer, Integer> getRemainingGoldTicksGems() {
        Integer remainingTicks = this.getTickGoals()[2] - this.getPropertyValue(Property.TICKS);
        Integer remainingGems = this.getGemGoals()[2] - this.getPropertyValue(Property.GEMS);
        return new Pair<Integer, Integer>(remainingTicks, remainingGems);
    }

    public Pair<Integer, Integer> getRemainingSilverTicksGems() {
        Integer remainingTicks = this.getTickGoals()[1] - this.getPropertyValue(Property.TICKS);
        Integer remainingGems = this.getGemGoals()[1] - this.getPropertyValue(Property.GEMS);
        return new Pair<Integer, Integer>(remainingTicks, remainingGems);
    }

    public Pair<Integer, Integer> getRemainingBronzeTicksGems() {
        Integer remainingTicks = this.getTickGoals()[0] - this.getPropertyValue(Property.TICKS);
        Integer remainingGems = this.getGemGoals()[0] - this.getPropertyValue(Property.GEMS);
        return new Pair<Integer, Integer>(remainingTicks, remainingGems);
    }

    /**
     * @return current medal, or null
     */
    public Medal getCurrentMedal() {
        if (this.getPropertyValue(Property.GEMS) >= this.getGemGoals()[2]
                && this.getPropertyValue(Property.TICKS) <= this.getTickGoals()[2]) {
            return Medal.GOLD;
        } else if (this.getPropertyValue(Property.GEMS) >= this.getGemGoals()[1]
                && this.getPropertyValue(Property.TICKS) <= this.getTickGoals()[1]) {
            return Medal.SILVER;
        } else if (this.getPropertyValue(Property.GEMS) >= this.getGemGoals()[0]
                && this.getPropertyValue(Property.TICKS) <= this.getTickGoals()[0]) {
            return Medal.BRONZE;
        } else {
            return null;
        }
    }


    public Feld[][] getMap() {
        return this.map;
    }

    public int getWidth() {
        return this.map[0].length;
    }

    public int getHeight() {
        return this.map.length;
    }

    public int[] getDifficult(){return difficult;}

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

    public int getDifficulty() {
        return this.difficulty;
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

    /**
     * get path to json soruce for this level
     */
    public String getJsonPath() {
        return this.jsonPath;
    }

    public WinningStatus getWinningStatus() {
        return this.winningStatus;
    }

    public void setWinningStatus(WinningStatus winningStatus) {
        this.winningStatus = winningStatus;
    }

    /**
     * @return existing global properties (!= 0)
     */
    public Map<Property, Integer> getProperties() {
        return this.properties;
    }

    /**
     * @return global value associated to property
     */
    public Integer getPropertyValue(Property property) {
        return this.properties.get(property) == null
                ? 0
                : this.properties.get(property);
    }

    /**
     * @param property Global property
     * @param value    value
     */
    public void setPropertyValue(Property property, int value) {
        if (property.isGlobal()) {
            this.properties.put(property, value);
        } else {
            throw new IllegalArgumentException("Property must be global");
        }
    }

    /**
     * Buffer a token change on a field, but do not apply it yet
     */
    public void bufferChangeToken(Feld field, Token newToken) {
        this.tokensToChange.put(field, newToken);
    }

    /**
     * Buffer a property change on a field, but do not apply it yet
     */
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
                this.bufferChangeProperty(field, property, intVal + by);
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
        /* Explosionen aus vorigem Tick löschen */
        for (Feld[] row : this.getMap()) {
            for (Feld current : row) {
                if (current.isToken(Token.EXPLOSION)) {
                    current.setToken(Token.PATH);
                }
            }
        }

        /* andere Regeln */
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
                if (current.isToken(Token.ME) && !current.hasProperty(Property.MOVED)) {
                    // direction setzen für view
                    if (this.getInputDirection() != null) {
                        current.setPropertyValue(Property.DIRECTION, this.getInputDirection().getFieldDirection().getDirection());
                    }

                    // keine Eingabe: nichts tun
                    if (this.getInputDirection() == null) {
                    }

                    // nur graben
                    else if (this.getInputDirection().isDigOnly()) {
                        Feld toDig = current.getNeighbour(this.getInputDirection());
                        if (toDig != null && toDig.isToken(Token.MUD)) toDig.setToken(Token.PATH);
                        // nicht-fallende Edelsteine einsammeln
                        if (toDig != null && toDig.isToken(Token.GEM) && !toDig.hasProperty(Property.FALLING)) {
                            toDig.setToken(Token.PATH);
                            current.increasePropertyValue(Property.GEMS, 1);
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
                                    current.increasePropertyValue(Property.GEMS, 1);
                                }
                                // bewegen (+graben/einsammeln)
                                current.moveTo(next);
                            }

                            // in Ausgang gehen
                            if (next.isToken(Token.EXIT)) {
                                for (int goalNo = 2; goalNo >= 0; goalNo--) {
                                    if (this.getPropertyValue(Property.GEMS) >= this.getGemGoals()[goalNo] &&
                                            this.getPropertyValue(Property.TICKS) <= this.getTickGoals()[goalNo]) {
                                        current.moveTo(next);
                                        this.setWinningStatus(WinningStatus.WON);
                                    }
                                }
                            }

                            // schieben
                            if ((this.getInputDirection() == InputDirection.GOLEFT
                                    || this.getInputDirection() == InputDirection.GORIGHT)
                                    && next.hasProperty(Property.PUSHABLE)) {

                                Feld behindNext = next.getNeighbour(this.getInputDirection());
                                if (behindNext != null && behindNext.isToken(Token.PATH)) {
                                    // Objekt auf freies Feld verschieben
                                    next.moveTo(behindNext);
                                    // Spielfigur auf Platz von Objekt bewegen
                                    current.moveTo(next);
                                }
                            }
                        }
                    }
                }


                /* Gravitation */

                // Lose Gegenstände fallen lassen
                if (current.hasProperty(Property.LOOSE) &&
                        (!current.hasProperty(Property.MOVED) || current.hasProperty(Property.FALLING))) {
                    // bottom muss frei sein
                    if (bottom != null && bottom.isToken(Token.PATH) && !bottom.hasProperty(Property.MOVED)) {
                        // Objekt nach unten bewegen
                        current.moveTo(bottom);
                        bottom.setProperty(Property.FALLING);
                    } else {
                        // Falls bottom nicht frei ist, dafür aber SLIPPERY
                        if (bottom != null && bottom.hasProperty(Property.SLIPPERY)) {
                            // wenn right und rightBottom frei sind
                            if (right != null && right.isToken(Token.PATH) && !right.hasProperty(Property.MOVED)
                                    && rightBottom != null && rightBottom.isToken(Token.PATH) && !rightBottom.hasProperty(Property.MOVED)) {
                                // Objekt nach rechts unten bewegen
                                current.moveTo(rightBottom);
                                rightBottom.setProperty(Property.FALLING);
                            }
                            // falls nicht, und left und bottomleft sind frei
                            else if (left != null && left.isToken(Token.PATH) && !left.hasProperty(Property.MOVED)
                                    && leftBottom != null && leftBottom.isToken(Token.PATH) && !leftBottom.hasProperty(Property.MOVED)) {
                                // Objekt nach links unten bewegen
                                current.moveTo(leftBottom);
                                leftBottom.setProperty(Property.FALLING);
                            }
                        }
                    }
                }
                // Spielfigur/Gegner durch herabfallende Gegenstände erschlagen
                if (current.hasProperty(Property.FALLING)) {
                    if (bottom.isToken(Token.ME, Token.SWAPLING, Token.XLING, Token.BLOCKLING)) {
                        bottom.setProperty(bottom.isToken(Token.BLOCKLING) ? Property.BAM : Property.BAMRICH);
                    }
                }

                /* Gegnerbewegungen */
                if (current.isToken(Token.SWAPLING, Token.XLING, Token.BLOCKLING) && !current.hasProperty(Property.MOVED)
                        && !current.hasProperty(Property.BAM) && !current.hasProperty(Property.BAMRICH)) {
                    // Derzeitige Ausrichtung des Gegeners, ohne Ausrichtung keine Bewegung
                    int curDirInt = current.getPropertyValue(Property.DIRECTION);
                    if (curDirInt == 1 || curDirInt == 2 || curDirInt == 3 || curDirInt == 4) {
                        FieldDirection curDirection = FieldDirection.getFromDirection(curDirInt);

                        // Felder aus Sicht des Gegners
                        Feld forward = current.getNeighbourRelative(curDirection, FieldDirection.TOP);
                        Feld rightSide = current.getNeighbourRelative(curDirection, FieldDirection.RIGHT);
                        Feld leftSide = current.getNeighbourRelative(curDirection, FieldDirection.LEFT);
                        Feld backwards = current.getNeighbourRelative(curDirection, FieldDirection.BOTTOM);
                        Feld backwardsRight = current.getNeighbourRelative(curDirection, FieldDirection.RIGHTBOTTOM);
                        Feld backwardsLeft = current.getNeighbourRelative(curDirection, FieldDirection.LEFTBOTTOM);

                        // horizontale / vertikale Bewegungen für Swapling
                        if (current.isToken(Token.SWAPLING)) {
                            if (forward != null && forward.isFree(true)) {
                                // move forward
                                current.moveEnemyTo(FieldDirection.TOP);
                            } else if (backwards != null && backwards.isFree(true)) {
                                // umkehren
                                current.moveEnemyTo(FieldDirection.BOTTOM);
                            } else {
                                // eingeklemmt
                            }
                            // Rechte-Hand-Regel für Xling
                        } else if (current.isToken(Token.XLING)) {
                            // Rechts frei und hinten rechts nicht frei
                            if (rightSide != null && rightSide.isFree(true) &&
                                    (backwardsRight == null || !backwardsRight.isFree(true))) {
                                // nach rechts gehen
                                current.moveEnemyTo(FieldDirection.RIGHT);
                                // Sonst wenn voraus frei
                            } else if (forward != null && forward.isFree(true)) {
                                // geradeaus gehen
                                current.moveEnemyTo(FieldDirection.TOP);
                                // sonst wenn links frei
                            } else if (leftSide != null && leftSide.isFree(true)) {
                                // nach links gehen
                                current.moveEnemyTo(FieldDirection.LEFT);
                                // sonst wenn hinten frei
                            } else if (backwards != null && backwards.isFree(true)) {
                                // umkehren
                                current.moveEnemyTo(FieldDirection.BOTTOM);
                                // sonst wenn rechts frei
                            } else if (rightSide != null && rightSide.isFree(true)) {
                                // nach rechts gehen
                                current.moveEnemyTo(FieldDirection.RIGHT);
                            } else {
                                // eingeschlossen
                            }
                            // Linke-Hand-Regel für Blockling
                        } else if (current.isToken(Token.BLOCKLING)) {
                            // Links frei und hinten links nicht frei
                            if (leftSide != null && leftSide.isFree(true) &&
                                    (backwardsLeft == null || !backwardsLeft.isFree(true))) {
                                // nach links gehen
                                current.moveEnemyTo(FieldDirection.LEFT);
                                // Sonst wenn voraus frei
                            } else if (forward != null && forward.isFree(true)) {
                                // geradeaus gehen
                                current.moveEnemyTo(FieldDirection.TOP);
                                // sonst wenn rechts frei
                            } else if (rightSide != null && rightSide.isFree(true)) {
                                // nach rechts gehen
                                current.moveEnemyTo(FieldDirection.RIGHT);
                                // sonst wenn hinten frei
                            } else if (backwards != null && backwards.isFree(true)) {
                                // umkehren
                                current.moveEnemyTo(FieldDirection.BOTTOM);
                                // sonst wenn links frei
                            } else if (leftSide != null && leftSide.isFree(true)) {
                                // nach links gehen
                                current.moveEnemyTo(FieldDirection.LEFT);
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
                                nb.setProperty(Property.BAMRICH);
                            } else if (nb.isToken(Token.BLOCKLING)) {
                                nb.setProperty(Property.BAM);
                            }
                        }
                    }

                    // Eingeschlossene Schleimfelder umwandeln
                    List<Feld> slimeFields = new ArrayList<>();
                    if (current.isInSlimeArea(slimeFields)) {
                        for (Feld slime : slimeFields) {
                            slime.setToken(this.isSlimeMaximumReached() ? Token.STONE : Token.GEM);
                        }
                    }
                }

            }
        }

        /* Explosionen ausführen */
        for (Feld[] row : this.getMap()) {
            for (Feld current : row) {
                if (current.hasProperty(Property.BAM)) {
                    current.bam(false);
                }
                if (current.hasProperty(Property.BAMRICH)) {
                    current.bam(true);
                }
            }
        }

        // apply buffer
        this.applyBufferedChanges();
    }

    public void execPreRules() {
        for (Rule rule : pre) {
            rule.execute(this.map, this.inputDirection);
        }
    }

    public void execPostRules() {
        for (Rule rule : post) {
            rule.execute(this.map, this.inputDirection);
        }
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

                // reset movement origins / destinations
                field.setCurrentTokenCameFrom(null);
                field.setLastTokenWentTo(null);
            }
        }
    }

    /**
     * increase ticks
     */
    public void tick() {
        Integer ticks = this.getPropertyValue(Property.TICKS);
        this.setPropertyValue(Property.TICKS, ticks == null ? 1 : ticks + 1);

        //  lose if time ran out
        if (this.getPropertyValue(Property.TICKS) > this.getTickGoals()[0]) {
            this.setWinningStatus(WinningStatus.LOST);
        }
    }

    /**
     * find ME
     *
     * @return Field with ME, or null if ME died
     */
    public Feld whereAmI() {
        for (Feld[] row : this.getMap()) {
            for (Feld feld : row) {
                if (feld.isToken(Token.ME)) {
                    return feld;
                }
            }
        }
        return null;
    }

    /**
     * sets the game status to lost of ME is missing
     */
    public void checkLosing() {
        if (this.whereAmI() == null) {
            this.setWinningStatus(WinningStatus.LOST);
        }

    }

    /**
     * ATTENTION: returns a fake-clone (only map and properties are really cloned, rules,goals, jsonPath and other unchangable vars are the same)
     */
    public Level clone() {
        Feld[][] copyOfMap = copyMap();
        Map<Property, Integer> globalPropsClone = copyProps(this.properties);
        return new Level(new String(name),copyOfMap,gemGoals,tickGoals,pre,post,maxslime, globalPropsClone, difficulty, jsonPath);
    }

    /**
     * copies property Maps
     */
    private Map<Property, Integer> copyProps(Map<Property, Integer> mapToCopy) {
        Map<Property, Integer> propertyClone = new HashMap<>();
        for (Map.Entry<Property, Integer> entry : mapToCopy.entrySet()) {
            propertyClone.put(entry.getKey(), new Integer(entry.getValue()));
        }
        return propertyClone;
    }

    /**
     * copies map by copying every Feld
     */
    public Feld[][] copyMap() {
        Feld[][] copy = new Feld[map.length][map[0].length];
        for (int y = 0; y < copy.length; y++) {
            for (int x = 0; x < copy[0].length; x++) {
                copy[y][x] = map[y][x].clone();
            }
        }
        return copy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMap(Feld[][] map) {
        this.map = map;
        setNeighbours();
    }
}