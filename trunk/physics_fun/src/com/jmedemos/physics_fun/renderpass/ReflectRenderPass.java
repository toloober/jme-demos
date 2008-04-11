package com.jmedemos.physics_fun.renderpass;

import java.net.URL;
import java.util.ArrayList;

import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.Util;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Plane;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.pass.Pass;
import com.jme.scene.Spatial;
import com.jme.scene.state.CullState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmedemos.physics_fun.core.PhysicsGame;


/*
 * Copyright (c) 2003-2007 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


/**
 * <code>ReflectRenderPass</code> Reflect effect pass.
 * 
 * @author Rikard Herlitz (MrCoder)
 * @version $Id: ReflectRenderPass.java,v 1.1 2007/06/20 23:39:26 mud Exp $
 */
public class ReflectRenderPass extends Pass {
    private static final long serialVersionUID = 1L;
    private Camera cam;
    private TextureRenderer tRenderer;
    private Texture textureReflect;
    private ArrayList<Spatial> renderList;
    private ArrayList<Texture> texArray = new ArrayList<Texture>();
    private GLSLShaderObjectsState reflectionShader;
    private CullState cullBackFace;
    private TextureState ts;
    private Plane reflectPlane;
    private Vector3f calcVect = new Vector3f();
    private boolean supported = true;
    private int renderScale;
    public static String simpleShaderStr = "simpleReflectionShader";

    public void resetParameters() {
        reflectPlane = new Plane(new Vector3f(0.0f, 1.0f, 0.0f), 0.0f);
    }

    /**
     * Release pbuffers in TextureRenderer's. Preferably called from user
     * cleanup method.
     */
    public void cleanup() {
        if (isSupported())
            tRenderer.cleanup();
    }

    public boolean isSupported() {
        return supported;
    }

    /**
     * Creates a new ReflectRenderPass
     * 
     * @param cam
     *            main rendercam to use for reflection settings etc
     * @param renderScale
     *            how many times smaller the reflection/refraction textures
     *            should be compared to the main display
     */
    public ReflectRenderPass(Camera cam, int renderScale) {
        this.cam = cam;
        this.renderScale = renderScale;
        resetParameters();
        initialize();
    }

