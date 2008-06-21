package com.jmedemos.stardust.scene;

import com.jme.bounding.BoundingBox;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jmex.effects.LensFlare;

/**
 * A Sun with lensflare effect.
 */
@SuppressWarnings("serial")
public class Sun {
    /**
     * the lightnode to attach the light to.
     */
    private LightNode lightNode = null;

    /**
     * the lightstate.
     */
    private LightState lightState = null;

    /**
     * the lensflare.
     */
    private LensFlare flare;

    /**
     * the Display.
     */
    private DisplaySystem display = DisplaySystem.getDisplaySystem();

    /**
     * the constructor.
     * @param rootNode the scene root node
     * @param skybox reference to the skybox
     */
    public Sun(final Node rootNode, final Node skybox) {
        lightState = display.getRenderer().createLightState();
        lightNode = new LightNode("sun", lightState);
        setSun(rootNode, skybox);
    }

    /**
     * create the sun.
     * @param rootNode refrence to root node
     * @param skybox the light gets attached to the skybox
     */
    private void setSun(final Node rootNode, final Node skybox) {
        lightState.detachAll();
        PointLight dr = new PointLight();
        dr.setEnabled(true);
        dr.setDiffuse(ColorRGBA.white);
        dr.setAmbient(ColorRGBA.gray);
        lightState.setTwoSidedLighting(true);
        lightNode.setLight(dr);
        lightNode.setTarget(rootNode);

        Vector3f min2 = new Vector3f(-0.15f, -0.15f, -0.15f);
        Vector3f max2 = new Vector3f(0.15f, 0.15f, 0.15f);
        Box lightBox = new Box("box", min2, max2);
        lightBox.setModelBound(new BoundingBox());
        lightBox.updateModelBound();

        lightNode.attachChild(lightBox);

        lightBox.setLightCombineMode(LightState.OFF);

//        TextureState[] tex = new TextureState[4];
//        tex[0] = display.getRenderer().createTextureState();
//        tex[0].setTexture(TextureManager.loadTexture(LensFlare.class
//                .getClassLoader().getResource("data/textures/flare1.png"),
//                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, Image.RGBA8888,
//                1.0f, true));
//        tex[0].setEnabled(true);
//
//        tex[1] = display.getRenderer().createTextureState();
//        tex[1].setTexture(TextureManager.loadTexture(LensFlare.class
//                .getClassLoader().getResource("data/textures/flare2.png"),
//                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR));
//        tex[1].setEnabled(true);
//
//        tex[2] = display.getRenderer().createTextureState();
//        tex[2].setTexture(TextureManager.loadTexture(LensFlare.class
//                .getClassLoader().getResource("data/textures/flare3.png"),
//                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR));
//        tex[2].setEnabled(true);
//
//        tex[3] = display.getRenderer().createTextureState();
//        tex[3].setTexture(TextureManager.loadTexture(LensFlare.class
//                .getClassLoader().getResource("data/textures/flare4.png"),
//                Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR));
//        tex[3].setEnabled(true);
//
//        flare = LensFlareFactory.createBasicLensFlare("flare", tex);
//        flare.setLocalScale(0.5f);
//        flare.updateGeometricState(0, false);
//        flare.setRootNode(rootNode);
//
//        lightNode.attachChild(flare);

        skybox.attachChild(lightNode);

        skybox.updateGeometricState(0, true);
        lightNode.updateGeometricState(0, true);

        rootNode.setRenderState(lightState);
    }

    public final LightState getLightState() {
        return lightState;
    }

    public final LightNode getLightNode() {
        return lightNode;
    }
}
