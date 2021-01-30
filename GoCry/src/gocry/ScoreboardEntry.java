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
     * @param gametime String zugehörige Spielzeit
     * @param creationdate String Datum der Erstellung des Scoreboardeintrages
     * @param name String Spielername
     */
    public ScoreboardEntry(String gametime, String creationdate, String name){
        this.gametime = gametime;
        this.creationdate = creationdate;
        this.name = name;
    }
    /**
     * Ausgabe des Erstellungsdatum dieses Eintrages
     * @return String Spieldatum
     */
    public String getCreationDate() {
        return this.creationdate;
    }
    /**
     * Ausgabe der Spielzeit dieses Eintrages
     * @return String Spielzeit
     */            
    public String getGameTime() {
        return this.gametime;
    }
    /**
     * Ausgabe des Spielernamens dieses Eintrages.
     * @return String Spielername
     */
    public String getName() {
        return this.name;
    }

}
