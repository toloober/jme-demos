package com.jmedemos.stardust.scene.asteroid;

import java.util.logging.Logger;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmedemos.stardust.effects.ParticleEffectFactory;
import com.jmedemos.stardust.scene.EntityManager;
import com.jmedemos.stardust.sound.SoundUtil;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

/**
 * The Asteroid creates different kind of asteroids.
 * The Factory holds a reference to the scenes root node and physics space.
 */
public class AsteroidFactory {
    private Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * reference to Scene root Node.
     */
    private Node rootNode = null;

    /**
     * reference to physics node.
     */
    private PhysicsSpace physicsSpace = null;

    /**
     * The constructor needs the scenes root node and physics space. 
     * @param root reference to Root-Node
     * @param physics reference to PhysicsSpace
     */
    public AsteroidFactory(final Node root, final PhysicsSpace physics) {
        this.rootNode = root;
        this.physicsSpace = physics;
    }

    /**
     * creates a static asteroid.
     * @param asteroidName asteroid name
     * @param modelName name of the model
     * @param scale scaling.
     * @param startPos spawn position
     * @return reference to a new asteroid.
     */
    public final Asteroid createStaticAsteroid(final String asteroidName,
           final String modelName, final float scale, final Vector3f startPos) {

        Asteroid asteroid = new Asteroid(asteroidName, modelName, scale,
                this.physicsSpace);

        DynamicPhysicsNode physNode = asteroid.getPhysNode();
        physNode.getLocalTranslation().set(startPos);

        rootNode.attachChild(physNode);
        physNode.updateRenderState();
        
        EntityManager.get().addEntity(asteroid);
        
        return asteroid;
    }

    /**
     * creates a rotating asteroid.
     * @param asteroidName asteroid name
     * @param modelName name of the model
     * @param scale scaling.
     * @param startPos spawn position
     * @param rotation rotation
     * @return reference to a new asteroid.
     */
    public final Asteroid createRotatingAsteroid(final String asteroidName,
            final String modelName, final float scale, final Vector3f startPos,
            final Vector3f rotation) {

        Asteroid asteroid = new Asteroid(asteroidName, modelName, scale,
                this.physicsSpace);

        DynamicPhysicsNode physNode = asteroid.getPhysNode();
        physNode.getLocalTranslation().set(startPos);
        physNode.addTorque(rotation);

        if (asteroid != null) {
            rootNode.attachChild(physNode);
            rootNode.attachChild(asteroid.getParticleGeom());
            physNode.updateRenderState();
        } else {
            log.severe("Unbekannter Asteroid:" + modelName);
        }
        
        EntityManager.get().addEntity(asteroid);
        return asteroid;
    }

    /**
     * creates a rotating asteroid which flys towrds a target.
     * @param asteroidName asteroid name
     * @param modelName name of the model
     * @param scale scaling.
     * @param startPos spawn position
     * @param rotation rotation
     * @param targetPos target
     * @param speed speed
     * @return reference to new asteroid.
     */
    public final Asteroid createAsteroidWithTarget(final String asteroidName,
           final String modelName, final float scale, final Vector3f startPos,
           final Vector3f targetPos, final Vector3f rotation, final int speed) {

        Asteroid asteroid = new Asteroid(asteroidName, modelName, scale,
                this.physicsSpace);

        // static sound of the asteroid (noise) 
        SoundUtil.get().addFx("/data/sounds/asteroid.wav", asteroid.getPhysNode());

        DynamicPhysicsNode physNode = asteroid.getPhysNode();
        physNode.getLocalTranslation().set(startPos);
        physNode.updateGeometricState(0, true);
        physNode.addTorque(rotation.mult(100));
        // create the asteroid trail
        physNode.attachChild(ParticleEffectFactory.get().getAsteroidTrail(asteroid.getModel()));
        physNode.addController(new AsteroidMover(asteroid, targetPos, speed));
        
        rootNode.attachChild(physNode);
        rootNode.updateGeometricState(0, true);
        physNode.updateRenderState();
        
        return asteroid;
    }
}
