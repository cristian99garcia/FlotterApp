package com.carrotgames.flotter;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;


public class FunctionsListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FunctionsItemListAdapter listAdapter;
    private LinearLayoutManager llm;
    private ArrayList<FunctionItem> lista = new ArrayList<>();

    private String[] funciones = new String[0];
    private int[] colores = new int[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions_list);

        toolbar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.rvFunctions);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        Bundle parametros = getIntent().getExtras();
        if (getIntent().hasExtra(getResources().getString(R.string.put_funciones))) {
            Log.i("functionslistactivity", "true");
            funciones = parametros.getStringArray(getResources().getString(R.string.put_funciones));
            colores = parametros.getIntArray(getResources().getString(R.string.put_colores));

            String name, formula;
            int color;
            for (int i = 0; i < funciones.length; i++) {
                name = funciones[i].substring(0, 1);
                formula = funciones[i].substring(2, funciones[i].length());
                color = colores[i];
                lista.add(new FunctionItem(name, formula, color));
            }
        } else {
            Log.i("functionslistactivity", "false");
        }

        listAdapter = new FunctionsItemListAdapter(this, lista);
        recyclerView.setAdapter(listAdapter);
    }
}
/*
public class FunctionsListActivity extends AppCompatActivity {

    private ArrayList<FunctionItem> lista = new ArrayList<FunctionItem>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions_list);

        recyclerView = (RecyclerView) findViewById(R.id.rvFunctions);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        lista.add(new FunctionItem("3x + 1", "a", Color.RED));
        lista.add(new FunctionItem("2x^2+1", "b", Color.GREEN));
        lista.add(new FunctionItem("2^x", "c", Color.BLUE));

        FunctionsItemListAdapter listAdapter = new FunctionsItemListAdapter(lista);
        recyclerView.setAdapter(listAdapter);
    }
}
*/