package proyectofinal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ProyectoFinal {
    public static void main(String[] args) {
        // Interfaz inicial para escoger el módulo
        Object[] opciones = {"Administración", "Cajero Automático"};
        int eleccion = JOptionPane.showOptionDialog(null, 
                "Seleccione el módulo al que desea ingresar:", 
                "Inicio - Sistema Bancario", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, opciones, opciones[0]);

        if (eleccion == 0) {
            // Abre la ventana de Administración
            SwingUtilities.invokeLater(() -> new AdminGUI().setVisible(true));
        } else if (eleccion == 1) {
            // Como CajeroGUI "no existe aún", mostramos un mensaje temporal
            JOptionPane.showMessageDialog(null, "Módulo de cajero en desarrollo...");
        } else {
            System.exit(0); // Cierra si presiona la 'X'
        }
    }
}