package gui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * Questa classe gestisce la logica di visualizzazione delle statistiche.
 * Recupera i dati storici relativi ai ritardi e alle corse saltate dal database
 * e li mostra all'utente attraverso un'interfaccia testuale contenuta in un menu a comparsa.
 */
public class StatisticsManager {

    private final Database db;

    /**
     * Costruttore del manager delle statistiche.
     * @param db Il database da cui attingere i dati storici e in tempo reale.
     */
    public StatisticsManager(Database db) {
        this.db = db;
    }

    /**
     * Crea e mostra un menu popup contenente le statistiche relative alla ricerca effettuata.
     * Il popup include un'area di testo scorrevole per ospitare elenchi lunghi.
     * @param invoker Il componente grafico che ha attivato la richiesta (es. un pulsante).
     * @param searchText Il testo inserito dall'utente (ID linea o nome fermata).
     */
    public void showStatisticsPopup(Component invoker, String searchText) {
        JPopupMenu popupMenu = new JPopupMenu();
        String text = buildStatisticsText(searchText);

        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setBackground(new Color(250, 250, 250)); // Coerenza estetica

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        popupMenu.add(scrollPane);
        popupMenu.show(invoker, -200, invoker.getHeight());
    }

    /**
     * Identifica se il testo cercato corrisponde a una linea (Route) o a una fermata (Stop)
     * e richiama il metodo di generazione testo appropriato.
     * @param searchText Stringa di ricerca.
     * @return Il testo formattato pronto per la visualizzazione.
     */
    private String buildStatisticsText(String searchText) {
        StringBuilder text = new StringBuilder();

        try {
            boolean found = false;

            // Cerca prima come Route
            Route route = db.getRoute(searchText);

            if (route != null) {
                found = true;
                text.append(buildRouteStatistics(route));
            } else {
                // Altrimenti cerca come Stop
                List<Stop> fermateTrovate = findStops(searchText);

                if (!fermateTrovate.isEmpty()) {
                    found = true;
                    text.append(buildStopStatistics(fermateTrovate));
                }
            }

            if (!found) {
                text.append("Nessuna linea o fermata trovata per: ").append(searchText);
            }

        } catch (SQLException ex) {
            text.append("Errore durante il recupero delle statistiche: ").append(ex.getMessage());
        }

        return text.toString();
    }

    /**
     * Genera il report statistico per tutte le fermate associate a una specifica linea bus.
     * @param route L'oggetto linea bus da analizzare.
     * @return Stringa contenente medie ritardi e corse saltate per ogni fermata della linea.
     */
    private String buildRouteStatistics(Route route) throws SQLException {
        StringBuilder text = new StringBuilder();
        text.append("STATISTICHE PER LINEA: ").append(route.getId()).append("\n\n");

        List<Stop> fermate = db.getStopsByRoute(route.getId());
        for (Stop fermata : fermate) {
            double[] stats = db.getStatisticheStoriche(route.getId(), fermata.getId());
            text.append("Fermata: ").append(fermata.getId()).append(" - ").append(fermata.getName()).append("\n");
            text.append("   Media ritardo: ").append(String.format("%.2f", stats[0])).append(" min\n");
            text.append("   Corse saltate: ").append(String.format("%.2f", stats[1])).append("%\n");
            text.append("--------------------------------------------------\n");
        }

        return text.toString();
    }

    /**
     * Genera il report statistico per una o più fermate, basandosi sulle linee bus
     * che vi transitano in tempo reale.
     * @param fermateTrovate Lista di fermate risultanti dalla ricerca.
     * @return Stringa contenente i dati storici incrociati con i transiti attuali.
     */
    private String buildStopStatistics(List<Stop> fermateTrovate) throws SQLException {
        StringBuilder text = new StringBuilder();
        text.append("STATISTICHE PER FERMATA/E TROVATE:\n");
        text.append("(Basate sulle linee attualmente in arrivo)\n\n");

        for (Stop stop : fermateTrovate) {
            text.append("FERMATA: ").append(stop.getName()).append(" (").append(stop.getId()).append(")\n");

            List<BusInUnaFermataRecord> arrivi = db.getRealTimeArrivals(stop.getId());
            Set<String> lineeProcessate = new HashSet<>();
            boolean autobusTrovati = false;

            if (arrivi != null) {
                for (BusInUnaFermataRecord bus : arrivi) {
                    String routeId = bus.getRouteId();

                    if (lineeProcessate.contains(routeId)) {
                        continue;
                    }

                    double[] stats = db.getStatisticheStoriche(routeId, stop.getId());

                    text.append("   Linea ").append(routeId).append(":\n");
                    text.append("      Media ritardo: ").append(String.format("%.2f", stats[0])).append(" min\n");
                    text.append("      Corse saltate: ").append(String.format("%.2f", stats[1])).append("%\n");

                    lineeProcessate.add(routeId);
                    autobusTrovati = true;
                }
            }

            if (!autobusTrovati) {
                text.append("   Nessun autobus in arrivo al momento.\n");
            }

            text.append("--------------------------------------------------\n");
        }

        return text.toString();
    }

    /**
     * Metodo di supporto per cercare le fermate tramite ID (se il testo è numerico di 5 cifre)
     * o tramite nome parziale.
     * @param searchText Il testo da cercare nel database.
     * @return Una lista di oggetti Stop corrispondenti.
     */
    private List<Stop> findStops(String searchText) throws SQLException {
        List<Stop> fermateTrovate = new ArrayList<>();

        if (!searchText.isEmpty() && Character.isDigit(searchText.charAt(0)) && searchText.length() == 5) {
            Stop s = db.getStop(searchText);
            if (s != null) fermateTrovate.add(s);
        } else {
            fermateTrovate = db.getStopsByName(searchText);
        }

        return fermateTrovate;
    }
}