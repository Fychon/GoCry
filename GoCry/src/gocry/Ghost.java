/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gocry;

import java.awt.image.BufferedImage;

/**
 *
 * @author johann
 */
public class Ghost {
    private int positionx;
    private int positiony;
    private BufferedImage image;
    
    public Ghost(BufferedImage img, int posx, int posy){
        this.image = img;
        this.positionx = posx;
        this.positiony = posy;
    }
    
    public int getPositionX(){
        return this.positionx;
    }
    public int getPositionY(){
        return this.positiony;
    }
    public BufferedImage getImage(){
        return this.image;
    }
    public void changeImage(){
    }
}
