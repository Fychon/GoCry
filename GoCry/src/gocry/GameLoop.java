/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gocry;

/**
 *
 * @author johann
 */
public class GameLoop extends Thread {

    private Thread t;
    private int threadLayer;

    public void run() {
        while (ViewController.getInstance().getActualLayer()==threadLayer && ViewController.getInstance().getInMenu() == false) {
            try {
                LevelController.getInstance().checkPosition();

                this.yield();
//                Thread.sleep(50);
            } catch (Exception e) {
                // Throwing an exception 
                e.printStackTrace();
            }
        }
    }

    public void start() {
        threadLayer = ViewController.getInstance().getActualLayer();
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }
}
