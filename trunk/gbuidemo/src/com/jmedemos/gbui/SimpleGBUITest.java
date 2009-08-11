package com.jmedemos.gbui;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.MouseInput;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.system.DisplaySystem;
import com.jmex.bui.BButton;
import com.jmex.bui.BDecoratedWindow;
import com.jmex.bui.BLabel;
import com.jmex.bui.BSlider;
import com.jmex.bui.BWindow;
import com.jmex.bui.BuiSystem;
import com.jmex.bui.PolledRootNode;
import com.jmex.bui.enumeratedConstants.Orientation;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.event.ChangeEvent;
import com.jmex.bui.event.ChangeListener;
import com.jmex.bui.layout.TableLayout;

public class SimpleGBUITest extends SimpleGame {
	/** the box which will be altered by the GUI controls */
	private Box box;

	/**
	 * Initialize GBUI, create the GUI and the Scene.
	 */
	@Override
	protected void simpleInitGame() {
		// remove SimpleGames input handling
		input = new InputHandler();
		KeyBindingManager.getKeyBindingManager().removeAll();
		
        // we don't hide the cursor
        MouseInput.get().setCursorVisible(true);
        
        // create a scene to play with
		createScene();
		
		// init GBUI
		BuiSystem.init(new PolledRootNode(timer, input), "/rsrc/style2.bss");
		
		// create the GUI
		createGUI();
	}

	/**
	 * creates the GUI.
	 * - a Button to close the application
	 * - a slider to change the Box's size
	 */
	private void createGUI() {
		BWindow window = new BDecoratedWindow(BuiSystem.getStyle(), null);
		TableLayout layout = new TableLayout(1, 10, 0);
		layout.setEqualRows(true);
		layout.setHorizontalAlignment(TableLayout.STRETCH);
        window.setLayoutManager(layout);
        BuiSystem.addWindow(window);
        window.setSize(250, display.getHeight());
        
        // the exit Button, calls BaseGame.quit() when executed
        BButton btnExit = new BButton("Exit");
        btnExit.addListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent event) {
        		quit();
        	}			
		});
        window.add(btnExit);

        // the slider changes the box's local scale
        window.add(new BLabel("LocalScale slider:"));
        final BSlider boxSlider = new BSlider(Orientation.HORIZONTAL, 1, 500, 100);
        boxSlider.getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				box.setLocalScale(boxSlider.getModel().getValue()/(float)100);
			}
		});
        window.add(boxSlider);
	}	

	/**
	 * simply creates a Box.
	 */
	private void createScene() {
		box = new Box("b", new Vector3f(), 1, 1,1 );
		box.setModelBound(new BoundingBox());
		box.updateModelBound();
		rootNode.attachChild(box);
	}

	@Override
	protected void simpleUpdate() {
		// the GBUI's node is not attached to the scene, we update it manually
		BuiSystem.getRootNode().updateGeometricState(tpf, true);
	}
	
	@Override
	protected void simpleRender() {
		// the GBUI's node is not attached to the scene, we render it manually
		DisplaySystem.getDisplaySystem().getRenderer().draw(BuiSystem.getRootNode());
	}
	
	/**
	 * Entry point
	 */
	public static void main(String[] args) {
		SimpleGBUITest game = new SimpleGBUITest();
		game.setConfigShowMode(ConfigShowMode.AlwaysShow);
		game.start();
	}
}
