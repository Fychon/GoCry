package gocry;


import java.awt.geom.Point2D;


/**
 * 
 * @author justus
 */
public class Question {

    // Die Fragestellung
    String question;

    // Position der Fragestellung
    Point2D position;

// Setter
    public void setQuestion(String question) {
        this.question = question;
    }

    public void setPosition(double x, double y) {
        this.position.setLocation(x, y);
    }

// Getter
    public String getQuestion(){
        return this.question;
    }
    
    public Point2D getPosition() {
        return this.position;
    }
}
