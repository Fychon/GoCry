package gocry;

import java.awt.Dimension;

/*
 * Bausteine der Level
 * 
 * 
 */
/**
 *
 * @author justus
 */
public class LevelObject{

    // Position des Objekts 
    private int positionx;

    private int positiony;

    // Höhe des Objekts
    private int height;

    // Breite des Objekts
    private int width;

    //Collision des Objekts
    private boolean collision;

    // Sichtbarkeit des Objekts
    private boolean visibility;

    // Pfad zur Textur des Objekts
    private String texture;
    
    private int textureid;
    

    /* Switch um Effekt des Objekts auf Spieler festzulegen
   * Kill - instantdeath
   * Winzone - Ziel => Level bestanden, wenn erreicht
   * Neutral - neutrales Element (bsp: normaler Untergrund)
     */
    public enum objectStatus {
        NEUTRAL,
        KILL,
        WINZONE,
    }

    // Status des Objekts
    private objectStatus status;


    LevelObject(int positionX, int positionY, boolean collision, boolean visibility, int status_id, int texture_id) {
        this.positionx = positionX;
        this.positiony = positionY;
        this.textureid = texture_id;

        this.collision = collision;
        this.visibility = visibility;
        switch (status_id) {
            case 0:
                this.status = objectStatus.NEUTRAL;
                break;
            case 1:
                this.status = objectStatus.KILL;
                break;
            case 2:
                this.status = objectStatus.WINZONE;
                break;
            default:
                this.status = objectStatus.NEUTRAL;
                break;

        }
    }

    void calcRealSize(Dimension size) {
        this.width = size.width / 32;
        this.height = width;
    }

    void calcRealLocation(Dimension size) {
        positionx *= getWidth();
        positiony = size.height - getHeight() * (positiony+1);
    }

// Setter
    public synchronized void setPositionX(int x) {
        this.positionx = x;
    }

    public synchronized void setPositionY(int y) {
        this.positionx = y;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }


    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

// Getter   
    public synchronized int getPositionX() {
        return this.positionx;
    }

    public synchronized int getPositionY() {
        return this.positiony;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public boolean getCollision() {
        return this.collision;
    }

    public boolean getVisibility() {
        return this.visibility;
    }

    public String getTexture() {
        return this.texture;
    }

    public objectStatus getStatus() {
        return this.status;
    }
    public int getTextureId(){
        return this.textureid;
    }

}
