package com.jmedemos.stardust.scene.projectile;

import com.jme.bounding.BoundingCapsule;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Capsule;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jmex.jbullet.PhysicsSpace;

/**
 * A bullet kind of projectile represented by a capsule, which
 * is usually rapidly fired. 
 */
public class BulletProjectile extends Projectile {

    /**
     * @param physics reference to physics space.
     */
    public BulletProjectile(final PhysicsSpace physics) {
        super(physics);
        
        node.setName("projectil bullet");
        setSpeed(1000);
        health = 1;
        damage = 10;
    }
    
    @Override
    protected void initModel() {
        model = new Capsule("projectil model", 2, 6, 2, 0.15f, 2.5f);
        model.setModelBound(new BoundingCapsule());
        model.updateModelBound();
        model.getLocalRotation().fromAngleAxis(FastMath.DEG_TO_RAD * 90, Vector3f.UNIT_X);
        MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
                                .createMaterialState();
        ms.setEmissive(new ColorRGBA(0.8f, 0.8f, 0.8f, 1));
        ms.setSpecular(ColorRGBA.white.clone());
        ms.setShininess(128);
        model.setRenderState(ms);
    }
    
    @Override
    public void fire(Vector3f direction, Vector3f startLocation,
            Quaternion rotation) {
        super.fire(direction, startLocation, rotation);
        // attach a controller to the projectile, so that it gets moved forward
        // every update cycle.
        getNode().addController(new ProjectileMover(this));
    }
}
