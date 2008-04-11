package com.jmedemos.physics_fun.util;

import com.jmex.physics.material.Material;

public enum MaterialType {
    IRON (Material.IRON),
    WOOD (Material.WOOD),
    CONCRETE (Material.CONCRETE),
    GRANITE (Material.GRANITE),
    GLASS (Material.GLASS),
    PLASTIC (Material.PLASTIC),
    RUBBER (Material.RUBBER),
    ICE (Material.ICE),
    DEFAULT (Material.DEFAULT),
    GHOST (Material.GHOST),
    OSMIUM (Material.OSMIUM),
    SPONGE(Material.SPONGE);
    
    private Material material;
    
    MaterialType(Material mat) {
        material = mat;
    }
    
    public Material getMaterial() {
        return material;
    }
}