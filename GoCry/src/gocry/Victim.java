package gocry;

import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;


/**
 * Datenklasse der Spielfgigur. Speicherung und Veränderung der Eigenschaften.
 * 
 * @author justus
 */
public class Victim {

    // Position der Spielfigur
    private int positionX;
    private int positionY;
    private int height;
    private int width;
    // Pfad zur Textur für Spielfigur
    private String texture;
    // Name des Spielers
    private String name;
    // Pfad zur Sounddatei fürs Springen
    private String jumpSound;
    // Ausgeführte Bewegung (links)
    private boolean moveLeft = false;
    //Ausgeführte Bewegung (rechts)
    private boolean moveRight = false;
    private static Victim instance;
    // Sprunghöhe 
    private int jumpHeight;
    private int maxHeight;
    private boolean inJump = false;

    private boolean blockOnRightSide;
    private boolean blockOnLeftSide;
    private boolean fallen;
    
    private boolean headWind = false;
    private int headWindCounter = 0;
    
    private double moveModification = 1;
    private double gravityModification = 1;
    
    private int gravityCounter = 0;
    
    private BufferedImage activeImage;
    
    private BufferedImage standRight;
    private BufferedImage standLeft;
    
    private BufferedImage walkRight[];
    private BufferedImage walkLeft[];
    private int walkTimer = 0;
    
    private ArrayList<Ghost> ghostList = new ArrayList<Ghost>();

    /**
     * Rückgabe des einzigen Victim Objects.
     * @return Rückgabe des Victims
     */
    public static Victim getInstance() {
        if (instance == null) {
            instance = new Victim();
        }
        return instance;
    }
    /**
     * Texturen werden bei Victimerstellung gerladen & Standardbild für Start wird gesetzt.
     */
    Victim() {
        loadTextures();
        activeImage = standRight;
    }
    /**
     * Ausgabe der GhostList (Aufnahme des Spiels)
     * @return GhostList
     */
    public ArrayList<Ghost> getGhostList(){
        return this.ghostList;
    }
    /**
     * Ghostlist wird zurückgesetzt. Sollte vor jedem LevelStart gemacht werden.
     */
    public void resetGhostList(){
        this.ghostList = new ArrayList<Ghost>();
    }
    /**
     * Laden aller Texturen für den Spielern
     */
    private void loadTextures(){
        walkRight = new BufferedImage[6];
        walkLeft = new BufferedImage[6];
        
        try {
             standRight = ImageIO.read(new File("victim/stand_right.png"));
             standLeft = ImageIO.read(new File("victim/stand_left.png"));
             for(int i = 0; i < 6; i++){
                 int a = i+1;
                 walkRight[i] = ImageIO.read(new File("victim/walking_right" + a + ".png"));
                 walkLeft[i] = ImageIO.read(new File("victim/walking_left" + a + ".png"));
             }
        } catch (IOException ex){
            System.out.println("Textures found");
        }
    }
    /**
     * Je nach vorherigen Bild wird das passende Bild für das Stehen geladen
     */
    private void switchToStand(){
        if(this.inJump == false){
            if(Arrays.asList(walkRight).contains(activeImage)){
                activeImage = standRight;
            }
            if(Arrays.asList(walkLeft).contains(activeImage)){
                activeImage = standLeft;
            }        
        }
    }
    /**
     * Animation nach rechts. Es wird für jeden Tick das nächste Bild aus dem Array geladen.
     * Überladungsschutz
     */
    private void walkRight(){
        if(this.inJump == false){
            if(walkTimer <= 5){
                activeImage = walkRight[walkTimer];
                walkTimer++;
            } else {
                walkTimer = 0;
            }
        }
    }
    /**
     * Animation nach links. Es wird für jeden Tick das nächste Bild aus dem Array geladen.
     * Überladungsschutz
     */
    private void walkLeft(){
        if(this.inJump == false){
            if(walkTimer <= 5){
                activeImage = walkLeft[walkTimer];
                walkTimer++;
            } else {
                walkTimer = 0;
            }
        }
    }
    /**
     * MODIFIKATION Victimgeschwindigkeit
     * @param in boolean Geschwindigkeitsveränderung
     */
    public void setMovementMod(double in){
        this.moveModification = in;
    }
    /**
     * MODIFIKATION Gravitation
     * @param in boolean Gravitationveränderung
     */
    public void setGravityMod(double in){
        this.gravityModification = in;
    }
    /**
     * MODIFIKATION Gegenwind
     * @param in Gegenwind an/aus
     */
    public void setHeadWindMod(boolean in){
        this.headWind = in;
    }
    /**
     * Nachträgliche setUp Methode für Victimveränderung. Kann bei jedem Levelstart gesetzt werden.
     * @param name Name des Victims
     * @param texture Texturpfad (aktuell nicht genutzt)
     * @param jumpSound soundPfad für Sprung (aktuell nicht genutzt)
     */
    public void setUp(String name, String texture, String jumpSound) {
        this.name = name;
        this.texture = texture;
        this.jumpSound = jumpSound;
    }
    /**
     * Initialisierung des Victims. Größe, Breite, Spawnpunkt wird berechnet 
     * @param blockwidth anhand der Breite eines Blockes im Spielfeld
     * @param spawn Position aus Datenbank
     */
    public void initial(int blockwidth, Point2D spawn) {
        Point2D in = spawn;
       // this.blocksize = blockwidth;
        this.width = (int) Math.round(blockwidth * 0.7);
        this.height = (int) Math.round(blockwidth * 1.2);
        this.setPosition(this.getRelToPixelSize(in));
        calcJumpHeight(blockwidth);
    }
    /**
     * Umwandlung von der relativen Position im ObjectArray(x; 0-31 - y; 0-17) in die
     * realer Screen Position (in Pixel Null-Punkt oben link)
     * Berechnung: x*=40; y = FensterBreite - (y*40) - SpielerHöhe
     * 
     * @param input Point2D Position im Array
     * 
     * @return Point2D reale Position in Pixel
     */
    public Point2D getRelToPixelSize(Point2D input) {
        input.setLocation(input.getX() * LevelController.getInstance().blockWidth, 
                LevelController.getInstance().frameHeight - (input.getY() * LevelController.getInstance().blockWidth) - Victim.getInstance().getHeight());
        return input;
    }
    /**
     * Umwandlung von realer Screen Position (1280*720 in Pixel Null-Punkt oben link) in
     * die relative Position im ObjectArray(x; 0-31 - y; 0-17 Null-Punkt unten links)
     *
     * Berechnung: x /= FensterBreite * ArrayBreite ( x / 1280 * 32)
     *             y = FensterHöhe - y / FensterHöhe * ArrayHöhe (720-(y/720*18) 
     *
     * @param point - Reale Position des Victims (Linker oberer Eckpunkt)
     * @return (x; 0.0-31.0 - y; 0.0-17.0)
     */
    public Point2D getRelativLocation(Point2D point) {
        return new Point2D.Double((point.getX() / LevelController.getInstance().frameWidth * LevelController.getInstance().blockArrayWidth),
                ((LevelController.getInstance().frameHeight - point.getY()) / LevelController.getInstance().frameHeight * LevelController.getInstance().blockArrayHeight));
    }
    
