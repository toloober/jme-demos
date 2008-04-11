package com.jmedemos.physics_fun.gamestates;

import java.util.concurrent.Callable;

import com.jme.renderer.Renderer;
import com.jme.renderer.pass.BasicPassManager;
import com.jme.renderer.pass.ShadowedRenderPass;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jmedemos.physics_fun.renderpass.ReflectRenderPass;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;

public class RenderPassGamestate extends GameState {
    private Renderer renderer = DisplaySystem.getDisplaySystem().getRenderer();
    private ShadowedRenderPass shadowPass = null;
    private BasicPassManager pManager = null;
    private ReflectRenderPass reflectRenderPass = null;
    
    public RenderPassGamestate() {
        setName("pass");
        Node scene = ((MainGameState)GameStateManager.getInstance().getChild("main")).getRootNode();
        Quad floor = ((MainGameState)GameStateManager.getInstance().getChild("main")).getCarpet();
        Node wall = ((MainGameState)GameStateManager.getInstance().getChild("main")).getWall();
        Node ballNode = ((MainGameState)GameStateManager.getInstance().getChild("main")).getBallNode();
        pManager = new BasicPassManager();
        shadowPass = new ShadowedRenderPass();
        shadowPass.add(scene);
        shadowPass.addOccluder(scene);
        shadowPass.setLightingMethod(ShadowedRenderPass.MODULATIVE);
        shadowPass.setRenderShadows(true);
        
        try {
            GameTaskQueueManager.getManager().update(new Callable<Object>() {
                public Object call() throws Exception {
                    reflectRenderPass = new ReflectRenderPass(renderer.getCamera(), 1);
                    return null;
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        reflectRenderPass.setReflectEffectOnSpatial(floor);
        reflectRenderPass.addReflectedScene(wall);
        reflectRenderPass.addReflectedScene(ballNode);
        
        pManager.add(reflectRenderPass);
        pManager.add(shadowPass);
    }
    
    @Override
    public void cleanup() {
        
    }
    
    @Override
    public void render(float tpf) {
        pManager.renderPasses(renderer);
    }
    
    @Override
    public void update(float tpf) {
        pManager.updatePasses(tpf);
    }
    
    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        
        if (active) {
        } else {
//            ((MainGameState)GameStateManager.getInstance().getChild("main")).refreshCarpetTexture();
        }
    }
}
