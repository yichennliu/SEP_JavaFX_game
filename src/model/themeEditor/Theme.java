package model.themeEditor;

import model.enums.Token;

public class Theme {

    /* "Type" of Feld, means the relation of one feld to its neighbours */
    public enum FeldType {
        ZEROEDGE(false), ONEEDGE(false), TWOEDGE(false), TWOEDGE_CORNER(false), THREEEDGE(false), FOUREDGE(false), // for "static" fields
        STEP_SIDE(true),STEP_UP(true), STEP_DOWN(true), IDLE(true); // for "moving" fields

        private boolean movable;
        FeldType(boolean movable){
            this.movable = movable;
        }
        public boolean isMovable(){
            return this.movable;
        }

        public final Position[] getAllowedPositions(){
            switch(this){
                case ONEEDGE: return new Position[]{Position.LEFT,Position.RIGHT,Position.TOP,Position.BOTTOM};
                case TWOEDGE: return new Position[]{Position.HORIZONTAL, Position.VERTICAL};
                case TWOEDGE_CORNER: return new Position[]{Position.BOTTOMLEFT,Position.TOPRIGHT,Position.TOPLEFT,Position.BOTTOMRIGHT};
                case THREEEDGE: return new Position[] {Position.TOP, Position.BOTTOM, Position.LEFT, Position.RIGHT};
                case STEP_SIDE: return new Position[] {Position.RIGHT, Position.LEFT};
                default: return new Position[]{Position.DEFAULT};
            }
        }
    }

    /* "position" of feld in relation to its neighbours (dependent of FeldType) */
    public enum Position {TOP, BOTTOM, LEFT, RIGHT, TOPRIGHT,TOPLEFT,BOTTOMRIGHT, BOTTOMLEFT, HORIZONTAL, VERTICAL, DEFAULT};

    private HashMap2D<Token,FeldType,SpriteSheetHolder> spriteSheetMap;
    private String name;
    private String author;

    public Theme(){
        this.spriteSheetMap = new HashMap2D<>();
    }

    public Theme(String name) {
        this.spriteSheetMap = new HashMap2D<>();
        this.name =name;
    }

    public Theme(HashMap2D<Token,FeldType,SpriteSheetHolder> map){
        this.spriteSheetMap = map;
    }

    /* puts a thumbnails SpriteSheet in Theme */
    public void putSpriteSheet(Token t, FeldType f, Position p, SpriteSheet sheet){
        SpriteSheetHolder holder = spriteSheetMap.get(t,f);

        if(holder==null){
            holder = new SpriteSheetHolder(f);
        }
        spriteSheetMap.put(t,f,holder);
        holder.putSpriteSheet(p,sheet);
    }

    /*returns the SpriteSheet at given Token, FeldTye and Position if present, otherwise null */
    public SpriteSheet getSpriteSheet(Token t, FeldType f, Position p){
        SpriteSheetHolder holder = spriteSheetMap.get(t,f);
        if(holder!=null){
            return holder.getSpriteSheet(p);
        }
        return null;
    }

    public HashMap2D<Token,FeldType,SpriteSheetHolder> getMap (){
        return this.spriteSheetMap;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
