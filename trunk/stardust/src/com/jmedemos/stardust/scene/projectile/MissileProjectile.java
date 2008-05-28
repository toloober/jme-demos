package com.jmedemos.stardust.scene.projectile;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jmedemos.stardust.effects.ParticleEffectFactory;
import com.jmedemos.stardust.util.ModelUtil;
import com.jmex.physics.PhysicsSpace;

/**
 * A slow flying missile.
 */
public class MissileProjectile extends Projectile {
    private Node target;
    /**
     * Replaces the default look with a missile model. 
     * @param physics reference to physics space.
     */
    public MissileProjectile(final PhysicsSpace physics) {
        super(physics);
        
        /** A missile does 40% damage. */
        damage = 40;
        
        setLifeTime(20);
        setSpeed(3000);
        Spatial model = ModelUtil.get().loadModel("missile.obj");
        MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
        ms.setEmissive(ColorRGBA.yellow);
        ms.setAmbient(ColorRGBA.yellow);
        model.setRenderState(ms);
        updateModel(model);
        // the missile should spawn a bit below us
        getNode().getLocalTranslation().addLocal(getNode().getLocalRotation().getRotationColumn(1).mult(-1));
        getNode().attachChild(ParticleEffectFactory.get().getMissileTrail());
    }
    
    @Override
    public void fire(Vector3f direction, Vector3f startLocation, Quaternion rotation) {
    	getNode().getLocalRotation().set(rotation);
    	super.fire(direction, startLocation, rotation);
    	getNode().addController(new HomingDevice(this, target));
    }

    public void setTarget(Node target) {
        this.target = target;
    }
    
}
