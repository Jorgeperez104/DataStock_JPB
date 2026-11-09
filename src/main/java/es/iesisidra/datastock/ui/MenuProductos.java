package es.iesisidra.datastock.ui;

import es.iesisidra.datastock.dao.CategoriaDAO;
import es.iesisidra.datastock.dao.ProductoDAO;
import es.iesisidra.datastock.dao.ProveedorDAO;
import es.iesisidra.datastock.modelo.Producto;
import es.iesisidra.datastock.modelo.ProductoDetalle;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;
import es.iesisidra.datastock.util.ListadoErrores;
import es.iesisidra.datastock.validaciones.Validador;

import java.math.BigDecimal;
import java.sql.SQLException;

public class MenuProductos {

    public static void iniciar() {
        int opcion;
        do {
            System.out.println();
            System.out.println(ColoresConsola.pintar("--- GESTIÓN DE PRODUCTOS (7.4) ---", ColoresConsola.CIAN));
            System.out.println("1. Dar de alta un producto");
            System.out.println("2. Consultar producto por código");
            System.out.println("3. Modificar un producto");
            System.out.println("4. Eliminar un producto");
            System.out.println("0. Volver al menú MySQL");

            opcion = LectorConsola.leerOpcion("Selecciona una opción (0-4): ", 0, 4);

            switch (opcion) {
                case 1 -> alta();
                case 2 -> consultar();
                case 3 -> modificar();
                case 4 -> eliminar();
            }
            if (opcion != 0) {
                LectorConsola.pausar();
            }
        } while (opcion != 0);
    }

