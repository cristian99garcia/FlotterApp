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
        //keyboardView.setOnKeyboardActionListener(this.makeKeyboardListener());

        entry = (EditText) findViewById(R.id.functionsInput);
        //entry.setOnKeyListener(null);       // Evitando que salga el teclado del sistema, no funciona
        //entry.setTextIsSelectable(true);    // sin este otro set
        //entry.setInputType(InputType.TYPE_CLASS_TEXT);// | InputType.TYPE_CLASS_NUMBER);

        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);
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

    private KeyboardView.OnKeyboardActionListener makeKeyboardListener() {
        final KeyboardView.OnKeyboardActionListener keyboardListener = new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
            }

            @Override
            public void onPress(int arg0) {
            }

            @Override
            public void onRelease(int primaryCode) {
                if (primaryCode == -1) {
                    entry.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                } else if (primaryCode == 0) {
                    entry.getText().clear();
                }
            }

            @Override
            public void onText(CharSequence sequence) {
                //int start = Math.max(((EditText)findViewById(R.id.functionsInput)).getSelectionStart(), 0);
                //int end = Math.max(((EditText)findViewById(R.id.functionsInput)).getSelectionEnd(), 0);
                //String text = ((EditText)findViewById(R.id.functionsInput)).getText().toString() + sequence.toString();
                //Log.i("onText2", "%s %s &s".format(sequence.toString(), entry.getText(), text));

                char[] a = new char[entry.getText().length() + sequence.length()];
                int i = 0;
                for (char x: entry.getText().toString().toCharArray()) {
                    a[i] = x;
                    i++;
                }

                for (char x: sequence.toString().toCharArray()) {
                    a[i] = x;
                    i++;
                }
                entry.setText(a, 0, a.length);
                //entry.setText(sequence, TextView.BufferType.NORMAL); //text, TextView.BufferType.EDITABLE);
                //entry.setSelection(entry.getText().length() - 1, entry.getText().length());



                //String text = ((EditText)findViewById(R.id.functionsInput)).getText().toString();
                //String newText = text.substring(0, start) + sequence + text.substring(end, text.length());
                //entry.setText(newText, TextView.BufferType.NORMAL);
                //Log.i("onText3", newText);

                ///entry.setText(text, TextView.BufferType.NORMAL);
                //int start = Math.max(entry.getSelectionStart(), 0);
                //int end = Math.max(entry.getSelectionEnd(), 0);
                //entry.setText((entry.getText().replace(Math.min(start, end), Math.max(start, end), sequence, 0, sequence.length())));
                //entry.setSelection(start + sequence.length() - 1);
                //entry.append(sequence, start, end);//, Math.min(start, end), Math.max(start, end));
                //entry.setText((entry.getText() + text).toCharArray(), 0, (entry.getText() + text).length());
                //entry.setText(entry.getText().replace(Math.min(start, end), Math.max(start, end), text, 0, text.length()));
                //entry.setFocusable(true);
                //entry.requestFocus();


                entry.setSelection(entry.getText().length());
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
