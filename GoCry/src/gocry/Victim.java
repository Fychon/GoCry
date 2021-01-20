package gocry;

import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;

/*
 * Spielerklasse mit Position der Spielfigur, Texturen, einem freiwählbaren Namen und dem Sprungsound
 */
/**
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
    private int headWindSwitch = 0;
    
    private double moveModification = 1;
    private double gravityModification = 1;
    
    private int gravitySwitch = 0;
    
    private BufferedImage activeImage;
    
    private BufferedImage standRight;
    private BufferedImage standLeft;
    
    private BufferedImage walkRight[];
    private BufferedImage walkLeft[];
    private int walkTimer = 0;
    
    private ArrayList<Ghost> ghostList = new ArrayList<Ghost>();

    
    public static Victim getInstance() {
        if (instance == null) {
            instance = new Victim();
        }
        return instance;
    }

    Victim() {
        loadTextures();
        activeImage = standRight;
    }
    
    public ArrayList<Ghost> getGhostList(){
        return this.ghostList;
    }
    
    public void resetGhostList(){
        this.ghostList = new ArrayList<Ghost>();
    }
    
    private void loadTextures(){
        walkRight = new BufferedImage[6];
        walkLeft = new BufferedImage[6];
        
        try {
             standRight = ImageIO.read(new File("victim/stand_right.png"));
             standLeft = ImageIO.read(new File("victim/stand_left.png"));
        } catch (IOException ex){
            System.out.println("This Textures found");
        }
        try {
             for(int i = 0; i < 6; i++){
                 int a = i+1;
                 walkRight[i] = ImageIO.read(new File("victim/walking_right" + a + ".png"));
                 walkLeft[i] = ImageIO.read(new File("victim/walking_left" + a + ".png"));
             }
        
        } catch (IOException ex){
            System.out.println("No Textures found");
        }
    }

    private void standRight(){
        walkTimer = 0;
        activeImage = standRight;
    }
    
    private void standLeft(){

        walkTimer = 0;
        activeImage = standLeft;

    }
    
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
    
    public void setMovementMod(double in){
        this.moveModification = in;
    }

    public void setGravityMod(double in){
        this.gravityModification = in;
    }
    
    public void setHeadWindMod(boolean in){
        this.headWind = in;
    }
    Victim(String name, String texture, String jumpSound) {
        this.name = name;
        this.texture = texture;
        this.jumpSound = jumpSound;
    }

    public void setUp(String name, String texture, String jumpSound) {
        this.name = name;
        this.texture = texture;
        this.jumpSound = jumpSound;
    }

    public void inital(int blockwidth, Point2D spawn) {
       // this.blocksize = blockwidth;
        this.width = (int) Math.round(blockwidth * 0.7);
        this.height = (int) Math.round(blockwidth * 1.2);
        this.setPosition(this.getRelToPixelSize(spawn));
        calcJumpHeight(blockwidth);
    }

    public Point2D getRelToPixelSize(Point2D input) {
        input.setLocation(input.getX() * LevelController.getInstance().blockArrayWidth, LevelController.getInstance().frameHeight - (input.getY() * LevelController.getInstance().blockWidth) - Victim.getInstance().getHeight());
        return input;
    }
    
    //public boolean
    /**
     * Umwandlung von realer Screen Position (in Pixel Null-Punkt oben link) in
     * die relative Position im ObjectArray(x; 0-31 & y; 0-17)
     *
     * Berechnung:
     *
     * @param point - Reale Position des Victims (Linker oberer Eckpunkt)
     * @return (x; 0.0-31.0 & y; 0.0-17.0)
     */
    public Point2D getRelativLocation(Point2D point) {
        return new Point2D.Double((point.getX() / LevelController.getInstance().frameWidth * LevelController.getInstance().blockArrayWidth),
                ((LevelController.getInstance().frameHeight - point.getY()) / LevelController.getInstance().frameHeight * LevelController.getInstance().blockArrayHeight));
    }

    // Sprunghöhe des Spielers
    public void calcJumpHeight(int blockwidth) {
        this.jumpHeight = (int) Math.round(blockwidth * 1.6);
    }

    public void startJump() {
        if (inJump == false && LevelController.getInstance().onSolidBlock()) {
            inJump = true;
            calcMaxJumpHeight();
        }
    }
    
    public void savePos(){
        ghostList.add(new Ghost(Victim.getInstance().getImage(), Victim.getInstance().getPositionX(), Victim.getInstance().getPositionY()));
    }

    public void calcMaxJumpHeight() {
        this.maxHeight = this.positionY - this.jumpHeight;
    }

    // Bewegungsmethode: Sprung
    public void jump() {
        if (this.positionY <= this.maxHeight) {
            this.endJump();
        } else {
            this.positionY -= (int) Math.round(this.height / 5.0);
        }
    }
    
    public void startFallen(){
        this.fallen = true;
    }
    
    public void endFallen(){
        this.fallen = false;
    }
    public void endJump() {
        this.inJump = false;
        this.maxHeight = 0;

    }
    
    public void resetMovement(){
        this.inJump = false;
        this.moveLeft = false;
        this.moveRight = false;
        this.fallen = true;
    }

    // Bewegungsmethode: Rechts
    public void startMoveRight() {
        if (blockOnRightSide == false) {
            this.moveRight = true;
        }
    }

    public  void endMoveRight() {
        this.moveRight = false;
    }

    // Bewegungsmethode: Links
    public void startMoveLeft() {
        if (blockOnLeftSide == false) {
            this.moveLeft = true;
        }
    }

    public void endMoveLeft() {
        this.moveLeft = false;
    }

    // Bewegung anhand Bewegungsvariablen
    public void calcMovement() {
        savePos();
        if(moveModification == 2){
            moveModification = 1.5;
        }
        
        if(this.headWind && LevelController.getInstance().blockOnLeftSide() == false){
            if(headWindSwitch == 2){
                this.setPositionX(this.getPositionX() - 1);
                headWindSwitch = 0;
            }
            headWindSwitch++;
        }
        if (this.inJump) {
            if (LevelController.getInstance().blockOnTop()) {
                this.endJump();
            } else {
                this.jump();
            }
        }
        if (this.moveLeft && LevelController.getInstance().blockOnLeftSide() == false) {
            walkLeft();
            this.setPositionX(this.getPositionX() - (int) Math.round(this.width / 10.0 * moveModification));
        }
        if (this.moveRight && LevelController.getInstance().blockOnRightSide() == false) {
            walkRight();
            this.setPositionX(this.getPositionX() + (int) Math.round(this.width / 10.0 * moveModification));
        }
        if (this.fallen && LevelController.getInstance().onSolidBlock()==false) {
            
            if(gravityModification == 0){
                if(gravitySwitch == 2){
                    this.setPositionY((int) (this.getPositionY() +  1));//Math.round(this.width / 25.0)));
                    gravitySwitch = 0;
                }
                gravitySwitch++;
            } else {
                //Normale Gravitation
                this.setPositionY(this.getPositionY() + (int) Math.round(this.width / 50.0));
            }
        }
        
        if(this.moveLeft == false && this.moveRight == false && this.inJump == false){
            switchToStand();
        }
    }

