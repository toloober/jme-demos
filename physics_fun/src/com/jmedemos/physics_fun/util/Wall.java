package com.jmedemos.physics_fun.util;

import com.jme.bounding.BoundingBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.RenderState;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

@SuppressWarnings("serial")
public class Wall extends Node {
	private DynamicPhysicsNode[][] boxes = null;
	private int x;
	private int y;
	private float bSize = 1;
	
	private final float OFFS = 0.001f;
	
	public Wall(final PhysicsSpace space, int x, int y, float bSize) {
		boxes = new DynamicPhysicsNode[x][y];
		this.x = x;
		this.y = y;
		this.bSize = bSize;
		
//		try {
//			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE,
//                    new SimpleResourceLocator(Main.class.getClassLoader().getResource("resources/")));
//        } catch (URISyntaxException e1) {
//        	PhysicsGame.get().getGame().finish();
//        }
		for (RenderState rs : ObjectFactory.get().getRenderStates(SceneSettings.get().getWallMaterial())) {
		    System.out.println("applinging: " +rs);
		    setRenderState(rs);
		}
	
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				Box b = new Box("vis box" +i, new Vector3f(), bSize, bSize, bSize);
				b.setModelBound(new BoundingBox());
				b.updateModelBound();
				DynamicPhysicsNode box = space.createDynamicNode();
				box.setName("physics box [" +i +"][" +j +"j");
				box.attachChild(b);
				box.generatePhysicsGeometry();
				box.setMaterial(SceneSettings.get().getWallMaterial().getMaterial());
				box.computeMass();
				boxes[i][j] = box;
				box.setLocalTranslation(2*i*bSize+(i*OFFS), 2*j*bSize+(j*OFFS), 0);
				attachChild(box);
			}
		}
	}

	public void reset() {
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				boxes[i][j].clearDynamics();
				boxes[i][j].setLocalTranslation(2*i*bSize+(i*OFFS), 2*j*bSize+(j*OFFS), 0);
				boxes[i][j].setLocalRotation(new Quaternion());
			}
		}
	}
	
	public void delete() {
	    for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                // detach from parent node and set inactive
                boxes[i][j].delete();
                // detach all children (visual box)
                boxes[i][j].detachAllChildren();
                // set reference to null
                boxes[i][j] = null;
            }
        }
	}

}
