package es.iesisidra.datastock.dao;

import es.iesisidra.datastock.modelo.ProductoDetalle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class ConsultaProductoJdbc {

    
    public Optional<ProductoDetalle> buscarPorCodigo(String codigo) throws SQLException {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de producto no puede estar vacío.");
        }

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

            ps.setString(1, codigo.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProductoDetalle producto = new ProductoDetalle(
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio"),
                        rs.getInt("stock"),
                        rs.getInt("categoria_id"),
                        rs.getString("categoria_nombre"),
                        rs.getInt("proveedor_id"),
                        rs.getString("proveedor_nombre")
                    );
                    return Optional.of(producto);
                }
            }
        }

        return Optional.empty();
    }
}