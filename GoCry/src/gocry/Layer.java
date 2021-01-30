package gocry;

/**
 * Verknüft die einzelnen Level mit dem ViewController, Question könnten optinional über den ViewController geladen werden (gestrichenes Feature)
 * Die Klasse Layer verbindet spezifische Sounds (Win, Death, Background) mit dem aktuellen Level / (Frage)
 * Alle Layer werden bei Erstellung des ViewControllers geladen.
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

    
    /**
     * Erstellung eines neuen Layereintrags. Parameter werden aus der Datenbank geladen
     * @param layerID
     * @param deathSound
     * @param winSound
     * @param backgroundSound
     * @param level_id
     * @param nextLayer 
     */
    Layer(int layerID, String deathSound, String winSound, String backgroundSound, int level_id, int nextLayer) {
        this.layerID = layerID;
        this.deathSound = deathSound;
        this.winSound = winSound;
        this.backGroundSound = backgroundSound;
        this.level_id = level_id;
        this.nextLayer = nextLayer;
    }
    
    
    /**
     * Rückgabe der aktuellen LayerID
     * @return int aktuelle LayerID
     */
    public int getLayerID(){
        return this.layerID;
    }
    /**
     * Rückgabe des Pfades zum aktuellen DeathSounds
     * @return String pathToDeathSound
     */
    public String getDeathSound(){
        return this.deathSound;
    }
    /**
     * Rückgabe des Pfades zum aktuellen WinSounds
     * @return String pathToWinSound
     */
    public String getWinSound(){
        return this.winSound;
    }
    /**
     * Rückgabe des Pfades zum aktuellen Background
     * @return String pathToBackground
     */
    public String getBackGroundSound(){
        return this.backGroundSound;
    }
}

