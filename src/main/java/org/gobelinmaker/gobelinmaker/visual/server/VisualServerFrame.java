package org.gobelinmaker.gobelinmaker.visual.server;

import com.github.sarxos.webcam.Webcam;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

/**
 * Szimulátor ablaka.
 *
 * @author imruf84
 */
public class VisualServerFrame extends JFrame {

    /**
     * Konstruktor.
     *
     * @throws HeadlessException kivétel
     */
    public VisualServerFrame() throws HeadlessException {

        setTitle("Gobelin Maker Simulator");
        setLayout(new BorderLayout());
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("gobelinmaker/theme/MainFrameIcon.png")));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOneTouchExpandable(false);
        splitPane.setResizeWeight(1);
        add(splitPane, BorderLayout.CENTER);

        // Főpanel.
        VisualServerPanel sp = new VisualServerPanel();
        splitPane.setDividerSize(5);
        splitPane.add(sp);

        // Vezérlők.
        JPanel controlsPanel = new JPanel(new BorderLayout());
        JScrollPane controlsScrollPane = new JScrollPane(controlsPanel);
        splitPane.add(controlsScrollPane);

        // Kamerák.
        JPanel jp1 = new JPanel(new BorderLayout());
        controlsPanel.add(jp1, BorderLayout.CENTER);
        CollapsiblePanel camerasPanel = new CollapsiblePanel();
        jp1.add(camerasPanel, BorderLayout.NORTH);
        camerasPanel.setLayout(new BoxLayout(camerasPanel, BoxLayout.PAGE_AXIS));
        camerasPanel.setTitle("Kamerák");

        // Kamerák listája.
        LinkedList<String> camerasList = new LinkedList<>();
        camerasList.add("");
        Webcam.getWebcams().forEach((w) -> {
            camerasList.add(w.getName());
        });

        // Kamera 1
        JPanel camera1Panel = new JPanel();
        camera1Panel.setLayout(new BoxLayout(camera1Panel, BoxLayout.LINE_AXIS));
        camerasPanel.add(camera1Panel);
        camera1Panel.add(new JLabel("#1 "));
        JComboBox camera1ComboBox = new JComboBox(camerasList.toArray());
        camera1Panel.add(camera1ComboBox);
        camera1ComboBox.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {

                // Kamerakép megjelenítése.
                Webcam w = null;
                int webcamIndex = camera1ComboBox.getSelectedIndex();
                if (webcamIndex > 0) {
                    w = Webcam.getWebcams().get(camera1ComboBox.getSelectedIndex() - 1);
                }

                sp.setWebcam(w);
            }
        });

        JPanel jp2 = new JPanel(new BorderLayout());
        jp1.add(jp2, BorderLayout.CENTER);
        CollapsiblePanel camerasPane2 = new CollapsiblePanel();
        jp2.add(camerasPane2, BorderLayout.NORTH);
        camerasPane2.setLayout(new BoxLayout(camerasPane2, BoxLayout.PAGE_AXIS));
        camerasPane2.setTitle("Kamerák2");
        camerasPane2.add(new JTextField("aaa"));

        // Állapotsor.
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(new BevelBorder(1));
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(new JLabel("Loading..."));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension((int) (screenSize.width * .8), (int) (screenSize.height * .8)));
        setMinimumSize(sp.getMinimumSize());
        pack();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent we) {
            }

            @Override
            public void windowClosing(WindowEvent we) {
                sp.closeWebcam(true);
            }

            @Override
            public void windowClosed(WindowEvent we) {
            }

            @Override
            public void windowIconified(WindowEvent we) {
            }

            @Override
            public void windowDeiconified(WindowEvent we) {
            }

            @Override
            public void windowActivated(WindowEvent we) {
            }

            @Override
            public void windowDeactivated(WindowEvent we) {
            }
        });
    }

}
