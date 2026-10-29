package es.iesisidra.datastock.servicio;

import java.util.ArrayList;

import es.iesisidra.datastock.modelo.Producto;

public class ConsultarProductos {

    private final Inventario inventario;

    public ConsultarProductos(Inventario inventario) {
        this.inventario = inventario;
    }

    public void listarProductos() {

        if (inventario.getProductos().isEmpty()) {
            System.out.println("El inventario está vacío.");
            return;
        }

        ArrayList<Producto> productos = new ArrayList<>(inventario.getProductos());

        for (int posicion = 0; posicion < productos.size() - 1; posicion++) {
            for (int siguiente = 0; siguiente < productos.size() - 1 - posicion; siguiente++) {
                if (productos.get(siguiente).getCodigo().compareToIgnoreCase(
                        productos.get(siguiente + 1).getCodigo()) > 0) {

                    Producto auxiliar = productos.get(siguiente);
                    productos.set(siguiente, productos.get(siguiente + 1));
                    productos.set(siguiente + 1, auxiliar);
                }
            }
        }
    }

    public void buscarPrCodigo(String codigo) {

        boolean existe = false;

        for (Producto producto : inventario.getProductos()) {
            if (producto.getCodigo().trim().equalsIgnoreCase(codigo)) {
                System.out.println("Código: " + producto.getCodigo());
                System.out.println("Nombre: " + producto.getNombre());
                System.out.println("Precio: " + producto.getPrecio() + " €");
                System.out.println("Stock: " + producto.getStock());
                System.out.println("Categoría: " + producto.getCategoriaId());
                System.out.println("Proveedor: " + producto.getProveedorId());

                existe = true;
            }
        }

        if (!existe) {
            System.out.println("No hay ningún producto con ese código.");
        }
    }
}
