package com.jmedemos.stardust.scene;

import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.material.Material;

/**
 * Arrghhh ugly, need better class design.  :(
 * @author Christoph Luder
 */
public class PhysicsEntity extends Entity {

    public PhysicsEntity(PhysicsSpace space, String modelName, float scale, boolean dynamic, boolean trianglAccurate) {
        super(space, modelName, scale);
        
        if (dynamic) {
            node = physicsSpace.createDynamicNode();
        } else {
            node = physicsSpace.createStaticNode();
        }
        node.setName("physic node:" +modelName);
        node.attachChild(model);
        node.setMaterial(Material.IRON);
        node.generatePhysicsGeometry(trianglAccurate);
        
        if (dynamic) {
            ((DynamicPhysicsNode)node).computeMass();
        }

        initNode();
        
        EntityManager.get().addEntity(this);
    }
    
    public PhysicsEntity(PhysicsSpace space, String modelName, float scale, boolean dynamic) {
        this(space, modelName, scale, dynamic, false);
    }
    
    protected void initNode () {
        
    }
}
