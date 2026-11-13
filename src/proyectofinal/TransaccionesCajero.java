package proyectofinal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransaccionesCajero {

    // Bloquea al cajero (estado=1) si está disponible
    public static boolean verificarYBloquearCajero(int idCajero) throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        PreparedStatement ps = con.prepareStatement("SELECT estado FROM cajeros WHERE id=?");
        ps.setInt(1, idCajero);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            if (rs.getInt("estado") == 0) {
                con.prepareStatement("UPDATE cajeros SET estado=1 WHERE id=" + idCajero).executeUpdate();
                return true; // Éxito
            }
        }
        return false; // Ocupado o no existe
    }

    // Busca la cuenta, si está libre la bloquea y retorna el nombre y ID del cliente
    public static Cliente verificarYBloquearCuenta(int nCuenta) throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        PreparedStatement ps = con.prepareStatement("SELECT id, nombre, estado FROM clientes WHERE ncuenta=?");
        ps.setInt(1, nCuenta);
        ResultSet rs = ps.executeQuery();
        if (rs.next() && rs.getInt("estado") == 0) {
            int idCliente = rs.getInt("id");
            con.prepareStatement("UPDATE clientes SET estado=1 WHERE id=" + idCliente).executeUpdate();
            
            Cliente cliente = new Cliente();
            cliente.setId(idCliente);
            cliente.setNombre(rs.getString("nombre"));
            return cliente;
        }
        return null;
    }

    // Verifica si la cuenta existe y trae el nombre (Para consignar/transferir)
    public static String obtenerNombreDestino(int nCuenta) throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        PreparedStatement ps = con.prepareStatement("SELECT nombre FROM clientes WHERE ncuenta=?");
        ps.setInt(1, nCuenta);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getString("nombre");
        return null;
    }

    public static double consultarSaldo(int idCliente) throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        PreparedStatement ps = con.prepareStatement("SELECT saldo FROM clientes WHERE id=?");
        ps.setInt(1, idCliente);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getDouble("saldo");
        return 0;
    }

    public static void consignar(int cuentaDestino, double monto) throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        con.prepareStatement("UPDATE clientes SET saldo = saldo + " + monto + " WHERE ncuenta=" + cuentaDestino).executeUpdate();
    }

    public static String transferir(int idOrigen, int cuentaDestino, double monto) throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        double saldoActual = consultarSaldo(idOrigen);
        if (saldoActual < monto) return "ERROR: Saldo insuficiente.";
        
        con.prepareStatement("UPDATE clientes SET saldo = saldo - " + monto + " WHERE id=" + idOrigen).executeUpdate();
        con.prepareStatement("UPDATE clientes SET saldo = saldo + " + monto + " WHERE ncuenta=" + cuentaDestino).executeUpdate();
        return "EXITO";
    }

    public static String ejecutarRetiro(int idCliente, int idCajero, int monto) throws SQLException {
        if (monto < 10000 || monto > 1000000 || monto % 10000 != 0) {
            return "ERROR: Retiro debe ser múltiplo de 10.000\n(Mín: 10.000, Máx: 1.000.000)";
        }

        Connection con = ConexionDB.obtenerConexion();
        
        // 1. Validar saldo cliente
        double saldoCliente = consultarSaldo(idCliente);
        if (saldoCliente < monto) return "ERROR: Saldo insuficiente en su cuenta.";

        // 2. Billetes del cajero
        PreparedStatement ps = con.prepareStatement("SELECT * FROM cajeros WHERE id=?");
        ps.setInt(1, idCajero);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int dbCien = rs.getInt("ncien"), dbCincuenta = rs.getInt("ncincuenta");
        int dbVeinte = rs.getInt("nveinte"), dbDiez = rs.getInt("ndiez");
        
        double totalCajero = (dbCien*100000) + (dbCincuenta*50000) + (dbVeinte*20000) + (dbDiez*10000);
        if(totalCajero < monto) return "ERROR: El cajero no tiene dinero suficiente.";

        // 3. Algoritmo de Billetes (Reglas del taller)
        int tempMonto = monto;
        int c100 = 0, c50 = 0, c20 = 0, c10 = 0;

        if (monto == 50000) { c20 = 2; c10 = 1; tempMonto = 0; } 
        else if (monto == 100000) { c50 = 1; c20 = 2; c10 = 1; tempMonto = 0; } 
        else if (monto > 100000) { c50 = 1; c20 = 2; c10 = 1; tempMonto -= 100000; }

        while(tempMonto >= 100000 && dbCien > c100) { c100++; tempMonto -= 100000; }
        while(tempMonto >= 50000 && dbCincuenta > c50) { c50++; tempMonto -= 50000; }
        while(tempMonto >= 20000 && dbVeinte > c20) { c20++; tempMonto -= 20000; }
        while(tempMonto >= 10000 && dbDiez > c10) { c10++; tempMonto -= 10000; }

        if (tempMonto > 0) return "ERROR: Cajero sin denominación exacta\npara este monto.";

        // 4. Actualizar Base de Datos
        con.prepareStatement("UPDATE clientes SET saldo = saldo - " + monto + " WHERE id=" + idCliente).executeUpdate();
        PreparedStatement psUp = con.prepareStatement("UPDATE cajeros SET ncien=ncien-?, ncincuenta=ncincuenta-?, nveinte=nveinte-?, ndiez=ndiez-? WHERE id=?");
        psUp.setInt(1, c100); psUp.setInt(2, c50); psUp.setInt(3, c20); psUp.setInt(4, c10); psUp.setInt(5, idCajero);
        psUp.executeUpdate();

        // 5. Generar Ticket
        String ticket = "RETIRO EXITOSO\n\nBilletes Entregados:\n";
        if(c100>0) ticket += c100 + " de $100.000\n";
        if(c50>0) ticket += c50 + " de $50.000\n";
        if(c20>0) ticket += c20 + " de $20.000\n";
        if(c10>0) ticket += c10 + " de $10.000\n";
        ticket += "\nNuevo Saldo: $" + (saldoCliente - monto);
        
        return ticket;
    }

    public static void liberarEstados(int idCajero, int idCliente) {
        try {
            Connection con = ConexionDB.obtenerConexion();
            if (idCajero != -1) con.prepareStatement("UPDATE cajeros SET estado=0 WHERE id=" + idCajero).executeUpdate();
            if (idCliente != -1) con.prepareStatement("UPDATE clientes SET estado=0 WHERE id=" + idCliente).executeUpdate();
        } catch (SQLException e) { System.out.println("Error liberando BD"); }
    }
}