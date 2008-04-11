package com.jmedemos.physics_fun.core;

import java.net.URISyntaxException;

import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmedemos.physics_fun.gamestates.GUIGameState;
import com.jmedemos.physics_fun.gamestates.MainGameState;
import com.jmedemos.physics_fun.gamestates.OrthoGameState;
import com.jmedemos.physics_fun.gamestates.RenderPassGamestate;
import com.jmex.game.state.GameStateManager;
import com.jmex.game.state.StatisticsGameState;

/**
 * The Entry point.
 * Creates a game instance, points the resource locator to the resources
 * and creates / activated the GameStates.
 *  
 * @author Christoph Luder
 */
public class Main {

	public static void main(String[] args) {
		PhysicsGame game = PhysicsGame.get();
		game.getGame().getSettings().setFullscreen(false);
		game.getGame().getSettings().setWidth(800);
		game.getGame().getSettings().setHeight(600);
		game.getGame().getSettings().setFramerate(60);
		game.getGame().getSettings().setVerticalSync(true);
		game.getGame().getSettings().setStencilBits(4);
		game.getGame().getSettings().setDepthBits(24);
		game.getGame().getSettings().setSamples(4);
		game.getGame().getSettings().setSFX(false);
		game.getGame().getSettings().setMusic(false);
		game.getGame().start();
		
      try {
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE,
                    new SimpleResourceLocator(Main.class.getClassLoader().getResource("com/jmedemos/physics_fun/resources/")));
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_SHADER,
                    new SimpleResourceLocator(Main.class.getClassLoader().getResource("com/jmedemos/physics_fun/resources/")));
        } catch (URISyntaxException e1) {
            PhysicsGame.get().getGame().finish();
        }
		
		GameStateManager.getInstance().attachChild(new MainGameState("main"));
		// deactivated for now
		GameStateManager.getInstance().attachChild(new RenderPassGamestate());
		GameStateManager.getInstance().attachChild(new OrthoGameState("txt"));
		GameStateManager.getInstance().attachChild(new StatisticsGameState());
		GameStateManager.getInstance().activateAllChildren();
		GameStateManager.getInstance().attachChild(new GUIGameState("gui",
		        (MainGameState)GameStateManager.getInstance().getChild("main")));
	}
}