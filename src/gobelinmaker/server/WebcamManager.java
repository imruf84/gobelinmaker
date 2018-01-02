package gobelinmaker.server;

import com.github.sarxos.webcam.Webcam;
import gobelinmaker.MyLog;
import java.util.HashMap;

/**
 * Szerver oldali kamerák kezelője.
 *
 * @author imruf84
 */
public class WebcamManager extends HashMap<Integer, ServerCamera> {
    
    /**
     * Csatlakoztatott kamerák felderítése.
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

    @Override
    public String toString() {

        String result = "";

        for (int i = 0; i < size(); i++) {
            result += "[" + i + "] " + get(i).getName() + "\n";
        }

        return result;
    }
}
