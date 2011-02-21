package com.jmedemos.stardust.scene;

import java.util.logging.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.scene.Spatial;
import com.jmedemos.stardust.util.ModelUtil;
import com.jmex.jbullet.nodes.PhysicsNode;

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
    protected String modelName;
    protected Spatial model;
    protected boolean dead = false;
    
    public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public Entity(String modelName, float scale) {
		this.modelName = modelName;
		
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
            log.info("e.getNode() == null");
        }
        if (this.getNode() == null) {
            log.info("this.getNode() == null");
        }
        
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
    	dead = true;
    	log.info(this.getNode() +" has died :(");
        EntityManager.get().remove(this);
        if (node.getControllerCount() > 0)
    	    this.node.removeController(0);
        getNode().removeFromParent();
    }
    
    /** how much damage we deal to the other entities */
    public int getDamage() {
        return damage;
    }

    public int getHealth() {
        return health;
    }
}
