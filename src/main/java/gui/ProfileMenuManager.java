package gui;

import model.Database;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Questa classe si occupa della gestione del menu a comparsa (popup) del profilo utente.
 * Genera l'interfaccia grafica che mostra le informazioni dell'account loggato
 * (Username ed Email) e fornisce l'opzione per effettuare il logout.
 */
public class ProfileMenuManager {

    private final Database db;
    private final MainFrame frame;

    /**
     * Costruttore del manager per il menu profilo.
     * * @param db Il database da cui recuperare i dettagli dell'utente.
     * @param frame Il frame principale per gestire il cambio di pagina durante il logout.
     */
    public ProfileMenuManager(Database db, MainFrame frame) {
        this.db = db;
        this.frame = frame;
    }

    /**
     * Crea un componente {@link JPopupMenu} configurato con i dati dell'utente.
     * Mostra username ed email in etichette non cliccabili e aggiunge un
     * {@link JMenuItem} per il logout.
     * * @param session La sessione utente corrente contenente l'ID e lo username.
     * @return Il menu popup pronto per essere visualizzato.
     */
    public JPopupMenu createProfilePopupMenu(UserSession session) {
        JPopupMenu menu = new JPopupMenu();

        String userEmail = getUserEmail(session);

        JLabel userLabel = UIComponentFactory.createLabel("Utente: " + session.getUsername(), JLabel.LEFT);
        JLabel emailLabel = UIComponentFactory.createLabel("Email: " + userEmail, JLabel.LEFT);

        userLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        emailLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 5, 10));

        Font infoFont = new Font("SansSerif", Font.PLAIN, 11);
        emailLabel.setFont(infoFont);
        emailLabel.setForeground(Color.GRAY);

        JMenuItem logoutItem = new JMenuItem("Esci");
        logoutItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutItem.addActionListener(e -> {
            session.logout();
            frame.setView(PageFactory.createPage(PageType.LOGIN, frame));
        });

        menu.add(userLabel);
        menu.add(emailLabel);
        menu.add(new JSeparator());
        menu.add(logoutItem);

        return menu;
    }

    /**
     * Recupera l'indirizzo email dell'utente interrogando il database tramite l'ID di sessione.
     * * @param session La sessione dell'utente.
     * @return L'email dell'utente come stringa, oppure "Non disponibile" in caso di errore SQL.
     */
    private String getUserEmail(UserSession session) {
        String userEmail = "...";
        try {
            User user = db.getUser(session.getUserId());
            if (user != null) {
                userEmail = user.getEmail();
            }
        } catch (SQLException ex) {
            userEmail = "Non disponibile";
        }
        return userEmail;
    }
}