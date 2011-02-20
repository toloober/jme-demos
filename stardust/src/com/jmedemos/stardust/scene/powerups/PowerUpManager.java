package com.jmedemos.stardust.scene.powerups;

import java.util.logging.Logger;

import com.jmex.jbullet.PhysicsSpace;

public class PowerUpManager {
	private static PowerUpManager instance = null;
	private PhysicsSpace space = null;
	
	private PowerUpManager (final PhysicsSpace space) {
		this.space = space;
	}
	
	public static void create(final PhysicsSpace space) {
		instance = new PowerUpManager(space);
	}
	
	public static PowerUpManager get () {
		if (instance == null) {
			Logger.getAnonymousLogger(PowerUpManager.class.getName()).severe(
					"EnemyFactory not yet initialized");
			return null;
		}
		return instance;
	}
	
	public HealthPowerUp createHealthPowerUp() {
		HealthPowerUp powerup = new HealthPowerUp(space);
		return powerup;
	}
}
