package com.jmedemos.stardust.scene;

import java.util.Random;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Point;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;

/**
 * represents the stardust in space.
 * http://www.jmonkeyengine.com/wiki/doku.php?id=stardust
 */
@SuppressWarnings("serial")
public class StarDust extends Node {
    private DisplaySystem display = DisplaySystem.getDisplaySystem();
    private int blockSize;
    private Point[][][] points;
    private int oldSecX;
    private int oldSecY;
    private int oldSecZ;

    /**
     * @param name node name
     * @param numStars number of particles
     * @param blockSize sice of a block
     * @param randomizeSize truefalse
     */
    public StarDust(final String name, final int numStars, final int blockSize,
            final boolean randomizeSize) {
        super(name);

        this.blockSize = blockSize;
        points = new Point[3][3][3];

        setIsCollidable(false);

        // A star field
        //
        // in this first edition, just use the standard 'point' class
        // but in future would like to have a custom drawn one - where intensity
        // is related to distance?
        Random r = new Random();

        Vector3f[] vertexes = new Vector3f[numStars];
        for (int x = 0; x < numStars; ++x) {
            vertexes[x] = new Vector3f((r.nextFloat()) * blockSize, (r
                    .nextFloat())
                    * blockSize, (r.nextFloat()) * blockSize);
        }

        // all dust particles are white
        MaterialState ms = display.getRenderer().createMaterialState();

        ms.setEmissive(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
        ms.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 0.5f));
        ms.setEnabled(true);

        BlendState as = display.getRenderer().createBlendState();
        as.setBlendEnabled(true);
        as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as.setDestinationFunction(BlendState.DestinationFunction.One);
        as.setTestEnabled(false);
        as.setEnabled(true);

        for (int k = 0; k < 3; ++k) {
            for (int j = 0; j < 3; ++j) {
                for (int i = 0; i < 3; ++i) {
                    points[i][j][k] = new Point("stardust " + i + "" + j + ""
                            + k, vertexes, null, null, null);
                    points[i][j][k].setAntialiased(true);
                    if (randomizeSize) {
                        points[i][j][k].setPointSize((float) Math.random() * 10f);
                    }
                    points[i][j][k].setLocalTranslation(new Vector3f((i - 1)
                            * blockSize, (j - 1) * blockSize, (k - 1)
                            * blockSize));
                    points[i][j][k].setModelBound(new BoundingBox());
                    points[i][j][k].updateModelBound();
                    attachChild(points[i][j][k]);
                }
            }
        }

        updateWorldBound();
        setRenderState(ms);
        setRenderState(as);
        updateRenderState();
    }

    /**
     * ensure the viewer is surrounded by stars.
     * @param viewer location of the viewver
     */
    public final void update(final Vector3f viewer) {
        // note: funny things happen when scaling things about the origin,
        // so for our purposes we compensate. (we could have used -0.5..0.5)
        // what we want is: -1000..0 -> -1
        // 0..1000 -> 0
        // 1000..2000 -> 1
        int secX = (int) ((viewer.x / blockSize) + ((viewer.x > 0) ? 0 : -1));
        int secY = (int) ((viewer.y / blockSize) + ((viewer.y > 0) ? 0 : -1));
        int secZ = (int) ((viewer.z / blockSize) + ((viewer.z > 0) ? 0 : -1));

        // reduce garbage collection...
        if ((secX != oldSecX) || (secY != oldSecY) || (secZ != oldSecZ)) {
            getLocalTranslation().set(secX * blockSize, secY * blockSize,
                    secZ * blockSize);
            oldSecX = secX;
            oldSecY = secY;
            oldSecZ = secZ;
        }
    }
}
