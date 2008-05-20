package com.jmedemos.physics_fun.gamestates;

import com.jme.bounding.BoundingBox;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.Debugger;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmedemos.physics_fun.core.PhysicsGame;
import com.jmedemos.physics_fun.objects.Seesaw;
import com.jmedemos.physics_fun.objects.Swing;
import com.jmedemos.physics_fun.objects.Wall;
import com.jmedemos.physics_fun.physics.PhysicsWindCallback;
import com.jmedemos.physics_fun.util.MaterialType;
import com.jmedemos.physics_fun.util.ObjectFactory;
import com.jmedemos.physics_fun.util.SceneSettings;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsDebugger;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.util.PhysicsPicker;
import com.jmex.physics.util.states.PhysicsGameState;

/**
 * The main GameState.
 * Creates the Physics Playground.
 * 
 * @author Christoph Luder
 */
public class MainGameState extends PhysicsGameState {
    /** reference to the camera */
	private Camera cam = DisplaySystem.getDisplaySystem().getRenderer().getCamera();
	/** we want to move the camera first person style */
	private FirstPersonHandler movementInput = null;
	/** InputHandler for the physics picker and basic command */
	private InputHandler input = new InputHandler();
	/** the static floor */
	private StaticPhysicsNode floor = null;
	/** The Node where newly created objects are attached to. */
	private Node objectsNode = null;
	
	/** should the physics debug view be rendereed */ 
	private boolean showPhysics = false;
	/** should the bounding boxes be rendered */
	private boolean showBounds = false;

	/** the physics picker */
	private PhysicsPicker picker = null;
	/** The physics Wind. */
	private PhysicsWindCallback wind = null;

	/** A Wall built with physics objects. */
	private Wall wall = null;
	/** a physics swing. */
	private Swing swing = null;
	/** a physics seesaw. */
	private Seesaw seesaw = null;
	
	
	/**
	 * Constructs the MainGameState.
	 * Creates the scene and add the different objects to the Scenegraph.
	 * 
	 * @param name name of the GameState
	 */
	public MainGameState(String name) {
		super(name);
		
		// the node where newly created objects get attached to
		objectsNode = new Node("object Node");
		rootNode.attachChild(objectsNode);
		
		// create the scene
        picker = new PhysicsPicker( input, rootNode, getPhysicsSpace(), true);
        picker.getInputHandler().setEnabled(false);
        
        init();
        setupInput();
        setupLight();
        
        // add a few objects to the scene
        ObjectFactory.createObjectFactory(getPhysicsSpace());
        
        createFloor(50, 50);
        createOuterWalls(75);
        
        wall = new Wall(getPhysicsSpace(), SceneSettings.get().getWallWidth(), 
        		SceneSettings.get().getWallHeigth(),
        		SceneSettings.get().getWallElementSize());
        
        wall.setLocalTranslation(0, 0, -3);
        rootNode.attachChild(wall);
        
        seesaw = new Seesaw(getPhysicsSpace());
        seesaw.setLocalTranslation(-13, -1, 5);
        rootNode.attachChild(seesaw);
        
//        swing = new Swing(getPhysicsSpace());
//        swing.setLocalTranslation(15, 3f, 4);
//        rootNode.attachChild(swing);
        
        rootNode.updateGeometricState(0, true);
        rootNode.updateRenderState();
	}
	
