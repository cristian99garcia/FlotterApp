package com.carrotgames.flotter;

/**
 * Created by cristian on 10/10/16.
 */

public class FunctionItem {

    private String nombre;
    private String expresion;
    private int color;

    public FunctionItem(String nombre, String expresion, int color) {
        this.nombre = nombre;
        this.expresion = expresion;
        this.color = color;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getExpresion() {
        return this.expresion;
    }

    public void setExpresion(String expresion) {
        this.expresion = expresion;
        // actualizar
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
        // actualizar
    }
}
