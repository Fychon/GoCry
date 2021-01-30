package gocry;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;

    /**
     * Erstellung des allgemeinen Fensters(JFrame) auf feste Spielgröße(1280x720).
     * Erstellen, Laden und Speichern aller drei Panels und deren Komponenten des Hauptmenüs.
     * Verweis bei Button-Action (Klick o. Hoover-Effekt) auf ViewController.
     * @author ahh-rief
     */
    public class MainMenuView extends JFrame {
        //Globale Variablen - Komponenten Panel 3   
        private JButton newGame = new JButton();
        private JButton loadGame = new JButton();
        private JButton scoreboard = new JButton();
        private JButton closeGame = new JButton();
        private JTextField enterName = new JTextField("ENTER NAME", 12);
        private JSlider volume = new JSlider(JSlider.HORIZONTAL,0,100,50);

        
        private JLayeredPane lmenu = new JLayeredPane();
        private JPanel lbackGround = new JPanel();
        private JPanel lmenuKomp = new JPanel();
        private JPanel ltitle = new JPanel();
        
        private ViewController controller;
        private ImageIcon imgLoadGameOff;
        private ImageIcon imgLoadGameOn;

        private ImageIcon imgCloseGameOn;
        private ImageIcon imgCloseGameOff;
        
        private ImageIcon volumeIcons[] = new ImageIcon[4];
        private JLabel volumeLabel;
        
        /**
         * Konstruktor für Hauptmenü. Größe wird festgelegt und ist nicht veränderbar.
         * Entfernen eines vorhandenen Layout-Managers für Pixelgenau GUI Gestaltung.
         * @param controller 
         */
        public MainMenuView(ViewController controller){
            this.setUndecorated(true);             
            this.controller = controller;
            this.setFocusable(true);
            this.setResizable(false);
            this.getContentPane().setLayout(null);
            createGui();
            this.getContentPane().setLayout(null);
        }

        /**
         * Erstellung des Hauptmenüs. Es werden erst die allgemeinen Frame Einstellungen geladen.
         * Danach werden die drei für das Hauptmenü benötigte Panel-Einstellungen geladen und mittels der 3 loadPanel() Methoden
         * der Inhalt dieser Panels. Die drei Panel werdem als LayeredPane im Fenster angezeigt.
         *
         */
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
               
        /**
         * Erstellung und Hinzufügen der Komponenten für das dritte (oberste) Panel.
         * Dieses Panel ist für die Userinteraktionen (Knöpfe, Slider, Textfelder) zuständig.
         * Größe und Position der Komponenten werden fest gesetzt. 
         * F+r eine gute Darstellung und da  überall mit Texturen und Icons gearbeitet wird, wird
         * die Sichtbarkeit der Kompoenten mangepasst und der Hintergrund entfernt.
         */
        public void loadThirdPanel(){
            Dimension sizeButtons = lmenu.getSize();
            sizeButtons.width = sizeButtons.width / 3;
            sizeButtons.height = sizeButtons.height / 7;
            
            try {
                volumeIcons[0] = new ImageIcon("textures/volume_0.png");
                volumeIcons[1] = new ImageIcon("textures/volume_30.png");
                volumeIcons[2] = new ImageIcon("textures/volume_60.png");
                volumeIcons[3] = new ImageIcon("textures/volume_100.png");
                ImageIcon imgNewGame = new ImageIcon("textures/NEWGAME.png");
                imgLoadGameOff = new ImageIcon("textures/CONTINUE_off.png");
                imgLoadGameOn = new ImageIcon("textures/CONTINUE_on.png");
                ImageIcon imgShowScore = new ImageIcon("textures/LEADERBOARD.png");
                imgCloseGameOn = new ImageIcon("textures/exitGameButton2.gif");
                imgCloseGameOff = new ImageIcon("textures/exitGameButton.png");
                newGame.setIcon(imgNewGame);
                loadGame.setIcon(imgLoadGameOff);
                scoreboard.setIcon(imgShowScore);
                closeGame.setIcon(imgCloseGameOff);
                
                closeGame.setSize(imgCloseGameOff.getIconWidth(), imgCloseGameOff.getIconHeight());
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
            
            closeGame.setBorderPainted(false);
            closeGame.setContentAreaFilled(false);
            newGame.setBorderPainted(false);
            newGame.setContentAreaFilled(false);
            loadGame.setBorderPainted(false);
            loadGame.setContentAreaFilled(false);
            scoreboard.setBorderPainted(false);
            scoreboard.setContentAreaFilled(false);
            
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
            closeGame.addMouseListener(controller);
            
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
        
        /**
         * Erstellung und Laden der Komponenten für das zweite Panel (in der Mitte).
         * Das Panel zeigt den Titel (GoCry) als Gif an (Wechsel von Schwarz auf Gelb) und ist zusätzlich
         * für die Anzeige des sich veränderen Volumeicons zuständig.
         */
        public void loadSecPanel(){
            Icon icon = new ImageIcon("textures/GoCry.gif");
            JLabel label = new JLabel(icon);
            label.setSize(icon.getIconWidth(), icon.getIconHeight());
            label.setLocation((lmenu.getSize().width-label.getSize().width)/2, 40);
            label.setVisible(true);
            volumeLabel = new JLabel(volumeIcons[2]);
            volumeLabel.setSize(volumeIcons[0].getIconWidth(), volumeIcons[0].getIconHeight());
            volumeLabel.setLocation(lmenuKomp.getSize().width-(closeGame.getSize().width*2)-volume.getSize().width-volumeLabel.getSize().width,
                    lmenuKomp.getSize().height+(int)(closeGame.getSize().height/2)-(closeGame.getSize().height*2)-volumeLabel.getSize().height/2);
            volumeLabel.setVisible(true);
            ltitle.add(label);
            ltitle.add(volumeLabel);
        }

        /**
         * Erstellung und Laden der Komponenteneingeschaften des ersten Hintergrund-Panels.
         * Dieses Panel ist das "Intro" und zeigt die Hintergrundanimation als GIF an.
         * Das Bild wird über die komplette Framegröße gelegt und wird automatisch als Endless-Loop abgespielt.
         */
        public void loadFirstPanel(){
            Icon icon = new ImageIcon("textures/mainmenu.gif");
            JLabel label = new JLabel(icon);
            label.setBounds(lmenu.getBounds());
            label.setVisible(true);
            lbackGround.add(label); 
        }
        
        /**
         * Anzeigen des Infoscreens beim ersten Spielstart.
         * Alle aktuellen Komponenten werden entfernt und der InfoScreen wird
         * als Bild in einem JPanel vollständig über das Fenster gelegt.
         */
        public void showInfoScreen(){
            this.getContentPane().removeAll();        
            JPanel infoPanel = new JPanel();
            infoPanel.setBounds(0,0,this.getSize().width, this.getSize().height);
            Icon icon = new ImageIcon("textures/infoscreen.png");
            JLabel label = new JLabel(icon);
            label.setBounds(infoPanel.getBounds());
            label.setVisible(true);
            this.addKeyListener(LevelController.getInstance());
            infoPanel.add(label);
            this.add(infoPanel);
            this.repaint();
        }
        
        /**
         * Methode für Anzeige des Hoover-Effect (an - true; aus - false).
         * Das Background Bild aus dem HintergrundPanels des MainMenüs wird gewechselt.
         * Das Icon des ExitButtons wird als Animation abgespielt.
         * @param in   Hoover an / aus
         */
        public void goodbyePanel(boolean in){
            lbackGround.removeAll();
            if(in){
                closeGame.setIcon(imgCloseGameOn);
                Icon icon = new ImageIcon("textures/exitGame.gif");
                JLabel label = new JLabel(icon);
                label.setBounds(lmenu.getBounds());
                label.setVisible(true);
                lbackGround.add(label);
            } else {
                closeGame.setIcon(imgCloseGameOff);
                Icon icon = new ImageIcon("textures/mainmenu.gif");
                JLabel label = new JLabel(icon);
                label.setBounds(lmenu.getBounds());
                label.setVisible(true);
                lbackGround.add(label);
            }
            lmenuKomp.repaint();
            lbackGround.repaint();
        }
        
        /**
         * Erstellung und Hinzugügung einer ScoreBoard View.
         * Bei Erstellung werden alle Einträge aus der Datenbank geladen und mitgegeben.
         * 
         * @throws SQLException 
         */
        public void showScoreboard() throws SQLException{
            ViewController.getInstance().setVictimName(enterName.getText());
            this.getContentPane().removeAll();
            ScoreboardView scoreboardView = new ScoreboardView(this, controller.getScoreboardEntrys());
            this.add(scoreboardView);
            this.repaint();
            this.revalidate();
        }

        /**
         * Erstellung und Anzeige einer GameView.
         * Laden und Anzeige der benötigten Spieleobjekt (LevelView) und des
         * Spielers (VictimView) passend zum mitgegebenen Level.
         * @param level_id          
         */
        public void showLevel(int level_id){
            this.getContentPane().removeAll();        
            ViewController.getInstance().setVictimName(enterName.getText());
            GameView game = new GameView(this, level_id);
            game.setLayout(null);
            game.setBounds(0, 0, this.getSize().width, this.getSize().height);
            this.setVisible(true);
            this.add(game);
            this.addKeyListener(LevelController.getInstance());


            this.repaint();
            this.revalidate();
        }
        
        /**
         * Wechsel des VolumeIcons im 2. Panel von 0-3 (0%;33%;66%;100%), um die Lautstärke zu visualisieren.
         * @param i 
         */
        public void switchVolumeIcon(int i){
            volumeLabel.setIcon(volumeIcons[i]);
            ltitle.repaint();
        }
        /**
         * Methode zum Anzeigen des Hauptmenü. Wird beim Rückgang in das Hauptmenü 
         * (z.B. von der Scorebaordansicht aus) aufgerufen. Entfernung aller aktuellen Komponenten
         * und Ansicht der gespeicherten LayeredPane für die Panels des Hauptmenüs
         */
        public void showMenu(){
            this.getContentPane().removeAll();
            this.add(lmenu);
            this.setTitle("GoCry Hauptmenü");
            enterName.setText(ViewController.getInstance().getVictimName());
            this.repaint();
            this.revalidate();
        }
        /**
         * Ansicht des Hauptmenüs mit deaktivierten o. aktivierten Continue Button. Falls der mitgegebnen Parameter ecPressed true ist wird der
         * nichtsnützige Continue Button aktiviert und ein anderes Bild verwendet. Im anderen Falle wird er deaktiviert und das ausgegraute Bild verwendet.
         * @param escPressed 
         */
        public void showMenuWithButton(boolean escPressed){
            this.getContentPane().removeAll();
            this.add(lmenu);
            this.setTitle("GoCry Hauptmenü");
            if(escPressed){
                loadGame.setIcon(imgLoadGameOn);
                loadGame.setEnabled(true);
                ViewController.getInstance().setBeMean(true);
            } else {
                loadGame.setIcon(imgLoadGameOff);
                loadGame.setEnabled(false); 
                ViewController.getInstance().setBeMean(false);
            }
            enterName.setText(ViewController.getInstance().getVictimName());
            lmenuKomp.repaint();
            this.repaint();
            this.revalidate();
        }
    }
