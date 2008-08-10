package com.jmedemos.physics_fun.util.test;

import java.net.URISyntaxException;

import jmetest.TutorialGuide.ExplosionFactory;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.image.Texture.WrapMode;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.util.SyntheticButton;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmedemos.physics_fun.core.Main;
import com.jmedemos.physics_fun.core.PhysicsGame;
import com.jmedemos.physics_fun.physics.ExplosionAction;
import com.jmedemos.physics_fun.util.MaterialType;
import com.jmedemos.physics_fun.util.ObjectFactory;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.SimplePhysicsGame;

public class ExplosionTest extends SimplePhysicsGame {
    public static void main(String[] args) {
       ExplosionTest app = new ExplosionTest();
       app.setConfigShowMode(ConfigShowMode.AlwaysShow);
       app.start();
    }
    @Override
    protected void simpleInitGame() {
        try {
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_AUDIO,
                    new SimpleResourceLocator(Main.class.getClassLoader().getResource("com/jmedemos/physics_fun/resources/")));
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE,
                    new SimpleResourceLocator(Main.class.getClassLoader().getResource("com/jmedemos/physics_fun/resources/")));
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_SHADER,
                    new SimpleResourceLocator(Main.class.getClassLoader().getResource("com/jmedemos/physics_fun/resources/")));
        } catch (URISyntaxException e1) {
            PhysicsGame.get().getGame().finish();
        }
        
        ExplosionFactory.warmup();
        
        ObjectFactory.createObjectFactory(getPhysicsSpace());
        TextureState tsCrate = display.getRenderer().createTextureState();
        tsCrate.setTexture(TextureManager.loadTexture(ExplosionTest.class.getClassLoader().getResource("jmetest/physics/resource/crate.png")));

        
        createFloor(100,100);
        
        Node wall = createWall(5, 5, 0.5f, MaterialType.IRON);
        wall.getLocalTranslation().set(-7, 0.5f, -2);
        rootNode.attachChild(wall);
        
        wall = createWall(5, 5, 0.5f, MaterialType.WOOD);
        wall.getLocalTranslation().set(-7, 0.5f, 2);
        rootNode.attachChild(wall);
        
        wall = createWall(5, 5, 0.5f, MaterialType.GRANITE);
        wall.getLocalTranslation().set(7, 0.5f, -2);
        rootNode.attachChild(wall);
        
        wall = createWall(5, 5, 0.5f, MaterialType.WOOD);
        wall.getLocalTranslation().set(3, 0.5f, -6);
        rootNode.attachChild(wall);
        wall = createWall(5, 5, 0.5f, MaterialType.WOOD);
        wall.getLocalTranslation().set(-3, 0.5f, -6);
        rootNode.attachChild(wall);

        wall = createWall(5, 5, 0.5f, MaterialType.GLASS);
        wall.getLocalTranslation().set(3, 0.5f, 6);
        rootNode.attachChild(wall);
        wall = createWall(5, 5, 0.5f, MaterialType.GLASS);
        wall.getLocalTranslation().set(-3, 0.5f, 6);
        rootNode.attachChild(wall);
        
        wall = createWall(5, 5, 0.5f, MaterialType.SPONGE);
        wall.getLocalTranslation().set(7, 0.5f, 2);
        rootNode.attachChild(wall);
        
        KeyBindingManager.getKeyBindingManager().add("drop", KeyInput.KEY_SPACE);
    }
    
    
    float lastDrop = 0;
    @Override
    protected void simpleUpdate() {
        super.simpleUpdate();
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("drop")) {
            float time = Timer.getTimer().getTimeInSeconds();
            if (lastDrop + 0.5f < time) {
                lastDrop = time;
            } else {
                return;
            }
            
            // Build an explosive crate.
            Sphere bomb = new Sphere("bomb", 15, 10, 0.5f);
            bomb.setModelBound(new BoundingBox());
            bomb.updateModelBound();
            DynamicPhysicsNode box = getPhysicsSpace().createDynamicNode();
            box.setName("physics box ");
            box.attachChild(bomb);
            box.generatePhysicsGeometry();
            box.setMaterial(Material.GHOST);
            box.computeMass();
            box.updateRenderState();
            
            box.getLocalTranslation().y += 40;
            rootNode.attachChild(box);
            // Listen for this block being hit.
            final SyntheticButton collisionEventHandler = box.getCollisionEventHandler();
            input.addAction( new ExplosionAction(box), collisionEventHandler, false );
            rootNode.updateGeometricState(0, true);
        }
    }
 
    private Node createWall (int x, int y, float bSize, MaterialType mat) {
        DynamicPhysicsNode [][] boxes = new DynamicPhysicsNode[x][y]; 
        Node n = new Node("n");
        for (RenderState r : ObjectFactory.get().getRenderStates(mat))
            n.setRenderState(r);
        
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Box b = new Box("vis box" +i, new Vector3f(), bSize, bSize, bSize);
                b.setModelBound(new BoundingBox());
                b.updateModelBound();
                DynamicPhysicsNode box = getPhysicsSpace().createDynamicNode();
                box.setName("physics box [" +i +"][" +j +"j");
                box.attachChild(b);
                box.generatePhysicsGeometry();
                box.setMaterial(mat.getMaterial());
                box.computeMass();
                boxes[i][j] = box;
                box.setLocalTranslation(2*i*bSize+(i*0.1f), 2*j*bSize+(j*0.1f), 0);
                n.attachChild(box);
            }
        }
        return n;
    }
    
    /**
     * creates the floor and two walls standing on the floor.
     */
    private void createFloor(float width, float length) {
        Texture tex = TextureManager.loadTexture(
                ExplosionTest.class.getClassLoader().getResource("jmetest/data/images/Fieldstone.jpg"));
        tex.setScale(new Vector3f(10, 10, 10));
        tex.setWrap(WrapMode.Repeat);
        TextureState tsCarpet = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        tsCarpet.setTexture(tex);

        StaticPhysicsNode floor = makeWall("floor", width, 0.5f, length, new Vector3f(0, -1, 0));
        floor.setRenderState(tsCarpet);
        rootNode.attachChild(floor);

//        rootNode.attachChild(makeWall("back wall", width/2, 5, 1, new Vector3f(0, 5, -width/2)));
//        rootNode.attachChild(makeWall("left wall", 1, 5, width/2, new Vector3f(-width/2, 5, 0)));
    }
    
    /**
     * a simple helper method to create a static physic wall.
     * @param name node name
     * @param x x extent
     * @param y y extent
     * @param z z extent
     * @param loc location
     * @param type type of material/texture
     * @return staticPhysicNode with the attached box
     */
    public StaticPhysicsNode makeWall(String name, float x, float y ,float z, Vector3f loc) {
        Box box = new Box(name, new Vector3f(), x, y, z);
        box.setModelBound(new BoundingBox());
        box.updateModelBound();
        StaticPhysicsNode physicWall = getPhysicsSpace().createStaticNode();
        physicWall.attachChild(box);
        physicWall.setLocalTranslation(loc);
        physicWall.generatePhysicsGeometry();
        return physicWall;
    }
    
}