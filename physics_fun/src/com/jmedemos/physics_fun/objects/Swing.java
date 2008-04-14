package com.jmedemos.physics_fun.objects;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Torus;
import com.jmedemos.physics_fun.util.MaterialType;
import com.jmedemos.physics_fun.util.ObjectFactory;
import com.jmex.font3d.Font3D;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.RotationalJointAxis;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.material.Material;

public class Swing extends Node {
    private static final long serialVersionUID = 1L;
    private Joint joint = null;
    private StaticPhysicsNode staticNode = null;
    private DynamicPhysicsNode dynamicNode = null;
    private int frameAngle = 60;

    public Swing(PhysicsSpace space) {
        super("Swing");
        staticNode = space.createStaticNode();
        staticNode.setMaterial(Material.WOOD);
        ObjectFactory.get().applyRenderStates(staticNode, MaterialType.WOOD);
        
        for (int i = 0; i < 4; i++) {
            Cylinder cyl = new Cylinder("cyl", 10, 10, 0.15f, 7, true);
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
        topCylinderNode.setLocalTranslation(0, 3f, 0);
        
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
            joint = space.createJoint();
            
//            joint.setSpring( 100, 0 );
            joint.setBreakingLinearForce( 5000 );
            
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
        }
    }
}
