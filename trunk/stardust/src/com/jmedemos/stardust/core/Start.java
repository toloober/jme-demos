package com.jmedemos.stardust.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.system.DisplaySystem;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmedemos.stardust.gamestates.InGameState;
import com.jmedemos.stardust.gamestates.IntroState;
import com.jmedemos.stardust.gamestates.MenuState;
import com.jmedemos.stardust.sound.SoundUtil;
import com.jmex.game.state.GameStateManager;
import com.jmex.game.state.load.TransitionGameState;

/**
 * The entry point.
 */
public final class Start {
    /**
     * entry point for the application.
     * creates the Game instance and starts it. 
     * creates Gamestates.
     * @param args not used.
     */
    public static void main(final String[] args) {
        Game game = Game.getInstance();
        game.start();
        
        try {
			ResourceLocatorTool.addResourceLocator(
			        ResourceLocatorTool.TYPE_TEXTURE,
			        new SimpleResourceLocator(Start.class
			                .getClassLoader().getResource(
			                        "com/jmedemos/stardust/data/textures/")));
			ResourceLocatorTool.addResourceLocator(
                    ResourceLocatorTool.TYPE_SHADER,
                    new SimpleResourceLocator(Start.class
                            .getClassLoader().getResource(
                                    "com/jmedemos/stardust/data/shader/")));
			ResourceLocatorTool.addResourceLocator(
			        ResourceLocatorTool.TYPE_MODEL,
			        new SimpleResourceLocator(Start.class
			                .getClassLoader().getResource(
			                        "com/jmedemos/stardust/data/models/")));
			ResourceLocatorTool.addResourceLocator(
			        ResourceLocatorTool.TYPE_AUDIO,
			        new SimpleResourceLocator(Start.class
			                .getClassLoader().getResource(
			                        "com/jmedemos/stardust/data/sounds/")));
		
    		Logger.getLogger("com.jme").setLevel(Level.WARNING);
    		Logger.getLogger("com.jmex").setLevel(Level.WARNING);
    		
    		TransitionGameState trans = new TransitionGameState(10,
    		        ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "loading_black.png"));
    		GameStateManager.getInstance().attachChild(trans);
    		trans.setActive(true);
    		
    		trans.setProgress(0, "Initializing Game ...");
    		
            DisplaySystem disp = DisplaySystem.getDisplaySystem(); 
            disp.getRenderer().getCamera().setFrustumPerspective( 45.0f,
                        (float) disp.getWidth() / (float) disp.getHeight(), 1f, Game.FAR_FRUSTUM );
            disp.getRenderer().getCamera().update();
    
            // init music
            SoundUtil.get().initMusic();
            
            trans.increment("Initializing GameState: Intro ...");
            
            GameStateManager.getInstance().attachChild(new IntroState("Intro"));
            
            trans.increment("Initializing GameState: Menu ...");
            
            GameStateManager.getInstance().attachChild(new MenuState("Menu", trans));
       
            trans.increment("Initializing GameState: InGame ...");
            
            GameStateManager.getInstance().attachChild(new InGameState("InGame", trans));
    
            trans.setProgress(1.0f, "Finished Loading");
            
            GameStateManager.getInstance().activateChildNamed("Menu");
        } catch (Exception e) {
            // if anything unexpected happens, shut down the game
            e.printStackTrace();
            Game.getInstance().quit();
        }
    }
}
