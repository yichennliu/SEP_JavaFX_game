package model.game;

import model.enums.*;

import java.util.*;

public class Feld {
    private Token token;
    private Map<Property, Integer> properties;
    private int row;
    private int column;
    private Level level;
    private Feld currentTokenCameFrom;
    private Feld lastTokenWentTo;

    public Feld(Token token, int column, int row) {
        this(token, new HashMap<Property, Integer>(), column, row);
    }

    public Feld(Token token, Map<Property, Integer> properties, int column, int row){
        this.token = token;
        this.properties = properties;
        this.column = column;
        this.row = row;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return this.level;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public void setToken(Token token) {
        if (this.token == Token.SLIME && token != Token.SLIME) {
            level.slimeCount--;
        } else if (this.token != Token.SLIME && token == Token.SLIME) {
            level.slimeCount++;
        }
        this.token = token;
    }

    /** buffer to set token, do not apply yet */
    public void bufferSetToken(Token token) {
        this.level.bufferChangeToken(this, token);
    }

    public Token getToken(){
        return this.token;
    }

    public boolean isToken(Token token) {
        return this.getToken() == token;
    }

    public boolean isToken(Token... tokens) {
        for (Token token : tokens)
            if (token == this.getToken()) return true;
        return false;
    }

    public Feld getNeighbour(FieldDirection nb) {
        return this.getLevel().getFeld(this.getRow()+nb.getRowOffset(), this.getColumn()+nb.getColumnOffset());
    }

    public Feld getNeighbour(InputDirection input) {
        return this.getNeighbour(input.getFieldDirection());
    }

    /**
     * @return all neighbours, including diagonal ones
     */
    public Collection<Feld> getNeighbours() {
        Collection<Feld> neighbours = new ArrayList<>();
        for (FieldDirection nb : FieldDirection.values()) {
            Feld neighbour = this.getLevel().getFeld(this.getRow()+nb.getRowOffset(),
                    this.getColumn()+nb.getColumnOffset());
            if (neighbour != null) neighbours.add(neighbour);
        }
        return neighbours;
    }

    /**
     * @return Only vertical/horizontal neighbours
     */
    public Collection<Feld> getNeighboursDirect() {
        Collection<Feld> neighbours = new ArrayList<>();
        for (FieldDirection nb : FieldDirection.values()) {
            if (nb == FieldDirection.TOP || nb == FieldDirection.RIGHT || nb == FieldDirection.BOTTOM || nb == FieldDirection.LEFT) {
                Feld neighbour = this.getLevel().getFeld(this.getRow() + nb.getRowOffset(),
                        this.getColumn() + nb.getColumnOffset());
                if (neighbour != null) neighbours.add(neighbour);
            }
        }
        return neighbours;
    }

    public String toString() {
        return this.token.name();
    }

    /** @return value associated to local or global property */
    public Integer getPropertyValue(Property property){
        if (property.isGlobal()) {
            return this.level.getPropertyValue(property);
        } else {
            return this.properties.get(property) == null
                    ? 0
                    : this.properties.get(property);
        }
    }

    /** sets local or global property */
    public void setPropertyValue(Property property, int value){
        if (property.isGlobal()) {
            this.level.setPropertyValue(property, value);
        } else {
            this.properties.put(property, value);
        }
    }

    /** buffer setting local or global property, do not apply yet */
    public void bufferSetPropertyValue(Property property, int value) {
        this.level.bufferChangeProperty(this, property, value);
    }

    /** increases value of property by the given value */
    public void increasePropertyValue(Property property, int by) {
        this.setPropertyValue(property, this.getPropertyValue(property)+by);
    }

    /** buffer to increase property by the given value, do not apply yet */
    public void bufferIncreasePropertyValue(Property property, int by) {
        this.level.bufferIncreaseProperty(this, property, by);
    }

    /**
     * @return existing non-global properties (value != 0)
     */
    public Map<Property, Integer> getProperties() {
        Map<Property, Integer> properties = new HashMap<>();
        for (Map.Entry entry : this.properties.entrySet()) {
            Property p = (Property) entry.getKey();
            Integer i = (Integer) entry.getValue();
            if (entry.getValue() != null && i != 0) {
                properties.put(p, i);
            }
        }
        return properties;
    }

    /** if property != 0 */
    public boolean hasProperty(Property property) {
        return this.getPropertyValue(property) != 0;
    }

    /** set property = 1 */
    public void setProperty(Property property) {
        this.setPropertyValue(property, 1);
    }

    /** buffer set property = 1, do not apply yet */
    public void bufferSetProperty(Property property) {
        this.bufferSetPropertyValue(property, 1);
    }

    /** set property = 0 */
    public void resetProperty(Property property) {
        this.setPropertyValue(property, 0);
    }

    /**
     * Make a 3*3 explosion
     *
     * @param rich true to generate GEMs, otherwise EXPLOSIONs
     */
    public void bam(boolean rich) {
        Collection<Feld> fields = this.getNeighbours();
        fields.add(this);
        for (Feld f : fields) {
            if (f.getToken() != Token.EXIT && f.getToken() != Token.WALL) {
                if (f.isToken(Token.ME) && this.getLevel().getWinningStatus() != WinningStatus.WON) {
                    // ME killed: loose
                    this.getLevel().setWinningStatus(WinningStatus.LOST);
                }
                f.setToken(rich ? Token.GEM : Token.EXPLOSION);
            }
        }
    }

    /**
     * Buffer a 3*3 explosion
     *
     * @param rich true to generate GEMs, otherwise EXPLOSIONs
     * @return true if ME was killed, false otherwise
     */
    public boolean bufferBam(boolean rich) {
        Collection<Feld> fields = this.getNeighbours();
        fields.add(this);
        boolean killed = false;
        for (Feld f : fields) {
            if (f.getToken() != Token.EXIT && f.getToken() != Token.WALL) {
                if (f.isToken(Token.ME)) killed = true;
                f.bufferSetToken(rich ? Token.GEM : Token.EXPLOSION);
            }
        }
        return killed;
    }

    /**
     * Check if area around this is completely slimed, save fields in given List
     */
    public boolean isInSlimeArea(List<Feld> slimeFields) {
        if (this.getToken() == Token.SLIME) {
            slimeFields.add(this);
            for (Feld nb : this.getNeighboursDirect()) {
                if (!slimeFields.contains(nb)) {
                    return nb.isInSlimeArea(slimeFields);
                }
            }
            return true;
        } else if (this.isToken(Token.PATH, Token.MUD, Token.SWAPLING, Token.XLING, Token.BLOCKLING)) {
            return false;
        } else if (slimeFields.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * make a movement:
     * Move to goal, set both to MOVED, replace current Token with PATH
     *
     * @param goal not null
     */
    public void moveTo(Feld goal) {
        // Objekt am Ziel einsetzen
        goal.setToken(this.getToken());
        goal.setProperty(Property.MOVED);

        // für ME direction verschieben
        if (this.isToken(Token.ME)) {
            goal.setPropertyValue(Property.DIRECTION, this.getPropertyValue(Property.DIRECTION));
            this.resetProperty(Property.DIRECTION);
        }

        // Platz freimachen
        this.setToken(Token.PATH);
        this.setProperty(Property.MOVED);

        // set bi-drirectional link betwenn this and goal
        goal.setCurrentTokenCameFrom(this);
        this.setLastTokenWentTo(goal);
    }

    /**
     * Buffer a movement:
     * Move to goal, set both to MOVED, replace current Token with PATH
     *
     * @param goal not null
     */
    public void bufferMoveTo(Feld goal) {
        // Objekt am Ziel einsetzen
        goal.bufferSetToken(this.getToken());
        goal.bufferSetProperty(Property.MOVED);
        // Platz freimachen
        this.bufferSetToken(Token.PATH);
        this.bufferSetProperty(Property.MOVED);
    }

    /**
     * execute an enemy move relative to its direction, check for ME collision
     */
    public void moveEnemyTo(FieldDirection to) {
        FieldDirection curDirection = FieldDirection.getFromDirection(this.getPropertyValue(Property.DIRECTION));
        if (curDirection.getDirection() == null || to.getDirection() == null)
            throw new IllegalArgumentException("'currentDir' and 'to' must not be diagonal");

        FieldDirection nbGoal = FieldDirection.getRotated(curDirection, to);
        Feld goal = this.getNeighbour(nbGoal);

        if (goal.isToken(Token.ME)) {
            // explode
            goal.setProperty(this.isToken(Token.BLOCKLING) ? Property.BAM : Property.BAMRICH);
        } else {
            this.moveTo(goal);
            goal.setPropertyValue(Property.DIRECTION, nbGoal.getDirection());
            this.setPropertyValue(Property.DIRECTION, 0);
        }
    }

    /**
     * buffer an enemy move relative to its direction, check for ME collision
     */
    public void bufferMoveEnemyTo(FieldDirection to) {
        FieldDirection curDirection = FieldDirection.getFromDirection(this.getPropertyValue(Property.DIRECTION));
        if (curDirection.getDirection() == null || to.getDirection() == null)
            throw new IllegalArgumentException("'currentDir' and 'to' must not be diagonal");

        FieldDirection nbGoal = FieldDirection.getRotated(curDirection, to);
        Feld goal = this.getNeighbour(nbGoal);

        if (goal.isToken(Token.ME)) {
            // explode
            goal.bufferSetProperty(this.isToken(Token.BLOCKLING) ? Property.BAM : Property.BAMRICH);
        } else {
            this.bufferMoveTo(goal);
            goal.bufferSetPropertyValue(Property.DIRECTION, nbGoal.getDirection());
            this.bufferSetPropertyValue(Property.DIRECTION, 0);
        }
    }

    /**
     * Get neighbour field relative to an original direction
     * E.g. original RIGHT, relative RIGHT -> BOTTOM
     */
    public Feld getNeighbourRelative(FieldDirection original, FieldDirection relative) {
        return this.getNeighbour(FieldDirection.getRotated(original, relative));
    }

    /**
     * @param allowME true if something is able to walk on ME, false otherwise
     * @return true if something can move here
     */
    public boolean isFree(boolean allowME) {
        if (allowME)
            return this.isToken(Token.ME) || (this.isToken(Token.PATH) && !this.hasProperty(Property.MOVED));
        else
            return this.isToken(Token.PATH) && !this.hasProperty(Property.MOVED);
    }

    /**
     * @return The field of which the token of the current field came from,
     * or null if nothing was moved to the current field
     */
    public Feld getCurrentTokenCameFrom() {
        return this.currentTokenCameFrom;
    }

    /**
     * @see Feld#getCurrentTokenCameFrom() Getter
     * @param origin
     */
    public void setCurrentTokenCameFrom(Feld origin) {
        this.currentTokenCameFrom = origin;
    }

    /**
     * @return The field to which the token which previously was on the current field has been moved,
     * or null if nothing was moved away from the current field
     */
    public Feld getLastTokenWentTo() {
        return this.lastTokenWentTo;
    }

    /**
     * @see Feld#getLastTokenWentTo() Getter
     * @param destination
     */
    public void setLastTokenWentTo(Feld destination) {
        this.lastTokenWentTo = destination;
    }
}
