package com.jmedemos.astar_pathfinder;

import java.util.concurrent.Callable;

import com.jme.input.MouseInput;
import com.jme.util.GameTaskQueueManager;
import com.jmex.game.StandardGame;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.GameStateManager;


/**
 * Example use of A* pathfinding. The A* used in this example is based on
 * advice from website http://www.policyalmanac.org/games/aStarTutorial.htm
 * and related pages. It uses a Binary Heap for maintaining an Open List.
 * Faster and more accurate than Example1.<br>
 * 
 * @author Mindgamer
 */
public class PathfindingTest {
	
	private static StandardGame game;					// Game root
	private static BasicGameState gamestate;			// Our gamestate
	
	/**
	 * Entry point to our simple test game
	 * 
	 * @param argv The arguments passed into the game
	 */
	public static void main(String[] args) {
		PathfindingTest pathfindingTest = new PathfindingTest();
	}
	
	public PathfindingTest() {
		
		/* Create the game */
		game = new StandardGame("Pathfinding Test");
		
		/* Try to get game settings */
		try{
			//GameSettingsPanel.prompt(game.getSettings());
		}catch(Exception e){
			e.printStackTrace();
		}
		game.getSettings();
		/* Start up the game */
		game.start();
		
		/* Load gamestates */
		gamestate = new MainGamestate("Main");
		GameStateManager.getInstance().attachChild(gamestate);
		gamestate.setActive(true);
		
		setCursorVisible(true);
	}

	/**
	 * @return the game
	 */
	public static StandardGame getGame() {
		return game;
	}
	
	/**
	 * Change the visibility of the mouse cursor
	 */
    private static void setCursorVisible(final boolean visible) {
        GameTaskQueueManager.getManager().update(new Callable<Object>() {
            public Object call() throws Exception {
                MouseInput.get().setCursorVisible(visible);
                return null;
            }
        });
    }
	
}
