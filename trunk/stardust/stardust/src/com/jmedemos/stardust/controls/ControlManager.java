package com.jmedemos.stardust.controls;

import com.jme.input.KeyInput;
import com.jme.input.controls.GameControl;
import com.jme.input.controls.GameControlManager;
import com.jme.input.controls.binding.KeyboardBinding;
import com.jme.input.controls.binding.MouseAxisBinding;
import com.jme.input.controls.binding.MouseOffsetBinding;
import com.jme.input.controls.controller.Axis;
import com.jme.input.controls.controller.RotationController;
import com.jme.scene.Node;

public class ControlManager {
    private static ControlManager instance = null;
    private GameControlManager gcm;
    private RotationController pitchControl = null;
    private RotationController yawControl = null;
    private RotationController rollControl = null;
    
    private ControlManager() {
        gcm = new GameControlManager();
    }
    
    public static ControlManager get () {
        if (instance == null) {
            instance  = new ControlManager();
        }
        return instance;
    }

    public GameControlManager getGcm() {
        return gcm;
    }
    
    public RotationController createRollControl(Node node, float speed) {
        GameControl rollLeft = gcm.addControl("Roll Left");
        rollLeft.addBinding(new KeyboardBinding(KeyInput.KEY_A));
        rollLeft.addBinding(new MouseOffsetBinding(MouseAxisBinding.AXIS_X, true));

        GameControl rollRight = gcm.addControl("Roll Right");
        rollRight.addBinding(new KeyboardBinding(KeyInput.KEY_D));
        rollRight.addBinding(new MouseOffsetBinding(MouseAxisBinding.AXIS_X, false));
        
        rollControl = new RotationController(node, rollRight,  rollLeft, speed, Axis.Z);
        return rollControl;
    }
    
    public RotationController createYawControl(Node node, float speed) {
        GameControl rotateLeft = gcm.addControl("Rotate Left");
        rotateLeft.addBinding(new KeyboardBinding(KeyInput.KEY_Q));
        rotateLeft.addBinding(new MouseOffsetBinding(MouseAxisBinding.AXIS_X, true));

        GameControl rotateRight = gcm.addControl("Rotate Right");
        rotateRight.addBinding(new KeyboardBinding(KeyInput.KEY_E));
        rotateRight.addBinding(new MouseOffsetBinding(MouseAxisBinding.AXIS_X, false));
        
        yawControl = new RotationController(node, rotateLeft,
                rotateRight, speed, Axis.Y);
        return yawControl;
    }
    
    public RotationController createPitchControl(Node node, float speed) {
        GameControl up = gcm.addControl("Pitch Up");
        up.addBinding(new MouseOffsetBinding(MouseAxisBinding.AXIS_Y, false));
        up.addBinding(new KeyboardBinding(KeyInput.KEY_R));
        
        GameControl down = gcm.addControl("Pitch Down");
        down.addBinding(new MouseOffsetBinding(MouseAxisBinding.AXIS_Y, true));
        down.addBinding(new KeyboardBinding(KeyInput.KEY_F));
        
        pitchControl = new RotationController(node, up, down, speed, Axis.X);
        return pitchControl;
    }
    
    /**
     * invert the pitch controls.
     */
    public void invertPitch() {
        GameControl tmp = pitchControl.getNegative();
        pitchControl.setNegative(pitchControl.getPositive());
        pitchControl.setPositive(tmp);
    }
}
