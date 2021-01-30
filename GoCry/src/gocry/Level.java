package gocry;

import java.awt.geom.Point2D;
/**
 * Datenklasse für die Eigenschaften eines Levels.
 * Neben LevelName, SpawnLocation und ID werden die zugehörigen Modifikationen abgespeichert.
 * Objekte dieser Klasse werden mittels Informationen aus der Datenbank erstellt und als Liste bei Erstellung des
 * LevelController übergeben.
 * @author justus
 */

public class Level {
    
    // ID des Levels
    private int levelID;
    
    public double gravitation;
    public double movementspeed;
    public boolean lightsout;
    public boolean soundlock;
    public boolean invertcontrol;
    public boolean headwind;
    public boolean wtfisenabled;
    public boolean killblocksarehidden;
    public boolean killblocksareinvisible;
    public boolean flipswitchillusion;
    public boolean tinnitus;
    public boolean ghostsEnabled;
    
    private String backgroundtexture;      
    // Name des Levels
    private String levelName;
    
    // Startpunkt der Spielfigur
    private Point2D.Double levelSpawnLocation;


    /**
     * Konstrukter eines Levels, Daten werden aus Datenbank vorgegeben.
     * @param levelid
     * @param namen
     * @param spawnpointx
     * @param spawnpointy
     * @param movementspeed
     * @param backgroundtexture
     * @param gravitation
     * @param lightsout
     * @param soundlock
     * @param invertcontrol
     * @param headwind
     * @param wtfisenabled
     * @param killblocksarehidden
     * @param killblocksareinvisible
     * @param flipswitchillusion
     * @param tinnitus
     * @param ghostsEnabled 
     */
    Level(int levelid, String namen, int spawnpointx, int spawnpointy, double movementspeed, String backgroundtexture, double gravitation, boolean lightsout, boolean soundlock, boolean invertcontrol, boolean headwind, boolean wtfisenabled, boolean killblocksarehidden, boolean killblocksareinvisible, boolean flipswitchillusion, boolean tinnitus, boolean ghostsEnabled) {
        this.levelID = levelid;
        this.levelName = namen;
        this.levelSpawnLocation = new Point2D.Double(spawnpointx,spawnpointy);
        
        this.movementspeed = movementspeed;
        this.backgroundtexture = backgroundtexture;
        this.gravitation = gravitation;
        this.lightsout = lightsout;
        this.soundlock = soundlock;
        this.invertcontrol = invertcontrol;
        this.headwind = headwind;
        this.wtfisenabled = wtfisenabled;
        this.killblocksarehidden = killblocksarehidden;
        this.killblocksareinvisible = killblocksareinvisible;
        this.flipswitchillusion = flipswitchillusion;
        this.tinnitus = tinnitus;
        this.ghostsEnabled = ghostsEnabled;
    }
        
    /**
     * Rückgabe der LevelID
     * @return int levlid
     */
    public int getLevelId(){
        return this.levelID;
    }
    /**
     * Rückgabe des Levelnamens
     * @return String Levelname
     */
    public String getLevelName(){
        return this.levelName;
    }
    /**
     * Rückgabe der SpawnLocation des Levels
     * @return Point2D spawnlocation
     */
    public Point2D.Double getSpawn(){
        return this.levelSpawnLocation;
    }
    /**
     * Rückgabe des Pfades zur zugehörigen Hintergrundtextur.
     * @return String texturePath
     */
    String getBackgroundTexture() {
        return this.backgroundtexture;
    }
}


