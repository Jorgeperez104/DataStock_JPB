package es.iesisidra.datastock.raf;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import es.iesisidra.datastock.modelo.Producto;
import es.iesisidra.datastock.servicio.GestorInventario;
import es.iesisidra.datastock.servicio.Inventario;
import es.iesisidra.datastock.util.LectorConsola;

public class AccesoAleatorio {

    private static final int TAMANO_REGISTRO = 36;

    private Inventario inventario;
    private Scanner teclado;
    private File fichero;

    public AccesoAleatorio(Inventario inventario) {
        this.inventario = inventario;
        this.teclado = new Scanner(System.in);
    }

    public void generarFichero() {

        if (inventario.getProductos().isEmpty()) {
            System.out.println("El inventario está vacío.");
            return;
        }

        System.out.print("Introduce la ruta del fichero: ");
        String ruta = teclado.nextLine();

        File fichero = new File(ruta);

        if (fichero.exists()) {
            System.out.print("El fichero ya existe. ¿Quieres sobrescribirlo? (s/n): ");
            String respuesta = teclado.nextLine();

            if (!respuesta.equalsIgnoreCase("s")) {
                System.out.println("Operación cancelada.");
                return;
            }
        }

        this.fichero = fichero;

        List<Producto> productos = new ArrayList<>(inventario.getProductos());

        for (int posicion = 0; posicion < productos.size() - 1; posicion++) {

            for (int siguiente = posicion + 1; siguiente < productos.size(); siguiente++) {

                if (productos.get(posicion).getCodigo()
                        .compareToIgnoreCase(productos.get(siguiente).getCodigo()) > 0) {

                    Producto auxiliar = productos.get(posicion);
                    productos.set(posicion, productos.get(siguiente));
                    productos.set(siguiente, auxiliar);
                }
            }
        }

        try (RandomAccessFile archivo = new RandomAccessFile(fichero, "rw")) {

            archivo.setLength(0);

            for (Producto producto : productos) {

                String codigo = producto.getCodigo();

                for (int posicion = 0; posicion < 12; posicion++) {

                    if (posicion < codigo.length()) {
                        archivo.writeChar(codigo.charAt(posicion));
                    } else {
                        archivo.writeChar(' ');
                    }
                }

                archivo.writeDouble(producto.getPrecio().doubleValue());
                archivo.writeInt(producto.getStock());
            }

            System.out.println("Fichero generado correctamente.");
            System.out.println("Productos guardados: " + productos.size());
            System.out.println("Tamaño del fichero: " + archivo.length() + " bytes");

        } catch (Exception e) {
            System.out.println("Error al generar el fichero: " + e.getMessage());
        }
    }

    public void consultarRegistro() {

        if (fichero == null || !fichero.exists()) {
            System.out.println("Primero debes generar el fichero.");
            return;
        }

        try (RandomAccessFile archivo = new RandomAccessFile(fichero, "r")) {

            // Comprobar que el tamaño sea múltiplo de 36
            if (archivo.length() % TAMANO_REGISTRO != 0) {
                System.out.println("El fichero tiene un tamaño incorrecto.");
                return;
            }

            int cantidadRegistros = (int) (archivo.length() / TAMANO_REGISTRO);

            System.out.print("Introduce la posición del producto: ");
            int posicion = Integer.parseInt(teclado.nextLine());

            if (posicion < 0 || posicion >= cantidadRegistros) {
                System.out.println("La posición no es válida.");
                return;
            }

            archivo.seek((long) posicion * TAMANO_REGISTRO);

            String codigo = "";

            for (int contador = 0; contador < 12; contador++) {
                codigo = codigo + archivo.readChar();
            }

            double precio = archivo.readDouble();
            int stock = archivo.readInt();

            System.out.println("\nProducto encontrado:");
            System.out.println("Código: " + codigo.trim());
            System.out.println("Precio: " + precio);
            System.out.println("Stock: " + stock);

        } catch (NumberFormatException e) {
            System.out.println("Debes introducir una posición numérica.");
        } catch (Exception e) {
            System.out.println("Error al consultar el registro: " + e.getMessage());
        }
    }

