package gui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

public class BusRowFactory {

    private final Database db;
    private final JLabel errorLabel;
    private final MapHandler mapManager;

    public BusRowFactory(Database db, JLabel errorLabel, MapHandler mapManager) {
        this.db = db;
        this.errorLabel = errorLabel;
        this.mapManager = mapManager;
    }

    public JPanel createBusRow(BusInUnaFermataRecord bus, Stop currentStop, boolean isHighlighted) {
        JPanel rowPanel = createBaseRowPanel(isHighlighted);
        String infoText = buildBusInfoText(bus);

        if (infoText == null) return null;

        JLabel label = new JLabel(infoText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(new Color(50, 50, 50));

        rowPanel.add(label, BorderLayout.CENTER);

        Color baseColor = isHighlighted ? new Color(255, 255, 200) : new Color(240, 240, 240);
        attachMouseListener(rowPanel, label, bus, currentStop, baseColor);

        return rowPanel;
    }

    private JPanel createBaseRowPanel(boolean isHighlighted) {
        JPanel rowPanel = new JPanel(new BorderLayout());

        Color highlightColor = new Color(255, 255, 200);
        Color normalColor = new Color(240, 240, 240);
        Color baseColor = isHighlighted ? highlightColor : normalColor;

        rowPanel.setBackground(baseColor);
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));

        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        rowPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return rowPanel;
    }

    private String buildBusInfoText(BusInUnaFermataRecord bus) {
        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");

        if (bus.isRealTime()) {
            return buildRealTimeInfo(bus);
        } else if (bus.getIsSmartPredicted()) {
            return buildSmartPredictedInfo(bus, timeFormatter);
        } else {
            return buildStaticInfo(bus, timeFormatter);
        }
    }

    private String buildRealTimeInfo(BusInUnaFermataRecord bus) {
        LocalTime orarioEffettivo = bus.getOrarioEffettivo();
        LocalTime now = LocalTime.now();
        long timeRemaining = java.time.temporal.ChronoUnit.MINUTES.between(now, orarioEffettivo);

        if (timeRemaining < 0) {
            timeRemaining = 0;
        }

        String labelGrigia;
        String valoreVerde;

        if (timeRemaining == 0) {
            labelGrigia = "In arrivo: ";
            valoreVerde = "Ora";
        } else {
            labelGrigia = "Arrivo: ";
            valoreVerde = timeRemaining + " min";
        }

        return "<html><nobr><b>🚌 " + bus.getRouteId() + "</b> → " + bus.getTextDestination() +
                " <span style='color:gray'>| " + labelGrigia + "</span>" +
                "<span style='color:green'><b>" + valoreVerde + " <span style='color:gray'>| Posti: " + bus.getAffollamento() + "</b></span>" +
                "</nobr></html>";
    }

    private String buildSmartPredictedInfo(BusInUnaFermataRecord bus, java.time.format.DateTimeFormatter timeFormatter) {
        LocalTime orarioPredetto = bus.getOrarioEffettivo();
        LocalTime orarioProgrammato = bus.getOrarioStatico();

        return "<html><nobr><b>🚌 " + bus.getRouteId() + "</b> → " + bus.getTextDestination() +
                " <span style='color:gray'>| Arrivo programmato: " + orarioProgrammato.format(timeFormatter) + "</span>" +
                " <span style='color:#0044CC'>| Previsione: " + orarioPredetto.format(timeFormatter) + "</span>" +
                "</nobr></html>";
    }

    private String buildStaticInfo(BusInUnaFermataRecord bus, java.time.format.DateTimeFormatter timeFormatter) {
        LocalTime orarioStatico = bus.getOrarioStatico();

        return "<html><nobr><b>🚌 " + bus.getRouteId() + "</b> → " + bus.getTextDestination() +
                " <span style='color:gray'>| Arrivo programmato: " + orarioStatico.format(timeFormatter) + "</span>" +
                "</nobr></html>";
    }

    private void attachMouseListener(JPanel rowPanel, JLabel label, BusInUnaFermataRecord bus, Stop currentStop, Color baseColor) {
        Color hoverColor = new Color(225, 225, 225);

        MouseAdapter clickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleBusClick(bus, currentStop);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                rowPanel.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                rowPanel.setBackground(baseColor);
            }
        };

        rowPanel.addMouseListener(clickListener);
        label.addMouseListener(clickListener);
    }

    private void handleBusClick(BusInUnaFermataRecord bus, Stop currentStop) {
        try {
            if (db == null) return;

            int direction = determineDirection(bus, currentStop);
            mapManager.showRouteWithBus(bus.getRouteId(), direction, bus, currentStop);

        } catch (Exception ex) {
            errorLabel.setText("Errore caricamento percorso.");
            errorLabel.setVisible(true);
        }
    }

    private int determineDirection(BusInUnaFermataRecord bus, Stop currentStop) throws SQLException {
        int direction = 1;
        List<Stop> stopsDir0 = db.getStopsByRouteByDirection(bus.getRouteId(), 0);

        if (stopsDir0 != null) {
            for (Stop s : stopsDir0) {
                if (s.getId().equals(currentStop.getId())) {
                    direction = 0;
                    break;
                }
            }
        }

        return direction;
    }
}