    private void initialize() {

        DisplaySystem display = DisplaySystem.getDisplaySystem();

        reflectionShader = display.getRenderer().createGLSLShaderObjectsState();

        if (!reflectionShader.isSupported()) {
            supported = false;
        }

        
        
        cullBackFace = display.getRenderer().createCullState();
        cullBackFace.setEnabled(true);
        cullBackFace.setCullMode(CullState.CS_BACK);

        if (isSupported()) {
            tRenderer = display.createTextureRenderer(display.getWidth()
                    / renderScale, display.getHeight() / renderScale,
                    TextureRenderer.RENDER_TEXTURE_2D);

            if (tRenderer.isSupported()) {
                tRenderer.setBackgroundColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
                tRenderer.getCamera().setFrustum(cam.getFrustumNear(),
                        cam.getFrustumFar(), cam.getFrustumLeft(),
                        cam.getFrustumRight(), cam.getFrustumTop(),
                        cam.getFrustumBottom());

                textureReflect = new Texture();
                textureReflect.setWrap(Texture.WM_ECLAMP_S_ECLAMP_T);
                textureReflect.setFilter(Texture.FM_LINEAR);
                textureReflect.setScale(new Vector3f(-1.0f, 1.0f, 1.0f));
                textureReflect.setTranslation(new Vector3f(1.0f, 0.0f, 0.0f));
                tRenderer.setupTexture(textureReflect);

                ts = display.getRenderer().createTextureState();
                ts.setEnabled(true);

                Texture tex = TextureManager.loadTexture(
                        ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,"floor.png"),
                        Texture.MM_LINEAR, Texture.FM_LINEAR, Image.GUESS_FORMAT_NO_S3TC, 0.0f, true);
                tex.setScale(new Vector3f(10, 10, 10));
                tex.setWrap(Texture.WM_WRAP_S_WRAP_T);
                ts.setTexture(tex, 0);
                ts.setTexture(textureReflect, 1);
                reloadShader();
            } else {
                supported = false;
            }
        }
    }

    @Override
    protected void doUpdate(float tpf) {
        super.doUpdate(tpf);
    }

    public void doRender(Renderer r) {
        if (isSupported()) {
            reflectionShader.clearUniforms();
            reflectionShader.setUniform("tex", 0);
            reflectionShader.setUniform("reflection", 1);
            reflectionShader.apply();

            renderReflection();
        } else {
            ts.getTexture().setTranslation(new Vector3f(0, 0, 0));
        }
    }

    public void reloadShader() {
        GLSLShaderObjectsState testShader = DisplaySystem.getDisplaySystem()
                .getRenderer().createGLSLShaderObjectsState();
        URL vert = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_SHADER,
                simpleShaderStr + ".vert");
        URL frag = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_SHADER,
                simpleShaderStr + ".frag");
        if (vert == null ||
            frag == null) {
            Thread.dumpStack();
            PhysicsGame.get().getGame().finish();
        }
        try {
            testShader.load(vert,frag);
            testShader.apply();
            Util.checkGLError();
        } catch (OpenGLException e) {
            e.printStackTrace();
            PhysicsGame.get().getGame().finish();
            return;
        }

        reflectionShader.load(vert,frag);

    }

    public void setReflectEffectOnSpatial(Spatial spatial) {
        spatial.setRenderState(cullBackFace);
//        AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
//        as.setSrcFunction(AlphaState.SB_ONE_MINUS_SRC_ALPHA);
//        as.setDstFunction(AlphaState.DB_SRC_ALPHA);
//        as.setBlendEnabled(true);
//        as.setTestFunction(AlphaState.TF_GREATER);
//        as.setEnabled(true);
//        spatial.setRenderState(as);
        
//        ZBufferState zs = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
//        zs.setFunction(ZBufferState.CF_LEQUAL);
//        zs.setEnabled(true);
//        spatial.setRenderState(zs);
        if (isSupported()) {
            spatial.setRenderQueueMode(Renderer.QUEUE_SKIP);
            spatial.setRenderState(reflectionShader);
            spatial.setRenderState(ts);
        } else {
            spatial.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
            spatial.setLightCombineMode(LightState.OFF);
            spatial.setRenderState(ts);
        }
        spatial.updateRenderState();
    }

    // temporary vectors for mem opt.
    private Vector3f tmpLocation = new Vector3f();
    private Vector3f camReflectPos = new Vector3f();
    private Vector3f camReflectDir = new Vector3f();
    private Vector3f camReflectUp = new Vector3f();
    private Vector3f camReflectLeft = new Vector3f();
    private Vector3f camLocation = new Vector3f();

    private void renderReflection() {
        camLocation.set(cam.getLocation());

        float planeDistance = reflectPlane.pseudoDistance(camLocation);
        calcVect.set(reflectPlane.getNormal()).multLocal(planeDistance * 2.0f);
        camReflectPos.set(camLocation.subtractLocal(calcVect));

        camLocation.set(cam.getLocation()).addLocal(cam.getDirection());
        planeDistance = reflectPlane.pseudoDistance(camLocation);
        calcVect.set(reflectPlane.getNormal()).multLocal(planeDistance * 2.0f);
        camReflectDir.set(camLocation.subtractLocal(calcVect)).subtractLocal(
                camReflectPos).normalizeLocal();

        camLocation.set(cam.getLocation()).addLocal(cam.getUp());
        planeDistance = reflectPlane.pseudoDistance(camLocation);
        calcVect.set(reflectPlane.getNormal()).multLocal(planeDistance * 2.0f);
        camReflectUp.set(camLocation.subtractLocal(calcVect)).subtractLocal(
                camReflectPos).normalizeLocal();

        camReflectLeft.set(camReflectDir).crossLocal(camReflectUp)
                .normalizeLocal();

        tRenderer.getCamera().getLocation().set(camReflectPos);
        tRenderer.getCamera().getDirection().set(camReflectDir);
        tRenderer.getCamera().getUp().set(camReflectUp);
        tRenderer.getCamera().getLeft().set(camReflectLeft);

        texArray.clear();
        texArray.add(textureReflect);
        tRenderer.render(renderList, texArray);

    }

    public void removeReflectedScene(Spatial renderNode) {
        if (renderList != null) {
            System.out.println(renderList.remove(renderNode));
        }
    }

    public void clearReflectedScene() {
        if (renderList != null) {
            renderList.clear();
        }
    }

    public void setReflectedScene(Spatial renderNode) {
        if (renderList == null) {
            renderList = new ArrayList<Spatial>();
        }
        renderList.clear();
        renderList.add(renderNode);
        renderNode.updateRenderState();
    }

    public void addReflectedScene(Spatial renderNode) {
        if (renderList == null) {
            renderList = new ArrayList<Spatial>();
        }
        if (!renderList.contains(renderNode)) {
            renderList.add(renderNode);
            renderNode.updateRenderState();
        }
    }

    public Camera getCam() {
        return cam;
    }

    public void setCam(Camera cam) {
        this.cam = cam;
    }

    public float getPlaneHeight() {
        return reflectPlane.getConstant();
    }

    public void setPlaneHeight(float planeHeight) {
        this.reflectPlane.setConstant(planeHeight);
    }

    public Vector3f getNormal() {
        return reflectPlane.getNormal();
    }

    public void setNormal(Vector3f normal) {
        reflectPlane.setNormal(normal);
    }

    public Plane getReflectPlane() {
        return reflectPlane;
    }

    public void setReflectPlane(Plane reflectPlane) {
        this.reflectPlane = reflectPlane;
    }

    public Texture getTextureReflect() {
        return textureReflect;
    }

    public int getRenderScale() {
        return renderScale;
    }

    public void setRenderScale(int renderScale) {
        this.renderScale = renderScale;
    }
}