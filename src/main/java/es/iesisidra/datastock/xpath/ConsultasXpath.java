package es.iesisidra.datastock.xpath;

import java.io.File;
import java.util.Scanner;

public class ConsultasXpath {

    

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);


        System.out.println("--- CONSULTAS XPATH ---");
        System.out.print("Introduce la ruta del fichero XML: ");
        String ruta = sc.nextLine();

        File fichero = new File(ruta);

        if (!fichero.exists() || !fichero.isFile()) {
            System.out.println("Error: el fichero no existe.");

        } else {

            System.out.println("Fichero encontrado: " + fichero.getAbsolutePath());
        }

        sc.close();
    }
}

    
    


