
package es.iesisidra.datastock.servicio;

import java.io.File;

public class ExploradorFicheros {

    public void explorar(String ruta) {
        File archivo = new File(ruta);

        if (!archivo.exists()) {
            System.out.println("La ruta no existe.");

        } else if (archivo.isFile()) {
            System.out.println("Es un fichero.");
            System.out.println("Nombre: " + archivo.getName());
            System.out.println("Tamaño: " + archivo.length() + " bytes");

        } else if (archivo.isDirectory()) {

            System.out.println("Es un directorio.");
            System.out.println("Contenido:");

            File[] contenido = archivo.listFiles();

            if (contenido != null) {

                for (File elemento : contenido) {
                    if (elemento.isDirectory()) {

                        System.out.println("[DIRECTORIO] " + elemento.getName());
                    } else {
                        
                        System.out.println("[FICHERO] " + elemento.getName());
                    }
                }
            }

        }
    }
}
