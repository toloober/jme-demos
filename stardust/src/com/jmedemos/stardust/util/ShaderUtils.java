package com.jmedemos.stardust.util;

import java.net.URL;

import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.system.DisplaySystem;
import com.jme.util.resource.ResourceLocatorTool;

public class ShaderUtils {
    private static GLSLShaderObjectsState spaceship, atmosphere, planet;
    
//    private static class SpaceshipDataLogic implements GLSLShaderDataLogic {
//        private boolean ranOnce = false;
//        public void applyData(GLSLShaderObjectsState shader, GeomBatch batch) {
//            AbstractCamera cam = (AbstractCamera)DisplaySystem.getDisplaySystem().getRenderer().getCamera();
//
//            //Vector3f pos =r.getCamera().getDirection();
//            //shader.setUniform("EyePos", pos);
//
//            if (!ranOnce) {
//                shader.setAttributePointer("rm_Tangent", 3, false, 0, batch.getTangentBuffer());
//                shader.setAttributePointer("rm_Binormal", 3, false, 0, batch.getBinormalBuffer());
//                ranOnce = true;
//            }
//        }
//    }
    
    public static GLSLShaderObjectsState getPlanetShader(){
        if (planet != null) {
            return null;
        }
        GLSLShaderObjectsState glsl = DisplaySystem.getDisplaySystem().getRenderer().createGLSLShaderObjectsState();
        
        URL vert = ResourceLocatorTool.locateResource(
                    ResourceLocatorTool.TYPE_SHADER,
                    "planet_nocloud.vert");
        URL frag = ResourceLocatorTool.locateResource(
                    ResourceLocatorTool.TYPE_SHADER,
                    "planet_nocloud.frag");

        glsl.load(vert,frag);

        glsl.setUniform("baseMap", 0);
        glsl.setUniform("normalMap", 1);
        glsl.setUniform("specMap", 2);
        //glsl.setUniform("cloudsMap", 3);
        
        System.out.println("Loaded planet shaders");
        
        return glsl;
    }
    
    public static GLSLShaderObjectsState getAtmosphereShader(){
        if (atmosphere != null) {
            return atmosphere;
        }
        GLSLShaderObjectsState glsl = DisplaySystem.getDisplaySystem().getRenderer().createGLSLShaderObjectsState();
        
        URL vert = ResourceLocatorTool.locateResource(
                    ResourceLocatorTool.TYPE_SHADER,
                    "atmosphere_vx.vert");
        URL frag = ResourceLocatorTool.locateResource(
                    ResourceLocatorTool.TYPE_SHADER,
                    "atmosphere_vx.frag");
        
        //System.out.println("Loading: "+frag);
        
        glsl.load(vert,frag);
        System.out.println("Loaded atmosphere shaders");
        
        return glsl;
    }
    
//    public static GLSLShaderObjectsState getSpaceshipShader(){
//        if (spaceship != null) {
//            return spaceship;
//        }
//        GLSLShaderObjectsState glsl = DisplaySystem.getDisplaySystem().getRenderer().createGLSLShaderObjectsState();
//        glsl.setShaderDataLogic(new SpaceshipDataLogic());
//
//        URL vert = ResourceLocatorTool.locateResource(
//                    ResourceLocatorTool.TYPE_SHADER,
//                    "spaceship.vert");
//        URL frag = ResourceLocatorTool.locateResource(
//                    ResourceLocatorTool.TYPE_SHADER,
//                    "spaceship.frag");
//        
//        System.out.println("Loading: "+vert);
//        System.out.println("Loading: "+frag);
//        
//        glsl.load(vert,frag);
//
//        glsl.setUniform("ColorMap", 0);
//        glsl.setUniform("NormalMap", 1);
//        glsl.setUniform("OcclusionMap", 2);
//
//        System.out.println("Loaded spacecraft shaders");
//        
//        //glsl.setUniform("LightPos", 25f, 25f, 10f);
//
//        return glsl;
//    }
}
