package com.jmedemos.stardust.sound;

import java.util.Hashtable;
import java.util.logging.Logger;

import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jmedemos.stardust.core.Game;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.RangedAudioTracker;
import com.jmex.audio.AudioTrack.TrackType;
import com.jmex.audio.MusicTrackQueue.RepeatType;

/**
 * Utility class with methods to manage Sound fx and music.
 */
public class SoundUtil {
    /** Singleton instance.*/
    private static SoundUtil instance = null;
    /** Index of the Intro Musik.  */
    public static int BG_SOUND_INTRO = 0;
    /** Index of the InGame Musik. */
    public static int BG_SOUND_INGAME = 1;
    /** Soundeffekt of a bullet shot. */
    public static int BG_SOUND_SHOT = 2;
    /** Soundeffekt of a missile shot. */
    public static int BG_MISSILE_SHOT = 3;
    /** Soundeffect for target sportted. */
    public static int BG_TARGET_SPOT = 4;
    /** Explosion Soundeffekt. */
    private AudioTrack exp = null;
    /** table of positional objects with its soundeffect. */
    private Hashtable<Spatial, RangedAudioTracker> trackers = null;
    /** list of soundeffects. */
    private Hashtable<String, AudioTrack> sounds = null;
    /** play sound effects. */
    private boolean enableSoundFx = true;
    /** play background music. */
    private boolean enableMusic = true;
    
    /**
     * returns the singleton instances.
     * @return singleton instance
     */
    public static SoundUtil get() {
        if (instance == null) {
            instance = new SoundUtil();
        }
        return instance;
    }

    /**
     * Konstruktor. Hash Tabellen erstellen.
     */
    private SoundUtil() {
        trackers = new Hashtable<Spatial, RangedAudioTracker>();
        sounds = new Hashtable<String, AudioTrack>();
    }

    /**
     * Hintergrund-Musik laden und der Queue hinzufuegen.
     */
    public final void initMusic() {
        // reference to the Audiosystem
        AudioSystem audio = AudioSystem.getSystem();

        if (!AudioSystem.isCreated()) {
            Logger.getLogger("util.SoundUtil").severe(
                    "Audiosystem noch nicht bereit!");
            Game.getInstance().quit();
        }

        // load Intro music
        AudioTrack intro = audio.createAudioTrack("/data/sounds/stardust.ogg",
                false);
        intro.setType(TrackType.MUSIC);
        intro.setRelative(false);
        intro.setTargetVolume(0.7f);
        intro.setLooping(true);

        // Die InGame Hintergrund Musik laden
        AudioTrack ingame = audio.createAudioTrack(
                "/data/sounds/stardustmain.ogg", false);
        ingame.setType(TrackType.MUSIC);
        ingame.setRelative(false);
        ingame.setTargetVolume(0.7f);
        ingame.setLooping(true);

        // add track to the Queue
        audio.getMusicQueue().setRepeatType(RepeatType.ONE);
        audio.getMusicQueue().setCrossfadeinTime(1.0f);
        audio.getMusicQueue().setCrossfadeoutTime(1.0f);
        audio.getMusicQueue().addTrack(intro);
        audio.getMusicQueue().addTrack(ingame);

        // bullet 
        AudioTrack bullet_shoot = audio.createAudioTrack("sentry_shoot.wav",
                false);
        bullet_shoot.setType(TrackType.MUSIC);
        bullet_shoot.setRelative(false);
        bullet_shoot.setVolume(0.3f);
        bullet_shoot.setTargetVolume(0.3f);
        bullet_shoot.setLooping(false);
        audio.getMusicQueue().addTrack(bullet_shoot);

        // missile
        AudioTrack missile_shot = audio.createAudioTrack("rocket_shoot.wav",
                false);
        missile_shot.setType(TrackType.MUSIC);
        missile_shot.setRelative(false);
        missile_shot.setVolume(0.3f);
        missile_shot.setTargetVolume(0.3f);
        missile_shot.setLooping(false);
        audio.getMusicQueue().addTrack(missile_shot);
        
        // target spotted
        AudioTrack target_spot = audio.createAudioTrack("sentry_spot.wav",
                false);
        target_spot.setType(TrackType.MUSIC);
        target_spot.setRelative(false);
        target_spot.setVolume(0.3f);
        target_spot.setTargetVolume(0.3f);
        target_spot.setLooping(false);
        audio.getMusicQueue().addTrack(target_spot);
        
        // Explosion
        exp = audio.createAudioTrack("explosion-01.wav", false);
        exp.setType(TrackType.POSITIONAL);
        exp.setTargetVolume(1.0f);
        exp.setMinVolume(0.2f);
        exp.setMaxVolume(1.0f);
        exp.setLooping(false);
        exp.setRelative(true);
    }

