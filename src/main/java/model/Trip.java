package model;

/**
 * Questa classe rappresenta un singolo viaggio (Trip) all'interno del sistema.
 * Un "Trip" è un'istanza specifica di una {@link Route} che avviene in un
 * determinato arco temporale e segue una direzione precisa.
 * * <p>Include informazioni sulla destinazione, l'accessibilità per disabili
 * e il tipo di servizio (es. feriale o festivo).</p>
 */
public class Trip {

    /** Contatore statico per monitorare il numero totale di viaggi istanziati */
    private static int tripCount = 0;
    private String id;
    private String routeId;
    private String serviceId;
    private String tripHeadsign;
    private String tripShortName;
    private int direction;
    private int wheelchair_accessible;

    /**
     * Costruttore: crea un nuovo viaggio e incrementa il contatore globale.
     * * @param id Identificativo univoco del viaggio.
     * @param routeId Riferimento alla linea (Route) di appartenenza.
     * @param serviceId Identificativo del calendario di servizio (es. giorni lavorativi).
     * @param tripHeadsign Testo della destinazione finale mostrato sul bus.
     * @param tripShortName Nome breve del viaggio (opzionale).
     * @param direction Identificatore della direzione (0 per andata, 1 per ritorno).
     * @param wheelchair_accessible Indica se il mezzo è accessibile in sedia a rotelle (1 = sì, 0 = no).
     */
    public Trip(String id, String routeId, String serviceId, String tripHeadsign, String tripShortName, int direction, int wheelchair_accessible) {
        this.id = id;
        this.routeId = routeId;
        this.serviceId = serviceId;
        this.tripHeadsign = tripHeadsign;
        this.tripShortName = tripShortName;
        this.direction = direction;
        this.wheelchair_accessible = wheelchair_accessible;
        tripCount++;
    }

    /** @return L'identificativo univoco del viaggio (TripId). */
    public String getId() {
        return id;
    }

    /** @return L'ID della linea associata a questo viaggio. */
    public String getRouteId() {
        return routeId;
    }

    /** @return L'ID del servizio (utile per filtrare corse feriali/festive). */
    public String getServiceId() {
        return serviceId;
    }

    /** @return Il testo della destinazione (es. "Termini"). */
    public String getTripHeadsign() {
        return tripHeadsign;
    }

    /** @return Il nome breve del viaggio. */
    public String getTripShortName() {
        return tripShortName;
    }

    /** @return La direzione del viaggio (0 o 1). */
    public int getDirection() {
        return direction;
    }

    /** @return Lo stato di accessibilità per disabili (standard GTFS). */
    public int getWheelchair_accessible() {
        return wheelchair_accessible;
    }

    /** * Restituisce il numero totale di oggetti Trip creati.
     * @return Il valore attuale di tripCount.
     */
    public static int getCount() {
        return tripCount;
    }

}
