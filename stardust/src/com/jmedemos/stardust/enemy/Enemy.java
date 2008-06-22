package com.jmedemos.stardust.enemy;

import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jmedemos.stardust.ai.ChaseController;
import com.jmedemos.stardust.effects.ParticleEffectFactory;
import com.jmedemos.stardust.scene.Entity;
import com.jmedemos.stardust.scene.TrailManager;
import com.jmedemos.stardust.sound.SoundUtil;
import com.jmedemos.stardust.util.ModelUtil;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.callback.FrictionCallback;
import com.jmex.physics.material.Material;

/**
 * Enemies are created by the EnemyFactory.
 * @author Christoph Luder
 */
public class Enemy extends Entity {
    private DynamicPhysicsNode node = null;
    private ChaseController chaseController;
    private FrictionCallback fc; 
    private PhysicsSpace space = null;
    private float defaultSpeed = 300;
    
    /**
     * Constructor which has package wide visibility.
     * Only the EnemyFactory should create Enemies.
     * @param target target which the enemy chases
     * @param space reference to physics space
     */
    Enemy(final String modelName, final Node target, final PhysicsSpace space) {
        this.space = space;
        node = space.createDynamicNode();
        node.setName("enemy");
        Spatial n = ModelUtil.get().loadModel(modelName);
        node.attachChild(n);
        n.setLocalScale(5);
        chaseController = new ChaseController(node, target, defaultSpeed, 0.5f);
        node.addController(chaseController);
        node.setMaterial(Material.IRON);
        node.generatePhysicsGeometry();
        // Friction Callback to reduce spinning effect after colliding with another object
        fc = new FrictionCallback();
        fc.add(node, 0f, 25.0f);
        space.addToUpdateCallbacks(fc);
        
        TrailManager.get().createTrail(getNode());
        
    }
    
    public Node getNode() {
        return node;
    }
    
    public void setTarget(Node target) {
    	chaseController.setTarget(target);
    }
    
    @Override
    public void die() {
        super.die();
        node.updateWorldVectors();
        ParticleEffectFactory.get().spawnExplosion(node.getWorldTranslation());
        SoundUtil.get().playExplosion(node.getWorldTranslation());
        node.delete();
        node.detachAllChildren();
        node.removeController(chaseController);
        space.removeFromUpdateCallbacks(fc);
        TrailManager.get().removeTrail(getNode());
        fc = null;
        space = null;
        chaseController = null;
        node = null;
    }
    
    public float getSpeed() {
        return chaseController.getSpeed();
    }

    public void setSpeed(float speed) {
        chaseController.setSpeed(speed);
    }

    public float getAgility() {
        return chaseController.getAgility();
    }

    public void setAgility(float agility) {
        chaseController.setAgility(agility);

    }
}
