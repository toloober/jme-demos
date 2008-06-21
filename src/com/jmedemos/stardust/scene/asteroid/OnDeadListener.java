package com.jmedemos.stardust.scene.asteroid;

import com.jmex.effects.particles.ParticleControllerListener;
import com.jmex.effects.particles.ParticleGeometry;

/**
 * Gets called a soon the particle trail of an asteroid/missile dies.
 * removes the asteroid from the scene.
 */
public class OnDeadListener implements ParticleControllerListener {

    /**
     * remove the asteroid/missile from the scene.
     */
    public void onDead(final ParticleGeometry particles) {
//        ObjectRemover.get().addObject(particles.getParent());
        particles.getParent().removeController(0);
        particles.getParent().removeFromParent();
        particles.getParent().detachAllChildren();
    }
}