    private static void alta() {
        System.out.println("\n-- Alta de Producto --");
        try {
            String codigo = LectorConsola.leerTexto("Código (1-12 caracteres, letras/números/guión): ");
            codigo = Validador.validarCodigo(codigo);

            if (ProductoDAO.existePorCodigo(codigo)) {
                ColoresConsola.error("Ya existe un producto con el código " + codigo);
                return;
            }

            String nombre = LectorConsola.leerTexto("Nombre (3-100 caracteres): ");
            nombre = Validador.validarNombreProducto(nombre);

            BigDecimal precio = LectorConsola.leerBigDecimal("Precio (€): ");
            if (precio == null) {
                System.out.println("Operación cancelada.");
                return;
            }
            precio = Validador.validarPrecio(precio);

            int stock = LectorConsola.leerEntero("Stock inicial (entero >= 0): ");
            stock = Validador.validarStock(stock);

            int catId = LectorConsola.leerEntero("ID de la categoría: ");
            Validador.validarId(catId);
            if (!CategoriaDAO.existePorId(catId)) {
                ColoresConsola.error("No existe ninguna categoría con ID " + catId);
                return;
            }

            int provId = LectorConsola.leerEntero("ID del proveedor: ");
            Validador.validarId(provId);
            if (!ProveedorDAO.existePorId(provId)) {
                ColoresConsola.error("No existe ningún proveedor con ID " + provId);
                return;
            }

            Producto prod = new Producto(codigo, nombre, precio, stock, catId, provId);
            ProductoDAO.insertar(prod);
            ColoresConsola.ok("Producto " + codigo + " guardado correctamente.");

        } catch (IllegalArgumentException e) {
            ColoresConsola.error(e.getMessage());
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }

    private static void consultar() {
        System.out.println("\n-- Consultar Producto por Código --");
        try {
            String codigo = LectorConsola.leerTexto("Código del producto: ");
            codigo = Validador.validarCodigo(codigo);

            ProductoDetalle p = ProductoDAO.buscarPorCodigo(codigo);
            if (p == null) {
                ColoresConsola.aviso("No existe ningún producto con el código " + codigo);
                return;
            }

            System.out.println("Código:     " + p.getCodigo());
            System.out.println("Nombre:     " + p.getNombre());
            System.out.println("Precio:     " + p.getPrecio() + " €");
            System.out.println("Stock:      " + p.getStock());
            System.out.println("Categoría:  [" + p.getCategoriaId() + "] " + p.getCategoriaNombre());
            System.out.println("Proveedor:  [" + p.getProveedorId() + "] " + p.getProveedorNombre());

        } catch (IllegalArgumentException e) {
            ColoresConsola.error(e.getMessage());
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }

    private static void modificar() {
        System.out.println("\n-- Modificar Producto --");
        try {
            String codigo = LectorConsola.leerTexto("Código del producto a modificar: ");
            codigo = Validador.validarCodigo(codigo);

            ProductoDetalle p = ProductoDAO.buscarPorCodigo(codigo);
            if (p == null) {
                ColoresConsola.error("No existe ningún producto con el código " + codigo);
                return;
            }

            System.out.println("\nDatos actuales del producto " + p.getCodigo() + ":");
            System.out.println("(Pulsa Intro en cualquier campo para conservar su valor actual)");

            // 1. Nombre
            System.out.println("Nombre actual: " + p.getNombre());
            String nuevoNombre = LectorConsola.leerTexto("Nuevo nombre: ");
            if (nuevoNombre.trim().isEmpty()) {
                nuevoNombre = p.getNombre();
            } else {
                nuevoNombre = Validador.validarNombreProducto(nuevoNombre);
            }

            // 2. Precio
            System.out.println("Precio actual: " + p.getPrecio() + " €");
            BigDecimal nuevoPrecio = LectorConsola.leerBigDecimalOpcional("Nuevo precio: ");
            if (nuevoPrecio == null) {
                nuevoPrecio = p.getPrecio();
            } else {
                nuevoPrecio = Validador.validarPrecio(nuevoPrecio);
            }

            // 3. Categoría
            System.out.println("Categoría actual: [" + p.getCategoriaId() + "] " + p.getCategoriaNombre());
            Integer nuevaCat = LectorConsola.leerEnteroOpcional("Nuevo ID de categoría: ");
            int catFinal = (nuevaCat != null) ? nuevaCat : p.getCategoriaId();
            Validador.validarId(catFinal);
            if (!CategoriaDAO.existePorId(catFinal)) {
                ColoresConsola.error("La categoría con ID " + catFinal + " no existe.");
                return;
            }

            // 4. Proveedor
            System.out.println("Proveedor actual: [" + p.getProveedorId() + "] " + p.getProveedorNombre());
            Integer nuevoProv = LectorConsola.leerEnteroOpcional("Nuevo ID de proveedor: ");
            int provFinal = (nuevoProv != null) ? nuevoProv : p.getProveedorId();
            Validador.validarId(provFinal);
            if (!ProveedorDAO.existePorId(provFinal)) {
                ColoresConsola.error("El proveedor con ID " + provFinal + " no existe.");
                return;
            }

            if (LectorConsola.confirmar("¿Confirmas la modificación del producto?")) {
                int filas = ProductoDAO.actualizar(codigo, nuevoNombre, nuevoPrecio, catFinal, provFinal);
                if (filas > 0) {
                    ColoresConsola.ok("Producto " + codigo + " modificado correctamente.");
                }
            } else {
                System.out.println("Modificación cancelada.");
            }

        } catch (IllegalArgumentException e) {
            ColoresConsola.error(e.getMessage());
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }

    private static void eliminar() {
        System.out.println("\n-- Eliminar Producto --");
        try {
            String codigo = LectorConsola.leerTexto("Código del producto a eliminar: ");
            codigo = Validador.validarCodigo(codigo);

            ProductoDetalle p = ProductoDAO.buscarPorCodigo(codigo);
            if (p == null) {
                ColoresConsola.error("No existe ningún producto con el código " + codigo);
                return;
            }

            System.out.println("Producto seleccionado: [" + p.getCodigo() + "] " + p.getNombre());
            if (!LectorConsola.confirmar("¿Seguro que deseas eliminar este producto?")) {
                System.out.println("Operación cancelada.");
                return;
            }

            ProductoDAO.eliminar(codigo);
            ColoresConsola.ok("Producto " + codigo + " eliminado correctamente.");

        } catch (IllegalArgumentException e) {
            ColoresConsola.error(e.getMessage());
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }
}