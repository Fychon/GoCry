package gocry;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Verwaltung und Steuerung der Interaktionen in einem Level selbst.
 * Aufbereitung der ObjectListe und Array aus der Datenbank für die Weiterverarbeitung
 * in Collsisions und Anzeige in LevelView.
 * 
 * Collisions zwischen Victim und Blöcken werden geprüft und ausgelöst.
 * Prüfen des Tastaturinputs und Steuerung des Spielers wird angestoßen.
 * Prüfen und Setzen der Modifikationen bei Levelstart.
 * 
 * @author johann
 */
public class LevelController implements KeyListener {

    private static LevelController instance;
    
    private boolean inLevel = true;
    public ArrayList<LevelObject> objects;
    private LevelObject[][] objectArray;
    
    private ArrayList<Level> levels;

    private long startTime;

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

    /**
     * Rückgabe der einzig vorhandenen LevelController Instanz, wenn keine vorhanden wird eine erstellt.
     * @return LevelController einzige Instanz
     */
    public static LevelController getInstance() {
        if (instance == null) {
            instance = new LevelController();
        }
        return instance;
    }

    /**
     * Erstellung des Controller und laden aller Level aus der Datenbank.
     */
    public LevelController() {
        loadLevel();
    }
    
   /**
    * Rückgabe eines spezifischen Levels nach LevelID
    * @param levelID int ID des Levels
    * @return Level komplettes Objekt
    */
    public Level getLevel(int levelID){
        return levels.get(levelID);
    }
    /**
     * Versuchtes Laden und Einschreiben in Liste aller Level aus Datenbank
     */
    public void loadLevel(){
        try {
            levels = DBInterface.getInstance().getAllLevel();
        } catch (SQLException ex) {
            
        }
    }
    /**
     * Setzen aller geladenen Modifikationen aus spezifischen Level.
     * Modifikationen werden in verscheidenen Bereichen gesetzt und genutzt.
     * @param levelID int Verknüpfung zum Level
     */
    public void setModsForLevel(int levelID){
        Victim.getInstance().setGravityMod(levels.get(levelID).gravitation);
        Victim.getInstance().setMovementMod(levels.get(levelID).movementspeed);
        Victim.getInstance().setHeadWindMod(levels.get(levelID).headwind);
        ViewController.getInstance().setGhostEnabled(levels.get(levelID).ghostsEnabled);
        invertEnabled = levels.get(levelID).invertcontrol;
        wtfEnabled = levels.get(levelID).wtfisenabled;
        tinnitus = levels.get(levelID).tinnitus;
    }

    
    /**
     * Collision detection, Prüfung ob und welcher Block an dieser ArrayPosition ist.
     * Richtiges Verhalten (kill, win, nichts tun) anhand Blockart/Status wird angestoßen.
     * @param values zu prüfender ContactPoint als 2x Dimensionale Double Koordinaten (0.0-31.9)u.(0.0-17.9)
     *                  Deizimalstellen werden bei Umwandlung in Integer abgeschnitten.
     * @return boolean false bei keinBlock
     */
    public boolean isBlockOnPoint(Point2D values) {
        boolean result = false;
        //Unter 0 und ab 32 // 18 wird true zurückgegeben -> Spielrand wird wie neutraler Block gewertet
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
                        levelInSwitch=true;
                        //resetVictim();
                        ViewController.getInstance().playDeathSound();
                        ViewController.getInstance().backToMenu(false);
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
    /**
     * Collsision-Detection nach unten. Es wird geschaut ob sich 1 Pixel unter dem Spieler ein Block befindet. Es wird der linke und rechte Eckpunkt des Spielers verwendet.
     * Um ein realistisches Sprung und Lauferlebnis zu ermöglichen, wird die Hitbox insgesamt um 10 Pixel nach innen verschoben.
     * Der Spieler kann somit an Kanten stehen/herunterfallen und Collsisions mit Killblöcken werden nciht zu früh ausgelöst. 
     * @return boolean block unter Victim
     */
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

    /**
     * Collsision Detection nach rechts. Es wird geschaut ob sich 1 Pixel auf der rechten Seite des Victims ein Block defindet. 
     * Es wird der obere, mittlere und untere Eckpunkt des Victims verwendet.
     * 
     * @return boolean Block auf rechter Seite
     */
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
    /**
     * Collsision Detection nach links. Es wird geschaut ob sich 1 Pixel auf der Linken Seite des Victims ein Block defindet. 
     * Es wird der obere, mittlere und untere Eckpunkt des Victims verwendet.
     * 
     * @return boolean Block auf linker Seite
     */
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

    /**
     * Collsision-Detection nach oben. Es wird geschaut ob sich 1 Pixel über dem Spieler ein Block befindet. Es wird der linke und rechte Eckpunkt des Spielers verwendet.
     * Um ein realistisches Sprung zu ermöglichen, wird die Hitbox insgesamt um 4 Pixel nach innen verschoben.
     * Collsisions mit Killblöcken werden nicht zu früh ausgelöst. 
     * @return boolean Block über Victim
     */
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

    /**
     * Initialisierung unserers BlockArrays (32x18) und Füllung mit Null für die Abfragen.
     * Blöcke werden direkt an die richtige Position im Array geschrieben.
     * @param objects LevelObjekte eines Levels als ArrayListe
     */
    public void setUpArray(ArrayList<LevelObject> objects) {
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
    
    /**
     * Levelwechsel bei Kontakt mit Win Block. Der ViewController wird den Levelwechsel durchführen und eine neue GameView erstellen.
     */
    public void nextLayer(){
        ViewController.getInstance().nextLayer();
    }
    /**
     * Laden aller Objekte eines Levels und Aufbereitung in eine ArrayListe für die Ausgabe und als
     * formatiertes Array für die Collsisiondetection.
     * @param levelID int verweist auf das aktuelle Level
     * @return LevelObjekte als ArrayListe
     * @throws SQLException Exception bei Datenbankfehlern 
     */
    public ArrayList<LevelObject> getAllObjectsFromLevelID(int levelID) throws SQLException {
        setUpArray(DBInterface.getInstance().allLevelObjects(levelID));
        return objects = DBInterface.getInstance().allLevelObjects(levelID);
    }
    /**
     * SetUp des Victims bei jeden Levelstart. Der SpawnPunkt des Victims wird passend zum aktuellen Level gesetzt.
     * @param victimID int zugehörig zum aktuellen Level
     */
    public void setVictim(int victimID){
        Victim.getInstance().initial(blockWidth, levels.get(victimID).getSpawn());
    }
    /**
     * Gravity Detection, wird regelmäßifg aus der GameLoop aufgerufen und prüft ob das Victim aktuell fallen muss oder nicht.
     */
    public void checkPosition() {
        if (onSolidBlock() == false && Victim.getInstance().getInJump() == false) {
            Victim.getInstance().startFallen();
        } else {
            Victim.getInstance().endFallen();
        }
    }
    /**
     * Nicht genutzte Methode, setzt den Spieler an den SpawnPunkt zurück.
     */
    public void resetVictim() {
        Victim.getInstance().setPosition(Victim.getInstance().getRelToPixelSize(new Point2D.Double(1.0, 3.0)));
        Victim.getInstance().setInJump(false);
    }
    /**
     * Ausgelöst bei CheatTaste um das Victim in die Nähe des Ziels zu bringen (Koordinaten 2/17)
     */
    public void vicToGoal(){
        Victim.getInstance().setPosition(Victim.getInstance().getRelToPixelSize(new Point2D.Double(2, 17.0)));
        Victim.getInstance().setInJump(false);
    }
    

    @Override
    public void keyTyped(KeyEvent arg0) {
    }

    /**
     * Wird automatisch aufgerufen falls bei Anzeigen der GameView eine Taste gedrückt wird.
     * Falls sich der Spieler noch im InfoScreen befindet geht der Spieler mit SPACE in das erste Level rein.
     * Mit ESC wird das Hauptmenü wieder aufgerufen. "A" u. "D" || "F" u. "T" sind für die Richtungs
     * @param e inKeyEvent
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if(ViewController.getInstance().getInMenu() == false){
                ViewController.getInstance().backToMenu(true);
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_U) {
            if(ViewController.getInstance().getCheated()==false){
                ViewController.getInstance().setCheated(true);
            }
            vicToGoal();
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            ViewController.getInstance().spacePressed();
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
        } else {
            
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
    /**
     * Wird automatisch aufgerufen falls bei Anzeigen der GameView eine Taste losgelassen wird.
     * Wird für die Bewegungssteuerung genutzt. RichtungsSwitch(Boolea) wird wieder auf false gesetzt.
     * @param e inKeyEvent
     */
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
    /**
     * Rückgabe true/false ob aktuell die Taste W gedrückt ist. Verwendung für Tinnitus.
     * @return boolean ob W gedrückt ist
     */
    public boolean getwIsPressed(){
        return this.wIsPressed;
    }
    /**
     * Rückgabe true/false ob aktuell die Taste A gedrückt ist. Verwendung für Tinnitus.
     * @return boolean ob A gedrückt ist
     */
    public boolean getaIsPressed(){
        return this.aIsPressed;
    }
    /**
     * Rückgabe true/false ob aktuell die Taste D gedrückt ist. Verwendung für Tinnitus.
     * @return boolean ob D gedrückt ist
     */    
    public boolean getdIsPressed(){
        return this.dIsPressed;
    }
    /**
     * Abspeicherung der aktuellen Systemzeit bei Spielbeginn für die Berechnung der allgemeinen Spielzeit
     * @param currentTimeMillis Speicherung der Startzeit
     */
    public void setStartTime(long currentTimeMillis) {
        this.startTime = currentTimeMillis;
    }
    /**
     * Rückgabe der abgespeicherten Startzeit des Spielbeginns
     * @return long Startzeit der aktuellen Runde
     */
    public long getStartTime() {
        return this.startTime;
    }
    /**
     * Setzen ob man sich aktuell in einem Levelwechsel befindet (Collisions werden deaktiviert)
     * Sicherheit das Collsisions nicht mehrmals anch "Spielende" registriert werden.
     * @param in boolean ob aktuell im Levelwechsel
     */
    public void setLevelInSwitch(boolean in){
        this.levelInSwitch = in;
    }
    /**
     * Abfrage ob aktuell eine Steuerungstaste gedrückt wird und der Tinnitus im Loop abgespielt werden soll.
     * @return boolean ob Tinnitus abgespielt werden soll
     */
    public boolean shouldTinnitusPlayed(){
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
