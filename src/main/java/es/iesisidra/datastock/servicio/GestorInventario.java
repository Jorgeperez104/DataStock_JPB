package es.iesisidra.datastock.servicio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import es.iesisidra.datastock.modelo.Producto;

public class GestorInventario {

    private final Inventario inventario;

    public GestorInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public void guardarInventario(String ruta) {
        if (inventario.getProductos().isEmpty()) {
            System.out.println("El inventario está vacío.");

        } else {
            File fichero = new File(ruta);

            try (ObjectOutputStream escritor = new ObjectOutputStream(new FileOutputStream(fichero));) {

                escritor.writeObject(new ArrayList<>(inventario.getProductos()));

                System.out.println("Inventario guardado correctamente.");
                System.out.println("Archivo: " + fichero.getAbsolutePath());
                System.out.println("Productos guardados: " + inventario.getProductos().size());

            } catch (Exception e) {
                System.out.println("Error al guardar el inventario: " + e.getMessage());
            }
        }
    }

    public void recuperarInventario(String ruta) {
        File fichero = new File(ruta);

        if (!fichero.exists() || !fichero.isFile()) {
            System.out.println("El fichero no existe.");

        } else {
            
            try (ObjectInputStream lector = new ObjectInputStream(new FileInputStream(fichero))) {

                ArrayList<Producto> productosGuardados = (ArrayList<Producto>) lector.readObject();

                inventario.getProductos().clear();
                inventario.getProductos().addAll(productosGuardados);

                System.out.println("Inventario recuperado correctamente.");
                System.out.println("Archivo: " + fichero.getAbsolutePath());
                System.out.println("Productos recuperados: "+ inventario.getProductos().size());

            } catch (Exception e) {
                System.out.println("Error al recuperar el inventario: "+ e.getMessage());
            }
        }

    }

}
