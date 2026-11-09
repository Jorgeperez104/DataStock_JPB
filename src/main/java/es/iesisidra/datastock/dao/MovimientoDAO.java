package es.iesisidra.datastock.dao;

import es.iesisidra.datastock.dao.ConexionBD;
import es.iesisidra.datastock.modelo.MovimientoStock;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovimientoDAO {

    public static void registrarMovimiento(String codigo, String tipo, int cantidad) throws SQLException {
        String sql = "CALL registrar_movimiento(?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, codigo);
            cs.setString(2, tipo);
            cs.setInt(3, cantidad);
            cs.execute();
        }
    }

    public static int obtenerStockActual(String codigo) throws SQLException {
        String sql = "SELECT stock FROM producto WHERE codigo = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock");
                }
            }
        }
        return -1;
    }

    public static List<MovimientoStock> listarTodos() throws SQLException {
        List<MovimientoStock> lista = new ArrayList<>();
        String sql = "SELECT id, producto_codigo, tipo, cantidad, fecha, stock_anterior, stock_posterior " +
                     "FROM movimiento_stock ORDER BY fecha ASC, id ASC";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MovimientoStock.TipoMovimiento tipo = MovimientoStock.TipoMovimiento.valueOf(rs.getString("tipo").toUpperCase());
                lista.add(new MovimientoStock(
                    rs.getLong("id"),
                    rs.getString("producto_codigo"),
                    tipo,
                    rs.getInt("cantidad"),
                    rs.getTimestamp("fecha").toLocalDateTime(),
                    rs.getInt("stock_anterior"),
                    rs.getInt("stock_posterior")
                ));
            }
        }
        return lista;
    }

    public static List<MovimientoStock> listarPorProducto(String codigo) throws SQLException {
        List<MovimientoStock> lista = new ArrayList<>();
        String sql = "SELECT id, producto_codigo, tipo, cantidad, fecha, stock_anterior, stock_posterior " +
                     "FROM movimiento_stock WHERE producto_codigo = ? ORDER BY fecha ASC, id ASC";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovimientoStock.TipoMovimiento tipo = MovimientoStock.TipoMovimiento.valueOf(rs.getString("tipo").toUpperCase());
                    lista.add(new MovimientoStock(
                        rs.getLong("id"),
                        rs.getString("producto_codigo"),
                        tipo,
                        rs.getInt("cantidad"),
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        rs.getInt("stock_anterior"),
                        rs.getInt("stock_posterior")
                    ));
                }
            }
        }
        return lista;
    }
}