package es.iesisidra.datastock.servicio;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import es.iesisidra.datastock.modelo.Producto;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Componente reutilizable encargado de leer y validar productos desde un fichero JSON.
 * <p>
 * Extrae la lógica de lectura de ficheros de la UT1 en una clase independiente,
 * sin dependencias de consola ({@code System.out} o {@code Scanner}) ni acceso directo
 * al inventario en memoria o a la base de datos.
 * </p>
 *
 * @author Jorge Pérez Bonillo
 * @version 3.0
 */

public class LectorProductosJson {

    /**
     * Construye una nueva instancia del lector de productos JSON.
     */
    public LectorProductosJson(){

    }


    /**
     * Lee un fichero JSON desde la ruta indicada, valida la estructura del documento
     * y cada uno de los productos contenidos en él, y devuelve la lista de productos válidos.
     *
     * @param rutaFichero ruta ({@link Path}) del fichero JSON que contiene el catálogo de productos.
     * @return lista con los objetos {@link Producto} válidos leídos desde el fichero.
     * @throws IOException              si se produce un error de entrada/salida al abrir o leer el fichero.
     * @throws IllegalArgumentException si la ruta es nula o el fichero no existe, si el JSON está
     *                                  mal formado, si no contiene el array {@code productos} o si
     *                                  alguno de los productos presenta datos inválidos.
     */
    public List<Producto> leerProductos(Path rutaFichero) throws IOException {
        if (rutaFichero == null || !Files.exists(rutaFichero) || !Files.isRegularFile(rutaFichero)) {
            throw new IllegalArgumentException("El fichero JSON no existe: " + rutaFichero);
        }

        try (FileReader lector = new FileReader(rutaFichero.toFile())) {

            JsonElement raiz = JsonParser.parseReader(lector);

            if (raiz == null || !raiz.isJsonObject()) {
                throw new IllegalArgumentException("El JSON debe contener un objeto.");
            }

            JsonObject objeto = raiz.getAsJsonObject();

            if (!objeto.has("productos") || !objeto.get("productos").isJsonArray()) {
                throw new IllegalArgumentException("Falta la lista de productos.");
            }

            JsonArray listaJson = objeto.getAsJsonArray("productos");
            Gson gson = new Gson();
            List<Producto> productosValidos = new ArrayList<>();

            for (int posicion = 0; posicion < listaJson.size(); posicion++) {
                JsonElement elemento = listaJson.get(posicion);

                try {
                    Producto producto = gson.fromJson(elemento, Producto.class);

                    if (producto == null || !validarProducto(producto)) {
                        throw new IllegalArgumentException(
                                "Producto " + (posicion + 1) + ": datos no válidos.");
                    }

                    productosValidos.add(producto);

                } catch (JsonParseException e) {
                    throw new IllegalArgumentException(
                            "Producto " + (posicion + 1) + ": formato incorrecto.", e);
                }
            }

            return productosValidos;

        } catch (JsonParseException e) {
            throw new IllegalArgumentException("JSON mal formado: " + e.getMessage(), e);
        }
    }

    

    /**
     * Comprueba que un objeto {@link Producto} cumple todas las reglas de formato y
     * validación establecidas en DataStock:
     * <ul>
     *   <li>Código no nulo, no vacío, de máximo 12 caracteres y con formato {@code [A-Z0-9-]+}.</li>
     *   <li>Nombre no nulo y con una longitud comprendida entre 3 y 100 caracteres.</li>
     *   <li>Precio no nulo y estrictamente mayor que cero.</li>
     *   <li>Stock mayor o igual que cero.</li>
     *   <li>Identificadores de categoría y proveedor estrictamente positivos.</li>
     * </ul>
     *
     * @param producto objeto {@link Producto} que se desea validar.
     * @return {@code true} si el producto cumple todas las reglas; {@code false} en caso contrario.
     */
    
    public boolean validarProducto(Producto producto) {

        if (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty()) {
            return false;
        }

        String codigo = producto.getCodigo().trim();

        if (codigo.length() > 12) {
            return false;
        }

        if (!codigo.matches("[A-Z0-9-]+")) {
            return false;
        }

        if (producto.getNombre() == null) {
            return false;
        }

        String nombre = producto.getNombre().trim();

        if (nombre.length() < 3 || nombre.length() > 100) {
            return false;
        }

        if (producto.getPrecio() == null || producto.getPrecio().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return false;
        }

        if (producto.getStock() < 0) {
            return false;
        }

        if (producto.getCategoriaId() <= 0) {
            return false;
        }

        if (producto.getProveedorId() <= 0) {
            return false;
        }

        return true;
    }
}