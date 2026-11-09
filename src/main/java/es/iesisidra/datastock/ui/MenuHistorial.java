package es.iesisidra.datastock.ui;

import es.iesisidra.datastock.dao.MovimientoDAO;
import es.iesisidra.datastock.dao.ProductoDAO;
import es.iesisidra.datastock.modelo.MovimientoStock;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;
import es.iesisidra.datastock.util.ListadoErrores;
import es.iesisidra.datastock.validaciones.Validador;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MenuHistorial {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void iniciar() {
        int opcion;
        do {
            System.out.println();
            System.out.println(ColoresConsola.pintar("--- HISTORIAL DE MOVIMIENTOS (7.7) ---", ColoresConsola.CIAN));
            System.out.println("1. Consultar todos los movimientos");
            System.out.println("2. Consultar movimientos de un producto");
            System.out.println("0. Volver al menú MySQL");

            opcion = LectorConsola.leerOpcion("Selecciona una opción (0-2): ", 0, 2);

            switch (opcion) {
                case 1 -> consultarTodos();
                case 2 -> consultarPorProducto();
            }
            if (opcion != 0) {
                LectorConsola.pausar();
            }
        } while (opcion != 0);
    }

    private static void mostrarTabla(List<MovimientoStock> lista) {
        if (lista.isEmpty()) {
            System.out.println("Sin movimientos registrados.");
            return;
        }
        System.out.printf("%-6s | %-19s | %-12s | %-8s | %-8s | %-10s | %-10s%n",
                "ID", "FECHA", "PRODUCTO", "TIPO", "CANTIDAD", "ST. ANTER.", "ST. POST.");
        System.out.println("---------------------------------------------------------------------------------------------");
        for (MovimientoStock m : lista) {
            System.out.printf("%-6d | %-19s | %-12s | %-8s | %-8d | %-10d | %-10d%n",
                    m.getId(), m.getFecha().format(FORMATO_FECHA), m.getProductoCodigo(),
                    m.getTipo(), m.getCantidad(), m.getStockAnterior(), m.getStockPosterior());
        }
        System.out.println("Total de movimientos: " + lista.size());
    }

    private static void consultarTodos() {
        System.out.println("\n-- Historial Completo de Movimientos --");
        try {
            List<MovimientoStock> lista = MovimientoDAO.listarTodos();
            mostrarTabla(lista);
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }

    private static void consultarPorProducto() {
        System.out.println("\n-- Historial de Movimientos por Producto --");
        try {
            String codigo = LectorConsola.leerTexto("Código del producto: ");
            codigo = Validador.validarCodigo(codigo);

            if (!ProductoDAO.existePorCodigo(codigo)) {
                ColoresConsola.error("El producto con código " + codigo + " no existe.");
                return;
            }

            List<MovimientoStock> lista = MovimientoDAO.listarPorProducto(codigo);
            mostrarTabla(lista);

        } catch (IllegalArgumentException e) {
            ColoresConsola.error(e.getMessage());
        } catch (SQLException e) {
            ColoresConsola.error(ListadoErrores.traducir(e));
        }
    }
}