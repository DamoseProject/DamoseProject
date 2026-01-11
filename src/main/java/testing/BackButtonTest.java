package testing;

import gui.BackButton;
import gui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class BackButtonTest extends JFrame {
    public static void main(String[] args) {
        // Crea la finestra principale
        JFrame frame = new JFrame("Test BackButton");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new FlowLayout());

        // Istanzia un MainFrame "finto" solo per soddisfare il costruttore
        MainFrame fakeMainFrame = null; // o crea una mock class se serve

        // Crea il pulsante con un’azione di test
        BackButton backButton = new BackButton(fakeMainFrame, () -> {
            System.out.println("Pulsante 'Indietro' cliccato!");
            JOptionPane.showMessageDialog(frame, "Hai cliccato 'Indietro'!");
        });

        // Aggiungi il pulsante alla finestra
        frame.add(backButton);

        // Rendi visibile la finestra
        frame.setVisible(true);
    }
}
