package explore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Gui extends JFrame {
    private Controller controller;

    public Gui (Controller controller){

        this.controller = controller;
        this.addKeyListener(new keyHandler());
        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e) {
                controller.stopped.set(true);
            }
        });

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //frame.setUndecorated(true);
        this.setSize(800, 800);
        this.setLocationRelativeTo(null);
        this.add(controller.getViewPanel(), BorderLayout.CENTER);
    }

    private class keyHandler implements KeyListener {

        @Override
        public synchronized void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()){
                case KeyEvent.VK_RIGHT:
                    controller.tickOne();
                    break;
                //case KeyEvent.VK_LEFT:
                //    break;
                case KeyEvent.VK_SPACE:
                    controller.pause();
                    break;
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public synchronized void keyReleased(KeyEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
