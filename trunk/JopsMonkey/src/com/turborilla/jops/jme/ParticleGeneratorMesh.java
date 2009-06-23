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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.openmali.FastMath;
import org.softmed.jops.Generator;
import org.softmed.jops.GeneratorBehaviour;
import org.softmed.jops.Particle;

import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.BlendState.DestinationFunction;
import com.jme.scene.state.BlendState.SourceFunction;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jme.util.resource.ResourceLocatorTool;

/**
 * <pre>
 * @author JME1.0 by: Tobias ( http://turborilla.com/ )
 * @author JME2.0-Port: Nicolas 'plusminus' Gramlich ( http://anddev.org )
 * </pre>
 */
public class ParticleGeneratorMesh extends TriMesh {
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final long serialVersionUID = -6348585822707556042L;

	// ===========================================================
	// Fields
	// ===========================================================

	private Generator mGenerator;
	private FloatBuffer mVerticeBuffer, mTextureBuffer, mColorBuffer;
	private IntBuffer mIndexBuffer;
	private Camera mCamera;

	private final Vector3f mPosition = new Vector3f();
	private final Vector3f mCameraUp = new Vector3f();
	private final Vector3f mCameraRight = new Vector3f();
	private final Vector3f mTmpUp = new Vector3f();
	private final Vector3f mTmpRight = new Vector3f();
	private final Vector3f mUp = new Vector3f();
	private final Vector3f mRight = new Vector3f();
	private final Vector3f topLeft = new Vector3f();
	private final Vector3f bottomLeft = new Vector3f();
	private final Vector3f bottomRight = new Vector3f();
	private final Vector2f mTextureBL = new Vector2f();
	private final Vector2f mTextureBR = new Vector2f();
	private final Vector2f mTextureTL = new Vector2f();
	private final ColorRGBA mColor = new ColorRGBA();

	private Quaternion mWorldRotation = new Quaternion();

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public ParticleGeneratorMesh(Generator generator) {
		super();
		this.mGenerator = generator;
		init();
	}

