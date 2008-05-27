package com.jmedemos.stardust.scene.projectile;

import java.util.logging.Logger;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmedemos.stardust.scene.EntityManager;
import com.jmedemos.stardust.scene.MissileCamera;
import com.jmedemos.stardust.scene.PlayerShip;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.callback.FrictionCallback;

/**
 * factory class to create different projectils.
 * 
 * the type of the projectile can either set directly as a parameter 
 * to createProjectile(), or the currently in the factory set type can be used.
 */
public class ProjectileFactory {

    /**
     * reference to the missile cam.
     */
    private MissileCamera missileCam;
    
    /**
     * reference to logger.
     */
    private Logger log = Logger.getLogger(ProjectileFactory.class.getName());

    /**
     * reference to root node.
     */
    private Node rootNode = null;

    /**
     * reference to physics space.
     */
    private PhysicsSpace physics = null;

    /**
     * Current projectile type.
     */
    private ProjectileType type = ProjectileType.BULLET;
    
    private BulletProjectilePool bulletProjectilePool;
    
    private MissileProjectilePool missileProjectilePool;

    /**
     * projectil factory constructor.
     * the root-node and physics space are needed to spwan new projectiles.
     * 
     * @param rootNode reference to root node.
     * @param physicsreference to physics space.
     * @param type projectile type.
     */
    public ProjectileFactory(final Node rootNode, final PhysicsSpace physics,
            final ProjectileType type, final MissileCamera mc) {
        this.rootNode = rootNode;
        this.physics = physics;
        this.type = type;
        missileCam = mc;
        
        this.bulletProjectilePool = new BulletProjectilePool(physics);
        this.missileProjectilePool = new MissileProjectilePool(physics);
    }

    /**
     * possible projectile types.
     */
    public enum ProjectileType {
        BULLET, MISSILE
    };

    /**
     * create a new projectile from a specific type.
     * The current internal type will be ignored. 
     * 
     * @param type projectil type to create.
     * @param direction direction in which the projectil should fly.
     * @param start spawnpoint of the projectile.
     * @param rotation rotation of the ship when firing.
     * @return reference to the newly created projectile.
     */
    public final Projectile createProjectile(final ProjectileType type) {
        Projectile p = null;
        switch (type) {
        case BULLET:
            p = bulletProjectilePool.get();
            break;
        case MISSILE:
            p = missileProjectilePool.get();
            break;
        default:
            break;
        }

        if (p != null) {
            // attach projectile to root node.
            rootNode.attachChild(p.getNode());
            p.getNode().updateRenderState();
            EntityManager.get().addEntity(p);
        } else {
            log.severe("Unknown projektil type:" + type);
        }
        return p;
    }

    /**
     * create a new projectile with the currently set type in the projectilefactory
     * @param direction direction in which the projectil should fly.
     * @param start spawn point of the projectile.
     * @param rotation rotation of the ship when firing.
     * @return reference to the newly created projectile.
     */
    public final Projectile createProjectile() {
        return createProjectile(this.type);
    }

    public final Projectile createHomingMissile(final PlayerShip player,
            final Vector3f direction, final Vector3f start,
            final Quaternion rotation) {
        
        MissileProjectile p = missileProjectilePool.get();
        rootNode.attachChild(p.getNode());
        p.getNode().updateRenderState();
        EntityManager.get().addEntity(p);
        
        // remove default ProjectileMover, add HomingDevice
        p.getNode().removeController(0);
        p.getNode().addController(new HomingDevice(p, player.getTargetDevice().getCurrentTarget()));
        p.getNode().setLinearVelocity(direction.mult(player.getPhysicsThrustController().getThrottle()));
        FrictionCallback fcb = new FrictionCallback();
        fcb.add(p.getNode(), 1000, 0);
        physics.addToUpdateCallbacks(fcb);
        
        p.getNode().attachChild(missileCam.getCameraNode());
        p.getNode().updateGeometricState(0, true);
        return p;
    }
    
    /**
     * currently set projectile type.
     * @return current projectile type.
     */
    public final ProjectileType getType() {
        return type;
    }

    /**
     * @param typeprojectile type-
     */
    public final void setType(final ProjectileType type) {
        this.type = type;
    }
}
