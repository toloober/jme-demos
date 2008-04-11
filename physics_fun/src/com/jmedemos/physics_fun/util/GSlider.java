package com.jmedemos.physics_fun.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GSlider extends JLayeredPane
{
    // maximum value for slider
    private int max;
    // minimum value for slider
    private int min;
    //the range of values
    private int currentValue;
    //the slider
    private JLabel sliderLabel;
    // the x offset of the image when the slider is at its minimum.
    private final int XOFF = 20;
    
    /**
     * Creates a slider with range 0 to 100 and initial value 50
     * and no image
     * 
     * @param icon The image icon to use for the slider
     */
    
    public GSlider(ImageIcon icon)
    {
        this.max = 100;
        this.min = 0;
        this.currentValue = 50;
        
        this.sliderLabel = new JLabel(icon);
        if (icon != null) {
            sliderLabel.setPreferredSize(new Dimension(icon.getIconWidth(),icon.getIconHeight()) );
        } 
        else {
            System.err.println("Duke icon not found; using black square instead.");
            sliderLabel.setPreferredSize(new Dimension(50,150));
            sliderLabel.setOpaque(true);
            sliderLabel.setBackground(Color.BLACK);
        }
        setUp();
    }
   
    /**
     * Creates a slider with given parameters
     * 
     * @param max the maximum value
     * @param min the minimum value
     * @param start the starting value (will by mid way if this is out of range)
     * @param icon icon The image icon to use for the slider
     */
    public GSlider(int max, int min, int start, ImageIcon icon)
    {
        this.max = Math.max(max,min);
        this.min = Math.min(max,min);
        
        if( (start >=max) && (start <= min)){
            start = (this.min + this.max)/2;
        }
        this.currentValue = start;
        
        this.sliderLabel = new JLabel(icon);
        if (icon != null) {
            sliderLabel.setPreferredSize(new Dimension(icon.getIconWidth(),icon.getIconHeight()) );
        } 
        else {
            System.err.println("Duke icon not found; using black square instead.");
            sliderLabel.setPreferredSize(new Dimension(50,150));
            sliderLabel.setOpaque(true);
            sliderLabel.setBackground(Color.BLACK);
        }
        setUp();
    }

    /**
     * @return the current value of the slider.
     */
    public int getValue(){
        return this.currentValue;
    }
    
    /*
     * set up the various components used to make this component.
     */
    private void setUp()
    {
        
        this.setPreferredSize(new Dimension(31, 150));
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
        this.setLayout(null);
        this.addMouseListener( new MouseAdapter(){ 
                  public void mousePressed(MouseEvent e) {
                      System.out.println("Press");
                      moveSlider(e);
                  }
                  public void mouseReleased(MouseEvent e) {
                      System.out.println("release");
                      moveSlider(e);
                  }
        });
        this.addMouseMotionListener( new MouseMotionAdapter(){ 
              public void mouseDragged(MouseEvent e) {
                  moveSlider(e);
              }
        });
        JPanel edge = new JPanel();
        edge.setPreferredSize(new Dimension(31, 150));
        Insets insets = edge.getInsets();
        Dimension size = edge.getPreferredSize();
        edge.setBounds(insets.right,insets.bottom,size.width, size.height);
        edge.setOpaque(false);
        edge.setBorder(BorderFactory.createLineBorder(Color.black));
        size = sliderLabel.getPreferredSize();
        sliderLabel.setBounds(2+insets.left,insets.top,size.width, size.height);
        sliderLabel.setLocation(sliderLabel.getX(), XOFF+currentValue);
        this.add(edge,new Integer(1));
        this.add(sliderLabel,new Integer(0));
        this.setVisible(true);
    }
    
    /*
     * move the slider image to represent the current value.
     */
    private void moveSlider(MouseEvent e)
    {
        int xpos = e.getY();
        if(xpos < XOFF)
            xpos = XOFF;
        if(xpos > (this.max+XOFF))
            xpos = this.max+XOFF;
        this.currentValue = xpos-XOFF;
        sliderLabel.setLocation(sliderLabel.getX(), xpos);
    } 
       
}