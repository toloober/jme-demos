package com.jmedemos.physics_fun.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingCapsule;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Capsule;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

public class ObjectFactory {
	private ObjectType objectType = ObjectType.SPHERE;
	private static ObjectFactory instance = null;
	private PhysicsSpace space = null;
	private MaterialType material = MaterialType.DEFAULT;
	private float scale = 0.5f;
	private float force = 750;
	private Hashtable<MaterialType, ArrayList<RenderState>> rsTable;
	private Renderer renderer = DisplaySystem.getDisplaySystem().getRenderer();
	
	private ObjectFactory(PhysicsSpace space) {
	    rsTable = new Hashtable<MaterialType, ArrayList<RenderState>>(MaterialType.values().length);
	    this.space = space;
	}
	
	public static void createObjectFactory(PhysicsSpace space) {
	    instance = new ObjectFactory(space);
	}
	
	public ArrayList<RenderState> getRenderStates(MaterialType type) {
	    ArrayList<RenderState> ts = rsTable.get(type);
	    if (ts == null) {
	        ts = createRenderStates(type);
	    } else {
	        System.out.println("reusing renderstates");
	    }
	    return ts;
	}
	
	/**
	 * Create RenderStates for a specified type of Material, and insert
	 * the list of states into the HashMap.
	 * For every type of material, a list of RenderStates is created.
	 * Some Materials might have an additional TextureState.
	 * 
	 * @param type
	 * @return ArrayList of RenderStates
	 */
	public ArrayList<RenderState> createRenderStates (MaterialType type) {
	    ArrayList<RenderState> rsList = new ArrayList<RenderState>();
	    
	    MaterialState ms = renderer.createMaterialState();
	    ms.setDiffuse(ColorRGBA.gray.clone());
	    ms.setShininess(0);
	    
	    // optional TextureState
	    TextureState ts = null;
	    String texture = null;
	    
	    switch (type) {
	    case CONCRETE:
	        texture = "concrete.jpg";
	        ms.setDiffuse(ColorRGBA.gray.clone());
	        ms.setShininess(0);
	        break;
	    case DEFAULT:
	        ms.setDiffuse(new ColorRGBA(0.5f, 0.5f, 0.5f, 1f));
	        break;
	    case GHOST:
	        ms.setDiffuse(new ColorRGBA(1, 1, 1, 0.5f));
	        break;
	    case GLASS:
	        texture = "glass.jpg";
	    	ms.setSpecular(ColorRGBA.white.clone());
	        ms.setDiffuse(new ColorRGBA(0.7f, 0.7f, 0.99f, 0.25f));
	        ms.setShininess(128);
	        break;
	    case GRANITE:
	        texture = "granite.jpg";
	        ms.setSpecular(new ColorRGBA(0.5f, 0.5f, 0.7f, 1f));
	        ms.setShininess(100);
	        break;
	    case ICE:
	        ms.setSpecular(ColorRGBA.gray.clone());
	        ms.setDiffuse(new ColorRGBA(0.9f, 0.9f, 1f, 0.7f));
	        ms.setShininess(100);
	        break;
	    case IRON:
	        ms.setDiffuse(ColorRGBA.gray.clone());
	        ms.setSpecular(ColorRGBA.white.clone());
	        ms.setShininess(120);
	        texture = "rust.jpg";
	        break;
	    case OSMIUM:
	    	ms.setSpecular(ColorRGBA.lightGray.clone());
	    	ms.setDiffuse(new ColorRGBA(0.1f, 0.1f, 0.25f, 1));
	    	ms.setShininess(128);
	        break;
	    case PLASTIC:
	        ms.setSpecular(new ColorRGBA(0.5f, 1f, 0.5f, 1));
	        ms.setDiffuse(ColorRGBA.green.clone());
	        ms.setShininess(50);
	        break;
	    case RUBBER:
	        ms.setDiffuse(ColorRGBA.red.clone());
	        ms.setShininess(10);
	        break;
	    case SPONGE:
	        texture = "sponge.jpg";
//	        ms.setDiffuse(ColorRGBA.yellow);
//	        ms.setShininess(2);
	        break;
	    case WOOD:
	        texture = "crate.png";
	        break;
        default:
            break;
	    }
	    if (texture != null) {
	        ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	        ts.setTexture(TextureManager.loadTexture(
	                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, 
	                texture), Texture.MM_LINEAR, Texture.FM_LINEAR));
	        rsList.add(ts);
	        
	    }
	    
