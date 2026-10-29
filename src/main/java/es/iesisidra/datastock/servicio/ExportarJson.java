package es.iesisidra.datastock.servicio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import es.iesisidra.datastock.modelo.Producto;

public class ExportarJson {

    private final Inventario inventario;

    public ExportarJson(Inventario inventario) {
        this.inventario = inventario;
    }

    public void exportar(String ruta) {
        Scanner sc = new Scanner(System.in);

        if (inventario.getProductos().isEmpty()) {
            System.out.println("El inventario está vacío.");
            return;
        }

        File fichero = new File(ruta);
        File directorio = fichero.getAbsoluteFile().getParentFile();

        if (!directorio.exists() || !directorio.isDirectory()) {
            System.out.println("El directorio no existe.");
            return;
        }

        if (fichero.exists()) {

            System.out.println("El fichero ya existe. ¿Quieres sobreescribirlo (S/N)?");
            String respuesta = sc.nextLine();
            if (!respuesta.equalsIgnoreCase("S")) {
                System.out.println("Exportación cancelada");
                return;
            }

        }

        ArrayList<Producto> productosOrdenados = new ArrayList<>(inventario.getProductos());

        for (int posicionActual = 0; posicionActual < productosOrdenados.size() - 1; posicionActual++) {

            for (int posicionComparada = 0; posicionComparada < productosOrdenados.size() - 1- posicionActual; posicionComparada++) {

                if (productosOrdenados.get(posicionComparada).getCodigo().compareToIgnoreCase(productosOrdenados.get(posicionComparada + 1).getCodigo()) > 0) {

                    Producto productoAuxiliar = productosOrdenados.get(posicionComparada);

                    productosOrdenados.set(posicionComparada,productosOrdenados.get(posicionComparada + 1));

                    productosOrdenados.set(posicionComparada + 1,productoAuxiliar);
                }
            }
        }

        JsonObject objetoRaiz = new JsonObject();
        JsonArray listaProductos = new JsonArray();

        for (Producto producto : productosOrdenados) {
            listaProductos.add(new com.google.gson.Gson().toJsonTree(producto));
        }

        objetoRaiz.add("productos", listaProductos);

        try (OutputStreamWriter escritor = new OutputStreamWriter(new FileOutputStream(fichero), StandardCharsets.UTF_8)) {

            GsonBuilder constructorGson = new GsonBuilder();
            constructorGson.setPrettyPrinting();
            constructorGson.create().toJson(objetoRaiz, escritor);

            System.out.println("Productos exportados correctamente.");
            System.out.println("Archivo: " + fichero.getAbsolutePath());
            System.out.println("Total de productos exportados: " + productosOrdenados.size());

        } catch (Exception e) {
            System.out.println("Error al exportar el archivo: " + e.getMessage());
        }

    }
}
