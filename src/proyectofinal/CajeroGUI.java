package proyectofinal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CajeroGUI extends JFrame {
    
    private JTextArea pantalla;
    private int pasoActual = 0; 
    
    private int idCajeroLogueado = -1;
    private int idClienteLogueado = -1; 
    private String nombreClienteLogueado = "";
    private int cuentaDestino = -1; 
    
    private String mensajeFijo = "BIENVENIDO\nIngrese ID del Cajero:";
    private String inputBuffer = ""; 

    public CajeroGUI() {
        setTitle("Cajero Automático");
        setSize(480, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Liberar BD al cerrar
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                TransaccionesCajero.liberarEstados(idCajeroLogueado, idClienteLogueado);
            }
        });

        // 1. PANTALLA
        pantalla = new JTextArea(10, 20);
        pantalla.setEditable(false); 
        pantalla.setFont(new Font("Monospaced", Font.BOLD, 18));
        pantalla.setBackground(new Color(0, 50, 0)); 
        pantalla.setForeground(Color.GREEN);
        actualizarPantalla();
        add(new JScrollPane(pantalla), BorderLayout.NORTH);

        // 2. TECLADO
        JPanel panelTeclado = new JPanel(new GridLayout(4, 4, 5, 5));
        panelTeclado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (int i = 1; i <= 9; i++) {
            JButton btnNum = new JButton(String.valueOf(i));
            btnNum.setFont(new Font("Arial", Font.BOLD, 20));
            btnNum.addActionListener(e -> { inputBuffer += btnNum.getText(); actualizarPantalla(); });
            panelTeclado.add(btnNum);
        }
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(Color.YELLOW);
        btnLimpiar.addActionListener(e -> { inputBuffer = ""; actualizarPantalla(); });
        
        JButton btnCero = new JButton("0");
        btnCero.setFont(new Font("Arial", Font.BOLD, 20));
        btnCero.addActionListener(e -> { inputBuffer += "0"; actualizarPantalla(); });
        
        JButton btnRetroceso = new JButton("<- Borrar");
        btnRetroceso.addActionListener(e -> {
            if (inputBuffer.length() > 0) { inputBuffer = inputBuffer.substring(0, inputBuffer.length() - 1); actualizarPantalla(); }
        });
        
        panelTeclado.add(btnLimpiar); panelTeclado.add(btnCero); panelTeclado.add(btnRetroceso);
        
        // 3. BOTONES DE ACCIÓN (Estrictamente los solicitados en el taller)
        JPanel panelAcciones = new JPanel(new GridLayout(5, 1, 5, 5)); // Cambiado a 5 botones
        panelAcciones.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        
        JButton btnAceptar = new JButton("ACEPTAR");
        JButton btnRetirar = new JButton("RETIRAR");
        JButton btnConsignar = new JButton("CONSIGNAR"); 
        JButton btnSaldo = new JButton("CONSULTAR SALDO");
        JButton btnCancelar = new JButton("CANCELAR OP."); 
        
        btnAceptar.setBackground(Color.LIGHT_GRAY); 
        btnRetirar.setBackground(Color.ORANGE);
        btnConsignar.setBackground(Color.CYAN); 
        btnCancelar.setBackground(Color.RED); 
        btnCancelar.setForeground(Color.WHITE);
        
        panelAcciones.add(btnAceptar); 
        panelAcciones.add(btnRetirar); 
        panelAcciones.add(btnConsignar);
        panelAcciones.add(btnSaldo); 
        panelAcciones.add(btnCancelar);
        add(panelTeclado, BorderLayout.CENTER); 
        add(panelAcciones, BorderLayout.EAST);
        
        // --- EVENTOS QUE LLAMAN A LA CLASE TransaccionesCajero ---
        btnAceptar.addActionListener(e -> procesarAceptar());
        
        btnRetirar.addActionListener(e -> {
            if(pasoActual == 2) { mensajeFijo = "--- RETIRO ---\nIngrese monto a retirar:"; pasoActual = 3; inputBuffer = ""; actualizarPantalla(); }
            else if(pasoActual == 3) ejecutarRetiro(); 
        });
        
        btnConsignar.addActionListener(e -> {
            if(pasoActual == 2) { mensajeFijo = "--- CONSIGNAR ---\nIngrese cuenta destino:"; pasoActual = 4; inputBuffer = ""; actualizarPantalla(); }
            else if(pasoActual == 4) validarDestino(5, "CONSIGNAR"); 
            else if(pasoActual == 5) ejecutarConsignacion();
        });
        
        btnSaldo.addActionListener(e -> { 
            if(pasoActual == 2) {
                try {
                    double saldo = TransaccionesCajero.consultarSaldo(idClienteLogueado);
                    mensajeFijo = "Su saldo actual es: $" + saldo + "\n\nSeleccione otra operación o Cancele.";
                    inputBuffer = ""; actualizarPantalla();
                } catch(Exception ex) {}
            }
        });
        
        btnCancelar.addActionListener(e -> reiniciarYLiberar(true));
    }
    
    private void actualizarPantalla() {
        pantalla.setText(mensajeFijo + "\n\n> " + inputBuffer);
    }
    
    // --- LÓGICA DE INTERFAZ SEPARADA DE SQL ---
    
    private void procesarAceptar() {
        if (inputBuffer.isEmpty()) return;
        try {
            if (pasoActual == 0) { // Validar Cajero
                int idCaj = Integer.parseInt(inputBuffer);
                if (TransaccionesCajero.verificarYBloquearCajero(idCaj)) {
                    idCajeroLogueado = idCaj;
                    mensajeFijo = "Cajero verificado.\nIngrese Número de Cuenta:";
                    pasoActual = 1; inputBuffer = ""; actualizarPantalla();
                } else mostrarError("Cajero ocupado o no existe.");
            } 
            else if (pasoActual == 1) { // Validar Cuenta
                Cliente cl = TransaccionesCajero.verificarYBloquearCuenta(Integer.parseInt(inputBuffer));
                if (cl != null) {
                    idClienteLogueado = cl.getId();
                    nombreClienteLogueado = cl.getNombre();
                    pasoActual = 2; inputBuffer = "";
                    mensajeFijo = "Cliente: " + nombreClienteLogueado + "\n\nSeleccione una operación (Botones)";
                    actualizarPantalla();
                } else mostrarError("Cuenta no existe u ocupada.");
            }
        } catch (Exception ex) { mostrarError("Error en base de datos."); }
    }
    
    private void validarDestino(int siguientePaso, String accion) {
        try {
            cuentaDestino = Integer.parseInt(inputBuffer);
            String nombre = TransaccionesCajero.obtenerNombreDestino(cuentaDestino);
            if (nombre != null) {
                pasoActual = siguientePaso;
                mensajeFijo = "Destinatario: " + nombre + "\n\nIngrese monto a " + accion.toLowerCase() + ":";
                inputBuffer = ""; actualizarPantalla();
            } else mostrarError("Cuenta destino NO existe."); // Cumple regla 4 de validar que exista el cliente
        } catch (Exception e) { mostrarError("Error al validar cuenta."); }
    }

    private void ejecutarConsignacion() {
        try {
            double monto = Double.parseDouble(inputBuffer);
            if (monto <= 0) { mostrarError("Monto inválido."); return; }
            
            // Usamos la lógica de transferencia (resta de tu cuenta, suma a la destino) 
            // para cumplir la regla 3 del documento utilizando el botón de consignar.
            String resultado = TransaccionesCajero.transferir(idClienteLogueado, cuentaDestino, monto);
            
            if (resultado.equals("EXITO")) mostrarTicket("CONSIGNACIÓN EXITOSA\nA: " + cuentaDestino + "\nMonto: $" + monto);
            else mostrarError(resultado); // Muestra error si no hay saldo suficiente
        } catch (Exception e) { mostrarError("Error en consignación."); }
    }

    private void ejecutarRetiro() {
        try {
            int monto = Integer.parseInt(inputBuffer);
            String resultado = TransaccionesCajero.ejecutarRetiro(idClienteLogueado, idCajeroLogueado, monto);
            
            if (resultado.startsWith("ERROR")) mostrarError(resultado.replace("ERROR: ", ""));
            else mostrarTicket(resultado + "\nCliente: " + nombreClienteLogueado);
        } catch (Exception e) { mostrarError("Error en retiro."); }
    }
    
    private void mostrarError(String error) {
        mensajeFijo = "ERROR: " + error + "\n\n" + mensajeFijo;
        inputBuffer = ""; actualizarPantalla();
    }
    
    private void mostrarTicket(String texto) {
        mensajeFijo = "====================\n" + texto + "\n====================\n\nPresione CANCELAR para salir.";
        inputBuffer = ""; actualizarPantalla();
        TransaccionesCajero.liberarEstados(idCajeroLogueado, idClienteLogueado); 
    }

    private void reiniciarYLiberar(boolean porBoton) {
        TransaccionesCajero.liberarEstados(idCajeroLogueado, idClienteLogueado);
        idCajeroLogueado = -1; idClienteLogueado = -1; nombreClienteLogueado = ""; cuentaDestino = -1;
        pasoActual = 0; inputBuffer = "";
        mensajeFijo = "BIENVENIDO\nIngrese ID del Cajero:";
        actualizarPantalla();
    }
}