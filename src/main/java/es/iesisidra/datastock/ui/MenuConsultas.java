package es.iesisidra.datastock.ui;

import es.iesisidra.datastock.dao.ProductoDAO;
import es.iesisidra.datastock.modelo.ProductoDetalle;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;
import es.iesisidra.datastock.util.ListadoErrores;
import es.iesisidra.datastock.validaciones.Validador;

import java.sql.SQLException;
import java.util.List;

public class MenuConsultas {

    public static void iniciar() {
        int opcion;
        do {
            System.out.println();
            System.out.println(ColoresConsola.pintar("--- CONSULTAS DE PRODUCTOS (7.5) ---", ColoresConsola.CIAN));
            System.out.println("1. Listar todos los productos");
            System.out.println("2. Buscar producto por código");
            System.out.println("3. Buscar productos por nombre");
            System.out.println("4. Consultar productos con stock inferior a un umbral");
            System.out.println("0. Volver al menú MySQL");

            opcion = LectorConsola.leerOpcion("Selecciona una opción (0-4): ", 0, 4);

            switch (opcion) {
                case 1 -> listarTodos();
                case 2 -> buscarPorCodigo();
                case 3 -> buscarPorNombre();
                case 4 -> buscarPorStock();
            }
            if (opcion != 0) {
                LectorConsola.pausar();
            }
        } while (opcion != 0);
    }

    private static void mostrarTabla(List<ProductoDetalle> lista) {
        if (lista.isEmpty()) {
            System.out.println("No se han encontrado productos.");
            return;
        }
        System.out.printf("%-12s | %-30s | %-10s | %-6s | %-20s | %-20s%n",
                "CÓDIGO", "NOMBRE", "PRECIO", "STOCK", "CATEGORÍA", "PROVEEDOR");
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        for (ProductoDetalle p : lista) {
            System.out.printf("%-12s | %-30s | %8.2f € | %-6d | %-20s | %-20s%n",
                    p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock(),
                    p.getCategoriaNombre(), p.getProveedorNombre());
        }
        System.out.println("Total encontrados: " + lista.size());
    }

    private static void listarTodos() {
        System.out.println("\n-- Listado Completo de Productos --");
        try {
            List<ProductoDetalle> lista = ProductoDAO.listarTodos();
            mostrarTabla(lista);
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }

    private static void buscarPorCodigo() {
        System.out.println("\n-- Búsqueda por Código --");
        try {
            String cod = LectorConsola.leerTexto("Código del producto: ");
            cod = Validador.validarCodigo(cod);

            ProductoDetalle p = ProductoDAO.buscarPorCodigo(cod);
            if (p == null) {
                ColoresConsola.aviso("No se ha encontrado ningún producto con el código " + cod);
                return;
            }
            mostrarTabla(List.of(p));
        } catch (IllegalArgumentException e) {
            ColoresConsola.error(e.getMessage());
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }

    private static void buscarPorNombre() {
        System.out.println("\n-- Búsqueda por Nombre --");
        try {
            String texto = LectorConsola.leerTexto("Texto a buscar en el nombre: ").trim();
            if (texto.isEmpty()) {
                ColoresConsola.aviso("Debe introducir al menos un carácter para buscar.");
                return;
            }
            List<ProductoDetalle> lista = ProductoDAO.buscarPorNombre(texto);
            mostrarTabla(lista);
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }

    private static void buscarPorStock() {
        System.out.println("\n-- Productos con Stock Inferior a un Umbral --");
        try {
            int umbral = LectorConsola.leerEntero("Umbral de existencias (stock < umbral): ");
            Validador.validarStock(umbral);

            List<ProductoDetalle> lista = ProductoDAO.buscarPorStockMenor(umbral);
            mostrarTabla(lista);
        } catch (IllegalArgumentException e) {
            ColoresConsola.error(e.getMessage());
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }
}