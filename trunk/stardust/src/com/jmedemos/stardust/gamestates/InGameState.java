package com.jmedemos.stardust.gamestates;

import java.util.Random;
import java.util.concurrent.Callable;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.controls.GameControl;
import com.jme.input.controls.GameControlManager;
import com.jme.input.controls.binding.KeyboardBinding;
import com.jme.input.controls.controller.ActionChangeController;
import com.jme.input.util.SyntheticButton;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Skybox;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.Timer;
import com.jmedemos.stardust.ai.FireController;
import com.jmedemos.stardust.core.Game;
import com.jmedemos.stardust.effects.ParticleEffectFactory;
import com.jmedemos.stardust.enemy.Enemy;
import com.jmedemos.stardust.enemy.EnemyFactory;
import com.jmedemos.stardust.gamestates.controller.InGameListener;
import com.jmedemos.stardust.hud.Hud;
import com.jmedemos.stardust.scene.ChaseCam;
import com.jmedemos.stardust.scene.EntityManager;
import com.jmedemos.stardust.scene.MissileCamera;
import com.jmedemos.stardust.scene.PhysicsPlanet;
import com.jmedemos.stardust.scene.PlayerShip;
import com.jmedemos.stardust.scene.StarDust;
import com.jmedemos.stardust.scene.Sun;
import com.jmedemos.stardust.scene.actions.CollisionAction;
import com.jmedemos.stardust.scene.asteroid.AsteroidFactory;
import com.jmedemos.stardust.scene.asteroid.AsteroidField;
import com.jmedemos.stardust.scene.powerups.HealthPowerUp;
import com.jmedemos.stardust.scene.powerups.PowerUpManager;
import com.jmedemos.stardust.scene.projectile.ProjectileFactory;
import com.jmedemos.stardust.sound.SoundUtil;
import com.jmex.audio.AudioSystem;
import com.jmex.game.state.load.TransitionGameState;
import com.jmex.physics.util.states.PhysicsGameState;

/**
 * The InGame GameState.
 * This GameState is like a playground to try out any features.
 * The real game should be made out of small levels each with its own
 * GameState maybe? 
 */
public class InGameState extends PhysicsGameState {
    /**
     * Displaysystem.
     */
    private DisplaySystem disp = DisplaySystem.getDisplaySystem();
    
    /**
     * The Earth, represented by a physics Sphere.
     */
    private PhysicsPlanet earth = null;

    /**
     * the Player Ship.
     */
    private PlayerShip player = null;

    /**
     * The Hud displays some more or less useful infos.
     */
    private Hud hud = null;

    /**
     * the stardust which surrounds the player.
     */
    private StarDust dust = null;

    /**
     * Our custom ChaseCam.
     */
    private ChaseCam chaseCam = null;

    /**
     * random number generator.
     */
    private Random rand = new Random();

    /**
     * Counter to spawn the Asteroids.
     */
    private float seconds = -5;

    /**
     * The asteroid factory.
     */
    private AsteroidFactory asteroidFactory = null;

    /**
     * Input handler for collision handling.
     */
    private InputHandler input = new InputHandler();

    /**
     * the missile camera.
     */
    private MissileCamera missileCam;
    private Skybox skybox;
    
