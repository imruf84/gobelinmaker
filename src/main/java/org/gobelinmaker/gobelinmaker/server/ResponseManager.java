package org.gobelinmaker.gobelinmaker.server;

import java.util.HashMap;

/**
 * Válaszokat kezelő osztály.
 *
 * @author igalambo
 */
public class ResponseManager {

    /**
     * Válaszok tárolója.
     */
    private static final HashMap<Integer, String> RESPONSES = new HashMap<>();

    /**
     * Hely lefoglalása válasz számára.
     *
     * @return lefoglalt hely sorszáma
     */
    synchronized public static int add() {
        int key = 0;
        while (RESPONSES.containsKey(key)) {
            key++;
        }
        RESPONSES.put(key, "");
        return key;
    }

    /**
     * Válasz megadása.
     *
     * @param key sorszám
     * @param s szöveg
     */
    synchronized public static void set(int key, String s) {
        RESPONSES.replace(key, s);
    }

    /**
     * Válasz lekérdezése és törlése.
     *
     * @param key sorszám
     * @return válasz szövege
     */
    synchronized public static String pop(int key) {
        String s = RESPONSES.get(key);
        RESPONSES.remove(key);
        return s;
    }

    /**
     * Tároló lekérdezése.
     *
     * @return tároló
     */
    synchronized public static HashMap<Integer, String> getResponses() {
        return RESPONSES;
    }

}
