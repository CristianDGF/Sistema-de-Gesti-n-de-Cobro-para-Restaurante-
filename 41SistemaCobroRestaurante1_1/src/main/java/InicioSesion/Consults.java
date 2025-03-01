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
package InicioSesion;

import com.mycompany.sistemacobrorestaurante.CConexion;
import com.mycompany.sistemacobrorestaurante.SelecMenu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author crist
 */
public class Consults {
    CConexion conex = new CConexion(); // Obtener la instancia de la clase CConexion
    
    private String user;
    private String pass;

    // Getters y setters para user y pass
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    
    // Método para crear un nuevo usuario en la Base de Datos
    public void InsertUser(JTextField paramUser, JPasswordField paramPass){
        // Obtiene el texto del campo de usuario y lo guarda usando el método setUser
        setUser(paramUser.getText());

        // Obtiene la contraseña del campo de contraseña, la convierte a String y la guarda usando el método setPass
        setPass(new String(paramPass.getPassword()));

        // Define la consulta SQL para insertar un nuevo usuario con su contraseña en la tabla 'users'
        String consult = "INSERT INTO users (usuario, contraseña) VALUES (?, ?)";

        // Intenta establecer la conexión y preparar la declaración SQL
        try(Connection cn = conex.establecerConexion();
            PreparedStatement ps = cn.prepareStatement(consult)) {

            // Asigna los valores de usuario y contraseña a los parámetros de la consulta
            ps.setString(1, getUser());
            ps.setString(2, getPass());

            // Ejecuta la actualización en la base de datos y guarda el resultado (número de filas afectadas)
            int result = ps.executeUpdate();

            // Si se insertó al menos una fila, muestra un mensaje de éxito
            if(result > 0) {
                JOptionPane.showMessageDialog(null, "Registrado correctamente");
            } else { // Si no se insertó ninguna fila, muestra un mensaje de error
                JOptionPane.showMessageDialog(null, "Error al registrar usuario", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch(SQLException e){
            // Si ocurre una excepción SQL, imprime el mensaje de error en la consola
            System.out.println("No se registró correctamente, error: " + e.getMessage());
        }
    }
    
    // Método para verificar si el usuario y contraseña son correctos
    public void Ingresar(String user, String pass, JFrame frame) {
        // Variables para almacenar el usuario y la contraseña correctos recuperados de la base de datos
        String usuarioCorrecto = null;
        String passCorrecto = null;

        // Intenta establecer la conexión y preparar la declaración SQL
        try (Connection cn = conex.establecerConexion();
             PreparedStatement pst = cn.prepareStatement("SELECT usuario, contraseña FROM users WHERE usuario = ?")) {

            // Asigna el valor del usuario al primer parámetro de la consulta
            pst.setString(1, user);

            // Ejecuta la consulta y obtiene el resultado
            ResultSet rs = pst.executeQuery();

            // Si hay un resultado, obtiene el usuario y la contraseña correctos de la base de datos
            if (rs.next()) {
                usuarioCorrecto = rs.getString(1);
                passCorrecto = rs.getString(2);
            }

            // Verifica si el usuario y la contraseña proporcionados coinciden con los valores correctos
            if (user.equals(usuarioCorrecto) && pass.equals(passCorrecto)) {
                // Muestra un mensaje de bienvenida si el login es correcto
                JOptionPane.showMessageDialog(null, "Login correcto, Bienvenido " + user, "Bienvenido", JOptionPane.PLAIN_MESSAGE);

                // Cierra el JFrame actual
                frame.dispose(); 

                // Abre un nuevo menú (SelecMenu)
                SelecMenu menu = new SelecMenu();
                menu.setVisible(true);
            } else {
                // Muestra un mensaje de error si el usuario o la contraseña son incorrectos
                JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            // Si ocurre una excepción SQL, imprime el mensaje de error en la consola
            System.out.println("Error: " + e.getMessage());
        }
    }
}
