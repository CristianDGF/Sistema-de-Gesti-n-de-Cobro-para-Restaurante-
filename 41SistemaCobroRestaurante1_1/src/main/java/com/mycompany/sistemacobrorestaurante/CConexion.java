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

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author crist
 */
public class CConexion {
    // Declaración de una variable para la conexión a la base de datos
    java.sql.Connection conect = null;

    // Definición de las credenciales de la base de datos y otros parámetros de conexión
    String user = "root";     // Usuario de la base de datos
    String pass = "";         // Contraseña del usuario de la base de datos
    String bd = "bd_restaurant";  // Nombre de la base de datos
    String ip = "localhost";  // Dirección IP del servidor de la base de datos (localhost para una base de datos local)
    String port = "3306";     // Puerto en el que el servidor de base de datos escucha (3306 es el puerto por defecto para MySQL)

    // Cadena de conexión que combina los parámetros anteriores para formar la URL de conexión
    String cadena = "jdbc:mysql://" + ip + ":" + port + "/" + bd;

    // Método para establecer la conexión con la base de datos
    public java.sql.Connection establecerConexion() {
        try {
            // Cargar el controlador JDBC de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establecer la conexión utilizando DriverManager y la cadena de conexión, el usuario y la contraseña
            conect = DriverManager.getConnection(cadena, user, pass);

            // Mensaje opcional que indica que la conexión fue exitosa (comentado)
            // System.out.println("Conexión correcta a la BD");
        } catch (Exception e) {
            // Captura cualquier excepción que ocurra durante la conexión e imprime un mensaje de error
            System.out.println("Error al conectar la Base de Datos, error: " + e.toString());
        }
        // Retorna la conexión (si se estableció correctamente, de lo contrario, retornará null)
        return conect;
    }
    
    // Método para visualizar datos de la tabla 'Comida'
    public ResultSet visualizar() {
        Connection conex = establecerConexion();
        ResultSet rs = null;
        try {
            // Preparación y ejecución de la consulta SQL
            PreparedStatement ps = conex.prepareStatement("SELECT * FROM Comida");
            rs = ps.executeQuery();
        } catch (SQLException e) {
            // Captura de errores en la consulta SQL con más detalles
            System.out.println("Error de consulta: " + e.getMessage());
        }
        return rs;
    }
    
    // Método para visualizar datos de la tabla 'Comida' dentro de un rango de horas
    public ResultSet visualizar2(String horaInicio, String horaFinal) {
        Connection conex = establecerConexion();
        ResultSet rs = null;
        try {
            // Preparación y ejecución de la consulta SQL con parámetros de hora
            String query = "SELECT * FROM ticket WHERE Hora BETWEEN ? AND ?";
            PreparedStatement ps = conex.prepareStatement(query);
            ps.setString(1, horaInicio);
            ps.setString(2, horaFinal);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            // Captura de errores en la consulta SQL con más detalles
            System.out.println("Error de consulta: " + e.getMessage());
        }
        return rs;
    }

    // Método para guardar una imagen en la base de datos junto con otros datos
    public void guardar_imagen(String ruta, String nombre, String precioComida) {
        Connection conex = establecerConexion();
        String insert = "INSERT INTO Comida(Imagen, Nombre, PrecioComida) VALUES (?, ?, ?)";
        FileInputStream fi = null;
        PreparedStatement ps = null;
        try {
            // Lectura del archivo de imagen desde la ruta especificada
            File file = new File(ruta);
            fi = new FileInputStream(file);

            // Preparación y ejecución de la consulta SQL para guardar la imagen
            ps = conex.prepareStatement(insert);
            ps.setBinaryStream(1, fi); // Se utiliza setBinaryStream para manejar la imagen
            ps.setString(2, nombre);
            ps.setString(3, precioComida);

            ps.executeUpdate(); // Ejecución de la inserción en la base de datos
        } catch (Exception e) {
            // Captura de errores en la inserción de la imagen
            System.out.println("Error al guardar: " + e.getMessage());
        } finally {
            // Cierre del flujo de entrada de la imagen
            if (fi != null) {
                try {
                    fi.close();
                } catch (Exception e) {
                    System.out.println("Error al cerrar el flujo de entrada: " + e.getMessage());
                }
            }
        }
    }
    
    // Método para insertar la fecha y el total del ticket en la tabla historial de la base de datos
    public void insertarHistorial(String fechaActual, double total) {
        Connection conn = establecerConexion(); // Usamos la conexión establecida en CConexion
        PreparedStatement stmt = null;
        try {
            // Preparar la consulta SQL para insertar en la tabla historial
            String sql = "INSERT INTO historial (Fecha, MontoTicket) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);

            // Establecer los valores de los parámetros de la consulta
            stmt.setString(1, fechaActual);
            stmt.setDouble(2, total);

            // Ejecutar la consulta
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Manejar cualquier error de SQL
            System.out.println("Error al insertar en historial: " + e.getMessage());
        } finally {
            // Cerrar la declaración
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar la declaración: " + e.getMessage());
                }
            }
        }
    }
    
    // Método para obtener el ResultSet de las ventas totales
    public ResultSet obtenerVentasTotales() {
        // Establecer la conexión a la base de datos
        Connection conex = establecerConexion();
        ResultSet rs = null;  // Declaración de la variable ResultSet para almacenar los resultados de la consulta

        try {
            // Preparar la consulta SQL para obtener las ventas totales
            String query = "SELECT idHistorial, Fecha, MontoTicket FROM historial";

            // Crear un PreparedStatement para ejecutar la consulta
            PreparedStatement ps = conex.prepareStatement(query);

            // Ejecutar la consulta y almacenar los resultados en el ResultSet
            rs = ps.executeQuery();
        } catch (SQLException e) {
            // Captura y manejo de cualquier excepción que ocurra durante la consulta SQL
            System.out.println("Error de consulta: " + e.getMessage());
        }

        // Retornar el ResultSet con los resultados de la consulta
        return rs;
    }
}
