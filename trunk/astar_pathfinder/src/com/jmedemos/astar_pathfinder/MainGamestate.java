package com.jmedemos.astar_pathfinder;

import java.util.HashMap;


import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.InputHandler;
import com.jme.input.MouseInput;
import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.Debugger;
import com.jmedemos.astar_pathfinder.pathfinder.algorithm.PathFinder;
import com.jmedemos.astar_pathfinder.pathfinder.algorithm.impl.AStarPathFinder;
import com.jmedemos.astar_pathfinder.pathfinder.heuristic.impl.AStarHeuristic;
import com.jmedemos.astar_pathfinder.pathfinder.path.Path;
import com.jmedemos.astar_pathfinder.pathfinder.path.Step;
import com.jmedemos.astar_pathfinder.world.Mover;
import com.jmedemos.astar_pathfinder.world.WorldMap;
import com.jmex.game.state.BasicGameState;

public class MainGamestate extends BasicGameState {

	private Camera cam;
	private DisplaySystem display;
	private Renderer renderer;
	private AbsoluteMouse mouse;							// This is our mouse
    protected InputHandler input;							// Handles our mouse/keyboard input.
    
    private WorldMap map = new GameMap();					// The map on which the units will move
    private Texture[] tileTextures = new Texture[6];		// The list of tile images to render the map
	private HashMap<Spatial,Mover> units = new HashMap<Spatial, Mover>();	// All the units on the map
	private PathFinder finder;								// The path finder we'll use to search our map
	private Path path;										// The last path found for the current unit
    
    private PickResults pickResults;						// Will hold what our mouse clicked on
    private Spatial pickObject;								// Picked scene element (Quad - terrain tile or vehicle)
    private Spatial lastPickObject;							// The scene element to which we got our last path (for avoiding multiple searches)
    private Mover pickedUnit;								// Picked vehicle Java object (Unit)
    private boolean pickIsVehicle;							// Identifies if we clicked on a unit this update
    private boolean mouseIsPressed;							// So that we sould not have to call "MouseInput.get().isButtonDown(0)" multiple times
    private boolean mousePressHandled;						// Used for avoiding handling one mousepress multiple times
    
    private boolean monitorPathfinder;
    private Quad pathTile;									// Tile used for drawing the path 
    private Quad markTile;									// Tile used for drawing the tiles visited by pathfinder
	
	public MainGamestate(String name) {
		super(name);
		this.cam = PathfindingTest.getGame().getCamera();
        this.display = PathfindingTest.getGame().getDisplay();
        this.renderer = display.getRenderer();
        init();
	}
	