// Setter
    public void setPositionX(int x) {
        this.positionX = x;
    }

    public void setPositionY(int y) {
        this.positionY = y;
    }

    public void setPosition(Point2D input) {
        this.positionY = (int) input.getY();
        this.positionX = (int) input.getX();
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJumpSound(String jumpSound) {
        this.jumpSound = jumpSound;
    }

// Getter
    public int getPositionY() {
        return this.positionY;
    }



    public Point2D getRTCorner() {
        return new Point2D.Double((this.positionX + this.width), (this.positionY));
    }

    public Point2D getLTCorner() {
        return new Point2D.Double((this.positionX), (this.positionY));
    }

    public Point2D getRBCorner() {
        return new Point2D.Double((this.positionX + this.width), (this.positionY + this.height));
    }

    public Point2D getLBCorner() {
        return new Point2D.Double((this.positionX), (this.positionY + this.height));
    }

    public boolean getInJump() {
        return this.inJump;
    }

    public int getPositionX() {
        return this.positionX;
    }

    public String getTexture() {
        return this.texture;
    }

    public String getName() {
        return this.name;
    }

    public void setInJump(boolean jump) {
        this.inJump = jump;
    }

    public String getJumpSound() {
        return this.jumpSound;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public void blockOnRightSide(boolean rightBlock) {
        this.blockOnRightSide = rightBlock; //To change body of generated methods, choose Tools | Templates.
    }

    public void blockOnLeftSide(boolean leftBlock) {
        this.blockOnLeftSide = leftBlock; //To change body of generated methods, choose Tools | Templates.
    }
    
    public BufferedImage getImage(){
        return this.activeImage;
    }
}
