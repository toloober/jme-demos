package com.jmedemos.stardust.core;

import com.jme.input.joystick.JoystickInput;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.audio.AudioSystem;
import com.jmex.game.StandardGame;
import com.jmex.game.StandardGame.GameType;
import com.jmex.game.state.GameStateManager;

/**
 * The Game class, has a reference to StandardGame.
 */
public final class Game {
    /** Singleton instance. */
    private static Game instance = null;
    /** far frustum.  */
    public static final float FAR_FRUSTUM = 100000;
    /** StandardGame reference. */
    private StandardGame stdGame;
    
    /**
     * returns the singleton instance of the game.
     * @return reference to the singleton instance of the game.
     */
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game("Stardust", GameType.GRAPHICAL);
        }
        return instance;
    }

    /**
     * Start (Standard-) Game.
     */
    public void start() {
        stdGame.start();
    }

    private boolean paused = false;
    public void pause() {
        stdGame.lock();
        paused = true;
    }
    
    public void resume() {
        stdGame.unlock();
        paused = false;
    }
    
    public boolean isPaused() {
        return paused;
    }
    /**
     * Finish Game.
     */
    public void quit() {
    	stdGame.shutdown();
    }

    /**
     * Constructs the game class.
     * @param gameName name
     * @param type type of game.
     */
    private Game(final String gameName, final StandardGame.GameType type) {
    	stdGame = new StandardGame(gameName, type, null, Thread.getDefaultUncaughtExceptionHandler());
    	stdGame.getSettings().setWidth(1024);
    	stdGame.getSettings().setHeight(768);
    	stdGame.getSettings().setFramerate(-1);
    	stdGame.getSettings().setVerticalSync(true);
    	stdGame.getSettings().setMusic(true);
    	stdGame.getSettings().setSFX(true);
    }
}
