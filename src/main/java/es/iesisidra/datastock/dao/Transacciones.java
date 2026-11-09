package es.iesisidra.datastock.dao;

import es.iesisidra.datastock.dao.ConexionBD;
import es.iesisidra.datastock.dao.CategoriaDAO;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.ListadoErrores;
import es.iesisidra.datastock.util.LectorConsola;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Transacciones {

    private static final int ID_PRUEBA_1 = 901;
    private static final int ID_PRUEBA_2 = 902;
    private static final int ID_PRUEBA_3 = 903;

    public static void ejecutar() {
        System.out.println("\n--- DEMOSTRACIÓN DE TRANSACCIONES JDBC (7.9) ---");
        System.out.println("Esta prueba técnica demostrará el control de commit y rollback.");
        System.out.println("Se utilizarán IDs temporales de prueba (" + ID_PRUEBA_1 + ", " + ID_PRUEBA_2 + ", " + ID_PRUEBA_3 + ").");

        if (!LectorConsola.confirmar("¿Deseas iniciar la demostración?")) {
            System.out.println("Demostración cancelada.");
            return;
        }

        Connection con = null;
        try {
            con = ConexionBD.obtenerConexion();

            // Limpieza previa defensiva
            limpiarRegistrosPrueba(con);

            // ESCENARIO A: COMMIT
            System.out.println("\n[ESCENARIO A] Probando transacción con éxito y COMMIT...");
            con.setAutoCommit(false);

            String sqlInsert = "INSERT INTO categoria (id, nombre) VALUES (?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                // Inserción 1
                ps.setInt(1, ID_PRUEBA_1);
                ps.setString(2, "Test Transaccion A1");
                ps.executeUpdate();

                // Inserción 2
                ps.setInt(1, ID_PRUEBA_2);
                ps.setString(2, "Test Transaccion A2");
                ps.executeUpdate();

                con.commit();
                ColoresConsola.ok("Transacción confirmada con commit().");
            }

            boolean existe1 = CategoriaDAO.existePorId(ID_PRUEBA_1);
            boolean existe2 = CategoriaDAO.existePorId(ID_PRUEBA_2);
            if (existe1 && existe2) {
                ColoresConsola.ok("Comprobación: Ambas categorías (" + ID_PRUEBA_1 + " y " + ID_PRUEBA_2 + ") existen en MySQL.");
            }

            // ESCENARIO B: ROLLBACK
            System.out.println("\n[ESCENARIO B] Probando fallo provocado y ROLLBACK...");
            try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                // Inserción correcta 1
                ps.setInt(1, ID_PRUEBA_3);
                ps.setString(2, "Test Transaccion B1");
                ps.executeUpdate();
                System.out.println("Fila 1 (ID " + ID_PRUEBA_3 + ") insertada en el contexto de la transacción.");

                // Inserción 2: provocamos fallo forzado duplicando la PK
                System.out.println("Provocando intencionadamente error de clave duplicada con ID " + ID_PRUEBA_3 + "...");
                ps.setInt(1, ID_PRUEBA_3);
                ps.setString(2, "Test Transaccion B1 Duplicado");
                ps.executeUpdate();

                con.commit(); // No debería alcanzarse
            } catch (SQLException ex) {
                System.out.println("Error SQL capturado según lo esperado: " + ex.getErrorCode() + " - " + ex.getMessage());
                con.rollback();
                ColoresConsola.ok("Rollback ejecutado con éxito.");
            }

            boolean existe3 = CategoriaDAO.existePorId(ID_PRUEBA_3);
            if (!existe3) {
                ColoresConsola.ok("Comprobación: La categoría (" + ID_PRUEBA_3 + ") NO existe en MySQL gracias al rollback.");
            } else {
                ColoresConsola.error("Fallo: La categoría " + ID_PRUEBA_3 + " permanece en la base de datos.");
            }

            // Limpieza final de datos de prueba
            limpiarRegistrosPrueba(con);
            System.out.println("\nLimpieza completada: IDs de prueba eliminados.");

        } catch (SQLException e) {
            ColoresConsola.error("Error imprevisto en la demostración: " + ListadoErrores.traducir(e));
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    ColoresConsola.error("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }

    private static void limpiarRegistrosPrueba(Connection con) {
        String sqlDelete = "DELETE FROM categoria WHERE id IN (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sqlDelete)) {
            ps.setInt(1, ID_PRUEBA_1);
            ps.setInt(2, ID_PRUEBA_2);
            ps.setInt(3, ID_PRUEBA_3);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}