    public void modificarStock() {

        if (fichero == null || !fichero.exists()) {
            System.out.println("Primero debes generar el fichero.");
            return;
        }

        try (RandomAccessFile archivo = new RandomAccessFile(fichero, "rw")) {

            if (archivo.length() % TAMANO_REGISTRO != 0) {
                System.out.println("El fichero tiene un tamaño incorrecto.");
                return;
            }

            int cantidadRegistros = (int) (archivo.length() / TAMANO_REGISTRO);

            System.out.print("Introduce la posición del producto: ");
            int posicion = Integer.parseInt(teclado.nextLine());

            if (posicion < 0 || posicion >= cantidadRegistros) {
                System.out.println("La posición no es válida.");
                return;
            }

            archivo.seek((long) posicion * TAMANO_REGISTRO + 32);
            int stockActual = archivo.readInt();

            System.out.println("Stock actual: " + stockActual);

            System.out.print("Introduce el nuevo stock: ");
            int nuevoStock = Integer.parseInt(teclado.nextLine());

            if (nuevoStock < 0) {
                System.out.println("El stock no puede ser negativo.");
                return;
            }

            archivo.seek((long) posicion * TAMANO_REGISTRO + 32);
            archivo.writeInt(nuevoStock);

            System.out.println("Stock modificado correctamente.");

        } catch (NumberFormatException e) {
            System.out.println("Debes introducir un número válido.");
        } catch (Exception e) {
            System.out.println("Error al modificar el stock: " + e.getMessage());
        }
    }

    public void listarRegistros() {

        if (fichero == null || !fichero.exists()) {
            System.out.println("Primero debes generar el fichero.");
            return;
        }

        try (RandomAccessFile archivo = new RandomAccessFile(fichero, "r")) {

            if (archivo.length() % TAMANO_REGISTRO != 0) {
                System.out.println("El fichero tiene un tamaño incorrecto.");
                return;
            }

            int cantidadRegistros = (int) (archivo.length() / TAMANO_REGISTRO);

            if (cantidadRegistros == 0) {
                System.out.println("El fichero está vacío.");
                return;
            }

            System.out.println("\n--- PRODUCTOS DEL FICHERO ---");

            for (int posicion = 0; posicion < cantidadRegistros; posicion++) {

                String codigo = "";

                for (int contador = 0; contador < 12; contador++) {
                    codigo = codigo + archivo.readChar();
                }

                double precio = archivo.readDouble();
                int stock = archivo.readInt();

                System.out.println("\nPosición: " + posicion);
                System.out.println("Código: " + codigo.trim());
                System.out.println("Precio: " + precio);
                System.out.println("Stock: " + stock);
            }

        } catch (Exception e) {
            System.out.println("Error al listar los registros: " + e.getMessage());
        }
    }

    public void menu() {

        int opcion = -1;

        while (opcion != 0) {

            System.out.println("\n--- ACCESO ALEATORIO ---");
            System.out.println("1. Generar fichero binario");
            System.out.println("2. Consultar un registro");
            System.out.println("3. Modificar el stock");
            System.out.println("4. Listar todos los registros");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            try {
                opcion = Integer.parseInt(teclado.nextLine());

                switch (opcion) {
                    case 1:
                        generarFichero();
                        break;
                    case 2:
                        consultarRegistro();
                        break;
                    case 3:
                        modificarStock();
                        break;
                    case 4:
                        listarRegistros();
                        break;
                    case 0:
                        System.out.println("Saliendo...");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Debes introducir un número.");
            }
        }
    }

    public static void main(String[] args) {

        Inventario inventario = new Inventario();

        GestorInventario gestor = new GestorInventario(inventario);

        String ruta = LectorConsola.leerTexto(
                "Introduce la ruta del inventario guardado (.dat): ");

        gestor.recuperarInventario(ruta);

        if (inventario.getProductos().isEmpty()) {
            System.out.println("No se han podido cargar productos.");
            return;
        }

        AccesoAleatorio acceso = new AccesoAleatorio(inventario);

        acceso.menu();
    }

}
