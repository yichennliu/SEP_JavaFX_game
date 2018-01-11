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
        setNeighbours(this.map);

    }

    public String getName() {
        return name;
    }

    public Feld [][]getMap() { return map; }

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

    public Map<Property, Integer> getProperties() {
        return properties;
    }


    public static void setNeighbours(Feld[][] map) {

        int width = map[0].length;
        int height = map.length;

        for(int row = 0; row < height;  row++){
            for (int column = 0; column < width; column++){
                System.out.println("row: " + row + " column: " + column);
                Feld feld = map[row][column];
                if (column>0) feld.setNeighbour(Feld.Neighbour.LEFT, map[row][column-1]); // LEFT
                if (column>0 && row>0) feld.setNeighbour(Feld.Neighbour.LEFTTOP, map[row-1][column-1]); // LEFTTOP
                if (column>0 && row<height-1) feld.setNeighbour(Feld.Neighbour.LEFTBOTTOM, map[row+1][column-1]); // LEFTBOTTOM
                if (column<width-1) feld.setNeighbour(Feld.Neighbour.RIGHT, map[row][column+1]); // RIGHT
                if (column<width-1 && row>0) feld.setNeighbour(Feld.Neighbour.RIGHTTOP, map[row-1][column+1]); // RIGHTTOP
                if (column<width-1 && row<height-1) feld.setNeighbour(Feld.Neighbour.RIGHTBOTTOM, map[row+1][column+1]); // RIGHTBOTTOM
                if (row>0) feld.setNeighbour(Feld.Neighbour.TOP, map[row-1][column]); // TOP
                if (row<height-1) feld.setNeighbour(Feld.Neighbour.BOTTOM, map[row+1][column]); // BOTTOM
            }
        }
    }
}