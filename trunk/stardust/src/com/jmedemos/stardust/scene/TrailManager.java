package com.jmedemos.stardust.scene;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.jme.image.Texture;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.effects.TrailMesh;

public class TrailManager {
	private static Logger log = Logger.getLogger(TrailManager.class.getName());
	private static TrailManager instance = null;
	private Node root = null;
	private BlendState bs;
	private ZBufferState zs;
	private CullState cs;
	private TextureState ts;
	private HashMap<Spatial, TrailMesh> trails = null;
	
	private TrailManager(final Node root) {
		this.root = root;
		
		trails = new HashMap<Spatial, TrailMesh>();
		
		Renderer r = DisplaySystem.getDisplaySystem().getRenderer();
		ts = r.createTextureState();
		ts.setEnabled(true);
		Texture t1 = TextureManager.loadTexture(ResourceLocatorTool.locateResource(
						ResourceLocatorTool.TYPE_TEXTURE, "trail_y1.png"),
						Texture.MinificationFilter.Trilinear,
						Texture.MagnificationFilter.Bilinear);
		ts.setTexture(t1);

		bs = r.createBlendState();
		bs.setBlendEnabled(true);
		bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		bs.setDestinationFunction(BlendState.DestinationFunction.One);
		bs.setTestEnabled(true);
		
		zs = r.createZBufferState();
		zs.setWritable(false);

		cs = r.createCullState();
		cs.setCullFace(CullState.Face.None);
		cs.setEnabled(true);
	}

	public static void create(final Node root) {
		instance = new TrailManager(root);
	}

	public static TrailManager get() {
		if (instance == null) {
			log.severe("TrailManager not yet initialized");
		}
		return instance;
	}
	public void removeTrail(Node parent) {
	    trails.get(parent).removeFromParent();
		trails.remove(parent);
	}
	
	public TrailMesh createTrail(Spatial parent) {
		TrailMesh trail = new TrailMesh("trail", 200);

		trail.setUpdateSpeed(60);
		trail.setFacingMode(TrailMesh.FacingMode.Billboard);
		trail.setUpdateMode(TrailMesh.UpdateMode.Interpolate);

		trail.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		trail.setCullHint(CullHint.Never);

		trail.setRenderState(bs);
		trail.setRenderState(ts);
		trail.setRenderState(zs);
		trail.setRenderState(cs);

		trails.put(parent, trail);
		
		root.attachChild(trail);	
		return trail;
	}

	public void update(float tpf) {
		for (Map.Entry<Spatial, TrailMesh> t: trails.entrySet()) {
			t.getKey().updateWorldVectors();
			t.getValue().setTrailFront(
					t.getKey().getWorldTranslation(), 30, tpf);
			t.getValue().update(DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation());
		}
	}
}
