package es.iesisidra.datastock.ui;

import es.iesisidra.datastock.dao.ProveedorDAO;
import es.iesisidra.datastock.modelo.Proveedor;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;
import es.iesisidra.datastock.util.ListadoErrores;
import es.iesisidra.datastock.validaciones.Validador;

import java.sql.SQLException;
import java.util.List;

public class MenuProveedores {

    public static void iniciar() {
        int opcion;
        do {
            System.out.println();
            System.out.println(ColoresConsola.pintar("--- GESTIÓN DE PROVEEDORES (7.3) ---", ColoresConsola.CIAN));
            System.out.println("1. Dar de alta un proveedor");
            System.out.println("2. Listar proveedores");
            System.out.println("3. Modificar un proveedor");
            System.out.println("4. Eliminar un proveedor");
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
        System.out.println("\n-- Alta de Proveedor --");
        try {
            int id = LectorConsola.leerEntero("ID del proveedor (entero positivo): ");
            Validador.validarId(id);

            if (ProveedorDAO.existePorId(id)) {
                ColoresConsola.error("Ya existe un proveedor con el ID " + id);
                return;
            }

            String nombre = LectorConsola.leerTexto("Nombre del proveedor (máx 100 caracteres): ");
            nombre = Validador.validarNombreProveedor(nombre);

            String email = LectorConsola.leerTexto("Correo (opcional, Intro para omitir): ");
            email = Validador.validarEmail(email);

            if (email != null && ProveedorDAO.existePorEmail(email, null)) {
                ColoresConsola.error("Ya existe un proveedor con el correo '" + email + "'.");
                return;
            }

            ProveedorDAO.insertar(new Proveedor(id, nombre, email));
            ColoresConsola.ok("Proveedor registrado correctamente.");

        } catch (IllegalArgumentException e) {
            ColoresConsola.error(e.getMessage());
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }

    private static void listar() {
        System.out.println("\n-- Listado de Proveedores --");
        try {
            List<Proveedor> lista = ProveedorDAO.listar();
            if (lista.isEmpty()) {
                System.out.println("No hay proveedores registrados.");
                return;
            }
            System.out.printf("%-6s | %-30s | %-30s%n", "ID", "NOMBRE", "EMAIL");
            System.out.println("------------------------------------------------------------------------");
            for (Proveedor p : lista) {
                String emailStr = (p.getEmail() != null) ? p.getEmail() : "-";
                System.out.printf("%-6d | %-30s | %-30s%n", p.getId(), p.getNombre(), emailStr);
            }
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }

    private static void modificar() {
        System.out.println("\n-- Modificar Proveedor --");
        try {
            int id = LectorConsola.leerEntero("ID del proveedor a modificar: ");
            Proveedor prov = ProveedorDAO.buscarPorId(id);
            if (prov == null) {
                ColoresConsola.error("No existe el proveedor con ID " + id);
                return;
            }

            System.out.println("Nombre actual: " + prov.getNombre());
            String nuevoNombre = LectorConsola.leerTexto("Nuevo nombre (Intro para conservar): ");
            if (nuevoNombre.trim().isEmpty()) {
                nuevoNombre = prov.getNombre();
            } else {
                nuevoNombre = Validador.validarNombreProveedor(nuevoNombre);
            }

            System.out.println("Email actual: " + (prov.getEmail() != null ? prov.getEmail() : "-"));
            System.out.println("(Intro para conservar, '-' para borrar el correo)");
            String nuevoEmail = LectorConsola.leerTexto("Nuevo email: ");

            if (nuevoEmail.trim().isEmpty()) {
                nuevoEmail = prov.getEmail();
            } else if (nuevoEmail.trim().equals("-")) {
                nuevoEmail = null;
            } else {
                nuevoEmail = Validador.validarEmail(nuevoEmail);
                if (ProveedorDAO.existePorEmail(nuevoEmail, id)) {
                    ColoresConsola.error("Ese correo ya está registrado por otro proveedor.");
                    return;
                }
            }

            if (LectorConsola.confirmar("¿Confirmas la modificación?")) {
                int filas = ProveedorDAO.actualizar(id, nuevoNombre, nuevoEmail);
                if (filas > 0) {
                    ColoresConsola.ok("Proveedor actualizado correctamente.");
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
        System.out.println("\n-- Eliminar Proveedor --");
        try {
            int id = LectorConsola.leerEntero("ID del proveedor a eliminar: ");
            Proveedor prov = ProveedorDAO.buscarPorId(id);
            if (prov == null) {
                ColoresConsola.error("No existe el proveedor con ID " + id);
                return;
            }

            System.out.println("Proveedor: [" + prov.getId() + "] " + prov.getNombre());
            if (!LectorConsola.confirmar("¿Confirmas la eliminación?")) {
                System.out.println("Operación cancelada.");
                return;
            }

            ProveedorDAO.eliminar(id);
            ColoresConsola.ok("Proveedor eliminado correctamente.");

        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }
}