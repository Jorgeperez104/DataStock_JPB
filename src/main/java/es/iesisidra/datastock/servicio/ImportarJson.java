package es.iesisidra.datastock.servicio;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import es.iesisidra.datastock.modelo.CatalogoJson;
import es.iesisidra.datastock.modelo.Producto;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportarJson {

    private final Inventario inventario;

    public ImportarJson(Inventario inventario) {
        this.inventario = inventario;
    }

    public void importar(String ruta) {

        try (FileReader lector = new FileReader(ruta)) {

            JsonElement raiz = JsonParser.parseReader(lector);
            JsonObject objeto = raiz.getAsJsonObject();

            if (!raiz.isJsonObject()) {
                System.out.println("Error: el JSON debe contener un objeto.");
                return;
            }


            if (!objeto.has("productos") || !objeto.get("productos").isJsonArray()) {
                System.out.println("Error: falta la lista de productos.");
                return;
            }

            JsonArray listaJson = objeto.getAsJsonArray("productos");
            Gson gson = new Gson();

            int encontrados = listaJson.size();
            int importados = 0;
            int duplicados = 0;
            int rechazados = 0;

            List<String> incidencias = new ArrayList<>();

            for (int posicion = 0; posicion < encontrados; posicion++) {
                JsonElement elemento = listaJson.get(posicion);

                try {
                    Producto producto = gson.fromJson(elemento, Producto.class);

                    if (producto == null || !validarProducto(producto)) {
                        rechazados++;
                        incidencias.add("Producto " + (posicion + 1)+ ": datos no válidos.");
                    } else if (inventario.existeCodigo(producto.getCodigo())) {
                        duplicados++;
                        incidencias.add("Producto " + (posicion + 1)+ ": código duplicado.");
                    } else {
                        inventario.getProductos().add(producto);
                        importados++;
                    }

                } catch (JsonParseException e) {
                    rechazados++;
                    incidencias.add("Producto " + (posicion + 1)+ ": formato incorrecto.");
                }
            }

            System.out.println("Productos encontrados: " + encontrados);
            System.out.println("Productos importados: " + importados);
            System.out.println("Productos duplicados: " + duplicados);
            System.out.println("Productos rechazados: " + rechazados);

            if (!incidencias.isEmpty()) {
                System.out.println("Incidencias:");

                for (String incidencia : incidencias) {
                    System.out.println("- " + incidencia);
                }
            }

        } catch (IOException | JsonParseException e) {
            System.out.println("No se ha podido importar el JSON: "+ e.getMessage());
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