/**
 * Copyright (c) 2008-2009, Turborilla
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of 'Turborilla' nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
package com.turborilla.jops.jme;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.softmed.jops.ParticleManager;
import org.softmed.jops.ParticleSystem;
import org.softmed.jops.fileloading.DataFormatException;

import com.jme.animation.SpatialTransformer;
import com.jme.app.SimpleGame;
import com.jme.input.FirstPersonHandler;
import com.jme.input.MouseInput;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.state.CullState;
import com.jme.scene.state.CullState.Face;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;

/**
 * Test to make sure both relative and absolute jopsNodes adhere
 * to both rootNode's and the individual jopsNode's transform 
 * (translation, scale, and rotation) 
 * 
 * <pre>
 * @author JME1.0 by: Tobias ( http://turborilla.com/ )
 * @author JME2.0-Port: Nicolas 'plusminus' Gramlich ( http://anddev.org )
 * </pre>
 */
public class TestJopsMonkey extends SimpleGame {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private ParticleManager mParticleManager;

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * Entry point for the test,
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TestJopsMonkey app = new TestJopsMonkey();
		app.setConfigShowMode(ConfigShowMode.ShowIfNoConfig);
		app.start();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void simpleUpdate() {
		if (tpf < 0.1f) {
			mParticleManager.process(tpf);
		}
	}

	/* (non-Javadoc)
	 * @see com.jme.app.BaseSimpleGame#simpleRender()
	 */
	@Override
	protected void simpleRender() {
		super.simpleRender();
	}

	/**
	 * builds the particle systems test
	 * 
	 * @see com.jme.app.SimpleGame#initGame()
	 */
	@Override
	protected void simpleInitGame() {
		display.setTitle("Jops Test");

		rootNode.setLocalScale(5f);

		JopsNode jopsNode, absJopsNode, relJopsNode;
		SpatialTransformer controller;

		jopsNode = new JopsNode("JopsNode");
		jopsNode.setCamera(cam);
		jopsNode.setLocalTranslation(0f, 2f, 0f);
		jopsNode.setLocalScale(3f);

		absJopsNode = new JopsNode("absoluteJopsNode");
		absJopsNode.setCamera(cam);
		absJopsNode.setLocalTranslation(0f, 0f, 0f);
		absJopsNode.setLocalScale(3f);

		relJopsNode = new JopsNode("relativeJopsNode");
		relJopsNode.setCamera(cam);
		relJopsNode.setLocalTranslation(0f, -2f, 0f);
		relJopsNode.setLocalScale(3f);

		rootNode.attachChild(jopsNode);
		rootNode.attachChild(absJopsNode);
		rootNode.attachChild(relJopsNode);

		controller = new SpatialTransformer(1);
		controller.setObject(absJopsNode, 0, -1);
		controller.setScale(0, 0f, new Vector3f(3f,3f,3f)); // scale is constant
		controller.setScale(0, 2f, new Vector3f(3f,3f,3f));
		controller.setPosition(0, 0f, new Vector3f(-1f,0f,0f)); // postition is constant
		controller.setPosition(0, 2f, new Vector3f(-1f,0f,0f));
		controller.setRotation(0, 0f, new Quaternion());
		controller.setRotation(0, 2f, new Quaternion().fromAngleAxis(FastMath.PI, new Vector3f(0f,0f,1f)));
		controller.setRepeatType(Controller.RT_CYCLE);
		rootNode.addController(controller);        

		controller = new SpatialTransformer(1);
		controller.setObject(relJopsNode, 0, -1);
		controller.setScale(0, 0f, new Vector3f(3f,3f,3f));
		controller.setScale(0, 2f, new Vector3f(3f,3f,3f));
		controller.setPosition(0, 0f, new Vector3f(-1f,-2f,0f));
		controller.setPosition(0, 2f, new Vector3f(-1f,-2f,0f));
		controller.setRotation(0, 0f, new Quaternion());
		controller.setRotation(0, 2f, new Quaternion().fromAngleAxis(FastMath.PI, new Vector3f(0f,0f,1f)));
		controller.setRepeatType(Controller.RT_CYCLE);
		rootNode.addController(controller);        


		try {
			final URL textureResource = TestJopsMonkey.class.getClassLoader().getResource("textures/");
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator(textureResource));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		mParticleManager = new ParticleManager();
		try {
			String name = mParticleManager.load(TestJopsMonkey.class.getClassLoader()
					.getResource("systems/candle.ops"));

			ParticleSystem particleSystem;
			particleSystem = mParticleManager.getCopyAttached(name);
			jopsNode.setParticleSystem(particleSystem);
			jopsNode.attachChild(jopsNode.getParticleNode());

			particleSystem = mParticleManager.getCopyAttached(name);
			absJopsNode.setParticleSystem(particleSystem);
			rootNode.attachChild(absJopsNode.getParticleNode()); // absolute orientation (or have I confused the two?)

			particleSystem = mParticleManager.getCopyAttached(name);
			relJopsNode.setParticleSystem(particleSystem);
			relJopsNode.attachChild(relJopsNode.getParticleNode());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (DataFormatException e) {
			e.printStackTrace();
		}

		CullState cs = display.getRenderer().createCullState();
		cs.setCullFace(Face.None);
		rootNode.setRenderState(cs);

		input = new FirstPersonHandler(cam, 10f, 1f);
		input.setEnabled(false);

		MouseInput.get().setCursorVisible(true);

		lightState.setEnabled(false);
		rootNode.setLightCombineMode(LightCombineMode.Off);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
