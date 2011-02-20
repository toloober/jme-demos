package com.jmedemos.stardust.scene.projectile;

import com.jmex.jbullet.PhysicsSpace;

/**
 * A ProjectilePool for BulletProjectiles.
 * 
 * @author rvanrijn
 */
public class BulletProjectilePool extends ProjectilePool<BulletProjectile> {
	
	/**
	 * Reference to the physics space.
	 */
	private PhysicsSpace physics;
	
	/**
	 * Constructs a new BulletProjectilePool.
	 * 
	 * @param physics  reference to the phyics space
	 */
	public BulletProjectilePool(final PhysicsSpace physics) {
		this.physics = physics;
	}
	
	@Override
	protected BulletProjectile newInstance() {
		return new BulletProjectile(physics);
	}

}

