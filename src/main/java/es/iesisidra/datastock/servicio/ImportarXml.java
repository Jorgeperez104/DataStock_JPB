package es.iesisidra.datastock.servicio;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import es.iesisidra.datastock.modelo.Producto;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.math.BigDecimal;

public class ImportarXml {

    private final Inventario inventario;

    public ImportarXml(Inventario inventario) {
        this.inventario = inventario;
    }

    public void importar(String ruta) {
        try {
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();

            fabrica.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            fabrica.setFeature(
                    "http://apache.org/xml/features/disallow-doctype-decl", true);

            DocumentBuilder constructor = fabrica.newDocumentBuilder();
            Document documento = constructor.parse(new File(ruta));

            Element raiz = documento.getDocumentElement();

            if (!raiz.getTagName().equals("catalogo")) {
                System.out.println("El XML no tiene una raíz válida.");
                return;
            }

            NodeList productos = raiz.getElementsByTagName("producto");

            int encontrados = productos.getLength();
            int importados = 0;
            int duplicados = 0;
            int rechazados = 0;

            System.out.println("Productos encontrados: " + productos.getLength());

            for (int posicion = 0; posicion < encontrados; posicion++) {
                Element producto = (Element) productos.item(posicion);

                String codigo = producto.getElementsByTagName("codigo")
                        .item(0).getTextContent();

                String nombre = producto.getElementsByTagName("nombre")
                        .item(0).getTextContent();

                String precio = producto.getElementsByTagName("precio")
                        .item(0).getTextContent();

                String stock = producto.getElementsByTagName("stock")
                        .item(0).getTextContent();

                String categoriaId = producto.getElementsByTagName("categoriaId")
                        .item(0).getTextContent();

                String proveedorId = producto.getElementsByTagName("proveedorId")
                        .item(0).getTextContent();

                System.out.println("Código: " + codigo);
                System.out.println("Nombre: " + nombre);
                System.out.println("Precio: " + precio);
                System.out.println("Stock: " + stock);
                System.out.println("Categoría: " + categoriaId);
                System.out.println("Proveedor: " + proveedorId);

                BigDecimal precioProducto = new BigDecimal(precio);
                int stockProducto = Integer.parseInt(stock);
                int categoria = Integer.parseInt(categoriaId);
                int proveedor = Integer.parseInt(proveedorId);

                Producto nuevoProducto = new Producto(codigo, nombre, precioProducto, stockProducto, categoria,
                        proveedor);

                if (!validarProducto(nuevoProducto)) {
                    rechazados++;
                    System.out.println("El producto tiene datos no válidos: " + codigo);
                } else if (inventario.existeCodigo(codigo)) {
                    duplicados++;
                    System.out.println("El producto ya existe: " + codigo);
                } else {
                    inventario.getProductos().add(nuevoProducto);
                    importados++;
                    System.out.println("Producto importado: " + codigo);
                }

            }

            System.out.println("Productos encontrados: " + encontrados);
            System.out.println("Productos importados: " + importados);
            System.out.println("Productos duplicados: " + duplicados);
            System.out.println("Productos rechazados: " + rechazados);
        } catch (Exception e) {
            System.out.println("Error al leer el XML: " + e.getMessage());
        }
    }

    public boolean validarProducto(Producto producto) {
        if (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty()) {
            return false;
        }

        String codigo = producto.getCodigo().trim();

        if (codigo.length() > 12 || !codigo.matches("[A-Z0-9-]+")) {
            return false;
        }

        if (producto.getNombre() == null) {
            return false;
        }

        String nombre = producto.getNombre().trim();

        if (nombre.length() < 3 || nombre.length() > 100) {
            return false;
        }

        if (producto.getPrecio() == null
                || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        return producto.getStock() >= 0 && producto.getCategoriaId() > 0 && producto.getProveedorId() > 0;
    }

}