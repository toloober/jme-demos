package com.jmedemos.physics_fun.core;

import com.jmex.game.StandardGame;

/**
 * Singleton wrapper for StandardGame.
 *  
 * @author Christoph Luder
 */
public class PhysicsGame {
	private StandardGame game = null;
	private static PhysicsGame instance = null;

	private PhysicsGame() {
		game = new StandardGame("physics fun");
	}
	
	public static PhysicsGame get() {
		if (instance == null) {
			instance = new PhysicsGame();
		}
		return instance;
	}
	
	public StandardGame getGame() {
		return game;
	}
}
