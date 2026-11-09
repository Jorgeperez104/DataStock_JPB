package es.iesisidra.datastock.dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static final String RUTA_PROPERTIES = "config/db.properties";

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        String url = System.getenv("JDBC_URL");
        String user = System.getenv("JDBC_USER");
        String password = System.getenv("JDBC_PASSWORD");

        if (url == null || user == null || password == null) {
            Path path = Paths.get(RUTA_PROPERTIES);
            if (Files.exists(path)) {
                Properties props = new Properties();
                try (InputStream in = Files.newInputStream(path)) {
                    props.load(in);
                    if (url == null) url = props.getProperty("jdbc.url");
                    if (user == null) user = props.getProperty("jdbc.user");
                    if (password == null) password = props.getProperty("jdbc.password");
                } catch (IOException e) {
                    throw new SQLException("Error al leer el archivo de configuración: " + RUTA_PROPERTIES);
                }
            }
        }

        if (url == null || user == null || password == null) {
            throw new SQLException("Faltan credenciales de conexión (configure variables de entorno o config/db.properties)");
        }

        return DriverManager.getConnection(url, user, password);
    }
}