	public ParticleGeneratorMesh(String name, Generator generator) {
		super(name);
		this.mGenerator = generator;
		init();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setCamera(Camera camera) {
		this.mCamera = camera;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================
	
	/* (non-Javadoc)
	 * @see com.jme.scene.Spatial#updateGeometricState(float, boolean)
	 */
	@Override
	public void updateGeometricState(final float pTime, final boolean pInitiator) {
		super.updateGeometricState(pTime, pInitiator);
		update(pTime);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	private void init() {
		final BlendState bs = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
		bs.setEnabled(true);
		bs.setBlendEnabled(true);
		bs.setSourceFunction(getSourceFunctionByFactor(mGenerator.getRender().getSourceFactor()));
		bs.setDestinationFunction(getDestinationFunctionByFactor(mGenerator.getRender().getDestinationFactor()));

		bs.setTestEnabled(true);
		bs.setReference(0.01f);
		bs.setTestFunction(BlendState.TestFunction.GreaterThan);
		setRenderState(bs);

		final TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		ts.setEnabled(true);
		final Texture tex = TextureManager.loadTexture(ResourceLocatorTool.locateResource(
				ResourceLocatorTool.TYPE_TEXTURE, mGenerator.getRender().getTextureName()),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear); // BilinearNearestMipMap);
		// tex.setApply(Texture.AM_MODULATE);
		tex.setWrap(Texture.WrapMode.BorderClamp); // WM_BCLAMP_S_BCLAMP_T;

		ts.setTexture(tex);
		setRenderState(ts);

		final ZBufferState zs = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
		zs.setEnabled(true);
		zs.setWritable(false);
		setRenderState(zs);

		setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		setLightCombineMode(Spatial.LightCombineMode.Off);
		setTextureCombineMode(Spatial.TextureCombineMode.Replace);

		final GeneratorBehaviour generatorBehaviour = mGenerator.getGb();
		final int numTriangles = generatorBehaviour.getNumber();
		final int numVertices = 3 * numTriangles;
		mVerticeBuffer = BufferUtils.createVector3Buffer(numVertices);
		mTextureBuffer = BufferUtils.createVector2Buffer(numVertices);
		mColorBuffer = BufferUtils.createColorBuffer(numVertices);
		mIndexBuffer = BufferUtils.createIntBuffer(numTriangles * 3);

		this.setVertexBuffer(mVerticeBuffer);
		this.setTextureCoords(new TexCoords(mTextureBuffer)); // Second parameter was 0
		this.setColorBuffer(mColorBuffer);
		this.setIndexBuffer(mIndexBuffer);
		for (int i = 0; i < numTriangles; i++) {
			mIndexBuffer.put(i * 3 + 2);
			mIndexBuffer.put(i * 3 + 1);
			mIndexBuffer.put(i * 3 + 0);
		}
		mIndexBuffer.flip();
		for (int i = 0; i < numVertices; i++) {
			mVerticeBuffer.put(0f).put(0f).put(0f);
			mTextureBuffer.put(0f).put(0f);
			mColorBuffer.put(1f).put(1f).put(1f).put(1f);
		}
		mVerticeBuffer.flip();
		mTextureBuffer.flip();
		mColorBuffer.flip();

		updateRenderState();
	}
	
	private void update(final float pTpf) {
		buildTriangles();
	}
	
	private void buildTriangles() {
		final List<Particle> particles = mGenerator.getParticles();
		Particle particle = null;
		int alive = 0;
		if (!mGenerator.isAbsoluteParticleAngle())
			setCameraVectors(); // billboards
		for (int i = 0; i < particles.size(); i++) {
			particle = particles.get(i);
			if (!(particle.life < 0.0f && mGenerator.isKillParticles())) {
				final int vIndex = alive * 3;
				buildTriangle(mVerticeBuffer, vIndex, particle);
				setColor(mColorBuffer, vIndex, particle);
				setTextureCoords(mTextureBuffer, vIndex, particle);
				alive++;
			}
		}
		mIndexBuffer.limit(alive * 3);
		this.setIndexBuffer(mIndexBuffer);
	}

	private void setCameraVectors() {
        if (mCamera != null) {
			mCameraUp.set(mCamera.getUp());
			mCameraRight.set(mCamera.getLeft());
			mCameraUp.normalizeLocal().multLocal(0.5f);
			mCameraRight.normalizeLocal().multLocal(-0.5f);
		} else {
			mCameraUp.set(0f,0.5f,0f);
			mCameraRight.set(0.5f,0f,0f);
		}
		mWorldRotation.set(getWorldRotation());
		mWorldRotation.inverseLocal();
		mWorldRotation.multLocal(mCameraUp);
		mWorldRotation.multLocal(mCameraRight);
	}
	
	private void buildTriangle(final FloatBuffer pVerticeBuffer, final int pVerticeIndex, final Particle pParticle) {
		if (mGenerator.isAbsoluteParticleAngle()) {
			buildOrientedTriangle(pVerticeBuffer, pVerticeIndex, pParticle);
		} else {
			buildBillboardTriangle(pVerticeBuffer, pVerticeIndex, pParticle);
		}
	}	
	
	private void buildOrientedTriangle(final FloatBuffer pVerticeBuffer, final int pVerticeIndex, final Particle pParticle) {
		final float size = pParticle.size;
		final float width = pParticle.width * 0.5f;
		final float height = pParticle.height * 0.5f;

		final float correctedVAngle = pParticle.angleV - FastMath.PI_HALF;

		mPosition.set(pParticle.position.getX(), pParticle.position.getY(), pParticle.position.getZ());

		final float sinCV = FastMath.sin(correctedVAngle);
		final float sinH = FastMath.sin(pParticle.angleH);
		final float sinV = FastMath.sin(pParticle.angleV);
		final float cosH = FastMath.cos(pParticle.angleH);

		mTmpUp.setX(cosH * sinCV);
		mTmpUp.setZ(sinH * sinCV);
		mTmpUp.setY(FastMath.cos(correctedVAngle));

		mTmpRight.setX(cosH * sinV);
		mTmpRight.setZ(sinH * sinV);
		mTmpRight.setY(FastMath.cos(pParticle.angleV));

		mTmpRight.crossLocal(mTmpUp);

		constructTriangle(pVerticeBuffer, pVerticeIndex, size, width, height);

	}

	private void buildBillboardTriangle(final FloatBuffer pVerticeBuffer, final int pVerticeIndex, final Particle pParticle) {
		final float size = pParticle.size;
		final float width = pParticle.width * 0.5f;
		final float height = pParticle.height * 0.5f;

		mPosition.set(pParticle.position.getX(), pParticle.position.getY(), pParticle.position.getZ());

		mTmpUp.set(mCameraUp);
		mTmpRight.set(mCameraRight);

		constructTriangle(pVerticeBuffer, pVerticeIndex, size, width, height);
	}

	private void constructTriangle(final FloatBuffer pVerticeBuffer, final int pVerticeIndex, final float pSize, final float pWidth, final float pHeight) {
		mTmpUp.multLocal(pHeight);
        mTmpRight.multLocal(pWidth);
        
		mUp.set(mTmpUp);
		mUp.multLocal(-0.5f);
		mRight.set(mTmpRight);
		mRight.multLocal(-0.5f);
		
		mUp.addLocal(mRight);
		
		topLeft.set(mTmpUp).subtractLocal(mTmpRight).subtractLocal(mUp);
		mTmpUp.multLocal(-1f);
		bottomLeft.set(mTmpUp).subtractLocal(mTmpRight).subtractLocal(mUp);
		bottomRight.set(mTmpUp).addLocal(mTmpRight).subtractLocal(mUp);

		/*
		topLeft.z =- topLeft.z;
		bottomLeft.z =- bottomLeft.z;
		bottomRight.z =- bottomRight.z;
		position.z =- position.z;
		*/
		
		topLeft.multLocal(pSize).addLocal(mPosition);
		bottomLeft.multLocal(pSize).addLocal(mPosition);
		bottomRight.multLocal(pSize).addLocal(mPosition);
		
		BufferUtils.setInBuffer(bottomLeft, pVerticeBuffer, pVerticeIndex);
		BufferUtils.setInBuffer(bottomRight, pVerticeBuffer, pVerticeIndex+1);
		BufferUtils.setInBuffer(topLeft, pVerticeBuffer, pVerticeIndex+2);
	}
	
	private void setColor(final FloatBuffer pColorBuffer, final int pVIndex, final Particle pParticle) {
		mColor.set(pParticle.color.getRed(), pParticle.color.getGreen(), pParticle.color.getBlue(), pParticle.alpha); // RGBA
		BufferUtils.setInBuffer(mColor, pColorBuffer, pVIndex);
		BufferUtils.setInBuffer(mColor, pColorBuffer, pVIndex+1);
		BufferUtils.setInBuffer(mColor, pColorBuffer, pVIndex+2);
	}
	
	private void setTextureCoords(final FloatBuffer tb, int vIndex,Particle particle) {
		final float texWidth = 1f / particle.texWidth;
		final float texHeight = 1f / particle.texHeight;

		mTextureBL.set(-0.5f, -0.5f);
		mTextureBR.set(+1.5f, -0.5f);
		mTextureTL.set(-0.5f, +1.5f);

		rotate2f(mTextureBL, mTextureBR, mTextureTL, particle.angle);

        final float bias = 0.5f;

        mTextureBL.set(mTextureBL.x * texWidth + bias, mTextureBL.y * texHeight + bias);
		mTextureBR.set(mTextureBR.x * texWidth + bias, mTextureBR.y * texHeight + bias);
		mTextureTL.set(mTextureTL.x * texWidth + bias, mTextureTL.y * texHeight + bias);
        
        BufferUtils.setInBuffer(mTextureBL, tb, vIndex);
        BufferUtils.setInBuffer(mTextureBR, tb, vIndex+1);
        BufferUtils.setInBuffer(mTextureTL, tb, vIndex+2);
	}

	private void rotate2f(final Vector2f pVector1, final Vector2f pVector2, final Vector2f pVector3, final float pAngle) {
		final float cosAngle = FastMath.cos(pAngle);
		final float sinAngle = FastMath.sin(pAngle);
		pVector1.set( pVector1.x * cosAngle - pVector1.y * sinAngle,
				pVector1.y * cosAngle + pVector1.x * sinAngle);
		
		pVector2.set( pVector2.x * cosAngle - pVector2.y * sinAngle,
				pVector2.y * cosAngle + pVector2.x * sinAngle);
		
		pVector3.set( pVector3.x * cosAngle - pVector3.y * sinAngle,
				pVector3.y * cosAngle + pVector3.x * sinAngle);
	}

	protected SourceFunction getSourceFunctionByFactor(final int pGLBlendFactor) {
		switch (pGLBlendFactor) {
			case 1:
				return BlendState.SourceFunction.One;
			case 0:
				return BlendState.SourceFunction.Zero;
			case 772:
				return BlendState.SourceFunction.DestinationAlpha;
			case 774:
				return BlendState.SourceFunction.DestinationColor;
			case 770:
				return BlendState.SourceFunction.SourceAlpha;
			case 768:
				return BlendState.SourceFunction.DestinationColor;
			case 773:
				return BlendState.SourceFunction.OneMinusDestinationAlpha;
			case 775:
				return BlendState.SourceFunction.OneMinusDestinationColor;
			case 771:
				return BlendState.SourceFunction.OneMinusSourceAlpha;
			case 776:
				return BlendState.SourceFunction.SourceAlphaSaturate;
			case 769:
				return BlendState.SourceFunction.OneMinusDestinationColor;

			default:
				throw (new RuntimeException("GL source blend mode not recognized : "
						+ pGLBlendFactor));
		}
	}	

	protected DestinationFunction getDestinationFunctionByFactor(final int pGLBlendFactor) {
		switch (pGLBlendFactor) {
			case 1:
				return BlendState.DestinationFunction.One;
			case 0:
				return BlendState.DestinationFunction.Zero;
			case 772:
				return BlendState.DestinationFunction.DestinationAlpha;
			case 774:
				return BlendState.DestinationFunction.SourceColor; // AlphaState.SB_DST_COLOR;
			case 770:
				return BlendState.DestinationFunction.SourceAlpha;
			case 768:
				return BlendState.DestinationFunction.SourceColor; // AlphaState.SB_DST_COLOR;
			case 773:
				return BlendState.DestinationFunction.OneMinusDestinationAlpha;
			case 775:
				return BlendState.DestinationFunction.OneMinusSourceColor; // AlphaState.SB_ONE_MINUS_DST_COLOR;
			case 771:
				return BlendState.DestinationFunction.OneMinusSourceAlpha;
			case 776:
				return BlendState.DestinationFunction.SourceAlpha; // AlphaState.SB_SRC_ALPHA_SATURATE;
			case 769:
				return BlendState.DestinationFunction.OneMinusSourceColor; // AlphaState.SB_ONE_MINUS_DST_COLOR;
			default:
				throw (new RuntimeException("GL source blend mode not recognized : " + pGLBlendFactor));
		}
	}


	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
