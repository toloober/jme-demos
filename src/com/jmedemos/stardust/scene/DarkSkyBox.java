package com.jmedemos.stardust.scene;

import java.util.concurrent.Callable;

import com.jme.image.Texture;
import com.jme.scene.Skybox;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;

/**
 * A simple Skybox.
 */
@SuppressWarnings("serial")
public class DarkSkyBox extends Skybox {

    public DarkSkyBox() {
        super("skybox", 100, 100, 100);

        Texture north = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,"data/textures/north.jpg"),
                Texture.MM_LINEAR, Texture.FM_LINEAR);
        Texture south = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,"data/textures/south.jpg"),
                Texture.MM_LINEAR, Texture.FM_LINEAR);
        Texture east = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,"data/textures/east.jpg"),
                Texture.MM_LINEAR, Texture.FM_LINEAR);
        Texture west = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,"data/textures/west.jpg"),
                Texture.MM_LINEAR, Texture.FM_LINEAR);
        Texture up = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,"data/textures/top.jpg"),
                Texture.MM_LINEAR, Texture.FM_LINEAR);
        Texture down = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,"data/textures/bottom.jpg"),
                Texture.MM_LINEAR, Texture.FM_LINEAR);

        setTexture(Skybox.NORTH, north);
        setTexture(Skybox.WEST, west);
        setTexture(Skybox.SOUTH, south);
        setTexture(Skybox.EAST, east);
        setTexture(Skybox.UP, up);
        setTexture(Skybox.DOWN, down);

        Callable<Object> preload = new Callable<Object>() {
            public Object call() throws Exception {
                preloadTextures();
                return null;
            }
        };
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
                .enqueue(preload);

        updateRenderState();
    }
}
