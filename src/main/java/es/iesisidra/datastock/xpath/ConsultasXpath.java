package es.iesisidra.datastock.xpath;

import java.io.File;
import java.util.Scanner;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ConsultasXpath {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Introduce la ruta: ");
        String ruta = sc.nextLine();

        File fichero = new File(ruta);

        if (!fichero.exists() || !fichero.isFile()) {
            System.out.println("Error: el fichero no existe.");
        } else {
            try {
                Document documento = cargarXML(fichero);

                if (!documento.getDocumentElement().getNodeName().equals("catalogo")) {
                    System.out.println("Error: la raíz del XML debe ser catalogo.");
                } else {
                    System.out.println("XML cargado correctamente.");

                    int opcion = -1;

                    while (opcion != 0) {
                        mostrarMenu();
                        opcion = Integer.parseInt(sc.nextLine());

                        switch (opcion) {
                            case 1:
                                buscarPorCodigo(documento, sc);
                                break;
                            case 2:
                                buscarPorCategoria(documento, sc);
                                break;
                            case 3:
                                buscarPorPrecio(documento, sc);
                                break;
                            case 4:
                                contarProductos(documento);
                                break;
                            case 0:
                                System.out.println("Saliendo...");
                                break;
                            default:
                                System.out.println("Opción no válida.");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error al leer el XML: " + e.getMessage());
            }
        }

        sc.close();
    }

    private static Document cargarXML(File fichero) throws Exception {
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();

        fabrica.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        fabrica.setFeature(
                "http://apache.org/xml/features/disallow-doctype-decl", true);
        fabrica.setFeature(
                "http://xml.org/sax/features/external-general-entities", false);
        fabrica.setFeature(
                "http://xml.org/sax/features/external-parameter-entities", false);

        DocumentBuilder constructor = fabrica.newDocumentBuilder();

        return constructor.parse(fichero);
    }

    private static void mostrarMenu() {
        System.out.println("\n===== CONSULTAS XPATH =====");
        System.out.println("1. Buscar producto por código");
        System.out.println("2. Buscar productos por categoría");
        System.out.println("3. Buscar productos por precio superior");
        System.out.println("4. Contar productos");
        System.out.println("0. Salir");
        System.out.print("Elige una opción: ");
    }

    private static void buscarPorCodigo(Document documento, Scanner sc) {
        System.out.print("Introduce el código del producto: ");
        String codigoBuscar = sc.nextLine().trim();

        try {

            XPath xpath = XPathFactory.newInstance().newXPath();

            NodeList productos = (NodeList) xpath.evaluate("/catalogo/producto", documento, XPathConstants.NODESET);

            boolean encontrado = false;

            for (int posicion = 0; posicion < productos.getLength(); posicion++) {
                Element producto = (Element) productos.item(posicion);

                String codigo = producto.getElementsByTagName("codigo").item(0).getTextContent().trim();

                if (codigo.equalsIgnoreCase(codigoBuscar)) {
                    System.out.println("Producto encontrado:");
                    System.out.println("Código: " + codigo);
                    System.out.println("Nombre: " + producto.getElementsByTagName("nombre").item(0).getTextContent());
                    System.out.println(
                            "Precio: " + producto.getElementsByTagName("precio").item(0).getTextContent() + " €");
                    System.out.println("Stock: " + producto.getElementsByTagName("stock").item(0).getTextContent());

                    encontrado = true;
                }
            }

            if (!encontrado) {
                System.out.println("No se ha encontrado ningún producto con ese código.");
            }

        } catch (Exception e) {
            System.out.println("Error al realizar la consulta.");
        }
    }

    private static void buscarPorCategoria(Document documento, Scanner sc) {
        System.out.print("Introduce el ID de la categoría: ");
        String categoriaBuscar = sc.nextLine().trim();

        if (!categoriaBuscar.matches("[0-9]+")) {
            System.out.println("Error: la categoría debe ser un número entero.");

        } else {

            try {
                XPath xpath = XPathFactory.newInstance().newXPath();

                NodeList productos = (NodeList) xpath.evaluate(
                        "/catalogo/producto[categoriaId=" + categoriaBuscar + "]", documento, XPathConstants.NODESET);

                if (productos.getLength() == 0) {
                    System.out.println("No se han encontrado productos de esa categoría.");

                } else {

                    for (int posicion = 0; posicion < productos.getLength(); posicion++) {
                        Element producto = (Element) productos.item(posicion);

                        System.out
                                .println("Código: " + producto.getElementsByTagName("codigo").item(0).getTextContent());
                        System.out
                                .println("Nombre: " + producto.getElementsByTagName("nombre").item(0).getTextContent());
                        System.out.println(
                                "Categoría: " + producto.getElementsByTagName("categoriaId").item(0).getTextContent());
                        System.out.println("--------------------");
                    }
                }

            } catch (Exception e) {
                System.out.println("Error al realizar la consulta.");
            }
        }
    }

    private static void buscarPorPrecio(Document documento, Scanner sc) {
        System.out.print("Introduce el precio mínimo: ");
        String textoPrecio = sc.nextLine().trim();

        double precioBuscado;

        try {
            precioBuscado = Double.parseDouble(textoPrecio);

            if (precioBuscado < 0) {
                System.out.println("Error: el precio no puede ser negativo.");
                return;
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: introduce un precio válido.");
            return;
        }

        try {
            XPath xpath = XPathFactory.newInstance().newXPath();

            NodeList productos = (NodeList) xpath.evaluate(
                    "/catalogo/producto[precio > " + precioBuscado + "]",
                    documento,
                    XPathConstants.NODESET);

            if (productos.getLength() == 0) {
                System.out.println("No hay productos con un precio superior a esa cantidad.");
            } else {

                for (int posicion = 0; posicion < productos.getLength(); posicion++) {
                    Element producto = (Element) productos.item(posicion);

                    System.out.println("Código: " + producto.getElementsByTagName("codigo").item(0).getTextContent());
                    System.out.println("Nombre: " + producto.getElementsByTagName("nombre").item(0).getTextContent());
                    System.out.println(
                            "Precio: " + producto.getElementsByTagName("precio").item(0).getTextContent() + " €");
                    System.out.println("--------------------");
                }
            }

        } catch (Exception e) {
            System.out.println("Error al realizar la consulta.");
        }
    }

    private static void contarProductos(Document documento) {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();

            Double cantidad = (Double) xpath.evaluate(
                    "count(/catalogo/producto)",
                    documento,
                    XPathConstants.NUMBER);

            System.out.println("Número total de productos: " + cantidad.intValue());

        } catch (Exception e) {
            System.out.println("Error al contar los productos.");
        }
    }
}
