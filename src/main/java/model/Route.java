package model;

/**
 * Questa classe rappresenta una linea di trasporto pubblico (Route).
 * Mappa le informazioni essenziali definite dallo standard GTFS, come l'identificativo
 * univoco, il nome breve (solitamente il numero della linea), il nome esteso e
 * l'agenzia di trasporto competente.
 * * <p>Include un contatore statico per monitorare il numero totale di linee
 * caricate nel sistema.</p>
 */
public class Route {

    /** Contatore statico per tenere traccia del numero totale di istanze Route create */
    private static int routeCounter = 0;
    private String id;
    private String agencyCode;
    private String shortName;
    private String longName;
    private String type;

    /**
     * Costruttore: crea una nuova istanza di Route e incrementa il contatore globale.
     * * @param id L'identificativo univoco della linea (es. "716").
     * @param agencyCode Il codice dell'agenzia che gestisce la linea (es. "ATAC").
     * @param shortName Il nome breve della linea (es. "64").
     * @param longName Il nome completo o la descrizione del percorso.
     * @param type Il tipo di mezzo di trasporto (es. bus, tram, metro).
     */
    public Route(String id, String agencyCode, String shortName, String longName, String type) {
        this.id = id;
        this.agencyCode = agencyCode;
        this.shortName = shortName;
        this.longName = longName;
        this.type = type;
        routeCounter++;
    }

    /** @return L'identificativo univoco della linea. */
    public String getId() {
        return id;
    }

    /** @return Il codice dell'agenzia di trasporto. */
    public String getAgencyCode() {
        return agencyCode;
    }

    /** @return Il nome breve (es. il numero del bus). */
    public String getShortName() {
        return shortName;
    }

    /** @return Il nome completo della linea. */
    public String getLongName() {
        return longName;
    }

    /** @return Il tipo di trasporto codificato secondo lo standard GTFS. */
    public String getType() {
        return type;
    }

    /** * Restituisce il numero totale di linee istanziate.
     * @return Il valore attuale di routeCounter.
     */
    public static int getCount(){
        return routeCounter;
    }

}