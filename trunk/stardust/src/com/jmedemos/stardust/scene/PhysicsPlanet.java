package com.jmedemos.stardust.scene;

import com.jme.scene.Node;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.StaticPhysicsNode;

public class PhysicsPlanet  {
    private StaticPhysicsNode node;
    
    public PhysicsPlanet(PhysicsSpace space, String texName, float radius) {
        Planet p = new Planet("earth", radius);
        node = space.createStaticNode();
        node.setName("physics planet node");
        node.attachChild(p);
        node.generatePhysicsGeometry();
    }
    
    public Node getNode() {
        return node;
    }
    
}
