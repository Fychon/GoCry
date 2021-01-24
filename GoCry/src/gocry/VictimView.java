    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */
    package gocry;

import java.awt.AlphaComposite;
    import java.awt.Color;
    import java.awt.Dimension;
    import java.awt.Font;
    import java.awt.FontMetrics;
    import java.awt.Graphics;
import java.awt.Graphics2D;
    import java.sql.SQLException;
    import java.text.SimpleDateFormat;
import java.util.ArrayList;
    import java.util.Date;
    import javax.swing.*;

    /**
     *
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
        
        public void setGhostList(ArrayList<Ghost> in){
                ghost = true;
                ghostList = in;
        }
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
        

        public void drawVictim(Graphics g) {        
            g.setColor(Color.white);
            g.drawImage(Victim.getInstance().getImage(), Victim.getInstance().getPositionX(), Victim.getInstance().getPositionY(), null);
        }
        
        public void drawGhost(Graphics g) {
            Graphics2D graphics2D = (Graphics2D)g;
            float opacity = 0.5f;
            graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            graphics2D.drawImage(ghostList.get(ghostTicker).getImage(), ghostList.get(ghostTicker).getPositionX(), ghostList.get(ghostTicker).getPositionY(), null);
        }
        
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


        public void startThread() {
            if (t2 == null) {
               t2 = new Thread(this);
               t2.start();
            }
        }

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
