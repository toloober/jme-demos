package com.jmedemos.stardust.scene.projectile;

import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;

public class HomingDevice extends Controller {

    private static final long serialVersionUID = 1L;
    private MissileProjectile object;
    private Node target;
    
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
        object.setLifeTime(object.getLifeTime()-time);
        if (object.getLifeTime() < 0) {
            object.die();
            return;
        }
        
        target.updateWorldVectors();
        object.getNode().updateWorldVectors();
        object.getNode().lookAt(target.getWorldTranslation(), Vector3f.UNIT_Y);

        float currentSpeed = object.getNode().getLinearVelocity(null).dot(object.getNode().getLocalRotation().getRotationColumn(2));
        float thrust = object.getSpeed()*50-currentSpeed;
        if (Math.round(thrust) > 0) {
            object.getNode().addForce(new Vector3f(object.getNode().getLocalRotation().getRotationColumn(2).mult(thrust * time)));
        }
    }
}
