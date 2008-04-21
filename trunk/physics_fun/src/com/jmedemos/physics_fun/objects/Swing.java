package com.jmedemos.physics_fun.objects;

import java.util.ArrayList;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Torus;
import com.jmedemos.physics_fun.util.MaterialType;
import com.jmedemos.physics_fun.util.ObjectFactory;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.RotationalJointAxis;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.material.Material;

/**
 * A physics Swing.
 * The Swing is represented by a static Frame build with 4 Cylinders.
 * On top of the frame lies a cross bar with a dynamic torus attached by joints.
 * 
 * @author Christoph Luder
 */
public class Swing extends Node {
    private static final long serialVersionUID = 1L;
    /** static physics node for the frame */
    private StaticPhysicsNode staticNode = null;
    /** dynamic node for the crossbar and torus */ 
    private DynamicPhysicsNode dynamicNode = null;
    /** angle of the frame cylinders */
    private int frameAngle = 70;
    /** length of the frame bars */
    private int frameLength = 10;
    /** the joints which attach the torus to the top bar */
    private ArrayList<Joint> joints = null;

    public static float DEFAULT_SPRING = 70;
    public static float DEFAULT_DAMPING = 10;
    
    public Swing(PhysicsSpace space) {
        super("Swing");
        
        joints = new ArrayList<Joint>();
        
        staticNode = space.createStaticNode();
        staticNode.setMaterial(Material.WOOD);
        ObjectFactory.get().applyRenderStates(staticNode, MaterialType.WOOD);
        
        for (int i = 0; i < 4; i++) {
            Cylinder cyl = new Cylinder("cyl", 10, 10, 0.15f, frameLength, true);
            cyl.setModelBound(new BoundingBox());
            cyl.updateModelBound();
            Quaternion q = new Quaternion();
            
            switch(i) {
            case 0:
                q.fromAngleAxis(FastMath.DEG_TO_RAD * -frameAngle, Vector3f.UNIT_X);
                cyl.setLocalTranslation(-3, 0, -1);
                break;
            case 1:
                q.fromAngleAxis(FastMath.DEG_TO_RAD * frameAngle, Vector3f.UNIT_X);
                cyl.setLocalTranslation(-3, 0, 1);
                break;
            case 2:
                q.fromAngleAxis(FastMath.DEG_TO_RAD * frameAngle, Vector3f.UNIT_X);
                cyl.setLocalTranslation(3, 0, 1);
                break;
            case 3:
                q.fromAngleAxis(FastMath.DEG_TO_RAD * -frameAngle, Vector3f.UNIT_X);
                cyl.setLocalTranslation(3, 0, -1);
                break;
            }
            cyl.setLocalRotation(q);
            staticNode.attachChild(cyl);
        }
        
        TriMesh topCylinderVisual = new Cylinder("cyl", 10, 10, 0.2f, 9, true);
//        TriMesh topCylinderVisual = new Box("top", new Vector3f(), 4, 0.2f, 0.2f);
        topCylinderVisual.setModelBound(new BoundingBox());
        topCylinderVisual.updateModelBound();
        topCylinderVisual.getLocalRotation().fromAngleAxis(FastMath.DEG_TO_RAD * 90, Vector3f.UNIT_Y);

        DynamicPhysicsNode topCylinderNode = space.createDynamicNode();
        topCylinderNode.setName("top cylinder");
        PhysicsMesh topCylinderMesh = topCylinderNode.createMesh("top cylinder");
        topCylinderMesh.copyFrom(topCylinderVisual);
        topCylinderNode.attachChild(topCylinderMesh);
        
//        topCylinderNode.attachChild(topCylinderVisual);
//        topCylinderNode.generatePhysicsGeometry();
        
        topCylinderNode.setMaterial(Material.WOOD);
        ObjectFactory.get().applyRenderStates(topCylinderNode, MaterialType.WOOD);
        
        topCylinderNode.attachChild(topCylinderVisual);
        
        topCylinderNode.computeMass();
        
        // let the top cylinder drop from above onto the static cylinders
        topCylinderNode.setLocalTranslation(0, frameLength/2, 0);
        
        staticNode.generatePhysicsGeometry();
        
        dynamicNode = space.createDynamicNode();
        TriMesh torus = new Torus("torus", 15, 10, 0.3f, 0.7f);
        torus.getLocalRotation().fromAngleAxis(FastMath.DEG_TO_RAD*90, Vector3f.UNIT_X);
        torus.setModelBound(new BoundingSphere());
        
        PhysicsMesh physicTourus = dynamicNode.createMesh("swing torus");
        physicTourus.copyFrom(torus);
        dynamicNode.attachChild(physicTourus);
        
        dynamicNode.attachChild(torus);
        dynamicNode.setMaterial(Material.RUBBER);
        ObjectFactory.get().applyRenderStates(dynamicNode, MaterialType.RUBBER);
        dynamicNode.computeMass();
        dynamicNode.setLocalTranslation(0, 0, 0);
        
        // attach everything to the main node
        this.attachChild(dynamicNode);
        this.attachChild(staticNode);
        this.attachChild(topCylinderNode);
        
        for (int i = 0; i < 4; i++) {
            // create two joints to fix the torus to the top cylinder
            Joint joint = space.createJoint();
            
            joint.setSpring( DEFAULT_SPRING, 20 );
            joint.setBreakingLinearForce( 10000 );
            
            // we want free rotation around the all Axis
            RotationalJointAxis axisX = joint.createRotationalAxis();
            axisX.setDirection(Vector3f.UNIT_X);
            RotationalJointAxis axisY = joint.createRotationalAxis();
            axisY.setDirection(Vector3f.UNIT_Y);
            RotationalJointAxis axisZ = joint.createRotationalAxis();
            axisZ.setDirection(Vector3f.UNIT_Z);
            
            switch(i) {
            case 0:
            	joint.setAnchor(new Vector3f(-1, 0, -1));
            	break;
            case 1:
            	joint.setAnchor(new Vector3f(-1, 0, 1));
            	break;
            case 2:
            	joint.setAnchor(new Vector3f(1, 0, 1));
            	break;
            case 3:
            	joint.setAnchor(new Vector3f(1, 0, -1));
            	break;
            }
            
            joint.attach(dynamicNode, topCylinderNode);
            joints.add(joint);
        }
        
        // draw ropes from the joint anchor to the top
        
    }
    
    public void reset() {
    	DynamicPhysicsNode topCylinder = (DynamicPhysicsNode)this.getChild("top cylinder");
    	topCylinder.clearDynamics();
    	topCylinder.setLocalRotation(new Quaternion());
    	topCylinder.setLocalTranslation(0, frameLength/2, 0);
    }
    
    public void setSpring(float spring) {
    	for (Joint j : joints) {
    		j.setSpring(spring, j.getDampingCoefficient());
    	}
    }
    public void setDamping(float damping) {
    	for (Joint j : joints) {
    		j.setSpring(j.getSpringConstant(), damping);
    	}
    }
    
    public void setBreakingForce(float force) {
    	for (Joint j : joints) {
    		j.setBreakingLinearForce(force);
    	}
    }
}
