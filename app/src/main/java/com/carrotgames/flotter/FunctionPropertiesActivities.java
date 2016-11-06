package com.carrotgames.flotter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class FunctionPropertiesActivities extends AppCompatActivity {

    private WebView wv;

    private String assets = "file:///android_asset/";
    private String[] funciones = new String[0];
    private int[] colores = new int[0];
    private Funcion funcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_properties_activities);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wv = (WebView) findViewById(R.id.wvPropertiesViewer);
        WebSettings configuraciones = wv.getSettings();
        configuraciones.setJavaScriptEnabled(true);
        configuraciones.setDefaultTextEncodingName("utf-8");

        if (getIntent().hasExtra(getResources().getString(R.string.put_funciones))) {
            Bundle parametros = getIntent().getExtras();
            funciones = parametros.getStringArray(getResources().getString(R.string.put_funciones));
            colores = parametros.getIntArray(getResources().getString(R.string.put_colores));

            String a = funciones[parametros.getInt(getResources().getString(R.string.put_index))];
            funcion = new Funcion(a.split("#")[0], a.split("#")[1], colores[parametros.getInt(getResources().getString(R.string.put_index))]);
        }

        String page = this.makePage();
        wv.loadDataWithBaseURL(assets, page, "text/html", "utf-8", null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(FunctionPropertiesActivities.this, FunctionsListActivity.class);
                intent.putExtra(getResources().getString(R.string.put_funciones), funciones);
                intent.putExtra(getResources().getString(R.string.put_colores), colores);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String makePage() {
        String cssPath = "jqmath-0.4.3.css";
        String jqueryPath = "jquery-1.4.3.min.js";
        String jqmathPath = "jqmath-etc-0.4.5.min.js";

        String pagina = "<!DOCTYPE html>" +
                "<head>" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">" +
                "<title>" + funcion.getNombre() + "(x) = " + funcion.getExpresion() + "</title>" +
                "<link rel=\"stylesheet\" href=\"" + cssPath + "\" type=\"text/css\">" +
                "<script src=\"" + jqueryPath + "\" type=\"text/javascript\"></script>" +
                "<script src=\"" + jqmathPath + "\" type=\"text/javascript\" charset=\"utf-8\"></script>" +
                "</head" +
                "<body>" +
                "<p style=\"font-size:200%\">" + funcion.getExpresionJqMath() + "</p>" +
                "<p>Tipo: " + getResources().getString(funcion.getNombreStringID()) + "</p>" +
                "<p style=\"margin-bottom: 1px\">Dominio: $\\" + funcion.getDominio(this) + "$</p>" +
                "<p style=\"margin-top: 1px\">Recorrido: $\\" + funcion.getRecorrido(this) + "$</p>" +
                "<h4 style=\"margin-bottom: 4px\">¿Cómo obtener las raíces?</h4>";

        Log.i("makepage", funcion.getDominio(this) + " " + funcion.getRecorrido(this));
        for (String paso: funcion.getRaicesPasoAPaso()) {
            if (paso.startsWith("#")) {
                pagina = pagina + "<p style=\"margin-top: 1px; margin-bottom: 4px;\">" + paso.substring(1, paso.length()) + "</p>";
            } else {
                pagina = pagina + "<p style=\"margin-top: 1px; margin-bottom: 0px;\">$" + paso + "$</p>";
            }
        }

        pagina = pagina + "<h4 style=\"margin-bottom: 4px\">¿Cómo obtener la ordenada en el origen?</h4>";
        for (String paso: funcion.getOEEOPasoAPaso()) {
            if (paso.startsWith("#")) {
                pagina = pagina + "<p style=\"margin-top: 1px; margin-bottom: 4px;\">" + paso.substring(1, paso.length()) + "</p>";
            } else {
                pagina = pagina + "<p style=\"margin-top: 1px; margin-bottom: 0px;\">$" + paso + "$</p>";
            }
        }

        pagina = pagina + "</body>";

        return pagina;
    }
}
