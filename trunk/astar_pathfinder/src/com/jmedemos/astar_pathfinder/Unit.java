package com.jmedemos.astar_pathfinder;


import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jmedemos.astar_pathfinder.world.Mover;



/**
 * An object representing the entity in the game that
 * is going to moving along the path. This allows us to pass around entity/state
 * information to determine whether a particular tile is blocked, or how much
 * cost to apply on a particular tile.
 * 
 * For instance, a Mover might represent a tank or plane on a game map. Passing round
 * this entity allows us to determine whether rough ground on a map should effect
 * the unit's cost for moving through the tile.
 */
public class Unit implements Mover{
	/** The unit ID moving */
	private int type;
	/** Unit model in the world */
	private Spatial model;
	
	private static final float z = 0.001f;
	
	/**
	 * Create a new mover to be used with path finder
	 * 
	 * @param type The ID of the created unit
	 * @param x coordinate of the created unit
	 * @param y coordinate of the created unit
	 * @param model of the created unit
	 */
	public Unit(int type, int x, int y, Spatial model) {
		this.type = type;
		this.model = model;
		this.model.setLocalTranslation(x, y, z);
	}
	

	/**
	 * Create a new mover to be used with path finder
	 * 
	 * @param name
	 * @param x
	 * @param y
	 * @param type
	 * @param display
	 * @param texture
	 */
	public Unit(int x, int y, int type, DisplaySystem display, Texture texture) {
		this.type = type;
		this.model = new Quad("Unit"+type+"_x"+x+":"+y , 1, 1);
		// Texture the object
		TextureState ts = display.getRenderer().createTextureState();
		ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(texture);
        this.model.setRenderState(ts);
    	// Make unit icons blend with what is already there on the screen
    	AlphaState as = display.getRenderer().createAlphaState();
    	as.setBlendEnabled(true);
        as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as.setTestEnabled(true);
        as.setTestFunction(AlphaState.TF_GREATER);
        this.model.setRenderState(as);
        this.model.setLocalTranslation(x, y, z);
        // Add model bounds to quad so that they could be picked
        this.model.setModelBound(new BoundingBox() );
        this.model.updateModelBound();
	}
	
	/**
	 * Get the ID of the unit moving
	 * @return The ID of the unit moving
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the model
	 */
	public Spatial getModel() {
		return model;
	}
	
	/**
	 * @return the model LocalTranslation
	 */
	public Vector3f getModelLocation(){
		return model.getLocalTranslation();
	}
	
	/**
	 * Set model LocalTranslation with y at 0.1f
	 * @param float x coordinate to move to
	 * @param float y coordinate to move to
	 */
	public void setModelLocation(float x, float y){
		model.setLocalTranslation(x, y, z);
	}
	
	/**
	 * Set model LocalTranslation to specified vector
	 * @param Vector3f location where to move the model
	 */
	public void setModelLocation(Vector3f location){
		model.setLocalTranslation(location);
	}
	
}
