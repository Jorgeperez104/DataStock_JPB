package es.iesisidra.datastock.complementarias;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DemoSQLite {

    private static final String RUTA_BD = "datos/sqlite/productos_demo.db";
    private static final String URL_JDBC = "jdbc:sqlite:" + RUTA_BD;

    public static void main(String[] args) {
        System.out.println("=== DEMOSTRACIÓN INDEPENDIENTE SQLITE (RA2-b) ===");

        // 1. Asegurar que existe el directorio local
        File carpeta = new File("datos/sqlite");
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        // 2. Conexión, creación de tabla, inserción y consulta con try-with-resources
        try (Connection con = DriverManager.getConnection(URL_JDBC)) {
            System.out.println("[OK] Conexión SQLite abierta: " + RUTA_BD);

            // 3. Crear tabla si no existe
            String sqlTabla = """
                CREATE TABLE IF NOT EXISTS producto_demo (
                    codigo TEXT PRIMARY KEY NOT NULL,
                    nombre TEXT NOT NULL)""";
                    
            try (Statement st = con.createStatement()) {
                st.execute(sqlTabla);
                System.out.println("[OK] Tabla producto_demo disponible.");
            }

            // 4. Inserción con control de duplicados (INSERT OR IGNORE)
            String sqlInsert = "INSERT OR IGNORE INTO producto_demo (codigo, nombre) VALUES (?, ?)";
            int insertados = 0;
            try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                // Registro 1
                ps.setString(1, "SQL-001");
                ps.setString(2, "Producto demo A");
                insertados += ps.executeUpdate();

                // Registro 2
                ps.setString(1, "SQL-002");
                ps.setString(2, "Producto demo B");
                insertados += ps.executeUpdate();
            }

            int yaExistentes = 2 - insertados;
            System.out.println("Insertados: " + insertados + " | Ya existentes: " + yaExistentes);

            // 5. Consulta y lectura de ResultSet
            System.out.println("\n-- Registros en SQLite --");
            String sqlSelect = "SELECT codigo, nombre FROM producto_demo ORDER BY codigo ASC";
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sqlSelect)) {
                while (rs.next()) {
                    System.out.printf("%-10s | %-20s%n", rs.getString("codigo"), rs.getString("nombre"));
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Error en la base de datos SQLite: " + e.getMessage());
        }
    }
}
