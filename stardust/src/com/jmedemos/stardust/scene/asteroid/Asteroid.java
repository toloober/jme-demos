package com.jmedemos.stardust.scene.asteroid;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jmedemos.stardust.effects.ParticleEffectFactory;
import com.jmedemos.stardust.scene.EntityManager;
import com.jmedemos.stardust.scene.PhysicsEntity;
import com.jmedemos.stardust.sound.SoundUtil;
import com.jmex.effects.particles.ParticleMesh;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.material.Material;

/**
 * Basic Asteroid. Loads a asteroid model.
 */
public class Asteroid extends PhysicsEntity {
    private ParticleMesh particleGeom = null;

    /**
     * creates an asteroid.
     * @param modelName name of the asteroidmodel 
     * @param scale asteroid size
     * @param physicsSpace reference to physics space.
     */
    public Asteroid(final String modelName,
            final float scale, final PhysicsSpace physicsSpace) {
    	super(physicsSpace, modelName, scale, true);
    }

    @Override
    protected void initModel() {
        super.initModel();
        MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
                                    .createMaterialState();
        ms.setDiffuse(ColorRGBA.brown);
        model.setRenderState(ms);
        model.updateRenderState();

    }
    
    @Override
    protected void initNode() {
    	node.setMaterial(Material.GRANITE);
    }
    
    /**
     * Death of an Asteroid.
     *  - play an explosion sound anddisplay a explosion particle effect.
     *  - hide the Asteroid and let the particle trail fade out
     */
    @Override
    public void die() {
        if (particleGeom != null) {
            // this asteroid has a particle trail, we can't remove the node from
            // its parent yet.
            // the node will detach from the parent when the particle trail faded out
            // -> see asteroid.OnDeadListener
            particleGeom.getParticleController().setRepeatType(Controller.RT_CLAMP);
            EntityManager.get().remove(this);
            getNode().detachAllChildren();
        } else {
            super.die();
        }
        node.updateWorldVectors();
        ParticleEffectFactory.get().spawnExplosion(node.getWorldTranslation().clone());
        SoundUtil.get().playExplosion(node.getWorldTranslation().clone());
        node.setCullHint(CullHint.Always);
        node.delete();
    }
    
    /**
     * trail as particle effect.
     * @return reference to the particle trail.
     */
    public final ParticleMesh getParticleGeom() {
        return particleGeom;
    }
}
