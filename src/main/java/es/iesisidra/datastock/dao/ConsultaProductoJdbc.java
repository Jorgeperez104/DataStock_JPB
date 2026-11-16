package es.iesisidra.datastock.dao;

import es.iesisidra.datastock.modelo.ProductoDetalle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


/**
 * Componente reutilizable de acceso a base de datos mediante JDBC para consultar
 * productos por su código único en MySQL.
 * <p>
 * Encapsula la consulta SQL parametrizada de la UT2 utilizando {@link PreparedStatement},
 * {@link ResultSet} y {@link ConexionBD#obtenerConexion()}, cerrando todos los recursos
 * automáticamente mediante {@code try-with-resources} y sin emitir mensajes por consola.
 * </p>
 *
 * @author Jorge Pérez Bonillo
 * @version 3.0
 */

public class ConsultaProductoJdbc {


    /**
     * Construye una nueva instancia del componente de consulta JDBC de productos.
     */
    public ConsultaProductoJdbc() {
    }
    

    /**
     * Busca un producto en la base de datos MySQL a partir de su código único,
     * recuperando también los nombres asociados de su categoría y proveedor.
     *
     * @param codigo código único del producto a consultar (no puede ser nulo ni estar vacío).
     * @return un {@link Optional} que contiene el {@link ProductoDetalle} si el producto
     *         existe en la base de datos, o {@link Optional#empty()} si no se encuentra.
     * @throws IllegalArgumentException si el parámetro {@code codigo} es nulo o está en blanco.
     * @throws SQLException             si ocurre un error al establecer la conexión o ejecutar
     *                                  la consulta SQL en MySQL.
     */
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