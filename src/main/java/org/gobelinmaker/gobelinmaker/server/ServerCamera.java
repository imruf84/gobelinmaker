package org.gobelinmaker.gobelinmaker.server;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.gobelinmaker.gobelinmaker.MyLog;

/**
 * Szerverhez csatlakoztatott kamera.
 *
 * @author imruf84
 */
public class ServerCamera {

    /**
     * Webkamera.
     */
    private final Webcam webcam;

    /**
     * Konstruktor.
     *
     * @param webcam webkmera
     */
    public ServerCamera(Webcam webcam) {
        this.webcam = webcam;

        getWebcam().addWebcamListener(new WebcamListener() {
            @Override
            public void webcamOpen(WebcamEvent we) {
                MyLog.info("Webcam '" + getName() + "' opened.");
            }

            @Override
            public void webcamClosed(WebcamEvent we) {
                MyLog.info("Webcam '" + getName() + "' closed.");
            }

            @Override
            public void webcamDisposed(WebcamEvent we) {
                MyLog.info("Webcam '" + getName() + "' disposed.");
            }

            @Override
            public void webcamImageObtained(WebcamEvent we) {
            }
        });
    }

    /**
     * Webkamera lekérdezése.
     *
     * @return webkamera
     */
    public Webcam getWebcam() {
        return webcam;
    }

    /**
     * Webkamera megnyitása.
     */
    public void open() {

        if (getWebcam().isOpen()) {
            return;
        }

        MyLog.info("Opening webcam '" + getName() + "'...");

        getWebcam().setCustomViewSizes(new Dimension[]{WebcamResolution.HD.getSize(), new Dimension(1920, 1080)});
        getWebcam().setViewSize(new Dimension(1920, 1080));

        getWebcam().open();
    }

    /**
     * Webkamera bezárása.
     */
    public void close() {
        if (!getWebcam().isOpen()) {
            return;
        }

        MyLog.info("Closing webcam '" + getName() + "'...");
        getWebcam().close();
    }

    /**
     * Kép készítése.
     *
     * @return kép
     */
    public BufferedImage getImage() {
        if (!getWebcam().isOpen()) {
            return null;
        }
        MyLog.debug("Getting image from " + getName() + "...");
        BufferedImage bi = getWebcam().getImage();
        MyLog.debug("Image got from " + getName() + ".");
        return bi;
    }

    /**
     * Név lekérdezése.
     *
     * @return név
     */
    public String getName() {
        String name = getWebcam().getName();
        return name.substring(0, name.lastIndexOf(" "));
    }

    /**
     * Állapot lekérdezése.
     *
     * @return állapot
     */
    public boolean isOpened() {
        return getWebcam().isOpen();
    }

}
