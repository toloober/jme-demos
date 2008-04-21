package com.jmedemos.physics_fun.util;

/**
 * Singleton class to easely set and get some vales from.
 * @author Christoph Luder
 */
public class SceneSettings {
    private MaterialType wallMaterial = MaterialType.WOOD; 
    private int wallWidth = 5;
    private int wallHeigth = 10;
    private float wallElementSize = 0.5f;
    private static SceneSettings instance = null;
    
    public static SceneSettings get() {
        if (instance == null) {
            instance = new SceneSettings();
        }
        return instance;
    }

    public MaterialType getWallMaterial() {
        return wallMaterial;
    }

    public void setWallMaterial(MaterialType wallMaterial) {
        this.wallMaterial = wallMaterial;
    }

    public int getWallHeigth() {
        return wallHeigth;
    }

    public void setWallHeigth(int wallHeigth) {
        this.wallHeigth = wallHeigth;
    }

    public int getWallWidth() {
        return wallWidth;
    }

    public void setWallWidth(int wallWidth) {
        this.wallWidth = wallWidth;
    }

    public float getWallElementSize() {
        return wallElementSize;
    }

    public void setWallElementSize(int wallElementSize) {
        this.wallElementSize = wallElementSize;
    }
        
}
