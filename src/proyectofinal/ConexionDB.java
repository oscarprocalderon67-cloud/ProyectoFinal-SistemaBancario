package proyectofinal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexionDB {
    
    // Variable estática para almacenar la única conexión (Patrón Singleton)
    private static Connection conexion;
    
    // Datos de tu servidor remoto
    private static final String URL = "jdbc:mysql://72.167.84.254:3306/banco";
    private static final String USUARIO = "oscar"; // <-- CAMBIA ESTO por tu usuario de MySQL
    private static final String CONTRASENA = "#3Programacion3"; // <-- CAMBIA ESTO por tu contraseña de MySQL

    // Constructor privado para evitar que se instancie con "new"
    private ConexionDB() {
    }

    public static Connection obtenerConexion() {
        if (conexion == null) {
            try {
                // Cargar el driver de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Establecer la conexión
                conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
                System.out.println("¡Conexión exitosa al servidor MySQL remoto!");
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Falta la librería (Driver) de MySQL en el proyecto.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error de conexión a la BD:\n" + e.getMessage());
            }
        }
        return conexion;
    }
    
    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                conexion = null;
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}