package com.jmedemos.stardust.scene.powerups;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.shape.RoundedBox;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmedemos.stardust.scene.PhysicsEntity;
import com.jmex.physics.PhysicsSpace;

/**
 * A powerup deals Negatve Damage to the other Entity on collision.
 * @author Christoph Luder
 */
public class HealthPowerUp extends PhysicsEntity {
	/**
	 * package wide visible constructor.
	 * PowerUp's are created by the PowerUpManager.
	 */
	HealthPowerUp(final PhysicsSpace space) {
		super(space, null, 1, true);
		node.setName("Health Power Up");
		// entities who receive 'damage' from us, will be healed!
		damage = -100;
		health = 1;
	}
	
	@Override
	protected void initModel() {
	    model = new RoundedBox("health", new Vector3f(1,1,1));
	    model.setModelBound(new BoundingBox());
	    model.updateModelBound();
	    model.setLocalScale(25);
	    TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	    ts.setTexture(TextureManager.loadTexture(
	            ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "health_powerup.png")));
	    model.setRenderState(ts);
	}
}
