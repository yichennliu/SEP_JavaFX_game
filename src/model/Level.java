package model;

import model.enums.Property;

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
    }

    public String getName() {
        return name;
    }

    public Feld[][] getMap() {
        return map;
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

    public Map<Property, Integer> getProperties() {
        return properties;
    }
}
