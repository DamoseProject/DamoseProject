package model;

import java.util.List;

/**
 * Questa classe funge da memoria temporanea (Cache) a livello applicativo.
 * Utilizza il pattern "Singleton" per garantire che i dati caricati dal database
 * (come la lista delle linee bus) rimangano disponibili in memoria per tutta
 * la durata della sessione, migliorando la velocità di risposta dell'interfaccia
 * ed evitando letture ridondanti sul disco.
 */
public class Cache {

    /** L'unica istanza della Cache esistente nel sistema */
    private static Cache instance;

    /** Lista memorizzata delle linee bus (Route) */
    private List<Route> routes;



    /**
     * Costruttore privato per impedire la creazione di istanze multiple.
     */
    private Cache() {

    }

    /**
     * Restituisce l'istanza unica della Cache.
     * Se la Cache non è ancora stata creata, provvede alla sua inizializzazione.
     * * @return L'oggetto Cache condiviso.
     */
    public static Cache getInstance() {
        if(instance == null) {
            instance = new Cache();
        }
        return instance;
    }


}
