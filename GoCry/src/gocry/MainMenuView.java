package gocry;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
    /*
     * Ansicht des Hauptmenüs. Weiterführung auf Start des Spiels, Spiel laden und Scoreboard anzeigen.
     */

    /**
     *
     * @author ahh-rief
     */
    public class MainMenuView extends JFrame {
        //Globale Variablen    
        private JButton newGame = new JButton();
        private JButton loadGame = new JButton();
        private JButton scoreboard = new JButton();
        private JButton closeGame = new JButton();
        private JTextField enterName = new JTextField("ENTER NAME", 12);
        private JLayeredPane lmenu = new JLayeredPane();
        private JPanel lbackGround = new JPanel();
        private JPanel lmenuKomp = new JPanel();
        private JPanel ltitle = new JPanel();
        private JLayeredPane lpane = new JLayeredPane();
        private JSlider volume = new JSlider(JSlider.HORIZONTAL,0,100,50);

        private boolean beMean = false;
        
        private ViewController controller;
        private LevelView level = new LevelView(this, 0);;

        private ImageIcon imgLoadGameOff;
        private ImageIcon imgLoadGameOn;

        //Constructor
        public MainMenuView(ViewController controller){
            this.setUndecorated(true);             
            this.controller = controller;
            this.setFocusable(true);
            this.setResizable(false);
            this.getContentPane().setLayout(null);
            createGui();
            this.getContentPane().setLayout(null);
        }


        public void createGui(){
            //Frame Settings
            this.setTitle("GoCry Hauptmenü");
            this.setSize(LevelController.getInstance().frameWidth, LevelController.getInstance().frameHeight);
            this.setBounds(0,0,(int)LevelController.getInstance().frameWidth, (int)LevelController.getInstance().frameHeight);
            this.setVisible(true);
            this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            this.setLocationRelativeTo(null);
            this.setLayout(null);
            
            //LayeredPane Settings
            lmenu.setBounds(0, 0, this.getSize().width, this.getSize().height);
            lmenu.setPreferredSize(this.getSize());
            //Panel Settings
            lbackGround.setBounds(0, 0, this.getSize().width, this.getSize().height);
            lmenuKomp.setBounds(0, 0, this.getSize().width, this.getSize().height);
            ltitle.setBounds(0, 0, this.getSize().width, this.getSize().height);  
            
            lbackGround.setVisible(true);
            lmenuKomp.setVisible(true);
            ltitle.setVisible(true);
            
            ltitle.setLayout(null);
            lbackGround.setLayout(null);
            lmenuKomp.setLayout(null);
            
            lmenuKomp.setOpaque(false);
            ltitle.setOpaque(false);
            lmenu.setVisible(true);

            //Load Panels from LayeredPane
            loadFirstPanel();
            loadThirdPanel();
            loadSecPanel();
            
            //Add Panels to LayeredPane    
            lmenu.add(lbackGround, new Integer(0), 0); 
            lmenu.add(ltitle,  new Integer(1), 0);
            lmenu.add(lmenuKomp, new Integer(2), 0);     

            //Add LayeredPane to Frame
            this.add(lmenu);
        }
               
        
        public void loadThirdPanel(){
            Dimension sizeButtons = lmenu.getSize();
            sizeButtons.width = sizeButtons.width / 3;
            sizeButtons.height = sizeButtons.height / 7;
            
            try {
                ImageIcon imgNewGame = new ImageIcon("textures/NEWGAME.png");
                imgLoadGameOff = new ImageIcon("textures/CONTINUE_off.png");
                imgLoadGameOn = new ImageIcon("textures/CONTINUE_on.png");
                ImageIcon imgShowScore = new ImageIcon("textures/LEADERBOARD.png");
                ImageIcon imgCloseGame = new ImageIcon("textures/CLOSEGAME.png");
                newGame.setIcon(imgNewGame);
                loadGame.setIcon(imgLoadGameOff);
                scoreboard.setIcon(imgShowScore);
                closeGame.setIcon(imgCloseGame);
                
                closeGame.setSize(imgCloseGame.getIconWidth(), imgCloseGame.getIconHeight());
                loadGame.setSize(imgLoadGameOff.getIconWidth(), imgLoadGameOff.getIconHeight());
                scoreboard.setSize(imgShowScore.getIconWidth(), imgShowScore.getIconHeight());
                newGame.setSize(imgNewGame.getIconWidth(), imgNewGame.getIconHeight());

            }  catch (Exception ex) {
                    System.out.println(ex);
            }
            
            //Textfield Settings
            enterName.setOpaque(false);
            enterName.setFont(new Font("Monospaced", Font.BOLD, 36));
            enterName.setHorizontalAlignment(JTextField.CENTER);

            //VolumeSlider Settings
            volume.setOpaque(false);
            volume.setSize(150,30);
            volume.setLocation(lmenuKomp.getSize().width-(closeGame.getSize().width*2)-volume.getSize().width,
                    lmenuKomp.getSize().height+(int)(closeGame.getSize().height/2)-(closeGame.getSize().height*2)-volume.getSize().height/2);
            volume.setBorder(null);
            volume.setVisible(true);
            volume.addChangeListener(controller);
            
            newGame.setMargin(new Insets(0, 0, 0, 0));
            loadGame.setMargin(new Insets(0, 0, 0, 0));
            scoreboard.setMargin(new Insets(0, 0, 0, 0));
            closeGame.setMargin(new Insets(0, 0, 0, 0));

            
            closeGame.setBorder(null);
            newGame.setBorder(null);
            loadGame.setBorder(null);
            scoreboard.setBorder(null);
            enterName.setBorder(null);
            
            newGame.setOpaque(false);
            loadGame.setOpaque(false);
            scoreboard.setOpaque(false);
            
            enterName.setSize(sizeButtons);

            newGame.setLocation((lmenuKomp.getSize().width-newGame.getSize().width)/2, sizeButtons.height * 3);
            loadGame.setLocation((lmenuKomp.getSize().width-loadGame.getSize().width)/2, (sizeButtons.height * 4) );
            scoreboard.setLocation((lmenuKomp.getSize().width-scoreboard.getSize().width)/2, (sizeButtons.height * 5));
            enterName.setLocation((lmenuKomp.getSize().width-enterName.getSize().width)/2, (sizeButtons.height * 6) - 10);
            closeGame.setLocation(lmenuKomp.getSize().width-closeGame.getSize().width*2,lmenuKomp.getSize().height-closeGame.getSize().height*2);

            newGame.addActionListener(controller);
            loadGame.addActionListener(controller);
            scoreboard.addActionListener(controller);
            closeGame.addActionListener(controller);
            
            newGame.setActionCommand("Neues Spiel");
            loadGame.setActionCommand("Spiel laden");
            scoreboard.setActionCommand("Scoreboard anzeigen"); 
            closeGame.setActionCommand("Exit Game");
            
            loadGame.setEnabled(false);
            
            lmenuKomp.add(newGame);
            lmenuKomp.add(loadGame);
            lmenuKomp.add(scoreboard);
            lmenuKomp.add(enterName);
            lmenuKomp.add(closeGame);
            lmenuKomp.add(volume);
        }
        
        public void loadSecPanel(){
            Icon icon = new ImageIcon("textures/GoCry.gif");
            JLabel label = new JLabel(icon);
            label.setSize(icon.getIconWidth(), icon.getIconHeight());
            label.setLocation((lmenu.getSize().width-label.getSize().width)/2, 40);
            label.setVisible(true);
            Icon volumeicon = new ImageIcon("textures/volume.png");
            JLabel volumeIc = new JLabel(volumeicon);
            volumeIc.setSize(volumeicon.getIconWidth(), volumeicon.getIconHeight());
            volumeIc.setLocation(lmenuKomp.getSize().width-(closeGame.getSize().width*2)-volume.getSize().width-volumeIc.getSize().width,
                    lmenuKomp.getSize().height+(int)(closeGame.getSize().height/2)-(closeGame.getSize().height*2)-volumeIc.getSize().height/2);
            volumeIc.setVisible(true);
            ltitle.add(label);
            ltitle.add(volumeIc);
        }

        public void loadFirstPanel(){
            Icon icon = new ImageIcon("textures/mainmenu.gif");
            JLabel label = new JLabel(icon);
            label.setBounds(lmenu.getBounds());
            label.setVisible(true);
            lbackGround.add(label); 
        }
        

        public void showScoreboard() throws SQLException{
            ViewController.getInstance().setVictimName(enterName.getText());
            this.getContentPane().removeAll();
            ScoreboardView scoreboardView = new ScoreboardView(this, controller.getScoreboardEntrys());
            this.add(scoreboardView);
            this.repaint();
            this.revalidate();
        }

        public void showLevel(int level_id){
            LevelController.getInstance().setModsForLevel(level_id);
            Victim.getInstance().resetMovement();
            
            if(level_id == 0){
                ViewController.getInstance().setCheated(false);
                ViewController.getInstance().setVictimName(enterName.getText());
                LevelController.getInstance().setStartTime(System.currentTimeMillis());
            }
            this.getContentPane().removeAll();        
            lpane = new JLayeredPane();
            lpane.setBounds(0, 0, this.getSize().width, this.getSize().height);
            lpane.setPreferredSize(this.getSize());

            level = new LevelView(this, level_id);
            level.setVisible(true);

            if(beMean && level_id == 0){
                level.setLevelName("Ups, did we say continue? sorry =(");
            }
            lpane.add(level, new Integer(0), 0);     

            VictimView transPanel = new VictimView(this.getSize());

            if(ViewController.getInstance().getOncePlayed()){
                if(ViewController.getInstance().getGhostsEnabled()){
                    transPanel.setGhostListArray(ViewController.getInstance().getGhostListArray());
                }
                if(ViewController.getInstance().getGhostList(level_id)==null){
                    
                }else{
                    transPanel.setGhostList(ViewController.getInstance().getGhostList(level_id));
                }
            }    
            lpane.add(transPanel, new Integer(1), 0);
            this.add(lpane);
            this.addKeyListener(LevelController.getInstance());

            //this.pack();

            level.resizeObjects();
            this.repaint();
            this.revalidate();
        }
        
        public void showMenu(){
            this.getContentPane().removeAll();
            this.add(lmenu);
            this.setTitle("GoCry Hauptmenü");
            enterName.setText(ViewController.getInstance().getVictimName());
            this.repaint();
            this.revalidate();
        }
        public void showMenuWithButton(boolean escPressed){
            this.getContentPane().removeAll();
            this.add(lmenu);
            this.setTitle("GoCry Hauptmenü");
            if(escPressed){
                loadGame.setIcon(imgLoadGameOn);
                loadGame.setEnabled(true);
                beMean = true;
            } else {
                loadGame.setIcon(imgLoadGameOff);
                loadGame.setEnabled(false); 
                beMean = false;
            }
            enterName.setText(ViewController.getInstance().getVictimName());
            lmenuKomp.repaint();
            this.repaint();
            this.revalidate();
        }
        public void setBeMean(boolean in){
            this.beMean = in;
        }
    }
