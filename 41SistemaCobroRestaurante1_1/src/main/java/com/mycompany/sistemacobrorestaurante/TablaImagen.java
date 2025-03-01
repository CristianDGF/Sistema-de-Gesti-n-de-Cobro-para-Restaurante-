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

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author crist
 */
public class TablaImagen extends DefaultTableCellRenderer{
    
    // Sobrescribe el método getTableCellRendererComponent para personalizar la representación de las celdas de la tabla
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Verifica si el valor de la celda es una instancia de JLabel
        if (value instanceof JLabel) {
            JLabel lbl = (JLabel) value; // Convierte el valor en un JLabel
            return lbl; // Retorna el JLabel para ser mostrado en la celda de la tabla
        }
        // Si el valor no es un JLabel, se usa la implementación predeterminada de la clase base para renderizar la celda
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
