package explore;

import org.graphstream.ui.swingViewer.ViewPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Gui extends JFrame {
    private Controller controller;
    private JSplitPane split;
    private ViewPanel graphViewPanel;

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
        //this.add(new JComboBox<String>());
        JPanel settingsPanel = new JPanel();
        JLabel lblNumberOfRobots = new JLabel();
        lblNumberOfRobots.setText("Robotok száma");
        JTextField txtNumberOfRobots = new JTextField("2",4);
        settingsPanel.add(lblNumberOfRobots);
        settingsPanel.add(txtNumberOfRobots);
        JButton btnRestart = new JButton();
        btnRestart.setText("Újraindít");
        btnRestart.addActionListener(e -> {
            try {
                controller.reset(Integer.parseInt(txtNumberOfRobots.getText()));
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        });
        settingsPanel.add(btnRestart);
        JLabel lblGeneratorType = new JLabel();
        lblGeneratorType.setText("Generátor típusa");
        JComboBox<String> cmbGeneratorType = new JComboBox<String>();
        cmbGeneratorType.addItem("Random");
        cmbGeneratorType.addItem("Lobster");
        cmbGeneratorType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.reset((String)cmbGeneratorType.getSelectedItem(),Integer.parseInt(txtNumberOfRobots.getText()));
         //           split.remove(1);
           //         split.add(controller.getViewPanel(),BorderLayout.SOUTH);
                    split.setRightComponent(controller.getViewPanel());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        settingsPanel.add(lblGeneratorType);
        settingsPanel.add(cmbGeneratorType);
        JButton btnNextStep = new JButton();
        btnNextStep.setText("Következő lépés");
        btnNextStep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.tickOne();
            }
        });
        settingsPanel.add(btnNextStep);
        this.graphViewPanel =controller.getViewPanel();
        split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,settingsPanel, graphViewPanel);

        this.add(split, BorderLayout.CENTER);

        //this.add(controller.getViewPanel(), BorderLayout.CENTER);
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
