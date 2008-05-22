package com.jmedemos.stardust.scene.projectile;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jmex.physics.PhysicsSpace;

/**
 * A bullet projectile represented by a small sphere. 
 */
public class BulletProjectile extends Projectile {

    /**
     * @param direction direction the projectile heads.
     * @param startLocation spawn location
     * @param physics reference to physics space.
     */
    public BulletProjectile(final PhysicsSpace physics,
            final Vector3f direction, final Vector3f startLocation) {
        super(direction, startLocation, physics);
        setSpeed(1000);
        
        health = 1;
        damage = 10;
        
        MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
                .createMaterialState();
        ms.setEmissive(new ColorRGBA(0, 1, 0, 1));
        getNode().setRenderState(ms);
        getNode().updateRenderState();
        getNode().setMass(50);
    }
}
