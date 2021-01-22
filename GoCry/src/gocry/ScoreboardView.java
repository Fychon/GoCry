package gocry;

import java.awt.Font;
import javax.swing.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableCellRenderer;

/*
 * Ansicht des Scoreboards, die den Usernamen anzeigt und die Bestenzeit.
   Ein zurück-button der wieder ins Hauptmenü führt.
 */
/**
 *
 * @author ahh-rief
 */
public class ScoreboardView extends JLayeredPane {

    private JFrame frame;
    private JScrollPane scrollPane;
    private JPanel lbackGround = new JPanel();
    private JPanel lkomp = new JPanel();
    private JButton back = new JButton();


    
    public ScoreboardView(JFrame frame, ArrayList<ScoreboardEntry> entrys) {
        this.frame = frame;
        this.setVisible(true);
        this.setBounds(0,0,frame.getWidth(), frame.getHeight());
        ImageIcon imgBackMenu = new ImageIcon("textures/back.png");
        back.setIcon(imgBackMenu);
        back.setSize(imgBackMenu.getIconWidth(), imgBackMenu.getIconHeight());
        back.setBorder(null);
        back.setBorderPainted(false);
        back.setContentAreaFilled(false);
        lkomp.setVisible(true);
        lkomp.setOpaque(false);
        lkomp.setBounds(0,0,frame.getWidth(), frame.getHeight());
        lbackGround.setVisible(true);
        lbackGround.setBounds(0,0,frame.getWidth(), frame.getHeight());
        lkomp.setLayout(null);
        
        createScoreboard(entrys);
        loadBackground();
        this.add(lbackGround, new Integer(0), 0);
        this.add(lkomp, new Integer(1), 0);

    }
    
    public void loadBackground(){
            Icon icon = new ImageIcon("textures/mainmenu.png");
            JLabel label = new JLabel(icon);
            label.setBounds(this.getBounds());
            label.setVisible(true);
            lbackGround.add(label);
    }

    public void createScoreboard(ArrayList<ScoreboardEntry> entrys) {
        String column[] = {"Name", "Zeit", "Datum"};
        back.setLocation(this.getSize().width-back.getSize().width*2,this.getSize().height-back.getSize().height*2);

        String data[][] = new String[entrys.size()][5];
        for (int i = 0; i < entrys.size(); i++) {
            data[i][0] = entrys.get(i).getName();
            data[i][1] = entrys.get(i).getGameTime();
            data[i][2] = entrys.get(i).getCreationDate();           
        }

        JTable scoreTable = new JTable(data, column);
        scoreTable.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 28));
        scoreTable.getTableHeader().setOpaque(false);

        scoreTable.setFont(new Font("Monospaced", Font.PLAIN, 24));
        scoreTable.setBorder(null);
        scoreTable.setOpaque(false);
        scoreTable.setSize(500, 600);
        scoreTable.setLocation(0,0);
        scoreTable.setShowVerticalLines(true);
        scoreTable.setRowHeight(50);
        scoreTable.setShowGrid(false);
        scoreTable.setRowSelectionAllowed(false);
        scoreTable.setFocusable(false);
        ((DefaultTableCellRenderer)scoreTable.getDefaultRenderer(Object.class)).setOpaque(false);

        //scoreTable.setBounds((this.getSize().width-500) / 2, 300, 500, 600);
        scrollPane = new JScrollPane(scoreTable);
        scrollPane.setSize(500, 600);
        scrollPane.setLocation(0,0);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        frame.setTitle("Scoreboard");

        scrollPane.setBounds((this.getSize().width-600) / 2, 140, 600, 500);

        back.addActionListener(ViewController.getInstance());
        back.setActionCommand("Zurück");
        lkomp.add(scrollPane);
        lkomp.add(back);

    }

}
