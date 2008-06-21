package com.jmedemos.stardust.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;

import com.jme.scene.Spatial;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmedemos.stardust.core.Game;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.ObjToJme;

/**
 * Utility class to load models.
 */
public final class ModelUtil {

    /**
     * Singleton instance.
     */
    private static ModelUtil instance = null;

    /**
     * HashTable for different models. 
     */
    private Hashtable<String, ByteArrayOutputStream> modelTable = null;

    /**
     * constructor, creates the HashTable.
     */
    private ModelUtil() {
        modelTable = new Hashtable<String, ByteArrayOutputStream>();
    }

    /**
     * converter for obj to jme.
     */
    private FormatConverter converter = new ObjToJme();

    /**
     * returns the singleton instance.
     * @return the singleton instance.
     */
    public static ModelUtil get() {
        if (instance == null) {
            instance = new ModelUtil();
        }
        return instance;
    }

    /**
     * Loads a model, converts it to the jme format and adds it to the hash table.
     * @param modelPath path to the model
     * @param texturePath path to the texture
     * @return reference to the Spatial representing the model.
     */
    public Spatial loadModel(final String modelPath) {
        URL modelUrl = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_MODEL, modelPath);
        
        if (modelPath.endsWith("jme")) {
            // already in internal format
            try {
                return (Spatial) BinaryImporter.getInstance().load(modelUrl);
            } catch (IOException e) {
                e.printStackTrace();
                Game.getInstance().quit();
            }
        }
        
        Spatial model = null;
        ByteArrayOutputStream out = modelTable.get(modelPath);
        if (out == null) {
            // no internal format created yet, convert the model to .jme
            converter.setProperty("mtllib", modelUrl);

            out = new ByteArrayOutputStream();

            // .obj -> jme intern
            try {
                converter.convert(modelUrl.openStream(), out);
            } catch (IOException e) {
                e.printStackTrace();
                Game.getInstance().quit();
            }
            modelTable.put(modelPath, out);
        }

        try {
            // jme intern ---> Spatial
            model = (Spatial) BinaryImporter.getInstance().load(
                    new ByteArrayInputStream(out.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
            Game.getInstance().quit();
        }

        return model;
    }
}
