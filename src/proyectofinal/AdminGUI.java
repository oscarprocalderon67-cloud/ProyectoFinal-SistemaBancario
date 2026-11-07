
package proyectofinal;

import javax.swing.*;
import java.awt.*;

public class AdminGUI extends JFrame {
    
    public AdminGUI() {
        setTitle("Panel de Administración - Sistema Bancario");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Crear pestañas para Clientes y Cajeros
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pestaña Clientes
        JPanel panelClientes = new JPanel(new BorderLayout());
        String[] columnasClientes = {"ID", "Nombre", "Apellido", "Teléfono", "Ciudad", "N. Cuenta", "Saldo", "Estado"};
        JTable tablaClientes = new JTable(new Object[0][0], columnasClientes); 
        panelClientes.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
        
        JPanel panelBotonesClientes = new JPanel();
        panelBotonesClientes.add(new JButton("Agregar Cliente"));
        panelBotonesClientes.add(new JButton("Editar Cliente"));
        panelBotonesClientes.add(new JButton("Eliminar Cliente"));
        panelBotonesClientes.add(new JButton("Consignar/Retirar Manual"));
        panelClientes.add(panelBotonesClientes, BorderLayout.SOUTH);
        
        // Pestaña Cajeros
        JPanel panelCajeros = new JPanel(new BorderLayout());
        String[] columnasCajeros = {"ID", "Billetes 10k", "Billetes 20k", "Billetes 50k", "Billetes 100k", "Estado"};
        JTable tablaCajeros = new JTable(new Object[0][0], columnasCajeros);
        panelCajeros.add(new JScrollPane(tablaCajeros), BorderLayout.CENTER);
        
        JPanel panelBotonesCajeros = new JPanel();
        panelBotonesCajeros.add(new JButton("Agregar Cajero"));
        panelBotonesCajeros.add(new JButton("Editar Billetes"));
        panelCajeros.add(panelBotonesCajeros, BorderLayout.SOUTH);
        
        // Pestaña Estadísticas
        JPanel panelEstadisticas = new JPanel(new GridLayout(2, 1));
        panelEstadisticas.add(new JLabel("Total Clientes: (Cargando...)", SwingConstants.CENTER));
        panelEstadisticas.add(new JLabel("Promedio de Dinero: (Cargando...)", SwingConstants.CENTER));
        
        tabbedPane.addTab("Gestión de Clientes", panelClientes);
        tabbedPane.addTab("Gestión de Cajeros", panelCajeros);
        tabbedPane.addTab("Estadísticas", panelEstadisticas);
        
        add(tabbedPane);
    }
}