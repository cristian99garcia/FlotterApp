package com.carrotgames.flotter.pojo;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by cristian on 26/10/16.
 */

public class CalculadorPasoAPaso {

    public Funcion funcion;

    public CalculadorPasoAPaso(Funcion funcion) {
        this.funcion = funcion;
    }

    public ArrayList<String> getRaices() {
        Log.i("getRaices", funcion.getTipo() + " " + funcion.getExpresion());
        ArrayList pasos = new ArrayList<>();
        pasos.add("#Para obtener las raíces de cualquier función, se iguala su a 0, y se resuelve la ecuación.");

        switch (funcion.getTipo()) {
            case NULA:
                pasos.add("Imposible resolver, parece no ser una función.");
                break;

            case CONSTANTE:
                pasos.add("Las funciones constantes no poseen raíces, o todos los puntos que las componen lo son.");
                break;

            case LINEAL:
                pasos = getRaicesLineal();
                break;

            case CUADRATICA:
                pasos = getRaicesCuadratica();
                break;

            case CUBICA:
                break;

            case POLINOMICA:
                break;

            case EXPONENCIAL:
                //steps = getRaicesExponencial();
                break;

            case RACIONAL:
                break;

            case LOGARITMICA:
                break;

            case TRIGONOMETRICA:
                break;
        }

        return pasos;
    }

    public ArrayList<String> getOEEO() {
        ArrayList pasos = new ArrayList<String>();

        switch (funcion.getTipo()) {
            case NULA:
                break;

            case CONSTANTE:
                break;

            case LINEAL:
                pasos = getOEEOLineal();
                break;

            case CUADRATICA:
                pasos = getOEEOCuadratica();
                break;

            case CUBICA:
                break;

            case POLINOMICA:
                break;

            case EXPONENCIAL:
                break;

            case RACIONAL:
                break;

            case LOGARITMICA:
                break;

            case TRIGONOMETRICA:
                break;
        }

        return pasos;
    }

    private String analizarFloat(float numero) {
        String sNumero = Float.toString(numero).replace(".1111112", ".1111111")
                .replace(".2222223", ".2222222")
                .replace(".3333334", ".3333333")
                .replace(".4444445", ".4444444")
                .replace(".5555556", ".5555555")
                .replace(".6666667", ".6666666")
                .replace(".7777778", ".7777777")
                .replace(".8888889", ".8888888");

        if (sNumero.endsWith(".0")) {
            sNumero = sNumero.replace(".0", "");
        }

        return sNumero;
    }

    private ArrayList<String> getRaicesLineal() {
        ArrayList<String> pasos = new ArrayList<String>();
        pasos.add("#Como se quiere obtener la preimagen del 0, se iguala la expresión a 0 y se resuelve la ecuación:");
        String paso;

        // Expresión general: ax + b = 0
        // Resolusión: x = -b / a
        float[] valores = ObtenedorDeValores.lineal(funcion);
        float a, b;
        a = valores[0];
        b = valores[1];

        paso = analizarFloat(a) + "x";
        if (b == 0) {
            paso = paso + " = 0";
        } else if (b > 0) {
            paso = paso + " + " + analizarFloat(b) + " = 0";
        } else if (b < 0) {
            paso = paso + " - " + analizarFloat(Math.abs(b)) + " = 0";
        }

        pasos.add(paso);

        if (b == 0) {
            pasos.add("x = 0 / {" + analizarFloat(a) + "}");
        } else if (b > 0) {
            pasos.add("x = {-" + analizarFloat(b) + "} / {" + analizarFloat(a) + "}");
        } else if (b < 0) {
            pasos.add("x = {" + analizarFloat(Math.abs(b)) + "} / {" + analizarFloat(a) + "}");
        }

        pasos.add("x = " + analizarFloat(-b / a));

        return pasos;
    }

