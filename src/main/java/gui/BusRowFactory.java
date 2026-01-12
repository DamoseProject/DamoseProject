package gui;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Questa classe si occupa di creare graficamente le "righe" che rappresentano i bus in arrivo.
 * Trasforma i dati che arrivano dal database in piccoli pannelli colorati che l'utente
 * può vedere e cliccare nella lista dei risultati.
 */
public class BusRowFactory {

    private final Database db;
    private final JLabel errorLabel;
    private final MapHandler mapManager;


    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Costruttore della fabbrica.
     * @param db Il database da cui scaricare i percorsi.
     * @param errorLabel L'etichetta dove mostrare eventuali messaggi di errore.
     * @param mapManager Il gestore della mappa per mostrare il percorso al click.
     */
    public BusRowFactory(Database db, JLabel errorLabel, MapHandler mapManager) {
        this.db = db;
        this.errorLabel = errorLabel;
        this.mapManager = mapManager;
    }

    /**
     * Crea il pannello grafico (la riga) per un singolo bus.
     * @param bus I dati del bus da visualizzare.
     * @param currentStop La fermata selezionata dall'utente.
     * @param isHighlighted Indica se la riga deve essere evidenziata (es. sfondo giallo).
     * @return Un JPanel configurato con testo e colori, pronto per la UI.
     */
    public JPanel createBusRow(BusInUnaFermataRecord bus, Stop currentStop, boolean isHighlighted) {
        JPanel rowPanel = createBaseRowPanel(isHighlighted);
        String infoText = buildBusInfoText(bus);

        if (infoText == null) return null;

        JLabel label = new JLabel(infoText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(new Color(50, 50, 50));
        label.setHorizontalAlignment(SwingConstants.LEFT);

        rowPanel.add(label, BorderLayout.CENTER);

        Color baseColor = isHighlighted ? new Color(255, 255, 200) : new Color(240, 240, 240);
        attachMouseListener(rowPanel, label, bus, currentStop, baseColor);

        return rowPanel;
    }

    /**
     * Crea lo sfondo e i bordi della riga del bus.
     * @param isHighlighted Se true, imposta lo sfondo giallino, altrimenti grigio chiaro.
     * @return Il pannello base con lo stile impostato.
     */
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

    /**
     * Sceglie quale tipo di testo mostrare in base alla qualità del dato (Tempo reale, predetto o statico).
     * @param bus Il record del bus.
     * @return Una stringa formattata in HTML.
     */
    private String buildBusInfoText(BusInUnaFermataRecord bus) {
        if (bus.isRealTime()) {
            return buildRealTimeInfo(bus);
        } else if (bus.getIsSmartPredicted()) {
            return buildSmartPredictedInfo(bus);
        } else {
            return buildStaticInfo(bus);
        }
    }

    /**
     * Crea il testo per i bus con dati GPS in tempo reale. Calcola i minuti mancanti.
     * @param bus Il record del bus.
     * @return Testo HTML con i minuti mancanti e l'affollamento.
     */
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

    /**
     * Crea il testo per i bus con orario predetto tramite algoritmi.
     * @param bus Il record del bus.
     * @return Testo HTML con orario programmato e previsione.
     */
    private String buildSmartPredictedInfo(BusInUnaFermataRecord bus) {
        LocalTime orarioPredetto = bus.getOrarioEffettivo();
        LocalTime orarioProgrammato = bus.getOrarioStatico();

        return "<html><nobr><b>🚌 " + bus.getRouteId() + "</b> → " + bus.getTextDestination() +
                " <span style='color:gray'>| Arrivo programmato: " + orarioProgrammato.format(TIME_FORMATTER) + "</span>" +
                " <span style='color:#0044CC'>| Previsione: " + orarioPredetto.format(TIME_FORMATTER) + "</span>" +
                "</nobr></html>";
    }

    /**
     * Crea il testo base per i bus che hanno solo l'orario da tabella (statico).
     * @param bus Il record del bus.
     * @return Testo HTML con l'orario ufficiale.
     */
    private String buildStaticInfo(BusInUnaFermataRecord bus) {
        LocalTime orarioStatico = bus.getOrarioStatico();

        return "<html><nobr><b>🚌 " + bus.getRouteId() + "</b> → " + bus.getTextDestination() +
                " <span style='color:gray'>| Arrivo programmato: " + orarioStatico.format(TIME_FORMATTER) + "</span>" +
                "</nobr></html>";
    }

    /**
     * Aggiunge i listener per gestire il click e il cambio colore quando il mouse passa sopra la riga.
     * @param rowPanel Il pannello della riga.
     * @param label L'etichetta di testo.
     * @param bus I dati del bus.
     * @param currentStop La fermata attuale.
     * @param baseColor Il colore da ripristinare quando il mouse esce.
     */
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

    /**
     * Gestisce l'azione da compiere quando si preme sulla riga del bus (mostra percorso su mappa).
     * @param bus Il bus selezionato.
     * @param currentStop La fermata di riferimento.
     */
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

    /**
     * Cerca di capire se il bus sta andando nella direzione 0 o 1 in base alla fermata attuale.
     * @param bus Il bus selezionato.
     * @param currentStop La fermata dell'utente.
     * @return 0 se la fermata è nel tragitto di andata, 1 altrimenti.
     * @throws SQLException In caso di problemi con la query al database.
     */
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