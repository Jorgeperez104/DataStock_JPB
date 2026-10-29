package es.iesisidra.datastock.servicio;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.iesisidra.datastock.modelo.Producto;
import es.iesisidra.datastock.util.LectorConsola;

public class ExportarXml {

    private final Inventario inventario;

    public ExportarXml(Inventario inventario) {
        this.inventario = inventario;

    }

    public void exportar(String ruta) {
        if (inventario.getProductos().isEmpty()) {
            System.out.println("El inventario está vacío.");

        } else {

            File fichero = new File(ruta);
            File directorio = fichero.getAbsoluteFile().getParentFile();

            if (!directorio.exists() || !directorio.isDirectory()) {
                System.out.println("El directorio no existe.");

            } else {
                if (fichero.exists()) {
                    String respuesta = LectorConsola.leerTexto("El fichero ya existe ¿Quieres sobrescribirlo? (S/N): ");

                    if (!respuesta.equalsIgnoreCase("S")) {
                        System.out.println("Exportación cancelada.");
                        return;
                    }
                }

                try {

                    DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
                    DocumentBuilder constructor = fabrica.newDocumentBuilder();

                    Document documento = constructor.newDocument();
                    Element raiz = documento.createElement("catalogo");
                    documento.appendChild(raiz);

                    ArrayList<Producto> productosOrdenados = new ArrayList<>(inventario.getProductos());

                    for (int posicionActual = 0; posicionActual < productosOrdenados.size() - 1; posicionActual++) {

                        for (int posicionComparada = 0; posicionComparada < productosOrdenados.size() - 1
                                - posicionActual; posicionComparada++) {

                            if (productosOrdenados.get(posicionComparada).getCodigo().compareToIgnoreCase(
                                    productosOrdenados.get(posicionComparada + 1).getCodigo()) > 0) {

                                Producto productoAuxiliar = productosOrdenados.get(posicionComparada);

                                productosOrdenados.set(posicionComparada,
                                        productosOrdenados.get(posicionComparada + 1));

                                productosOrdenados.set(posicionComparada + 1, productoAuxiliar);
                            }
                        }
                    }

                    for (Producto producto : productosOrdenados) {
                        Element elementoProducto = documento.createElement("producto");
                        raiz.appendChild(elementoProducto);

                        Element codigo = documento.createElement("codigo");
                        codigo.setTextContent(producto.getCodigo());
                        elementoProducto.appendChild(codigo);

                        Element nombre = documento.createElement("nombre");
                        nombre.setTextContent(producto.getNombre());
                        elementoProducto.appendChild(nombre);

                        Element precio = documento.createElement("precio");
                        precio.setTextContent(producto.getPrecio().toString());
                        elementoProducto.appendChild(precio);

                        Element stock = documento.createElement("stock");
                        stock.setTextContent(String.valueOf(producto.getStock()));
                        elementoProducto.appendChild(stock);

                        Element categoriaId = documento.createElement("categoriaId");
                        categoriaId.setTextContent(String.valueOf(producto.getCategoriaId()));
                        elementoProducto.appendChild(categoriaId);

                        Element proveedorId = documento.createElement("proveedorId");
                        proveedorId.setTextContent(String.valueOf(producto.getProveedorId()));
                        elementoProducto.appendChild(proveedorId);
                    }

                    TransformerFactory fabricaTransformador = TransformerFactory.newInstance();
                    Transformer transformador = fabricaTransformador.newTransformer();

                    transformador.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformador.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

                    DOMSource origen = new DOMSource(documento);
                    StreamResult destino = new StreamResult(fichero);

                    transformador.transform(origen, destino);

                    System.out.println("Productos exportados correctamente.");
                    System.out.println("Archivo: " + fichero.getAbsolutePath());
                    System.out.println("Total de productos exportados: " + inventario.getProductos().size());

                } catch (Exception e) {

                    System.out.println("Error al crear el XML: " + e.getMessage());
                }
            }
        }
    }
}
