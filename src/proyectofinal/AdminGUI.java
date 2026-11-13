package proyectofinal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminGUI extends JFrame {

    private DefaultTableModel modClientes, modCajeros;
    private JTable tblClientes, tblCajeros;
    private JTextField txtIdC, txtNombre, txtApellido, txtTelefono, txtCiudad, txtCuenta, txtSaldo;
    private JTextField txtIdCaj, txt10k, txt20k, txt50k, txt100k;

    public AdminGUI() {
        setTitle("Módulo de Administración Completo - Sistema Bancario");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.addTab("Gestión de Clientes", crearPanelClientes());
        pestañas.addTab("Gestión de Cajeros", crearPanelCajeros());
        add(pestañas);

        // Cargar datos al abrir
        actualizarTablaClientes(0, "");
        actualizarTablaCajeros();
    }

    // ==========================================
    // PESTAÑA CLIENTES
    // ==========================================
    private JPanel crearPanelClientes() {
        JPanel panel = new JPanel(new BorderLayout());

        // --- Buscadores ---
        JPanel pNorte = new JPanel();
        JTextField txtBuscar = new JTextField(15);
        JButton btnBuscarNombre = new JButton("Buscar Nombre"); 
        JButton btnBuscarID = new JButton("Buscar ID");         
        JButton btnListar = new JButton("Ver Todos");           
        JButton btnStats = new JButton("Estadísticas");         
        
        pNorte.add(new JLabel("Búsqueda:")); pNorte.add(txtBuscar);
        pNorte.add(btnBuscarNombre); pNorte.add(btnBuscarID); 
        pNorte.add(btnListar); pNorte.add(btnStats);
        panel.add(pNorte, BorderLayout.NORTH);

        // --- Tabla ---
        modClientes = new DefaultTableModel(new String[]{"ID", "Nombre", "Apellido", "Teléfono", "Ciudad", "N.Cuenta", "Saldo", "Estado"}, 0);
        tblClientes = new JTable(modClientes);
        panel.add(new JScrollPane(tblClientes), BorderLayout.CENTER);

        tblClientes.getSelectionModel().addListSelectionListener(e -> llenarFormularioCliente());

        // --- Formulario y Botones ---
        JPanel pSur = new JPanel(new BorderLayout());
        JPanel pForm = new JPanel(new GridLayout(2, 7, 2, 2));
        
        txtIdC = new JTextField(); txtIdC.setEditable(false);
        txtNombre = new JTextField(); txtApellido = new JTextField();
        txtTelefono = new JTextField(); txtCiudad = new JTextField();
        txtCuenta = new JTextField(); txtSaldo = new JTextField();

        pForm.add(new JLabel("ID")); pForm.add(new JLabel("Nombre")); pForm.add(new JLabel("Apellido"));
        pForm.add(new JLabel("Teléfono")); pForm.add(new JLabel("Ciudad")); pForm.add(new JLabel("Cuenta")); pForm.add(new JLabel("Saldo"));
        pForm.add(txtIdC); pForm.add(txtNombre); pForm.add(txtApellido);
        pForm.add(txtTelefono); pForm.add(txtCiudad); pForm.add(txtCuenta); pForm.add(txtSaldo);

        JPanel pBotones = new JPanel();
        JButton btnAgregar = new JButton("Agregar"); JButton btnEditar = new JButton("Editar"); JButton btnEliminar = new JButton("Eliminar");   
        JButton btnConsignar = new JButton("Consignar"); JButton btnRetirar = new JButton("Retirar");     
        
        btnConsignar.setBackground(Color.CYAN); btnRetirar.setBackground(Color.ORANGE);
        pBotones.add(btnAgregar); pBotones.add(btnEditar); pBotones.add(btnEliminar); pBotones.add(btnConsignar); pBotones.add(btnRetirar);

        pSur.add(pForm, BorderLayout.CENTER); pSur.add(pBotones, BorderLayout.SOUTH);
        panel.add(pSur, BorderLayout.SOUTH);

        // --- EVENTOS QUE LLAMAN A TransaccionesAdmin ---
        btnListar.addActionListener(e -> actualizarTablaClientes(0, ""));
        btnBuscarNombre.addActionListener(e -> actualizarTablaClientes(1, txtBuscar.getText()));
        btnBuscarID.addActionListener(e -> actualizarTablaClientes(2, txtBuscar.getText()));

        btnStats.addActionListener(e -> {
            try { JOptionPane.showMessageDialog(this, TransaccionesAdmin.obtenerEstadisticas()); } 
            catch (Exception ex) { mostrarError(ex); }
        });

        btnAgregar.addActionListener(e -> {
            try {
                TransaccionesAdmin.crearCliente(txtNombre.getText(), txtApellido.getText(), txtTelefono.getText(), 
                        txtCiudad.getText(), Integer.parseInt(txtCuenta.getText()), Double.parseDouble(txtSaldo.getText()));
                actualizarTablaClientes(0, ""); limpiarFormularioC();
            } catch (Exception ex) { mostrarError(ex); }
        });

        btnEditar.addActionListener(e -> {
            try {
                TransaccionesAdmin.editarCliente(Integer.parseInt(txtIdC.getText()), txtNombre.getText(), txtApellido.getText(), 
                        txtTelefono.getText(), txtCiudad.getText(), Integer.parseInt(txtCuenta.getText()), Double.parseDouble(txtSaldo.getText()));
                actualizarTablaClientes(0, "");
            } catch (Exception ex) { mostrarError(ex); }
        });

        btnEliminar.addActionListener(e -> {
            try {
                TransaccionesAdmin.eliminarCliente(Integer.parseInt(txtIdC.getText()));
                actualizarTablaClientes(0, ""); limpiarFormularioC();
            } catch (Exception ex) { mostrarError(ex); }
        });

        btnConsignar.addActionListener(e -> gestionarSaldo("Consignar", true));
        btnRetirar.addActionListener(e -> gestionarSaldo("Retirar", false));

        return panel;
    }

    // ==========================================
    // PESTAÑA CAJEROS
    // ==========================================
    private JPanel crearPanelCajeros() {
        JPanel panel = new JPanel(new BorderLayout());

        // --- Tabla ---
        modCajeros = new DefaultTableModel(new String[]{"ID", "Billetes 10k", "Billetes 20k", "Billetes 50k", "Billetes 100k", "Estado"}, 0);
        tblCajeros = new JTable(modCajeros);
        panel.add(new JScrollPane(tblCajeros), BorderLayout.CENTER);

        tblCajeros.getSelectionModel().addListSelectionListener(e -> llenarFormularioCajero());

        // --- Formulario ---
        JPanel pSur = new JPanel(new BorderLayout());
        JPanel pForm = new JPanel(new GridLayout(2, 5, 2, 2));
        
        txtIdCaj = new JTextField(); txtIdCaj.setEditable(false);
        txt10k = new JTextField(); txt20k = new JTextField(); txt50k = new JTextField(); txt100k = new JTextField();

        pForm.add(new JLabel("ID")); pForm.add(new JLabel("N. 10.000")); pForm.add(new JLabel("N. 20.000")); pForm.add(new JLabel("N. 50.000")); pForm.add(new JLabel("N. 100.000"));
        pForm.add(txtIdCaj); pForm.add(txt10k); pForm.add(txt20k); pForm.add(txt50k); pForm.add(txt100k);

        JPanel pBotones = new JPanel();
        JButton btnAgregar = new JButton("Agregar"); JButton btnEditar = new JButton("Editar"); JButton btnEliminar = new JButton("Eliminar");
        JButton btnAddBill = new JButton("Ingresar Billetes"); JButton btnQuitarBill = new JButton("Retirar Billetes");
        
        btnAddBill.setBackground(Color.GREEN); btnQuitarBill.setBackground(Color.RED); btnQuitarBill.setForeground(Color.WHITE);

        pBotones.add(btnAgregar); pBotones.add(btnEditar); pBotones.add(btnEliminar); pBotones.add(btnAddBill); pBotones.add(btnQuitarBill);
        pSur.add(pForm, BorderLayout.CENTER); pSur.add(pBotones, BorderLayout.SOUTH);
        panel.add(pSur, BorderLayout.SOUTH);

        // --- EVENTOS QUE LLAMAN A TransaccionesAdmin ---
        btnAgregar.addActionListener(e -> {
            try {
                TransaccionesAdmin.crearCajero(Integer.parseInt(txt10k.getText()), Integer.parseInt(txt20k.getText()), 
                        Integer.parseInt(txt50k.getText()), Integer.parseInt(txt100k.getText()));
                actualizarTablaCajeros(); limpiarFormularioCaj();
            } catch (Exception ex) { mostrarError(ex); }
        });

        btnEditar.addActionListener(e -> {
            try {
                TransaccionesAdmin.editarCajero(Integer.parseInt(txtIdCaj.getText()), Integer.parseInt(txt10k.getText()), 
                        Integer.parseInt(txt20k.getText()), Integer.parseInt(txt50k.getText()), Integer.parseInt(txt100k.getText()));
                actualizarTablaCajeros();
            } catch (Exception ex) { mostrarError(ex); }
        });

        btnEliminar.addActionListener(e -> {
            try {
                TransaccionesAdmin.eliminarCajero(Integer.parseInt(txtIdCaj.getText()));
                actualizarTablaCajeros(); limpiarFormularioCaj();
            } catch (Exception ex) { mostrarError(ex); }
        });

        btnAddBill.addActionListener(e -> gestionarBilletes("Ingresar", "+"));
        btnQuitarBill.addActionListener(e -> gestionarBilletes("Quitar", "-"));

        return panel;
    }

    // ==========================================
    // METODOS AUXILIARES Y DE COMUNICACIÓN
    // ==========================================
    
    private void actualizarTablaClientes(int tipo, String filtro) {
        modClientes.setRowCount(0);
        try {
            List<Object[]> datos = TransaccionesAdmin.obtenerClientes(tipo, filtro);
            for (Object[] fila : datos) modClientes.addRow(fila);
        } catch (Exception e) { System.out.println("Error actualizando clientes."); }
    }

    private void actualizarTablaCajeros() {
        modCajeros.setRowCount(0);
        try {
            List<Object[]> datos = TransaccionesAdmin.obtenerCajeros();
            for (Object[] fila : datos) modCajeros.addRow(fila);
        } catch (Exception e) { System.out.println("Error actualizando cajeros."); }
    }

    private void gestionarSaldo(String accion, boolean esConsignacion) {
        if(txtIdC.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Seleccione un cliente."); return; }
        
        String input = JOptionPane.showInputDialog("Monto a " + accion.toLowerCase() + " para " + txtNombre.getText() + ":");
        if(input != null && !input.isEmpty()) {
            try {
                double monto = Double.parseDouble(input);
                if (!esConsignacion && Double.parseDouble(txtSaldo.getText()) < monto) {
                    JOptionPane.showMessageDialog(this, "Saldo insuficiente."); return;
                }
                TransaccionesAdmin.transaccionSaldo(Integer.parseInt(txtIdC.getText()), monto, esConsignacion);
                actualizarTablaClientes(0, "");
            } catch (Exception ex) { mostrarError(ex); }
        }
    }

    private void gestionarBilletes(String accion, String operador) {
        if(txtIdCaj.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Seleccione un cajero."); return; }
        
        String[] op = {"10.000", "20.000", "50.000", "100.000"};
        String denom = (String) JOptionPane.showInputDialog(this, "Denominación a " + accion + ":", "Billetes", JOptionPane.QUESTION_MESSAGE, null, op, op[0]);
        if (denom != null) {
            String cantStr = JOptionPane.showInputDialog("¿Cuántos billetes de " + denom + "?");
            if (cantStr != null && !cantStr.isEmpty()) {
                String campo = denom.equals("10.000") ? "ndiez" : denom.equals("20.000") ? "nveinte" : denom.equals("50.000") ? "ncincuenta" : "ncien";
                try {
                    TransaccionesAdmin.modificarBilletes(Integer.parseInt(txtIdCaj.getText()), campo, operador, Integer.parseInt(cantStr));
                    actualizarTablaCajeros();
                } catch (Exception ex) { mostrarError(ex); }
            }
        }
    }

    private void llenarFormularioCliente() {
        int f = tblClientes.getSelectedRow();
        if (f != -1) {
            txtIdC.setText(modClientes.getValueAt(f, 0).toString()); txtNombre.setText(modClientes.getValueAt(f, 1).toString());
            txtApellido.setText(modClientes.getValueAt(f, 2).toString()); txtTelefono.setText(modClientes.getValueAt(f, 3).toString());
            txtCiudad.setText(modClientes.getValueAt(f, 4).toString()); txtCuenta.setText(modClientes.getValueAt(f, 5).toString());
            txtSaldo.setText(modClientes.getValueAt(f, 6).toString());
        }
    }

    private void llenarFormularioCajero() {
        int f = tblCajeros.getSelectedRow();
        if (f != -1) {
            txtIdCaj.setText(modCajeros.getValueAt(f, 0).toString()); txt10k.setText(modCajeros.getValueAt(f, 1).toString());
            txt20k.setText(modCajeros.getValueAt(f, 2).toString()); txt50k.setText(modCajeros.getValueAt(f, 3).toString());
            txt100k.setText(modCajeros.getValueAt(f, 4).toString());
        }
    }

    private void limpiarFormularioC() { txtIdC.setText(""); txtNombre.setText(""); txtApellido.setText(""); txtTelefono.setText(""); txtCiudad.setText(""); txtCuenta.setText(""); txtSaldo.setText(""); }
    private void limpiarFormularioCaj() { txtIdCaj.setText(""); txt10k.setText(""); txt20k.setText(""); txt50k.setText(""); txt100k.setText(""); }
    private void mostrarError(Exception ex) { JOptionPane.showMessageDialog(this, "Asegúrese de llenar todos los campos correctamente."); }
}