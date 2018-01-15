package model;

public class Medaille {

    public enum Color {UNSICHTBAR, BRONZE, SILBER, GOLD};
    private String levelname;
    private int gems, time;
    private Color color;

   public Medaille(Level level, String levelname, int gems, int time, int[] timeArray, int[] gemsArray){
       this.levelname = levelname;
       this.gems = gems;
       this.time = time;
       calculateColor(timeArray, gemsArray);
   }

   public String getLevelname(){
       return this.levelname;
   }

   public Color getColor(){
       return this.color;
   }

   public double getRatio(){
       return this.gems/this.time;
   }

   public void calculateColor (int[] timeArray, int[] gemsArray) {
      Color[] colors = new Color[]{Color.BRONZE,Color.SILBER,Color.GOLD};
       for(int i = 2; i >= 0; i--){
          if(this.time<=timeArray[i]){
              if(this.gems>=gemsArray[i]){
                  this.color = colors[i];
                  return;
              }
          }
      }
      this.color = Color.UNSICHTBAR;

   }
}
