/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gocry;

import java.awt.image.BufferedImage;

/**
 * Datenklasse für GhostErstellung.
 * Die Positions- und Animationsdaten werden mit Hilfe dieser Klasse über den Spielverlauf erstellt und gespeichert.
 * Damit werden die Ghosts als Replay  und die Modifikation "GhostsEnabled" erstellt.
 * @author johann
 */
public class Ghost {
    private int positionx;
    private int positiony;
    private BufferedImage image;
    
    /**
     * Erstellung eines Ghost. Das aktuelle Bild wird mit der aktuellen Position abgespeichert.
     * @param img BufferedImage aktuelles Bild
     * @param posx int aktuelle Position X
     * @param posy int aktuelle Position Y
     */
    public Ghost(BufferedImage img, int posx, int posy){
        this.image = img;
        this.positionx = posx;
        this.positiony = posy;
    }
    /**
     * Rückgabe der Position auf der X-Achse
     * @return int Position auf X
     */
    public int getPositionX(){
        return this.positionx;
    }
    /**
     * Rückgabe der Position auf der Y-Achse
     * @return int Position auf Y
     */
    public int getPositionY(){
        return this.positiony;
    }
    /**
     * Rückgabe des abgespeicherten Bildes.
     * @return BufferedImage aktuelles Bild
     */
    public BufferedImage getImage(){
        return this.image;
    }
}
