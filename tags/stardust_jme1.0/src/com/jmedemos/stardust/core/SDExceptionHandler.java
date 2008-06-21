package com.jmedemos.stardust.core;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Logger;

/**
 * Default Exception handler for all threads.
 * If an unexpected Exception is thrown, we shut down StandardGame
 * and exit. 
 */
public class SDExceptionHandler implements UncaughtExceptionHandler {
	private Logger log = Logger.getLogger(SDExceptionHandler.class.getName());
	private Game game;
	
	public SDExceptionHandler(final Game game) {
		this.game = game;
	}

	public void uncaughtException(Thread t, Throwable e) {
		log.severe("caucht uncaught exception, quitting game");
		log.severe(e.getMessage());
		e.printStackTrace();
		game.quit();
	}
}
