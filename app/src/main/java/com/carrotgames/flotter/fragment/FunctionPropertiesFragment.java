package com.carrotgames.flotter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.carrotgames.flotter.R;
import com.carrotgames.flotter.pojo.Funcion;

/**
 * Created by cristian on 07/11/16.
 */

public class FunctionPropertiesFragment extends Fragment {

    private WebView webView;
    private Funcion funcion;

    public static String assets = "file:///android_asset/";

    public static FunctionPropertiesFragment newInstance(String nombre, String expresion, int color) {
        final Bundle args = new Bundle();
        args.putString("nombre", nombre);
        args.putString("expresion", expresion);
        args.putInt("color", color);

        final FunctionPropertiesFragment fragment = new FunctionPropertiesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public FunctionPropertiesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_function_properties, container, false);
        webView = (WebView) view.findViewById(R.id.webView);

        configurarWebView();

        return view;
    }

    private void configurarWebView() {
        // Configura el WebView, y hace que cargue la página dinámica.
        WebSettings configuraciones = webView.getSettings();
        configuraciones.setJavaScriptEnabled(true);
        configuraciones.setDefaultTextEncodingName("utf-8");

        final Bundle args = getArguments();
        String nombre = args.getString("nombre");
        String expresion = args.getString("expresion");
        int color = args.getInt("color");

        funcion = new Funcion(nombre, expresion, color);
        webView.loadDataWithBaseURL(assets, crearPagina(), "text/html", "utf-8", null);
    }

    public String crearPagina() {
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
                "<p style=\"margin-bottom: 1px\">Dominio: $\\" + funcion.getDominio(getActivity()) + "$</p>" +
                "<p style=\"margin-top: 1px\">Recorrido: $\\" + funcion.getRecorrido(getActivity()) + "$</p>" +
                "<h4 style=\"margin-bottom: 4px\">¿Cómo obtener las raíces?</h4>";

        Log.i("makepage", funcion.getDominio(getActivity()) + " " + funcion.getRecorrido(getActivity()));
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