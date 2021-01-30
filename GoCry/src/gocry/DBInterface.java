package gocry;

/*
 * Schnittstelle zur Datenbank. Aufruf und Abspeichern von Datenbankinhalt.
 */
import java.sql.*;
import java.util.ArrayList;

/**
 * Klasse für den Zugriff auf die Datenbank.
 * Es werden benötigte Daten aus der Datenbank nach mitgegebenen Parametern oder in sortierter Form ausgegeben.
 * Scoreboardeinträge werdend er Datenbank hinzugefügt.
 * @author johann
 */
public class DBInterface {
    private static final String treibername = "org.hsqldb.jdbc.JDBCDriver";
    private static final String dbURL = "jdbc:hsqldb:file:data/CryDB;ifexists=true";
    private Connection conn;
    private static DBInterface instance;

    /**
     * Rückgabe des einzigen DBInterface Objektes, falls noch kein existiert wird ein erstellt.
     * @return 
     */
    public static DBInterface getInstance() {
        if (instance == null) {
            instance = new DBInterface();
        }
        return instance;
    }

    /**
     * Konstruktor für Schnittstellenerstellung. Es wird nach dem Datenbanktreiber gesucht.
     */
    private DBInterface() {
        try {
            Class.forName(treibername);
        } catch (ClassNotFoundException exc) {
            exc.printStackTrace();
            System.exit(-1);
        }
    }
    /**
     * Verbindung mit der Datenbank wird aufgebaut.
     * @throws SQLException 
     */
    private void connect() throws SQLException {
        conn = DriverManager.getConnection(dbURL, "admin", "");
    }
    /**
     * Bestehende Verbindung mit der Datenbank wird unterbrochen.
     * @throws SQLException 
     */
    private void close() throws SQLException {
        if (conn != null) {
            if (!conn.isClosed()) {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("SHUTDOWN");
                conn.close();
            }
        }
    }
    /**
     * Ausgabe aller bestehenden Scoreboardeinträge in der Datenbank.
     * Die Einträge sind nach der Spielzeit sortiert.
     * @return ArrayListe aller ScoreboardEntry(s)
     * @throws SQLException 
     */
    public ArrayList<ScoreboardEntry> allScoreboardEntrys() throws SQLException {
        ArrayList<ScoreboardEntry> alle = new ArrayList<ScoreboardEntry>();
        connect();
        Statement stmt = conn.createStatement();
        ResultSet rst = stmt.executeQuery("SELECT * FROM Scoreboard ORDER BY GAMETIME");
        while (rst.next()) {
            ScoreboardEntry entry = new ScoreboardEntry(
                    rst.getString("gametime"),
                    rst.getString("creationdate"),
                    rst.getString("name"));
            alle.add(entry);
        }
        close();
        return alle;
    }
    /**
     * Ausgabe aller LevelObjekte eines Levels
     * @param levelid
     * @return ArrayListe aller LevelObject(s)
     * @throws SQLException 
     */
    public ArrayList<LevelObject> allLevelObjects(int levelid) throws SQLException {
        ArrayList<LevelObject> all = new ArrayList<LevelObject>();
        connect();
        Statement stmt = conn.createStatement();
        ResultSet rst = stmt.executeQuery("SELECT * FROM Object WHERE Level_id = " + levelid);
        while (rst.next()) {
            LevelObject object = new LevelObject(
                    rst.getInt("positionx"),
                    rst.getInt("positiony"),
                    rst.getBoolean("collision"),
                    rst.getBoolean("visibility"),
                    rst.getInt("status_id"),
                    rst.getInt("textureid")
            );
            all.add(object);
        }
        close();
        return all;
    }
    /**
     * Ausgabe der Victiminformationen.
     * @param victimDesignID
     * @return
     * @throws SQLException 
     */
    public String[] getVictim(int victimDesignID) throws SQLException {
        connect();
        String[] result = new String[5];
        Statement stmt = conn.createStatement();
        ResultSet rst = stmt.executeQuery("SELECT * FROM VictimDesign WHERE VictimDesignID = " + victimDesignID);
        result[0] = rst.getString("name");
        result[1] = rst.getString("texture");
        result[2] = rst.getString("jumpSound");
        close();
        return result;
    }

