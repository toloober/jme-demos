package com.jmedemos.stardust.enemy;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jmedemos.stardust.effects.ParticleEffectFactory;
import com.jmedemos.stardust.scene.Entity;
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
    private float speed = 350;
    private float agility = 0.5f;
    private ChaseController chaseController;
    private int status = STATUS_CHASE;
    private float evadeBoost = 2f;
    private FrictionCallback fc; 
    private PhysicsSpace space = null;
    
    private static int STATUS_EVADE = 0;
    private static int STATUS_CHASE = 1;
    
    /**
     * Constructor which has package wide visibility.
     * Only the EnemyFactory should create Enemies.
     * @param target target which the enemy chases
     * @param space reference to physics space
     */
    Enemy(final Node target, PhysicsSpace space) {
        this.space = space;
        node = space.createDynamicNode();
        node.setName("enemy");
        Spatial n = ModelUtil.get().loadModel("base_ship.obj", "data/textures");
        node.attachChild(n);
        n.setLocalScale(5);
        chaseController = new ChaseController(target);
        node.addController(chaseController);
        node.setMaterial(Material.IRON);
        node.generatePhysicsGeometry();
        // Friction Callback to reduce spinning effect after colliding with another object
        fc = new FrictionCallback();
        fc.add(node, 0f, 25.0f);
        space.addToUpdateCallbacks(fc);
    }
    
    public Node getNode() {
        return node;
    }
    
    public void setTarget(Node target) {
    	chaseController.target = target;
    }
    
    class ChaseController extends Controller {
        private static final long serialVersionUID = 1L;
        private Node target = null;
        
        public ChaseController(Node target) {
            this.target = target;
        }
        /**
         * Chases the target.
         * If no target is set we do wait and see...
         */
        public void update(float time) {
        	if (target != null) {
	            Quaternion oldRot = new Quaternion(node.getLocalRotation());
	            node.updateWorldVectors();
	            node.lookAt(target.getLocalTranslation(), Vector3f.UNIT_Y);
	            if (node.getLocalTranslation().distance(
	            		target.getLocalTranslation()) < 500) {
	            	status = STATUS_EVADE;
	            }
	            
	            if (node.getLocalTranslation().distance(
	            		target.getLocalTranslation()) > 1500) {
	            	status = STATUS_CHASE;
	            }
	            
	            if (status == STATUS_EVADE) {
	                Quaternion newRot = node.getLocalRotation().inverse();
	                node.getLocalRotation().set(oldRot);
	            	node.getLocalRotation().slerp(newRot, agility*time*evadeBoost);
	            	node.getLocalTranslation().addLocal(
	            			node.getLocalRotation().getRotationColumn(2).mult(speed*time*evadeBoost));
	            } else {
	                Quaternion newRot = new Quaternion(node.getLocalRotation());
	                node.getLocalRotation().set(oldRot);

	            	node.getLocalRotation().slerp(newRot, agility*time);
	            	node.getLocalTranslation().addLocal(
	            			node.getLocalRotation().getRotationColumn(2).mult(speed*time));
	            }
        	}
        }
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
        fc = null;
        space = null;
        chaseController.target = null;
        chaseController = null;
        node = null;
    }
    
    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getAgility() {
        return agility;
    }

    public void setAgility(float agility) {
        this.agility = agility;
    }
}
