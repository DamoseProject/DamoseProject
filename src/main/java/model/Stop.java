package model;

/**
 * Questa classe rappresenta una fermata (Stop) del sistema di trasporto.
 * Contiene i dati identificativi della fermata e le sue coordinate geografiche
 * (latitudine e longitudine), necessarie per il posizionamento dei segnaposto
 * sulla mappa interattiva.
 * * <p>Mantiene un contatore statico per monitorare il numero complessivo di
 * fermate caricate nell'applicazione.</p>
 */
public class Stop {

    /** Contatore statico per tenere traccia del numero totale di istanze Stop create */
    private static int stopCounter = 0;
    private String id;
    private String code;
    private String name;
    private float latitude;
    private float longitude;

    /**
     * Costruttore: crea una nuova fermata e incrementa il contatore globale.
     * * @param id L'identificativo univoco interno (usato per le relazioni nel database).
     * @param code Il codice pubblico della fermata (spesso visualizzato sulle paline).
     * @param name Il nome descrittivo della fermata.
     * @param latitude La coordinata di latitudine.
     * @param longitude La coordinata di longitudine.
     */
    public Stop(String id, String code, String name, float latitude, float longitude) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        stopCounter++;
    }

    /** @return L'ID univoco della fermata. */
    public String getId() {
        return id;
    }

    /** @return Il codice della fermata. */
    public String getCode() {
        return code;
    }

    /** @return Il nome della fermata. */
    public String getName() {
        return name;
    }

    /** * Restituisce il numero totale di fermate istanziate.
     * @return Il valore attuale di stopCounter.
     */
    public static int getCount(){
        return stopCounter;
    }

    /** @return La latitudine geografica della fermata. */
    public float getLatitude() {return latitude;}

    /** @return La longitudine geografica della fermata. */
    public float getLongitude() {return longitude;}
}
