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


    private JButton backButton;


    public AppPage(MainFrame frame) {
        this.frame = frame;
        appPanel = new JPanel(new BorderLayout());

        createTopPanel();

        appPanel.add(topPanel, BorderLayout.NORTH);

    }


    public void createTopPanel() {
        topPanel = new JPanel(new BorderLayout());

        //Bottone che torna su pagina di login
        JButton regLogButton = new JButton("Accedi o Registrati!");
        regLogButton.addActionListener(e -> frame.setView(new LoginPage(frame)));

        topPanel.add(regLogButton, BorderLayout.WEST);


    }

    @Override
    public JPanel getPanel() {
        return appPanel;
    }
}