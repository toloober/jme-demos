package com.jmedemos.stardust.hud;

import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jme.util.Timer;
import com.jmedemos.stardust.scene.PlayerShip;

/**
 * The HUD displays different Information for the Player like:.
 * Health of the Player/Ship, current Weapon.
 */
public class Hud {

    /**
     * the Display.
     */
    private DisplaySystem display = DisplaySystem.getDisplaySystem();

    /**
     * height of the current resolution.
     */
    private float screenHeight;

    /**
     * the main HUD Node.
     */
    private Node hudNode = null;

    /**
     * the FPS Node, which will be attached to the hudNode.
     */
    private Node fpsNode = null;

    /**
     * Text which displays the Frames per Second.
     */
    private Text fpsText = null;

    /**
     * reference to the Player, to read the information.
     */
    private PlayerShip player = null;

    /**
     * the Player info-Node.
     */
    private Node playerInfoNode = null;

    /**
     * Current throttle.
     */
    private Text currentThrottle = null;

    /**
     *  current Target.
     */
    private Text currentTarget = null;
    
    private Gauge throttleGauge = null;
    private Gauge speedGauge = null;
    private Gauge healthGauge = null;
    
    /**
     * HUD update rate in ms.
     */
    private float updateRate = 50;
    
    /**
     * time of last update.
     */
    private float lastUpdate = 0;
    
    /**
     * creates a HUD Node and attaches it to the Scene-Root.
     * 
     * @param width the displays width in pixel
     * @param heigth the displays heigth in pixel
     * @param player the player
     */
    public Hud(final float width, final float heigth, final PlayerShip player) {
        screenHeight = heigth;
        this.player = player;

        hudNode = new Node("HUD");

        setupFps();
        setupPlayerInfos();

        LightState ls = display.getRenderer().createLightState();
        ls.setEnabled(false);
        hudNode.setLightCombineMode(LightState.REPLACE);
        hudNode.setRenderState(ls);

        hudNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        hudNode.updateRenderState();
    }

    /**
     * init Frames per Second.
     */
    private void setupFps() {
        fpsNode = new Node("FPS Node");

        fpsText = Text.createDefaultTextLabel("fps", "FPS: " + 0);
        fpsText.setLocalScale(1.5f);
        fpsText.setLocalTranslation(10,
                screenHeight - 10 - fpsText.getHeight(), 0);

        fpsNode.attachChild(fpsText);
        hudNode.attachChild(fpsNode);
    }

    /**
     * display player Info.
     */
    private void setupPlayerInfos() {
        playerInfoNode = new Node("Playerinfo Node");

        // throttle
        currentThrottle = Text.createDefaultTextLabel("speed",
                "current Throttle:"
                        + player.getPhysicsThrustController().getThrottle());
        currentThrottle.setLocalTranslation(10, 10 + currentThrottle
                .getHeight() * 1, 0);
        playerInfoNode.attachChild(currentThrottle);

        // target
        currentTarget = Text.createDefaultTextLabel("target", "current Target: none");
        currentTarget.setLocalTranslation(10, 10 + currentTarget.getHeight() * 2, 0);
        playerInfoNode.attachChild(currentTarget);
        
        playerInfoNode.setLocalScale(1f);
        hudNode.attachChild(playerInfoNode);
        
        throttleGauge = new Gauge("gaugeblue.png", "gaugeframe.png", 0, 50);
        hudNode.attachChild(throttleGauge.getNode());
        
        speedGauge = new Gauge("gaugeblue.png", "gaugeframe.png", 50, 50);
        speedGauge.setMaximum(player.getPhysicsThrustController().getMaxForwardSpeed());
        speedGauge.setMinimum(-player.getPhysicsThrustController().getMaxReverseSpeed());
        hudNode.attachChild(speedGauge.getNode());
        
        healthGauge = new Gauge("gaugeblue.png", "gaugeframe.png", 150, 50);
        healthGauge.setMaximum(100);
        healthGauge.setMinimum(0);
        hudNode.attachChild(healthGauge.getNode());
    }


    /**
     * Update HUD elements with current Values.
     * @param tpf Time since last update
     */
    public final void update(final float tpf) {
    	if (lastUpdate + updateRate*0.001 < Timer.getTimer().getTimeInSeconds()) {
    		lastUpdate = Timer.getTimer().getTimeInSeconds();
    	} else {
    		// chill
    		return;
    	}
    	
        fpsText.print("FPS:" + (int) (1 / tpf));
        currentThrottle.print("current Throttle: "
        		  + player.getPhysicsThrustController().getThrottle()
        		  + " Speed: " +player.getPhysicsThrustController().getSpeed());
//                + player.getThrottleControl().getCurrentThrottle());
        
        if (player.getTargetDevice().getCurrentTarget() != null) {
            currentTarget.print("current Target: " +player.getTargetDevice().getCurrentTarget().getName());
        } else {
            currentTarget.print("current Target: none");
        }
        if (player.getTargetDevice().getLocked()) {
            currentTarget.print(currentTarget.getText() + " [LOCKED]");
        }
        throttleGauge.setGauge(player.getPhysicsThrustController().getThrottle());
        speedGauge.setGauge(player.getPhysicsThrustController().getSpeed());
        healthGauge.setGauge(player.getHealth());
    }

    /**
     * @return return the hud Node.
     */
    public final Node getHudNode() {
        return hudNode;
    }
}