	    rsList.add(ms);

	    rsTable.put(type, rsList);
	    
	    return rsList;
	}
	
	/**
	 * Convenience method to apply a list of renderstates.
	 * @param spatial the spatial to set the renderstates
	 * @param type type of material
	 */
	public void applyRenderStates(Spatial spatial, MaterialType type) {
	    for (RenderState rs: getRenderStates(type)) {
	        spatial.setRenderState(rs);
	    }
	}
	
	public static ObjectFactory get() {
		return instance;
	}
	
	public void setType(ObjectType type) {
		this.objectType = type;
	}
	
	public void setMaterial(MaterialType material) {
		this.material = material;
	}
	
	/**
	 * creates a new physics Object depending on the
	 * currently set type and material.
	 * @return physicsnode
	 */
	public DynamicPhysicsNode createObject() {
		DynamicPhysicsNode node = space.createDynamicNode();
		Spatial visual = null;
		
		switch (objectType) {
		case BOX:
			visual = new Box("box", new Vector3f(), 0.5f, 0.5f, 0.5f);
			visual.setModelBound(new BoundingBox());
			break;
		case SPHERE:
			visual = new Sphere("sphere", 30, 30, 1f );
			visual.setModelBound(new BoundingSphere());
			break;
		case CAPSULE:
			visual = new Capsule("capsule", 15, 10, 10, 0.5f, 2f);
			visual.setModelBound(new BoundingCapsule());
			break;
		default:
		    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe("OOps:" +objectType +" unknown");
		    return null;
		}
		visual.updateModelBound();
		node.setLocalScale(scale);
		node.setName(objectType.toString());
		node.setMaterial(material.getMaterial());
		
		for (RenderState rs : getRenderStates(material)) {
		    visual.setRenderState(rs);
		}
		
		node.attachChild(visual);
	    node.generatePhysicsGeometry();
		node.computeMass();
		node.updateRenderState();
		return node;
	}
	
	/**
	 * remove all dynamic physic objects from the specified Node.
	 * @param node Node to remove the physic objects from
	 */
	public void removeAllPhysicObjects(final Node node) {
		GameTaskQueueManager.getManager().update(new Callable<Object>() {
		    public Object call() throws Exception {
		    	for (int i = node.getChildren().size() -1; i >= 0; i--) {
		    		if (node.getChild(i) instanceof DynamicPhysicsNode) {
		    			// remove the visual and ode geometries from the physic node
		    			((DynamicPhysicsNode)node.getChild(i)).detachAllChildren();
		    			// remove the physic node from the scene
		    			((DynamicPhysicsNode)node.getChild(i)).delete();
		    		}
		    	}
				return null;
			}
		});
	}
	
	/**
	 * call unrest on all nodes.
	 * @param node all DynamicPhysicNode children on this node will get unrested.
	 */
	public void unrestAll(final Node node) {
	    if (node instanceof DynamicPhysicsNode) {
	        System.out.println("unresting: " +node);
	        ((DynamicPhysicsNode)node).unrest();
	    }
	    if (node.getChildren() == null) {
	        return;
	    }
	    System.out.println("node: " +node +"has " +node.getChildren().size() +" children");
	    for (int i = 0; i < node.getChildren().size(); i++) {
	        if (node.getChild(i) instanceof Node) {
	            unrestAll((Node)node.getChild(i));
	        }
	    }
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}

    public void setForce(float force) {
        this.force = force;
    }

    public float getForce() {
        return force;
    }

	public float getScale() {
		return scale;
	}

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }
}
