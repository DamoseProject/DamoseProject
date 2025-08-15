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

public class AppPage extends BasePage {
    private JPanel topPanel;
    private JPanel mapPanel;
    private JTextField researchField;

    public AppPage(MainFrame frame) {
        super(frame);
        createTopPanel();
        createMapPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(mapPanel, BorderLayout.CENTER);
    }

    private void createTopPanel() {
        topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // ---------------- leftPanel ----------------
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JButton regLogButton = new JButton("Accedi o Registrati!");
        regLogButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        regLogButton.addActionListener(e -> frame.setView(new LoginPage(frame)));
        leftPanel.add(regLogButton);

        leftPanel.add(Box.createVerticalStrut(70));

        researchField = new JTextField(20);
        JPanel researchFieldPanel = createFieldPanel("Inserisci n. Fermata o nome della Linea!", researchField);
        researchFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(researchFieldPanel);

        // aggiunta in topPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        topPanel.add(leftPanel, gbc);

        // ---------------- centerLabel ----------------
        JLabel centerLabel = new JLabel("Dove vuoi andare?", SwingConstants.CENTER);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(centerLabel, gbc);

        // ---------------- right spacer ----------------
        int leftWidth = leftPanel.getPreferredSize().width;
        Component rightSpacer = Box.createRigidArea(new Dimension(leftWidth, 1));

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        topPanel.add(rightSpacer, gbc);

        // ---------------- filler ----------------
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
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.insets = new Insets(10, 0, 0, 0);
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

        // listener
        MouseInputListener mil = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mil);
        mapViewer.addMouseMotionListener(mil);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        return mapViewer;
    }

    public String getResearchField() {
        return researchField.getText().trim();
    }
}