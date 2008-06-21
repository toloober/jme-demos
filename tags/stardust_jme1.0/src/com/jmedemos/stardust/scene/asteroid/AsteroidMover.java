package com.jmedemos.stardust.scene.asteroid;

import java.util.concurrent.Callable;

import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jmedemos.stardust.sound.SoundUtil;

/**
 * Moves the asteroid towards hist target.
 */
@SuppressWarnings("serial")
public class AsteroidMover extends Controller {

    private DisplaySystem display = DisplaySystem.getDisplaySystem();

    /**
     * reference to the asteroid to be moved.
     */
    private Asteroid asteroid = null;

    /**
     * target position.
     */
    private Vector3f targetPos = null;

    /**
     * speed of the asteroid.
     */
    private int speed = 0;

    /**
     * helper.
     */
    private Node tempNode = new Node();

    /**
     * constructor.
     * @param asteroid reference to the asteroid.
     * @param targetPos the target.
     * @param speed the speed.
     */
    public AsteroidMover(final Asteroid asteroid, final Vector3f targetPos,
            final int speed) {
        this.asteroid = asteroid;
        this.targetPos = targetPos;
        this.speed = speed;
    }

    /**
     * move the asteroid towards its target with the defined speed.
     * @param time time since last frame.
     */
    @Override
    public final void update(final float time) {
        tempNode.getLocalTranslation().set(asteroid.getPhysNode().getLocalTranslation());
        tempNode.getLocalRotation().set(asteroid.getPhysNode().getLocalRotation());
        tempNode.updateWorldVectors();
        tempNode.lookAt(targetPos, Vector3f.UNIT_Y);

        asteroid.getPhysNode().getLocalTranslation().addLocal(
                tempNode.getLocalRotation().getRotationColumn(2).mult(
                        time * speed));

        GameTaskQueueManager.getManager().update(new Callable<Object>() {
			public Object call() throws Exception {
				SoundUtil.get().getTrackers().get(
						asteroid.getPhysNode()).checkTrackAudible(
								display.getRenderer().getCamera().getLocation());
				return null;
			}
		});
    }
}
