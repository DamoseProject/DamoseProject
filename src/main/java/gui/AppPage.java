package gui;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;

public class AppPage implements GeneralPanel {
    private final MainFrame frame;
    private final JPanel appPanel;
    private JPanel topPanel;
    private JPanel mapPanel;

    private JTextField researchField;


    public AppPage(MainFrame frame) {
        this.frame = frame;
        appPanel = new JPanel(new BorderLayout());

        createTopPanel();
        createMapPanel();

        appPanel.add(topPanel, BorderLayout.NORTH);
        appPanel.add(mapPanel, BorderLayout.CENTER);
    }

    private JPanel createFieldPanel(String labelName, JComponent field) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelName);
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        field.setMaximumSize(field.getPreferredSize());
        field.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        fieldPanel.add(label);
        fieldPanel.add(field);

        return fieldPanel;


    }


    private void createTopPanel() {
        topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // spazi vuoi da 5 pixel attorno

        // ---------------- leftPanel: bottone + spazio + textfield (verticale) ----------------
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JButton regLogButton = new JButton("Accedi o Registrati!");
        regLogButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        regLogButton.addActionListener(e -> frame.setView(new LoginPage(frame)));
        leftPanel.add(regLogButton);

        // spazio verticale tra bottone e textfield
        leftPanel.add(Box.createVerticalStrut(30));

        researchField = new JTextField(20);
        JPanel researchFieldPanel = createFieldPanel("Inserisci n. Fermata o nome della Linea!", researchField);
        researchFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(researchFieldPanel);

        // Aggiungo leftPanel nella colonna 0, riga 0 (ancorato in alto a sinistra)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        topPanel.add(leftPanel, gbc);

        // ---------------- centerLabel: colonna centrale, occupa lo spazio orizzontale ----------------
        JLabel centerLabel = new JLabel("Dove vuoi andare?", SwingConstants.CENTER);
        // la ancoro in alto per avere la stessa "linea" del bottone
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;                    // la colonna centrale si espande
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(centerLabel, gbc);

        // ---------------- right spacer: colonna 2, stessa larghezza di leftPanel ----------------
        // uso la larghezza preferita di leftPanel
        int leftWidth = leftPanel.getPreferredSize().width;
        Component rightSpacer = Box.createRigidArea(new Dimension(leftWidth, 1));

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        topPanel.add(rightSpacer, gbc);

        // ---------------- riga di riempimento per occupare lo spazio rimanente verticalmente ----------------
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        topPanel.add(Box.createGlue(), gbc);
    }


    private void createMapPanel() {
        mapPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.SOUTHWEST; // angolo in basso a sinistra
        gbc.insets = new Insets(10, 0, 0, 0); // 10px sopra per il JTextField
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;


        JXMapViewer mapViewer = createMapViewer();
        mapPanel.add(mapViewer, gbc);
    }

    private JXMapViewer createMapViewer() {
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        JXMapViewer mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);

        GeoPosition roma = new GeoPosition(41.9028, 12.4964);
        mapViewer.setZoom(5);
        mapViewer.setAddressLocation(roma);

        mapViewer.setPreferredSize(new Dimension(500, 400));

        // --- AGGIUNGI I LISTENER ---
        // Pan con mouse (drag)
        MouseInputListener mil = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mil);
        mapViewer.addMouseMotionListener(mil);

        // Zoom con rotellina del mouse
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));

        // Pan con tastiera (freccette)
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        return mapViewer;
    }





    @Override
    public JPanel getPanel() {
        return appPanel;
    }


    public String getResearchField() {
        return researchField.getText().trim();
    }
}