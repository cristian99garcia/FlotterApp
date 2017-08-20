package com.carrotgames.flotter;

import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.carrotgames.flotter.pojo.Funcion;
import com.getbase.floatingactionbutton.FloatingActionButton;

public class EditFunctionActivity extends AppCompatActivity {

    private String[] nombres = new String[0];
    private String[] expresiones = new String[0];
    private int[] colores = new int[0];

    enum KeyboardType {
        MAIN,
        SECONDARY,
        QWERTY,
    }

    Keyboard mainKeyboard;
    Keyboard secondaryKeyboard;
    Keyboard qwertyKeyboard;
    KeyboardView keyboardView;
    EditText entry;
    KeyboardType currentKeyboard = KeyboardType.MAIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_function);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle parametros = getIntent().getExtras();
        if (getIntent().hasExtra(getResources().getString(R.string.put_nombres))) {
            nombres = parametros.getStringArray(getResources().getString(R.string.put_nombres));
            expresiones = parametros.getStringArray(getResources().getString(R.string.put_expresiones));
            colores = parametros.getIntArray(getResources().getString(R.string.put_colores));
        }

        FloatingActionButton saveButton = (FloatingActionButton) findViewById(R.id.action_save_function);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean go = addFunction();
                if (go) {
                    Intent intent = new Intent(EditFunctionActivity.this, MainActivity.class);
                    intent.putExtra(getResources().getString(R.string.put_nombres), nombres);
                    intent.putExtra(getResources().getString(R.string.put_expresiones), expresiones);
                    intent.putExtra(getResources().getString(R.string.put_colores), colores);
                    startActivity(intent);
                }
            }
        });

        mainKeyboard = new Keyboard(EditFunctionActivity.this, R.xml.keyboard);
        secondaryKeyboard = new Keyboard(EditFunctionActivity.this, R.xml.secondary_keyboard);
        qwertyKeyboard = new Keyboard(EditFunctionActivity.this, R.xml.qwerty_keyboard);

        keyboardView = (KeyboardView)findViewById(R.id.keyboardView);
        keyboardView.setKeyboard(mainKeyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this.kvlistener());

        entry = (EditText) findViewById(R.id.functionsInput);
        entry.setOnKeyListener(null);       // Evitando que salga el teclado del sistema, no funciona
        entry.setTextIsSelectable(true);    // sin este otro set

        keyboardView.setVisibility(View.VISIBLE);
    }

    private boolean addFunction() {
        // Agregar función, devuelve si la expresión no esta malformada
        String expresion = entry.getText().toString();
        String lNombre = "";
        String lExpresion = "";

        String[] lNombres = new String[nombres.length + 1];
        String[] lExpresiones = new String[expresiones.length + 1];
        int[] lColores = new int[colores.length + 1];

        if (!expresion.trim().equals("")) {
            for (int i = 0; i < nombres.length; i++) {
                lNombres[i] = nombres[i];
                lExpresiones[i] = expresiones[i];
                lColores[i] = colores[i];
            }

            if (expresion.contains("=")) {
                lNombre = expresion.split("=")[0];
                lExpresion = expresion.split("=")[1];

                if (lNombre.contains("(x)")) {
                    lNombre = Character.toString(lNombre.charAt(0));
                } else if (lNombre.equals("y")) {
                    lNombre = "f";
                } else {
                    lNombre = "f";
                }
            } else {
                lNombre = "f";
                lExpresion = expresion;
            }
        } else {
            showSnackbar();
            return false;
        }

        try {
            Funcion.analizarExpresion(lExpresion);
            lNombres[lNombres.length - 1] = lNombre;
            lExpresiones[lExpresiones.length - 1] = lExpresion;
            lColores[lColores.length - 1] = Color.BLUE;  // TODO: La idea es que el usuario pueda elegir el color

            nombres = new String[lNombres.length];
            expresiones = new String[lExpresiones.length];
            colores = new int[lColores.length];

            nombres = lNombres;
            expresiones = lExpresiones;
            colores = lColores;
            return true;
        } catch (Funcion.SintaxException error) {
            showSnackbar();
            return false;
        }
    }

    private void showSnackbar() {
        Snackbar.make(keyboardView, "Expresión malformada", Snackbar.LENGTH_LONG).
                setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Hacer algo
                        Log.i("SNACKBAR", "hicieron click en el snackbar");
                    }
                })
                .show();
    }

    private KeyboardView.OnKeyboardActionListener kvlistener() {
        final KeyboardView.OnKeyboardActionListener keyboardListener = new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
            }

            @Override
            public void onPress(int arg) {
                if (arg == -1) {
                    borrarDesdeCursor();
                } else if (arg == 0) {
                    limpiarEntrada();
                }
            }

            @Override
            public void onRelease(int primaryCode) {
            }

            @Override
            public void onText(CharSequence sequence) {
                insertarDesdeCursor(sequence.toString());
            }

            @Override
            public void swipeDown() {
                Log.i("KEYBOARD", "deplazado hacia abajo");
            }

            @Override
            public void swipeLeft() {
                switch (currentKeyboard) {
                    case QWERTY:
                        keyboardView.setKeyboard(mainKeyboard);
                        currentKeyboard = KeyboardType.MAIN;
                        break;

                    case MAIN:
                        keyboardView.setKeyboard(secondaryKeyboard);
                        currentKeyboard = KeyboardType.SECONDARY;
                        break;
                }
            }

            @Override
            public void swipeRight() {
                switch (currentKeyboard) {
                    case SECONDARY:
                        keyboardView.setKeyboard(mainKeyboard);
                        currentKeyboard = KeyboardType.MAIN;
                        break;

                    case MAIN:
                        keyboardView.setKeyboard(qwertyKeyboard);
                        currentKeyboard = KeyboardType.QWERTY;
                        break;
                }
            }

            @Override
            public void swipeUp() {
                Log.i("KEYBOARD", "deplazado hacia arriba");
            }
        };

        return keyboardListener;
    }

    public void borrarDesdeCursor() {
        String texto = entry.getText().toString();
        String nuevo;
        String inicio, fin;
        int posicion;

        int start = entry.getSelectionStart();
        int end = entry.getSelectionEnd();

        if (start == end && start != 0) {
            posicion = start - 1;
        } else {
            posicion = start;
        }

        inicio = texto.substring(0, posicion);
        fin = texto.substring(end, texto.length());
        nuevo = inicio + fin;
        entry.setText(nuevo);

        if (posicion < nuevo.length())
            entry.setSelection(posicion);
        else
            entry.setSelection(nuevo.length());
    }

    public void insertarDesdeCursor(String texto) {
        int start = entry.getSelectionStart();
        int end = entry.getSelectionEnd();

        if (start != end) {
            borrarDesdeCursor();
            start = entry.getSelectionStart();
        }

        String actual = entry.getText().toString();
        String nuevo = actual.substring(0, start) + texto + actual.substring(start, actual.length());
        entry.setText(nuevo);

        int posicion = start + texto.length();
        if (texto.endsWith("()") || texto.endsWith("||")) {
            posicion -= 1;
        }

        entry.setSelection(posicion);
    }

    public void limpiarEntrada() {
        entry.setText("");
    }

    public String getTextForKey(int key) {
        switch (key) {
            case 0: return getResources().getString(R.string.keyboard_0);
            case 1: return getResources().getString(R.string.keyboard_1);
            case 2: return getResources().getString(R.string.keyboard_2);
            case 3: return getResources().getString(R.string.keyboard_3);
            case 4: return getResources().getString(R.string.keyboard_4);
            case 5: return getResources().getString(R.string.keyboard_5);
            case 6: return getResources().getString(R.string.keyboard_6);
            case 7: return getResources().getString(R.string.keyboard_7);
            case 8: return getResources().getString(R.string.keyboard_8);
            case 9: return getResources().getString(R.string.keyboard_9);
            case 20: return getResources().getString(R.string.keyboard_divide);
            case 21: return getResources().getString(R.string.keyboard_multiply);
            case 22: return getResources().getString(R.string.keyboard_substract);
            case 23: return getResources().getString(R.string.keyboard_add);
            default: return "";

        }
    }
}
