package com.jmedemos.stardust.scene;

import com.jme.scene.Node;
import com.jmex.jbullet.PhysicsSpace;
import com.jmex.jbullet.nodes.PhysicsNode;

public class PhysicsPlanet  {
    private PhysicsNode node;
    
    public PhysicsPlanet(PhysicsSpace space, String texName, float radius, boolean useShader) {
        Planet p = new Planet("earth", radius, useShader);
        node = new PhysicsNode(p);
        node.setName("physics planet node");
        node.attachChild(p);
    }
    
    public Node getNode() {
        return node;
    }
    
}
