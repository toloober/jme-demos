package com.jmedemos.physics_fun.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.math.Vector3f;
import com.jmedemos.physics_fun.gamestates.MainGameState;
import com.jmedemos.physics_fun.util.JFloatSlider;
import com.jmedemos.physics_fun.util.ObjectFactory;

/**
 * A JPanel used in JMEdesktopGUIGamestate, it contains elements
 * to set different attributes of the Scene.
 *  
 * @author Christoph Luder
 */
@SuppressWarnings("serial")
public class ScenePanel extends JPanel {
    private Logger log = Logger.getLogger(ScenePanel.class.getName());
    
    /**
     * the constructor which creates the JPanel.
     * @param main a reference to the MainGamestate
     */
    public ScenePanel(final MainGameState main) {
        super(new GridBagLayout());
        
        if (main == null) {
            log.severe("MainGameState not yet created!");
            return;
        }
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2,2,2,2);
        setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 3;
        final JLabel lbl = new JLabel("Scene Settings");
        add(lbl, gc);
        gc.gridwidth = 1;
        
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 4;
        gc.gridy = 1;
        add(new JLabel("Gravity"), gc);
        
        gc.gridy = 2;
        gc.gridheight = 10;
        gc.fill = GridBagConstraints.VERTICAL;
        final JSlider sldGravity = new JFloatSlider(-10, 10, main.getPhysicsSpace().getDirectionalGravity(null).y);
        sldGravity.setPaintTicks(true);
        sldGravity.setOrientation(JSlider.VERTICAL);
        sldGravity.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                main.getPhysicsSpace().setDirectionalGravity(new Vector3f(0,
                        ((JFloatSlider)e.getSource()).getFloatValue(), 0));
            }
        });
        sldGravity.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        add(sldGravity, gc);
        
        gc.gridy = 13;
        JButton zero= new JButton("zero gravity");
        zero.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                main.getPhysicsSpace().setDirectionalGravity(new Vector3f(0, 0, 0));
                sldGravity.setValue(0);
            }
        });
        add(zero, gc);
        
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridheight = 1;
        gc.gridx = 0;
        gc.gridy = 1;
        add(new JLabel("Auto-disable"), gc);
        
        gc.gridx = 1;
        final JTextField txtDisable = new JTextField(String.valueOf(0));
        add(txtDisable, gc);
        
        gc.gridy++;
        gc.gridx = 0;
        JButton applyDisable = new JButton("apply");
        applyDisable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.info("setting new auto-disable treshold: " 
                        +Float.parseFloat(txtDisable.getText()));
                main.getPhysicsSpace().setAutoRestThreshold(
                        Float.parseFloat(txtDisable.getText()));
            }
        });
        add(applyDisable, gc);
        
//        gc.gridy++;
        gc.gridx = 1;
        JButton unrest = new JButton("unrest");
        unrest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ObjectFactory.get().unrestAll(main.getRootNode());
            }
        });
        add(unrest, gc);
        
      
        gc.gridy++;
        gc.gridx = 0;
        
        JCheckBox checkBox = new JCheckBox("show physics debug");
        checkBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                switch(e.getStateChange()) {
                case ItemEvent.SELECTED:
                    main.setShowPhysics(true);
                    break;
                case ItemEvent.DESELECTED:
                    main.setShowPhysics(false);
                    break;
                }
            }
        });
        add(checkBox, gc);
        
        gc.gridy++;
        gc.gridx = 0;
        
        checkBox = new JCheckBox("enable physics picker");
        checkBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                switch(e.getStateChange()) {
                case ItemEvent.SELECTED:
                    main.getPicker().getInputHandler().setEnabled(true);
                    break;
                case ItemEvent.DESELECTED:
                    main.getPicker().getInputHandler().setEnabled(false);
                    break;
                }
            }
        });
        add(checkBox, gc);
        
        
        gc.gridy++;
        gc.gridx = 0;
        
        checkBox = new JCheckBox("show Bounds");
        checkBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                switch(e.getStateChange()) {
                case ItemEvent.SELECTED:
                    main.setShowBounds(true);
                    break;
                case ItemEvent.DESELECTED:
                    main.setShowBounds(false);
                    break;
                }
            }
        });
        add(checkBox, gc);
        
        // set the panels size and location
        // bottom right corner
        setSize(getPreferredSize());
    }
}
