package com.jmedemos.stardust.scene;

import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.util.Timer;

public class TargetDevice extends Controller {
    private static final long serialVersionUID = 1L;
    private Node origin;
    private Node scene;
    private int hits;
    
    private Node player;
    private Node currentTarget = null;
    private Boolean locked = false;
    private Boolean foundTarget = false;
    private float lastPlayed = 0;
    private float interval = 0.5f;
    
    public TargetDevice(Node origin, Node scene, Node player) {
        this.origin = origin;
        this.scene = scene;
        this.player = player;
    }
    
    @Override
    public void update(float time) {
        foundTarget = false;
        if (!locked) {
            Ray ray = new Ray(origin.getLocalTranslation(), origin.getLocalRotation().getRotationColumn(2));
            PickResults results = new BoundingPickResults();
            results.setCheckDistance(true);
            scene.findPick(ray, results);
            hits += results.getNumber();
            if(results.getNumber() > 0) {
            	float currentTime = Timer.getTimer().getTimeInSeconds();
                for(int i = 0; i < results.getNumber(); i++) {
                    Geometry geom = results.getPickData(i).getTargetMesh().getParentGeom();
                    if ((geom.getParent() != player && EntityManager.get().getEntity(geom.getParent()) instanceof Entity) || 
                        (geom.getParent().getParent() != player && EntityManager.get().getEntity(geom.getParent().getParent()) instanceof Entity)) {
                        currentTarget = results.getPickData(i).getTargetMesh().getParentGeom().getParent();
                        // found a asteroid model
//                        System.out.println(results.getPickData(i).getTargetMesh().getParentGeom().getParent().getName());
                        foundTarget = true;
                        if (lastPlayed + interval < currentTime) {
                        	lastPlayed = currentTime;
//                        	AudioSystem.getSystem().getMusicQueue().getTrack(
//                        			SoundUtil.BG_TARGET_SPOT).play();
                        }
                    }
//                    hitItems += results.getPickData(i).getTargetMesh().getParentGeom().getName() + " " + results.getPickData(i).getDistance();
                }
            }
            
            results.clear();
            if (foundTarget == false) {
                currentTarget = null;
            }
        }
    }

    public Boolean getLocked() {
        return locked;
    }

    public void toggleLock() {
        locked = !locked;
    }
    
    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Node getCurrentTarget() {
        return currentTarget;
    }
}
