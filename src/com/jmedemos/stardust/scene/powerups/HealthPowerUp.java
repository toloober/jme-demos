package com.jmedemos.stardust.scene.powerups;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.RoundedBox;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmedemos.stardust.scene.Entity;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.material.Material;

/**
 * A powerup deals Negatve Damage to the other Entity on collision.
 * @author Christoph Luder
 */
public class HealthPowerUp extends Entity {
	private DynamicPhysicsNode node = null; 
	
	/**
	 * package wide visible constructor.
	 * PowerUp's are created by the PowerUpManager.
	 */
	HealthPowerUp(final PhysicsSpace space) {
		// entities who receive 'damage' from us, will be healed!
		damage = -100;
		health = 1;
		
		RoundedBox geom = new RoundedBox("health", new Vector3f(1,1,1));
		geom.setModelBound(new BoundingBox());
		geom.updateModelBound();
		geom.setLocalScale(25);
		TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		ts.setTexture(TextureManager.loadTexture(
				ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "health_powerup.png")));
		geom.setRenderState(ts);
		node = space.createDynamicNode();
		node.setName("power up");
		node.attachChild(geom);
		node.generatePhysicsGeometry();
		node.setMaterial(Material.WOOD);
		node.computeMass();
	}

	@Override
	public Node getNode() {
		return node;
	}
}
