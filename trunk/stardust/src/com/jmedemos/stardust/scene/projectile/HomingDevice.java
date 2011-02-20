package com.jmedemos.stardust.scene.projectile;

import java.util.logging.Logger;

import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;

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
        object.setAge(object.getAge()-time);
        if (object.getLifeTime() < 0) {
            target = null;
            object.die();
            return;
        }
        
        target.updateWorldVectors();
        
        // TODO workaround 
        object.getNode().updateWorldVectors();
        this.setActive(false);
        object.getNode().lookAt(target.getLocalTranslation(), Vector3f.UNIT_Y);
        this.setActive(true);
        
//        float currentSpeed = object.getNode().getLinearVelocity().dot(object.getNode().getLocalRotation().getRotationColumn(2));
        float thrust = object.getCurrentSpeed()*2;
        if (Math.round(thrust) > 0) {
        	log.info("applying: " +thrust +" to missile");
        	object.getNode().clearForces();
            object.getNode().applyCentralForce(new Vector3f(object.getNode().getLocalRotation().getRotationColumn(2).mult(thrust * time)));
        }
    }
}
