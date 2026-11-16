# DataStock – Proyecto Integrador (Entrega 3 – UT3)

**Módulo:** Acceso a Datos · 2.º DAM · IES Isidra de Guzmán · Curso 2026–2027  
**Lenguaje:** Java 21 · **Construcción:** Maven · **Interfaz:** consola

## 1. ¿Qué es este proyecto?

DataStock es una práctica **individual e integradora** desarrollada a lo largo de tres unidades de trabajo:

- **UT1 – Ficheros:** importar JSON/XML, consultar en memoria, exportar, serializar y desarrollar dos actividades independientes (XPath y `RandomAccessFile`).
- **UT2 – JDBC:** incorporar MySQL, operaciones CRUD SQL, consultas y transacciones con commit/rollback.
- **UT3 – Componentes:** reorganizar el acceso a ficheros JSON y la consulta JDBC por código en componentes reutilizables desacoplados de la interfaz de consola.

## 2. Requisitos previos

- JDK **21** instalado y seleccionado en el IDE (`java -version`).
- Maven instalado, o un IDE con integración Maven.
- Servidor **MySQL** activo con la base de datos `datastock` creada mediante el script de `material_referencia/DataStock_BD_MySQL.sql` y credenciales configuradas en `config/db.properties`.
- Consola con UTF-8 para ver correctamente tildes y símbolos.

## 3. Cómo abrir y ejecutar

1. Abre **la carpeta que contiene `pom.xml`** (`DataStock_UT1_Proyecto_Inicial`) como proyecto Maven en VS Code, IntelliJ IDEA o Eclipse.
2. Espera a que Maven descargue las dependencias configuradas (Gson, conector JDBC de MySQL y JUnit).
3. Ejecuta `es.iesisidra.datastock.App` desde el IDE, o en una terminal situada en la raíz del proyecto:

```bash
mvn clean compile
mvn exec:java

Para ejecutar las actividades complementarias (todavía pendientes de desarrollar):

```bash
mvn exec:java -Dexec.mainClass=es.iesisidra.datastock.complementarias.XPathActividad
mvn exec:java -Dexec.mainClass=es.iesisidra.datastock.complementarias.AccesoAleatorioActividad
```

> Si el IDE muestra caracteres de control ANSI, desactiva el color al ejecutar Java con `-Ddatastock.color=false`, o configura la variable de entorno `NO_COLOR=1`.

## 4. Organización del proyecto

```text
DataStock_UT1_Proyecto_Inicial/
├── pom.xml
├── README.md
├── .gitignore
├── config/
│   └── db.properties                  # Configuración de conexión JDBC a MySQL
├── datos/
│   ├── ejemplos/                      # JSON/XML de prueba (válidos, con errores y mal formados)
│   └── salidas/                       # Exportaciones y binarios generados
├── material_referencia/               # Modelo relacional y script SQL
└── src/main/java/es/iesisidra/datastock/
    ├── App.java                       # Punto de entrada
    ├── modelo/                        # Clases de dominio (Producto, ProductoDetalle, Categoria, Proveedor, MovimientoStock)
    ├── dao/
    │   ├── ConexionBD.java            # Gestión de conexiones MySQL
    │   ├── ProductoDAO.java           # Operaciones CRUD y listados de productos
    │   └── ConsultaProductoJdbc.java  # [UT3] Componente reutilizable de consulta JDBC por código
    ├── servicio/
    │   ├── Inventario.java            # Colección compartida en memoria
    │   ├── ImportarJson.java          # Implementación original UT1
    │   └── LectorProductosJson.java   # [UT3] Componente reutilizable de lectura y validación JSON
    ├── ui/
    │   ├── MenuPrincipal.java         # Menú principal (opción 2 integrada con LectorProductosJson)
    │   ├── MenuMySQL.java             # Menú de gestión MySQL (UT2)
    │   └── MenuConsultas.java         # Submenú 7.5 (opción 2 integrada con ConsultaProductoJdbc)
    ├── util/                          # Utilidades de consola, colores y traducción de errores
    └── validaciones/                  # Validaciones de formato y dominio
