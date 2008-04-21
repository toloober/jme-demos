package com.jmedemos.physics_fun.util;

import java.awt.Font;
import java.text.NumberFormat;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

/**
 * Creates a JSlider for float values.
 * @author Christoph Luder
 */
public class JFloatSlider extends JSlider {
    private static final long serialVersionUID = 1L;
    private static int SCALE = 100;
    
    /**
     * Construct the float Slider
     * @param min min value
     * @param max max value
     * @param init initial value of the slider
     */
    public JFloatSlider(float min, float max, float init) {
        this(SwingConstants.HORIZONTAL, min, max, init);
    }
    
    /**
     * Construct the float Slider
     * @param position SwingConstants.HORIZONTAL/ SwingConstants.VERTICAL
     * @param min min value
     * @param max max value
     * @param init initial value of the slider
     */
    public JFloatSlider(int position, float min, float max, float init) {
    	super((int)(min*SCALE), (int)(max*SCALE), (int)(init*SCALE));
        int iMin = (int)(min*SCALE);
        int iMax = (int)(max*SCALE);
        int iQuater = (iMax-iMin)/4;
        float quater = (max-min)/4;
        
        setPaintLabels(true);
        setSnapToTicks(false);
        setFont(new Font("Arial", Font.PLAIN, 8));
        
        NumberFormat fmt = NumberFormat.getNumberInstance();
        fmt.setMaximumFractionDigits(2);
        fmt.setMinimumFractionDigits(1);
        
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(iMin), new JLabel(""+min));
        labelTable.put(new Integer(iMin + iQuater), new JLabel(""+ fmt.format(min + quater)));
        labelTable.put(new Integer(iMin + iQuater*2), new JLabel(""+ fmt.format(min + quater*2)));
        labelTable.put(new Integer(iMin + iQuater*3), new JLabel(""+ fmt.format(min + quater*3)));
        labelTable.put(new Integer(iMax), new JLabel(""+max));
        setLabelTable(labelTable);
    }
    
    /**
     * Get the Float Value.
     * @return the float value.
     */
    public float getFloatValue() {
        return (float)getValue()/(float)SCALE; 
    }
}
