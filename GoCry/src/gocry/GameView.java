/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gocry;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

/**
 *
 * @author johann
 */
public class GameView extends JLayeredPane {
    public GameView(JFrame frame, int level_id){
        Victim.getInstance().resetMovement();
        this.setBounds(0, 0, frame.getSize().width, frame.getSize().height);
        this.setPreferredSize(frame.getSize());
            
        if(level_id == 0){
                LevelController.getInstance().loadLevel();
                ViewController.getInstance().setCheated(false);
                //ViewController.getInstance().setVictimName(enterName.getText());
                LevelController.getInstance().setStartTime(System.currentTimeMillis());
            }
        
            LevelView level = new LevelView(frame, level_id);
            level.setLayout(null);
            level.setVisible(true);
            
            VictimView transPanel = new VictimView(this.getSize(), level_id);
            transPanel.setLayout(null);
            transPanel.setVisible(true);  
            
            LevelController.getInstance().setModsForLevel(level_id);

            if(ViewController.getInstance().getBeMean() && level_id == 0){
                level.setLevelName("Did we say 'continue'? Oops...");
            }

            if(ViewController.getInstance().getOncePlayed()){
                if(ViewController.getInstance().getGhostsEnabled()){
                    transPanel.setGhostListArray(ViewController.getInstance().getGhostListArray());
                }
                if(ViewController.getInstance().getGhostList(level_id)==null){
                    
                }else{
                    transPanel.setGhostList(ViewController.getInstance().getGhostList(level_id));
                }
            }
            this.add(level, new Integer(0), 0);
            this.add(transPanel, new Integer(1), 0);
    }  
}
