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
    private ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    private ArrayList<BufferedImage> killImages = new ArrayList<BufferedImage>();
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
            ArrayList<String> texturePaths = DBInterface.getInstance().getTextures();
            try {
                background = ImageIO.read(new File(LevelController.getInstance().getLevel(levelID).getBackgroundTexture()));
                for(int i = 0; i < texturePaths.size(); i++){
                    images.add(ImageIO.read(new File(texturePaths.get(i))));
                    if(i == 1){
                        killImages.add(ImageIO.read(new File("textures/killtest_top.png")));
                        killImages.add(ImageIO.read(new File("textures/killtest_right.png")));
                        killImages.add(ImageIO.read(new File("textures/killtest_bot.png")));
                        killImages.add(ImageIO.read(new File("textures/killtest_left.png")));
                    }
                }
            } catch (IOException ex){
                System.out.println("File not found");
            }
        } catch (SQLException ex) {
        }
        
        this.setBackground(Color.blue);
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
        g.drawString(levelName, (this.getSize().width-textBreite)/2 , textHoehe + 2);

        for (LevelObject object : objectList) {
            if(object.getStatus() == LevelObject.objectStatus.NEUTRAL){
                //g.setColor(Color.black);
                //g.fillRect(object.getPositionX(), object.getPositionY(), object.getWidth(), object.getHeight());
                g.drawImage(images.get(0), object.getPositionX(), object.getPositionY(), null);
            }
            if(object.getStatus() == LevelObject.objectStatus.KILL){

                //g.setColor(Color.red);
                //g.fillRect(object.getPositionX(), object.getPositionY(), object.getWidth(), object.getHeight());
                switch(object.getRotation()){
                    case 0:
                        g.drawImage(killImages.get(0), object.getPositionX(), object.getPositionY(), null);
                        break;
                    case 90:
                        g.drawImage(killImages.get(1), object.getPositionX(), object.getPositionY(), null);
                        break;
                    case 180:
                        g.drawImage(killImages.get(2), object.getPositionX(), object.getPositionY(), null);
                        break;
                    case 270:
                        g.drawImage(killImages.get(3), object.getPositionX(), object.getPositionY(), null);
                        break;
                    default:
                        g.drawImage(images.get(1), object.getPositionX(), object.getPositionY(), null);
                        break;
                }
            }
            if(object.getStatus() == LevelObject.objectStatus.WINZONE){
                //g.setColor(Color.green);
                //g.fillRect(object.getPositionX(), object.getPositionY(), object.getWidth(), object.getHeight());
                g.drawImage(images.get(2), object.getPositionX(), object.getPositionY(), null);

            }
        }
    }

}
