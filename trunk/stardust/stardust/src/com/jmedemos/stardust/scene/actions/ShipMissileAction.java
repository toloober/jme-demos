package com.jmedemos.stardust.scene.actions;

import com.jme.math.Vector3f;
import com.jmedemos.stardust.scene.PlayerShip;
import com.jmedemos.stardust.scene.projectile.Projectile;
import com.jmedemos.stardust.scene.projectile.ProjectileFactory;
import com.jmedemos.stardust.sound.SoundUtil;

/**
 * 
 */
public class ShipMissileAction implements Runnable {
    
    private PlayerShip ship = null;

    public ShipMissileAction(final PlayerShip ship) {
        this.ship = ship;
    }

    /**
     * TODO
     * start location of the missile need to be outside the player bound.
     */
    public final void run() {
        if (ship.getTargetDevice().getCurrentTarget() == null)
            return;
        
        Vector3f direction = ship.getNode().getLocalRotation().getRotationColumn(2);

        // fire one missile 
        Projectile p =  ProjectileFactory.get().createHomingMissile(ship);
        p.fire(direction,
               ship.getNode().getLocalTranslation().add(direction.mult(100)),
               ship.getNode().getLocalRotation());
        // Play sound
        SoundUtil.get().playSFX(SoundUtil.BG_MISSILE_SHOT);
    }
}
