package com.jmedemos.physics_fun.util;

import java.util.Hashtable;

import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.AudioTrack.TrackType;


/**
 * Utility Class to play Sounds.
 */
public class SoundUtil {
    /**
     * the singleton instance.
     */
    private static SoundUtil instance = null;

    /**
     * @return returns the Singleton instance.
     */
    public static SoundUtil get() {
        if (instance == null) {
            instance = new SoundUtil();
        }
        return instance;
    }

    /**
     * Table of Materials and its Sounds.
     */
    private Hashtable<MaterialType, AudioTrack> materialSounds = null;

    /**
     * Table of sound effects
     */
    private Hashtable<String, AudioTrack> sounds = null;

    /**
     * Construct the HashTables.
     */
    private SoundUtil() {
    	materialSounds = new Hashtable<MaterialType, AudioTrack>();
        sounds = new Hashtable<String, AudioTrack>();
        
        addMaterialSound("default.wav", MaterialType.DEFAULT);
    }

    /**
     * add a sound effect for a specific material.
     * @param track name of the sound effect.
     * @param material the MaterialType.
     */
    public final void addMaterialSound(final String track, final MaterialType material) {
        // Check if this sound effect was already loaded before.
        AudioTrack sound = sounds.get(track);
        if (sound == null) {
            // create a new AudioTrack if it has not been loaded before.
            sound = AudioSystem.getSystem().createAudioTrack(track, false);
            sound.setType(TrackType.POSITIONAL);
            sound.setRelative(true);
            sound.setLooping(false);
            
            sounds.put(track, sound);
        }
        
        materialSounds.put(material, sound);
    }

    /**
     * return the AudioTrack for a specific material.
     * @param material the material to get a SoundEffect for.
     * @return an AudioTrack
     */
    public AudioTrack getSound(final MaterialType material) {
    	AudioTrack track = materialSounds.get(material);
    	if (track == null) {
    		return materialSounds.get(MaterialType.DEFAULT);
    	}
    	return track;
    }
}