	private void init(){
		/* Set-up input handler */
		input = new InputHandler();
		
		
		/* Set up mouse */
		mouse = new AbsoluteMouse("The Mouse", display.getWidth(), display.getHeight());
        mouse.registerWithInputHandler(input);					// Assign the mouse to an input handler
        rootNode.attachChild(mouse);							// Attach mouse to scene
        pickResults = new BoundingPickResults();
        
        
        /* Setup our camera in good position */
		cam.setLocation(new Vector3f(map.getWidth()/2, (map.getHeight()/2) - 0.5f, 31.0f));
        cam.update();	
        
        
		/* Get textures */
		tileTextures[WorldMap.TERRAIN_TREES] = TextureManager.loadTexture(BasicGameState.class.getClassLoader().getResource("com/jmedemos/astar_pathfinder/img/trees.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
		tileTextures[WorldMap.TERRAIN_GRASS] = TextureManager.loadTexture(BasicGameState.class.getClassLoader().getResource("com/jmedemos/astar_pathfinder/img/grass.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
		tileTextures[WorldMap.TERRAIN_WATER] = TextureManager.loadTexture(BasicGameState.class.getClassLoader().getResource("com/jmedemos/astar_pathfinder/img/water.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
		tileTextures[WorldMap.TANK]  = TextureManager.loadTexture(BasicGameState.class.getClassLoader().getResource("com/jmedemos/astar_pathfinder/img/tank.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
		tileTextures[WorldMap.PLANE] = TextureManager.loadTexture(BasicGameState.class.getClassLoader().getResource("com/jmedemos/astar_pathfinder/img/plane.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
		tileTextures[WorldMap.BOAT]  = TextureManager.loadTexture(BasicGameState.class.getClassLoader().getResource("com/jmedemos/astar_pathfinder/img/boat.png"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
		
		
		/* Create our map */
		for (int y = 0; y < map.getHeight(); y++){
			for (int x = 0; x < map.getWidth(); x++){
				// Add tiles
				Quad quad = new Quad("Tile " + x + ":" + y, 1, 1);
		        TextureState ts = display.getRenderer().createTextureState();
		        ts.setEnabled(true);
		        ts.setTexture(tileTextures[map.getTerrain(x, y)]);
		        quad.setRenderState(ts);
		        quad.setLocalTranslation(x, y, 0f);
		        // Add model bounds to quad so that they could be picked
		        quad.setModelBound(new BoundingBox() );
				quad.updateModelBound();
		        rootNode.attachChild(quad);
			}
		}
		
		
		/* Pathfinding */
		finder = new AStarPathFinder(
				map, 
				new AStarHeuristic());
		pathTile = new Quad("Tile", 0.5f, 0.5f);
		pathTile.setSolidColor(ColorRGBA.red);
		pathTile.updateRenderState();
		markTile = new Quad("Tile", 0.5f, 0.5f);
		markTile.setSolidColor(ColorRGBA.white);
		markTile.updateRenderState();
		monitorPathfinder = true;
		
		
		/* 
		 * Add our units. Units are added to the HashMap with the Spatial as a key to
		 * allow identifying the Unit by the spatial in picking operation later.
		 */
		Mover temp = new Unit(15, 15, WorldMap.TANK, display, tileTextures[WorldMap.TANK]);
		units.put(temp.getModel(), temp);
		rootNode.attachChild(temp.getModel());
		temp = new Unit(2, 7, WorldMap.BOAT, display, tileTextures[WorldMap.BOAT]);
		units.put(temp.getModel(), temp);
		rootNode.attachChild(temp.getModel());
		temp = new Unit(20, 25, WorldMap.PLANE, display, tileTextures[WorldMap.PLANE]);
		units.put(temp.getModel(), temp);
		rootNode.attachChild(temp.getModel());
		pickedUnit = null;		// Currently nothing is selected
		
		
		rootNode.updateRenderState();
	}
	
	
	@Override
    public void update(float tpf) {
    	super.update(tpf);
        input.update(tpf);
        
        /* Find where mouse is now */
        Vector2f screenPos = new Vector2f();
		screenPos.set(mouse.getHotSpotPosition().x, mouse.getHotSpotPosition().y);	// Get the position that the mouse is pointing to
		Vector3f worldCoords = display.getWorldCoordinates(screenPos, 0);			// Get the world location of that X,Y value
		Vector3f worldCoords2 = display.getWorldCoordinates(screenPos, 1);
		Ray mouseRay = new Ray( worldCoords, 										// Create a ray starting from the camera & going 
				worldCoords2.subtractLocal(worldCoords).normalizeLocal());			//		in the direction of the mouse's location
		pickResults.clear();
		rootNode.findPick(mouseRay, pickResults);									// Check if there is any object under the mouse
		for (int i = 0; i < pickResults.getNumber(); i++) {
			pickObject = (Spatial)pickResults.getPickData(i).getTargetMesh().getParentGeom();
			if (units.containsKey(pickObject)){
				pickIsVehicle = true;
				break;
			}else{
				pickIsVehicle = false;
			}
		}
		
		/* Determine what we must do based on what the mouse is doing */
		mouseIsPressed = MouseInput.get().isButtonDown(0);
        if (mouseIsPressed && !mousePressHandled){						// Mouse is pressed down and we have not handled this yet
        	mousePressHandled = true;									// Note that we have handled this mouse press
        	if (pickIsVehicle){											// If we are clicking on a unit, pick that unit
        		pickedUnit = units.get(pickObject);
        	}else if (pickedUnit != null && path != null){ 				// Otherwise, if some unit is already selected and there was a path found, move to new location
        		pickedUnit.setModelLocation(pickObject.getLocalTranslation().getX(), pickObject.getLocalTranslation().getY());
        	}
        	path = null;
        }else if (!mouseIsPressed){ 									// Mouse is not pressed
        	mousePressHandled = false;									// Note that mouse is no longer pressed
	        if (pickedUnit != null){									// If some object is selected, trace its path
	        	if (pickObject != lastPickObject && 					// If we are at a new location and there is
	        			!units.containsKey(pickObject)){				// 		no other vehicle here, search for a new path
	        		lastPickObject = pickObject;
	        		if (monitorPathfinder) map.clearVisited();			// Clear which tiles have been visited by pathfinder previously
		        	path = finder.findPath(
		        			pickedUnit, 
		        			(int) pickedUnit.getModelLocation().getX(), 
		        			(int) pickedUnit.getModelLocation().getY(), 
		        			(int) pickObject.getLocalTranslation().getX(), 
		        			(int) pickObject.getLocalTranslation().getY());
	        	}
	        }
        }
        
        if (MouseInput.get().isButtonDown(1)){
        	pickedUnit = null;		// If right mouse button pressed, deselect unit
        	path = null;			// Clear path
        }
		
	}
	
	
	public void render(float tpf) {
    	super.render(tpf);
    	
    	// Draw unit selection
    	if (pickedUnit != null){
    		Debugger.drawBounds(pickedUnit.getModel(), renderer, false);
    	}
    	
    	// Mark tiles visited by path-finder
    	if (!units.containsKey(pickObject) && path != null && monitorPathfinder){
    		for (int y = 0; y < map.getHeight(); y++){
    			for (int x = 0; x < map.getWidth(); x++){
    				if (map.isVisited(x, y)){
		    			markTile.setLocalTranslation(x, y, 0.00105f);
		    			markTile.draw(renderer);
    				}
    			}
    		}
    	}
    	
    	// Draw path
    	if (!units.containsKey(pickObject) && path != null){
    		Step tempStep;
    		for (int i=1; i<path.getLength(); i++){
    			tempStep = path.getStep(i);
    			pathTile.setLocalTranslation(tempStep.getX(), tempStep.getY(), 0.0011f);
    			pathTile.draw(renderer);
    		}
    		tempStep = null;
    	}
    	
	}

	

}
