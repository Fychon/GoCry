package gocry;

/*
 * Führt Frage- und Jump'n Run Level zusammen um sie über die Layerebene im Welchsel steuern zu können
 * speichert Sounddateien für Sieg, Niederlage und generelle Hintergrundsounds
 */

/**
 *
 * @author justus
 */
public class Layer {
    
    // ID des Layers, sprich individuelle nummer für sowohl level als auch question
    int layerID;
    
    //Fremdschlüssel zum Ansprechen des Levels
    int level_id;
    
    //Fremdschlüssel, der auf sich selbst verweist
    int nextLayer;
    
    // Pfad zur Sounddatei für Todesfall
    private String deathSound;
    
    // Pfad zur Sounddatei für Siegesfall
    private String winSound;
    
    // Pfad zur Sounddatei für Hintergrundmusik
    private String backGroundSound;

    Layer(int layerID, String deathSound, String winSound, String backgroundSound, int level_id, int nextLayer) {
        this.layerID = layerID;
        this.deathSound = deathSound;
        this.winSound = winSound;
        this.backGroundSound = backgroundSound;
        this.level_id = level_id;
        this.nextLayer = nextLayer;
    }
    
    
// Setter
    public void setLayerID(int layerID){
        this.layerID = layerID;
    }
    
    public void setDeathSound(String deathSound){
        this.deathSound = deathSound;
    }
    
    public void setWinSound(String winSound){
        this.winSound = winSound;
    }
    
    public void setBackGroundSound(String backGroundSound){
        this.backGroundSound = backGroundSound;
    }
    
// Getter
    
    public int getLayerID(){
        return this.layerID;
    }
    
    public String getDeathSound(){
        return this.deathSound;
    }
    
    public String getWinSound(){
        return this.winSound;
    }
    
    public String getBackGroundSound(){
        return this.backGroundSound;
    }
}

