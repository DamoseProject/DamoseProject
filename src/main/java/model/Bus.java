package model;

import java.util.Objects;

/**
 * Questa classe rappresenta un autobus all'interno del sistema.
 * Contiene le informazioni identificative del veicolo (ID, etichetta, targa)
 * e le sue coordinate geografiche per il tracciamento sulla mappa.
 * * <p>Utilizza una variabile statica per tenere traccia del numero totale
 * di istanze di bus create durante l'esecuzione del programma.</p>
 */
public class Bus {

    /** Conteggio globale dei bus istanziati */
    private static int numberBus = 0;
    private String idBus;
    private String labelBus;
    private String licensePlate;
    private float latitude;
    private float longitude;


    /**
     * Costruttore: crea un nuovo bus con i dati identificativi di base.
     * Incrementa automaticamente il contatore globale dei bus.
     * * @param id L'identificativo univoco del bus.
     * @param label L'etichetta descrittiva o nome del bus.
     * @param licensePlate La targa del veicolo.
     */
    public Bus(String id, String label, String licensePlate) {
        this.idBus = id;
        this.labelBus = label;
        this.licensePlate = licensePlate;

        numberBus++;
    }






    /**
     * Verifica se due oggetti Bus sono uguali confrontando i loro ID.
     * * @param otherBus L'altro oggetto Bus da confrontare.
     * @return true se gli ID corrispondono, false altrimenti.
     */
    public boolean equals(Bus otherBus) {
        return Objects.equals(this.idBus, otherBus.idBus);
    }

    /** @return La latitudine attuale del bus. */
    public float getLatitude() {
        return latitude;
    }

    /** @return La longitudine attuale del bus. */
    public float getLongitude() {
        return longitude;
    }

    /** @param latitude Imposta la nuova latitudine del bus. */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /** @param longitude Imposta la nuova longitudine del bus. */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /** * Restituisce il numero totale di oggetti Bus creati.
     * @return Il valore della variabile statica numberBus.
     */
    public static int getNumberBus() {
        return numberBus;
    }

    /** @return L'ID univoco del bus. */
    public String getIdBus() {
        return idBus;
    }

    /** @return L'etichetta del bus. */
    public String getLabelBus() {
        return labelBus;
    }

    /** @return La targa del veicolo. */
    public String getLicensePlate() {
        return licensePlate;
    }
}
