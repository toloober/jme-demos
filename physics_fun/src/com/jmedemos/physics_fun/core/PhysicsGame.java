package com.jmedemos.physics_fun.core;

import com.jmex.game.StandardGame;

/**
 * Singleton wrapper for StandardGame.
 * This is needed to be able shutdown the Game when something unexpected happens.
 * @author Christoph Luder
 */
public class PhysicsGame {
	private StandardGame game = null;
	private static PhysicsGame instance = null;

	private PhysicsGame() {
		game = new StandardGame("physics fun");
	}
	/**
	 * @return the PhysicsGame instance.
	 */
	public static PhysicsGame get() {
		if (instance == null) {
			instance = new PhysicsGame();
		}
		return instance;
	}
	
	/**
	 * @return the standardGame instance.
	 */
	public StandardGame getGame() {
		return game;
	}
}
