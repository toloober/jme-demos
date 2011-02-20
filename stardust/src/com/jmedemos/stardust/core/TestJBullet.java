package com.jmedemos.stardust.core;

import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Pyramid;
import com.jmex.jbullet.PhysicsSpace;
import com.jmex.jbullet.nodes.PhysicsNode;

public class TestJBullet extends SimpleGame {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TestJBullet game = new TestJBullet();
		game.setConfigShowMode(ConfigShowMode.AlwaysShow);
		game.start();
	}
	PhysicsSpace space;
	@Override
	protected void simpleInitGame() {
		
		space = PhysicsSpace.getPhysicsSpace();
//		space.setGravity(new Vector3f(0, 0, 0));
		
		Box b = new Box("box", new Vector3f(), 1, 1, 1);
		Box floor = new Box("floor", new Vector3f(-50, -1, -50), new Vector3f(50, 1, 50));
		
		
		
		PhysicsNode physicsBox = new PhysicsNode(b);
//		physicsBox.setFriction(0);
		physicsBox.setMass(0.1f);
		
		physicsBox.setLocalTranslation(0, 5, 0);
		
		
		PhysicsNode physicsFloor = new PhysicsNode(floor);
		physicsFloor.setMass(0);
		physicsFloor.setLocalTranslation(0, -20, 0);
		

		space.add(physicsFloor);
		space.add(physicsBox);
		
		rootNode.attachChild(physicsBox);
		rootNode.attachChild(physicsFloor);
		
	}
	
	@Override
	protected void simpleUpdate() {
		super.simpleUpdate();
		
		space.update(tpf);
	}
}
