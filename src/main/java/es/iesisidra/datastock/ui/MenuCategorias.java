package es.iesisidra.datastock.ui;

import es.iesisidra.datastock.dao.CategoriaDAO;
import es.iesisidra.datastock.modelo.Categoria;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.ListadoErrores;
import es.iesisidra.datastock.util.LectorConsola;
import es.iesisidra.datastock.validaciones.Validador;

import java.sql.SQLException;
import java.util.List;

public class MenuCategorias {

    public static void iniciar() {
        int opcion;
        do {
            System.out.println();
            System.out.println(ColoresConsola.pintar("--- GESTIÓN DE CATEGORÍAS (7.2) ---", ColoresConsola.CIAN));
            System.out.println("1. Dar de alta una categoría");
            System.out.println("2. Listar categorías");
            System.out.println("3. Modificar el nombre de una categoría");
            System.out.println("4. Eliminar una categoría");
            System.out.println("0. Volver al menú MySQL");

            opcion = LectorConsola.leerOpcion("Selecciona una opción (0-4): ", 0, 4);

            switch (opcion) {
                case 1 -> alta();
                case 2 -> listar();
                case 3 -> modificar();
                case 4 -> eliminar();
            }
            if (opcion != 0) {
                LectorConsola.pausar();
            }
        } while (opcion != 0);
    }

    private static void alta() {
        System.out.println("\n-- Alta de Categoría --");
        try {
            int id = LectorConsola.leerEntero("ID de la categoría (entero positivo): ");
            Validador.validarId(id);

            if (CategoriaDAO.existePorId(id)) {
                ColoresConsola.error("Ya existe una categoría con el ID " + id);
                return;
            }

            String nombre = LectorConsola.leerTexto("Nombre de la categoría (1-60 caracteres): ");
            nombre = Validador.validarNombreCategoria(nombre);

            if (CategoriaDAO.existePorNombre(nombre, null)) {
                ColoresConsola.error("Ya existe una categoría con el nombre '" + nombre + "'.");
                return;
            }

            CategoriaDAO.insertar(new Categoria(id, nombre));
            ColoresConsola.ok("Categoría registrada correctamente.");

        } catch (IllegalArgumentException e) {
            ColoresConsola.error(e.getMessage());
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }

    private static void listar() {
        System.out.println("\n-- Listado de Categorías --");
        try {
            List<Categoria> lista = CategoriaDAO.listar();
            if (lista.isEmpty()) {
                System.out.println("No hay categorías registradas.");
                return;
            }
            System.out.printf("%-6s | %-30s%n", "ID", "NOMBRE");
            System.out.println("----------------------------------------");
            for (Categoria c : lista) {
                System.out.printf("%-6d | %-30s%n", c.getId(), c.getNombre());
            }
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }

    private static void modificar() {
        System.out.println("\n-- Modificar Nombre de Categoría --");
        try {
            int id = LectorConsola.leerEntero("ID de la categoría a modificar: ");
            Categoria cat = CategoriaDAO.buscarPorId(id);
            if (cat == null) {
                ColoresConsola.error("No existe la categoría con ID " + id);
                return;
            }

            System.out.println("Nombre actual: " + cat.getNombre());
            String nuevoNombre = LectorConsola.leerTexto("Nuevo nombre: ");
            nuevoNombre = Validador.validarNombreCategoria(nuevoNombre);

            if (CategoriaDAO.existePorNombre(nuevoNombre, id)) {
                ColoresConsola.error("Ya existe otra categoría con el nombre '" + nuevoNombre + "'.");
                return;
            }

            if (LectorConsola.confirmar("¿Confirmas la modificación?")) {
                int filas = CategoriaDAO.actualizarNombre(id, nuevoNombre);
                if (filas > 0) {
                    ColoresConsola.ok("Categoría modificada con éxito.");
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
        System.out.println("\n-- Eliminar Categoría --");
        try {
            int id = LectorConsola.leerEntero("ID de la categoría a eliminar: ");
            Categoria cat = CategoriaDAO.buscarPorId(id);
            if (cat == null) {
                ColoresConsola.error("No existe la categoría con ID " + id);
                return;
            }

            System.out.println("Categoría a borrar: [" + cat.getId() + "] " + cat.getNombre());
            if (!LectorConsola.confirmar("¿Confirmas la eliminación?")) {
                System.out.println("Operación cancelada.");
                return;
            }

            CategoriaDAO.eliminar(id);
            ColoresConsola.ok("Categoría eliminada correctamente.");

        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }
}