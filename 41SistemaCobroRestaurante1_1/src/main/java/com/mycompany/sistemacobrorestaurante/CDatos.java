/*
 * Program created on 05/19/2024 by:
 * Cristian David Gutiérrez Fernández
 * Diana Laura Sandoval González
 * Arturo Uriel Sosa Ortiz
 * Anthony Alexander Zarate Bautista
 */

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemacobrorestaurante;

import java.awt.Font;
import java.awt.Image;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author crist
 */
public class CDatos {
    CConexion conex = new CConexion(); // Obtener la instancia de la clase CConexion para establecer conexión a la base de datos
    int idComida; // Variable para almacenar el ID de una comida

    // Getters y setters para la variable idComida
    public int getIdComida() {
        return idComida;
    }

    public void setIdComida(int idComida) {
        this.idComida = idComida;
    }
    
    // Método para visualizar datos en una tabla
    public void visualizar_tabla(JTable tabla) {
        try {
            ResultSet rs = conex.visualizar(); // Obtiene un ResultSet con los datos a visualizar desde la base de datos

            if (rs == null) {
                // Manejo del caso cuando rs es nulo
                throw new SQLException("El ResultSet es nulo. La consulta falló.");
            }

            // Configuración del modelo de la tabla
            DefaultTableModel dt = new DefaultTableModel(); // Crea un modelo de tabla por defecto
            dt.addColumn("ID"); // Agrega columnas al modelo
            dt.addColumn("Imagen");
            dt.addColumn("Nombre");
            dt.addColumn("Precio");

            while (rs.next()) {
                Object[] fila = new Object[4]; // Crea un arreglo para almacenar los datos de cada fila

                fila[0] = rs.getInt("idComida"); // Obtiene el ID de la comida desde el ResultSet
                Blob blob = rs.getBlob("Imagen"); // Obtiene la imagen desde el ResultSet
                if (blob != null) {
                    // Muestra la imagen si existe
                    byte[] imageBytes = blob.getBytes(1, (int) blob.length());
                    ImageIcon imageIcon = new ImageIcon(imageBytes);
                    Image image = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    JLabel lbl = new JLabel(new ImageIcon(image));
                    fila[1] = lbl;
                } else {
                    fila[1] = "No imagen";
                }
                fila[2] = rs.getString("Nombre"); // Obtiene el nombre de la comida desde el ResultSet
                fila[3] = rs.getDouble("PrecioComida"); // Obtiene el precio de la comida desde el ResultSet

                dt.addRow(fila); // Agrega la fila al modelo de tabla
            }

            // Configuración adicional de la tabla
            tabla.setModel(dt); // Establece el modelo en la tabla
            tabla.setRowHeight(120); // Define la altura de las filas
            tabla.setFont(new Font("Serif", Font.PLAIN, 24)); // Cambia el tamaño de la fuente del contenido de la tabla
            JTableHeader header = tabla.getTableHeader(); // Obtiene el encabezado de la tabla
            header.setFont(new Font("Serif", Font.BOLD, 20)); // Cambia el tamaño de la fuente del encabezado de la tabla

            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(dt); // Crea un sorter para la tabla
            tabla.setRowSorter(sorter); // Establece el sorter en la tabla
            sorter.setSortKeys(List.of(new RowSorter.SortKey(2, SortOrder.ASCENDING))); // Define la ordenación inicial por la columna "Nombre"

            // Asigna el renderizador personalizado a la columna de imagen
            tabla.getColumnModel().getColumn(1).setCellRenderer(new TablaImagen());

        } catch (SQLException e) {
            // Captura de errores al visualizar la tabla
            System.out.println("Error al visualizar la tabla: " + e.toString());
        } catch (Exception e) {
            // Captura de otros errores posibles
            System.out.println("Error inesperado al visualizar la tabla: " + e.toString());
        }
    }
    
