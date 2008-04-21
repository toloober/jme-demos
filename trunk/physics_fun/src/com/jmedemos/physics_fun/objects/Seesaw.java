package com.jmedemos.physics_fun.objects;

import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.RenderState;
import com.jmedemos.physics_fun.util.MaterialType;
import com.jmedemos.physics_fun.util.ObjectFactory;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.RotationalJointAxis;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;

/**
 * A physics seesaw.
 * It consists of a simple Static Box with a Dynamic Board on top of it.
 * 
 * @author Christoph Luder
 */
public class Seesaw extends Node {
    private static final long serialVersionUID = 1L;
    
    /** the joint which keeps the Board in place. */
	private Joint joint = null;
	/** the base represented by a box */
	private StaticPhysicsNode staticBox = null;
	/** the dynamic board */
	private DynamicPhysicsNode dynamicBoard = null;
	
	/**
	 * construct the Seesaw.
	 * @param space reference to a physics space.
	 */
	public Seesaw(final PhysicsSpace space) {
		super("Seesaw");
		
		// create the base of the see-saw
		Box visualBox = new Box("visual box", new Vector3f(), 1, 1, 1);
		visualBox.setModelBound(new BoundingBox());
		visualBox.updateModelBound();
		visualBox.setLocalScale(2);
		Quaternion q = new Quaternion();
		visualBox.setLocalRotation(q.fromAngleAxis(FastMath.DEG_TO_RAD * 45, Vector3f.UNIT_Z));
		for (RenderState rs: ObjectFactory.get().getRenderStates(MaterialType.CONCRETE)) {
			visualBox.setRenderState(rs);
		}
		staticBox = space.createStaticNode();
		staticBox.attachChild(visualBox);
		staticBox.setMaterial(Material.CONCRETE);
		staticBox.generatePhysicsGeometry();

		// create the board of the see-saw
		Box board = new Box("board", new Vector3f(), 10, 0.1f, 2f);
		board.setModelBound(new BoundingBox());
		board.updateModelBound();
		
		dynamicBoard = space.createDynamicNode();
		dynamicBoard.attachChild(board);
		dynamicBoard.setMaterial(Material.WOOD);
		for (RenderState rs: ObjectFactory.get().getRenderStates(MaterialType.WOOD)) {
			dynamicBoard.setRenderState(rs);
		}
		dynamicBoard.generatePhysicsGeometry();
		dynamicBoard.computeMass();
		dynamicBoard.setLocalTranslation(0, 3, 0);
		
		this.attachChild(dynamicBoard);
		this.attachChild(staticBox);
		
		// create a joint to fix the board in place a little bit above the base-quad
		joint = space.createJoint();
		// we want free rotation around the Z Axis
		RotationalJointAxis axisZ = joint.createRotationalAxis();
		axisZ.setDirection(Vector3f.UNIT_Z);
		// make the joint unbreakable
		joint.setBreakingLinearForce(Float.POSITIVE_INFINITY);
		joint.setBreakingTorque(Float.POSITIVE_INFINITY);
		joint.attach(dynamicBoard);
		// set the anchor of the joint to the location of the seesaw
		joint.setAnchor(dynamicBoard.getWorldTranslation());
	}
	
	/**
	 * If the seesaw is moved, we need to move the 
	 * joints anchor in world coordinates too.
	 */
	@Override
	public void setLocalTranslation(float x, float y, float z) {
	    super.setLocalTranslation(x, y, z);
	    // make sure that the worldtranslation is up-to-date
	    dynamicBoard.updateWorldVectors();
	    joint.setAnchor(dynamicBoard.getWorldTranslation());
	}
}