    /**
     * Ausgabe aller Layer des Spiels
     * Alle Layer werden einer ArrayListe hinzugefügt und ausgegeben um die Levelwechsel zu organisieren.
     * @return
     * @throws SQLException 
     */
    public ArrayList<Layer> getAllLayer() throws SQLException {
        connect();
        ArrayList<Layer> result = new ArrayList<Layer>();
        Statement stmt = conn.createStatement();
        ResultSet rst = stmt.executeQuery("SELECT * FROM Layer");
        while (rst.next()) {
            Layer layerObject = new Layer(
                    rst.getInt("layerID"),
                    rst.getString("deathSound"),
                    rst.getString("winSound"),
                    rst.getString("backgroundSound"),
                    rst.getInt("level_id"),
                    rst.getInt("nextLayer"));
            result.add(layerObject);
        }
        close();
        return result;
    }
    
    /**
     * Ausgabe aller Level und deren Modifikationen
     * @return ArrayListe aller Level
     * @throws SQLException 
     */
    public ArrayList<Level> getAllLevel() throws SQLException {
        connect();
            ArrayList<Level> result = new ArrayList<Level>();
        Statement stmt = conn.createStatement();
        ResultSet rst = stmt.executeQuery("SELECT * FROM Level");
        while (rst.next()) {
            Level levelObject = new Level(
                    rst.getInt("levelid"),
                    rst.getString("name"),
                    rst.getInt("spawnpointx"),
                    rst.getInt("spawnpointy"),
                    rst.getDouble("movementspeed"),
                    rst.getString("backgroundtexture"),
                    rst.getDouble("gravitation"),
                    rst.getBoolean("lightsout"),
                    rst.getBoolean("soundlock"),
                    rst.getBoolean("invertcontrol"),
                    rst.getBoolean("headwind"),
                    rst.getBoolean("wtfisenabled"),
                    rst.getBoolean("killblocksarehidden"),
                    rst.getBoolean("killblocksareinvisible"),
                    rst.getBoolean("flipswitchillusion"),
                    rst.getBoolean("tinnitus"),
                    rst.getBoolean("ghostsEnabled")
            );
           result.add(levelObject);
        }
        close();
        return result;
    }
    
    /**
     * Ausgabe aller Texturenpfade für alle Objekte.
     * Texturen werden anhand StatusID und TextureID des Objektes zugeordnet.
     * @return
     * @throws SQLException 
     */
    public ArrayList<ObjectTexture> getTextures() throws SQLException {
        connect();
        ArrayList<ObjectTexture> result = new ArrayList<ObjectTexture>();
        Statement stmt = conn.createStatement();
        ResultSet rst = stmt.executeQuery("SELECT * FROM Objecttexture");
        while(rst.next()){
            ObjectTexture oTexture = new ObjectTexture(
                    rst.getString("texture"),
                    rst.getInt("status_id"),
                    rst.getInt("texture_id")
            );
            result.add(oTexture);
        }
        close();
        return result;
    }

    /**
     * Aus den mitgegeben Informationen (Spielzeit, Datum, Name) wird ein neuer ScoreboardEintrag der Datenbank in die Tabelle SCOREBOARD hinzugeügt.
     * @param gameTime      gespielte Spielzeit
     * @param creationDate  Tag an dem die Spielzeit erstellt wurde
     * @param name          name des Victims der die SPielzeit erspielt hat
     * @return              Rückgabe ob das Speichern erfolgreich gewesen ist.
     * @throws SQLException 
     */
    public boolean newScoreboardEntry(String gameTime, String creationDate, String name) throws SQLException {
        connect();
        boolean result = false;
        
        String query = "INSERT INTO SCOREBOARD (GAMETIME, CREATIONDATE, NAME)" + " values (?, ?, ?)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, gameTime);
        preparedStmt.setString(2, creationDate);
        preparedStmt.setString(3, name);
        preparedStmt.execute();            
        close();
        
        return true;
    }
}
