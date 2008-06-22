package com.jmedemos.stardust.ai;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;

/**
 * This controller moves a node towards a target node with a given
 * speed and agility.
 * 
 * @author Christoph Luder
 */
public class ChaseController extends Controller {
    private static final long serialVersionUID = 1L;
    private Node me = null;
    private Node target = null;
    private AIMode mode = null;
    private float speed = 0;
    private float agility = 0;
    private float evadeBoost = 2;
    
    public ChaseController(final Node me, final Node target, float speed, float agility) {
        this.me = me;
        this.target = target;
        this.speed = speed;
        this.agility = agility;
    }
    
    /**
     * Chases the target.
     * If no target is set we do wait and see...
     * TODO switch to physics based moving ?
     */
    public void update(float time) {
        if (target != null) {
            Quaternion oldRot = new Quaternion(me.getLocalRotation());
            me.updateWorldVectors();
            me.lookAt(target.getLocalTranslation(), Vector3f.UNIT_Y);
            
            // if we get too near, flee
            if (me.getLocalTranslation().distance(
                    target.getLocalTranslation()) < 500) {
                mode = AIMode.EVADE;
            }
            
            // if we are far enough away, start the attack
            if (me.getLocalTranslation().distance(
                    target.getLocalTranslation()) > 2500) {
                mode = AIMode.CHASE;
            }
            
            if (mode == AIMode.EVADE) {
                // move away from the target
                Quaternion newRot = me.getLocalRotation().inverse();
                me.getLocalRotation().set(oldRot);
                me.getLocalRotation().slerp(newRot, agility*time*evadeBoost);
                me.getLocalTranslation().addLocal(
                        me.getLocalRotation().getRotationColumn(2).mult(speed*time*evadeBoost));
            } else {
                // move towards the target
                Quaternion newRot = new Quaternion(me.getLocalRotation());
                me.getLocalRotation().set(oldRot);

                me.getLocalRotation().slerp(newRot, agility*time);
                me.getLocalTranslation().addLocal(
                        me.getLocalRotation().getRotationColumn(2).mult(speed*time));
            }
        }
    }
    
    public void setTarget(final Node target) {
        this.target = target;
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