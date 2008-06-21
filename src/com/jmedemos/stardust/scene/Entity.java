package com.jmedemos.stardust.scene;

import java.util.logging.Logger;

import com.jme.scene.Node;

/**
 * Base class for all entities in game.
 * All Entities have health and do damage when they collide. 
 * @author Christoph Luder
 */
public abstract class Entity {
    protected Logger log = Logger.getLogger(Entity.class.getName());
    protected int health = 100;
    protected int damage = 1;
    
    /** the node with the graphical representation */
    public abstract Node getNode();
    
    /** collision with another entity, reduce our health */
    public void doCollision(Entity e) {
        health -= e.getDamage();
        if (health <= 0) {
            die();
        }
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
