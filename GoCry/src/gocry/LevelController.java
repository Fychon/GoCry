package gocry;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/*
 * Controller für die Interaktionen zwischen Victim und LevelObjects + Erstellung des Levels
 */
/**
 *
 * @author johann
 */
public class LevelController implements KeyListener {

    private static LevelController instance;
    
    private boolean inLevel = true;
    public Victim victim;
    public ArrayList<LevelObject> objects;
    private LevelObject[][] objectArray;
    
    private ArrayList<Level> levels;

    private long startTime;

    
    private boolean objectMovedXAxis = false;

    private boolean objectMovedYAxis = true;

    private boolean invertEnabled = false;
    private boolean wtfEnabled = false;
    private boolean tinnitus = false;
    
    private boolean wIsPressed = false;
    private boolean aIsPressed = false;
    private boolean dIsPressed= false;
    
    private boolean levelInSwitch = false;

    
    final public int blockArrayWidth = 32;
    final public int blockArrayHeight = 18;
    final public int frameWidth = 1280;
    final public int frameHeight = 720;
    final public int blockWidth = frameWidth / blockArrayWidth;
    
    int deathTimeCounter = 0;

    public static LevelController getInstance() {
        if (instance == null) {
            instance = new LevelController();
        }
        return instance;
    }

    public LevelController() {
    }
    
    public Level getLevel(int levelID){
        return levels.get(levelID);
    }
    
    public void setModsForLevel(int levelID){
        Victim.getInstance().setGravityMod(levels.get(levelID).gravitation);
        Victim.getInstance().setMovementMod(levels.get(levelID).movementspeed);
        Victim.getInstance().setHeadWindMod(levels.get(levelID).headwind);
        ViewController.getInstance().setGhostEnabled(levels.get(levelID).ghostsEnabled);
        invertEnabled = levels.get(levelID).invertcontrol;
        wtfEnabled = levels.get(levelID).wtfisenabled;
        tinnitus = levels.get(levelID).tinnitus;
    }
    
    public String getLevelName(int levelID){
        return levels.get(levelID).getLevelName();
    }

    public boolean isBlockOnPoint(Point2D values) {
        boolean result = false;
        
        if ((values.getX() <= 0 || values.getX() >= LevelController.getInstance().blockArrayWidth)
                || (values.getY() <= 0 || values.getY() >= LevelController.getInstance().blockArrayHeight)) {
            return true;
        } else {
            if (objectArray[(int) (values.getX())][((int) values.getY())] == null) {
                result = false;
            } else {
                if (objectArray[((int) values.getX())][((int) values.getY())].getStatus() == LevelObject.objectStatus.NEUTRAL) {
                    result = true;
                }
                if (objectArray[((int) values.getX())][((int) values.getY())].getStatus() == LevelObject.objectStatus.KILL) {
                    if(levelInSwitch==false){
                        levelInSwitch=true;
                        //resetVictim();
                        ViewController.getInstance().playDeathSound();
                        ViewController.getInstance().backToMenu();
                    }
                }            
                if (objectArray[((int) values.getX())][((int) values.getY())].getStatus() == LevelObject.objectStatus.WINZONE) {
                    if(levelInSwitch==false){
                        levelInSwitch = true;
                        nextLayer();
                    }
                }
            }
            return result;
        }
    }

    public boolean onSolidBlock() {
        boolean result = false;
        Point2D pixelLocationRB = Victim.getInstance().getRBCorner();
        Point2D pixelLocationLB = Victim.getInstance().getLBCorner();

        //Schaue einen Pixel nach unten; Hitbox wird verkleinert -> nan kann an Kanten besser springen
        pixelLocationRB.setLocation(pixelLocationRB.getX() - 5, pixelLocationRB.getY() + 1);
        pixelLocationLB.setLocation(pixelLocationLB.getX() + 5, pixelLocationLB.getY() + 1);

        Point2D relPosRB = Victim.getInstance().getRelativLocation(pixelLocationRB);
        Point2D relPosLB = Victim.getInstance().getRelativLocation(pixelLocationLB);

        if (isBlockOnPoint(relPosRB) || isBlockOnPoint(relPosLB)) {
            result = true;
        }
        return result;
    }

