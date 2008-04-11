package com.jmedemos.physics_fun.util;

import java.awt.Font;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class JFloatSlider extends JSlider {
    private static final long serialVersionUID = 1L;
    private static int SCALE = 100;
    
    public JFloatSlider(float min, float max, float init) {
        this(SwingConstants.HORIZONTAL, min, max, init);
    }
    
    public JFloatSlider(int position, float min, float max, float init) {
    	super((int)(min*SCALE), (int)(max*SCALE), (int)(init*SCALE));
        int iMin = (int)(min*SCALE);
        int iMax = (int)(max*SCALE);
        int iQuater = (iMax-iMin)/4;
        float quater = (max-min)/4;
        
        setPaintLabels(true);
        setSnapToTicks(false);
        setFont(new Font("Arial", Font.PLAIN, 8));
        
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(iMin), new JLabel(""+min));
        labelTable.put(new Integer(iMin + iQuater), new JLabel(""+ (min + quater)));
        labelTable.put(new Integer(iMin + iQuater*2), new JLabel(""+ (min + quater*2)));
        labelTable.put(new Integer(iMin + iQuater*3), new JLabel(""+ (min + quater*3)));
        labelTable.put(new Integer(iMax), new JLabel(""+max));
        setLabelTable(labelTable);
    }
    
    public float getFloatValue() {
        return (float)getValue()/(float)SCALE; 
    }
}