    // Método para insertar un ticket en la base de datos
    public void insertTicket(String nombre, double precio, int cantidad) {
        CConexion conexion = new CConexion();
        try (Connection con = conexion.establecerConexion();
             PreparedStatement statement = con.prepareStatement("INSERT INTO ticket (fecha, hora, producto, cantidad, totalticket) VALUES (?, ?, ?, ?, ?)")) {

            // Obtiene la fecha y hora actual del sistema
            java.util.Date fechaActual = new java.util.Date(); // Utiliza java.util.Date para obtener la fecha actual
            java.sql.Date sqlFechaActual = new java.sql.Date(fechaActual.getTime()); // Convierte la fecha actual a java.sql.Date
            java.sql.Time sqlHoraActual = new java.sql.Time(fechaActual.getTime()); // Convierte la hora actual a java.sql.Time

            // Establece los valores de los parámetros de la sentencia SQL
            statement.setDate(1, sqlFechaActual); // Fecha
            statement.setTime(2, sqlHoraActual); // Hora
            statement.setString(3, nombre); // Producto
            statement.setInt(4, cantidad); // Cantidad
            statement.setDouble(5, precio * cantidad); // Total del ticket

            // Ejecuta la sentencia SQL para insertar los datos
            statement.executeUpdate();

            // Muestra un mensaje de éxito
            JOptionPane.showMessageDialog(null, "Datos insertados correctamente");

        } catch (SQLException e) {
            // Maneja cualquier excepción que pueda ocurrir durante la inserción
            System.out.println("Error al insertar datos: " + e.toString());
        }
    }
    
