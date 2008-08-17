package com.jmedemos.stardust.enemy;

import com.jme.scene.Node;
import com.jmedemos.stardust.ai.ChaseController;
import com.jmedemos.stardust.effects.ParticleEffectFactory;
import com.jmedemos.stardust.scene.PhysicsEntity;
import com.jmedemos.stardust.scene.TrailManager;
import com.jmedemos.stardust.sound.SoundUtil;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.callback.FrictionCallback;

/**
 * Enemies are created by the EnemyFactory.
 * @author Christoph Luder
 */
public class Enemy extends PhysicsEntity {
    private ChaseController chaseController;
    private FrictionCallback fc; 
    private float defaultSpeed = 300;
    
    /**
     * Constructor which has package wide visibility.
     * Only the EnemyFactory should create Enemies.
     * @param target target which the enemy chases
     * @param space reference to physics space
     */
    Enemy(final String modelName, final Node target, final PhysicsSpace space) {
    	super(space, modelName, 5, true);
    	chaseController = new ChaseController(node, target, defaultSpeed, 300, 1500, 0.5f);
    	node.addController(chaseController);
    }

    @Override
    protected void initNode() {
        // Friction Callback to reduce spinning effect after colliding with another object
        fc = new FrictionCallback();
        fc.add((DynamicPhysicsNode)node, 0f, 25.0f);
        physicsSpace.addToUpdateCallbacks(fc);
        TrailManager.get().createTrail(getNode());
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
        physicsSpace.removeFromUpdateCallbacks(fc);
        TrailManager.get().removeTrail(getNode());
        fc = null;
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
