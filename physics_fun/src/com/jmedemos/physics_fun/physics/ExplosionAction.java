package com.jmedemos.physics_fun.physics;

import jmetest.TutorialGuide.ExplosionFactory;

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.effects.particles.ParticleMesh;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

/** This action will produce an explosion which destroys the given node. */
public class ExplosionAction extends InputAction {
        private DynamicPhysicsNode node = null;
        private boolean hasBeenInvoked = false;

        public ExplosionAction(DynamicPhysicsNode node) {
            this.node = node;
        }

        public void performAction(InputActionEvent evt) {
            // Here to prevent multiple entries for the same instance.
            // Seem to get 5 of these (including original) for same explosion. No sure why.
            // Maybe different parts of ragdoll hitting it.
            if (hasBeenInvoked) return;
            hasBeenInvoked = true;

            // Cache values.
            PhysicsSpace space = node.getSpace();
            Vector3f center = node.getWorldTranslation();

            // Remove the node that has just exploded.
            Node root = node.getParent();
            node.setActive(false);
            node.removeFromParent();

            // Cause explosion.
            ExplosionManager.createExplosion(space, center, 1000f, 20f);
            ParticleMesh exp = ExplosionFactory.getSmallExplosion(); 
            exp.setLocalScale(0.2f);
            root.attachChild(exp);
            exp.forceRespawn();
        }
    }