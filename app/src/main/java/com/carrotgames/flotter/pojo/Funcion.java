package com.carrotgames.flotter.pojo;

import android.app.Activity;
import android.graphics.Color;

import com.carrotgames.flotter.R;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Created by cristian on 06/10/16.
 */

public class Funcion {

    public static class SintaxException extends ArithmeticException {

        public SintaxException() {
            super("Error de sintaxis en el polinomio");
        }
        public SintaxException(String e) {
            super(e);
        }
    }

    public static enum TipoFuncion {
        NULA,
        CONSTANTE,
        LINEAL,
        CUADRATICA,
        CUBICA,
        POLINOMICA,
        EXPONENCIAL,
        RACIONAL,
        LOGARITMICA,
        TRIGONOMETRICA,
    }

    private int color = Color.BLUE;
    private String nombre;
    private String formula;
    private TipoFuncion tipo = TipoFuncion.NULA;

    public ArrayList<float[]> puntos;

    public Funcion(String nombre, String formula) {
        this.nombre = nombre;
        this.formula = formula;

        puntos = new ArrayList<>();
        this.adivinarTipo();
    }

    public Funcion(String nombre, String formula, int color) {
        this.nombre = nombre;
        this.formula = formula;
        this.color = color;

        puntos = new ArrayList<>();
        this.adivinarTipo();
    }
    /*
    public void crearPuntos(float startX, float endX, float advance) {
        for (float i = startX; i < endX; i += advance) {
            float y = this.getY(i);
            float[] point = new float[2];
            point[0] = i;
            point[1] = y;
            this.puntos.add(point);
        }
    }*/

