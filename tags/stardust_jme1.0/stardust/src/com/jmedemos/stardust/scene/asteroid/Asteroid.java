package com.jmedemos.stardust.scene.asteroid;

import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jmedemos.stardust.effects.ParticleEffectFactory;
import com.jmedemos.stardust.scene.Entity;
import com.jmedemos.stardust.scene.EntityManager;
import com.jmedemos.stardust.sound.SoundUtil;
import com.jmedemos.stardust.util.ModelUtil;
import com.jmex.effects.particles.ParticleGeometry;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.material.Material;

/**
 * Basic Asteroid. Loads a asteroid model.
 */
@SuppressWarnings("serial")
public class Asteroid extends Entity {
    /** physic node.*/
    private DynamicPhysicsNode node = null;
    private ParticleGeometry particleGeom = null;
    private static int counter = 0;

    /**
     * creates an asteroid.
     * @param name name of the asteroid.
     * @param modelName name of the asteroidmodel 
     * @param scale asteroid size
     * @param physicsSpace reference to physics space.
     */
    public Asteroid(final String name, final String modelName,
            final float scale, final PhysicsSpace physicsSpace) {

        Spatial model = ModelUtil.get().loadModel(modelName + ".obj");
        model.setName("model");
        model.setLocalScale(scale);
        model.setModelBound(new BoundingBox());
        model.updateModelBound();

        MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
                .createMaterialState();

        ms.setSpecular(ColorRGBA.brown);
        model.setRenderState(ms);
        model.updateRenderState();

        node = physicsSpace.createDynamicNode();
        node.setName(name + (counter++));
        node.setMaterial(Material.GRANITE);
        
        node.attachChild(model);
        node.generatePhysicsGeometry();
        node.setMass(100f);
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
        node.setCullMode(SceneElement.CULL_ALWAYS);
        node.delete();
    }
    
    public Node getNode() {
        return node;
    }
    
    /**
     * The physic node of the asteroid.
     * @return reference to physic node.
     */
    public final DynamicPhysicsNode getPhysNode() {
        return node;
    }

    /**
     * trail as particle effect.
     * @return reference to the particle trail.
     */
    public final ParticleGeometry getParticleGeom() {
        return particleGeom;
    }
}
