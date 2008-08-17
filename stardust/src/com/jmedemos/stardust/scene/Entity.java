package com.jmedemos.stardust.scene;

import java.util.logging.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.scene.Spatial;
import com.jmedemos.stardust.util.ModelUtil;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;

/**
 * Base class for all entities in game.
 * All Entities have health and do damage when they collide. 
 * @author Christoph Luder
 */
public abstract class Entity {
    protected Logger log = Logger.getLogger(Entity.class.getName());
    protected int health = 100;
    protected int damage = 1;
    protected PhysicsNode node;
    protected PhysicsSpace physicsSpace;
    protected String modelName;
    protected Spatial model;
    
    public Entity(PhysicsSpace space, String modelName, float scale) {
		this.modelName = modelName;
		this.physicsSpace = space;
		
		if (modelName != null) {
	        model = ModelUtil.get().loadModel(modelName);
	        model.setLocalScale(scale);
	        model.setModelBound(new BoundingBox());
	        model.updateModelBound();
		}
		
		initModel();
	}
    
    
    /** the node with the graphical representation */
    public PhysicsNode getNode() {
    	return node;
    }
    
    /** set up the model */
    protected void initModel() {};
    
    /** collision with another entity, reduce our health */
    public void doCollision(Entity e) {
        if (e.getNode() == null) {
            System.out.println();
        }
        if (this.getNode() == null) {
            System.out.println();
        }
//        System.out.println("Entity doCollision: collision between " +this.getNode().getName() + " and " +e.getNode().getName());
//        System.out.flush();
        health -= e.getDamage();
        if (health <= 0) {
            die();
        }
    }
    
    public void setHealth(int health) {
		this.health = health;
	}

	@Override
    protected void finalize() throws Throwable {
        log.info(" ->>-->> Finalizing: ->>->>:" +toString());
        super.finalize();
    }
    
    public void die() {
        EntityManager.get().remove(this);
        getNode().removeFromParent();
        getNode().detachAllChildren();
    }
    
    /** how much damage we deal to the other entities */
    public int getDamage() {
        return damage;
    }

    public int getHealth() {
        return health;
    }
}
