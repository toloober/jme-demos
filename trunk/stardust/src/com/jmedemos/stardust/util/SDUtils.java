package com.jmedemos.stardust.util;

import com.jme.scene.Node;

public class SDUtils {
    public static boolean containsNode(Node toCheck, Node toFind) {
        if (toCheck == toFind) {
            return true;
        }
        if (toCheck.getParent() != null) {
            return containsNode(toCheck.getParent(), toFind);
        } else {
            return false;
        }
    }
}
