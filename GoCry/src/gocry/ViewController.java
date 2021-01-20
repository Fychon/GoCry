package gocry;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.SwingUtilities;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author johann
 */
public class ViewController implements ActionListener {

    private MainMenuView menuView;
    private ArrayList<Layer> layers = new ArrayList();
    private static ViewController instance;
    private int actualLayer = 0;
    private String gameTime;
    private boolean inGameSoundOn = false;
    private boolean inMenuSoundOn = false;
    private String name = "ENTER NAME";
    private Clip inGameSound;
    private Clip menuSound;

    private boolean youCheated = false;
    
    private boolean oncePlayed = false;
    
    private ArrayList<Ghost>[] levelsGhost;

    
    private boolean inMenu = true;
    private boolean ghostsEnabled = false;
    
    
    public ViewController() {
        menuView = new MainMenuView(this);
        menuView.setVisible(true);
        try {
            playMenuSound();
        } catch (Exception ex) {
        }
        try {
            layers = DBInterface.getInstance().getAllLayer();
            actualLayer = layers.get(0).level_id;
        } catch (SQLException ex) {
            
        }
        
        levelsGhost = new ArrayList[layers.size()];


    }
    
    public static ViewController getInstance() {
        if (instance == null) {
            instance = new ViewController();
        }
        return instance;
    }

    public ArrayList<ScoreboardEntry> getScoreboardEntrys() throws SQLException {
        return DBInterface.getInstance().allScoreboardEntrys();
    }

    public void nextLayer(){
        levelsGhost[actualLayer]= new ArrayList<Ghost>(Victim.getInstance().getGhostList());
        Victim.getInstance().resetGhostList();
            try {
                inGameSound.stop();
                //Thread.sleep(1000);
                playWinSound();
                //Thread.sleep(1000);
                inGameSound.start();
            } catch (Exception ex) {
            }
            if(layers.get(actualLayer).nextLayer==0){
                SimpleDateFormat format = new SimpleDateFormat("mm:ss.SSS");
                gameTime = format.format(new Date(System.currentTimeMillis() - LevelController.getInstance().getStartTime()));
                lastLevelFinished();
            } else {
                actualLayer = layers.get(actualLayer).nextLayer;
                menuView.showLevel(layers.get(actualLayer).level_id);
            }
            //if(actualLayer != 0){
            //   menuView.showLevel(layers.get(actualLayer).level_id);
            //} else {
            //    SimpleDateFormat format = new SimpleDateFormat("mm:ss.SSS");
            //    gameTime = format.format(new Date(System.currentTimeMillis() - LevelController.getInstance().getStartTime()));
            //    lastLevelFinished();
            //}
    }
    
    public void backToMenu(){
        inMenu = true;
        levelsGhost[actualLayer] = new ArrayList<Ghost>(Victim.getInstance().getGhostList());
        Victim.getInstance().resetGhostList();
        oncePlayed = true;
        actualLayer = 0;
        menuView.showMenu();
        stopInGameSound();
        try {
            playMenuSound();
        } catch (Exception ex) {
        }
        
    }
    
    
    
    public void lastLevelFinished(){
        oncePlayed = true;
        actualLayer = 0;
        if(this.youCheated==false){
            try {
                DBInterface.getInstance().newScoreboardEntry(gameTime, LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), name);
            } catch (SQLException ex){
                System.out.println("Konnte nicht gespeichert werden.");
            }
        }
        menuView.showMenu();
        inGameSound.stop();
        try {
            playMenuSound();
        } catch (Exception ex) {
        }  
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        if (command.equals("Neues Spiel")) {
            inMenu = false;
            LevelController.getInstance().setLevelInSwitch(false);
            stopMenuSound();
            try {
                playInGameSound();
            } catch (Exception ex) {
            }
            menuView.showLevel(actualLayer);
        }

        if (command.equals("Spiel laden")) {
            inMenu = false;
            LevelController.getInstance().setLevelInSwitch(false);
            stopMenuSound();
            try {
                playInGameSound();
            } catch (Exception ex) {
            }
            menuView.showLevel(actualLayer);

        }

        if (command.equals("Scoreboard anzeigen")) {
            try {
                menuView.showScoreboard();
            } catch (SQLException ex) {
            }

        }
        if (command.equals("Zurück")) {
            backToMenu();
        }
        if (command.equals("Exit Game")) {
            System.exit(0);
            return;
        }
    }
    
    public int getActualLayer(){
        return this.actualLayer;
    }
    
    public String getVictimName(){
        return this.name;
    }
    
    public void setVictimName(String name){
        if(!name.equals("ENTER NAME")){
            if(!name.equals(this.name))
                this.name = name;
            if(name.equals("")|| name == null){
                return;
            }
        }
        if(name.equals("ENTER NAME")){
            if(this.name != null){
                return;
            } else {
                this.name = "Fred";
            }
        }
    }
    
    public String getWinSound(){
        return this.layers.get(actualLayer).getWinSound();
    }
    public String getDeathSound(){
        return this.layers.get(actualLayer).getDeathSound();
    }
    
    public void playWinSound() throws Exception{
            File file = new File(getWinSound());
            Clip clip = AudioSystem.getClip();
            // getAudioInputStream() also accepts a File or InputStream
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            clip.open(ais);
            clip.start();
        }
        
    public void playDeathSound() throws Exception{
            File file = new File(getDeathSound());
            Clip clip = AudioSystem.getClip();
            // getAudioInputStream() also accepts a File or InputStream
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            clip.open(ais);
            clip.start();
        }
    
    public void playInGameSound() throws Exception{
        if(inGameSoundOn == false){
            inGameSoundOn = true;
            File file = new File("sounds/toto_ingame.wav");
            inGameSound = AudioSystem.getClip();
            // getAudioInputStream() also accepts a File or InputStream
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            inGameSound.open(ais);
            inGameSound.loop(Clip.LOOP_CONTINUOUSLY);
            SwingUtilities.invokeLater(() -> {
                // A GUI element to prevent the Clip's daemon Thread
                // from terminating at the end of the main()
                //JOptionPane.showMessageDialog(null, "Close to exit!");
            });
        }
    }
    
    public void stopInGameSound(){
        if(inGameSoundOn){
            inGameSound.stop();
            inGameSoundOn = false;
        }
    }
    
    public void playMenuSound() throws Exception{
        if(inMenuSoundOn == false){
            inMenuSoundOn = true;
            File file = new File("sounds/moderntalking_mainmenu.wav");
            menuSound = AudioSystem.getClip();
            // getAudioInputStream() also accepts a File or InputStream
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            menuSound.open(ais);
            menuSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

                       
    public void stopMenuSound(){
        if(inMenuSoundOn == true){
            menuSound.stop();
            inMenuSoundOn = false;
        }
    }
    
    public void setInMenu(boolean in){
        this.inMenu = in;
    }
    public boolean getInMenu(){
        return this.inMenu;
    }
    public ArrayList<Ghost> getGhostList(int layerID){
        return this.levelsGhost[layerID];
    }
    public ArrayList<Ghost>[] getGhostListArray(){
        return this.levelsGhost;
    }
    public boolean getOncePlayed(){
        return this.oncePlayed;
    }
    
    public boolean getCheated(){
        return this.youCheated;
    }
    public void setCheated(boolean in){
        this.youCheated = in;
    }
    public void setGhostEnabled(boolean in){
        ghostsEnabled = in;
    }
    public boolean getGhostsEnabled(){
        return this.ghostsEnabled;
    }
}
