package model;

import model.enums.Property;
import model.enums.Token;

import java.util.*;

public class Level {
    private String name;
    /** Aufbau von oben links: map[rowNum][colNum] */
    private Feld[][] map;
    /** laut Doku genau 3 Einträge */
    private int[] gems;
    /** laut Doku genau 3 Einträge */
    private int[] ticks;
    private List<Rule> pre;
    private List<Rule> post;
    /** optional */
    private Integer maxslime;
    /** globale properties */
    private Map<Property, Integer> properties;


    public Level(String name, Feld[][] map, int[] gems, int[] ticks, List<Rule> pre, List<Rule> post, Integer maxslime) {
        this.name = name;
        this.map = map;
        this.gems = gems;
        this.ticks = ticks;
        this.pre = pre;
        this.post = post;
        this.maxslime = maxslime;
        setNeighbours();
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

    public int getWidth() {
        return this.map[0].length;
    }

    public int getHeight() {
        return this.map.length;
    }

    public int[] getGems() {
        return gems;
    }

    public int[] getTicks() {
        return ticks;
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


    private void setNeighbours() {
        int width = map[0].length;
        int height = map.length;

        for(int row = 0; row < this.getHeight();  row++){
            for (int column = 0; column < this.getWidth(); column++){
                Feld feld = getFeld(row, column);
                feld.setNeighbour(Feld.Neighbour.LEFT,       getFeld(row, column-1));
                feld.setNeighbour(Feld.Neighbour.LEFTTOP,    getFeld(row-1, column-1));
                feld.setNeighbour(Feld.Neighbour.LEFTBOTTOM, getFeld(row+1, column-1));
                feld.setNeighbour(Feld.Neighbour.RIGHT,      getFeld(row, column+1));
                feld.setNeighbour(Feld.Neighbour.RIGHTTOP,   getFeld(row-1, column+1));
                feld.setNeighbour(Feld.Neighbour.RIGHTBOTTOM,getFeld(row+1, column+1));
                feld.setNeighbour(Feld.Neighbour.TOP,        getFeld(row-1, column));
                feld.setNeighbour(Feld.Neighbour.BOTTOM,     getFeld(row+1, column));

                // also set back link to level
                feld.setLevel(this);
            }
        }
    }
}