package com.jmedemos.stardust.scene.projectile;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jmedemos.stardust.effects.ParticleEffectFactory;
import com.jmedemos.stardust.util.ModelUtil;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.callback.FrictionCallback;

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
        
        setLifeTime(15);
        setSpeed(3000);
    }

    @Override
    protected void initModel() {
        model = ModelUtil.get().loadModel("missile.obj");
        MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
        ms.setEmissive(ColorRGBA.yellow);
        ms.setAmbient(ColorRGBA.yellow);
        model.setRenderState(ms);
    }
    
    @Override
    protected void initNode() {
        // the missile should spawn a bit below us
        getNode().getLocalTranslation().addLocal(getNode().getLocalRotation().getRotationColumn(1).mult(-1));
        getNode().attachChild(ParticleEffectFactory.get().getMissileTrail());
        
        // the HomingMissile needs a force Friction callback, to eliminate the force which 
        // pushes the rocked into the wrong (old) direction
        FrictionCallback fcb = new FrictionCallback();
        fcb.add((DynamicPhysicsNode)node, 1000, 0);
        physicsSpace.addToUpdateCallbacks(fcb);
        node.setName("projectile Missile");
    }
    
    @Override
    public void fire(Vector3f direction, Vector3f startLocation, Quaternion rotation) {
        super.fire(direction, startLocation, rotation);
    	getNode().getLocalRotation().set(rotation);
    	((DynamicPhysicsNode)getNode()).setLinearVelocity(direction.mult(100));
    	getNode().addController(new HomingDevice(this, target));
    }

    public void setTarget(Node target) {
        this.target = target;
    }
    
}
