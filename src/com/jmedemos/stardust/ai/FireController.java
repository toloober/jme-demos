package com.jmedemos.stardust.ai;

import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.util.Timer;
import com.jmedemos.stardust.scene.projectile.ProjectileFactory;
import com.jmedemos.stardust.scene.projectile.ProjectileFactory.ProjectileType;
import com.jmedemos.stardust.sound.SoundUtil;

/**
 * Fire at the target if we are close enough.
 * @author Christoph Luder
 */
public class FireController extends Controller {
    private static final long serialVersionUID = 1L;
    private Node me = null;
    private Node target = null;
    private Node scene = null;
    private float weaponRange = 0;
    private boolean freeSight = false;
    
    /**
     * @param me the Entity which is firing
     * @param target the target to shoot at
     * @param weaponRange max distance to be able to fire
     * @param scene the scene to check for obstacles
     */
    public FireController(final Node me, final Node target, float weaponRange,
                            final Node scene) {
        this.me = me;
        this.target = target;
        this.weaponRange = weaponRange;
        this.scene = scene;
    }
    
    float lastupdate = 0;
    float updateRate = 0.3f;
    
    /**
     * Check if the player is in the enemies field of view.
     * 
     */
    @Override
    public void update(float time) {
        float cur = Timer.getTimer().getTimeInSeconds();
        if (lastupdate + updateRate < cur) {
            lastupdate = cur;
        } else {
            return;
        }
        if (!isVisible()) {
            return;
        }
        fire();
    }
    
    /**
     * TODO
     * we need to move the bullet in front of the enemy.
     * compute the direction to shoot at, based on the targets
     * movement speed and direction
     */
    private void fire() {
        Vector3f targetDirection = target.getLocalTranslation().subtract(
                me.getLocalTranslation()).normalizeLocal();
        
        ProjectileFactory.get().createProjectile(ProjectileType.BULLET).fire(
                targetDirection,
                me.getLocalTranslation().add(targetDirection.mult(50)),
                me.getLocalRotation());
        
//        SoundUtil.get().playSFX(SoundUtil.BG_BULLET_SHOT);
        SoundUtil.get().playEnemyfire(me.getLocalTranslation());
    }
    
    /**
     * Checks if the target is in range and in field of view.
     * @return
     */
    private boolean isVisible() {
        if (me.getLocalTranslation().distance(target.getLocalTranslation()) > weaponRange) {
            // to far away
            return false;
        }
        
        // get the direction from us to the player
        Vector3f direction = target.getLocalTranslation().subtract(
                                       me.getLocalTranslation()).normalizeLocal();
        // get the difference between the Enemy's heading direction and the
        Vector3f difference = me.getLocalRotation().getRotationColumn(2).subtract(direction);
        
        if (difference.length() > 0.5f) {
            // 2   == 180°
            // 1   == 90°
            // 0.5 == 45°
            // not in my field of view
            return false;
        }
        
        Ray ray = new Ray(me.getLocalTranslation(), direction);
        PickResults results = new BoundingPickResults();
        results.setCheckDistance(true);
        scene.findPick(ray, results);
        
        freeSight = false;
        for(int i = 0; i < results.getNumber(); i++) {
            Geometry geom = results.getPickData(i).getTargetMesh().getParentGeom();
            if (geom.getParent().getParent() == me) {
                // oops, ignore that
                continue;
            }
            if (isTarget(geom.getParent())) {
                // got our target   
                freeSight = true;
                break;
            } else {
                // our ray hit something else than the player
                freeSight = false;
                break;
            }
        }
        return freeSight;
    }
    
    /**
     * Check if this node or any of its Parents is our Target
     * @param node the node to check.
     * @return true if this is our target.
     */
    private boolean isTarget(final Node node) {
        while (node.getParent() != null) {
            if (node == target) {
                return true;
            }
            return isTarget(node.getParent());
        }
        return false;
    }

    public boolean isFreeSight() {
        return freeSight;
    }
}
