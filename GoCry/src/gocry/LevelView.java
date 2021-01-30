package gocry;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.imageio.ImageIO;


/**
 * Klasse für die Anzeige des Levels und seiner Komponenten.
 * Es sollen alle Komponeten dargestellt werden die sich nciht regelmäßig verändern und 
 * kein dauerhaftes repaint() der Komponenten benötigt ist.
 * Darstellung von: 
 * <br>Backgroundtexture, 
 * <br>Levelname, 
 * <br>Spielbläcke im Level.
 * 
 * @author johann
 */
public class LevelView extends JPanel {
    private String levelName;
    private ArrayList<LevelObject> objectList = new ArrayList<LevelObject>();
    private BufferedImage[][] images = new BufferedImage[50][50];
    private BufferedImage background;

    /**
     * Erstellung eines Levels in einem Frame.
     * LevelObjekte, Objekttexturen u. Levelname werden anhand LevelID aus der Datenbank geladen und zugefügt.
     * @param frame
     * @param levelID 
     */
    public LevelView(JFrame frame, int levelID) {
        
            this.setPreferredSize((frame.getSize()));
            this.setBounds(0,0,frame.getSize().width, frame.getSize().height);
            this.setLayout(null);
            addObjectsFromLevelID(levelID);
            try{
                this.levelName = LevelController.getInstance().getLevel(levelID).getLevelName();
            } catch (Exception ex){   
            }
        try {    
            ArrayList<ObjectTexture> texturePaths = DBInterface.getInstance().getTextures();
            try {
                background = ImageIO.read(new File(LevelController.getInstance().getLevel(levelID).getBackgroundTexture()));
                for(int i = 0; i < texturePaths.size(); i++){
                    images[texturePaths.get(i).getStatusId()][texturePaths.get(i).getTextureId()] = (ImageIO.read(new File(texturePaths.get(i).getTexture())));
                }
            } catch (IOException ex){
                System.out.println("File not found");
            }
        } catch (SQLException ex) {
        }
        
        this.setBackground(Color.blue);
        this.resizeObjects();
    }
    
    /**
     * Verändern des anzuzeigenden Level
     * @param in 
     */
    public void setLevelName(String in){
        this.levelName = in;
    }
    
    /**
     * Laden aller Objekte zu einem Level aus der Datenbank und erstellung einer Objektliste
     * @param levelID 
     */
    public void addObjectsFromLevelID(int levelID) {
        try {
            objectList = LevelController.getInstance().getAllObjectsFromLevelID(levelID);
        } catch (SQLException ex) {
            System.out.print(ex);
        }
    }

    /**
     * Position und Größe aller Objekte werden an die aktuelle Framegröße angepasst.
     */
    public void resizeObjects() {
        for (LevelObject object : objectList) {
            object.calcRealSize(this.getSize());
            object.calcRealLocation(this.getSize());
        }
    }
    /**
     * Überschreibung der PaintComponent Methode. Wird automatisch bei jeden repaint() mitaufgerufen.
     * LevelName, Background und alle LevelObjekte mit richtiger Textur werden in die Graphic  gezeichnet.
     * @param g 
     */
    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        
        Font f = new Font("Dialog", Font.PLAIN, 38);
        g.setFont(f);
        g.setColor(Color.white);
        FontMetrics myFM = g.getFontMetrics();
        String levelNummer = "Level " + ViewController.getInstance().getLevelCounter() + ": " + levelName;  
        int textBreiteLnr = myFM.stringWidth(levelNummer);
        int textHoeheLnr = myFM.getHeight(); 
        g.drawImage(background, 0, 0, null);
        g.drawString(levelNummer, (this.getSize().width-textBreiteLnr)/2, textHoeheLnr + 4);

        for (LevelObject object : objectList) {
            if(object.getStatus() == LevelObject.objectStatus.NEUTRAL){
                g.drawImage(images[0][object.getTextureId()], object.getPositionX(), object.getPositionY(), null);
            }
            if(object.getStatus() == LevelObject.objectStatus.KILL){
                g.drawImage(images[1][object.getTextureId()], object.getPositionX(), object.getPositionY(), null);
            }
            if(object.getStatus() == LevelObject.objectStatus.WINZONE){
                g.drawImage(images[2][object.getTextureId()], object.getPositionX(), object.getPositionY(), null);

            }
        }
    }

}
