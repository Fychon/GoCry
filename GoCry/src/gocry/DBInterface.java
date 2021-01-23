package gocry;

/*
 * Schnittstelle zur Datenbank. Aufruf und Abspeichern von Datenbankinhalt.
 */
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author johann
 */
public class DBInterface {
    private static final String treibername = "org.hsqldb.jdbc.JDBCDriver";
    private static final String dbURL = "jdbc:hsqldb:file:data/CryDB;ifexists=true";
    private Connection conn;
    private static DBInterface instance;

    public static DBInterface getInstance() {
        if (instance == null) {
            instance = new DBInterface();
        }
        return instance;
    }

    private DBInterface() {     
        try {
            Class.forName(treibername);
        } catch (ClassNotFoundException exc) {
            exc.printStackTrace();
            System.exit(-1);
        }
    }

    private void connect() throws SQLException {
        conn = DriverManager.getConnection(dbURL, "admin", "");
    }

    private void close() throws SQLException {
        if (conn != null) {
            if (!conn.isClosed()) {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("SHUTDOWN");
                conn.close();
            }
        }
    }

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