    public float getY(float x) {
        boolean z = false;
        String strX = Float.toString(x);
        // La E de notación científica se confunde con el número e
        if (strX.contains("E")) {
            z = true;
            float n = Float.parseFloat(strX.split("E")[0]);
            float e = Float.parseFloat(strX.split("E")[1]);
            strX = String.format("%f * (1 ", n);
            if (e < 0) {
                for (int i=0; i > e; i--) {
                    strX = strX + "/ 10";
                }
            } else if (e > 0) {
                for (int i=0; i < e; i++) {
                    strX = strX + "* 10";
                }
            }
            strX = strX + ")";
        }
        strX = String.format("(%s)", strX);

        String parsed = this.analizarExpresion(formula.replace("x", " * " + strX));
        return this.calcular(parsed);
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getExpresion() {
        return this.formula;
    }

    public static String analizarExpresion(String expression) {
        String parsed = "0";

        Stack stackNumeros = new Stack();
        Stack stackOperadores = new Stack();
        String expr = expression.toLowerCase().replace(" ", "");
        String fragmento;
        int pos = 0;
        int tamano = 0;
        byte cont = 1;
        final String funciones[] = { "1 2 3 4 5 6 7 8 9 0 ( ) x e + - * / ^ %",
                "pi",
                "ln(",
                "log( abs( sen( sin( cos( tan( sec( csc( cot( sgn(",
                "rnd() asen( asin( acos( atan( asec( acsc( acot( senh( sinh( cosh( tanh( sech( csch( coth( sqrt(",
                "round( asenh( acosh( atanh( asech( acsch( acoth(" };

        final String parentesis = "( ln log abs sen sin cos tan sec csc cot sgn asen asin acos atan asec acsc acot senh sinh cosh tanh sech csch coth sqrt round asenh asinh acosh atanh asech acsch acoth";
        byte anterior = 0;

        try{
            while (pos < expr.length()) {
                tamano = 0;
                cont = 1;
                while (tamano == 0 && cont <= 6) {
                    if (pos + cont <= expr.length() && funciones[cont - 1].contains(expr.substring(pos, pos + cont))) {
                        tamano = cont;
                    }
                    cont++;
                }

                if (tamano == 0) {
                    parsed = "0";
                    throw new SintaxException("Error en la expresion");
                } else if (tamano == 1) {
                    if (esNumero(expr.substring(pos, pos + tamano))) {
                        if (anterior == 1 || anterior == 4) {
                            sacaOperadores(stackNumeros, stackOperadores, "*");
                        }

                        fragmento = "";

                        do {
                            fragmento = fragmento+expr.charAt(pos);
                            pos++;
                        } while (pos<expr.length() && (esNumero(expr.substring(pos, pos + tamano)) || expr.charAt(pos) == '.' || expr.charAt(pos) == ','));

                        try {
                            Double.valueOf(fragmento);
                        } catch(NumberFormatException e) {
                            parsed = "0";
                            throw new SintaxException("Numero mal digitado");
                        }

                        stackNumeros.push(new String(fragmento));
                        anterior=1;
                        pos--;
                    } else if (expr.charAt(pos) == 'x' || expr.charAt(pos) == 'e') {
                        if (anterior == 1 || anterior == 4) {
                            sacaOperadores(stackNumeros, stackOperadores, "*");
                        }

                        stackNumeros.push(expr.substring(pos,pos+tamano));
                        anterior = 1;
                    } else if (expr.charAt(pos) == '+' || expr.charAt(pos) == '*' || expr.charAt(pos) == '/' || expr.charAt(pos) == '%') {
                        if (anterior == 0 || anterior == 2 || anterior == 3)
                            throw new SintaxException("Error en la expresion");

                        sacaOperadores(stackNumeros, stackOperadores, expr.substring(pos, pos + tamano));
                        anterior = 2;
                    } else if (expr.charAt(pos) == '^') {
                        if (anterior == 0 || anterior == 2 || anterior == 3)
                            throw new SintaxException("Error en la expresion");

                        stackOperadores.push(new String("^"));
                        anterior = 2;
                    } else if (expr.charAt(pos) == '-') {
                        if (anterior == 0 || anterior == 2 || anterior == 3) {
                            stackNumeros.push(new String("-1"));
                            sacaOperadores(stackNumeros, stackOperadores, "*");
                        } else {
                            sacaOperadores(stackNumeros, stackOperadores, "-");
                        }

                        anterior = 2;
                    } else if (expr.charAt(pos) == '(') {
                        if (anterior == 1 || anterior == 4) {
                            sacaOperadores(stackNumeros, stackOperadores, "*");
                        }

                        stackOperadores.push(new String("("));
                        anterior = 3;
                    } else if (expr.charAt(pos) == ')') {
                        if (anterior != 1 && anterior != 4)
                            throw new SintaxException("Error en la expresion");

                        while(!stackOperadores.empty() && !parentesis.contains(((String)stackOperadores.peek()))) {
                            sacaOperador(stackNumeros, stackOperadores);
                        }

                        if (!((String)stackOperadores.peek()).equals("(")) {
                            stackNumeros.push(new String(((String)stackNumeros.pop()) + " " + ((String)stackOperadores.pop())));
                        } else {
                            stackOperadores.pop();
                        }
                        anterior = 4;
                    }
                } else if (tamano >= 2) {
                    fragmento=expr.substring(pos,pos+tamano);
                    if (fragmento.equals("pi")) {
                        if (anterior == 1 || anterior == 4) {
                            sacaOperadores(stackNumeros, stackOperadores, "*");
                        }
                        stackNumeros.push(fragmento);
                        anterior=1;
                    } else if (fragmento.equals("rnd()")) {
                        if (anterior == 1 || anterior == 4) {
                            sacaOperadores(stackNumeros, stackOperadores, "*");
                        }
                        stackNumeros.push("rnd");
                        anterior = 1;
                    } else {
                        if (anterior == 1 || anterior == 4) {
                            sacaOperadores(stackNumeros, stackOperadores, "*");
                        }
                        stackOperadores.push(fragmento.substring(0,fragmento.length() - 1));
                        anterior = 3;
                    }
                }
                pos += tamano;
            }

            while (!stackOperadores.empty()) {
                if (parentesis.contains((String)stackOperadores.peek()))
                    throw new SintaxException("Hay un parentesis de mas");

                sacaOperador(stackNumeros, stackOperadores);
            }

        } catch (EmptyStackException e) {
            parsed = "0";
            throw new SintaxException("Expresion mal digitada");
        }

        parsed = ((String)stackNumeros.pop());

        if (!stackNumeros.empty()) {
            parsed = "0";
            throw new SintaxException("Error en la expresion");
        }

        return parsed;
    }

    private static float calcular(String expresionParseada) throws ArithmeticException{
        Stack stackEvaluar = new Stack();
        double a;
        double b;
        StringTokenizer tokens = new StringTokenizer(expresionParseada);
        String tokenActual;

        try {
            while (tokens.hasMoreTokens()) {
                tokenActual=tokens.nextToken();

                if (tokenActual.equals("e")) {
                    stackEvaluar.push(new Double(Math.E));
                } else if (tokenActual.equals("pi")) {
                    stackEvaluar.push(new Double(Math.PI));
                } else if (tokenActual.equals("+")) {
                    b = ((Double)stackEvaluar.pop()).doubleValue();
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(a + b));
                } else if (tokenActual.equals("-")) {
                    b = ((Double)stackEvaluar.pop()).doubleValue();
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(a - b));
                } else if (tokenActual.equals("*")) {
                    b = ((Double)stackEvaluar.pop()).doubleValue();
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(a * b));
                } else if (tokenActual.equals("/")) {
                    b = ((Double)stackEvaluar.pop()).doubleValue();
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(a / b));
                } else if (tokenActual.equals("^")) {
                    b = ((Double)stackEvaluar.pop()).doubleValue();
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.pow(a, b)));
                } else if (tokenActual.equals("%")) {
                    b = ((Double)stackEvaluar.pop()).doubleValue();
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(a % b));
                } else if (tokenActual.equals("ln")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.log(a)));
                } else if (tokenActual.equals("log")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.log(a) / Math.log(10)));
                } else if (tokenActual.equals("abs")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.abs(a)));
                } else if (tokenActual.equals("rnd")) {
                    stackEvaluar.push(new Double(Math.random()));
                } else if (tokenActual.equals("sen") || tokenActual.equals("sin")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.sin(a)));
                } else if (tokenActual.equals("cos")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.cos(a)));
                } else if (tokenActual.equals("tan")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.tan(a)));
                } else if (tokenActual.equals("sec")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(1 / Math.cos(a)));
                } else if (tokenActual.equals("csc")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(1 / Math.sin(a)));
                } else if (tokenActual.equals("cot")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(1 / Math.tan(a)));
                } else if (tokenActual.equals("sgn")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(sgn(a)));
                } else if (tokenActual.equals("asen") || tokenActual.equals("asin")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.asin(a)));
                } else if (tokenActual.equals("acos")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.acos(a)));
                } else if (tokenActual.equals("atan")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.atan(a)));
                } else if (tokenActual.equals("asec")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.acos(1 / a)));
                } else if (tokenActual.equals("acsc")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.asin(1 / a)));
                } else if (tokenActual.equals("acot")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.atan(1 / a)));
                } else if (tokenActual.equals("senh") || tokenActual.equals("sinh")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double((Math.exp(a) - Math.exp(-a)) / 2));
                } else if (tokenActual.equals("cosh")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double((Math.exp(a) + Math.exp(-a)) / 2));
                } else if (tokenActual.equals("tanh")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double((Math.exp(a) - Math.exp(-a)) / (Math.exp(a) + Math.exp(-a))));
                } else if (tokenActual.equals("sech")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(2 / (Math.exp(a) + Math.exp(-a))));
                } else if (tokenActual.equals("csch")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(2 / (Math.exp(a) - Math.exp(-a))));
                } else if (tokenActual.equals("coth")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double((Math.exp(a) + Math.exp(-a)) / (Math.exp(a) - Math.exp(-a))));
                } else if (tokenActual.equals("asenh") || tokenActual.equals("asinh")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.log(a + Math.sqrt(a * a + 1))));
                } else if (tokenActual.equals("acosh")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.log(a + Math.sqrt(a * a - 1))));
                } else if (tokenActual.equals("atanh")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.log((1 + a) / (1 - a)) / 2));
                } else if (tokenActual.equals("asech")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.log((Math.sqrt(1 - a * a) + 1) / a)));
                } else if (tokenActual.equals("acsch")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.log((sgn(a) * Math.sqrt(a * a + 1) + 1) / a)));
                } else if (tokenActual.equals("acoth")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.log((a + 1) / (a - 1)) / 2));
                } else if (tokenActual.equals("sqrt")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Math.sqrt(a)));
                } else if (tokenActual.equals("round")) {
                    a = ((Double)stackEvaluar.pop()).doubleValue();
                    stackEvaluar.push(new Double(Long.toString(Math.round(a))));
                } else {
                    stackEvaluar.push(Double.valueOf(tokenActual));
                }
            }
        } catch(EmptyStackException e) {
            throw new ArithmeticException("Expresion mal parseada");
        } catch(NumberFormatException e) {
            throw new ArithmeticException("Expresion mal digitada");
        } catch(ArithmeticException e) {
            throw new ArithmeticException("Valor no real en la expresion");
        }

        a = ((Double)stackEvaluar.pop()).doubleValue();

        if (!stackEvaluar.empty())
            throw new ArithmeticException("Expresion mal digitada");

        return (float)a;
    }

    private static double sgn(double a) {
        if (a < 0)
            return -1;
        else if (a > 0)
            return 1;
        else
            return 0;
    }

    private static int getPrioridad(char s) {
        if (s == '+' || s == '-')
            return 0;
        else if (s=='*' || s=='/' || s=='%')
            return 1;
        else if (s=='^')
            return 2;

        return -1;
    }

    private static boolean esNumero(String str) {
        return (str.compareTo("0") >= 0 && str.compareTo("9") <= 0);
    }

    private static void sacaOperadores(Stack stackNumeros, Stack stackOperadores, String operador) {
        final String parentesis="( ln log abs sen sin cos tan sec csc cot sgn asen asin acos atan asec acsc acot senh sinh cosh tanh sech csch coth sqrt round asenh asinh acosh atanh asech acsch acoth";

        while(!stackOperadores.empty() && !parentesis.contains((String)stackOperadores.peek()) && ((String)stackOperadores.peek()).length() == 1 && getPrioridad(((String)stackOperadores.peek()).charAt(0)) >= getPrioridad(operador.charAt(0))) {
            sacaOperador(stackNumeros, stackOperadores);
        }

        stackOperadores.push(operador);
    }

    private static void sacaOperador(Stack Numeros, Stack operadores) throws EmptyStackException {
        String operador, a, b;
        final String operadoresBinarios="+ - * / ^ %";

        try{
            operador = (String)operadores.pop();

            if (operadoresBinarios.contains(operador)) {
                b = (String)Numeros.pop();
                a = (String)Numeros.pop();
                Numeros.push(new String(a + " " + b + " " + operador));
            } else {
                a = (String)Numeros.pop();
                Numeros.push(new String(a + " " + operador));
            }
        } catch(EmptyStackException e) {
            throw e;
        }
    }

    public String getExpresionJqMath() {
        String jqformula = "$" + nombre + "(x) = " + formula + "$";
        return jqformula;
    }

    private void adivinarTipo() {
        String cadena = formula.replaceAll(" ", "")
                .replace("**", "^")
                .replace(",", ".")
                .toLowerCase();

        if (Pattern.compile("^[-+]?(\\d+)*(.(\\d+))?$").matcher(cadena).find()) {
            tipo = TipoFuncion.CONSTANTE;
        } else if (cadena.contains("x") && !Pattern.compile("[+-]?[x][\\^][-+]?(\\d+)*(.(\\d+))?").matcher(cadena).find() && !Pattern.compile("(?:sec|tan|cos)").matcher(cadena).find() && !cadena.contains("^x")) {
            tipo = TipoFuncion.LINEAL;
        } else if (Pattern.compile("(.+)[/](.+)").matcher(cadena).find()) {
            tipo = TipoFuncion.RACIONAL;
        } else if (Pattern.compile("[+-]?[x][\\^][2]").matcher(cadena).find() && !Pattern.compile("[+-]?[x][\\^][3-9]").matcher(cadena).find()) {
            tipo = TipoFuncion.CUADRATICA;
        } else if (Pattern.compile("[+-]?[x][\\^][3]").matcher(cadena).find() && !Pattern.compile("[+-]?[x][\\^][4-9]").matcher(cadena).find()) {
            tipo = TipoFuncion.CUBICA;
        } else if (Pattern.compile("[+-]?[x][\\^][4-9]").matcher(cadena).find()) {
            tipo = TipoFuncion.POLINOMICA;
        } else if (Pattern.compile("[+-]?(\\d+)*(.(\\d+))?[\\^][+-]?(\\d+)*(.(\\d+))?[x]").matcher(cadena).find()) {
            tipo = TipoFuncion.EXPONENCIAL;
        } else if (Pattern.compile("[-+]?(\\d+)*(.(\\d+))?[+-]?(?:sec|tan|cos)[(].+[)]").matcher(cadena).find()) {
            tipo = TipoFuncion.TRIGONOMETRICA;
        }
    }

    private boolean checkRegex(String regex, String string) {
        return Pattern.compile(regex).matcher(string).find();
    }

    public TipoFuncion getTipo() {
        return this.tipo;
    }

    public int getNombreStringID() {
        switch (tipo) {
            case CONSTANTE:
                return R.string.tipo_constante;

            case LINEAL:
                return R.string.tipo_lineal;

            case CUADRATICA:
                return R.string.tipo_cuadratica;

            case CUBICA:
                return R.string.tipo_cubica;

            case POLINOMICA:
                return R.string.tipo_polinomica;

            case EXPONENCIAL:
                return R.string.tipo_exponencial;

            case RACIONAL:
                return R.string.tipo_racional;

            case LOGARITMICA:
                return R.string.tipo_logaritmica;

            case TRIGONOMETRICA:
                return R.string.tipo_trigonometrica;

            default:
                return R.string.tipo_nula;
        }
    }

    public String getDominio(Activity activity) {
        switch (tipo) {
            case CONSTANTE:
                return activity.getResources().getString(R.string.conjunto_reales);

            case LINEAL:
                return activity.getResources().getString(R.string.conjunto_reales);

            case CUADRATICA:
                return activity.getResources().getString(R.string.conjunto_reales);

            case CUBICA:
                return activity.getResources().getString(R.string.conjunto_reales);

            case POLINOMICA:
                return activity.getResources().getString(R.string.conjunto_reales);

            case EXPONENCIAL:
                return activity.getResources().getString(R.string.conjunto_reales);

            case RACIONAL:
                // TODO: No son todos los reales, hay que sacar los problemas de existencia
                return activity.getResources().getString(R.string.conjunto_reales);

            case LOGARITMICA:
                // TODO: No son todos los reales, solo los valores que hagan que la expresión dentro del logarítmo sea mayor que cero
                return activity.getResources().getString(R.string.conjunto_reales);

            case TRIGONOMETRICA:
                // TODO: No siempre son todos los reales, depende del tipo de función:
                // sen: R
                // cos: R
                // tan: R - { (2k + 1) * pi/2; k e Z }
                // cot: R - { k*pi; k e Z }
                // sec: R - { (2k + 1) * pi/2; k e Z }
                return activity.getResources().getString(R.string.conjunto_reales);

            default:
                return activity.getResources().getString(R.string.conjunto_vacio);
        }
    }

    public String getRecorrido(Activity activity) {
        switch (tipo) {
            case CONSTANTE:
                return Float.toString(getY(0));

            case LINEAL:
                return activity.getResources().getString(R.string.conjunto_reales);

            case CUADRATICA:
                // Si a > 0:
                // Recorrido = [VY, infinito)
                // Si a < 0:
                // Recorrido = (-infinito, VY]
                return activity.getResources().getString(R.string.conjunto_reales);

            case CUBICA:
                return activity.getResources().getString(R.string.conjunto_reales);

            case POLINOMICA:
                // Solo si el máximo exponente es impar
                return activity.getResources().getString(R.string.conjunto_reales);

            case EXPONENCIAL:
                // Si b >= 0
                // R+
                // Si b < 0
                // R+ + (b, 0]
                return activity.getResources().getString(R.string.conjunto_reales);

            case RACIONAL:
                // TODO: buscar asíntota horizontal
                return activity.getResources().getString(R.string.conjunto_reales);

            case LOGARITMICA:
                return activity.getResources().getString(R.string.conjunto_reales);

            case TRIGONOMETRICA:
                // TODO: Estudiar más en profundidad
                return activity.getResources().getString(R.string.conjunto_reales);

            default:
                return activity.getResources().getString(R.string.conjunto_vacio);
        }
    }

    public ArrayList<String> getRaicesPasoAPaso() {
        CalculadorPasoAPaso calculador = new CalculadorPasoAPaso(this);
        return calculador.getRaices();
    }

    public ArrayList<String> getOEEOPasoAPaso() {
        CalculadorPasoAPaso calculador = new CalculadorPasoAPaso(this);
        return calculador.getOEEO();
    }
}
