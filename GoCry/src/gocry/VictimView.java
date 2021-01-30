    package gocry;

    import java.awt.AlphaComposite;
    import java.awt.Color;
    import java.awt.Dimension;
    import java.awt.Font;
    import java.awt.FontMetrics;
    import java.awt.Graphics;
    import java.awt.Graphics2D;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import javax.swing.*;

    /**
     * Anzeige aller Objekte (Victim, Timer, Ghost(s)) auf einem Panel die in möglichst kleinen Intervallen
     * repaintet werden sollen. Repaint zur Ausgabenaktualisierung wird regelmäßig durch Thread aufgerufen.
     * Teil des JLayeredPane GameView.
     * @author johann
     */
    public class VictimView extends JPanel implements Runnable{
        private Thread t2;
        private ArrayList<Ghost> ghostList = new ArrayList<Ghost>();
        private ArrayList<Ghost>[] ghostListArray;

        Font f = new Font("Dialog", Font.PLAIN, 38);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss.SSS");
        Date dateTime = new Date();
        private int threadLayer;
        private int ghostTicker = 0;
        private boolean ghost = false;
        private boolean ghostModification = false;
        private int biggestRecord;
        
        private String gametime;
        
        /**
         * Erstellung der VictimView
         * @param size
         * @param level 
         */
        public VictimView(Dimension size, int level) {
            this.setBounds(0, 0, size.width, size.height);
            this.setPreferredSize(size);
            this.setOpaque(false);
            this.setVisible(true);
            this.setFocusable(true);
            this.setLayout(null);
            
            LevelController.getInstance().setVictim(level);

            threadLayer = ViewController.getInstance().getActualLayer();
            startThread();

            GameLoop loop = new GameLoop();
            loop.start();

        }
        /**
         * Setzen der GhostListe
         * @param in 
         */
        public void setGhostList(ArrayList<Ghost> in){
                ghost = true;
                ghostList = in;
        }
        /**
         * GhostListe wird nach Fehlern durchgegangen.
         * biggestRecord (längster GhostEintrag) wird gesucht.
         * @param in 
         */
        public void setGhostListArray(ArrayList<Ghost>[] in){
                ghostModification = true;
                int i = 0;
                for(ArrayList<Ghost> g : in) {
                    if(g != null)i++;
                }
                ghostListArray = new ArrayList[i];
                i = 0;
                biggestRecord = 0;
                for(ArrayList<Ghost> g : in){
                    if(g != null){
                        if(g.size() > biggestRecord){
                            biggestRecord = g.size();
                        }
                        ghostListArray[i] = g;
                        i++;
                    }
                }
        }
        
        /**
         * Zeichnet den gespielten Victim ohne Transparenzveränderung.
         * @param g 
         */
        public void drawVictim(Graphics g) {        
           g.drawImage(Victim.getInstance().getImage(), Victim.getInstance().getPositionX(), Victim.getInstance().getPositionY(), null);
        }
        /**
         * RecapGhost-Zeichnung. Zeichnet ein Ghost  in 50% Transparenz.
         * @param g 
         */
        public void drawGhost(Graphics g) {
            Graphics2D graphics2D = (Graphics2D)g;
            float opacity = 0.5f;
            graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            graphics2D.drawImage(ghostList.get(ghostTicker).getImage(), ghostList.get(ghostTicker).getPositionX(), ghostList.get(ghostTicker).getPositionY(), null);
        }
        /**
         * Modification Ghosts für LuckyLuke. Es werden alle vorhandenen Ghost 
         * in der GhostList mit 50% Transparenz gezeichnet.
         * Sicherstellung das die einzelnen GhostLists nicht überladen werden.
         * @param g 
         */
        public void drawGhosts(Graphics g) {
            Graphics2D graphics2D = (Graphics2D)g;
            float opacity = 0.5f;
            graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            for(int a = 0; a < ghostListArray.length; a++){
                if(!(ghostTicker >= ghostListArray[a].size())){
                    graphics2D.drawImage(ghostListArray[a].get(ghostTicker).getImage(), ghostListArray[a].get(ghostTicker).getPositionX(), ghostListArray[a].get(ghostTicker).getPositionY(), null);
                }
            }
        }
        /**
         * Zeichnen des aktuellen Timers in aktuelle Graphic
         * @param g 
         */
        public void drawTimer(Graphics g){
            Graphics2D graphics2D = (Graphics2D)g;
            graphics2D.setFont(f);
            graphics2D.setColor(Color.white);
            float opacity = 1.0f;
            graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            FontMetrics myFM = g.getFontMetrics();
            int textBreite = myFM.stringWidth(gametime);
            int textHoehe = myFM.getHeight();
            graphics2D.drawString(gametime, (this.getSize().width-textBreite-5) , textHoehe + 4);
        }
        /**
         * Zeichnen aller Komponenten
         * @param g 
         */
        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            drawTimer(g);
            drawVictim(g);

            if(ghost || ghostModification){
                if(ghostModification){
                    drawGhosts(g);
                    ghostTicker++;
                } else {
                    if(!(ghostTicker>=ghostList.size())){
                        drawGhost(g);
                        ghostTicker++;
                    } else {
                        ghost = false;
                    }
                }
            }
            
        }

        /**
         * Erstellung und Start des Threads
         */
        public void startThread() {
            if (t2 == null) {
               t2 = new Thread(this);
               t2.start();
            }
        }
        /**
         * Run Method, wird beim Levelladen gestartet und durchgehend im Spiel aufgeführt. Wird bei LevelEnde beendet.
         */
        @Override
        public void run() {
            while (ViewController.getInstance().getActualLayer()==threadLayer && ViewController.getInstance().getInMenu() == false) {
                    try {
                        gametime = format.format(new Date(System.currentTimeMillis() - LevelController.getInstance().getStartTime()));
                        repaint();
                        Victim.getInstance().calcMovement();
                        //Thread.yield();
                        Thread.sleep(10);
                    } catch (Exception e) {
                        // Throwing an exception 
                        e.printStackTrace();
                    }
                }
            }
    }
