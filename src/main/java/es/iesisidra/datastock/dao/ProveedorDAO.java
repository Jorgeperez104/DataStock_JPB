package es.iesisidra.datastock.dao;

import es.iesisidra.datastock.dao.ConexionBD;
import es.iesisidra.datastock.modelo.Proveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    public static boolean existePorId(int id) throws SQLException {
        String sql = "SELECT 1 FROM proveedor WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean existePorEmail(String email, Integer idExcluir) throws SQLException {
        if (email == null) return false;
        String sql = "SELECT 1 FROM proveedor WHERE LOWER(email) = LOWER(?)";
        if (idExcluir != null) {
            sql += " AND id != ?";
        }
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            if (idExcluir != null) {
                ps.setInt(2, idExcluir);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static Proveedor buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, email FROM proveedor WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Proveedor(rs.getInt("id"), rs.getString("nombre"), rs.getString("email"));
                }
            }
        }
        return null;
    }

    public static void insertar(Proveedor prov) throws SQLException {
        String sql = "INSERT INTO proveedor (id, nombre, email) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, prov.getId());
            ps.setString(2, prov.getNombre());
            if (prov.getEmail() != null) {
                ps.setString(3, prov.getEmail());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }
            ps.executeUpdate();
        }
    }

    public static List<Proveedor> listar() throws SQLException {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, email FROM proveedor ORDER BY id ASC";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Proveedor(rs.getInt("id"), rs.getString("nombre"), rs.getString("email")));
            }
        }
        return lista;
    }

    public static int actualizar(int id, String nuevoNombre, String nuevoEmail) throws SQLException {
        String sql = "UPDATE proveedor SET nombre = ?, email = ? WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoNombre);
            if (nuevoEmail != null) {
                ps.setString(2, nuevoEmail);
            } else {
                ps.setNull(2, Types.VARCHAR);
            }
            ps.setInt(3, id);
            return ps.executeUpdate();
        }
    }

    public static int eliminar(int id) throws SQLException {
        String sql = "DELETE FROM proveedor WHERE id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }
}