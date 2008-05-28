package com.jmedemos.stardust.enemy;

import java.util.Random;
import java.util.logging.Logger;

import com.jme.scene.Node;
import com.jmedemos.stardust.scene.EntityManager;
import com.jmex.physics.PhysicsSpace;

/** TODO replaced with entity manager */
public class EnemyFactory {
	private static EnemyFactory instance;
	private PhysicsSpace space = null;
	private Random rand = new Random();
	
	private EnemyFactory (PhysicsSpace space) {
		this.space = space;
	}
	
	public static void create(PhysicsSpace space) {
		instance = new EnemyFactory(space);
	}
	
	public static EnemyFactory get () {
		if (instance == null) {
			Logger.getAnonymousLogger(EnemyFactory.class.getName()).severe(
					"EnemyFactory not yet initialized");
			return null;
		}
		return instance;
	}
	
	public Enemy createEnemy(String modelName, Node target) {
		Enemy enemy = new Enemy(modelName, target, space);
		enemy.setSpeed(enemy.getSpeed()*rand.nextFloat()*2);
		enemy.setAgility(enemy.getAgility()*rand.nextFloat()*3);
		EntityManager.get().addEntity(enemy);
		return enemy;
	}
}
