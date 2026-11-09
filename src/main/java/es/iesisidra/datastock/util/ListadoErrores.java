package es.iesisidra.datastock.util;

import java.sql.SQLException;

public class ListadoErrores {

    private ListadoErrores() {
    }

    public static String traducir(SQLException ex) {
        int codigo = ex.getErrorCode();

        if (codigo == 1045) {
            return "Acceso denegado: usuario o contraseña incorrectos.";
        }
        if (codigo == 1049) {
            return "La base de datos especificada no existe en el servidor.";
        }
        if (codigo == 1062) {
            return "Registro duplicado: ya existe un elemento con ese identificador o nombre.";
        }
        if (codigo == 1451) {
            return "Operación no permitida: tiene registros asociados en otras tablas.";
        }
        if (codigo == 1452) {
            return "Referencia no válida: la clave foránea indicada no existe.";
        }
        if (codigo == 3819) {
            return "Restricción CHECK incumplida en la base de datos.";
        }

        return "Error SQL [" + codigo + "]: " + ex.getMessage();
    }
}