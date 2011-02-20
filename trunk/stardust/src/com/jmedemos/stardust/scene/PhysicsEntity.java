package com.jmedemos.stardust.scene;

import com.jme.bounding.BoundingBox;
import com.jmex.jbullet.PhysicsSpace;
import com.jmex.jbullet.collision.shapes.BoxCollisionShape;
import com.jmex.jbullet.collision.shapes.CollisionShape;
import com.jmex.jbullet.nodes.PhysicsNode;

/**
 * Arrghhh ugly, need better class design.  :(
 * @author Christoph Luder
 */
public class PhysicsEntity extends Entity {
	
    public PhysicsEntity(PhysicsSpace space, String modelName, float scale, boolean dynamic, boolean trianglAccurate) {
        super(modelName, scale);
        CollisionShape shape = null;
        
        // hack
        if (modelName != null && modelName.contains("xwing")) {
        	model.setModelBound(new BoundingBox());
        	model.updateModelBound();
        	model.updateWorldBound();
        }
        
        if (dynamic) {
        	if (model.getWorldBound() instanceof BoundingBox) {
        		shape = new BoxCollisionShape((BoundingBox)model.getWorldBound());
        		node = new PhysicsNode(model, shape);
        	} else {
        		node = new PhysicsNode(model);
        	} 
        } else {
        	node = new PhysicsNode(model);
        	node.setMass(0);
        }
        node.setName("physic node:" +modelName);
        node.attachChild(model);
        
        space.add(node);
        
        initNode();
        
        EntityManager.get().addEntity(this);
    }
    
    public PhysicsEntity(PhysicsSpace space, String modelName, float scale, boolean dynamic) {
        this(space, modelName, scale, dynamic, false);
    }
    
    protected void initNode () {
        
    }
}
