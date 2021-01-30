package gocry;

import java.awt.geom.Point2D;

/**
 * NOT USED - Für mögliche Erweiterungen offen gelassen.
 * @author justu
 */
public class QuizObject {
    
   // Position des Objekts 
   private Point2D position;
   
   // Höhe des Objekts
   private int height;
   
   // Breite des Objekts
   private int width;
   
   //Farbe des Objekts
   private int color;
   
   // Sichtbarkeit des Objekts
   private boolean visibility;
   
   // Pfad zur Textur des AntwortObjekts
   private String answerTexture;
   
   //Pfad zur Texture des Objekts
   private String texture;
   
   

// Setter
   public void setPosition(double x, double y){
       this.position.setLocation(x, y);
   }
   
   public void setHeight(int height){
       this.height = height;
   }
           
   public void setWidth (int width){
       this.width = width;
   }
   
   public void setColor (int color){
       this.color = color;
   }
   
   public void setVisibility (boolean visibility){
       this.visibility = visibility;
   }
   
   public void setAnswerTexture (String answerTexture){
       this.answerTexture = answerTexture;
   }
   
   public void setTexture (String texture){
       this.texture = texture;
   }
           
 
   
// Getter   
   public Point2D getPosition(){
       return this.position;
   }
   
   public int getHeight(){
       return this.height;
   }
   
   public int getWidth(){
       return this.width;
   }
   
   public int getColor(){
       return this.color;
   }
   
   public boolean getVisibility(){
       return this.visibility;
   }
   
   public String getAnswerTexture(){
       return this.answerTexture;
   }
   
   public String getTexture(){
       return this.texture;
   }
   
}


