package gocry;

import java.awt.geom.Point2D;
/*
 * Verbindet Spielfeld, Spieler und Levelobjekte
 */

/**
 *
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
    
    
    public void setLevelName(String levelName){
        this.levelName = levelName;
    }
    
    
// Getter
    public int getLevelId(){
        return this.levelID;
    }
    
    public String getLevelName(){
        return this.levelName;
    }
    
    public Point2D.Double getSpawn(){
        return this.levelSpawnLocation;
    }

    String getBackgroundTexture() {
        return this.backgroundtexture;
    }
}


