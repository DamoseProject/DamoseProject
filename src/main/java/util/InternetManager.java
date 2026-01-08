package util;

import java.net.URL;
import java.net.URLConnection;

public class InternetManager {

    /**
     * Verifica se c'è connessione internet tentando di contattare Google.
     * @return true se connesso, false se offline.
     */
    public static boolean isOnline() {
        try {
            URL url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();

            connection.setConnectTimeout(3000);

            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}