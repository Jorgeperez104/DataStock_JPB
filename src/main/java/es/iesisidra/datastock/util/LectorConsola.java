package es.iesisidra.datastock.util;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Utilidad compartida para solicitar datos por consola.
 * <p>Utiliza un único {@link Scanner} para toda la aplicación. Se leen líneas
 * completas y se convierten cuando procede, evitando conflictos entre
 * {@code nextInt()} y {@code nextLine()}.</p>
 */
public final class LectorConsola {
    private static final Scanner TECLADO = new Scanner(System.in);
    public static final String CANCELAR = "cancelar";

    private LectorConsola() { }

    /** Lee una línea; permite una cadena vacía. */
    public static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!TECLADO.hasNextLine()) {
            // Entrada cerrada (por ejemplo, ejecución no interactiva).
            System.out.println();
            System.exit(0);
        }
        return TECLADO.nextLine();
    }

    /** Lee un entero; repite la pregunta ante una entrada no válida. */
    public static int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).trim();
            try { 
                return Integer.parseInt(texto); 
            } catch (NumberFormatException ex) {
                ColoresConsola.aviso("Introduce un número entero válido.");
            }
        }
    }

    /** Lee una opción comprendida entre los límites indicados. */
    public static int leerOpcion(String mensaje, int minimo, int maximo) {
        while (true) {
            int opcion = leerEntero(mensaje);
            if (opcion >= minimo && opcion <= maximo) return opcion;
            ColoresConsola.aviso("Opción fuera de rango.");
        }
    }

    /** Solicita confirmación con S/N y devuelve true únicamente si se elige S. */
    public static boolean confirmar(String mensaje) {
        while (true) {
            String respuesta = leerTexto(mensaje + " (S/N): ").trim();
            if (respuesta.equalsIgnoreCase("S")) return true;
            if (respuesta.equalsIgnoreCase("N")) return false;
            ColoresConsola.aviso("Responde S o N.");
        }
    }

    /** Detiene el menú hasta que el usuario pulse Intro. */
    public static void pausar() { 
        leerTexto("Pulsa Intro para continuar..."); 
    }

    // --- Métodos añadidos para la UT2 (Paso 4) ---

    /**
     * Lee un entero opcional. Si el usuario pulsa Intro sin escribir nada, devuelve null
     * para conservar el valor actual en las modificaciones.
     */
    public static Integer leerEnteroOpcional(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).trim();
            if (texto.isEmpty()) {
                return null;
            }
            if (texto.equalsIgnoreCase(CANCELAR)) {
                return null;
            }
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException ex) {
                ColoresConsola.aviso("Introduce un número entero válido o pulsa Intro para conservar.");
            }
        }
    }

    /**
     * Lee un importe monetario usando BigDecimal (nunca float/double).
     * Devuelve null si el usuario escribe 'cancelar'.
     */
    public static BigDecimal leerBigDecimal(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).trim().replace(',', '.');
            if (texto.equalsIgnoreCase(CANCELAR)) {
                return null;
            }
            try {
                return new BigDecimal(texto);
            } catch (NumberFormatException ex) {
                ColoresConsola.aviso("Formato decimal no válido. Introduce un número o escribe 'cancelar'.");
            }
        }
    }

    /**
     * Lee un BigDecimal opcional. Si pulsa Intro devuelve null (para conservar valor actual).
     */
    public static BigDecimal leerBigDecimalOpcional(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).trim().replace(',', '.');
            if (texto.isEmpty() || texto.equalsIgnoreCase(CANCELAR)) {
                return null;
            }
            try {
                return new BigDecimal(texto);
            } catch (NumberFormatException ex) {
                ColoresConsola.aviso("Formato decimal no válido o pulsa Intro para conservar.");
            }
        }
    }
}