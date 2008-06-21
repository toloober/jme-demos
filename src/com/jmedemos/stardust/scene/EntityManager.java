package com.jmedemos.stardust.scene;

import java.util.HashMap;

import com.jme.scene.Node;


public class EntityManager {
    private HashMap<Node, Entity> entites;
    
    private static EntityManager instance;
    
    private EntityManager () {
        entites = new HashMap<Node, Entity>();
    }
    
    public static EntityManager get () {
        if (instance == null) {
            instance = new EntityManager();
            return instance;
        }
        return instance;
    }
    public void remove(Entity e) {
        entites.remove(e.getNode());
    }
    public void addEntity(Entity e) {
        entites.put(e.getNode(), e);
    }
    
    public Entity getEntity(Node n) {
        return entites.get(n);
    }
    
    /**
     * Check if this Node is a projectile.
     * @param node node to check
     * @return true if this node belongs to an Enemy
     */
    public boolean isProjectile (Node node) {
        return entites.containsKey(node);
    }
}
