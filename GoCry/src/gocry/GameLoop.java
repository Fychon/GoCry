package gocry;

/**
 * GameLoop sorgt für die konstanten Prüfungen die nicht vom Userinput abhängig sind (aktuell nur die Gravitation)
 * Ruft so schnell wie möglich die @link LevelController.checkPosition() Methode auf und prüft ob das Victim aktuell falles soll oder nicht. (- Gravitationsüberprüfung)
 * Gravitationsauswirkung wird im LevelController durchgeführt.
 * @author johann
 */
public class GameLoop extends Thread {

    private Thread t;
    private int threadLayer;

    /**
     * ThreadRun Methode, diese wird dauerhaft ausgeführt bis ein Levelwechsel durchgeführt wird oder der User in das Menü zurück geht.
     */
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
    /**
     * Erstellung und Start des Threads
     */
    public void start() {
        threadLayer = ViewController.getInstance().getActualLayer();
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }
}
