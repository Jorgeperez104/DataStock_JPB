package es.iesisidra.datastock.servicio;

import com.google.gson.Gson;

import es.iesisidra.datastock.modelo.CatalogoJson;
import es.iesisidra.datastock.modelo.Producto;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ImportarJson {

    private final Inventario inventario;

    public ImportarJson(Inventario inventario) {
        this.inventario = inventario;
    }

    public void importar(String ruta) {
        try (FileReader lector = new FileReader(ruta)) {
            Gson gson = new Gson();

            CatalogoJson catalogo = gson.fromJson(lector, CatalogoJson.class);

            if (catalogo != null && catalogo.getProductos() != null) {
                int importados = 0;
                int duplicados = 0;
                int rechazados = 0;

                for (Producto producto : catalogo.getProductos()) {

                    if (!validarProducto(producto)) {
                        rechazados++;
                    } else if (inventario.existeCodigo(producto.getCodigo())) {
                        duplicados++;
                    } else {
                        inventario.getProductos().add(producto);
                        importados++;
                    }
                }

                System.out.println("Productos importados: " + importados);
                System.out.println("Productos duplicados: " + duplicados);
                System.out.println("Productos rechazados: " + rechazados);

            } else {
                System.out.println("El archivo no tiene productos.");
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
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