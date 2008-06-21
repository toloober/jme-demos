package com.jmedemos.stardust.scene.projectile;

import com.jme.scene.Controller;

/**
 * the controller (engine) of a projectile.
 * the controller moves the projectile every updatecycle forward.
 */
@SuppressWarnings("serial")
public class ProjectileMover extends Controller {
    /**
     * reference to the projectile.
     */
    private Projectile projectile = null;

    /**
     * Constructor takes the projectile to move as a parameter.
     * @param projectile reference to the projectile.
     */
    public ProjectileMover(final Projectile projectile) {
        this.projectile = projectile;
    }

    /**
     * the update() method moves the projectile forward with the defined speed.
     * When the Lifetime of the projectile runs out, it will be removed from the scene.
     * @param time time since last update
     */
    public final void update(final float time) {
        projectile.setAge(projectile.getAge() - time);
        if (projectile.getAge() < 0) {
            projectile.die();
            return;
        }
        projectile.getNode().getLocalTranslation().addLocal(
        		projectile.getDirection().mult(time * projectile.getSpeed()));
    }
}
