package com.jmedemos.physics_fun.gamestates;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.input.MouseInput;
import com.jme.math.Vector3f;
import com.jmedemos.physics_fun.core.PhysicsGame;
import com.jmedemos.physics_fun.objects.Swing;
import com.jmedemos.physics_fun.objects.Wall;
import com.jmedemos.physics_fun.util.JFloatSlider;
import com.jmedemos.physics_fun.util.MaterialType;
import com.jmedemos.physics_fun.util.ObjectFactory;
import com.jmedemos.physics_fun.util.ObjectType;
import com.jmedemos.physics_fun.util.SceneSettings;
import com.jmex.awt.swingui.JMEDesktopState;

/**
 * The jMEDesktop Gamestate.
 * Creates Swing panels to modify and alter the scene in-game.
 * 
 * @author Christoph Luder
 */
public class JMEDesktopGUIGameState extends JMEDesktopState {
    private Logger log = Logger.getLogger(this.getClass().getName());
    private MainGameState main = null;
    private JTextField txtWallWidth = null;
    private JTextField txtWallHeigth = null;
    private JTextField txtDisable = null;
	private JPanel panel = null;
	
	/**
	 * Constructs the GUI GameState.
	 * @param name GameStates name.
	 */
	public JMEDesktopGUIGameState(String name, MainGameState main) {
	    super(true);
	    this.main = main;
		setName(name);

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
		
		int w = getDesktop().getJDesktop().getWidth();
        int h = getDesktop().getJDesktop().getHeight();
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		tabbedPane.add("Scene", createScenePanel());
		tabbedPane.addTab("new Objects", createProjectilePanel());
		tabbedPane.add("Swing", createSwingPanel());
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
        
        gc.gridx = 0;
        gc.gridy++;
        gc.gridwidth = 1;
        panel.add(new JLabel("Wind Variation"), gc);

        gc.gridx = 1;
        gc.gridwidth = 1;
        sld = new JFloatSlider(0f, 5, SceneSettings.get().getWindVariation().x);
        sld.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                main.getWind().getVariation().x = ((JFloatSlider)e.getSource()).getFloatValue();
            }
        });
        sld.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        sld.setPaintLabels(true);
        sld.setPaintTicks(true);
        panel.add(sld, gc);

        gc.gridy ++;
        gc.gridx = 1;
        gc.gridwidth = 1;
        sld = new JFloatSlider(-0f, 5f, SceneSettings.get().getWindVariation().y);
        sld.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                main.getWind().getVariation().y = ((JFloatSlider)e.getSource()).getFloatValue();
            }
        });
        sld.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        sld.setPaintLabels(true);
        sld.setPaintTicks(true);
        panel.add(sld, gc);
        
        gc.gridy ++;
        gc.gridx = 1;
        gc.gridwidth = 1;
        sld = new JFloatSlider(0f, 5f, SceneSettings.get().getWindVariation().z);
        sld.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                main.getWind().getVariation().z = ((JFloatSlider)e.getSource()).getFloatValue();
            }
        });
        sld.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        sld.setPaintLabels(true);
        sld.setPaintTicks(true);
        panel.add(sld, gc);
        
        panel.add(sld, gc);
        
        return panel;
    }
	
	/**
	 * create a panel to modify the scene.
	 * @return JPanel 
	 */
	private JPanel createScenePanel() {
	    GridBagConstraints gc = new GridBagConstraints();
	    panel = new JPanel(new GridBagLayout());
	    panel.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
	    
	    gc.gridx = 0;
	    gc.gridy = 0;
	    gc.gridwidth = 3;
	    panel.add(new JLabel("Scene Settings"), gc);
	    gc.gridwidth = 1;
	    
	    gc.fill = GridBagConstraints.HORIZONTAL;
	    gc.gridx = 4;
        gc.gridy = 1;
        panel.add(new JLabel("Gravity"), gc);
        
        gc.gridy = 2;
        gc.gridheight = 10;
        gc.fill = GridBagConstraints.VERTICAL;
        JSlider sldGravity = new JFloatSlider(-10, 10, main.getPhysicsSpace().getDirectionalGravity(null).y);
        sldGravity.setPaintTicks(true);
        sldGravity.setOrientation(JSlider.VERTICAL);
        sldGravity.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                main.getPhysicsSpace().setDirectionalGravity(new Vector3f(0,
                        ((JFloatSlider)e.getSource()).getFloatValue(), 0));
            }
        });
        sldGravity.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        panel.add(sldGravity, gc);
	    
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridheight = 1;
		gc.gridx = 0;
        gc.gridy = 1;
        panel.add(new JLabel("Auto-disable"), gc);
        
        gc.gridx = 1;
        txtDisable = new JTextField(String.valueOf(0));
        panel.add(txtDisable, gc);
        
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
        panel.add(applyDisable, gc);
        
