package com.jmedemos.physics_fun.gamestates;

import com.jme.image.Texture;
import com.jme.renderer.Renderer;
import com.jme.scene.Text;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.game.state.BasicGameState;

/**
 * This GameState displays some Text and a Crosshair in Ortho mode.
 */
public class OrthoGameState extends BasicGameState {
	private float locX = 20;
	private float locY = DisplaySystem.getDisplaySystem().getHeight()-20;
	private float lastLocY = locY;
	private float offsetY = 0;
	private TextureState font = null;
	private AlphaState textAlphaState = null;
	
	/**
	 * Construct the GameState.
	 * @param name the Gamestates name.
	 */
	public OrthoGameState(String name) {
		super(name);
		
		// we want the text alway pass the ZBuffer Test,, so it gets drawn over everything else.
		ZBufferState zs = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
		zs.setEnabled(true);
		zs.setFunction(ZBufferState.CF_ALWAYS);
		rootNode.setRenderState(zs);
		
		// An AlphaState to display the Text correctly
		textAlphaState = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
		textAlphaState.setBlendEnabled(true);
		textAlphaState.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		textAlphaState.setDstFunction(AlphaState.DB_ONE);
		textAlphaState.setTestEnabled(true);
		textAlphaState.setTestFunction(AlphaState.TF_GREATER);
		textAlphaState.setEnabled(true);
		rootNode.setRenderState(textAlphaState);

		// load the font texture for the ortho gamestate 
		font = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		font.setTexture(TextureManager.loadTexture(
						ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "defaultfont.tga"),
						Texture.MM_LINEAR, Texture.FM_LINEAR));
		font.setEnabled(true);
		
		// add the text to display
		addText("Press ESC to quit");
		addText("Press TAB to enable/disable GUI");
		addText("Press SPACE to create and release a new Object");
		
		// create a crosshair in the middle of the screen
		setCrosshair();
		
		// finish 
		rootNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		
		rootNode.updateGeometricState(0.0f, true);
		rootNode.updateRenderState();
	}
	
	/**
	 * create a crosshair to easely see the center of the screen.
	 */
	public void setCrosshair() {
		AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
        as.setBlendEnabled(true);
        as.setTestEnabled(false);
        as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as.setEnabled(true);
		TextureState cross = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		cross.setTexture(TextureManager.loadTexture(ResourceLocatorTool.locateResource(
		                ResourceLocatorTool.TYPE_TEXTURE,"crosshair.png"),
						Texture.MM_LINEAR, Texture.FM_LINEAR));
		cross.setEnabled(true);
		Quad q = new Quad("quad", 30, 30);
		q.setLocalTranslation(DisplaySystem.getDisplaySystem().getWidth()/2,
				DisplaySystem.getDisplaySystem().getHeight()/2, 0);
		q.setRenderState(as);
		q.setRenderState(cross);
		rootNode.attachChild(q);
	}
	
	/**
	 * Add a Text line.
	 * @param text text to add
	 */
	private void addText(String text) {
		Text txtObj = new Text("text", text);
		txtObj.setLocalTranslation(locX, lastLocY, 0);
		txtObj.setRenderState(font);
		lastLocY = lastLocY - txtObj.getHeight();
		lastLocY -= offsetY;
		rootNode.attachChild(txtObj);
	}
	
	/**
	 * we don't need to update anything, since nothing is moving around.
	 */
	public void update(float tpf) {
	}

	/**
	 * not much  to clean up here.
	 */
	public void cleanup() {
		this.textAlphaState = null;
		this.rootNode = null;
	}
}