    public boolean blockOnRightSide() {
        boolean result = false;
        Point2D pixelLocationRT = Victim.getInstance().getRTCorner();
        Point2D pixelLocationRB = Victim.getInstance().getRBCorner();

        //Schaue einen Pixel nach Rechts
        pixelLocationRT.setLocation(pixelLocationRT.getX() + 1, pixelLocationRT.getY());
        pixelLocationRB.setLocation(pixelLocationRB.getX() + 1, pixelLocationRB.getY());

        Point2D relPosRT = Victim.getInstance().getRelativLocation(pixelLocationRT);
        //3. CollisionDetection in der MItte
        Point2D relPosRBT = Victim.getInstance().getRelativLocation(new Point2D.Double(Victim.getInstance().getRBCorner().getX() + 1, (Victim.getInstance().getRTCorner().getY()+(Victim.getInstance().getRBCorner().getY()-Victim.getInstance().getRTCorner().getY())/2)));
        Point2D relPosRB = Victim.getInstance().getRelativLocation(pixelLocationRB);

        if (isBlockOnPoint(relPosRT) || isBlockOnPoint(relPosRB) || isBlockOnPoint(relPosRBT)) {
            result = true;
        }
        return result;
    }

    public boolean blockOnLeftSide() {
        boolean result = false;
        Point2D pixelLocationLT = Victim.getInstance().getLTCorner();
        Point2D pixelLocationLB = Victim.getInstance().getLBCorner();

        //Schaue einen Pixel nach links
        pixelLocationLT.setLocation(pixelLocationLT.getX() - 1, pixelLocationLT.getY());
        pixelLocationLB.setLocation(pixelLocationLB.getX() - 1, pixelLocationLB.getY());

        Point2D relPosLT = Victim.getInstance().getRelativLocation(pixelLocationLT);
        //3. CollisionDetection in der MItte
        Point2D relPosLBT = Victim.getInstance().getRelativLocation(new Point2D.Double(Victim.getInstance().getLBCorner().getX() + 1, (Victim.getInstance().getLTCorner().getY()+(Victim.getInstance().getLBCorner().getY()-Victim.getInstance().getLTCorner().getY())/2)));
        Point2D relPosLB = Victim.getInstance().getRelativLocation(pixelLocationLB);

        if (isBlockOnPoint(relPosLT) || isBlockOnPoint(relPosLB) || isBlockOnPoint(relPosLBT)) {
            result = true;
        }
        return result;
    }

    public boolean blockOnTop() {
        boolean result = false;
        Point2D pixelLocationLT = Victim.getInstance().getLTCorner();
        Point2D pixelLocationRT = Victim.getInstance().getRTCorner();

        //Schaue einen Pixel nach links ; Hitbox wird von der Breite verkleinert (Sprung nach oben am Bildschirmrand)
        pixelLocationLT.setLocation(pixelLocationLT.getX()+2, pixelLocationLT.getY() - 1);
        pixelLocationRT.setLocation(pixelLocationRT.getX()-2, pixelLocationRT.getY() - 1);

        Point2D relPosLT = Victim.getInstance().getRelativLocation(pixelLocationLT);
        Point2D relPosRT = Victim.getInstance().getRelativLocation(pixelLocationRT);

        if (isBlockOnPoint(relPosLT) || isBlockOnPoint(relPosRT)) {
            result = true;
        }
        return result;
    }

    public void setUpArray(ArrayList<LevelObject> objects) {
        //Alle vorhandenen Level aus DB Laden
        try {
            levels = DBInterface.getInstance().getAllLevel();
        } catch (SQLException ex) {
            
        }
        //Initialisierung unserers BlockArrays (32x19) und Füllung mit Null für die abfragen
        objectArray = new LevelObject[blockArrayWidth][blockArrayHeight];
        for (int i = 0; i < blockArrayWidth; i++) {
            for (int y = 0; y < blockArrayHeight; y++) {
                objectArray[i][y] = null;
            }
        }
        //Füllung aus DB an richtige Position
        for (LevelObject o : objects) {
            objectArray[o.getPositionX()][o.getPositionY()] = o;
        }
    }
    
    public void nextLayer(){
        ViewController.getInstance().nextLayer();
    }

    public ArrayList<LevelObject> getAllObjectsFromLevelID(int levelID) throws SQLException {
        setUpArray(DBInterface.getInstance().allLevelObjects(levelID));
        return objects = DBInterface.getInstance().allLevelObjects(levelID);
    }

    public Victim setVictim(int victimID) throws SQLException {

        Victim.getInstance().inital(blockWidth, new Point2D.Double(1, 4));

        String[] data = DBInterface.getInstance().getVictim(victimID);
        Victim.getInstance().setUp(data[0], data[1], data[2]);
        return Victim.getInstance();
    }

