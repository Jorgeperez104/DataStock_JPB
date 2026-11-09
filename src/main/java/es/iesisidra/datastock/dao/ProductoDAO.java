package es.iesisidra.datastock.dao;

import es.iesisidra.datastock.dao.ConexionBD;
import es.iesisidra.datastock.modelo.Producto;
import es.iesisidra.datastock.modelo.ProductoDetalle;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductoDAO {

    public static boolean existePorCodigo(String codigo) throws SQLException {
        String sql = "SELECT 1 FROM producto WHERE codigo = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static ProductoDetalle buscarPorCodigo(String codigo) throws SQLException {
        String sql = """
            SELECT p.codigo, p.nombre, p.precio, p.stock,
                   p.categoria_id, c.nombre AS categoria_nombre,
                   p.proveedor_id, pr.nombre AS proveedor_nombre
            FROM producto p
            JOIN categoria c ON p.categoria_id = c.id
            JOIN proveedor pr ON p.proveedor_id = pr.id
            WHERE p.codigo = ?
        """;
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ProductoDetalle(
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio"),
                        rs.getInt("stock"),
                        rs.getInt("categoria_id"),
                        rs.getString("categoria_nombre"),
                        rs.getInt("proveedor_id"),
                        rs.getString("proveedor_nombre")
                    );
                }
            }
        }
        return null;
    }

    public static void insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO producto (codigo, nombre, precio, stock, categoria_id, proveedor_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setBigDecimal(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getCategoriaId());
            ps.setInt(6, p.getProveedorId());
            ps.executeUpdate();
        }
    }

    public static int actualizar(String codigo, String nombre, BigDecimal precio, int categoriaId, int proveedorId) throws SQLException {
        String sql = "UPDATE producto SET nombre = ?, precio = ?, categoria_id = ?, proveedor_id = ? WHERE codigo = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setBigDecimal(2, precio);
            ps.setInt(3, categoriaId);
            ps.setInt(4, proveedorId);
            ps.setString(5, codigo);
            return ps.executeUpdate();
        }
    }

    public static int eliminar(String codigo) throws SQLException {
        String sql = "DELETE FROM producto WHERE codigo = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            return ps.executeUpdate();
        }
    }
}