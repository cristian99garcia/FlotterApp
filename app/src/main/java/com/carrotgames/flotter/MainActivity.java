package com.carrotgames.flotter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.carrotgames.flotter.pojo.Funcion;
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
                ArrayList<Funcion> funciones = graph.getFunciones();
                String[] nombres = new String[funciones.size()];
                String[] expresiones = new String[funciones.size()];

                int[] colores = new int[funciones.size()];
                for (int i = 0; i < funciones.size(); i++) {
                    Funcion funcion = funciones.get(i);
                    nombres[i] = funcion.getNombre();
                    expresiones[i] = funcion.getExpresion();
                    colores[i] = funcion.getColor();
                }

                Intent intent = new Intent(MainActivity.this, EditFunctionActivity.class);
                intent.putExtra(getResources().getString(R.string.put_nombres), nombres);
                intent.putExtra(getResources().getString(R.string.put_expresiones), expresiones);
                intent.putExtra(getResources().getString(R.string.put_colores), colores);
                startActivity(intent);
            }
        });

        FloatingActionButton studyElementsButton = (FloatingActionButton) findViewById(R.id.action_study_elements);
        studyElementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Funcion> funciones = graph.getFunciones();
                String[] nombres = new String[funciones.size()];
                String[] expresiones = new String[funciones.size()];
                int[] colores = new int[funciones.size()];

                for (int i = 0; i < funciones.size(); i++) {
                    Funcion funcion = funciones.get(i);
                    nombres[i] = funcion.getNombre();
                    expresiones[i] = funcion.getExpresion();
                    colores[i] = funcion.getColor();
                }

                Intent intent = new Intent(MainActivity.this, FunctionsActivity.class);
                intent.putExtra(getResources().getString(R.string.put_nombres), nombres);
                intent.putExtra(getResources().getString(R.string.put_expresiones), expresiones);
                intent.putExtra(getResources().getString(R.string.put_colores), colores);
                startActivity(intent);
            }
        });

        Bundle parametros = getIntent().getExtras();
        if (getIntent().hasExtra(getResources().getString(R.string.put_nombres))) {
            String[] nombres = parametros.getStringArray(getResources().getString(R.string.put_nombres));
            String[] expresiones = parametros.getStringArray(getResources().getString(R.string.put_expresiones));
            int[] colores = parametros.getIntArray(getResources().getString(R.string.put_colores));

            for (int i=0; i < nombres.length; i++) {
                Funcion funcion = new Funcion(nombres[i], expresiones[i], colores[i]);
                graph.agregarFuncion(funcion);
            }
        } else {
            Log.i("mainactivity", "false");
            this.test(2);
        }
    }

    private void test(int n) {
        if (n > 0) {
            graph.agregarFuncion(new Funcion("a", "2x-1", Color.RED));
            /*
            graph.agregarFuncion(new Funcion("a", "2x-2", Color.RED));
            graph.agregarFuncion(new Funcion("a", "2x-3", Color.BLACK));
            graph.agregarFuncion(new Funcion("a", "2x-4", Color.BLUE));
            graph.agregarFuncion(new Funcion("a", "2x-5", Color.GREEN));
            graph.agregarFuncion(new Funcion("a", "2x-6", Color.CYAN));
            graph.agregarFuncion(new Funcion("a", "2x+0", Color.DKGRAY));
            graph.agregarFuncion(new Funcion("a", "2x+1", Color.GRAY));
            graph.agregarFuncion(new Funcion("a", "2x+2", Color.YELLOW));
            graph.agregarFuncion(new Funcion("a", "2x+3", Color.RED));
            graph.agregarFuncion(new Funcion("a", "2x-7", Color.CYAN));
            graph.agregarFuncion(new Funcion("a", "2x+4", Color.DKGRAY));
            graph.agregarFuncion(new Funcion("a", "2x+5", Color.GRAY));
            graph.agregarFuncion(new Funcion("a", "2x+6", Color.YELLOW));
            graph.agregarFuncion(new Funcion("a", "2x+7", Color.YELLOW));
            */
        }

        if (n > 1)
            graph.agregarFuncion(new Funcion("c", "(2x+1)/(-3x+2)", Color.BLUE));

        if (n > 2)
            graph.agregarFuncion(new Funcion("d", "cos(1x)", Color.GRAY));

        if (n > 3)
            graph.agregarFuncion(new Funcion("e", "sen(1x)", Color.CYAN));

        if (n > 4)
            graph.agregarFuncion(new Funcion("f", "3x^2-2x+1", Color.GREEN));
    }
}
