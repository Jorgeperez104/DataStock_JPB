package es.iesisidra.datastock.dao;

import es.iesisidra.datastock.dao.ConexionBD;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.ListadoErrores;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ComprobarConexion {

    public static void comprobar() {
        System.out.println("\nComprobando conexión con MySQL...");

        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM producto")) {

            DatabaseMetaData meta = conn.getMetaData();
            String bd = conn.getCatalog();
            String gestor = meta.getDatabaseProductName();
            String version = meta.getDatabaseProductVersion();

            int totalProductos = 0;
            if (rs.next()) {
                totalProductos = rs.getInt(1);
            }

            ColoresConsola.ok("Conexión establecida correctamente.");
            System.out.println("Base de datos: " + (bd != null ? bd : "datastock"));
            System.out.println("Gestor:        " + gestor);
            System.out.println("Versión:       " + version);
            System.out.println("Productos almacenados: " + totalProductos);

        } catch (SQLException ex) {
            ColoresConsola.error(ListadoErrores.traducir(ex));
        }
    }
}