    private ArrayList<String> getRaicesCuadratica() {
        Log.i("getRaicesCuadratica", "1");
        ArrayList<String> pasos = new ArrayList<>();

        float[] valores = ObtenedorDeValores.cuadratica(funcion);
        float a, b, c;
        a = valores[0];
        b = valores[1];
        c = valores[2];

        Log.i("ObtenedorDeValores", "" + a + " " + b + " " + c);

        if (b != 0 && c != 0) {
            String signoB;
            String signoC;
            if (b > 0) {
                signoB = "+";
            } else {
                signoB = "-";
            }
            if (c > 0) {
                signoC = "+";
            } else {
                signoC = "-";
            }

            pasos.add(analizarFloat(a) + "x^2 " + signoB + " " + analizarFloat(Math.abs(b)) + "x " + signoC + " " + analizarFloat(Math.abs(c)) + " = 0");
            pasos.add("#Como la ecuación cuadrática es completa (es decir, los tres coeficientes son distintos a 0), usamos la formula de bhaskara:");
            pasos.add("x = { -b ±√{ b^2 - 4ac }} / { 2a }");

            String pA, pB, pC;
            if (a < 0) {
                pA = "(" + analizarFloat(a) + ")";
            } else {
                pA = analizarFloat(a);
            }

            if (b < 0) {
                pB = "(" + analizarFloat(b) + ")";
            } else {
                pB = analizarFloat(b);
            }

            if (c < 0) {
                pC = "(" + analizarFloat(c) + ")";
            } else {
                pC = analizarFloat(c);
            }

            float delta = ((float)Math.sqrt(Math.pow(b, 2) - (4 * a * c)));

            pasos.add("x = { " + analizarFloat(-b) + "±√{ " + pB + "^2 - 4·" + pA + "·" + pC + "}} / { 2·" + pA + " }");
            pasos.add("x = { " + analizarFloat(-b) + "±√{ " + analizarFloat((float)Math.pow(b, 2)) + analizarFloat(-4 * a * c) +"}} / { " + analizarFloat(2 * a) + " }");
            pasos.add("x_1 = " + analizarFloat((-b + delta) / (2 * a)));
            pasos.add("x_2 = " + analizarFloat((-b - delta) / (2 * a)));
        } else if (b != 0 && c == 0) {
            String signoB;
            if (b > 0) {
                signoB = "+";
            } else {
                signoB = "-";
            }

            pasos.add(analizarFloat(a) + "x^2 " + signoB + " " + analizarFloat(Math.abs(b)) + " = 0");
            pasos.add("#Como el término independiente vale 0, factorizamos la x:");
            pasos.add("x(" + analizarFloat(a) + " " + signoB + " " + analizarFloat(Math.abs(b)) + ")");
            pasos.add("#Por la propiedad Hankeliana:");
            pasos.add("x = 0");
            pasos.add(analizarFloat(a) + "x " + signoB + " " + analizarFloat(b) + " = 0");


            if (b == 0) {
                pasos.add("x = 0 / {" + analizarFloat(a) + "}");
            } else if (b > 0) {
                pasos.add("x = {-" + analizarFloat(b) + "} / {" + analizarFloat(a) + "}");
            } else if (b < 0) {
                pasos.add("x = {" + analizarFloat(Math.abs(b)) + "} / {" + analizarFloat(a) + "}");
            }

            pasos.add("x = " + analizarFloat(-b / a));

        } else if (b == 0 && c != 0) {
            String signoC, signoOC;
            if (c > 0) {
                signoC = "+";
                signoOC = "-";
            } else {
                signoC = "-";
                signoOC = "+";
            }

            pasos.add(analizarFloat(a) + "x^2 " + signoC + " " + analizarFloat(Math.abs(c)) + " = 0");
            pasos.add(analizarFloat(a) + "x^2 = " + signoOC + analizarFloat(Math.abs(c)));
            pasos.add("x = {√" + analizarFloat(-c) + "} / {"+ analizarFloat(a) + "}");

            if (-c >= 0) {
                pasos.add("x_1 = " + analizarFloat((float) Math.sqrt(-c) / a));
                pasos.add("x_2 = " + analizarFloat((float) -Math.sqrt(-c) / a));
            } else {
                pasos.add("#Debido a que no existen raíces reales de números negativos, la función no posee raíces reales");
            }
        } else if (b == 0 && c == 0) {
            pasos.add(analizarFloat(a) + "x^2 = 0");
            pasos.add("x = 0   Raíz doble");
        }

        return pasos;
    }

    private ArrayList<String> getOEEOLineal() {
        ArrayList<String> pasos = new ArrayList<>();
        pasos.add("#Como se quiere obtener la imagen del 0, se remplaza la x por 0:");

        float[] valores = ObtenedorDeValores.lineal(funcion);
        float a, b;
        a = valores[0];
        b = valores[1];

        if (b > 0) {
            pasos.add(funcion.getNombre() + "(0) = " + analizarFloat(a) + "·0 + " + analizarFloat(b));
            pasos.add(funcion.getNombre() + "(0) = " + analizarFloat(b));
        } else if (b < 0) {
            pasos.add(funcion.getNombre() + "(0) = " + analizarFloat(a) + "·0 - " + analizarFloat(Math.abs(b)));
            pasos.add(funcion.getNombre() + "(0) = " + analizarFloat(b));
        } else {
            pasos.add(funcion.getNombre() + "(0) = " + analizarFloat(a) + "·0");
            pasos.add(funcion.getNombre() + "(0) = 0");
        }

        return pasos;
    }

    private ArrayList<String> getOEEOCuadratica() {
        ArrayList<String> pasos = new ArrayList<>();
        pasos.add("#Como se quiere obtener la imagen del 0, se remplaza la x por 0:");

        float[] valores = ObtenedorDeValores.cuadratica(funcion);
        float a, b, c;
        a = valores[0];
        b = valores[1];
        c = valores[2];

        String signoB, signoC;
        // Si b == 0 no importa, porque no se usa cuando eso pasa
        if (b > 0) {
            signoB = "+";
        } else {
            signoB = "-";
        }
        if (c > 0) {
            signoC = "+";
        } else {
            signoC = "-";
        }

        if (b != 0 && c != 0) {
            pasos.add(funcion.getNombre() + "(0) = " + analizarFloat(a) + "·0^2 " + signoB + " " + analizarFloat(Math.abs(b)) + "·0 + " + signoC + " " + Math.abs(c));
            pasos.add(funcion.getNombre() + "(0) = " + analizarFloat(c));
        } else if (b != 0 && c == 0) {
            pasos.add(funcion.getNombre() + "(0) = " + analizarFloat(a) + "·0^2 " + signoB + " " + analizarFloat(Math.abs(b)) + "·0");
            pasos.add(funcion.getNombre() + "(0) = 0");
        } else if (b == 0 && c != 0) {
            pasos.add(funcion.getNombre() + "(0) = " + analizarFloat(a) + "·0^2 " + signoC + " " + analizarFloat(Math.abs(c)));
            pasos.add(funcion.getNombre() + "(0) = " + analizarFloat(c));
        } else if (b == 0 && c == 0) {
            pasos.add(funcion.getNombre() + "(0) = " + a + "·0^2");
            pasos.add(funcion.getNombre() + "(0) = 0");
        }

        return pasos;
    }
}
