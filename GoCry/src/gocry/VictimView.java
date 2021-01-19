    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */
    package gocry;

    import java.awt.Color;
    import java.awt.Dimension;
    import java.awt.Font;
    import java.awt.FontMetrics;
    import java.awt.Graphics;
    import java.sql.SQLException;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    import javax.swing.*;

    /**
     *
     * @author johann
     */
    public class VictimView extends JPanel implements Runnable{
        private Thread t2;
        Font f = new Font("Dialog", Font.PLAIN, 38);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss.SSS");
        Date dateTime = new Date();
        private int threadLayer;

        private String gametime;

        public VictimView(Dimension size) {
            this.setBounds(0, 0, size.width, size.height);
            this.setPreferredSize(size);
            this.setOpaque(false);
            this.setVisible(true);
            this.setFocusable(true);

            try {
                LevelController.getInstance().setVictim(0);
            } catch (SQLException ex) {

            }

            threadLayer = ViewController.getInstance().getActualLayer();
            startThread();

            GameLoop loop = new GameLoop();
            loop.start();




        }

        public void drawVictim(Graphics g) {        

            g.setColor(Color.white);
            g.drawImage(Victim.getInstance().getImage(), Victim.getInstance().getPositionX(), Victim.getInstance().getPositionY(), null);
            //g.fillRect(Victim.getInstance().getPositionX(), Victim.getInstance().getPositionY(), Victim.getInstance().getWidth(), Victim.getInstance().getHeight());
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            drawVictim(g);

            g.setFont(f);
            FontMetrics myFM = g.getFontMetrics();
            int textBreite = myFM.stringWidth(gametime);
            int textHoehe = myFM.getHeight();
            g.drawString(gametime, (this.getSize().width-textBreite-2) , textHoehe + 2);
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
