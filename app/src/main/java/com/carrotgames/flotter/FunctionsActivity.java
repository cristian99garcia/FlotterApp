package com.carrotgames.flotter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.carrotgames.flotter.fragment.FunctionPropertiesFragment;
import com.carrotgames.flotter.fragment.FunctionsListFragment;

public class FunctionsActivity extends AppCompatActivity implements FunctionsListFragment.OnFunctionSelected {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.root_layout) != null) {
            if (savedInstanceState != null) {
                return;
            }

            String[] nombres = new String[0];
            String[] expresiones = new String[0];
            int[] colores = new int[0];
            Bundle parametros = getIntent().getExtras();
            if (getIntent().hasExtra(getResources().getString(R.string.put_expresiones))) {
                nombres = parametros.getStringArray(getResources().getString(R.string.put_nombres));
                expresiones = parametros.getStringArray(getResources().getString(R.string.put_expresiones));
                colores = parametros.getIntArray(getResources().getString(R.string.put_colores));
            }

            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.root_layout,
                            FunctionsListFragment.newInstance(nombres, expresiones, colores),
                            "functionsList").
                    commit();
        }
    }

    @Override
    public void onFunctionSelected(String nombre, String expresion, int color) {
        FunctionPropertiesFragment fragment = (FunctionPropertiesFragment)getSupportFragmentManager().findFragmentById(R.id.function_properties_fragment);

        if (fragment != null) {
            fragment.setInformacion(nombre, expresion, color);
        } else {
            fragment = FunctionPropertiesFragment.newInstance(nombre, expresion, color);
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_layout, fragment)
                .addToBackStack(null)
                .commit();
        }
    }
}
