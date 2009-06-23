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

import java.util.ArrayList;
import java.util.List;

import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Quaternion4f;
import org.softmed.jops.Generator;
import org.softmed.jops.ParticleSystem;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;

/**
 * <pre>
 * @author JME1.0 by: Tobias ( http://turborilla.com/ )
 * @author JME2.0-Port: Nicolas 'plusminus' Gramlich ( http://anddev.org )
 * </pre>
 */
public class JopsNode extends Node {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final long serialVersionUID = 2104378643740035361L;

	// ===========================================================
	// Fields
	// ===========================================================

	/** The node holding the particles */
	private Node mParticleNode;
	private ArrayList<ParticleGeneratorMesh> mGenerators;
	private ParticleSystem mParticleSystem;
	private Camera mCamera;

	private Quaternion4f maliQuat = new Quaternion4f();
	private float[] mPosition = new float[3];
	private Vector3f mWorldTranslation = new Vector3f();
	private Quaternion mWorldRotation = getWorldRotation();

	// ===========================================================
	// Constructors
	// ===========================================================
	
	/**	Create a JopsNode without a name. */
	public JopsNode() {
		init();
	}

	/**
	 * Create a JopsNode with a name. 
	 * @param name
	 */
	public JopsNode(String name) {
		super(name);
		init();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * This is the node containing the particles, if you attach it to
	 * this node you get a relatively oriented particle system.
	 * e.g. jopsNode.attachChild(jopsNode.getParticleNode());
	 * 
	 * Attach it to another node to get an absolute system.
	 * e.g. rootNode.attachChild(jopsNode.getParticleNode());
	 * 
	 * @return the particleNode
	 */
	public Node getParticleNode() {
		return mParticleNode;
	}
	
	public void setCamera(Camera camera) {
		this.mCamera = camera;
	}

	/**
	 * @return the particleSystem
	 */
	public ParticleSystem getParticleSystem() {
		return mParticleSystem;
	}

	/**
	 * @param pParticleSystem the particleSystem to set
	 */
	public void setParticleSystem(final ParticleSystem pParticleSystem) {
		this.mParticleSystem = pParticleSystem;
		if (pParticleSystem.getRotation() == null) 
			pParticleSystem.setRotation(new org.openmali.vecmath2.Matrix4f(
					new float[] {
						1f,0f,0f,0f,
						0f,1f,0f,0f,
						0f,0f,1f,0f,
						0f,0f,0f,1f}));
		
		if (pParticleSystem.getPosition() == null) 
			pParticleSystem.setPosition(new Point3f());
		
		installGenerators();
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	/* (non-Javadoc)
	 * @see com.jme.scene.Spatial#updateGeometricState(float, boolean)
	 */
	@Override
	public void updateGeometricState(float time, boolean initiator) {
		super.updateGeometricState(time, initiator);
		update(time);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	private void init() {
		mParticleNode = new Node(getName() + ":ParticleNode");
		mGenerators = new ArrayList<ParticleGeneratorMesh>();
	}
	
	
	public void update(final float pTpf) {
		if (mParticleSystem != null && mParticleNode.getParent() != this) { // relative or absolute positioning of particles
			// Get this jopsNode's world transformation
			mWorldTranslation.set(getWorldTranslation());
			mWorldRotation.set(getWorldRotation());

			// update particle node's localScale to be the same as the jopsNode's
			// Necessary since there is no way to scale the JOPS ParticleSystem
			mParticleNode.getLocalScale().set(getLocalScale());
			mWorldTranslation.divideLocal(getWorldScale());
			
			// update system rotation to correspond to this node's rotation
			maliQuat.set(mWorldRotation.x, mWorldRotation.y, mWorldRotation.z, mWorldRotation.w);
			mParticleSystem.getRotation().set(maliQuat);
			
			// update system position to correspond to this node's position
			// This trickery is necessary since JOPS internally applies position before rotation, so we need to cancel that out
			mWorldRotation.inverseLocal();
			mWorldRotation.mult(mWorldTranslation, mWorldTranslation);
			mParticleSystem.getPosition().set( mWorldTranslation.toArray(mPosition) ) ;
		}
	}
		
	private ParticleGeneratorMesh createGenerator(final Generator pGenerator) {
		ParticleGeneratorMesh particleGenerator = new ParticleGeneratorMesh(pGenerator.getName(), pGenerator);
		mParticleNode.attachChild(particleGenerator);
		particleGenerator.setCamera(mCamera);
		return particleGenerator;
	}
	
	private void installGenerators() {
		final List<Generator> generators = mParticleSystem.getGenerators();
		for(int i = 0 ; i < generators.size() ; i++)
			createGenerator(generators.get(i));
	}
	
	@SuppressWarnings("unused")
	private void clearGenerators() {
		for (int i = 0 ; i < mGenerators.size() ; i++) {
			mParticleNode.detachChild(mGenerators.get(i));
		}
		mGenerators.clear();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
