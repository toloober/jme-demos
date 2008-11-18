package com.jmedemos.stardust.test;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.light.Light;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;
import com.jme.util.Timer;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.model.util.ModelLoader;

public class TestStation extends SimpleGame {

    public static void main(String[] args) {
        TestStation test = new TestStation();
        test.setConfigShowMode(ConfigShowMode.AlwaysShow);
        test.start();
    }
    
    Node stationNode = null;
    
    private void createShip() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    stationNode = ModelLoader.loadModel("spacestation.obj");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rootNode.attachChild(stationNode);
                
                
                createFlasher(stationNode);
                rootNode.updateRenderState();
            }   
        });
        t.start();
    }
    
    private void createFlasher(Node n) {
        Vector3f position = new Vector3f(0, 4, 0);
        PointLight pl = new PointLight();
        pl.setAmbient(ColorRGBA.black);
        pl.setDiffuse(new ColorRGBA(0.5f, 0.5f, 0.5f, 1));
        pl.setSpecular(new ColorRGBA(0.2f, 1.0f, 0.2f, 1));
        pl.setAttenuate(true);
        pl.setEnabled(true);
        pl.setQuadratic(0.3f);
        
        Sphere lamp = new Sphere("", 7, 7, 0.1f);
        lamp.setModelBound(new BoundingBox());
        lamp.updateModelBound();
        n.attachChild(lamp);
        
        pl.getLocation().set(position);
        lamp.getLocalTranslation().set(position);
        lamp.addController(new FlashController(lamp, pl));
        lightState.attach(pl);
    }
    
    @Override
    protected void simpleInitGame() {
        
        try {
            ResourceLocatorTool.addResourceLocator(
                    ResourceLocatorTool.TYPE_MODEL,
                    new SimpleResourceLocator(TestStation.class
                            .getClassLoader().getResource(
                                    "com/jmedemos/stardust/data/models/")));

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        createShip();
    }

    @Override
    protected void simpleUpdate() {
        
    }
    
    class FlashController extends Controller  {
        float timeout = 2;
        float lastUpdated = 0;
        Spatial spat;
        Light l;
        boolean flashOn = true;
        public FlashController(Spatial s, Light l) {
            this.spat = s;
            this.l = l;
        }
        
        @Override
        public void update(float time) {
            float tmp = Timer.getTimer().getTimeInSeconds();
            if (lastUpdated + timeout < tmp) {
                if (flashOn == true) {
                    l.setDiffuse(ColorRGBA.darkGray);
                } else {
                    l.setDiffuse(ColorRGBA.yellow);
                }
                flashOn = !flashOn;
                lastUpdated = tmp;
            }
        }
    }
}
