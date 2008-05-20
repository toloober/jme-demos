package com.jmedemos.physics_fun.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jmedemos.physics_fun.gamestates.MainGameState;
import com.jmedemos.physics_fun.objects.Wall;
import com.jmedemos.physics_fun.util.MaterialType;
import com.jmedemos.physics_fun.util.SceneSettings;

/**
 * A JPanel used in JMEdesktopGUIGamestate, it contains elements
 * to set different attributes of the Wall.
 *  
 * @author Christoph Luder
 */
@SuppressWarnings("serial")
public class WallPanel extends JPanel {
    private Logger log = Logger.getLogger(WallPanel.class.getName());
    private JTextField txtWallWidth = null;
    private JTextField txtWallHeigth = null;
    
    /**
     * Constructor for the JPanel, a reference to the (already created)
     * @param main reference to the already created MainGamestate.
     */
    public WallPanel(final MainGameState main) {
        super(new GridBagLayout());
        
        if (main == null) {
            log.severe("MainGameState not yet created!");
            return;
        }
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2,2,2,2);
        gc.fill = GridBagConstraints.HORIZONTAL;
        this.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        
        gc.gridx = 0;
        gc.gridy++;
        this.add(new JLabel("Wall width"), gc);
        gc.gridx = 1;
        txtWallWidth = new JTextField(String.valueOf(SceneSettings.get().getWallWidth()));
        this.add(txtWallWidth, gc);
        
        gc.gridx = 0;
        gc.gridy++;
        this.add(new JLabel("Wall heigth"), gc);
        gc.gridx = 1;
        txtWallHeigth = new JTextField(String.valueOf(SceneSettings.get().getWallHeigth()));
        this.add(txtWallHeigth, gc);
        
        gc.gridx = 0;
        gc.gridy++;
        this.add(new JLabel("Wall Material: "), gc);
        
        gc.gridx = 1;
        JComboBox comboMaterial = new JComboBox(MaterialType.values());
        comboMaterial.setSelectedItem(MaterialType.WOOD);
        comboMaterial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SceneSettings.get().setWallMaterial((MaterialType)((JComboBox)e.getSource()).getSelectedItem());
            }
        });
        comboMaterial.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        this.add(comboMaterial, gc);
        
        
        gc.gridy++;
        gc.gridx = 0;
        JButton button = new JButton("Reset Wall");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                main.getWall().reset();
                
            }
        });
        this.add(button, gc);
        
        gc.gridx = 0;
        gc.gridy ++;
        button = new JButton("Recreate Wall");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SceneSettings.get().setWallWidth(Integer.parseInt(txtWallWidth.getText().trim()));
                SceneSettings.get().setWallHeigth(Integer.parseInt(txtWallHeigth.getText().trim()));
                
                main.getWall().delete();
                main.getWall().removeFromParent();
                Wall w = new Wall(main.getPhysicsSpace(),
                        SceneSettings.get().getWallWidth(),
                        SceneSettings.get().getWallHeigth(),
                        SceneSettings.get().getWallElementSize());
                w.setLocalTranslation(0, 0, -5);
                main.setWall(w);
                main.getRootNode().attachChild(w);
                main.getRootNode().updateGeometricState(0, true);
                main.getRootNode().updateRenderState();
            }
        });
        this.add(button, gc);
    }
}
