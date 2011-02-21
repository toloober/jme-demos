
package com.jmedemos.stardust.util;

import com.jme.input.controls.GameControl;
import com.jme.input.controls.controller.Axis;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jmex.jbullet.nodes.PhysicsNode;

/**
 * @author Matthew D. Hicks
 */
public class PhysicsRotationController extends Controller {
	
	private static final long serialVersionUID = -911814334954766964L;

	private PhysicsNode spatial;
	private GameControl positive;
	private GameControl negative;
	private float multiplier;
	
	private Quaternion quat;
	private Vector3f dir;
	
	public PhysicsRotationController(PhysicsNode spatial, GameControl positive, GameControl negative, float multiplier, final Axis axis) {
		this.spatial = spatial;
		this.positive = positive;
		this.negative = negative;
		this.multiplier = multiplier;
		
		quat = new Quaternion();
		if (axis == Axis.X) {
			dir = new Vector3f(1.0f, 0.0f, 0.0f);
		} else if (axis == Axis.Y) {
			dir = new Vector3f(0.0f, 1.0f, 0.0f);
		} else if (axis == Axis.Z) {
			dir = new Vector3f(0.0f, 0.0f, 1.0f);
		} else {
			throw new RuntimeException("Unknown axis: " + axis);
		}
	}

	public void update(float time) {
		this.setActive(false);
		float value = positive.getValue() - negative.getValue();
		float delta = (value * time) * multiplier;
		if (value != 0.0f) {
			quat.fromAngleAxis(delta * FastMath.PI, dir);
			spatial.setLocalRotation(
					spatial.getLocalRotation().mult(quat));
		}
		this.setActive(true);
	}

	public GameControl getPositive() {
		return positive;
	}

	public void setPositive(GameControl positive) {
		this.positive = positive;
	}

	public GameControl getNegative() {
		return negative;
	}

	public void setNegative(GameControl negative) {
		this.negative = negative;
	}

	public float getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(float multiplier) {
		this.multiplier = multiplier;
	}

	public Spatial getSpatial() {
		return spatial;
	}

	public void setSpatial(PhysicsNode spatial) {
		this.spatial = spatial;
	}
}

