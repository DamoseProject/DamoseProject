package gui;

import model.Database;
import model.Route;
import model.Stop;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class FavoritesManager {

    private static final String EMPTY_STAR = "<html>&#9734;</html>";
    private static final String FILLED_STAR = "<html>&#9733;</html>";

    private final Database db;
    private final JLabel errorLabel;
    private final ButtonMapPageConfig config;

    public FavoritesManager(Database db, JLabel errorLabel, ButtonMapPageConfig config) {
        this.db = db;
        this.errorLabel = errorLabel;
        this.config = config;
    }

    public JButton createFavButtonForStop(Stop stop) {
        String initialIcon = getInitialIconForStop(stop);
        JButton favButton = UIComponentFactory.createSymbolButton(initialIcon, 15);

        favButton.addActionListener(e -> handleStopFavoriteToggle(favButton, stop));
        return favButton;
    }

    public JButton createFavButtonForRoute(Route route) {
        String initialIcon = getInitialIconForRoute(route);
        JButton favButton = UIComponentFactory.createSymbolButton(initialIcon, 15);

        favButton.addActionListener(e -> handleRouteFavoriteToggle(favButton, route));
        return favButton;
    }

    private String getInitialIconForStop(Stop stop) {
        UserSession session = UserSession.getInstance();
        if (!session.isLogged()) return EMPTY_STAR;

        try {
            User user = db.getUser(session.getUserId());
            List<Stop> userFavorites = db.getFavouriteStopsByUser(user);
            for (Stop s : userFavorites) {
                if (s == null) continue;
                if (s.getId().equals(stop.getId())) {
                    return FILLED_STAR;
                }
            }
        } catch (SQLException e) {
            showError(Constants.FAVORITES_RETRIEVAL_ERROR, Color.RED);
        }
        return EMPTY_STAR;
    }

    private String getInitialIconForRoute(Route route) {
        UserSession session = UserSession.getInstance();
        if (!session.isLogged()) return EMPTY_STAR;

        try {
            User user = db.getUser(session.getUserId());
            List<Route> userFavorites = db.getFavouriteRoutesByUser(user);
            for (Route r : userFavorites) {
                if (r.getId().equals(route.getId())) {
                    return FILLED_STAR;
                }
            }
        } catch (SQLException e) {
            showError(Constants.FAVORITES_RETRIEVAL_ERROR, Color.RED);
        }
        return EMPTY_STAR;
    }

    private void handleStopFavoriteToggle(JButton favButton, Stop stop) {
        UserSession session = UserSession.getInstance();

        if (!validateFavoriteAction(session)) return;

        try {
            User user = db.getUser(session.getUserId());
            if (user == null) throw new SQLException(Constants.USER_NOT_FOUND);

            if (favButton.getText().contains("9734")) {
                db.addUserFavouriteStop(user, stop);
                favButton.setText(FILLED_STAR);
                showError(Constants.FAV_ADDED + stop.getName(), new Color(0, 100, 0));
            } else {
                db.removeUserFavouriteStop(user, stop);
                favButton.setText(EMPTY_STAR);
                showError(Constants.FAV_REMOVED + stop.getName(), new Color(255, 140, 0));
            }
        } catch (SQLException ex) {
            showError(Constants.FAV_UPDATE_ERROR, Color.RED);
        }
    }

    private void handleRouteFavoriteToggle(JButton favButton, Route route) {
        UserSession session = UserSession.getInstance();

        if (!validateFavoriteAction(session)) return;

        try {
            User user = db.getUser(session.getUserId());
            if (user == null) throw new SQLException(Constants.USER_NOT_FOUND);

            if (favButton.getText().contains("9734")) {
                db.addUserFavouriteRoute(user, route);
                favButton.setText(FILLED_STAR);
                showError(Constants.FAV_ADDED + route.getShortName(), new Color(0, 100, 0));
            } else {
                db.removeUserFavouriteRoute(user, route);
                favButton.setText(EMPTY_STAR);
                showError(Constants.FAV_REMOVED + route.getShortName(), new Color(255, 140, 0));
            }
        } catch (SQLException ex) {
            showError(Constants.FAV_UPDATE_ERROR, Color.RED);
        }
    }

    private boolean validateFavoriteAction(UserSession session) {
        if (!session.isLogged()) {
            showError(Constants.LOGIN_REQUIRED_FAVORITES, Color.RED);
            return false;
        }

        if (!config.isFavoritesEnabled()) {
            showError(config.getFavoritesErrorMessage(), Color.RED);
            return false;
        }

        return true;
    }

    private void showError(String message, Color color) {
        errorLabel.setForeground(color);
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}