    /**
     * play the explosion sound effect at a given location in the world.
     * @param pos Position der Explosion.
     */
    public final void playExplosion(final Vector3f pos) {
    	if (!enableSoundFx) {
    		return;
    	}
        exp.setWorldPosition(pos);
        exp.play();
    }

    /**
     * Soundeffekt laden und in die Liste der Positionierten Sounds aufnehmen.
     * Falls der Soundeffekt schon mal geladen wurde, wird dieser aus der
     * HashTabelle geholt.
     * 
     * @param track Soundeffekt.
     * @param emitter Objekt von welchem der Soundeffekt abgespielt wird.
     */
    public final void addFx(final String track, final Spatial emitter) {
        // Check ob dieser Sound schon geladen wurde
        AudioTrack sound = sounds.get(track);
        if (sound == null) {
            // Falls dieser Track bisher noch nciht geladen wurde, diesen
            // neu erstellen und in die Hash Tabelle einfuegen.
            sound = AudioSystem.getSystem().createAudioTrack(track, false);
            sound.setType(TrackType.POSITIONAL);
            sound.setRelative(true);
            sound.setLooping(true);

            // Name des Soundeffektes zusammen mit dem AudioTrack in der
            // HashTabelle speichern.
            sounds.put(track, sound);
        }

        RangedAudioTracker tracker = new RangedAudioTracker(sound);
        tracker.setPlayRange(800);
        tracker.setStopRange(1000);
        tracker.setFadeTime(2.0f);
        tracker.setToTrack(emitter);
        tracker.setTrackIn3D(true);
        tracker.setMaxVolume(1.0f);

        // Objekt zusammen mit dem Soundeffekt in die Hashtabelle einfuegen.
        trackers.put(emitter, tracker);
    }

    public void playSFX(int idx) {
    	if (!enableSoundFx) {
    		return;
    	}
    	AudioSystem.getSystem().getMusicQueue().getTrack(idx).play();
    }
    
    public void playMusic(int idx) {
    	if (!enableMusic) {
    		return;
    	}
    	AudioSystem.getSystem().getMusicQueue().getTrack(idx).play();
    }
    
    public void stopMusic() {
    	AudioSystem.getSystem().getMusicQueue().stop();
    }
    
    /**
     * @return HashTabelle der im Raum Positionierten Sounds.
     */
    public final Hashtable<Spatial, RangedAudioTracker> getTrackers() {
        return trackers;
    }

	public boolean isEnableSoundFx() {
		return enableSoundFx;
	}

	public void setEnableSoundFx(boolean enableSoundFx) {
		this.enableSoundFx = enableSoundFx;
	}

	public boolean isEnableMusic() {
		return enableMusic;
	}

	public void setEnableMusic(boolean enableMusic) {
		if (enableMusic == true) {
			AudioSystem.getSystem().getMusicQueue().getTrack(
                    SoundUtil.BG_SOUND_INGAME).stop();
		}
		this.enableMusic = enableMusic;
	}
}