    /**
     * Abspeicherung der aktuellen Position und hinzufügen in die GhostList
     */
    public void savePos(){
        ghostList.add(new Ghost(Victim.getInstance().getImage(), Victim.getInstance().getPositionX(), Victim.getInstance().getPositionY()));
    }
    
    /**
     * Berechnung der Sprunghöhe des Victims
     * @param blockwidth wird anhand Blockgröße festgesetzt
     */
    public void calcJumpHeight(int blockwidth) {
        this.jumpHeight = (int) Math.round(blockwidth * 1.6);
    }

    /**
     * Einleitung des Sprunges falls Victim auf Block steht und nicht bereits springt
     */
    public void startJump() {
        if (inJump == false && LevelController.getInstance().onSolidBlock()) {
            inJump = true;
            calcMaxJumpHeight();
        }
    }
    /**
     * Berechnung der Sprunghöhe für jeden Sprung
     */
    public void calcMaxJumpHeight() {
        this.maxHeight = this.positionY - this.jumpHeight;
    }
    /**
     * Sprungabfrage, falls berechnete Höhe erreicht wurde - Abbruch
     */
    public void jump() {
        if (this.positionY <= this.maxHeight) {
            this.endJump();
        } else {
            this.positionY -= (int) Math.round(this.height / 10.0);
        }
    }
    /**
     * Gravitation / Fallen an
     */
    public void startFallen(){
        this.fallen = true;
    }
    /**
     * Gravitation / Fallen aus
     */
    public void endFallen(){
        this.fallen = false;
    }
    /**
     * Sprung beenden
     */
    public void endJump() {
        this.inJump = false;
        this.maxHeight = 0;
    }
    /**
     * Alle Bewegung zurücksetzen (Für LevelStart)
     */
    public void resetMovement(){
        this.inJump = false;
        this.moveLeft = false;
        this.moveRight = false;
        this.fallen = true;
    }
    /**
     * Schritte nach rechts einleiten
     */
    public void startMoveRight() {
        if (blockOnRightSide == false) {
            this.moveRight = true;
        }
    }
    /**
     * Schritte nach recht beenden
     */
    public  void endMoveRight() {
        this.moveRight = false;
    }
    /**
     * Schritte nach links einleiten
     */
    public void startMoveLeft() {
        if (blockOnLeftSide == false) {
            this.moveLeft = true;
        }
    }
    /**
     * Schritte nach links beenden
     */
    public void endMoveLeft() {
        this.moveLeft = false;
    }
    /**
     * CalcMovement wird im Thread regelmäßig aufgerufen.
     * Anhand der oben gesetzten Booleans für die Bewegungsrichtungen und Modifikationen
     * wird hier die Position des Victims geprüft und wenn möglich verändert
     */
    public void calcMovement() {
        // GhostSpeicher und Modifikation
        savePos();
        if(LevelController.getInstance().shouldTinnitusPlayed()){
            ViewController.getInstance().playTinnitus();
        } else {
            ViewController.getInstance().stopTinnitus();
        }
        if(this.moveModification == 2){
            this.moveModification = 1.5;
        }
        if(this.headWind && LevelController.getInstance().blockOnLeftSide() == false){
            if(this.headWindCounter == 2){
                this.setPositionX(this.getPositionX() - 1);
                this.headWindCounter = 0;
            }
            this.headWindCounter++;
        }
        
        /*
        * Bewegungssteuerung anhand Keylistener
        */
        if (this.inJump) {
            if (LevelController.getInstance().blockOnTop()) {
                this.endJump();
            } else {
                this.jump();
            }
        }
        if (this.moveLeft && LevelController.getInstance().blockOnLeftSide() == false) {
            this.walkLeft();
            this.setPositionX(this.getPositionX() - (int) Math.round(this.width / 10.0 * this.moveModification));
        }
        if (this.moveRight && LevelController.getInstance().blockOnRightSide() == false) {
            this.walkRight();
            this.setPositionX(this.getPositionX() + (int) Math.round(this.width / 10.0 * this.moveModification));
        }
        // Bewegungsmodifikation durch Gameloop
        if (this.fallen) {
            
            if(this.gravityModification == 0){
                if(this.gravityCounter == 2){
                    this.setPositionY((int) (this.getPositionY() +  1));//Math.round(this.width / 25.0)));
                    this.gravityCounter = 0;
                }
                this.gravityCounter++;
            } else {
                //Normale Gravitation
                this.setPositionY(this.getPositionY() + (int) Math.round(this.width / 25.0));
            }
        }
        
        if(this.moveLeft == false && this.moveRight == false && this.inJump == false){
            switchToStand();
        }
    }

// Setter
    /**
     * Setzte die Position auf der X Achse
     * @param x PositionY int
     */
    public synchronized void setPositionX(int x) {
        this.positionX = x;
    }
    /**
     * Setzte die Position auf der Y Achse
     * @param y PositionY int
     */
    public synchronized void setPositionY(int y) {
        this.positionY = y;
    }
    /**
     * Setzte die Position als Punkt
     * @param input Position Point2D
     */
    public synchronized void setPosition(Point2D input) {
        this.positionY = (int) input.getY();
        this.positionX = (int) input.getX();
    }


// Getter
    /**
     * Ausgabe der Y Position
     * @return Position Y
     */
    public synchronized int getPositionY() {
        return this.positionY;
    }
    /**
     * Ausgabe der X Position
     * @return Position X
     */
    public synchronized int getPositionX() {
        return this.positionX;
    }
    /**
     * Ausgabe des Rechten Oberen Eckpunktes
     * @return Point2D Eckpunkt oben rechts
     */
    public Point2D getRTCorner() {
        return new Point2D.Double((this.positionX + this.width), (this.positionY));
    }
    /**
     * Ausgabe des Linken Oberen Eckpunktes
     * @return Point2D Eckpunkt oben links
     */
    public Point2D getLTCorner() {
        return new Point2D.Double((this.positionX), (this.positionY));
    }
    /**
     * Ausgabe des Rechten Unteren Eckpunktes
     * @return Point2D Eckpunkt unten rechts
     */
    public Point2D getRBCorner() {
        return new Point2D.Double((this.positionX + this.width), (this.positionY + this.height));
    }
    /**
     * Ausgabe des Linken Unteren Eckpunktes
     * @return Point2D Eckpunkt unten links
     */
    public Point2D getLBCorner() {
        return new Point2D.Double((this.positionX), (this.positionY + this.height));
    }
    /**
     * Rückgabe ob das Victim aktuell springt
     * @return Ausgabe inJump
     */
    public boolean getInJump() {
        return this.inJump;
    }
    /**
     * Setzten ob Spieler sich im Sprung befindet
     * @param jump boolen inJump
     */
    public void setInJump(boolean jump) {
        this.inJump = jump;
    }
    /**
     * Ausgabe der Höhe
     * @return Höhe des Spielers
     */
    public int getHeight() {
        return this.height;
    }
    /**
     * Ausgabe der BReite
     * @return Breite des Spielers
     */
    public int getWidth() {
        return this.width;
    }
    /**
     * Setzten ob ein Block auf der rechten Seite ist
     * @param rightBlock boolean ob Block auf der Seite ist 
     */
    public void blockOnRightSide(boolean rightBlock) {
        this.blockOnRightSide = rightBlock; //To change body of generated methods, choose Tools | Templates.
    }
    /**
     * Setzen ob ein Block auf der linken Seite ist
     * @param leftBlock boolean ob Block auf der Seite ist
     */
    public void blockOnLeftSide(boolean leftBlock) {
        this.blockOnLeftSide = leftBlock; //To change body of generated methods, choose Tools | Templates.
    }
    /**
     * Ausgabe aktuelles Spieler Bild
     * @return aktuelles VictimBild
     */
    public BufferedImage getImage(){
        return this.activeImage;
    }
}
