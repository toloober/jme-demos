package com.jmedemos.stardust.gamestates;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.jme.input.MouseInput;
import com.jme.system.DisplaySystem;
import com.jmedemos.stardust.core.Game;
import com.jmex.awt.swingui.JMEDesktopState;
import com.jmex.game.state.GameStateManager;

/**
 * The Menu GameState. 
 * created with Swing and JMEDesktop.
 * Does not work with MacOS.
 */
public class SwingMenuState extends JMEDesktopState {
    /**
     * Constructor.
     * @param name name of the Gamestate.
     */
    public SwingMenuState(final String name) {
        super(true);
        setName(name);
    }

    /**
     * create the Menu.
     */
    @Override
    protected void buildUI() {
        int width = DisplaySystem.getDisplaySystem().getWidth();
        int height = DisplaySystem.getDisplaySystem().getHeight();
        
        // Substance Look And Feel setzen, damit das Native LAF benutzt wird.
//        try {
//            UIManager.setLookAndFeel(new SubstanceLookAndFeel());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        SwingUtilities.updateComponentTreeUI(getDesktop().getJDesktop());
        
        // create a black panel in the middle of the Screen
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 5));
        panel.setBackground(Color.black);
        panel.setSize(150, 150);
        panel.setLocation(width/2 - panel.getWidth()/2, height/2 - panel.getHeight()/2);
        getDesktop().getJDesktop().add(panel);
        panel.setVisible(true);
        
        JButton start = new JButton("Spiel starten");
        panel.add(start);
        
        JButton settings = new JButton("Einstellungen");
        panel.add(settings);
        
        JButton quit = new JButton("Spiel beenden");
        panel.add(quit);
        
        getDesktop().getJDesktop().revalidate();
        
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GameStateManager.getInstance().deactivateChildNamed("Menu");
                GameStateManager.getInstance().activateChildNamed("InGame");
                MouseInput.get().setCursorVisible(false);
            }
        });
        
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Game.getInstance().quit();
            }
        });
    }
    
    /**
     * Activate / deactivate mouse cursor.
     * @param active active yes/No.
     */
    @Override
    public final void setActive(final boolean active) {
        super.setActive(active);
        if (active == false) {
            MouseInput.get().setCursorVisible(false);
            return;
        }
        MouseInput.get().setCursorVisible(true);
    }
}
