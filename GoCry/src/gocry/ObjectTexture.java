package gocry;

/**
 * Modelklasse für Texturen aller Objekte
 * Objekte werden mittels Status_ID und Texture_ID mit der zuegehörigen
 * Texture (als Path to Texture) versehen. Eine Lister dieser Objekte
 * wird aus der Datenbank erstellt und zu Anzeige der LevelView verwendet.
 * @author johann
 */
public class ObjectTexture {
    private String texturePath;
    private int status_id;
    private int texture_id;
    
    /**
     * Erstellung eines Textureeintrages.
     * @param texturePath   Pfad zur Bilddatei
     * @param status_id     Neutral/Kill/Win
     * @param texture_id    ID Gegenschlüssen in Spielblock selbst vermerkt
     */
    public ObjectTexture(String texturePath, int status_id, int texture_id){
        this.texturePath = texturePath;
        this.status_id = status_id;
        this.texture_id = texture_id;
    }
    /**
     * Rückgabe des Pfades zur Texture
     * @return 
     */
    public String getTexture(){
        return this.texturePath;
    }
    /**
     * Rückgabe der TextureID
     * @return 
     */
    public int getTextureId(){
        return this.texture_id;
    }
    /**
     * Rückgabe der StatusID (0:neutral; 1: kill; 2: win)
     * @return 
     */
    public int getStatusId(){
        return this.status_id;
    }
}
