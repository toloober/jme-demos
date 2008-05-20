package com.jmedemos.physics_fun.objects;

import com.jme.bounding.BoundingBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jmedemos.physics_fun.util.ObjectFactory;
import com.jmedemos.physics_fun.util.SceneSettings;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

/**
 * A Wall built by dynamic physic boxes.
 * @author Christoph Luder
 */
@SuppressWarnings("serial")
public class Wall extends Node {
	private DynamicPhysicsNode[][] boxes = null;
	private int x;
	private int y;
	private float bSize = 1;
	
	private final float OFFS = 0.000001f;
	
	/**
	 * Constructs the Wall with a specified width and heigth.
	 * @param space reference to the physics space.
	 * @param x width of the wall
	 * @param y hegth of the wall
	 * @param bSize scale of the elements.
	 */
	public Wall(final PhysicsSpace space, int x, int y, float bSize) {
		boxes = new DynamicPhysicsNode[x][y];
		this.x = x;
		this.y = y;
		this.bSize = bSize;
		
		// apply the renderstates for the specified material
		ObjectFactory.get().applyRenderStates(this, 
						SceneSettings.get().getWallMaterial());
	
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

	/**
	 * resets the single elements to form a Wall again.
	 */
	public void reset() {
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				boxes[i][j].clearDynamics();
				boxes[i][j].setLocalTranslation(2*i*bSize+(i*OFFS), 2*j*bSize+(j*OFFS), 0);
				boxes[i][j].setLocalRotation(new Quaternion());
				boxes[i][j].updateGeometricState(0, false);
			}
		}
	}
	
	/**
	 * delete and free all objects.
	 */
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
