package com.jmedemos.stardust.scene.actions;

import com.jme.math.Vector3f;
import com.jmedemos.stardust.scene.PlayerShip;
import com.jmedemos.stardust.sound.SoundUtil;

/**
 * fires projectiles created by the ProjectilFactory.
 */
public class ShipWeaponAction implements Runnable {

    /**
     * reference to player ship.
     */
    private PlayerShip ship = null;

    /**
     * constructor.
     * @param ship reference to the players ship.
     */
    public ShipWeaponAction(final PlayerShip ship) {
        this.ship = ship;
    }

    /**
     * creates new projectiles.
     * Dependant on the current type set in the ProjectileFactory, 
     * bullets or Missiles are created.
     */
    public final void run() {
        Vector3f direction = ship.getNode().getLocalRotation()
                .getRotationColumn(2);
        
        ship.getNode().updateWorldVectors();
        // update world vectors
        ship.getUpperLeftWeapon().updateWorldVectors();
        ship.getUpperRightWeapon().updateWorldVectors();
        ship.getLowerLeftWeapon().updateWorldVectors();
        ship.getLowerRightWeapon().updateWorldVectors();
        
        // Fire 4 bullets
        ship.getWeapon().createProjectile(direction,
                ship.getUpperLeftWeapon().getWorldTranslation(),
                ship.getNode().getWorldRotation());
        ship.getWeapon().createProjectile(direction,
                ship.getUpperRightWeapon().getWorldTranslation(),
                ship.getNode().getWorldRotation());
        ship.getWeapon().createProjectile(direction,
                ship.getLowerLeftWeapon().getWorldTranslation(),
                ship.getNode().getLocalRotation());
        ship.getWeapon().createProjectile(direction,
                ship.getLowerRightWeapon().getWorldTranslation(),
                ship.getNode().getLocalRotation());
        // Play sound
        SoundUtil.get().playSFX(SoundUtil.BG_SOUND_SHOT);
    }
};
