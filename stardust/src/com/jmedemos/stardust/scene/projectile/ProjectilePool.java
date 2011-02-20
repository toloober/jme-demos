package com.jmedemos.stardust.scene.projectile;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.jmedemos.stardust.scene.EntityManager;

/**
 * A ResourcePool for Projectiles. Subclasses have to
 * implement {@link #newInstance()} to create instances of the pooled Object.
 * 
 * @author rvanrijn
 *
 * @param <P> the type of Projectile to pool
 */
public abstract class ProjectilePool<P extends Projectile> {
    /** our logger */
	private Logger log = Logger.getLogger(ProjectilePool.class.getName());
	/** The pool of Projectiles. */
	private List<P> pool;
	
	/**
	 * Constructs an empty ProjectilePool.
	 */
	public ProjectilePool() {
		this.pool = new ArrayList<P>(100);
	}
	
	/**
	 * Returns a Projectile from the pool.
	 * @return the Projectile
	 */
	public P get() {
		P projectile = null;
		for (P p: pool) {
			if (!p.isActive()) {
			    log.finer("recycling projectile");
				projectile = p;
			}
		}
		if (projectile == null) {
		    log.info("creating new projectile, pool size: " +pool.size());
			projectile = newInstance();
			pool.add(projectile);
		}
		projectile.getNode().activate();
		projectile.setActive(true);
		EntityManager.get().addEntity(projectile);
		projectile.setAge(projectile.getLifeTime());
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
