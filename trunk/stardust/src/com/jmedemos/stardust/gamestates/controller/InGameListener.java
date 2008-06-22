package com.jmedemos.stardust.gamestates.controller;

import java.util.concurrent.Callable;

import com.jme.input.controls.GameControl;
import com.jme.input.controls.controller.ControlChangeListener;
import com.jme.util.GameTaskQueueManager;
import com.jmedemos.stardust.core.Game;
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
            Game.getInstance().pause();
//            GameStateManager.getInstance().deactivateChildNamed("InGame");
//            GameStateManager.getInstance().activateChildNamed("Menu");
        }
    }
}
