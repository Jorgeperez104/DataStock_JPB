package es.iesisidra.datastock.servicio;

import es.iesisidra.datastock.dao.ConexionBD;
import es.iesisidra.datastock.dao.CategoriaDAO;
import es.iesisidra.datastock.dao.ProductoDAO;
import es.iesisidra.datastock.dao.ProveedorDAO;
import es.iesisidra.datastock.servicio.Inventario;
import es.iesisidra.datastock.modelo.Producto;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;
import es.iesisidra.datastock.util.ListadoErrores;
import es.iesisidra.datastock.validaciones.Validador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImportarMemoria {

    public static void importarMemoriaMySQL(Inventario inventario) {
        System.out.println("\n--- IMPORTAR PRODUCTOS DE MEMORIA A MYSQL (7.8) ---");

        List<Producto> productosMemoria = inventario.getProductos();
        if (productosMemoria.isEmpty()) {
            ColoresConsola.aviso("No hay productos cargados en el inventario en memoria.");
            return;
        }

        System.out.println("Productos disponibles en memoria: " + productosMemoria.size());
        if (!LectorConsola.confirmar("¿Deseas importar estos productos a MySQL?")) {
            System.out.println("Importación cancelada.");
            return;
        }

        System.out.println("Iniciando comprobación de productos...");
        List<Producto> validosParaInsertar = new ArrayList<>();
        int omitidosDuplicados = 0;
        int rechazados = 0;

        for (Producto p : productosMemoria) {
            System.out.println("\nComprobando " + p.getCodigo() + " - " + p.getNombre() + "...");
            try {
                Validador.validarCodigo(p.getCodigo());
                Validador.validarNombreProducto(p.getNombre());
                Validador.validarPrecio(p.getPrecio());
                Validador.validarStock(p.getStock());

                if (ProductoDAO.existePorCodigo(p.getCodigo())) {
                    ColoresConsola.aviso("El producto ya existe en MySQL. Se omite.");
                    omitidosDuplicados++;
                    continue;
                }

                if (!CategoriaDAO.existePorId(p.getCategoriaId())) {
                    ColoresConsola.error("No existe la categoría con ID " + p.getCategoriaId() + " en MySQL.");
                    rechazados++;
                    continue;
                }

                if (!ProveedorDAO.existePorId(p.getProveedorId())) {
                    ColoresConsola.error("No existe el proveedor con ID " + p.getProveedorId() + " en MySQL.");
                    rechazados++;
                    continue;
                }

                ColoresConsola.ok("Producto válido para importar.");
                validosParaInsertar.add(p);

            } catch (IllegalArgumentException e) {
                ColoresConsola.error("Datos incorrectos: " + e.getMessage());
                rechazados++;
            } catch (SQLException e) {
                ColoresConsola.error("Error al comprobar en BD: " + ListadoErrores.traducir(e));
                rechazados++;
            }
        }

        int insertados = 0;
        if (!validosParaInsertar.isEmpty()) {
            String sql = "INSERT INTO producto (codigo, nombre, precio, stock, categoria_id, proveedor_id) VALUES (?, ?, ?, ?, ?, ?)";
            Connection con = null;
            try {
                con = ConexionBD.obtenerConexion();
                con.setAutoCommit(false);

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    for (Producto p : validosParaInsertar) {
                        ps.setString(1, p.getCodigo());
                        ps.setString(2, p.getNombre());
                        ps.setBigDecimal(3, p.getPrecio());
                        ps.setInt(4, p.getStock());
                        ps.setInt(5, p.getCategoriaId());
                        ps.setInt(6, p.getProveedorId());
                        ps.executeUpdate();
                        insertados++;
                    }
                    con.commit();
                    ColoresConsola.ok("Lote de inserciones confirmado (commit).");
                } catch (SQLException e) {
                    con.rollback();
                    insertados = 0;
                    ColoresConsola.error("Error SQL inesperado durante la inserción. Rollback realizado: " + ListadoErrores.traducir(e));
                } finally {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                ColoresConsola.error("Error en la conexión transaccional: " + ListadoErrores.traducir(e));
            }
        }

        System.out.println("\n-------------------------------------------");
        System.out.println("RESULTADO DE LA IMPORTACIÓN");
        System.out.println("-------------------------------------------");
        System.out.println("Productos analizados:            " + productosMemoria.size());
        System.out.println("Productos insertados:            " + insertados);
        System.out.println("Omitidos por código duplicado:   " + omitidosDuplicados);
        System.out.println("Rechazados por datos/referencias: " + rechazados);
        if (insertados > 0) {
            ColoresConsola.ok("Importación finalizada con éxito.");
        }
    }
}
