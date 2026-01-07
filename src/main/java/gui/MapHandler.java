package gui;

import model.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.awt.BasicStroke;

public class MapHandler {

    private final JXMapViewer mapViewer;
    private final Set<Waypoint> currentWaypoints = new HashSet<>();
    private final Database db;
    private final JLabel errorLabel;

    private static class LabeledWaypoint extends DefaultWaypoint {
        private final String id;
        private final String name;

        public LabeledWaypoint(GeoPosition coord, String id, String name) {
            super(coord);
            this.id = id;
            this.name = name;
        }

        public String getLabel() {
            return "<html><b>Fermata:</b> " + id + "<br><b>Nome:</b> " + name + "</html>";
        }
    }

    private static class RoutePainter implements Painter<JXMapViewer> {
        private final List<GeoPosition> track;

        public RoutePainter(List<GeoPosition> track) {
            this.track = new ArrayList<>(track);
        }

        @Override
        public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
            g = (Graphics2D) g.create();
            g.setColor(Color.BLUE);
            g.setStroke(new BasicStroke(3));

            int lastX = -1;
            int lastY = -1;

            Rectangle rect = map.getViewportBounds();

            for (GeoPosition gp : track) {
                Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());
                int x = (int) (pt.getX() - rect.getX());
                int y = (int) (pt.getY() - rect.getY());

                if (lastX != -1 && lastY != -1) {
                    g.drawLine(lastX, lastY, x, y);
                }

                lastX = x;
                lastY = y;
            }
            g.dispose();
        }
    }

    public MapHandler(Database db, JLabel errorLabel) {
        this.db = db;
        this.errorLabel = errorLabel;
        this.mapViewer = createMapViewer();
    }

    private JXMapViewer createMapViewer() {
        TileFactoryInfo info = new OSMTileFactoryInfo("OpenStreetMap", "https://tile.openstreetmap.org");
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        JXMapViewer mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);

        GeoPosition roma = new GeoPosition(41.9028, 12.4964);
        mapViewer.setZoom(5);
        mapViewer.setAddressLocation(roma);
        mapViewer.setPreferredSize(new Dimension(500, 400));

        MouseInputListener mil = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mil);
        mapViewer.addMouseMotionListener(mil);
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        mapViewer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Rectangle rect = mapViewer.getViewportBounds();
                Point clickPoint = e.getPoint();

                for (Waypoint w : currentWaypoints) {
                    if (w instanceof LabeledWaypoint) {
                        Point2D point = mapViewer.getTileFactory().geoToPixel(w.getPosition(), mapViewer.getZoom());

                        int x = (int) (point.getX() - rect.getX());
                        int y = (int) (point.getY() - rect.getY());
                        Point waypointPoint = new Point(x, y);

                        if (clickPoint.distance(waypointPoint) < 20) {
                            JPopupMenu popup = new JPopupMenu();
                            popup.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                            JLabel infoLabel = new JLabel(((LabeledWaypoint) w).getLabel());
                            infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                            infoLabel.setBackground(Color.WHITE);
                            infoLabel.setOpaque(true);

                            popup.add(infoLabel);
                            popup.show(mapViewer, e.getX(), e.getY());

                            return;
                        }
                    }
                }
            }
        });

        return mapViewer;
    }

    public void setupKeyboardZoom() {
        InputMap inputMap = mapViewer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = mapViewer.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "zoomIn");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "zoomOut");

        actionMap.put("zoomIn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapViewer.setZoom(mapViewer.getZoom() - 1);
            }
        });

        actionMap.put("zoomOut", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapViewer.setZoom(mapViewer.getZoom() + 1);
            }
        });

        mapViewer.addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            mapViewer.setZoom(mapViewer.getZoom() - notches);
        });
    }

    public void showStopOnMap(Stop stop) {
        GeoPosition position = new GeoPosition(stop.getLatitude(), stop.getLongitude());
        mapViewer.setAddressLocation(position);
        mapViewer.setZoom(2);

        currentWaypoints.clear();
        currentWaypoints.add(new LabeledWaypoint(position, stop.getId(), stop.getName()));

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(currentWaypoints);
        waypointPainter.setRenderer((g, map, wp) -> {
            Point2D p = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
            g.setFont(new Font("SansSerif", Font.PLAIN, 40));
            g.drawString("📍", (int)p.getX() - 10, (int)p.getY());
        });

        mapViewer.setOverlayPainter(waypointPainter);

        mapViewer.revalidate();
        mapViewer.repaint();
    }

    public void showRouteDirectionOnMap(Route route, int direction) throws SQLException {
        List<Stop> fermate = db.getStopsByRouteByDirection(route.getId(), direction);

        if (fermate.isEmpty()) {
            errorLabel.setText("Nessun percorso trovato per questa direzione.");
            errorLabel.setVisible(true);
            return;
        }

        currentWaypoints.clear();
        List<GeoPosition> track = new ArrayList<>();

        for (Stop fermata : fermate) {
            GeoPosition pos = new GeoPosition(fermata.getLatitude(), fermata.getLongitude());
            track.add(pos);
            currentWaypoints.add(new LabeledWaypoint(pos, fermata.getId(), fermata.getName()));
        }

        RoutePainter routePainter = new RoutePainter(track);

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(currentWaypoints);

        waypointPainter.setRenderer((g, map, wp) -> {
            Point2D p = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
            g.setFont(new Font("SansSerif", Font.PLAIN, 40));
            g.drawString("📍", (int)p.getX() - 10, (int)p.getY());
        });

        CompoundPainter<JXMapViewer> compoundPainter = new CompoundPainter<>(Arrays.asList(routePainter, waypointPainter));
        mapViewer.setOverlayPainter(compoundPainter);

        Stop firstStop = fermate.getFirst();
        GeoPosition centerPos = new GeoPosition(firstStop.getLatitude(), firstStop.getLongitude());
        mapViewer.setAddressLocation(centerPos);
        mapViewer.setZoom(5);

        mapViewer.revalidate();
        mapViewer.repaint();
    }

    public JXMapViewer getMapViewer() {
        return mapViewer;
    }
}