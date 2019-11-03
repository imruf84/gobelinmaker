package org.gobelinmaker.gobelinmaker.console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

/**
 * Képmegjelenítő.
 *
 * @author imruf84
 */
public class ImageViewer extends JFrame {

    /**
     * Konstruktor.
     *
     * @param image megjelenítendő kép
     */
    public ImageViewer(BufferedImage image) {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("org/gobelinmaker/gobelinmaker/theme/MainFrameIcon.png")));
        setTitle("Kép megjelenítő");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        PanZoomPanel pzp = new PanZoomPanel() {
            @Override
            protected void drawScene(Graphics2D g) {
                g.translate(-image.getWidth() / 2, -image.getHeight() / 2);
                drawImage(g, image);
            }
        };
        add(pzp, BorderLayout.CENTER);
        pzp.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        pack();
        setLocationRelativeTo(null);
    }

}