    // Método para eliminar un menú de la base de datos
    public void deleteMenu(int paramIdComida) {
        String consult = "DELETE FROM comida WHERE idcomida = ?"; // Consulta SQL para eliminar el menú

        // Obtener la conexión utilizando el método establecerConexion de la clase CConexion
        CConexion conexion = new CConexion();
        try (Connection connection = conexion.establecerConexion();
             PreparedStatement ps = connection.prepareStatement(consult)) {

            ps.setInt(1, paramIdComida); // Establece el parámetro de la consulta
            int rowsAffected = ps.executeUpdate(); // Ejecuta la consulta y obtiene el número de filas afectadas

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Eliminación exitosa"); // Muestra un mensaje de éxito si se eliminó al menos una fila
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró ningún menú con el ID especificado"); // Muestra un mensaje si no se encontró ninguna fila para eliminar
            }

        } catch (SQLException e) {
            // Captura cualquier error que pueda ocurrir durante la eliminación
            System.out.println("Error al eliminar, error: " + e.toString());
        }
    }
    
    // Método para mostrar los tickets en una tabla
    public void showTicket(JTable paramTableProducto) {
        DefaultTableModel model = new DefaultTableModel(); // Crea un nuevo modelo de tabla

        // Agrega las columnas al modelo de tabla
        model.addColumn("ID");
        model.addColumn("Fecha");
        model.addColumn("Hora");
        model.addColumn("Producto");
        model.addColumn("Cantidad");
        model.addColumn("Monto");

        paramTableProducto.setModel(model); // Establece el modelo en la tabla

        String mysql = "SELECT * FROM ticket;"; // Consulta SQL para seleccionar todos los registros de la tabla 'ticket'

        try (Connection con = conex.establecerConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(mysql)) {

            // Itera sobre cada fila del resultado y agrega los datos al modelo de tabla
            while (rs.next()) {
                Object[] datos = {
                    rs.getInt("idTicket"),       // Asegúrate de que estos nombres de columna coincidan con los de tu base de datos
                    rs.getDate("fecha"),
                    rs.getTime("hora"),
                    rs.getString("producto"),
                    rs.getInt("cantidad"),
                    rs.getDouble("totalticket")
                };
                model.addRow(datos);
            }

            // Crea un ordenador de filas para la tabla
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
            paramTableProducto.setRowSorter(sorter);
            sorter.setSortKeys(List.of(new RowSorter.SortKey(1, SortOrder.ASCENDING))); // Define la ordenación inicial por la columna "Fecha"
            
            paramTableProducto.setRowHeight(80); // Establece la altura de las filas de la tabla

            // Cambia el tamaño de la fuente del contenido de la tabla
            paramTableProducto.setFont(new Font("Serif", Font.PLAIN, 18)); // Puedes cambiar "Serif" y 18 por la fuente y tamaño que desees

            // Centra el contenido de las celdas en la tabla
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            for (int i = 0; i < paramTableProducto.getColumnCount(); i++) {
                paramTableProducto.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            

        } catch (SQLException e) {
            // Manejo de errores al mostrar los registros
            System.out.println("No se pudo mostrar los registros, error: " + e.toString());
        }
    }
    
    // Método para editar la cantidad y el monto de un ticket
    public void editTicket(int idTicket, int nuevaCantidad) {
        // Consulta para obtener el precio del producto de la tabla comida
        String consultaPrecio = "SELECT PrecioComida FROM comida WHERE Nombre = (SELECT producto FROM ticket WHERE idTicket = ?)";
        // Consulta para actualizar la cantidad y el monto en la tabla ticket
        String consultaActualizar = "UPDATE ticket SET cantidad = ?, totalticket = ? WHERE idTicket = ?";

        try (Connection con = conex.establecerConexion()) {
            if (con == null || con.isClosed()) {
                throw new SQLException("La conexión no está disponible.");
            }

            con.setAutoCommit(false); // Comienza una transacción

            try (PreparedStatement psPrecio = con.prepareStatement(consultaPrecio);
                 PreparedStatement psActualizar = con.prepareStatement(consultaActualizar)) {

                // Obtener el precio del producto de la tabla comida
                psPrecio.setInt(1, idTicket);
                try (ResultSet rsPrecio = psPrecio.executeQuery()) {
                    if (rsPrecio.next()) {
                        double precioProducto = rsPrecio.getDouble("PrecioComida");
                        double nuevoMonto = precioProducto * nuevaCantidad;

                        // Actualizar la cantidad y el monto en la tabla ticket
                        psActualizar.setInt(1, nuevaCantidad);
                        psActualizar.setDouble(2, nuevoMonto);
                        psActualizar.setInt(3, idTicket);

                        int filasActualizadas = psActualizar.executeUpdate();

                        if (filasActualizadas > 0) {
                            con.commit(); // Confirma la transacción
                            JOptionPane.showMessageDialog(null, "Cantidad y monto actualizados correctamente");
                        } else {
                            con.rollback(); // Revierte la transacción
                            JOptionPane.showMessageDialog(null, "No se encontró el ticket");
                        }
                    } else {
                        con.rollback(); // Revierte la transacción
                        JOptionPane.showMessageDialog(null, "Producto no encontrado en la tabla comida");
                    }
                }
            } catch (SQLException e) {
                con.rollback(); // Revierte la transacción en caso de error
                throw e; // Relanza la excepción para que sea manejada fuera del bloque
            } finally {
                con.setAutoCommit(true); // Restaura el modo de auto-commit
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar la cantidad y el monto: " + e.toString());
        }
    }
    
    // Método para guardar un ticket de venta en un archivo de texto y en la tabla historial
    public void guardarTicket(String horaInicio, String horaFinal) {
        try {
            // Obtener la fecha actual para el nombre del archivo y para el historial
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String fechaActual = dateFormat.format(new Date());

            // Crear el objeto BufferedWriter para escribir en el archivo seleccionado
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Ticket");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));

            // Mostrar el diálogo de selección de archivo
            int userSelection = fileChooser.showSaveDialog(null);

            // Verificar si el usuario ha seleccionado un archivo y ha hecho clic en "Guardar"
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Obtener el archivo seleccionado por el usuario
                java.io.File fileToSave = fileChooser.getSelectedFile();

                // Crear el objeto BufferedWriter para escribir en el archivo seleccionado
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));

                // Escribir la fecha actual y el nombre del restaurante en el archivo
                writer.write("Fecha: " + fechaActual);
                writer.newLine();
                writer.write("Restaurante Caldo de Pollo");
                writer.newLine();
                writer.write("");
                writer.newLine();
                writer.write("******************************************************");
                writer.newLine();

                // Obtener los datos de los productos vendidos dentro del rango de hora especificado
                ResultSet rs = conex.visualizar2(horaInicio, horaFinal);

                // Escribir los datos de los productos en el archivo
                writer.write("Producto\tCantidad\tTotalTicket");
                writer.newLine();
                double total = 0.0;
                while (rs.next()) {
                    String producto = rs.getString("Producto");
                    int cantidad = rs.getInt("Cantidad");
                    double totalTicket = rs.getDouble("TotalTicket");
                    writer.write(producto + "\t" + cantidad + "\t" + totalTicket);
                    writer.newLine();
                    total += totalTicket;
                }

                // Escribir el total de la sumatoria de los precios de los productos vendidos
                writer.write("");
                writer.newLine();
                writer.write("******************************************************");
                writer.newLine();
                writer.write("");
                writer.newLine();
                writer.write("Total: " + total);

                // Cerrar el BufferedWriter
                writer.close();

                // Insertar los datos en la tabla historial
                conex.insertarHistorial(fechaActual, total);

                // Imprimir mensaje de éxito en la consola
                System.out.println("Ticket guardado correctamente en " + fileToSave.getAbsolutePath());
            }
        } catch (IOException | SQLException e) {
            // Manejar cualquier error ocurrido durante el proceso de guardar el ticket
            System.out.println("Error al guardar el ticket: " + e.getMessage());
        }
    }
    
    // Método para mostrar el historial en una tabla
    public void showHistorial(JTable paramTableHistorial) {
        // Crea un nuevo modelo de tabla
        DefaultTableModel model = new DefaultTableModel();

        // Agrega las columnas al modelo de tabla
        model.addColumn("Orden #");
        model.addColumn("Fecha");
        model.addColumn("Monto Ticket");

        // Establece el modelo en la tabla proporcionada como parámetro
        paramTableHistorial.setModel(model);

        // Consulta SQL para seleccionar todos los registros de la tabla 'historial'
        String mysql = "SELECT * FROM historial;";

        // Intenta establecer una conexión a la base de datos y ejecutar la consulta
        try (Connection con = conex.establecerConexion(); 
             Statement st = con.createStatement(); 
             ResultSet rs = st.executeQuery(mysql)) {

            // Itera sobre cada fila del resultado y agrega los datos al modelo de tabla
            while (rs.next()) {
                Object[] datos = {
                    rs.getInt("idHistorial"), // Obtiene el valor de la columna 'idHistorial' como un entero
                    rs.getDate("fecha"), // Obtiene el valor de la columna 'fecha' como una fecha
                    rs.getDouble("MontoTicket") // Obtiene el valor de la columna 'MontoTicket' como un doble
                };
                model.addRow(datos); // Agrega los datos al modelo de la tabla
            }

            // Crea un ordenador de filas para la tabla
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
            // Establece el ordenador de filas en la tabla
            paramTableHistorial.setRowSorter(sorter);
            // Define la ordenación inicial por la columna 'Fecha' en orden ascendente
            sorter.setSortKeys(List.of(new RowSorter.SortKey(1, SortOrder.ASCENDING)));

            // Establece la altura de las filas de la tabla
            paramTableHistorial.setRowHeight(80);

            // Cambia el tamaño de la fuente del contenido de la tabla
            paramTableHistorial.setFont(new Font("Serif", Font.PLAIN, 18)); // Puedes cambiar "Serif" y 18 por la fuente y tamaño que desees

            // Centra el contenido de las celdas en la tabla
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            // Aplica el renderizado centrado a cada columna de la tabla
            for (int i = 0; i < paramTableHistorial.getColumnCount(); i++) {
                paramTableHistorial.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        } catch (SQLException e) {
            // Manejo de errores al mostrar los registros
            System.out.println("No se pudo mostrar los registros, error: " + e.toString());
        }
    }
    
    // Método para mostrar la suma de las ventas totales en un JTextField
    public void ventaTotal(JTextField paramTotal) {
        // Inicializa la variable 'total' a 0.0, que almacenará la suma total de las ventas
        double total = 0.0;

        // Crea un objeto 'Date' con la fecha actual
        Date hoy = new Date();

        // Define el formato de fecha como 'yyyy/MM/dd'
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        // Convierte la fecha actual al formato definido y la almacena en 'fechaHoy'
        String fechaHoy = sdf.format(hoy);

        // Consulta SQL para obtener la suma de 'MontoTicket' del historial donde la fecha coincide con la fecha actual
        String query = "SELECT SUM(MontoTicket) AS total FROM historial WHERE DATE(Fecha) = ?";

        // Maneja la conexión y la consulta SQL usando try-with-resources para asegurar el cierre de recursos
        try (Connection conn = conex.establecerConexion();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Establece el parámetro de la consulta SQL con la fecha actual
            stmt.setString(1, fechaHoy);

            // Ejecuta la consulta y obtiene el resultado
            ResultSet rs = stmt.executeQuery();

            // Si hay un resultado, obtiene el valor de 'total' de la consulta y lo asigna a la variable 'total'
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            // Imprime el stack trace si ocurre una excepción SQL
            e.printStackTrace();
        }

        // Actualiza el JTextField 'paramTotal' con el total de ventas, formateado a dos decimales
        paramTotal.setText(String.format("%.2f", total));
    }
    
    // Método para guardar las ventas toales del día en un archivo de texto
    public void guardarVentasTotales() {
        try {
            // Obtener la fecha actual para el nombre del archivo
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String fechaActual = dateFormat.format(new Date());

            // Crear el objeto JFileChooser para seleccionar el archivo de destino
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Ventas Totales");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));

            // Mostrar el diálogo de selección de archivo
            int userSelection = fileChooser.showSaveDialog(null);

            // Verificar si el usuario ha seleccionado un archivo y ha hecho clic en "Guardar"
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Obtener el archivo seleccionado por el usuario
                java.io.File fileToSave = fileChooser.getSelectedFile();

                // Crear el objeto BufferedWriter para escribir en el archivo seleccionado
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));

                // Escribir la fecha actual y el nombre del restaurante en el archivo
                writer.write("Fecha: " + fechaActual);
                writer.newLine();
                writer.write("Restaurante Caldo de Pollo");
                writer.newLine();
                writer.write("");
                writer.newLine();
                writer.write("******************************************************");
                writer.newLine();

                // Obtener los datos de ventas totales
                ResultSet rs = conex.obtenerVentasTotales();

                // Verificar si el ResultSet es null
                if (rs != null) {
                    // Escribir los datos de ventas totales en el archivo
                    writer.write("Orden #\tFecha\t\tMonto Total");
                    writer.newLine();
                    double sumaTotal = 0.0; // Inicializar la suma total
                    while (rs.next()) {
                        int idHistorial = rs.getInt("idHistorial");
                        String fecha = rs.getString("Fecha");
                        double montoTotal = rs.getDouble("MontoTicket");
                        writer.write(idHistorial + "\t" + fecha + "\t" + montoTotal);
                        writer.newLine();
                        sumaTotal += montoTotal; // Sumar al total
                    }
                    // Escribir la suma total al final del archivo
                    writer.write("");
                    writer.newLine();
                    writer.write("******************************************************");
                    writer.newLine();
                    writer.write("");
                    writer.newLine();
                    writer.write("Total de ventas: " + sumaTotal);
                } else {
                    // Indicar que no hay datos para mostrar
                    writer.write("No hay datos disponibles.");
                    writer.newLine();
                }

                // Cerrar el BufferedWriter
                writer.close();

                // Imprimir mensaje de éxito en la consola
                System.out.println("Ventas totales guardadas correctamente en " + fileToSave.getAbsolutePath());
            }
        } catch (IOException | SQLException e) {
            // Manejar cualquier error ocurrido durante el proceso de guardar las ventas totales
            System.out.println("Error al guardar las ventas totales: " + e.getMessage());
        }
    }
}
