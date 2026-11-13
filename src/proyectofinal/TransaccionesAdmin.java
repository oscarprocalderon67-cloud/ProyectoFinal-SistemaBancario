package proyectofinal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransaccionesAdmin {

    // ==========================================
    // SECCIÓN 1: TRANSACCIONES DE CLIENTES
    // ==========================================
    
    // Obtiene los clientes para llenar la tabla. Tipo: 0=Todos, 1=Por Nombre, 2=Por ID
    public static List<Object[]> obtenerClientes(int tipoBusqueda, String filtro) throws SQLException {
        List<Object[]> lista = new ArrayList<>();
        Connection con = ConexionDB.obtenerConexion();
        String sql = "SELECT * FROM clientes";
        
        if (tipoBusqueda == 1) sql += " WHERE nombre LIKE ?";
        else if (tipoBusqueda == 2) sql += " WHERE id = ?";
        
        PreparedStatement ps = con.prepareStatement(sql);
        if (tipoBusqueda == 1) ps.setString(1, "%" + filtro + "%");
        else if (tipoBusqueda == 2) ps.setInt(1, Integer.parseInt(filtro));
        
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            lista.add(new Object[]{ rs.getInt("id"), rs.getString("nombre"), rs.getString("apellido"), 
                rs.getString("telefono"), rs.getString("ciudad"), rs.getInt("ncuenta"), 
                rs.getDouble("saldo"), rs.getInt("estado") });
        }
        return lista;
    }

    public static void crearCliente(String nom, String ape, String tel, String ciu, int ncta, double saldo) throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        String sql = "INSERT INTO clientes (nombre, apellido, telefono, ciudad, ncuenta, saldo, estado) VALUES (?,?,?,?,?,?,0)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, nom); ps.setString(2, ape); ps.setString(3, tel);
        ps.setString(4, ciu); ps.setInt(5, ncta); ps.setDouble(6, saldo);
        ps.executeUpdate();
    }

    public static void editarCliente(int id, String nom, String ape, String tel, String ciu, int ncta, double saldo) throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        String sql = "UPDATE clientes SET nombre=?, apellido=?, telefono=?, ciudad=?, ncuenta=?, saldo=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, nom); ps.setString(2, ape); ps.setString(3, tel);
        ps.setString(4, ciu); ps.setInt(5, ncta); ps.setDouble(6, saldo); ps.setInt(7, id);
        ps.executeUpdate();
    }

    public static void eliminarCliente(int id) throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        con.prepareStatement("DELETE FROM clientes WHERE id=" + id).executeUpdate();
    }

    public static void transaccionSaldo(int id, double monto, boolean esConsignacion) throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        String operador = esConsignacion ? "+" : "-";
        PreparedStatement ps = con.prepareStatement("UPDATE clientes SET saldo = saldo " + operador + " ? WHERE id=?");
        ps.setDouble(1, monto); ps.setInt(2, id);
        ps.executeUpdate();
    }

    public static String obtenerEstadisticas() throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        ResultSet rs = con.prepareStatement("SELECT COUNT(*) AS total, AVG(saldo) AS promedio FROM clientes").executeQuery();
        if(rs.next()) {
            return "Total Clientes: " + rs.getInt("total") + "\nSaldo Promedio: $" + rs.getDouble("promedio");
        }
        return "Sin datos.";
    }

    // ==========================================
    // SECCIÓN 2: TRANSACCIONES DE CAJEROS
    // ==========================================
    
    public static List<Object[]> obtenerCajeros() throws SQLException {
        List<Object[]> lista = new ArrayList<>();
        Connection con = ConexionDB.obtenerConexion();
        ResultSet rs = con.prepareStatement("SELECT * FROM cajeros").executeQuery();
        while(rs.next()) {
            lista.add(new Object[]{ rs.getInt("id"), rs.getInt("ndiez"), rs.getInt("nveinte"), 
                rs.getInt("ncincuenta"), rs.getInt("ncien"), rs.getInt("estado") });
        }
        return lista;
    }

    public static void crearCajero(int n10, int n20, int n50, int n100) throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        String sql = "INSERT INTO cajeros (ndiez, nveinte, ncincuenta, ncien, estado) VALUES (?,?,?,?,0)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, n10); ps.setInt(2, n20); ps.setInt(3, n50); ps.setInt(4, n100);
        ps.executeUpdate();
    }

    public static void editarCajero(int id, int n10, int n20, int n50, int n100) throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        String sql = "UPDATE cajeros SET ndiez=?, nveinte=?, ncincuenta=?, ncien=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, n10); ps.setInt(2, n20); ps.setInt(3, n50); ps.setInt(4, n100); ps.setInt(5, id);
        ps.executeUpdate();
    }

    public static void eliminarCajero(int id) throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        con.prepareStatement("DELETE FROM cajeros WHERE id=" + id).executeUpdate();
    }

    public static void modificarBilletes(int id, String campo, String operador, int cant) throws SQLException {
        Connection con = ConexionDB.obtenerConexion();
        String sql = "UPDATE cajeros SET " + campo + " = " + campo + " " + operador + " ? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, cant); ps.setInt(2, id);
        ps.executeUpdate();
    }
}