    public void checkPosition() {
        if (objectMovedYAxis) {
            if (onSolidBlock() == false && Victim.getInstance().getInJump() == false) {
                Victim.getInstance().startFallen();
            } else {
                Victim.getInstance().endFallen();
            }
        }
    }

    public void resetVictim() {
        Victim.getInstance().setPosition(Victim.getInstance().getRelToPixelSize(new Point2D.Double(1.0, 3.0)));
        Victim.getInstance().setInJump(false);
    }
    
    public void vicToGoal(){
        Victim.getInstance().setPosition(Victim.getInstance().getRelToPixelSize(new Point2D.Double(2, 17.0)));
        Victim.getInstance().setInJump(false);
    }
    

    @Override
    public void keyTyped(KeyEvent arg0) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if(ViewController.getInstance().getInMenu() == false){
                ViewController.getInstance().backToMenu();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_U) {
            if(ViewController.getInstance().getCheated()==false){
                ViewController.getInstance().setCheated(true);
            }
            vicToGoal();
        }
         if(invertEnabled){
            if (e.getKeyCode() == KeyEvent.VK_D) {
                Victim.getInstance().startMoveLeft();
            }
            if (e.getKeyCode() == KeyEvent.VK_A) {
                Victim.getInstance().startMoveRight();
            }
            if (e.getKeyCode() == KeyEvent.VK_W) {
            //Ziel wird aktiviert falls LevelWechsel durchgeführt wurde
                if(levelInSwitch){
                    levelInSwitch = false;
                }
                Victim.getInstance().startJump();
            }
        } else if(wtfEnabled){
            if (e.getKeyCode() == KeyEvent.VK_T) {
                Victim.getInstance().startMoveLeft();
            }
            if (e.getKeyCode() == KeyEvent.VK_F) {
                Victim.getInstance().startMoveRight();
            }
            if (e.getKeyCode() == KeyEvent.VK_W) {
            //Ziel wird aktiviert falls LevelWechsel durchgeführt wurde
                if(levelInSwitch){
                    levelInSwitch = false;
                }
                Victim.getInstance().startJump();
            }
        }   else {
            
            if (e.getKeyCode() == KeyEvent.VK_A) {
                aIsPressed = true;
                Victim.getInstance().startMoveLeft();
            }
            if (e.getKeyCode() == KeyEvent.VK_D) {
                dIsPressed = true;
                Victim.getInstance().startMoveRight();
            }
            if (e.getKeyCode() == KeyEvent.VK_W) {
            //Ziel wird aktiviert falls LevelWechsel durchgeführt wurde
                if(levelInSwitch){
                    levelInSwitch = false;
                }
                wIsPressed = true;
                Victim.getInstance().startJump();
            }            

        }
    
    }

    @Override
    public void keyReleased(KeyEvent e) {

        if(invertEnabled){
            if (e.getKeyCode() == KeyEvent.VK_D) {
                Victim.getInstance().endMoveLeft();
            }
            if (e.getKeyCode() == KeyEvent.VK_A) {
                Victim.getInstance().endMoveRight();

            }
        }else if(wtfEnabled){
            if (e.getKeyCode() == KeyEvent.VK_T) {
                Victim.getInstance().endMoveLeft();
            }
            if (e.getKeyCode() == KeyEvent.VK_F) {
                Victim.getInstance().endMoveRight();
            }       
        } else {
            if (e.getKeyCode() == KeyEvent.VK_D) {
                dIsPressed = false;
                Victim.getInstance().endMoveRight();
            }
            if (e.getKeyCode() == KeyEvent.VK_A) {
                aIsPressed = false;
                Victim.getInstance().endMoveLeft();
            }
            if (e.getKeyCode() == KeyEvent.VK_W) {
                wIsPressed = false;
            }
        }
    }
    public boolean getwIsPressed(){
        return this.wIsPressed;
    }
    public boolean getaIsPressed(){
        return this.aIsPressed;
    }
    public boolean getdIsPressed(){
        return this.dIsPressed;
    }    
    public void setStartTime(long currentTimeMillis) {
        this.startTime = currentTimeMillis;
    }
    public long getStartTime() {
        return this.startTime;
    }
    public void setLevelInSwitch(boolean in){
        this.levelInSwitch = in;
    }
    public boolean shouldTinnituaPlayed(){
        if(this.tinnitus){
            if(this.wIsPressed || this.aIsPressed || this.dIsPressed){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
