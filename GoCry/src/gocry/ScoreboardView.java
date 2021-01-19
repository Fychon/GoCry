package gocry;

import java.awt.BorderLayout;
import javax.swing.*;
import java.util.ArrayList;

/*
 * Ansicht des Scoreboards, die den Usernamen anzeigt und die Bestenzeit.
   Ein zurück-button der wieder ins Hauptmenü führt.
 */
/**
 *
 * @author ahh-rief
 */
public class ScoreboardView extends JPanel {

    private JFrame frame;
    private JScrollPane scrollPane;

    
    public ScoreboardView(JFrame frame, ArrayList<ScoreboardEntry> entrys) {
        this.frame = frame;
        this.setVisible(true);
        this.setPreferredSize(frame.getSize());
        this.setSize(frame.getSize());
        createScoreboard(entrys);
    }

    public void createScoreboard(ArrayList<ScoreboardEntry> entrys) {
        JButton back = new JButton("Zurück");
        String column[] = {"Name", "Zeit", "Datum"};

        String data[][] = new String[entrys.size()][5];
        for (int i = 0; i < entrys.size(); i++) {
            data[i][0] = entrys.get(i).getName();
            data[i][1] = entrys.get(i).getGameTime();
            data[i][2] = entrys.get(i).getCreationDate();           
        }

        JTable scoreTable = new JTable(data, column);
        scoreTable.setBounds(0, 0, frame.size().width, frame.size().height);
        scrollPane = new JScrollPane(scoreTable);
        frame.setTitle("Scoreboard");

        back.addActionListener(ViewController.getInstance());
        back.setActionCommand("Zurück");
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(back);

    }

}
