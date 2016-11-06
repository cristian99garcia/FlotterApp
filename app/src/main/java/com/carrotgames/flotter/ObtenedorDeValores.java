package com.carrotgames.flotter;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by cristian on 27/10/16.
 */

public class ObtenedorDeValores {

    private static String[] separarEnMonomios(String data) {
        data = data.replace("+", "SPLIT+").replace("-", "SPLIT-");

        if (data.contains("SPLIT")) {
            ArrayList<String> lista = new ArrayList(Arrays.asList(data.split("SPLIT")));
            lista.removeAll(Arrays.asList("", null));
            return lista.toArray(new String[lista.size()]);
        } else {
            return new String[] { data };
        }
    }

    private static float extraerCoeficiente(String monomio) {
        monomio = monomio.replace(" ", "");
        float numero;

        if (!monomio.trim().equals("") && !monomio.contains("x")) {
            return Float.parseFloat(monomio);
        }

        String sNumero;

        if (monomio.contains("x")) {
            String[] separado = monomio.split("x");
            if (separado[0] != "") {
                sNumero = separado[0];
            } else {
                sNumero = separado[1];
            }
        } else {
            sNumero = monomio;
        }

        if (sNumero.endsWith("^")) {
            sNumero = sNumero.substring(0, (sNumero.length() - 1));
        }

        if (sNumero == "+") {
            numero = 1;
        } else if (sNumero == "-") {
            numero = -1;
        } else if (sNumero != "") {
            numero = Float.parseFloat(sNumero);
        } else {
            numero = 1;
        }

        return numero;
    }

    public static float[] lineal(Funcion funcion) {
        String[] monomios = separarEnMonomios(funcion.getExpresion());
        float a = 0f;
        float b = 0f;

        for (String monomio: monomios) {
            if (monomio.trim() != "") {
                if (monomio.contains("x")) {
                    a = extraerCoeficiente(monomio);
                } else {
                    b = extraerCoeficiente(monomio);
                }
            }
        }

        return new float[] { a, b };
    }

    public static float[] cuadratica(Funcion funcion) {
        String[] monomios = separarEnMonomios(funcion.getExpresion());
        float a = 0f;
        float b = 0f;
        float c = 0f;

        for (String monomio: monomios) {
            if (monomio.trim() != "") {
                if (monomio.contains("x^")) {
                    a = extraerCoeficiente(monomio);
                } else if (monomio.contains("x") && !monomio.contains("x^")) {
                    b = extraerCoeficiente(monomio);
                } else {
                    c = extraerCoeficiente(monomio);
                }
            }
        }

        return new float[] { a, b, c };
    }

    public static float[] cubica(Funcion funcion) {
        return new float[2];
    }

    public static float[] polinomica(Funcion funcion) {
        return new float[2];
    }

    public static float[] exponencial(Funcion funcion) {
        return new float[2];
    }

    public static float[] racional(Funcion funcion) {
        return new float[2];
    }

    public static float[] logaritmica(Funcion funcion) {
        return new float[2];
    }

    public static float[] trigonometrica(Funcion funcion) {
        return new float[2];
    }
}
