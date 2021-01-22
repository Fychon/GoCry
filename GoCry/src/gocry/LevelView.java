/*
 * Anzeige der LevelObjects und des Victim
 */
package gocry;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.imageio.ImageIO;


/**
 *
 * @author johann
 */
public class LevelView extends JPanel {
    private String levelName;
    private ArrayList<LevelObject> objectList = new ArrayList<LevelObject>();
    private BufferedImage[][] images = new BufferedImage[50][50];
    private BufferedImage background;

    public LevelView(JFrame frame, int levelID) {
        
            this.setPreferredSize((frame.getSize()));
            this.setBounds(0,0,frame.getSize().width, frame.getSize().height);
            addObjectsFromLevelID(levelID);
            try{
                this.levelName = LevelController.getInstance().getLevelName(levelID);
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
    }
    
    public void setLevelName(String in){
        this.levelName = in;
    }
    
    public void resetLevel(){
        objectList = new ArrayList<LevelObject>();
    }

    public void addObjectsFromLevelID(int levelID) {
        try {
            objectList = LevelController.getInstance().getAllObjectsFromLevelID(levelID);
        } catch (SQLException ex) {
            System.out.print(ex);
        }
    }

    public void resizeObjects() {
        for (LevelObject object : objectList) {
            object.calcRealSize(this.getSize());
            object.calcRealLocation(this.getSize());
        }
    }
    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        
        Font f = new Font("Dialog", Font.PLAIN, 38);
        g.setFont(f);
        g.setColor(Color.white);
        FontMetrics myFM = g.getFontMetrics();
        int textBreite = myFM.stringWidth(levelName);
        int textHoehe = myFM.getHeight();       
        
        g.drawImage(background, 0, 0, null);
        g.drawString(levelName, (this.getSize().width-textBreite)/2 , textHoehe + 4);

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
