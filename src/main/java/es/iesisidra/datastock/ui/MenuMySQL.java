package es.iesisidra.datastock.ui;

import es.iesisidra.datastock.dao.ComprobarConexion;
import es.iesisidra.datastock.servicio.ImportarMemoria;
import es.iesisidra.datastock.servicio.Inventario;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;

/**
 * Menú de la UT2. Solo enruta a cada submenú: no contiene lógica JDBC.
 * Recibe el Inventario porque la opción 8 (importar de memoria a MySQL)
 * lo necesitará.
 */
public class MenuMySQL {

    private final Inventario inventario;

    public MenuMySQL(Inventario inventario) {
        this.inventario = inventario;
    }

    /** Muestra el menú MySQL hasta que el usuario elige volver. */
    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN MYSQL (UT2) ---");
            System.out.println("1. Comprobar conexión y mostrar información de la BD");
            System.out.println("2. Gestionar categorías");
            System.out.println("3. Gestionar proveedores");
            System.out.println("4. Gestionar productos (CRUD)");
            System.out.println("5. Consultar y buscar productos");
            System.out.println("6. Registrar entrada o salida de stock");
            System.out.println("7. Consultar historial de movimientos");
            System.out.println("8. Importar productos de memoria a MySQL");
            System.out.println("9. Demostración de transacción con commit/rollback");
            System.out.println("0. Volver al menú principal");

            opcion = LectorConsola.leerOpcion("Selecciona una opción: ", 0, 9);

            switch (opcion) {
                case 1 -> ComprobarConexion.comprobar();
                case 2 -> MenuCategorias.iniciar();
                case 3 -> MenuProveedores.iniciar();
                case 4 -> MenuProductos.iniciar();
                case 5 -> MenuConsultas.iniciar();
                case 6 -> MenuMovimientos.iniciar();
                case 7 -> MenuHistorial.iniciar();
                case 8 -> ImportarMemoria.importarMemoriaMySQL(inventario);
                case 9 -> pendiente("Demostración commit/rollback");
                case 0 -> { }
                default -> throw new IllegalStateException("Opción inesperada");
            }
        } while (opcion != 0);
    }

    
    private void pendiente(String nombre) {
        System.out.println(ColoresConsola.pintar("[PENDIENTE] " + nombre, ColoresConsola.AMARILLO));
        LectorConsola.pausar();
    }
}