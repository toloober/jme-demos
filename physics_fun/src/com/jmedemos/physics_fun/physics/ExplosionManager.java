package com.jmedemos.physics_fun.physics;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;

/**
 * <code>ExplosionManager</code> contains a static method to simulate an explosion with a given
 * <b>force</b> and <b>radius</b> at a given <b>coordinate</b>.
 *
 * @author Per Thulin
 */
public class ExplosionManager {
    /** Temp variable to flatline memory usage. */
    private static final Vector3f distance = new Vector3f();

    /** Temp variable to flatline memory usage. */
    private static final Vector3f direction = new Vector3f();

    /** Temp variable to flatline memory usage. */
    private static final Vector3f forceToApply = new Vector3f();

    /**
    * A static method to simulate an explosion with a given
    * <b>force</b> and <b>radius</b> at a given <b>coordinate</b>.
    *
    * @param position
    *     The explosion center.
    * @param force
    *     The force of which an object right in the explosion center
    *     will be affected by. The applied force attenuates relative
    *     to the distance between the object and the explosion center.
    * @param radius
    *     The explosion radius. Objects outside this radius will
    *     not get affected.
    */
    public static void createExplosion(PhysicsSpace space, Vector3f position, float force, float radius) {
        // Loop through all the objects in the physics world and apply an
        // explosion force.
        for (PhysicsNode obj : space.getNodes()) {
            // Escape 1: if the object is static.
            if (obj.isStatic()) continue;

            // Calculate the distance between the object and the explosion centre.
            obj.getChild(0).getWorldTranslation().subtract(position, distance);

            // Calculate the direction vector between the explosion centre
            // and the object.
            direction.set(distance);
            direction.normalizeLocal();

            distance.x = FastMath.abs(distance.x);
            distance.y = FastMath.abs(distance.y);
            distance.z = FastMath.abs(distance.z);

            // Escape 2: if the object is outside of the explosion radius. Maybe
            // this is a little unnecessary, but will save computations in a
            // scene with many objects spread out.
            if (distance.x > radius || distance.y > radius || distance.z > radius) continue;

            // Calculate the force to apply. The force should attenuate
            // relative to the distance between the object and the
            // explosion center.
            forceToApply.x = (1 - (distance.x/radius)) * force;
            forceToApply.y = (1 - (distance.y/radius)) * force;
            forceToApply.z = (1 - (distance.z/radius)) * force;
            forceToApply.multLocal(direction);

            // Apply the force.
            ((DynamicPhysicsNode)obj).addForce(forceToApply);
        }
    }
}