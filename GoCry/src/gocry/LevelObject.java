package gocry;

import java.awt.Dimension;

/**
 * Modelklasse für die Erstellung aller LevelObjekte aus der Datenbank.
 * Objekte werden aus der Datenbank anhand LevelID geladen.
 * Position und Größe werden mittels der Framedimensionen berechnet und verändert.
 * Die Objekte werden für die Ansicht in LevelView wie auch für die Collsisions in LEvelController verwendet.
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

    /**
     * Konstruktor für levelObjekte. Ein Objekt braucht eine Position im Array und einen 
     * zugehörigen Status (Neutral/Kill/Win). Alle Objekte werden mittels Informationen aus der Datenbank ersteltl.
     * 
     * @param positionX
     * @param positionY
     * @param collision
     * @param visibility
     * @param status_id
     * @param texture_id 
     */
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

    /**
     * Anpassung der Breite und Hööge eines Levelblockes.
     * Alle Objekte sollen Quadratisch sein. Die Breite wird durch die gesamte Framebreite und der Anzahl der Blöcke in "einer Zeile"
     * Alle Blöcke auf der X-Achse zusammen, bilden die gesamte Framebreite
     * @param size 
     */
    void calcRealSize(Dimension size) {
        this.width = size.width / 32;
        this.height = width;
    }
    /**
     * Berechnung der realen Position eines LevelObjektes im Frame.
     * Die Position wird mit der Breite eines Objektes verrechnet (e.g. block(x:4;y:0) -> x; 4*40px = 160px
     * Die Position auf der Y Achse muss invertiert werden, da der Nullpunkt oben ist, und Faktor 1 aus der DB-Position hinzugerechnet
     * (e.g. ist: x:4;y:0 soll: x:160;y:680) y = 720 - 40 * (y(0)+1)
     * @param size 
     */
    void calcRealLocation(Dimension size) {
        positionx *= getWidth();
        positiony = size.height - getHeight() * (positiony+1);
    }


    /**
     * Rückgabe der Position des Blockes auf der x Achse
     * @return 
     */
    public int getPositionX() {
        return this.positionx;
    }
    /**
     * Rückgabe der Position des Blockes auf der Y Achse
     * @return 
     */
    public int getPositionY() {
        return this.positiony;
    }
    /**
     * Rückgabe der Höhe des Blockes
     * @return 
     */
    public int getHeight() {
        return this.height;
    }
    /**
     * Rückgabe der Breite des Blockes
     * @return 
     */
    public int getWidth() {
        return this.width;
    }
    /**
     * Rückgabe des Status des Blockes
     * @return 
     */
    public objectStatus getStatus() {
        return this.status;
    }
    /**
     * Rückgabe der TextureID des Blockes
     * @return 
     */
    public int getTextureId(){
        return this.textureid;
    }

}
