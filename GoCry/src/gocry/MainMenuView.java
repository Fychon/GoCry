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
        private JPanel inGameMenu = new JPanel();
        private JLayeredPane lpane = new JLayeredPane();

        private boolean inGameMenuBoolean = false;
        private ViewController controller;
        private LevelView level = new LevelView(this, 0);;



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
            
            //Panel Settings
            lmenu.setBounds(0, 0, this.getSize().width, this.getSize().height);
            lmenu.setPreferredSize(this.getSize());
            lbackGround.setBounds(0, 0, this.getSize().width, this.getSize().height);
            lmenuKomp.setBounds(0, 0, this.getSize().width, this.getSize().height);
            ltitle.setBounds(0, 0, this.getSize().width, this.getSize().height);            
            lbackGround.setVisible(true);
            lmenuKomp.setVisible(true);
            
            lbackGround.setLayout(null);
            lmenuKomp.setLayout(null);
            
            lmenuKomp.setOpaque(false);
            ltitle.setOpaque(false);
            ltitle.setVisible(true);
            lmenu.setVisible(true);

            //Load Panels from LayeredPane
            loadFirstPanel();
            loadSecPanel();
            loadThirdPanel();
            
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
                ImageIcon imgLoadGame = new ImageIcon("textures/CONTINUE.png");
                ImageIcon imgShowScore = new ImageIcon("textures/LEADERBOARD.png");
                ImageIcon imgCloseGame = new ImageIcon("textures/CLOSEGAME.png");
                newGame.setIcon(imgNewGame);
                loadGame.setIcon(imgLoadGame);
                scoreboard.setIcon(imgShowScore);
                closeGame.setIcon(imgCloseGame);
                
                //TODO BUTTONSIZE FROM ICONSIZE -> CONFIGURE POSITION
                closeGame.setSize(imgCloseGame.getIconWidth(), imgCloseGame.getIconHeight());
            }  catch (Exception ex) {
                    System.out.println(ex);
            }
            
            //Textfield Settings
            enterName.setOpaque(false);
            enterName.setFont(new Font("Monospaced", Font.BOLD, 36));
            enterName.setHorizontalAlignment(JTextField.CENTER);

            //
            newGame.setMargin(new Insets(0, 0, 0, 0));
            loadGame.setMargin(new Insets(0, 0, 0, 0));
            scoreboard.setMargin(new Insets(0, 0, 0, 0));
            closeGame.setMargin(new Insets(0, 0, 0, 0));

            
            newGame.setBorder(null);
            loadGame.setBorder(null);
            scoreboard.setBorder(null);
            enterName.setBorder(null);
            
            newGame.setOpaque(false);
            loadGame.setOpaque(false);
            scoreboard.setOpaque(false);
            
            newGame.setSize(sizeButtons);
            loadGame.setSize(sizeButtons);
            scoreboard.setSize(sizeButtons);
            enterName.setSize(sizeButtons);

            newGame.setLocation(sizeButtons.width, sizeButtons.height * 3);
            loadGame.setLocation(sizeButtons.width, (sizeButtons.height * 4) );
            scoreboard.setLocation(sizeButtons.width, (sizeButtons.height * 5));
            enterName.setLocation(sizeButtons.width, (sizeButtons.height * 6) - 10);
            closeGame.setLocation(lmenuKomp.getSize().width-closeGame.getSize().width*2,lmenuKomp.getSize().height-closeGame.getSize().height*2);

            newGame.addActionListener(controller);
            loadGame.addActionListener(controller);
            scoreboard.addActionListener(controller);
            closeGame.addActionListener(controller);
            
            newGame.setActionCommand("Neues Spiel");
            loadGame.setActionCommand("Spiel laden");
            scoreboard.setActionCommand("Scoreboard anzeigen"); 
            closeGame.setActionCommand("Exit Game");
            lmenuKomp.add(newGame);
            lmenuKomp.add(loadGame);
            lmenuKomp.add(scoreboard);
            lmenuKomp.add(enterName);
            lmenuKomp.add(closeGame);
        }
        
        public void loadSecPanel(){
            Icon icon = new ImageIcon("textures/GoCry.png");
                JLabel label = new JLabel(icon);
                label.setBounds(lmenu.getBounds());
                label.setVisible(true);
                ltitle.add(label);
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
               ViewController.getInstance().setVictimName(enterName.getText());
               LevelController.getInstance().setStartTime(System.currentTimeMillis());
            }
            this.getContentPane().removeAll();        
            lpane = new JLayeredPane();
            lpane.setBounds(0, 0, this.getSize().width, this.getSize().height);
            lpane.setPreferredSize(this.getSize());

            level = new LevelView(this, level_id);
            level.setVisible(true);

            lpane.add(level, new Integer(0), 0);     

            VictimView transPanel = new VictimView(this.getSize());

            lpane.add(transPanel, new Integer(1), 0);
            this.add(lpane);
            this.addKeyListener(LevelController.getInstance());

            //this.pack();

            level.resizeObjects();
            this.repaint();
            this.revalidate();
        }
        
        public void switchGameMenu(){
            if(inGameMenuBoolean == false){
                showGameMenu();
            } else {
                closeGameMenu();
            }
        }
        
        public void showGameMenu(){
            inGameMenuBoolean = true;
            inGameMenu = new JPanel();
            inGameMenu.setBounds(this.getSize().width/3,50,this.getSize().width/3, this.getSize().height-100);
            inGameMenu.setVisible(true);
            inGameMenu.setFocusable(true);
            Dimension sizeButtons = inGameMenu.getSize();
            sizeButtons.width = sizeButtons.width;
            sizeButtons.height = sizeButtons.height / 7;
            
            inGameMenu.setLayout(null);
            JButton pauseGame = new JButton();
            JButton backToMain = new JButton();
            JButton continueB = new JButton();
            pauseGame.setOpaque(true);
            backToMain.setOpaque(true);
            continueB.setOpaque(true);
            
            pauseGame.setActionCommand("Neues Spiel");
            backToMain.setActionCommand("Zurück");
            continueB.setActionCommand("Close inGameMenu");  
            
            try {
                ImageIcon imgNewGame = new ImageIcon("textures/NEWGAME.png");
                ImageIcon imgLoadGame = new ImageIcon("textures/CONTINUE.png");
                ImageIcon imgShowScore = new ImageIcon("textures/LEADERBOARD.png");
                pauseGame.setIcon(imgNewGame);
                backToMain.setIcon(imgLoadGame);
                continueB.setIcon(imgShowScore);
                //pauseGame.setLocation(inGameMenu.getSize().width/2 - imgNewGame.getIconWidth()/2, inGameMenu.getSize().height/2 - imgNewGame.getIconHeight()/2);
            }  catch (Exception ex) {
                    System.out.println(ex);
            }
            
            
            
            continueB.setLocation(0, sizeButtons.height * 2);
            pauseGame.setLocation(0, (sizeButtons.height * 3) );
            backToMain.setLocation(0, (sizeButtons.height * 4));
            continueB.setSize(sizeButtons);
            pauseGame.setSize(sizeButtons);
            backToMain.setSize(sizeButtons);
            continueB.setVisible(true);
            pauseGame.setVisible(true);
            backToMain.setVisible(true);           
            inGameMenu.add(continueB);
            inGameMenu.add(pauseGame);
            inGameMenu.add(backToMain);

            //panel.setOpaque(false);
            //panel.setBackground(new Color(0,0,0,190));
            lpane.add(inGameMenu, new Integer(2), 0);
            this.repaint();
            this.revalidate();
        }

        public void closeGameMenu(){
            inGameMenuBoolean = false;
            inGameMenu.setVisible(false);
            inGameMenu.setFocusable(false);
            
            this.repaint();
            this.revalidate();
        }
        public void showMenu(){
            
            
            this.getContentPane().removeAll();
            this.add(lmenu);
            this.setTitle("GoCry Hauptmenü");
            this.repaint();
            this.revalidate();
            enterName.setText(ViewController.getInstance().getVictimName());

        }
    }
