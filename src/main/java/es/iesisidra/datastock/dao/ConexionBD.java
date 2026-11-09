package es.iesisidra.datastock.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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

        // 1. Probar en las distintas rutas relativas posibles según cómo abra VS Code
        File[] candidatos = new File[] {
            new File(RUTA_PROPERTIES),
            new File(System.getProperty("user.dir"), RUTA_PROPERTIES),
            new File(System.getProperty("user.dir") + "/DataStock_UT1_Proyecto_Inicial", RUTA_PROPERTIES),
            new File("DataStock_UT1_Proyecto_Inicial/" + RUTA_PROPERTIES)
        };

        for (File f : candidatos) {
            if (f.exists() && f.isFile()) {
                Properties props = new Properties();
                try (InputStream in = new FileInputStream(f)) {
                    props.load(in);
                    if (url == null) url = props.getProperty("jdbc.url");
                    if (user == null) user = props.getProperty("jdbc.user");
                    if (password == null) password = props.getProperty("jdbc.password");
                    if (url != null && user != null && password != null) {
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        // 2. Respaldo directo infalible para asegurar la entrega
        if (url == null || url.trim().isEmpty()) {
            url = "jdbc:mysql://127.0.0.1:3306/datastock?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        }
        if (user == null || user.trim().isEmpty()) {
            user = "datastock_user";
        }
        if (password == null) {
            password = "password123";
        }

        return DriverManager.getConnection(url.trim(), user.trim(), password.trim());
    }
}