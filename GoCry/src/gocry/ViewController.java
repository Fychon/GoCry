package gocry;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author johann
 */
public class ViewController implements ActionListener, ChangeListener, MouseListener {

    private MainMenuView menuView;
    private ArrayList<Layer> layers = new ArrayList();
    private static ViewController instance;
    private int actualLayer = 0;
    private String gameTime;
    
    private boolean inGameSoundOn = false;
    private boolean inMenuSoundOn = false;
    private boolean inExitSoundOn = false;
    
    private String name = "ENTER NAME";
    private Clip inGameSound;
    private Clip menuSound;
    private Clip clipTin;
    private Clip winSound;
    private Clip deathSound;
    private Clip exitSound;
    
    private boolean tinnitusIsPlaying = false;

    private boolean youCheated = false;
    
    private boolean oncePlayed = false;
    
    private ArrayList<Ghost>[] levelsGhost;
    
    private boolean inMenu = true;
    private boolean inInfoscreen = false;
    private boolean ghostsEnabled = false;
   
    private boolean gameJustStarted = true;
    
    private int levelCounter = 0;
    
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
            levelCounter++;
            actualLayer = layers.get(actualLayer).nextLayer;
            menuView.showLevel(layers.get(actualLayer).level_id);
        }
    }
    
    public void backToMenu(boolean escPressed){
        if(tinnitusIsPlaying){
            stopTinnitus();
        }
        if(inInfoscreen == false) {
            inMenu = true;
            levelsGhost[actualLayer] = new ArrayList<Ghost>(Victim.getInstance().getGhostList());
            Victim.getInstance().resetGhostList();
            oncePlayed = true;
            actualLayer = 0;
            levelCounter = 0;
            menuView.showMenuWithButton(escPressed);
        } else {
            menuView.showMenuWithButton(false);
            inInfoscreen = false;
        }
        stopInGameSound();
        try {
            playMenuSound();
        } catch (Exception ex) {
        }
        
    }
    
    
    
    public void lastLevelFinished(){
        oncePlayed = true;
        levelCounter = 0;
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
    
    public void spacePressed(){
        if(inInfoscreen){
            gameJustStarted = false;
            inInfoscreen = false;
            LevelController.getInstance().setLevelInSwitch(false);
            stopMenuSound();
            try {
                playInGameSound();
            } catch (Exception ex) {}
            menuView.showLevel(actualLayer);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        if (command.equals("Neues Spiel")) {
            if(gameJustStarted){
                menuView.showInfoScreen();
                inMenu = false;
                inInfoscreen = true;
            } else {
                inMenu = false;
                LevelController.getInstance().setLevelInSwitch(false);
                stopMenuSound();
                try {
                    playInGameSound();
                } catch (Exception ex) {
                }
                menuView.showLevel(actualLayer);
            }
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
    //        this.name = "Fred";
        }
    }
    /**
     * Aktuellen WinSound aus Db laden
     * @return 
     */
    public String getWinSound(){
        return this.layers.get(actualLayer).getWinSound();
    }
    /**
     * Aktuellen DeathSound aus Db laden
     * @return 
     */
    public String getDeathSound(){
        return this.layers.get(actualLayer).getDeathSound();
    }
    
    /**
     * Alle Soundfiles laden und in Clips abspeichern
     * @throws Exception 
     */
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
        
        File exitFile = new File("sounds/goodbye.wav");
        exitSound = AudioSystem.getClip();
        AudioInputStream exitStream = AudioSystem.getAudioInputStream(exitFile);
        exitSound.open(exitStream);
        
        File winFile = new File(getWinSound());
        winSound = AudioSystem.getClip();
        AudioInputStream winStream = AudioSystem.getAudioInputStream(winFile);
        winSound.open(winStream);
        
        File deathFile = new File(getDeathSound());
        deathSound = AudioSystem.getClip();
        AudioInputStream deathStream = AudioSystem.getAudioInputStream(deathFile);
        deathSound.open(deathStream);
    }
    /**
     * Falls noch kein Tinnitus abgespielt -> anfangen in Dauerloop
     */
    public void playTinnitus(){
        if(this.tinnitusIsPlaying == false){
            tinnitusIsPlaying = true;
            clipTin.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    /**
     * Falls aktuell Tinnitus abgespielt -> stoppen
     */
    public void stopTinnitus(){
        if(this.tinnitusIsPlaying == true){
            clipTin.stop();
            tinnitusIsPlaying = false;
        }
    }
    /**
     * Falls noch kein Exit abgespielt -> anfangen in Dauerloop
     */
    private void playExitSound() {
        if(inExitSoundOn == false){
            inExitSoundOn = true;
            exitSound.setFramePosition(0);
            exitSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    /**
     * Winsound von anfang an einmal abspielen
     */   
    public void playWinSound(){
            winSound.setFramePosition(0);
            winSound.start();
        }
    /**
     * Deathsound von anfang an einmal abspielen
     */    
    public void playDeathSound(){
            deathSound.setFramePosition(0);
            deathSound.start();
        }
    /**
     * Falls noch kein Gamesound abgespielt -> anfangen in Dauerloop
     */
    public void playInGameSound(){
        if(inGameSoundOn == false){
            inGameSoundOn = true;
            inGameSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    /**
     * Falls aktuell Gamesound abgespielt -> stoppen
     */
    public void stopInGameSound(){
        if(inGameSoundOn){
            inGameSound.stop();
            inGameSoundOn = false;
        }
    }
    /**
     * Falls noch kein Menusound abgespielt -> anfangen in Dauerloop
     */
    public void playMenuSound(){
        if(inMenuSoundOn == false){
            inMenuSoundOn = true;
            menuSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    /**
     * Falls aktuell Exitsound abgespielt wird -> stoppen
     */
    public void stopExitSound(){
        if(inExitSoundOn == true){
            exitSound.stop();
            inExitSoundOn = false;
        }
    }
    /**
     * Falls aktuell Menusound abgespielt wird, stoppen
     */                 
    public void stopMenuSound(){
        if(inMenuSoundOn == true){
            menuSound.stop();
            inMenuSoundOn = false;
        }
    }
    /**
     * Setter ob man sich aktuell im Menu befondet
     * @param in 
     */
    public void setInMenu(boolean in){
        this.inMenu = in;
    }
    /**
     * Ausgabe ob man sich aktuell im Hauptmenü befindet
     * @return 
     */
    public boolean getInMenu(){
        return this.inMenu;
    }
    /**
     * Ausgabe der GhostListe für ein Level
     * @param layerID
     * @return 
     */
    public ArrayList<Ghost> getGhostList(int layerID){
        return this.levelsGhost[layerID];
    }
    /**
     * Ausgabe aller GhostListen
     * @return 
     */
    public ArrayList<Ghost>[] getGhostListArray(){
        return this.levelsGhost;
    }
    /**
     * Wurde das Spiel bereits einmal mit esc, lose oder lastLevel beendet
     * @return 
     */
    public boolean getOncePlayed(){
        return this.oncePlayed;
    }
    /**
     * Getter ob U gedrückt wurde
     * @return 
     */
    public boolean getCheated(){
        return this.youCheated;
    }
    /**
     * Setter ob U gedrückt wurde
     * @param in 
     */
    public void setCheated(boolean in){
        this.youCheated = in;
    }
    /**
     * Setter ob Ghosts abgespielt werden sollen
     * @param in 
     */
    public void setGhostEnabled(boolean in){
        ghostsEnabled = in;
    }
    /**
     * Getter ob Ghosts abgespielt werden sollen
     * @return 
     */
    public boolean getGhostsEnabled(){
        return this.ghostsEnabled;
    }
   
    /**
     * Veränderung des Lautstärleicons
     * @param i iconBild 0 bis 3
     */
    public void changeVolumeIcon(int i){
        menuView.switchVolumeIcon(i);
    }
    
    /**
     * Veränderung der Lautstärke auf alle Clips. Veränderung des Icons in der View
     * @param level Volume von 0 bis 100
     */
    public void setGeneralVolume(int level){
        setVolume(inGameSound, level);
        setVolume(menuSound, level);
        setVolume(clipTin, level);
        setVolume(winSound, level);
        setVolume(deathSound, level);
        setVolume(exitSound, level);
        if(level == 0){
            ViewController.getInstance().changeVolumeIcon(0);
        }else if(level <= 33){
            ViewController.getInstance().changeVolumeIcon(1);
        }else if(level <= 66){
            ViewController.getInstance().changeVolumeIcon(2);
        }else{
            ViewController.getInstance().changeVolumeIcon(3);
        }
        
    }
    /**
     * Lautstärkeveränderug pro Clip
     * @param clip eingabe des Clips der verändert werden soll
     * @param level volume von 0 bis 100
     */
    public static void setVolume(Clip clip, int level) {
        Objects.requireNonNull(clip);
        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        if (volume != null) {
            float dB = (float) (Math.log(level / 100.0) / Math.log(10.0) * 20.0);
            volume.setValue(dB);
        }
        
    }
    
    /**
     * Methode wird als hoover-Effekt beim Exit Button aufgerufen,
     * das BackGround Panel im Hauuptmenü und die Musik wird angepasst
     * @param in 
     */
    public void heWantsToCry(boolean in){
        if(in){
            ViewController.getInstance().stopMenuSound();
            ViewController.getInstance().playExitSound();
            menuView.goodbyePanel(true);
        } else {
            ViewController.getInstance().stopExitSound();
            ViewController.getInstance().playMenuSound();
            menuView.goodbyePanel(false);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        setGeneralVolume(source.getValue());    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        heWantsToCry(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        heWantsToCry(false);
    }
    /**
     * Ausgabe der aktuellen Levenummer
     * @return int levelnummer
     */
    public int getLevelCounter(){
        return this.levelCounter;
    }
}
