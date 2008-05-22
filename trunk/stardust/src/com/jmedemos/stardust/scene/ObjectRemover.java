package com.jmedemos.stardust.scene;

import java.util.ArrayList;

import com.jme.scene.Node;
import com.jme.scene.Spatial;

public class ObjectRemover {
    private static ObjectRemover instance = null;
    private ArrayList<Spatial> toRemove;
    
    private ObjectRemover() {
        toRemove = new ArrayList<Spatial>();
    }

    public static ObjectRemover get() {
        if (instance == null) {
            instance = new ObjectRemover();
        }
        return instance;
    }
    
    /**
     * remove all Spatials in the toRemove list from the scene.
     * A Spatial could be a Node or Geometry.
     */
    public void purge() {
        // iterate through the list of spatials
        for (Spatial s : toRemove) {
            // if the spatial is a Node, remove its children
            if (s instanceof Node) {
                Node n = ((Node)s);
                n.detachAllChildren();
//                ArrayList<Spatial> l = n.getChildren();
//                for (int i = 0; i < l.size(); i++) {
//                   l.get(i).removeFromParent();
//                }
//                System.out.println(Thread.currentThread().getName());
            }
            s.removeFromParent();
//            System.gc();
        }
        toRemove.clear();
    }
    
    public void addObject(Spatial s) {
        toRemove.add(s);
    }
    
    
}