```

## 5. Menús que ya aparecen

**Menú principal:**

```text
1. Explorar ficheros y directorios (UT1)
2. Importar productos desde JSON (UT1)     -> Delega en LectorProductosJson
3. Importar productos desde XML (UT1)
4. Consultar productos (UT1)
5. Exportar productos (UT1)
6. Guardar y recuperar inventario (UT1)
7. Gestionar base de datos MySQL (UT2)
0. Salir
```

1. Listar todos los productos
2. Buscar producto por código              -> Delega en ConsultaProductoJdbc
3. Buscar productos por nombre
4. Consultar productos con stock inferior a un umbral
0. Volver al menú MySQL

`RF-FIC-07` (XPath) y `RF-FIC-08` (`RandomAccessFile`) se ejecutan en clases independientes.

 ## 6. Cuestiones a responder en el README

## 1. Ventajas e inconvenientes de separar la lectura JSON del menú
Separar la lectura y validación del fichero JSON en la clase independiente LectorProductosJson aporta claridad y reutilización: el componente recibe únicamente una ruta (Path), valida la estructura del JSON y las reglas de negocio de Producto, y devuelve una lista de productos válidos o lanza excepciones controladas. Al no depender de Scanner, de System.out ni del Inventario en memoria, esta misma clase puede reutilizarse desde una interfaz gráfica, un servicio web o una batería de pruebas unitarias sin modificar su código.

Como contrapartida, dividir la lógica en más clases introduce un ligero coste estructural: aumenta el número de ficheros a mantener en el proyecto, obliga a diseñar contratos claros entre capas (qué parámetros recibe cada método y qué excepciones propaga) y requiere trasladar al menú la gestión de duplicados en el inventario y el formateo de los mensajes de salida ([OK] y [ERROR]).

## 2. Importancia de separar una consulta JDBC de la presentación por consola
Aislar la consulta SQL en ConsultaProductoJdbc evita mezclar código de acceso a datos (Connection, PreparedStatement, ResultSet) con lógica de interfaz de usuario. De este modo, el componente se encarga exclusivamente de obtener la conexión desde ConexionBD, ejecutar la consulta parametrizada de forma segura frente a inyección SQL, cerrar automáticamente los recursos mediante try-with-resources y devolver un Optional<ProductoDetalle> que indica de forma explícita si el producto existe o no. La presentación por consola queda centralizada en el menú, que decide cómo mostrar la ficha del producto, los avisos o los errores SQL traducidos al usuario.

## 3. Herramientas utilizadas en el desarrollo
IDE (Visual Studio Code): utilizado para escribir y refactorizar el código Java, gestionar las ramas y commits de Git, y ejecutar y depurar la aplicación.

Apache Maven: empleado para gestionar la estructura estándar del proyecto, compilar con Java 21, resolver las dependencias externas (Gson y el driver JDBC de MySQL) y ejecutar plugins como Javadoc.

Javadoc: utilizado para documentar las clases públicas de los nuevos componentes (LectorProductosJson y ConsultaProductoJdbc) y sus métodos, especificando su finalidad, parámetros (@param), valor de retorno (@return) y excepciones lanzadas (@throws).

Git / GitHub: empleado para controlar las versiones mediante commits descriptivos sobre una rama de desarrollo y etiquetar la entrega final de la UT3.

## 4. Opciones de DataStock modificadas para integrar los componentes
Opción 2 del Menú Principal (MenuPrincipal.java – Importar productos desde JSON): ahora instancia LectorProductosJson, le pasa la ruta (Path) introducida por el usuario, incorpora al Inventario los productos válidos recibidos omitiendo códigos duplicados y muestra el resultado [OK] en verde o [ERROR] en rojo.

Opción 7 > 5 > 2 (MenuConsultas.java – Buscar producto por código en MySQL): ahora delega la búsqueda en ConsultaProductoJdbc.buscarPorCodigo(cod) y, a partir del Optional<ProductoDetalle> devuelto, presenta la ficha del producto con [OK] en verde, un aviso si no existe o un [ERROR] en rojo ante entradas inválidas o fallos de conexión.

## Fin de la práctica DataStock
