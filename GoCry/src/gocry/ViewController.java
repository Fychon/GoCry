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
import java.util.Objects;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author johann
 */
public class ViewController implements ActionListener, ChangeListener {

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
    private Clip clipTin;
    private Clip winSound;
    private Clip deathSound;
    
    private boolean tinnitusIsPlaying = false;

    private boolean youCheated = false;
    
    private boolean oncePlayed = false;
    
    private ArrayList<Ghost>[] levelsGhost;

    
    private boolean inMenu = true;
    private boolean ghostsEnabled = false;
    
    
    public ViewController() {
        menuView = new MainMenuView(this);
        menuView.setVisible(true);
        try {
            layers = DBInterface.getInstance().getAllLayer();
            actualLayer = layers.get(0).level_id;
        } catch (SQLException ex) {
            
        }
        try {
            loadClips();
            playMenuSound();
        } catch (Exception ex) {
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
        menuView.setBeMean(false);
        if(tinnitusIsPlaying){
            stopTinnitus();
        }
        
        levelsGhost[actualLayer]= new ArrayList<Ghost>(Victim.getInstance().getGhostList());
        Victim.getInstance().resetGhostList();
        try {
            playWinSound();
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
    }
    
    public void backToMenu(boolean escPressed){
        if(tinnitusIsPlaying){
            stopTinnitus();
        }
        
        inMenu = true;
        levelsGhost[actualLayer] = new ArrayList<Ghost>(Victim.getInstance().getGhostList());
        Victim.getInstance().resetGhostList();
        oncePlayed = true;
        actualLayer = 0;
        menuView.showMenuWithButton(escPressed);
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
            backToMenu(false);
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
    
    
    private void loadClips() throws Exception{
        File file = new File("sounds/tinnitus.wav");
        clipTin = AudioSystem.getClip();
        AudioInputStream tinnitusStream = AudioSystem.getAudioInputStream(file);
        clipTin.open(tinnitusStream);
                
        File inGameFile = new File("sounds/toto_ingame.wav");
        inGameSound = AudioSystem.getClip();
        AudioInputStream inGameStream = AudioSystem.getAudioInputStream(inGameFile);
        inGameSound.open(inGameStream);
        
        File menuFile = new File("sounds/moderntalking_mainmenu.wav");
        menuSound = AudioSystem.getClip();
        AudioInputStream menuStream = AudioSystem.getAudioInputStream(menuFile);
        menuSound.open(menuStream);
        
        File winFile = new File(getWinSound());
        winSound = AudioSystem.getClip();
        AudioInputStream winStream = AudioSystem.getAudioInputStream(winFile);
        winSound.open(winStream);
        
        File deathFile = new File(getDeathSound());
        deathSound = AudioSystem.getClip();
        AudioInputStream deathStream = AudioSystem.getAudioInputStream(deathFile);
        deathSound.open(deathStream);
    }
    
    public void playTinnitus(){
        if(this.tinnitusIsPlaying == false){
            tinnitusIsPlaying = true;
            clipTin.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    public void stopTinnitus(){
        if(this.tinnitusIsPlaying == true){
            clipTin.stop();
            tinnitusIsPlaying = false;
        }
    }
    
    public void playWinSound(){
            winSound.setFramePosition(0);
            winSound.start();
        }
        
    public void playDeathSound(){
            deathSound.setFramePosition(0);
            deathSound.start();
        }
    
    public void playInGameSound(){
        if(inGameSoundOn == false){
            inGameSoundOn = true;
            inGameSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    public void stopInGameSound(){
        if(inGameSoundOn){
            inGameSound.stop();
            inGameSoundOn = false;
        }
    }
    
    public void playMenuSound(){
        if(inMenuSoundOn == false){
            inMenuSoundOn = true;
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
    
    public void setGeneralVolume(int level){
        setVolume(inGameSound, level);
        setVolume(menuSound, level);
        setVolume(clipTin, level);
        setVolume(winSound, level);
        setVolume(deathSound, level);
    }
    //DUPLICATED FROM
    public static void setVolume(Clip clip, int level) {
        //Objects.requireNonNull(clip);
        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        if (volume != null) {
            float dB = (float) (Math.log(level / 100.0) / Math.log(10.0) * 20.0);
            volume.setValue(dB);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        setGeneralVolume(source.getValue());        
    }
}
