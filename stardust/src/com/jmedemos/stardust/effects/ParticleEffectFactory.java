package com.jmedemos.stardust.effects;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmedemos.stardust.scene.asteroid.OnDeadListener;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;
import com.jmex.effects.particles.ParticleSystem.ParticleType;

/**
 * creates and manages the particle effects for an explosion.
 */
public class ParticleEffectFactory {
    private ArrayList<ParticleMesh> explosions = null;
    private ArrayList<ParticleMesh> asteroidTrails = null;
    private ArrayList<ParticleMesh> missileTrails = null;
    private BlendState bs = null;
    private TextureState ts = null;
    private ZBufferState zs = null;
    private static ParticleEffectFactory instanze = null;
    private Node rootNode = null;
    
    public static ParticleEffectFactory get() {
        if (instanze == null) {
            Logger.getLogger(ParticleEffectFactory.class.toString())
                    .severe("ParticleEffectFactory not yet initialized");
        }
        return instanze;
    }
    
    public static void init(final Node root) {
        if (instanze == null) {
            instanze = new ParticleEffectFactory(root);
        }
    }
    
    /**
     * creates the RenderStates needed for the particle effects.
     * @param root scene root node to attach the particle effects to.
     */
    private ParticleEffectFactory(final Node root) {
        explosions = new ArrayList<ParticleMesh>();
        asteroidTrails = new ArrayList<ParticleMesh>();
        missileTrails = new ArrayList<ParticleMesh>();
        
        rootNode = root;
        
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        bs = display.getRenderer().createBlendState();
        bs = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        bs.setBlendEnabled(true);
        bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        bs.setDestinationFunction(BlendState.DestinationFunction.One);
        bs.setTestEnabled(true);
        bs.setTestFunction(BlendState.TestFunction.GreaterThan);

        ts = display.getRenderer().createTextureState();
        ts.setTexture(TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE,
                        "data/textures/flaresmall.jpg"), 
                        Texture.MinificationFilter.BilinearNoMipMaps,
                        Texture.MagnificationFilter.Bilinear));

        zs = display.getRenderer().createZBufferState();
        zs.setWritable(false);
        zs.setEnabled(true);

        for (int i = 0; i < 5; i++) {
            createExplosion();
        }
        
        for (int i = 0; i < 5; i++) {
            createMissileTrail();
        }
    }
    
    /**
     * Searches and returns a unused ParticleEffect.
     * If all effects are in use, a new one is created and added to the pool.
     * @return a unused particle effect
     */
    public ParticleMesh getExplosion() {
        for (int x = 0, tSize = explosions.size(); x < tSize; x++) {
            ParticleMesh e = explosions.get(x);
            if (!e.isActive()) {
                return e;
            }
        }
        return createExplosion();
    }
    
    /**
     * Searches and returns a unused ParticleEffect.
     * If all effects are in use, a new one is created and added to the pool.
     * @param asteroidmodel the TriMesh of an asteroid 
     * @return a unused particle effect
     */
    public ParticleMesh getAsteroidTrail(final Spatial asteroidmodel) {
        for (int x = 0, tSize = asteroidTrails.size(); x < tSize; x++) {
            ParticleMesh e = asteroidTrails.get(x);
            if (!e.isActive()) {
                Logger.getLogger(this.getClass().getName()).warning("re-using asteroid trail, parent:" +e.getParent());
                e.removeFromParent();
                e.getParticleController().setRepeatType(Controller.RT_WRAP);
                e.forceRespawn();
                return e;
            }
        }
        return createAstroidTrail(asteroidmodel);
    }
    
    /**
     * Searches and returns a unused particle effect.
     * If all effects are in use, a new one is created and added to the pool.
     * @return a unused particle effect
     */
    public ParticleMesh getMissileTrail() {
        for (int x = 0, tSize = missileTrails.size(); x < tSize; x++) {
            ParticleMesh e = missileTrails.get(x);
            if (!e.isActive()) {
                e.getParticleController().setRepeatType(Controller.RT_WRAP);
                e.forceRespawn();
                return e;
            }
        }
        return createMissileTrail();
    }
    
    /**
     * creates a new particle mesh representing an explosion.
     * @return particle mesh of an explosion
     */
    private ParticleMesh createExplosion() {
        ParticleMesh pMesh = ParticleFactory.buildParticles("explosion", 30);
        pMesh.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
        pMesh.setMaximumAngle(3.1415927f);
        pMesh.setMinimumAngle(0);
        pMesh.getParticleController().setSpeed(0.1f);
        pMesh.setMinimumLifeTime(20.0f);
        pMesh.setMaximumLifeTime(150.0f);
        pMesh.setStartSize(15);
        pMesh.setEndSize(2);
        pMesh.getParticleController().setControlFlow(false);
        pMesh.getParticleController().setRepeatType(Controller.RT_CLAMP);
        pMesh.warmUp(80);
        pMesh.setInitialVelocity(2.5f);
        pMesh.setStartColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 1.0f));
        pMesh.setEndColor(new ColorRGBA(1.0f, 0.24313726f, 0.03137255f, 0.0f));
        pMesh.setRenderState(ts);
        pMesh.setRenderState(bs);
        pMesh.setRenderState(zs);

        rootNode.attachChild(pMesh);
        rootNode.updateRenderState();
       
        explosions.add(pMesh);
        
        return pMesh;
    }
    
    /**
     * creates a new particle mesh representing an asteroid trail.
     * @return particle mesh of an asteroid trail
     */
    private ParticleMesh createAstroidTrail(final Spatial asteroidmodel) {
        ParticleMesh pMesh = ParticleFactory.buildParticles("tail", 180,
                ParticleType.Quad);
        pMesh.setEmissionDirection(new Vector3f(1, 1, 1));
        pMesh.setOriginOffset(new Vector3f(0, 0, 0));
        pMesh.setInitialVelocity(0.005f);
        pMesh.setStartSize(50);
        pMesh.setEndSize(1);
        pMesh.setMinimumLifeTime(5000f);
        pMesh.setMaximumLifeTime(7000f);
        pMesh.setStartColor(new ColorRGBA(0.5f, 0.5f, 1, 0.5f));
        pMesh.setParticleSpinSpeed(180 * FastMath.DEG_TO_RAD);
        pMesh.setGeometry((Geometry)asteroidmodel);
        pMesh.getParticleController().setControlFlow(false);
        
        pMesh.forceRespawn();
        
        pMesh.setModelBound(new BoundingBox());
        pMesh.updateModelBound();
        pMesh.setCullHint(Spatial.CullHint.Never);

        // removes the asteroid from the scene after the particle trail has died
        pMesh.getParticleController().addListener(new OnDeadListener());
        
        pMesh.setRenderState(ts);
        pMesh.setRenderState(bs);
        pMesh.setRenderState(zs);
        
        asteroidTrails.add(pMesh);
        
        return pMesh;
    }
    
    /**
     * creates a new particle mesh representing an missile trail.
     * @return particle mesh of an missile trail
     */
    private ParticleMesh createMissileTrail() {
        ParticleMesh pMesh = ParticleFactory.buildParticles("tail", 200,
                ParticleType.Quad);
        pMesh.setEmissionDirection(new Vector3f(1, 1, 1));
        pMesh.setInitialVelocity(0);
        pMesh.setStartSize(1f);
        pMesh.setEndSize(3.45f);
        pMesh.setMinimumLifeTime(500);
        pMesh.setMaximumLifeTime(500);
        pMesh.setStartColor(ColorRGBA.lightGray.clone());
        pMesh.setEndColor(ColorRGBA.black.clone());
        pMesh.setParticleSpinSpeed(180 * FastMath.DEG_TO_RAD);
        pMesh.getParticleController().setControlFlow(true);
//        pMesh.setReleaseRate(500);
        pMesh.forceRespawn();
        
        pMesh.setModelBound(new BoundingBox());
        pMesh.updateModelBound();
        pMesh.setCullHint(Spatial.CullHint.Never);

        // removes the missile from the scene after the particle trail has died
        pMesh.getParticleController().addListener(new OnDeadListener());
        pMesh.setRenderState(ts);
        pMesh.setRenderState(bs);
        pMesh.setRenderState(zs);
        
        missileTrails.add(pMesh);
        
        return pMesh;
    }
    
    /**
     * Sets the correct location of the explosion and forces a respawn
     * of particles.
     * @param location location to spawn the explosion.
     */
    public void spawnExplosion(Vector3f location) {
        ParticleMesh mesh = getExplosion();
        mesh.getLocalTranslation().set(location);
        mesh.updateGeometricState(0, true);
        mesh.forceRespawn();
    }
    
}
