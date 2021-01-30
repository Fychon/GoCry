package gocry;

/**
 * Modelklasse für einen Scoreboard eintrag.
 * Eine Liste dieser Objekte wird für die ScoreBoardView verwendet.
 */
public class ScoreboardEntry {


    // verdiente Platzierung auf dem Scoreboard
    private String gametime;
    // Name des verdienten Rangs
    private String creationdate;
    // Name des Spielers
    private String name;
        
    /**
     * Erstellung eines neuen Eintrages.
     * @param gametime
     * @param creationdate
     * @param name 
     */
    public ScoreboardEntry(String gametime, String creationdate, String name){
        this.gametime = gametime;
        this.creationdate = creationdate;
        this.name = name;
    }
    /**
     * Ausgabe des Erstellungsdatum dieses Eintrages
     * @return 
     */
    public String getCreationDate() {
        return this.creationdate;
    }
    /**
     * Ausgabe der Spielzeit dieses Eintrages
     * @return 
     */            
    public String getGameTime() {
        return this.gametime;
    }
    /**
     * Ausgabe des Spielernamens dieses Eintrages.
     * @return 
     */
    public String getName() {
        return this.name;
    }

}
