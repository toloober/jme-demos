package com.jmedemos.stardust.scene.projectile;

import java.util.logging.Logger;

import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jmedemos.stardust.scene.Entity;
import com.jmedemos.stardust.scene.EntityManager;

public class HomingDevice extends Controller {

    private static final long serialVersionUID = 1L;
    private MissileProjectile object;
    private Node target;
    private Logger log = Logger.getLogger(HomingDevice.class.getName());
    
    public HomingDevice(MissileProjectile obj, final Node target) {
    	this.target = target;
        this.object = obj;
    }
    
    /**
     * Alter the objects Rotation, so that it points towards the Target and
     * moves the object forward.
     * TODO die when target dies.
     */
    @Override
    public void update(float time) {
    	this.setActive(false);
        object.setAge(object.getAge()-time);
        if (object.getLifeTime() < 0) {
            target = null;
            object.die();
            return;
        }
        
        Entity e = EntityManager.get().getEntity(target);
        if (e == null || e.isDead()) {
        	object.die();
        }
        
        target.updateWorldVectors();
        
        object.getNode().updateWorldVectors();
        object.getNode().lookAt(target.getLocalTranslation(), Vector3f.UNIT_Y);
        
//        float currentSpeed = object.getNode().getLinearVelocity().dot(object.getNode().getLocalRotation().getRotationColumn(2));
        float thrust = object.getSpeed();
        if (Math.round(thrust) > 0) {
//        	log.info("applying: " +thrust +" to missile");
            object.getNode().applyCentralForce(new Vector3f(object.getNode().getLocalRotation().getRotationColumn(2).mult(thrust * time)));
        }
        this.setActive(true);
    }
}
