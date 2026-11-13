package proyectofinal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ProyectoFinal {
    public static void main(String[] args) {
        
        // 1. Prueba de conexión a la base de datos remota
        ConexionDB.obtenerConexion(); 
        
        // 2. Menú principal
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
            // Abre el Cajero Automático
            SwingUtilities.invokeLater(() -> new CajeroGUI().setVisible(true));
        } else {
            System.exit(0);
        }
    }
}