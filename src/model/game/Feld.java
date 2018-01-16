package model.game;

import model.enums.*;

import java.util.*;

public class Feld {
    private Token token;
    private Map<Property, Integer> properties;
    private int row;
    private int column;
    private Level level;

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

    public Feld getNeighbour(Neighbour nb) {
        return this.getLevel().getFeld(this.getRow()+nb.getRowOffset(), this.getColumn()+nb.getColumnOffset());
    }

    public Feld getNeighbour(InputDirection input) {
        return this.getNeighbour(input.getNeighbour());
    }

    /**
     * @return all neighbours, including diagonal ones
     */
    public Collection<Feld> getNeighbours() {
        Collection<Feld> neighbours = new ArrayList<>();
        for (Neighbour nb : Neighbour.values()) {
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
        for (Neighbour nb : Neighbour.values()) {
            if (nb == Neighbour.TOP || nb == Neighbour.RIGHT || nb == Neighbour.BOTTOM || nb == Neighbour.LEFT) {
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
     * Buffer a 3*3 explosion
     *
     * @param rich true to generate GEMs, otherwise EXPLOSIONs
     */
    public void bufferBam(boolean rich) {
        Collection<Feld> fields = this.getNeighbours();
        fields.add(this);
        for (Feld f : fields) {
            if (f.getToken() != Token.EXIT && f.getToken() != Token.WALL) {
                f.bufferSetToken(rich ? Token.GEM : Token.EXPLOSION);
            }
        }
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

    public enum Move {FORWARD, TORIGHT, BACKWARD, TOLEFT}
    /** buffer an enemy move */
    public void bufferMoveEnemyTo(Feld goal, int currentDirection, Move to) {
        if (to == null || currentDirection < 1 || currentDirection > 4)
            throw new IllegalArgumentException("'direction' must be between 1 and 4, 'to' must not be null");
        int newDirection = 0;
        if (to == Move.FORWARD)       newDirection = currentDirection;
        else if (to == Move.TORIGHT)  newDirection = currentDirection == 4 ? 1 : currentDirection+1;
        else if (to == Move.BACKWARD) newDirection = currentDirection <= 2 ? currentDirection+2 : currentDirection-2;
        else if (to == Move.TOLEFT)   newDirection = currentDirection == 1 ? 4 : currentDirection-1;

        this.bufferMoveTo(goal);
        goal.bufferSetPropertyValue(Property.DIRECTION, newDirection);
        this.bufferSetPropertyValue(Property.DIRECTION, 0);
    }
}
