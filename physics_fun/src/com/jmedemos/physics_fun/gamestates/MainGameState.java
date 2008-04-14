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
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmedemos.physics_fun.core.PhysicsGame;
import com.jmedemos.physics_fun.objects.Seesaw;
import com.jmedemos.physics_fun.objects.Swing;
import com.jmedemos.physics_fun.objects.Wall;
import com.jmedemos.physics_fun.util.MaterialType;
import com.jmedemos.physics_fun.util.ObjectFactory;
import com.jmedemos.physics_fun.util.SceneSettings;
import com.jmex.game.state.GameStateManager;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsDebugger;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.PhysicsPicker;
import com.jmex.physics.util.states.PhysicsGameState;

public class MainGameState extends PhysicsGameState {
	private Camera cam = DisplaySystem.getDisplaySystem().getRenderer().getCamera();
	private FirstPersonHandler movementInput = new FirstPersonHandler(cam, 15.0f, 0.5f);
	private InputHandler input = new InputHandler();
	private Wall wall = null;
	private StaticPhysicsNode floor = null;
	private Quad carpet = null;
	private Node objectsNode = null;
	private boolean showPhysics = false;
	private TextureState tsCarpet = null;
	private PhysicsPicker picker = null;
	
	public MainGameState(String name) {
		super(name);
		
		ObjectFactory.createObjectFactory(getPhysicsSpace());
		objectsNode = new Node("ball Node");
		rootNode.attachChild(objectsNode);
		
		wall = new Wall(getPhysicsSpace(), SceneSettings.get().getWallWidth(), 
	                                   SceneSettings.get().getWallHeigth(),
	                                   SceneSettings.get().getWallElementSize());

		wall.setLocalTranslation(0, 0, -3);
		rootNode.attachChild(wall);
        
        createFloor();

        init();
        setupInput();
        setupLight();
        
        picker = new PhysicsPicker( input, rootNode, getPhysicsSpace(), true);
        picker.getInputHandler().setEnabled(false);
        
        Seesaw seesaw = new Seesaw(getPhysicsSpace());
        seesaw.setLocalTranslation(-13, -1, 5);
        rootNode.attachChild(seesaw);
        
        Swing swing = new Swing(getPhysicsSpace());
        swing.setLocalTranslation(15, 3f, 4);
        rootNode.attachChild(swing);
        
        cam.setLocation(new Vector3f(2, 10, 15));
        rootNode.updateRenderState();
	}
	
	/**
	 * creates the floor.
	 * a large flat box is used as physical representation.
	 * on top of that we lay a Quad with the floor Texture on it.
	 */
	private void createFloor() {
	    Texture tex = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,"floor.png"),
                Texture.MM_LINEAR, Texture.FM_LINEAR, Image.GUESS_FORMAT_NO_S3TC, 0.0f, true);
        tex.setScale(new Vector3f(10, 10, 10));
        tex.setWrap(Texture.WM_WRAP_S_WRAP_T);
        TextureState tsCarpet = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        tsCarpet.setTexture(tex);
        
        Box f = new Box("vis floor", new Vector3f(), 25, 1.0f, 25);
        f.setModelBound(new BoundingBox());
        f.updateModelBound();
        floor = getPhysicsSpace().createStaticNode();
        floor.attachChild(f);
        floor.generatePhysicsGeometry();
        floor.setMaterial(Material.CONCRETE);
        floor.setLocalTranslation(0, -1.5f, 0);
        rootNode.attachChild(floor);

        carpet = new Quad("carpet", 50, 50);
        carpet.setModelBound(new BoundingBox());
        carpet.updateModelBound();
        carpet.getLocalRotation().fromAngleAxis(FastMath.DEG_TO_RAD * -90, Vector3f.UNIT_X);
        carpet.getLocalTranslation().set(f.getLocalTranslation());
        carpet.getLocalTranslation().y -= ((BoundingBox)f.getWorldBound()).yExtent/2;
        carpet.getLocalTranslation().y += 0.01f;
        carpet.setRenderState(tsCarpet);
        rootNode.attachChild(carpet);
        
        Box backWall = new Box("back wall", new Vector3f(), 25, 5, 1);
        backWall.setModelBound(new BoundingBox());
        backWall.updateModelBound();
        ObjectFactory.get().applyRenderStates(backWall, MaterialType.CONCRETE);
        final StaticPhysicsNode staticBackWall = getPhysicsSpace().createStaticNode();
        staticBackWall.attachChild(backWall);
        staticBackWall.generatePhysicsGeometry();
        staticBackWall.setLocalTranslation(0, 5, -25);
        rootNode.attachChild(staticBackWall);
        
        Box leftWall = new Box("left wall", new Vector3f(), 1, 5, 25);
        leftWall.setModelBound(new BoundingBox());
        leftWall.updateModelBound();
        ObjectFactory.get().applyRenderStates(leftWall, MaterialType.CONCRETE);
        final StaticPhysicsNode staticLeftWall = getPhysicsSpace().createStaticNode();
        staticLeftWall.attachChild(leftWall);
        staticLeftWall.generatePhysicsGeometry();
        staticLeftWall.setLocalTranslation(-25, 5, 0);
        rootNode.attachChild(staticLeftWall);
	}
	
	public void refreshCarpetTexture() {
	    carpet.setRenderState(tsCarpet);
	    carpet.updateRenderState();
	}
	
	private void init() {
	    AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
        as.setEnabled(true);
        as.setBlendEnabled(true);
        as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        rootNode.setRenderState(as);
        rootNode.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        rootNode.setLightCombineMode(LightState.REPLACE);
	    
	    CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
	    cs.setCullMode(CullState.CS_BACK);
	    cs.setEnabled(true);
	    rootNode.setRenderState(cs);
	    
//	    final SyntheticButton collisionEventHandler = getPhysicsSpace().getCollisionEventHandler();
//	    input.addAction( new MyCollisionAction(), collisionEventHandler, false );
	    
	}
	
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
		        	try {
                        spawnObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

	private void spawnObject() {
		DynamicPhysicsNode node = ObjectFactory.get().createObject();
		node.setName("physics node");
		node.getLocalTranslation().set(cam.getLocation());
		node.getLocalTranslation().addLocal(cam.getDirection().mult(new Vector3f(2,2,2).add(node.getLocalScale())));
		node.addForce(cam.getDirection().mult(ObjectFactory.get().getForce()));
		objectsNode.attachChild(node);
		objectsNode.updateRenderState();
	}

	@Override
	public void update(float tpf) {
		input.update(tpf);
		if (movementInput.isEnabled()) {
			movementInput.update(tpf);
		}
		super.update(tpf);
	}

	@Override
	public void render(float tpf) {
	    super.render(tpf);
	    if (showPhysics) {
	        rootNode.updateGeometricState(0, false);
	        PhysicsDebugger.drawPhysics(getPhysicsSpace(), 
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

    public Quad getCarpet() {
        return carpet;
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

}
