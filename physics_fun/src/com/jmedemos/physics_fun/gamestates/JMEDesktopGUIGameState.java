package com.jmedemos.physics_fun.gamestates;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.input.MouseInput;
import com.jmedemos.physics_fun.core.PhysicsGame;
import com.jmedemos.physics_fun.gui.ScenePanel;
import com.jmedemos.physics_fun.gui.WallPanel;
import com.jmedemos.physics_fun.objects.Swing;
import com.jmedemos.physics_fun.util.JFloatSlider;
import com.jmedemos.physics_fun.util.MaterialType;
import com.jmedemos.physics_fun.util.ObjectFactory;
import com.jmedemos.physics_fun.util.ObjectType;
import com.jmedemos.physics_fun.util.SceneSettings;
import com.jmex.awt.swingui.JMEDesktopState;

/**
 * The jMEDesktop GameState.
 * Creates Swing panels to modify and alter the scene in-game.
 * 
 * @author Christoph Luder
 */
public class JMEDesktopGUIGameState extends JMEDesktopState {
    /** a reference to the main gamestate to access the physics objects */
    private MainGameState main = null;

    /**
	 * Constructs the GUI GameState.
	 * @param name GameStates name.
	 */
	public JMEDesktopGUIGameState(String name, MainGameState main) {
	    super(true);
	    this.main = main;
		setName(name);
		
		// create the Swing menu in the Swing thread
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					createSwingUI();
				};  
			}
		);
	}
	
	/**
	 * sets the look and feel and create the swing GUI.
	 */
	private void createSwingUI() {
		try {
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//			UIManager.setLookAndFeel(new GTKLookAndFeel());
//			UIManager.setLookAndFeel(new WindowsLookAndFeel());
//			UIManager.setLookAndFeel(new SubstanceLookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
			PhysicsGame.get().getGame().finish();
		}
		SwingUtilities.updateComponentTreeUI(getDesktop().getJDesktop());
		
        int h = getDesktop().getJDesktop().getHeight();
		
        // create a tabbed pane to add one tab per phsics object
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		tabbedPane.add("Scene", new ScenePanel(main));
		tabbedPane.addTab("new Objects", createProjectilePanel());
		tabbedPane.add("Wall", new WallPanel(main));
//		tabbedPane.add("Swing", createSwingPanel());
		tabbedPane.add("Wind", createWindPanel());
		
		tabbedPane.setSize(tabbedPane.getPreferredSize());
		tabbedPane.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
		
		tabbedPane.setLocation(20, h - tabbedPane.getHeight()-20);
		getDesktop().getJDesktop().add(tabbedPane);
	}

	/**
	 * creates a JPanel for the Swing object.
	 */
	private JPanel createSwingPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.95f));
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(2,2,2,2);
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridx = 0;
	    gc.gridy = 0;
	    gc.gridwidth = 2;
	    panel.add(new JLabel("Swing Settings"), gc);
	    gc.gridwidth = 1;
	    
        gc.gridy++;
        gc.gridx = 0;
		JButton button = new JButton("Reset Swing");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.getSwing().reset();
			}
		});
		panel.add(button, gc);
        
        gc.gridx = 0;
        gc.gridy++;
        gc.gridwidth = 1;
        panel.add(new JLabel("Joint Spring"), gc);

        gc.gridx = 1;
        gc.gridwidth = 1;
        JFloatSlider sld = new JFloatSlider(0.01f, 100, Swing.DEFAULT_SPRING);
        sld.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                main.getSwing().setSpring(((JFloatSlider)e.getSource()).getFloatValue());
            }
        });
        sld.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.95f));
        sld.setPaintLabels(true);
        sld.setPaintTicks(true);
        panel.add(sld, gc);

        gc.gridx = 0;
        gc.gridy++;
        gc.gridwidth = 1;
        panel.add(new JLabel("Joint Damping"), gc);

        gc.gridx = 1;
        gc.gridwidth = 1;
        sld = new JFloatSlider(0.01f, 50, Swing.DEFAULT_DAMPING);
        sld.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                main.getSwing().setDamping(((JFloatSlider)e.getSource()).getFloatValue());
            }
        });
        sld.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.95f));
        sld.setPaintLabels(true);
        sld.setPaintTicks(true);
        
        panel.add(sld, gc);
        
		return panel;
	}
	
	   /**
     * creates a JPanel for the Swing object.
     */
    private JPanel createWindPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.95f));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2,2,2,2);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        panel.add(new JLabel("Wind Settings"), gc);
        gc.gridwidth = 1;
        
        gc.gridx = 0;
        gc.gridy++;
        gc.gridwidth = 1;
        panel.add(new JLabel("Wind Force"), gc);

        gc.gridx = 1;
        gc.gridwidth = 1;
        JFloatSlider sld = new JFloatSlider(-10f, 10f, SceneSettings.get().getWindForce().x);
        sld.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                main.getWind().getForce().x = ((JFloatSlider)e.getSource()).getFloatValue();
            }
        });
        sld.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        sld.setPaintLabels(true);
        sld.setPaintTicks(true);
        sld.setSnapToTicks(true);
        panel.add(sld, gc);

        gc.gridy ++;
        gc.gridx = 1;
        gc.gridwidth = 1;
        sld = new JFloatSlider(-10f, 10f, SceneSettings.get().getWindForce().y);
        sld.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                main.getWind().getForce().y = ((JFloatSlider)e.getSource()).getFloatValue();
            }
        });
        sld.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        sld.setPaintLabels(true);
        sld.setPaintTicks(true);
        panel.add(sld, gc);
        
        gc.gridy ++;
        gc.gridx = 1;
        gc.gridwidth = 1;
        sld = new JFloatSlider(-10f, 10f, SceneSettings.get().getWindForce().z);
        sld.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                main.getWind().getForce().z = ((JFloatSlider)e.getSource()).getFloatValue();
            }
        });
        sld.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        sld.setPaintLabels(true);
        sld.setPaintTicks(true);
        panel.add(sld, gc);
        
        return panel;
    }
	
	/**
	 * create a panel to modify the projectile settings.
	 * @return JPanel with projectile settings.
	 */
	private JPanel createProjectilePanel() {
	    GridBagConstraints gc = new GridBagConstraints();
	    gc.insets = new Insets(2,2,2,2);
	    JPanel panel = new JPanel(new GridBagLayout());
	    panel.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));

	    gc.gridx = 0;
	    gc.gridy = 0;
	    gc.gridwidth = 3;
	    JLabel lblName = new JLabel("Projectile Settings:");
	    panel.add(lblName, gc);
	    
	    gc.fill = GridBagConstraints.HORIZONTAL;
	    gc.gridheight = 1;
	    gc.gridx = 0;
	    gc.gridy++;
	    gc.gridwidth = 1;
	    panel.add(new JLabel("Force: "), gc);
	    
	    gc.gridx = 1;
	    gc.gridwidth = 2;
	    JSlider sldForce = new JSlider(0, 50000, (int)ObjectFactory.get().getForce());
	    sldForce.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	            ObjectFactory.get().setForce(((JSlider)e.getSource()).getValue());
	        }
	    });
	    sldForce.setPaintLabels(true);
	    sldForce.setPaintTicks(true);
	    sldForce.setMajorTickSpacing(10000);
	    sldForce.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
	    panel.add(sldForce, gc);
	    
        gc.gridx = 0;
        gc.gridy++;
        gc.gridwidth = 1;
        panel.add(new JLabel("Scale"), gc);

        gc.gridx = 1;
        gc.gridwidth = 2;
        JFloatSlider sldScale = new JFloatSlider(0.01f, 4, ObjectFactory.get().getScale());
        sldScale.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ObjectFactory.get().setScale(((JFloatSlider)e.getSource()).getFloatValue());
            }
        });
        sldScale.setPaintLabels(true);
        sldScale.setPaintTicks(true);
        sldScale.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        panel.add(sldScale, gc);

        gc.gridwidth = 1;
        gc.gridx = 0;
        gc.gridy++;
        panel.add(new JLabel("Shape: "), gc);
        
        gc.gridx = 1;
        gc.gridwidth = 1;

        JComboBox comboObjectType = new JComboBox(ObjectType.values());
        comboObjectType.setSelectedItem(ObjectFactory.get().getObjectType());
        comboObjectType.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		ObjectFactory.get().setType((ObjectType)((JComboBox)e.getSource()).getSelectedItem());
        	}
        });
        comboObjectType.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        panel.add(comboObjectType, gc);
	    
        gc.gridwidth = 1;
        gc.gridx = 0;
        gc.gridy++;
        panel.add(new JLabel("Material: "), gc);
        
        gc.gridx = 1;
        gc.gridwidth = 1;
        
        JComboBox comboMaterial = new JComboBox(MaterialType.values());
        comboMaterial.setSelectedItem(MaterialType.DEFAULT);
        comboMaterial.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		ObjectFactory.get().setMaterial((MaterialType)((JComboBox)e.getSource()).getSelectedItem());
        	}
        });
        comboMaterial.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        panel.add(comboMaterial, gc);
     
        gc.gridy ++;
        gc.gridx = 1;
        
        JButton button = new JButton("remove created objects");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ObjectFactory.get().removeAllPhysicObjects(main.getBallNode());
            }
        });
        panel.add(button, gc);
        
		// set the panels size and location
        // bottom left corner
		panel.setSize(panel.getPreferredSize());
        
        return panel;
	}
	
	/**
	 * Activate / deactivate this GameState.
	 * Make the Mousecursor visible when this GameState is active.
	 */
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		if (active) {
			MouseInput.get().setCursorVisible(true);
		} else {
			MouseInput.get().setCursorVisible(false);
		}
	}
}
