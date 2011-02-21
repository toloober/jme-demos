package com.jmedemos.stardust.scene.projectile;

import com.jme.bounding.BoundingBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.shape.Sphere;
import com.jmedemos.stardust.scene.PhysicsEntity;
import com.jmex.jbullet.PhysicsSpace;

/**
 * Base class for projectiles
 * A projectile can be a bullet or missile.
 * A projectile has a direction in which is flyes and a speed.
 * A controller (ProjectileMover) accelerates the projectile after it has been fired.
 * The graphical representaion of a projectil, can be changed with updateModel().
 */
public class Projectile extends PhysicsEntity {
    /**
     * Speed of the projectile.
     */
    private float speed = 20;

    /**
     * Lifetime of a Projectil in seconds.
     * when a projectile dies, it will be removed from the Scene.
     */
    private float lifeTime = 5;
    
    /**
     * the current age of the projectile.
     */
    private float age = 5;

    /**
     * Direction in which the projectile flies.
     */
    private Vector3f direction = null;

    /**
     * @param physicsSpace reference to physicsspace.
     */
    public Projectile(final PhysicsSpace physicsSpace) {
    	super(physicsSpace, null, 1, true);
    	node.setName("projectile");
        health = 1;
    }
    
    @Override
    protected void initModel() {
        // default look of a projectile, a simple sphere
        model = new Sphere("projectil Model", 5, 5, 0.2f);
        model.setModelBound(new BoundingBox());
        model.updateModelBound();
    }
    
    /**
     * Fires this projectile.
     * 
     * @param direction      the direction to fire the projectile in
     * @param startLocation  the location to fire the projectile from
     * @param rotation       the rotation of the projectile
     */
    public void fire(final Vector3f direction, final Vector3f startLocation,  final Quaternion rotation) {
    	this.direction = direction.normalize();
    	node.setLocalTranslation(startLocation.clone());
    	node.setLocalRotation(rotation.clone());
    	    	
        node.setCullHint(CullHint.Dynamic);
    	node.updateGeometricState(0, true);
    	node.clearForces();
    }
    
    /**
     * returns the lifetime.
     * @return lifetime in seconds.
     */
    public final float getLifeTime() {
        return lifeTime;
    }

    /**
     * Sets the lifetime.
     * @param lifeTime in seconds
     */
    public final void setLifeTime(final float lifeTime) {
        this.lifeTime = lifeTime;
    }

    /**
     * returns the speed.
     * @return speed.
     */
    public final float getSpeed() {
        return speed;
    }

    public final float getCurrentSpeed() {
        return getNode().getLinearVelocity().dot(getNode().getLocalRotation().getRotationColumn(2));
    }
    
    /**
     * @param speed new speed.
     */
    public final void setSpeed(final float speed) {
        this.speed = speed;
    }

    /**
     * returns the direction of the projectile.
     * @return direction.
     */
    public final Vector3f getDirection() {
        return direction;
    }

    public float getAge() {
        return age;
    }

    public void setAge(float age) {
        this.age = age;
    }
}
