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
    private final JPanel appPanel;

    private final GeoPosition roma;

    private GridBagConstraints gbc;

    private JXMapViewer mapViewer;



    public AppPage(MainFrame frame) {
        appPanel = new JPanel();
        appPanel.setLayout(new GridBagLayout());


        mapViewer = new JXMapViewer();

        gbc = new GridBagConstraints();

        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        roma = new GeoPosition(41.9028, 12.4964);
        mapViewer.setAddressLocation(roma);
        mapViewer.setZoom(7);

        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        // Dimensioni mappa
        int larghezza = 300;
        int altezza = 200;
        mapViewer.setPreferredSize(new Dimension(larghezza, altezza));

        // Posizionamento con GridBagLayout: in basso a sinistra
        gbc.gridx = 0;           // colonna 0 (sinistra)
        gbc.gridy = 1;           // riga 1 (in basso)
        gbc.anchor = GridBagConstraints.LINE_START;  // allinea a sinistra
        gbc.weightx = 1.0;       // per spingere tutto verso il basso
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);  // margine in basso (10px)

        // Per spingere la mappa in basso, mettiamo un "dummy" componente sopra per occupare lo spazio
        // Riga 0: componente invisibile che occupa spazio
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        appPanel.add(Box.createGlue(), gbc);

        // Ora aggiungiamo la mappa
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        appPanel.add(mapViewer, gbc);
    }

    @Override
    public JPanel getPanel() {
        return appPanel;
    }
}
