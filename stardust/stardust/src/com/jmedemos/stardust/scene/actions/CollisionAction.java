package com.jmedemos.stardust.scene.actions;

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jmedemos.stardust.scene.Entity;
import com.jmedemos.stardust.scene.EntityManager;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.contact.ContactInfo;

/**
 *  Global physics collision handler. 
 */
public class CollisionAction extends InputAction {
    
    /**
     * The action which gets executed when 2 physic nodes collide.
     * InputAvtionEvent.getTriggerData return a contactInfo object
     * with information like:
     *   which nodes collide, position where the objects collided.
     *  
     * @param evt event with contact infos
     */
    public void performAction(final InputActionEvent evt) {
        // the TriggerData of this event Events is of Type ContactInfo
        final ContactInfo info = ((ContactInfo) evt.getTriggerData());
        PhysicsNode node1 = (PhysicsNode) info.getNode1();
        PhysicsNode node2 = (PhysicsNode) info.getNode2();

        if (node1.isActive() == false && 
            node2.isActive() == false) {
            // nothing to do
            return;
        }
        
        // if the node is not active, we ignore it
        if (node1.isActive() == false) {
            node1 = null;
        }
        if (node2.isActive() == false) {
            node2 = null;
        }

        Entity e1 = EntityManager.get().getEntity(node1);
        Entity e2 = EntityManager.get().getEntity(node2);
        
        if (e1 == null || e2 == null) {
            // there were not two Entities involved
            return;
        }
        
        e1.doCollision(e2);
        e2.doCollision(e1);
    }
}
