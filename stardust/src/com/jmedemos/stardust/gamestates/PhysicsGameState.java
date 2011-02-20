package com.jmedemos.stardust.gamestates;

import com.jmex.game.state.BasicGameState;
import com.jmex.jbullet.PhysicsSpace;

public class PhysicsGameState extends BasicGameState {
	PhysicsSpace physicsSpace = PhysicsSpace.getPhysicsSpace();
	public PhysicsGameState(String name) {
		super(name);
	}
	
	public PhysicsSpace getPhysicsSpace() {
		return physicsSpace;
	}
	
	@Override
	public void update(float tpf) {
		super.update(tpf);
		physicsSpace.update(tpf);
	}
}
