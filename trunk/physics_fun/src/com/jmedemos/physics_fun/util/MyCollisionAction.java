package com.jmedemos.physics_fun.util;

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jme.system.DisplaySystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.RangedAudioTracker;
import com.jmex.physics.contact.ContactInfo;

public class MyCollisionAction extends InputAction {
	private Vector3f contactVelocity = new  Vector3f();
	private RangedAudioTracker tracker = new RangedAudioTracker(SoundUtil.get().getSound(MaterialType.DEFAULT));

	public MyCollisionAction() {
	}

	public void performAction(InputActionEvent evt) {
		ContactInfo info = (ContactInfo) evt.getTriggerData();
        info.getContactVelocity( contactVelocity );
        float vel = contactVelocity.length();
        
        // check that the velocity the items hit is large enough to make a sound
        if (vel > 2) {
        	AudioTrack track = SoundUtil.get().getSound(MaterialType.DEFAULT);
        	track.setVolume(Math.min(vel/100, 1));
        	tracker.setAudioTrack(track);
        	tracker.setToTrack(info.getNode1());
        	tracker.checkTrackAudible(DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation());
        	track.play();
        }
	}
}