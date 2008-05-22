package com.jmedemos.stardust.scene.actions;

import com.jme.math.Vector3f;
import com.jmedemos.stardust.scene.PlayerShip;
import com.jmedemos.stardust.sound.SoundUtil;

/**
 * Not Used yet
 */
public class ShipMissileAction implements Runnable {

    private PlayerShip ship = null;

    public ShipMissileAction(final PlayerShip ship) {
        this.ship = ship;
    }

    public final void run() {
        if (ship.getTargetDevice().getCurrentTarget() == null)
            return;
        
        Vector3f direction = ship.getNode().getLocalRotation()
                                    .getRotationColumn(2);

        // fire 1 missile 
        ship.getWeapon().createHomingMissile(ship, direction,
                ship.getNode().getLocalTranslation().add(direction.mult(1)),
                ship.getNode().getLocalRotation());
        // Play sound
        SoundUtil.get().playSFX(SoundUtil.BG_MISSILE_SHOT);
    }
};
