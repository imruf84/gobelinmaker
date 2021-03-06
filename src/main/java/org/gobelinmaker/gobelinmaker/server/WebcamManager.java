package org.gobelinmaker.gobelinmaker.server;

import com.github.sarxos.webcam.Webcam;
import java.util.HashMap;

import org.gobelinmaker.gobelinmaker.MyLog;

/**
 * Szerver oldali kamerák kezelője.
 *
 * @author imruf84
 */
public class WebcamManager extends HashMap<Integer, ServerCamera> {
    
    /**
     * Csatlakoztatott kamerák felderítése.
     * 
     * @return sikeres találat esetén igaz egyébként hamis
     */
    public boolean scan() {
        MyLog.info("Scanning for webcams...");

        int i = 0;
        for (Webcam webcam : Webcam.getWebcams()) {

            ServerCamera sc = new ServerCamera(webcam);

            MyLog.info("Webcam found with name:'" + sc.getName() + "'");
            put(i++, sc);
        }

        if (isEmpty()) {
            MyLog.warning("No webcams found.");
            return false;
        }
        
        return true;
    }
    
    public ServerCamera getFirst() {
        if (isEmpty()) {
            return null;
        }
        
        return get((int)keySet().toArray()[0]);
    }

    @Override
    public String toString() {

        String result = "";

        for (int i = 0; i < size(); i++) {
            result += "[" + i + "] " + get(i).getName() + "\n";
        }

        return result;
    }
}
