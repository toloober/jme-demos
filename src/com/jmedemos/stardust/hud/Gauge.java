package com.jmedemos.stardust.hud;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;

/**
 * Displays a dynamic Value such as throttle / speed etc.
 * with a bar within a frame.  
 */
public class Gauge {
	/**
	 * Renderer alias.
	 */
	private Renderer renderer = DisplaySystem.getDisplaySystem().getRenderer(); 
	/**
	 * the Gauge quad.
	 */
	private Quad gauge = null;
	
	/**
	 * The Gauges frame Quad (Background).
	 */
	private Quad gaugeFrame = null;
	
	/**
	 * The node to attach the Gauge to.
	 */
	private Node gaugeNode = null;
	
	/**
	 * Gauge width (with Border).
	 */
	private float width = 0;
	
	/**
	 * Gauge heigth (with Border).
	 */
	private float heigth = 0;
	
	/**
	 * Border thinkness in Pixel.
	 */
	private int border;
	
	/**
	 * min Value.
	 */
	private float min = -1;
	
	/**
	 * max Value.
	 */
	private float max = 1;
	
	/**
	 * the Gauge Texturestate.
	 */
	private TextureState tsGauge = null;
	
	/**
	 * Loads the Gauge Texture and creates a Quad with the correct size.
	 * @param texture
	 * @param xPos
	 * @param yPos
	 */
	public Gauge(String gaugeTex, String gaugeFrameTex, float xPos, float yPos) {
		// load the textures
		tsGauge = renderer.createTextureState();
		tsGauge.setTexture(TextureManager.loadTexture(
				ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, gaugeTex),
				Texture.MM_LINEAR, Texture.FM_LINEAR, 1.0f, true));
		
		TextureState tsGaugeFrame = renderer.createTextureState();
		tsGaugeFrame.setTexture(TextureManager.loadTexture(
		        ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, gaugeFrameTex),
		        Texture.MM_LINEAR, Texture.FM_LINEAR, 1.0f, true));
		
		// get width / height of the gauge
		width = tsGauge.getTexture().getImage().getWidth();
		heigth = tsGauge.getTexture().getImage().getHeight();
		
		// calculate the border thinkness
		border = tsGaugeFrame.getTexture().getImage().getWidth() - tsGauge.getTexture().getImage().getWidth();
		
		// create the gauge quads and the Node to attach the quads to
		gauge = new Quad("gauge", width, heigth);
		gaugeFrame = new Quad("gaugeFrame", width+border, heigth+border);
		gaugeNode = new Node("gaugeNode");
		gaugeNode.attachChild(gauge);
		gaugeNode.attachChild(gaugeFrame);
		// move the gauge inside the Frame
		gauge.setLocalTranslation(border/2, border/2, 0);
		
        // apply texturestates to the quads
        gauge.setRenderState(tsGauge);
        gaugeFrame.setRenderState(tsGaugeFrame);
        
        // create a alphastate for the Gaugenode
        AlphaState as = renderer.createAlphaState();
        as.setBlendEnabled(true);
        as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as.setTestEnabled(false);
        gaugeNode.setRenderState(as);
        
        gaugeNode.setLocalTranslation(xPos+width+border, yPos+heigth+border, 0);
        gaugeNode.updateGeometricState(0, true);
        gaugeNode.updateRenderState();
	}
	/**
	 * Sets the Gauge value.
	 * @param value
	 */
	public final void setGauge(float value) {
		if (value > max) {
			value = max;
		}
        float range = max - min;
        float adjustedRange = value - min;
        float percent = adjustedRange / range;
 
        tsGauge.getTexture().setTranslation(new Vector3f(0, 1 - percent, 0));
	}

	/**
	 * Sets the Position for the gauge.
	 * 0 ,0 means the lower left corner of the gauge will be in the
	 * lower left corner of the Screen.
	 * @param x position
	 * @param y position
	 */
	public final void setPosition(final float x, final float y) {
		gaugeNode.setLocalTranslation(x, y, 0);
	}
    
	/**
	 * sets the min Value, can be negative.
	 * @param min min val.
	 */
    public final void setMinimum(final float min) {
        this.min = min;
    }
 
	/**
	 * sets the max Value.
	 * @param max max value.
	 */
    public final void setMaximum(final float max) {
        this.max = max;
    }
    
    /**
     * returns the node with the attached gauge quads.
     * @return gauge node.
     */
    public final Node getNode() {
    	return gaugeNode;
    }
    
}
