package com.jmedemos.stardust.scene;

import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.CameraNode;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmedemos.stardust.core.Game;
import com.jmex.physics.DynamicPhysicsNode;

public class MissileCamera {
    /**
     * Displaysystem.
     */
    private DisplaySystem disp = DisplaySystem.getDisplaySystem();
    
    /**
     * is the monitor active yes/no.
     */
    private boolean isActive = true;
    
    /**
     * the quad where the camera is displayed on.
     */
    private Quad monitorQuad;
    
    /**
     * The node with the monitor quad attached.
     */
    private Node monitorNode;
    
    /**
     * the CameraNode which gets attached to the missile.
     */
    private CameraNode missileCamNode;
    
    /**
     * the Texturerenderer which renders the scene to a texture.
     */
    private TextureRenderer tRenderer;
    
    /**
     *  Texture to render the scene to.
     */
    private Texture2D fakeTex;
    
    /**
     * TextureState with the rendered scene.
     */
    private TextureState screenTextureState;
    
    private float lastRend = 1;
    /**
     * render speed of the missile camera.
     */
    private float throttle = 1/30f;
    
    /**
     * updatespeed of the noise.
     */
    private float noiseThrottle = 1/10f;
    
    /**
     * alternative position of the noise texture.
     */
    private Vector3f texturePos = new Vector3f(0.25f, 0.25f, 0.25f);
    boolean textSwitch = false;
    private Texture noiseTex;
    private TextureState noiseTextureState;
    
    /**
     * the scene to render;
     */
    private Spatial scene;
    
    /**
     * creates the missile cam.
     * @param x position of the display in ortho values
     * @param y position of the display in ortho values
     * @param scene the root of the scene to render
     */
    public MissileCamera(final float x, final float y, final Spatial scene) {
        this.scene = scene;
        
        createNoiseTextureState();
        
        tRenderer = disp.createTextureRenderer(
                240, 180, TextureRenderer.Target.Texture2D);
        
        missileCamNode = new CameraNode("missile Camera Node", tRenderer.getCamera());
        tRenderer.getCamera().setFrustumFar(Game.FAR_FRUSTUM);
        tRenderer.getCamera().update();
        missileCamNode.setLocalTranslation(new Vector3f(0, 2.5f, -10));
        missileCamNode.updateGeometricState(0, true);
        
        monitorNode = new Node("Monitor Node");
        monitorQuad = new Quad("Monitor");
        monitorQuad.initialize(240, 180);
        monitorQuad.setLocalTranslation(x, y, 0);
        
        Quad quad2 = new Quad("Monitor");
        quad2.initialize(250, 190);
        quad2.setLocalTranslation(x, y, 0);
        monitorNode.attachChild(quad2);
        monitorNode.attachChild(monitorQuad);
        
        
        // Ok, now lets create the Texture object that our scene will be rendered to.
        tRenderer.setBackgroundColor(new ColorRGBA(0f, 0f, 0f, 1f));
        fakeTex = new Texture2D();
        fakeTex.setRenderToTextureType(Texture.RenderToTextureType.RGBA);
        tRenderer.setupTexture(fakeTex);
        screenTextureState = disp.getRenderer().createTextureState();
        screenTextureState.setTexture(fakeTex);
        screenTextureState.setEnabled(true);
        monitorQuad.setRenderState(screenTextureState);
        
        monitorNode.updateGeometricState(0.0f, true);
        monitorNode.updateRenderState();
        monitorNode.setLightCombineMode(LightCombineMode.Off);
        setActive(false);
    }
    
    /**
     * creates a default TextureState to display a noise picture
     * while the cam is offline
     */
    public void createNoiseTextureState() {
        noiseTex = TextureManager.loadTexture(
        		ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,
        			"noise.jpg"),
                    Texture.MinificationFilter.BilinearNoMipMaps, 
                    Texture.MagnificationFilter.Bilinear);
        noiseTex.setWrap(Texture.WrapMode.Repeat);
        noiseTex.setTranslation(new Vector3f());
        noiseTextureState = disp.getRenderer().createTextureState();
        noiseTextureState.setTexture(noiseTex);
    }
    
    public Node getMonitorNode() {
        return monitorNode;
    }
    
    /**
     * renders the texture.
     * once the CameraNode gets detached from its client,
     * the camera disables itself and displays the noise.   
     * @param tpf time since last frame.
     */
    public void render(final float tpf) {
        boolean parentStatus = false;
        if (missileCamNode.getParent()!= null) {
            parentStatus = ((DynamicPhysicsNode)missileCamNode.getParent()).isActive();
        }
        if ( parentStatus != isActive) {
            setActive(parentStatus);
        }
        
        if (isActive) {
            // render the missile cam to a texture
            lastRend += tpf;
            if (lastRend > throttle ) {
              tRenderer.render(scene, fakeTex);
              lastRend = 0;
            }
        } else {
            // animate the noise texture
            lastRend += tpf;
            if (lastRend > noiseThrottle ) {
                textSwitch = !textSwitch;
                if (textSwitch) {
                    noiseTex.setTranslation(texturePos);
                } else { 
                    noiseTex.setTranslation(Vector3f.ZERO.clone());
                }
                lastRend = 0;
            }
        }
    }
    
    /**
     * actively render the missile camera,
     * or display a noise texture if the cam is disabled.
     * @param active
     */
    public void setActive(final boolean active) {
        isActive = active;
        if (isActive) {
            monitorQuad.setRenderState(screenTextureState);
        } else {
            monitorQuad.setRenderState(noiseTextureState);
        }
        monitorNode.updateRenderState();
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public CameraNode getCameraNode() {
        return missileCamNode;
    }
}
