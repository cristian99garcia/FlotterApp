package com.carrotgames.flotter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DrawingArea graph;
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(toolbar);

        mainLayout = (LinearLayout) findViewById(R.id.lyMain);
        graph = new DrawingArea(this);
        mainLayout.addView(graph);

        FloatingActionButton newFunctionButton = (FloatingActionButton) findViewById(R.id.action_new_function);
        newFunctionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Funcion> funs = graph.getFunciones();
                String[] funciones = new String[funs.size()];
                int[] colores = new int[funs.size()];
                for (int i = 0; i < funs.size(); i++) {
                    Funcion funcion = funs.get(i);
                    funciones[i] = funcion.getNombre() + "#" + funcion.getExpresion();
                    colores[i] = funcion.getColor();
                }

                Intent intent = new Intent(MainActivity.this, EditFunctionActivity.class);
                intent.putExtra(getResources().getString(R.string.put_funciones), funciones);
                intent.putExtra(getResources().getString(R.string.put_colores), colores);
                startActivity(intent);
            }
        });

        /*FloatingActionButton editFunctionsButton = (FloatingActionButton) findViewById(R.id.action_edit_functions);
        editFunctionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Funcion> funs = graph.getFunctions();
                String[] funciones = new String[funs.size()];
                int[] colores = new int[funs.size()];
                for (int i = 0; i < funs.size(); i++) {
                    Funcion funcion = funs.get(i);
                    funciones[i] = funcion.getName() + "#" + funcion.getFormula();
                    colores[i] = funcion.getColor();
                }

                Intent intent = new Intent(MainActivity.this, FunctionsListActivity.class);
                intent.putExtra(getResources().getString(R.string.put_funciones), funciones);
                intent.putExtra(getResources().getString(R.string.put_colores), colores);
                startActivity(intent);
            }
        });*/

        FloatingActionButton studyElementsButton = (FloatingActionButton) findViewById(R.id.action_study_elements);
        studyElementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Funcion> funs = graph.getFunciones();
                String[] funciones = new String[funs.size()];
                int[] colores = new int[funs.size()];
                for (int i = 0; i < funs.size(); i++) {
                    Funcion funcion = funs.get(i);
                    funciones[i] = funcion.getNombre() + "#" + funcion.getExpresion();
                    colores[i] = funcion.getColor();
                }

                Intent intent = new Intent(MainActivity.this, FunctionsListActivity.class);
                intent.putExtra(getResources().getString(R.string.put_funciones), funciones);
                intent.putExtra(getResources().getString(R.string.put_colores), colores);
                startActivity(intent);
            }
        });

        Bundle parametros = getIntent().getExtras();
        if (getIntent().hasExtra(getResources().getString(R.string.put_funciones))) {
            Log.i("mainactivity", "true");
            String[] funciones = parametros.getStringArray(getResources().getString(R.string.put_funciones));
            int[] colores = parametros.getIntArray(getResources().getString(R.string.put_colores));

            for (int i=0; i < funciones.length; i++) {
                String nombre = funciones[i].split("#")[0];
                String formula = funciones[i].split("#")[1];
                Funcion funcion = new Funcion(nombre, formula);
                funcion.setColor(colores[i]);
                graph.agregarFuncion(funcion);
            }
        } else {
            Log.i("mainactivity", "false");
            this.test(4);
        }
    }

    private void test(int n) {
        if (n > 0) {
            graph.agregarFuncion(new Funcion("a", "2x-1", Color.RED));
            graph.agregarFuncion(new Funcion("b", "-2x+1", Color.GREEN));
            graph.agregarFuncion(new Funcion("c", "2x^2-3x+1", Color.GRAY));
        }

        /*if (n > 1) {
            graph.agregarFuncion(new Funcion("b", "cos(1x)", Color.GRAY));
        }

        if (n > 2) {
            graph.agregarFuncion(new Funcion("c", "sen(1x)", Color.CYAN));
        }

        if (n > 3) {
            graph.agregarFuncion(new Funcion("d", "3x^2-2x+1", Color.GREEN));
        }*/
    }
}
