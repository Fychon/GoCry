package gocry;

import javax.swing.SwingUtilities;

/**
 * Hauptklasse, Start des Spiels
 * @author johann
 */
public class GoCry {
    /**
     * 
     * HAUPTKLASSE-MAINMETHODE Spielstart
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ViewController viewController = ViewController.getInstance();
            }
        });
    }
}
