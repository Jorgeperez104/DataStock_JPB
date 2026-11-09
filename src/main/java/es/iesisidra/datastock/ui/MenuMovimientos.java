package es.iesisidra.datastock.ui;

import es.iesisidra.datastock.dao.MovimientoDAO;
import es.iesisidra.datastock.dao.ProductoDAO;
import es.iesisidra.datastock.modelo.ProductoDetalle;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;
import es.iesisidra.datastock.util.ListadoErrores;
import es.iesisidra.datastock.validaciones.Validador;

import java.sql.SQLException;

public class MenuMovimientos {

    public static void iniciar() {
        int opcion;
        do {
            System.out.println();
            System.out.println(ColoresConsola.pintar("--- MOVIMIENTOS DE STOCK (7.6) ---", ColoresConsola.CIAN));
            System.out.println("1. Registrar una ENTRADA de stock");
            System.out.println("2. Registrar una SALIDA de stock");
            System.out.println("0. Volver al menú MySQL");

            opcion = LectorConsola.leerOpcion("Selecciona una opción (0-2): ", 0, 2);

            switch (opcion) {
                case 1 -> registrar("ENTRADA");
                case 2 -> registrar("SALIDA");
            }
            if (opcion != 0) {
                LectorConsola.pausar();
            }
        } while (opcion != 0);
    }

    private static void registrar(String tipo) {
        System.out.println("\n-- Registrar " + tipo + " de Stock --");
        try {
            String codigo = LectorConsola.leerTexto("Código del producto: ");
            codigo = Validador.validarCodigo(codigo);

            ProductoDetalle producto = ProductoDAO.buscarPorCodigo(codigo);
            if (producto == null) {
                ColoresConsola.error("El producto con código " + codigo + " no existe.");
                return;
            }

            int stockAnterior = producto.getStock();
            System.out.println("Producto:     " + producto.getNombre());
            System.out.println("Stock actual: " + stockAnterior);

            int cantidad = LectorConsola.leerEntero("Cantidad a registrar (> 0): ");
            if (cantidad <= 0) {
                ColoresConsola.error("La cantidad debe ser un entero estrictamente positivo.");
                return;
            }

            if (tipo.equals("SALIDA") && cantidad > stockAnterior) {
                ColoresConsola.error("Stock insuficiente. Hay " + stockAnterior + " unidades y se solicitan " + cantidad + ".");
                return;
            }

            if (!LectorConsola.confirmar("¿Confirmas el registro del movimiento?")) {
                System.out.println("Operación cancelada.");
                return;
            }

            MovimientoDAO.registrarMovimiento(codigo, tipo, cantidad);

            int stockPosterior = MovimientoDAO.obtenerStockActual(codigo);
            ColoresConsola.ok("Movimiento registrado con éxito.");
            System.out.println("Tipo:            " + tipo);
            System.out.println("Cantidad:        " + cantidad);
            System.out.println("Stock anterior:  " + stockAnterior + " -> Stock posterior: " + stockPosterior);

        } catch (IllegalArgumentException e) {
            ColoresConsola.error(e.getMessage());
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }
}