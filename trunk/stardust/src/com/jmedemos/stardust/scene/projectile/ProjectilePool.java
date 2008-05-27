package com.jmedemos.stardust.scene.projectile;

import java.util.ArrayList;
import java.util.List;
/**
 * A ResourcePool for Projectiles. Subclasses have to
 * implement {@link #newInstance()} to create instances of the pooled Object.
 * 
 * @author rvanrijn
 *
 * @param <P> the type of Projectile to pool
 */
public abstract class ProjectilePool<P extends Projectile> {
	
	/**
	 * The pool of Projectiles.
	 */
	private List<P> pool;
	
	/**
	 * Constructs an empty ProjectilePool.
	 */
	public ProjectilePool() {
		this.pool = new ArrayList<P>();
	}
	
	/**
	 * Returns a Projectile from the pool.
	 * 
	 * @return the Projectile
	 */
	public synchronized P get() {
		P projectile = null;
		for (P p: pool) {
			if (!p.isActive()) {
				projectile = p;
			}
		}
		if (projectile == null) {
			projectile = newInstance();
			pool.add(projectile);
		}
		projectile.setActive(true);
		return projectile;
	}

	/**
	 * Returns a new instance of the pooled Projectile. To be implemented by
	 * subclass because Java does not allow newInstance on generic types.
	 * 
	 * @return a new instance of the pooled Projectile
	 */
	protected abstract P newInstance();
	
}
