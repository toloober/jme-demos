package com.jmedemos.astar_pathfinder.world;

import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

/**
 * A tagging interface for an object representing the entity in the game that
 * is going to moving along the path. This allows us to pass around entity/state
 * information to determine whether a particular tile is blocked, or how much
 * cost to apply on a particular tile.
 * 
 * For instance, a Mover might represent a tank or plane on a game map. Passing round
 * this entity allows us to determine whether rough ground on a map should effect
 * the unit's cost for moving through the tile.
 */
public interface Mover {
	
	/**
	 * @return the model
	 */
	public Spatial getModel();
	
	/**
	 * Set model LocalTranslation with y at 0.1f
	 * @param float x coordinate to move to
	 * @param float y coordinate to move to
	 */
	public void setModelLocation(float x, float y);
	
	/**
	 * @return the model LocalTranslation
	 */
	public Vector3f getModelLocation();

}
