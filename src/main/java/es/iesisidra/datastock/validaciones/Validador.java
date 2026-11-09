package es.iesisidra.datastock.validaciones;

import java.math.BigDecimal;

public class Validador {

    public static String validarCodigo(String codigo) {
        if (codigo == null) throw new IllegalArgumentException("El código no puede ser nulo.");
        String norm = codigo.trim().toUpperCase();
        if (!norm.matches("^[A-Z0-9-]{1,12}$")) {
            throw new IllegalArgumentException("El código debe tener entre 1 y 12 caracteres (letras, números y guión).");
        }
        return norm;
    }

    public static String validarNombreProducto(String nombre) {
        if (nombre == null || nombre.trim().length() < 3 || nombre.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre del producto debe tener entre 3 y 100 caracteres.");
        }
        return nombre.trim();
    }

    public static String validarNombreCategoria(String nombre) {
        if (nombre == null || nombre.trim().isEmpty() || nombre.trim().length() > 60) {
            throw new IllegalArgumentException("El nombre de categoría debe tener entre 1 y 60 caracteres.");
        }
        return nombre.trim();
    }

    public static String validarNombreProveedor(String nombre) {
        if (nombre == null || nombre.trim().isEmpty() || nombre.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre de proveedor debe tener entre 1 y 100 caracteres.");
        }
        return nombre.trim();
    }

    public static String validarEmail(String email) {
        if (email == null || email.trim().isEmpty() || email.trim().equals("-")) return null;
        String norm = email.trim();
        if (norm.length() > 120 || !norm.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }
        return norm;
    }

    public static BigDecimal validarPrecio(BigDecimal precio) {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0.");
        }
        if (precio.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("El precio no puede tener más de dos decimales.");
        }
        return precio;
    }

    public static int validarStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
        return stock;
    }

    public static int validarId(int id) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser un número positivo.");
        return id;
    }
}