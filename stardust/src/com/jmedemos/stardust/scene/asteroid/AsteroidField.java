package com.jmedemos.stardust.scene.asteroid;

import com.jme.math.Vector3f;
import com.jme.scene.Node;

public class AsteroidField {
    private Node field = null;
    private int gap = 2000;
    private int var = 1000;
    private int rot = 2000;
    
    public AsteroidField(AsteroidFactory factory, Vector3f pos,
            int width, int length, int heigth) {
        field = new Node("AsteroidField");
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                for (int k = 0; k < heigth; k++) {
                    Asteroid asteroid = factory.createRotatingAsteroid("asteroid.obj", 15.0f, 
                            //position
                            new Vector3f(pos.x+i*gap+(float)Math.random()*var*(Math.random()<0.5?-1:1),
                                         pos.y+j*gap+(float)Math.random()*var*(Math.random()<0.5?-1:1),
                                         pos.z+k*gap+(float)Math.random()*var*(Math.random()<0.5?-1:1)),
                            // rotation
                            new Vector3f((float)Math.random()*rot*(Math.random()<0.5?-1:1),
                                         (float)Math.random()*rot*(Math.random()<0.5?-1:1),
                                         (float)Math.random()*rot*(Math.random()<0.5?-1:1)));
                    field.attachChild(asteroid.getNode());
                }
            }
        }
    }

    public Node getField() {
        return field;
    }
}
