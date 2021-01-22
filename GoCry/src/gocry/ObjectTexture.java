/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gocry;

/**
 *
 * @author johann
 */
public class ObjectTexture {
    private String texturePath;
    private int status_id;
    private int texture_id;
    
    public ObjectTexture(String texturePath, int status_id, int texture_id){
        this.texturePath = texturePath;
        this.status_id = status_id;
        this.texture_id = texture_id;
    }
    
    public String getTexture(){
        return this.texturePath;
    }
    public int getTextureId(){
        return this.texture_id;
    }
    public int getStatusId(){
        return this.status_id;
    }
}
