package es.iesisidra.datastock.servicio;

import com.google.gson.Gson;
import es.iesisidra.datastock.modelo.Producto;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ImportarJson {

    public void importar(String ruta) {
        try (FileReader lector = new FileReader(ruta)) {
            Gson gson = new Gson();

            System.out.println("Archivo JSON leído correctamente.");

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
    
    
}