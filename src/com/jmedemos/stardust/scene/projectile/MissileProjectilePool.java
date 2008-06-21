package com.jmedemos.stardust.scene.projectile;

import com.jmex.physics.PhysicsSpace;

/**
 * A ProjectilePool for MissileProjectiles.
 * 
 * @author rvanrijn
 */
public class MissileProjectilePool extends ProjectilePool<MissileProjectile>{
	
	/**
	 * Reference to the physics space.
	 */
	private PhysicsSpace physics;
	
	/**
	 * Constructs a new MissileProjectilePool.
	 * 
	 * @param physics  reference to the physics space
	 */
	public MissileProjectilePool(final PhysicsSpace physics) {
		this.physics = physics;
	}
	
	@Override
	protected MissileProjectile newInstance() {
		return new MissileProjectile(physics);
	}

}
