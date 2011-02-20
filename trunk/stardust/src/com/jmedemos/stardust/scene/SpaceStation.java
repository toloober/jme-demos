package com.jmedemos.stardust.scene;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jmex.jbullet.PhysicsSpace;

public class SpaceStation extends PhysicsEntity {

	public SpaceStation(PhysicsSpace space, String modelName, float scale) {
		super(space, modelName, scale, false, true);
        MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
        ms.setAmbient(ColorRGBA.darkGray);
        ms.setDiffuse(ColorRGBA.green);
        ms.setSpecular(ColorRGBA.yellow);
        ms.setShininess(128);
        model.setRenderState(ms);
        node.addController(new StationController(this.getNode()));
	}
	
	@Override
	public void doCollision(Entity e) {
	    super.doCollision(e);
	    System.out.println("station health: " +getHealth());
	}
	
	class StationController extends Controller {
        private static final long serialVersionUID = 1L;
        Node me;
	    float angle = 0;
	    float speed = 0.5f;
	    public StationController(Node me) {
	        this.me = me;
        }
	    @Override
	    public void update(float time) {
	    	this.setActive(false);
	        angle += speed*time;
	        if (angle >= 360) 
	            angle = 0;
	        me.getLocalRotation().fromAngleAxis(angle, Vector3f.UNIT_Y);
	        me.setLocalRotation(me.getLocalRotation());
	        this.setActive(true);
	    }
	}
}
