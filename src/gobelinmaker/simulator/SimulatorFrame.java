package gobelinmaker.simulator;

import com.github.sarxos.webcam.Webcam;
import com.sun.java.swing.plaf.motif.MotifBorders;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;

/**
 * Szimulátor ablaka.
 * 
 * @author imruf84
 */
public class SimulatorFrame extends JFrame {

    /**
     * Konstruktor.
     * 
     * @throws HeadlessException kivétel
     */
    public SimulatorFrame() throws HeadlessException {
        
        setTitle("Gobelin Maker Simulator");
        setLayout(new BorderLayout());
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("gobelinmaker/theme/MainFrameIcon.png")));
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOneTouchExpandable(false);
        splitPane.setResizeWeight(1);    
        add(splitPane, BorderLayout.CENTER);
        
        // Főpanel.
        SimulatorPanel sp = new SimulatorPanel();
        splitPane.setDividerSize(5);
        splitPane.add(sp);
        
        // Vezérlők.
        JPanel controlsPanel = new JPanel(new BorderLayout());
        JScrollPane controlsScrollPane = new JScrollPane(controlsPanel);
        splitPane.add(controlsScrollPane);
        
        // Kamerák.
        JPanel jp1 = new JPanel(new BorderLayout());
        controlsPanel.add(jp1, BorderLayout.CENTER);
        JPanel camerasPanel = new JPanel();
        jp1.add(camerasPanel, BorderLayout.NORTH);
        camerasPanel.setLayout(new BoxLayout(camerasPanel, BoxLayout.PAGE_AXIS));
        camerasPanel.setBorder(BorderFactory.createTitledBorder("Kamerák"));
        
        LinkedList<String> camerasList = new LinkedList<>();
        camerasList.add("");
        Webcam.getWebcams().forEach((w) -> {
            camerasList.add(w.toString());
        });
        
        // Kamera 1
        JPanel camera1Panel = new JPanel();
        camera1Panel.setLayout(new BoxLayout(camera1Panel, BoxLayout.LINE_AXIS));
        camerasPanel.add(camera1Panel);
        camera1Panel.add(new JLabel("#1 "));
        camera1Panel.add(new JComboBox(camerasList.toArray()));
        
        
        // Állapotsor.
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(new BevelBorder(1));
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(new JLabel("Loading..."));
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension((int)(screenSize.width*.5), (int)(screenSize.height*.8)));
        setMinimumSize(sp.getMinimumSize());
        pack();
        
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
}