	/**
	 * creates the floor and two walls standing on the floor.
	 */
	private void createFloor(float width, float length) {
	    Texture tex = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,"floor.png"),
                Texture.MM_LINEAR, Texture.FM_LINEAR, Image.GUESS_FORMAT_NO_S3TC, 0.0f, true);
        tex.setScale(new Vector3f(10, 10, 10));
        tex.setWrap(Texture.WM_WRAP_S_WRAP_T);
        TextureState tsCarpet = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        tsCarpet.setTexture(tex);

        StaticPhysicsNode floor = makeWall("floor", width, 0.5f, length, new Vector3f(0, -1, 0), null);
        floor.setRenderState(tsCarpet);
        rootNode.attachChild(floor);

        rootNode.attachChild(makeWall("back wall", width/2, 5, 1, new Vector3f(0, 5, -width/2), MaterialType.GRANITE));
        rootNode.attachChild(makeWall("left wall", 1, 5, width/2, new Vector3f(-width/2, 5, 0), MaterialType.GRANITE));
	}
	
	/**
	 * create the outer boundaries of the scene.
	 * 6 walls prevent object from falling endlessly thourgh the world.
	 */
	public void createOuterWalls(float size) {
	    rootNode.attachChild(makeWall("top",    size, 1, size, new Vector3f(0, size, 0),  MaterialType.GLASS));
	    rootNode.attachChild(makeWall("bottom", size, 1, size, new Vector3f(0, -size/2, 0), MaterialType.GLASS));
	    rootNode.attachChild(makeWall("left",   1, size, size, new Vector3f(-size, 0, 0),  MaterialType.GLASS));
	    rootNode.attachChild(makeWall("right",  1, size, size, new Vector3f(size, 0, 0),  MaterialType.GLASS));
	    rootNode.attachChild(makeWall("back",   size, size, 1, new Vector3f(0, 0, -size), MaterialType.GLASS));
	    rootNode.attachChild(makeWall("front",  size, size, 1, new Vector3f(0, 0, size),  MaterialType.GLASS));
	}
	
	/**
	 * a simple helper method to create a static physic wall.
	 * @param name node name
	 * @param x x extent
	 * @param y y extent
	 * @param z z extent
	 * @param loc location
	 * @param type type of material/texture
	 * @return staticPhysicNode with the attached box
	 */
	public StaticPhysicsNode makeWall(String name, float x, float y ,float z, Vector3f loc, MaterialType type) {
	    Box box = new Box(name, new Vector3f(), x, y, z);
	    box.setModelBound(new BoundingBox());
	    box.updateModelBound();
	    if (type != null)
	        ObjectFactory.get().applyRenderStates(box, type);
	    StaticPhysicsNode physicWall = getPhysicsSpace().createStaticNode();
	    physicWall.attachChild(box);
	    physicWall.setLocalTranslation(loc);
	    physicWall.generatePhysicsGeometry();
	    return physicWall;
	}
	
	/**
	 * Initializes the rootNodes RenderStates, initializes the Camera and 
	 */
	private void init() {
	    // we attach transparent objects to this node, so it must be
	    // rendered in the transparent bucket
	    objectsNode.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
	    
	    CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
	    cs.setCullMode(CullState.CS_BACK);
	    cs.setEnabled(true);
	    rootNode.setRenderState(cs);
	    
	    // create a first person controller to move the Camera with W,A,S,D and mouse look
	    movementInput = new FirstPersonHandler(cam, 15.0f, 0.5f);
	    // move the camera a bit backwards and up
	    cam.setLocation(new Vector3f(2, 10, 15));
	    
	    // create a Physics update callback, to simulate basic wind force
	    wind = new PhysicsWindCallback(SceneSettings.get().getWindVariation(),
	                                    SceneSettings.get().getWindForce());
	    getPhysicsSpace().addToUpdateCallbacks(wind);
	}
	
	/**
	 * set up some key actions.
	 * - SPACE to release a new object,
	 * - ESC to quit the game
	 * - TAB to enable / disable the GUI GameState
	 */
	private void setupInput() {
		input.addAction( new InputAction() {
			public void performAction( InputActionEvent evt ) {
				if ( evt.getTriggerPressed() ) {
					PhysicsGame.get().getGame().finish();
				}
			}
		}, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_ESCAPE, InputHandler.AXIS_NONE, false );
		
		input.addAction( new InputAction() {
		    public void performAction( InputActionEvent evt ) {
		        if ( evt.getTriggerPressed() ) {
		        	spawnObject();
		        }
		    }
		}, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false );
		
		input.addAction( new InputAction() {
		    public void performAction( InputActionEvent evt ) {
		        if ( evt.getTriggerPressed() ) {
		        	if (GameStateManager.getInstance().getChild("gui").isActive()) {
		        		movementInput.setEnabled(true);
		        		GameStateManager.getInstance().getChild("gui").setActive(false);
					} else {
						movementInput.setEnabled(false);
						GameStateManager.getInstance().getChild("gui").setActive(true);
					}
		        }
		    }
		}, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_TAB, InputHandler.AXIS_NONE, false );
	}

	/**
	 * create some light sources to illuminate the scene.
	 */
	private void setupLight() {
		LightState ls = DisplaySystem.getDisplaySystem().getRenderer().createLightState();

		DirectionalLight dr1 = new DirectionalLight();
        dr1.setEnabled(true);
        dr1.setAmbient(new ColorRGBA(0, 0, 0, 0.5f));
        dr1.setDiffuse(ColorRGBA.white.clone());
        dr1.setDirection(new Vector3f(-0.2f, -0.3f, -0.4f).normalizeLocal());
        dr1.setShadowCaster(true);

        ls.attach(dr1);
		ls.setEnabled(true);
		ls.setGlobalAmbient(new ColorRGBA(0.6f, 0.6f, 0.6f, 1.0f));
		ls.setTwoSidedLighting(false);

		rootNode.setRenderState(ls);
	}

	/**
	 * create a new Object.
	 * The ObjectFactory create an object depending on what we selected in the GUI GameState.
	 * - the new Object gets attached to the objectNode,
	 * - is moved a bit in front of the camera
	 * - and finally we add force to it.
	 */
	private void spawnObject() {
		DynamicPhysicsNode node = ObjectFactory.get().createObject();
		node.setName("physics node");
		node.getLocalTranslation().set(cam.getLocation());
		node.getLocalTranslation().addLocal(cam.getDirection().mult(new Vector3f(2,2,2).add(node.getLocalScale())));
		node.addForce(cam.getDirection().mult(ObjectFactory.get().getForce()));
		objectsNode.attachChild(node);
		objectsNode.updateRenderState();
		objectsNode.updateGeometricState(0, false);
	}

	/**
	 * we update the input controllers and some physic object if needed,
	 * then we update the physics world and call updateGeometricstate()
	 * which happens in super.update().
	 */
	@Override
	public void update(float tpf) {
		input.update(tpf);
//		swing.update();
		
		if (movementInput.isEnabled()) {
			movementInput.update(tpf);
		}
		super.update(tpf);
	}

	/**
	 * render the scene, draw bounds or physics if needed.
	 */
	@Override
	public void render(float tpf) {
	    super.render(tpf);
	    if (showPhysics) {
	        PhysicsDebugger.drawPhysics(getPhysicsSpace(),
	                DisplaySystem.getDisplaySystem().getRenderer());
	    }
	    
	    if (showBounds) {
	        Debugger.drawBounds(getRootNode(),
	                DisplaySystem.getDisplaySystem().getRenderer());
	    }
	}

	public Wall getWall() {
		return wall;
	}

    public StaticPhysicsNode getFloor() {
        return floor;
    }

    public Node getBallNode() {
        return objectsNode;
    }

	public boolean isShowPhysics() {
		return showPhysics;
	}

	public void setShowPhysics(boolean showPhysics) {
		this.showPhysics = showPhysics;
	}

    public void setWall(Wall wall) {
        this.wall = wall;
    }

    public PhysicsPicker getPicker() {
        return picker;
    }

	public Swing getSwing() {
		return swing;
	}

	public Seesaw getSeesaw() {
		return seesaw;
	}

    public PhysicsWindCallback getWind() {
        return wind;
    }

    public void setShowBounds(boolean showBounds) {
        this.showBounds = showBounds;
    }
}
