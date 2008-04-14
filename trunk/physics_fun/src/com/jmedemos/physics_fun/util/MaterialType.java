package com.jmedemos.physics_fun.util;

import com.jmex.physics.material.Material;

public enum MaterialType {
    CONCRETE (Material.CONCRETE),
    DEFAULT (Material.DEFAULT),
    GHOST (Material.GHOST),
    GLASS (Material.GLASS),
    GRANITE (Material.GRANITE),
    ICE (Material.ICE),
    IRON (Material.IRON),
    OSMIUM (Material.OSMIUM),
    PLASTIC (Material.PLASTIC),
    RUBBER (Material.RUBBER),
    SPONGE(Material.SPONGE),
    WOOD (Material.WOOD);
    
    private Material material;
    
    MaterialType(Material mat) {
        material = mat;
    }
    
    public Material getMaterial() {
        return material;
    }
}