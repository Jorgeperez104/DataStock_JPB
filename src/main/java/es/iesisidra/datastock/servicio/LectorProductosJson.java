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


public class LectorProductosJson {

    
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