package gocry;

/**
 * 
 * @author justus
 */
public class ScoreboardEntry {


    // verdiente Platzierung auf dem Scoreboard
    private String gametime;
    // Name des verdienten Rangs
    private String creationdate;
    // Name des Spielers
    private String name;
        
    public ScoreboardEntry(String gametime, String creationdate, String name){
        this.gametime = gametime;
        this.creationdate = creationdate;
        this.name = name;
    }
    
    public String getCreationDate() {
        return this.creationdate;
    }
                
    public String getGameTime() {
        return this.gametime;
    }

    public String getName() {
        return this.name;
    }

}