//        gc.gridy++;
        gc.gridx = 1;
        JButton unrest = new JButton("unrest");
        unrest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ObjectFactory.get().unrestAll(main.getRootNode());
            }
        });
        panel.add(unrest, gc);
        
        gc.gridx = 0;
        gc.gridy++;
        panel.add(new JLabel("Wall width"), gc);
        gc.gridx = 1;
        txtWallWidth = new JTextField(String.valueOf(SceneSettings.get().getWallWidth()));
        panel.add(txtWallWidth, gc);
        
        gc.gridx = 0;
        gc.gridy++;
        panel.add(new JLabel("Wall heigth"), gc);
        gc.gridx = 1;
        txtWallHeigth = new JTextField(String.valueOf(SceneSettings.get().getWallHeigth()));
        panel.add(txtWallHeigth, gc);
        
        gc.gridx = 0;
        gc.gridy++;
        panel.add(new JLabel("Wall Material: "), gc);
        
        gc.gridx = 1;
        JComboBox comboMaterial = new JComboBox(MaterialType.values());
        comboMaterial.setSelectedItem(MaterialType.WOOD);
        comboMaterial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SceneSettings.get().setWallMaterial((MaterialType)((JComboBox)e.getSource()).getSelectedItem());
            }
        });
        comboMaterial.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
        panel.add(comboMaterial, gc);
        
        
        gc.gridy++;
        gc.gridx = 0;
		JButton button = new JButton("Reset Wall");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.getWall().reset();
			}
		});
		panel.add(button, gc);
		
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
        panel.add(button, gc);
		
        gc.gridy ++;
        gc.gridx = 0;
        
		button = new JButton("remove projectiles");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ObjectFactory.get().removeAllPhysicObjects(main.getBallNode());
			}
		});
		panel.add(button, gc);
	    
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
		panel.add(checkBox, gc);
		
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
        panel.add(checkBox, gc);
		
//        gc.gridy++;
//        gc.gridwidth = 1;
//        gc.gridx = 2;
//        
//        checkBox = new JCheckBox("enable shadow and reflection");
//        checkBox.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent e) {
//                switch(e.getStateChange()) {
//                case ItemEvent.SELECTED:
//                    GameStateManager.getInstance().getChild("pass").setActive(true);
//                    break;
//                case ItemEvent.DESELECTED:
//                    GameStateManager.getInstance().getChild("pass").setActive(false);
//                    break;
//                }
//            }
//        });
//        panel.add(checkBox, gc);
		
		// set the panels size and location
		// bottom right corner
		panel.setSize(panel.getPreferredSize());
		
		
		return panel;
	}
	
	/**
	 * create a panel to modify the projectile settings.
	 * @return JPanel with projectile settings.
	 */
	private JPanel createProjectilePanel() {
	    GridBagConstraints gc = new GridBagConstraints();
	    panel = new JPanel(new GridBagLayout());
	    panel.setBackground(new Color(0.5f, 0.5f, 0.5f, 0.75f));
//	    panel.setLocation(10, h-panel.getHeight()-50);

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
     
		// set the panels size and location
        // bottom left corner
		panel.setSize(panel.getPreferredSize());
        
        return panel;
	}
	
	/**
	 * Activate / deactivate this GameState.
	 */
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		if (active) {
			panel.setFocusable(true);
			getDesktop().getJDesktop().validate();
		    getInputHandler().setEnabled(true);
			MouseInput.get().setCursorVisible(true);
		} else {
			panel.setFocusable(false);
		    getInputHandler().setEnabled(false);
			MouseInput.get().setCursorVisible(false);
		}
	}
	
	@Override
	public void update(float tpf) {
	    if (getInputHandler().isEnabled())
	        getInputHandler().update(tpf);
        
	    getGUINode().updateGeometricState(tpf, true);
	}
}