package com.jmedemos.stardust.gamestates.controller;

import com.jme.input.controls.GameControl;
import com.jme.input.controls.controller.ControlChangeListener;
import com.jmex.game.state.GameStateManager;

/**
 * InGame Input listener.
 */
public class InGameListener implements ControlChangeListener {
    /**
     * Action which gets executed when ESC is pressed.
     * Activates the Menu Gamestate.
     * @param control active Gamecontrol
     * @param oldValue old Value
     * @param newValue new Value
     * @param time time
     */
    public final void changed(final GameControl control, final float oldValue,
            final float newValue, final float time) {
        if (newValue == 1.0f) {
            GameStateManager.getInstance().deactivateChildNamed("InGame");
            GameStateManager.getInstance().activateChildNamed("Menu");
        }
    }
}
