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
                Texture.MinificationFilter.BilinearNoMipMaps,
                Texture.MagnificationFilter.Bilinear);
        Texture south = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,"data/textures/south.jpg"),
                Texture.MinificationFilter.BilinearNoMipMaps,
                Texture.MagnificationFilter.Bilinear);
        Texture east = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,"data/textures/east.jpg"),
                Texture.MinificationFilter.BilinearNoMipMaps,
                Texture.MagnificationFilter.Bilinear);
        Texture west = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,"data/textures/west.jpg"),
                Texture.MinificationFilter.BilinearNoMipMaps,
                Texture.MagnificationFilter.Bilinear);
        Texture up = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,"data/textures/top.jpg"),
                Texture.MinificationFilter.BilinearNoMipMaps,
                Texture.MagnificationFilter.Bilinear);
        Texture down = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,"data/textures/bottom.jpg"),
                Texture.MinificationFilter.BilinearNoMipMaps,
                Texture.MagnificationFilter.Bilinear);

        setTexture(Skybox.Face.North, north);
        setTexture(Skybox.Face.West, west);
        setTexture(Skybox.Face.South, south);
        setTexture(Skybox.Face.East, east);
        setTexture(Skybox.Face.Up, up);
        setTexture(Skybox.Face.Down, down);

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
