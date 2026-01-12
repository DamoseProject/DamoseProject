package gui;

import model.BusInUnaFermataRecord;

import java.time.LocalTime;
import java.util.Comparator;

/**
 * Questa classe serve a decidere l'ordine con cui i bus appaiono nella lista.
 * Non li ordina solo per orario, ma dà la precedenza a quelli che hanno
 * i dati in tempo reale rispetto a quelli programmati (orario statico).
 */
public class BusArrivalComparator implements Comparator<BusInUnaFermataRecord> {

    /**
     * Confronta due bus per stabilire chi deve stare sopra nella lista.
     * Segue questa logica:
     * 1. Controlla la priorità (Real-Time > Predetto > Statico).
     * 2. Se hanno la stessa priorità, vince chi arriva prima (orario minore).
     * * @param b1 Il primo bus da confrontare.
     * @param b2 Il secondo bus da confrontare.
     * @return Un numero negativo se b1 viene prima, positivo se viene dopo, zero se sono uguali.
     */
    @Override
    public int compare(BusInUnaFermataRecord b1, BusInUnaFermataRecord b2) {
        int priority1 = getPriority(b1);
        int priority2 = getPriority(b2);

        if (priority1 != priority2) {
            return Integer.compare(priority1, priority2);
        }

        LocalTime time1 = b1.getOrarioEffettivo();
        LocalTime time2 = b2.getOrarioEffettivo();

        return time1.compareTo(time2);
    }

    /**
     * Assegna un punteggio di priorità al bus.
     * Più il numero è basso, più il bus è "importante" e salirà in alto.
     * * @param bus Il record del bus da valutare.
     * @return 1 per Real-Time (GPS), 2 per Predetto (AI), 3 per Orario Statico (Tabella).
     */
    private int getPriority(BusInUnaFermataRecord bus) {
        if (bus.isRealTime()) return 1;
        if (bus.getIsSmartPredicted()) return 2;
        return 3;
    }

}