package com.jmedemos.physics_fun.physics;

import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;

/**
 * A Physics update Callback which simulates very basic wind force.
 * @author Christoph Luder
 */
public class PhysicsWindCallback implements PhysicsUpdateCallback {
    /** Wind variation in percent. TODO not yet used*/
    private Vector3f variation = null;
    /** Wind force. */
    private Vector3f force = null;

    /**
     * Create a physics wind update callback. 
     * @param initialVariation not yet used
     * @param initialForce initial force as a direction vector
     */
    public PhysicsWindCallback(final Vector3f initialVariation, final Vector3f initialForce) {
        variation = initialVariation;
        force = initialForce;
    }
    
    /**
     * apply the force of the wind beore every physics step to all DynamicNodes.
     */
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