    /**
     * Constructor of the InGame GameState. Init the Scene: 
     * - create scene elements
     * - create the Player and the ChaseCam
     * 
     * @param name Name of the Gamestates
     */
    public InGameState(final String name, final TransitionGameState trans) {
        super(name);
        
        // init physics
        initPhysics();
        
        EnemyFactory.create(getPhysicsSpace());
        PowerUpManager.create(getPhysicsSpace());
        
        try {
            GameTaskQueueManager.getManager().render(new Callable<Object>() {
                public Object call() throws Exception {
                    missileCam = new MissileCamera(disp.getWidth()-125, 95, rootNode);
                    earth = new PhysicsPlanet(getPhysicsSpace(), "earth", 4000);
                    earth.getNode().setLocalTranslation(new Vector3f(0, -5000, 6000));
                    rootNode.attachChild(earth.getNode());
                    return null;
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            Game.getInstance().quit();
        }
        ProjectileFactory.create(rootNode, getPhysicsSpace(), missileCam);

//        getPhysicsSpace().addToUpdateCallbacks(
//                new PointGravityCallback(new Vector3f(0, -5000, 6000), 1000, 7000));
        // create the Earth
//        earth = new PhysicsPlanet("earth", getPhysicsSpace(), 50, 50, 4000.0f,
//                new Vector3f(0, -5000, 6000), "data/textures/earth.jpg");
//        rootNode.attachChild(earth.getPhysicNode());
        
        // create an instance of the AsteroidFactory
        asteroidFactory = new AsteroidFactory(rootNode, getPhysicsSpace());
        
        // create the Player
        player = new PlayerShip("player", getRootNode(), getPhysicsSpace(),
                "xwing.obj", 0.7f);
        rootNode.attachChild(player.getNode());
        EntityManager.get().addEntity(player);
        SoundUtil.get().addFx("shoot.wav", player.getNode());
        
        HealthPowerUp health = PowerUpManager.get().createHealthPowerUp();
        health.getNode().getLocalTranslation().z = 1000;
        rootNode.attachChild(health.getNode());
        
        trans.increment();

        // create the Hud
        hud = new Hud(disp.getWidth(), disp.getHeight(), player);
        hud.getHudNode().attachChild(missileCam.getMonitorNode());

        // create our ChaseCam
        chaseCam = new ChaseCam(player.getNode(), 7f, -20f);

        trans.increment();

        // escape leaves the InGamestate
        GameControlManager manager = new GameControlManager();
        GameControl exit = manager.addControl("Exit");
        exit.addBinding(new KeyboardBinding(KeyInput.KEY_ESCAPE));
        ActionChangeController quit = new ActionChangeController(exit,
                new InGameListener());
        this.getRootNode().addController(quit);

        // create a Z-Buffer for the Scene
        ZBufferState z = disp.getRenderer().createZBufferState();
        z.setFunction(ZBufferState.CF_LEQUAL);
        rootNode.setRenderState(z);
        
        // init the explosion effect
        ParticleEffectFactory.init(rootNode);

        // create two sun's
        // they are attached to the skybox, to move with the camera
        Sun sun1 = new Sun(getRootNode(), chaseCam.getCamNode());
        sun1.getLightNode().setLocalTranslation(
        		new Vector3f(4000f, -3000f, Game.FAR_FRUSTUM - 1000f));
        Sun sun2 = new Sun(getRootNode(), chaseCam.getCamNode());
        sun2.getLightNode().setLocalTranslation(new Vector3f(7000, 5000, -5000));
        
        trans.increment();
    
        // create the Asteroid field
        AsteroidField af = new AsteroidField(asteroidFactory,
        		new Vector3f(0, 2000, 3000), 3, 3, 2);
        rootNode.attachChild(af.getField());
        trans.increment();
        
        // create the Stardust
        dust = new StarDust("dust", 500, (int) Game.FAR_FRUSTUM, false);
        rootNode.attachChild(dust);
        trans.increment();
        
        for (int i = 0; i < 0; i++) {
            Enemy enemy = EnemyFactory.get().createEnemy("xwing.obj", player.getNode());
            enemy.setTarget(player.getNode());
            enemy.getNode().setLocalTranslation(i*100, 500, 500);
            enemy.getNode().addController(new FireController(enemy.getNode(),
                    player.getNode(), 1500, rootNode));
            rootNode.attachChild(enemy.getNode());
        }
        
        // Captial ship
//        Enemy enemyCapital = EnemyFactory.get().createEnemy("CapitalShip.jme", null);
//        enemyCapital.getNode().setLocalTranslation(500, 1000, 2000);
//        rootNode.attachChild(enemyCapital.getNode());
        
//        skybox = new DarkSkyBox();
//        rootNode.attachChild(skybox);
        
        // update renderstates
        rootNode.updateRenderState();
        rootNode.updateGeometricState(0, true);
        
    }
    
    /** Init physics. Disable gravitation, 
     * create a ignoreColission ContactCallback.
     * This is needed for the projectiles which are fired from the Ship.
     * The Callback ignores collissions between the projectile and our ship.
     * TODO ugly
     */
    private void initPhysics() {
        getPhysicsSpace().setDirectionalGravity(new Vector3f(0, 0, 0));
        getPhysicsSpace().setAccuracy(1/60f);
//        ContactCallback ignoreColission = new ContactCallback() {
//            public boolean adjustContact(final PendingContact c) {
//                if ((c.getNode1() == player.getNode() &&
//                     c.getNode2().getName().startsWith("projectil")) ||
//                    (c.getNode1().getName().startsWith("projectil") &&
//                     c.getNode2() == player.getNode())) {
//                    // collision player <-> projectil, ignore it
//                    c.setIgnored(true);
//                    return true;
//                }
//                return false;
//            }
//        };

//        getPhysicsSpace().getContactCallbacks().add(ignoreColission);
        
        SyntheticButton collisionHandler = getPhysicsSpace().getCollisionEventHandler();
        input = new InputHandler();
        input.addAction(new CollisionAction(), collisionHandler.getDeviceName(), 
                collisionHandler.getIndex(), InputHandler.AXIS_NONE, false);
    }

    /**
     * Set the background color to Black.
     * Hide the mouse cursor.
     * @param active GameState aktiv Yes/no
     */
    @Override
    public final void setActive(final boolean active) {
        super.setActive(active);
        if (!active) {
            // stop Music
            AudioSystem.getSystem().getMusicQueue().getTrack(
                    SoundUtil.BG_SOUND_INGAME).stop();
            return;
        }
        
        // disable mouse cursor
        MouseInput.get().setCursorVisible(false);

        // play ambient Music
        if (SoundUtil.get().isEnableMusic()) {
        	AudioSystem.getSystem().getMusicQueue().getTrack(
                SoundUtil.BG_SOUND_INGAME).play();
        }
        
        Timer.getTimer().reset();
        Callable<Object> exe = new Callable<Object>() {
            public Object call() {
                disp.getRenderer().setBackgroundColor(ColorRGBA.black);
                return null;
            }
        };
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
                .enqueue(exe);
    }

    /**
     * the update Method gets called once per Frame.
     * Spawn Random Asteroids with the Earth as target.
     * Updates the position of the skybox/stardust.
     * Updates the chasecam.
     * 
     * @param tpf time since last frame in ms.
     */
    @Override
    public final void update(final float tpf) {
    	// render the missile cam
    	missileCam.render(tpf);
    	
        getPhysicsSpace().update(tpf);
        input.update(tpf);
        
        if (seconds + 15 < Timer.getTimer().getTimeInSeconds()) {
//            spawnRandomAsteroid();
//            ObjectRemover.get().purge();
//            rootNode.updateRenderState();
            seconds = Timer.getTimer().getTimeInSeconds();
        	Enemy enemy = EnemyFactory.get().createEnemy("xwing.obj", player.getNode());
            enemy.getNode().setLocalTranslation((float)Math.random()*1000, (float)Math.random()*1000, (float)Math.random()*1000);
            enemy.getNode().addController(new FireController(enemy.getNode(), player.getNode(),
                    1500, rootNode));
            rootNode.attachChild(enemy.getNode());
            rootNode.updateRenderState();
        }

        hud.update(tpf);
        // move the stardust with the camera
        dust.update(chaseCam.getCamNode().getLocalTranslation());

        // move the Skybox with the Camera
//        skybox.setLocalTranslation(chaseCam.getCamNode().getLocalTranslation().clone());
        rootNode.updateGeometricState(tpf, true);
        
        // -> Last <- but not least, update the ChaseCam
        chaseCam.update(tpf);
        chaseCam.getCamNode().updateGeometricState(tpf, true);
//        SceneMonitor.getMonitor().updateViewer(tpf);
    }

    /**
     * Render the Scene and draw the HUD.
     */
    @Override
    public void render(final float tpf) {
        super.render(tpf);
        // draw the hud separately (Ortho queue)
        disp.getRenderer().draw(hud.getHudNode());
    }

    /**
     * Creates a new Asteroid at a more or less random location.
     */
    private void spawnRandomAsteroid() {
        // spawn point
        final Vector3f start = new Vector3f(-5000, 5000, -2000);

        // add random values
        start.x += rand.nextFloat() * 12000;
        start.y += rand.nextFloat() * 500;
        start.z += rand.nextFloat() * 12000;

        // 0 = asteroid.obj, 1 = asteroid1.obj
        int model = Math.round(rand.nextFloat() * 1);
        String modelName;
        switch (model) {
        case 1:
            modelName = "asteroid1";
            break;
        default:
            modelName = "asteroid";
            break;
        }

        // create the asteroid
        asteroidFactory.createAsteroidWithTarget("asteroid", modelName, 10f, start,
                earth.getNode().getLocalTranslation().clone(), // target
                new Vector3f(15, 10, 5), // rotation
                (int) ((rand.nextFloat() * 250)) + 120); // speed 
    }
   
}
