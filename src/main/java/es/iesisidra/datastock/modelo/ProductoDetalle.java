package es.iesisidra.datastock.modelo;

import java.math.BigDecimal;

public class ProductoDetalle {
    private final String codigo;
    private final String nombre;
    private final BigDecimal precio;
    private final int stock;
    private final int categoriaId;
    private final String categoriaNombre;
    private final int proveedorId;
    private final String proveedorNombre;

    public ProductoDetalle(String codigo, String nombre, BigDecimal precio, int stock,
                           int categoriaId, String categoriaNombre,
                           int proveedorId, String proveedorNombre) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.proveedorId = proveedorId;
        this.proveedorNombre = proveedorNombre;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public int getStock() { return stock; }
    public int getCategoriaId() { return categoriaId; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public int getProveedorId() { return proveedorId; }
    public String getProveedorNombre() { return proveedorNombre; }
}