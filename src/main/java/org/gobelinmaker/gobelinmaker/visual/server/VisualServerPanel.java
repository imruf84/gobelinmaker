package org.gobelinmaker.gobelinmaker.visual.server;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Szimulátor panel.
 *
 * @author imruf84
 */
public class VisualServerPanel extends JPanel {

    public static final int DEFAULT_MIN_ZOOM_LEVEL = -20;
    public static final int DEFAULT_MAX_ZOOM_LEVEL = 10;
    public static final double DEFAULT_ZOOM_MULTIPLICATION_FACTOR = 1.2;

    private int zoomLevel = 0;
    private final int minZoomLevel = DEFAULT_MIN_ZOOM_LEVEL;
    private final int maxZoomLevel = DEFAULT_MAX_ZOOM_LEVEL;
    private final double zoomMultiplicationFactor = DEFAULT_ZOOM_MULTIPLICATION_FACTOR;

    private Point dragStartScreen;
    private Point dragEndScreen;
    private final AffineTransform coordTransform = new AffineTransform();
    private boolean init = true;
    BufferedImage image = null;
    private final AtomicReference<Webcam> webcam = new AtomicReference<>(null);
    private final AtomicReference<BufferedImage> webcamImage = new AtomicReference<>(null);
    private final Timer webcamTimer;

    /**
     * Konstruktor.
     */
    public VisualServerPanel() {

        /*try {
            image = ImageIO.read(new URL("http://www.findcatnames.com/wp-content/uploads/2017/01/tabby-cat-names.jpg"));
        } catch (MalformedURLException ex) {
            Logger.getLogger(VisualServerPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VisualServerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        webcamTimer = new Timer(true);
        webcamTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Webcam wc = webcam.get();
                if (wc != null) {

                    if (!wc.isOpen() && !wc.getLock().isLocked()) {
                        wc.setViewSize(new Dimension(640,480));
                        //wc.setCustomViewSizes(new Dimension[]{WebcamResolution.HD.getSize()});
//                        wc.setViewSize(WebcamResolution.HD.getSize());
                        wc.open();
                    }

                    webcamImage.set(wc.getImage());

                    revalidate();
                    repaint();
                }
            }
        }, 0, 100);

        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(300, 300));

        addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                dragStartScreen = e.getPoint();
                dragEndScreen = null;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                moveCamera(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });

        addMouseWheelListener((MouseWheelEvent e) -> {
            zoomCamera(e);
        });

    }

    public void closeWebcam(boolean cancelTimer) {

        if (cancelTimer) {
            webcamTimer.cancel();
        }

        Webcam wc = webcam.get();

        if (wc != null && wc.isOpen()) {
            wc.close();
        }

        webcam.set(null);
    }

    public void setWebcam(Webcam pwc) {
        closeWebcam(false);
        webcam.set(pwc);
    }

    private java.awt.geom.Point2D.Float transformPoint(Point p1) throws NoninvertibleTransformException {
        AffineTransform inverse = coordTransform.createInverse();

        java.awt.geom.Point2D.Float p2 = new java.awt.geom.Point2D.Float();
        inverse.transform(p1, p2);
        return p2;
    }

    private void moveCamera(MouseEvent e) {

        if (!SwingUtilities.isMiddleMouseButton(e)) {
            return;
        }

        try {
            dragEndScreen = e.getPoint();
            java.awt.geom.Point2D.Float dragStart = transformPoint(dragStartScreen);
            java.awt.geom.Point2D.Float dragEnd = transformPoint(dragEndScreen);
            double dx = dragEnd.getX() - dragStart.getX();
            double dy = dragEnd.getY() - dragStart.getY();
            coordTransform.translate(dx, dy);
            dragStartScreen = dragEndScreen;
            dragEndScreen = null;
            repaintComponent();
        } catch (NoninvertibleTransformException ex) {
        }
    }

    private void repaintComponent() {
        revalidate();
        repaint();
    }

    private void zoomCamera(MouseWheelEvent e) {
        try {
            int wheelRotation = e.getWheelRotation();
            Point p = e.getPoint();
            if (wheelRotation > 0) {
                if (zoomLevel < maxZoomLevel) {
                    zoomLevel++;
                    java.awt.geom.Point2D p1 = transformPoint(p);
                    coordTransform.scale(1 / zoomMultiplicationFactor, 1 / zoomMultiplicationFactor);
                    java.awt.geom.Point2D p2 = transformPoint(p);
                    coordTransform.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY());
                    repaintComponent();
                }
            } else {
                if (zoomLevel > minZoomLevel) {
                    zoomLevel--;
                    java.awt.geom.Point2D p1 = transformPoint(p);
                    coordTransform.scale(zoomMultiplicationFactor, zoomMultiplicationFactor);
                    java.awt.geom.Point2D p2 = transformPoint(p);
                    coordTransform.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY());
                    repaintComponent();
                }
            }
        } catch (NoninvertibleTransformException ex) {
        }
    }

    @Override
    public void paintComponent(Graphics pg) {
        super.paintComponent(pg);
        Graphics2D g = (Graphics2D) pg;

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (init) {
            init = false;
            Dimension d = getSize();
            int xc = d.width / 2;
            int yc = d.height / 2;
            g.translate(xc, yc);
            g.scale(1, -1);
            coordTransform.setToIdentity();
            coordTransform.setTransform(g.getTransform());
        } else {
            g.setTransform(coordTransform);
        }

        drawImage(g, webcamImage.get());

        g.setColor(Color.BLACK);

        g.drawLine(-100, 0, 1500, 0);
        g.drawLine(0, -100, 0, 1000);

    }

    private void drawImage(Graphics2D g, BufferedImage image) {

        if (image == null) {
            return;
        }

        AffineTransform tr = g.getTransform();
        g.scale(1, -1);
        g.translate(0, -image.getHeight());
        g.drawImage(image, 0, 0, null);
        g.drawRect(0, 0, image.getWidth(), image.getHeight());
        g.setTransform(tr);
    }

}
