package gui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class StatisticsManager {

    private final Database db;

    public StatisticsManager(Database db) {
        this.db = db;
    }

    public void showStatisticsPopup(Component invoker, String searchText) {
        JPopupMenu popupMenu = new JPopupMenu();
        String text = buildStatisticsText(searchText);

        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        popupMenu.add(scrollPane);
        popupMenu.show(invoker, -200, invoker.getHeight());
    }

    private String buildStatisticsText(String searchText) {
        StringBuilder text = new StringBuilder();

        try {
            boolean found = false;
            Route route = db.getRoute(searchText);

            if (route != null) {
                found = true;
                text.append(buildRouteStatistics(route));
            } else {
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

    private List<Stop> findStops(String searchText) throws SQLException {
        List<Stop> fermateTrovate = new ArrayList<>();

        if (Character.isDigit(searchText.charAt(0)) && searchText.length() == 5) {
            Stop s = db.getStop(searchText);
            if (s != null) fermateTrovate.add(s);
        } else {
            fermateTrovate = db.getStopsByName(searchText);
        }

        return fermateTrovate;
    }
}
