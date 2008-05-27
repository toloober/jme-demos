package com.jmedemos.stardust.scene.projectile;

import com.jme.bounding.BoundingBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;
import com.jmedemos.stardust.scene.Entity;
import com.jmedemos.stardust.scene.EntityManager;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.material.Material;

/**
 * Base class for projectiles
 * A projectile can be a bullet or missile.
 * A projectile has a direction in which is flyes and a speed.
 * A controller (ProjectileMover) accelerates the projectile after it has been fired.
 * The graphical representaion of a projectil, can be changed with updateModel().
 */
public class Projectile extends Entity {

    /**
     * Graphical representation of the Projectile.
     * This can be a Sphere or more complex model.
     */
    private Spatial model = null;

    /**
     * Speed of the projectile.
     */
    private float speed = 20;

    /**
     * Lifetime of a Projectil.
     * when a projectile dies, it will be removed from the Scene.
     */
    private float lifeTime = 100;

    /**
     * Direction in which the projectile flies.
     */
    private Vector3f direction = null;

    /**
     * The Physics node.
     */
    private DynamicPhysicsNode node = null;

    /**
     * little helper.
     */
    private static int counter = 0;
    
    /**
     * Indicates whether this Projectile is active.
     */
    private boolean active;

    /**
     * @param physicsSpace reference to physicsspace.
     */
    @SuppressWarnings("serial")
    public Projectile(final PhysicsSpace physicsSpace) {
        health = 1;

        // create the physical representation of the projectile
        node = physicsSpace.createDynamicNode();
        node.setName("projectil Physics [" + counter++ + "]");
        node.setMaterial(Material.IRON);

        // default look of a projectile, a simple sphere
        model = new Sphere("projectil Model", 5, 5, 0.2f);
        model.setModelBound(new BoundingBox());
        model.updateModelBound();

        node.attachChild(model);
        node.generatePhysicsGeometry();
    }

    /**
     * Changes the graphical representation of the projectil.
     * @param model new trimesh for the projectile.
     */
    public final void updateModel(final Spatial model) {
        this.model.removeFromParent();
        this.model = model;
        node.attachChild(model);
        model.setModelBound(new BoundingBox());
        model.updateModelBound();
        node.generatePhysicsGeometry();
//        node.updateGeometricState(0, true);
        node.updateRenderState();
    }
    
    /**
     * Fires this projectile.
     * 
     * @param direction      the direction to fire the projectile in
     * @param startLocation  the location to fire the projectile from
     * @param rotation       the rotation of the projectile
     */
    public void fire(final Vector3f direction, final Vector3f startLocation,  final Quaternion rotation) {
    	this.lifeTime = 100;
    	this.direction = direction.normalize();
    	node.getLocalTranslation().set(startLocation);
    	
    	// attach a controller to the projectile, so that it gets moved forward
        // every update cycle.
        node.addController(new ProjectileMover(this));
    	
        node.setCullMode(SceneElement.CULL_DYNAMIC);
    	node.updateGeometricState(0, false);
    }

    /**
     * destroys the projectile and removes it from the parent Node.
     * @param controller controller which initiated the removal.
     */
    public void die() {
    	EntityManager.get().remove(this);
        this.node.removeFromParent();
        this.node.removeController(0);
        this.active = false;
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

    /**
     * returns the physics node of the entity
     * @return PhysicNode
     */
    public DynamicPhysicsNode getNode() {
        return node;
    }
    
    public void setActive(boolean active) {
		this.active = active;
	}

	/**
     * Indicates whether this Projectile is active.
     * 
     * @return whether this Projectile is active
     */
	public boolean isActive() {
		return active;
	}
    
}
