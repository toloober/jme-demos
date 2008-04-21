package com.jmedemos.physics_fun.objects;

import java.util.ArrayList;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.CullState;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;
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
 * Ropes represented by quads visually connect the torus with the top cross bar. 
 * 
 * @author Christoph Luder
 */
public class Swing extends Node {
    private static final long serialVersionUID = 1L;
    /** static physics node for the frame */
    private StaticPhysicsNode staticNode = null;
    /** the cross bar at top */
    private DynamicPhysicsNode topCylinderNode = null;
    /** dynamic node for the crossbar and torus */ 
    private DynamicPhysicsNode torusNode = null;
    /** angle of the frame cylinders */
    private int frameAngle = 70;
    /** length of the frame bars */
    private int frameLength = 10;
    /** the joints which attach the torus to the top bar */
    private ArrayList<Joint> joints = null;
    /** quads representing the ropes for each join */
    private ArrayList<Quad> ropes = null;

    public static float DEFAULT_SPRING = 70;
    public static float DEFAULT_DAMPING = 10;
    
    public Swing(PhysicsSpace space) {
        super("Swing");
        
        joints = new ArrayList<Joint>(4);
        ropes = new ArrayList<Quad>(4);
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
        topCylinderVisual.setModelBound(new BoundingBox());
        topCylinderVisual.updateModelBound();
        topCylinderVisual.getLocalRotation().fromAngleAxis(FastMath.DEG_TO_RAD * 90, Vector3f.UNIT_Y);

        topCylinderNode = space.createDynamicNode();
        topCylinderNode.setName("top cylinder");
        PhysicsMesh topCylinderMesh = topCylinderNode.createMesh("top cylinder");
        topCylinderMesh.copyFrom(topCylinderVisual);
        topCylinderNode.attachChild(topCylinderMesh);
        
        topCylinderNode.setMaterial(Material.WOOD);
        ObjectFactory.get().applyRenderStates(topCylinderNode, MaterialType.WOOD);
        
        topCylinderNode.attachChild(topCylinderVisual);
        topCylinderNode.computeMass();
        
        // let the top cylinder drop from above onto the static cylinders
        topCylinderNode.setLocalTranslation(0, frameLength/2, 0);
        staticNode.generatePhysicsGeometry();
        
        torusNode = space.createDynamicNode();
        TriMesh torus = new Torus("torus", 15, 10, 0.3f, 0.7f);
        torus.getLocalRotation().fromAngleAxis(FastMath.DEG_TO_RAD*90, Vector3f.UNIT_X);
        torus.setModelBound(new BoundingSphere());
        
        // create an invisible quad in place of the torus
        // we need the quads vertex positions
        Quad q = new Quad("dummy", 1, 1);
        q.setModelBound(new BoundingBox());
        q.updateModelBound();
        q.getLocalRotation().fromAngleAxis(FastMath.DEG_TO_RAD*-90, Vector3f.UNIT_X);
        q.setCullMode(SceneElement.CULL_ALWAYS);
        torusNode.attachChild(q);
        
        PhysicsMesh physicTourus = torusNode.createMesh("swing torus");
        physicTourus.copyFrom(torus);
        torusNode.attachChild(physicTourus);
        
        torusNode.attachChild(torus);
        torusNode.setMaterial(Material.RUBBER);
        ObjectFactory.get().applyRenderStates(torusNode, MaterialType.RUBBER);
        torusNode.computeMass();
        torusNode.setLocalTranslation(0, 0, 0);
        
        // attach everything to the main node
        this.attachChild(torusNode);
        this.attachChild(staticNode);
        this.attachChild(topCylinderNode);
        
//        BillboardNode bNode = new BillboardNode("billy node");
//        bNode.setAlignment(BillboardNode.AXIAL_Z);
        Node bNode = new Node("rope node");
        CullState cs = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
        cs.setEnabled(true);
        cs.setCullMode(CullState.CS_NONE);
        bNode.setRenderState(cs);
        bNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
            
        for (int i = 0; i < 4; i++) {
            // create joints to fix the torus to the top cylinder
            Joint joint = space.createJoint();
            
            joint.setSpring(DEFAULT_SPRING, DEFAULT_DAMPING);
            joint.setBreakingLinearForce(10000);
            
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
            
            joint.attach(torusNode, topCylinderNode);
            joints.add(joint);
            
            Quad rope = new Quad("rope", 0.1f, topCylinderNode.getLocalTranslation().distance(joint.getAnchor(null).add(torusNode.getLocalTranslation())));
            rope.setModelBound(new BoundingBox());
            rope.updateModelBound();
            
            Vector3f topLeft = topCylinderNode.getLocalTranslation().clone();
            topLeft.x -= 0.1f;
            Vector3f topRight = topCylinderNode.getLocalTranslation().clone();
            topRight.x += 0.05f;
            
            Vector3f bottomLeft = torusNode.localToWorld(joint.getAnchor(null), null);
            bottomLeft.x -= 0.1f;
            Vector3f bottomRight = torusNode.localToWorld(joint.getAnchor(null), null);
            bottomRight.x += 0.1f;
            
            BufferUtils.setInBuffer(topLeft, rope.getVertexBuffer(0), 0);
            BufferUtils.setInBuffer(bottomLeft, rope.getVertexBuffer(0), 1);
            BufferUtils.setInBuffer(bottomRight, rope.getVertexBuffer(0), 2);
            BufferUtils.setInBuffer(topRight, rope.getVertexBuffer(0), 3);

            ropes.add(rope);
            bNode.attachChild(rope);
        }
        attachChild(bNode);
    }
    
    /**
     * Reset the Swing.
     * Resets the location, forces and reattaches broken joints.
     */
    public void reset() {
    	DynamicPhysicsNode topCylinder = (DynamicPhysicsNode)this.getChild("top cylinder");
    	topCylinder.clearDynamics();
    	topCylinder.setLocalRotation(new Quaternion());
    	topCylinder.setLocalTranslation(0, frameLength/2, 0);
    	torusNode.clearDynamics();
    	torusNode.setLocalTranslation(0, 0, 0);
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
    
    /**
     * updates the ropes by directly setting the vertices location.
     * the top vertices get the location of the top cross bar.
     * the lower vertices ge the location of the dummy quad.
     */
    private Vector3f tmpVec = new Vector3f();
    public void update() {
        int i = 0;
    	for (Quad rope: ropes) {
    		// top vertices of the quads
            Vector3f topLeft = topCylinderNode.getLocalTranslation().clone();
            topLeft.x -= 0.05f;
            Vector3f topRight = topCylinderNode.getLocalTranslation().clone();
            topRight.x += 0.05f;
            
            tmpVec.x = ((Quad)torusNode.getChild("dummy")).getVertexBuffer(0).get(i*3);
            tmpVec.z = ((Quad)torusNode.getChild("dummy")).getVertexBuffer(0).get(i*3+1);
            tmpVec.y = ((Quad)torusNode.getChild("dummy")).getVertexBuffer(0).get(i*3+2);
            
            tmpVec.addLocal(torusNode.getLocalTranslation());
            BufferUtils.setInBuffer(topLeft, rope.getVertexBuffer(0), 0);
            tmpVec.x -= 0.05f;
            BufferUtils.setInBuffer(tmpVec, rope.getVertexBuffer(0), 1);
            tmpVec.x += 0.1f;
            BufferUtils.setInBuffer(tmpVec, rope.getVertexBuffer(0), 2);
            BufferUtils.setInBuffer(topRight, rope.getVertexBuffer(0), 3);
    		i++;
    	}
    }
}
