package com.jmedemos.physics_fun.physics;

import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;

public class PhysicsWindCallback implements PhysicsUpdateCallback {
    /**
     * Wind variation in percent.
     */
    private Vector3f variation = null;
    
    /**
     * Wind force.
     */
    private Vector3f force = null;

    public PhysicsWindCallback(final Vector3f initialVariation, final Vector3f initialForce) {
        variation = initialVariation;
        force = initialForce;
    }
    
    public void beforeStep(PhysicsSpace space, float time) {
        for (PhysicsNode n: space.getNodes()) {
            if (n instanceof DynamicPhysicsNode) {
                ((DynamicPhysicsNode)n).addForce(force);
            }
        }
    }
    public void afterStep(PhysicsSpace space, float time) {
        
    }

    public Vector3f getVariation() {
        return variation;
    }

    public void setVariation(Vector3f variation) {
        this.variation = variation;
    }

    public Vector3f getForce() {
        return force;
    }

    public void setForce(Vector3f force) {
        this.force = force;
    }
    
}
