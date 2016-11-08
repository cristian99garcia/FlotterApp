package com.carrotgames.flotter.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.carrotgames.flotter.pojo.FunctionItem;
import com.carrotgames.flotter.R;

import java.util.ArrayList;

/**
 * Created by cristian on 07/11/16.
 */

public class FunctionsListFragment extends Fragment {

    public interface OnFunctionSelected {
        void onFunctionSelected(String nombre, String function, int color);
    }

    private String[] nombres;
    private String[] expresiones;
    private int[] colores;;

    private OnFunctionSelected mListener;

    public static FunctionsListFragment newInstance(String[] nombres, String[] expresiones, int[] colores) {
        FunctionsListFragment fragment = new FunctionsListFragment();
        fragment.nombres = nombres;
        fragment.expresiones = expresiones;
        fragment.colores = colores;
        return fragment;
    }

    public FunctionsListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFunctionSelected) {
            mListener = (OnFunctionSelected) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnFunctionSelected.");
        }

        final Resources resources = context.getResources();
        //mNames = resources.getStringArray(R.array.names);
        //mDescriptions = resources.getStringArray(R.array.descriptions);
        //mUrls = resources.getStringArray(R.array.urls);

        // Get rage face images.
        //final TypedArray typedArray = resources.obtainTypedArray(R.array.images);
        //final int imageCount = mNames.length;
        //mImageResIds = new int[imageCount];
        //for (int i = 0; i < imageCount; i++) {
        //    mImageResIds[i] = typedArray.getResourceId(i, 0);
        //}
        //typedArray.recycle();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_functions_list, container, false);

        final Activity activity = getActivity();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        ArrayList<FunctionItem> items = new ArrayList<>();

        for (int i = 0; i < nombres.length; i++) {
            items.add(new FunctionItem(nombres[i], expresiones[i], colores[i]));
        }

        recyclerView.setAdapter(new FunctionsListAdapter(activity, items));

        return view;
    }

    public class FunctionsListAdapter extends RecyclerView.Adapter<FunctionsListFragment.ViewHolder> {

        private LayoutInflater mLayoutInflater;

        public ArrayList<FunctionItem> items;
        public String[] nombres;
        public String[] expresiones;
        public int[] colores;

        public FunctionsListAdapter(Context context, ArrayList<FunctionItem> items) {
            mLayoutInflater = LayoutInflater.from(context);
            this.items = items;

            nombres = new String[items.size()];
            expresiones = new String[items.size()];
            colores = new int[items.size()];

            for (int i = 0; i < items.size(); i++) {
                nombres[i] = items.get(i).getNombre();
                expresiones[i] = items.get(i).getExpresion();
                colores[i] = items.get(i).getColor();
            }
        }

        @Override
        public FunctionsListFragment.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new FunctionsListFragment.ViewHolder(mLayoutInflater.inflate(R.layout.recycler_item_function, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(FunctionsListFragment.ViewHolder viewHolder, final int position) {
            final String nombre = nombres[position];
            final String expresion = expresiones[position];
            final int color = colores[position];
            viewHolder.mostrar(nombre, expresion, color);
            viewHolder.cvRecycler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onFunctionSelected(nombre, expresion, color);
                }
            });

            detectarPulsaciones(viewHolder.wvFormula, nombre, expresion, color);
        }

        @Override
        public int getItemCount() {
            return nombres.length;
        }
    }

    private void detectarPulsaciones(WebView view, String n, String e, int c) {
        final String nombre = n;
        final String expresion = e;
        final int color = c;
        view.setOnTouchListener(new View.OnTouchListener() {
            public final static int FINGER_RELEASED = 0;
            public final static int FINGER_TOUCHED = 1;
            public final static int FINGER_DRAGGING = 2;
            public final static int FINGER_UNDEFINED = 3;

            private int fingerState = FINGER_RELEASED;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (fingerState == FINGER_RELEASED) fingerState = FINGER_TOUCHED;
                        else fingerState = FINGER_UNDEFINED;
                        return true;

                    case MotionEvent.ACTION_UP:
                        if(fingerState != FINGER_DRAGGING) {
                            fingerState = FINGER_RELEASED;

                            mListener.onFunctionSelected(nombre, expresion, color);

                        }
                        else if (fingerState == FINGER_DRAGGING) fingerState = FINGER_RELEASED;
                        else fingerState = FINGER_UNDEFINED;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING) fingerState = FINGER_DRAGGING;
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    default:
                        fingerState = FINGER_UNDEFINED;
                }

                return false;
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private WebView wvFormula;
        private CardView cvRecycler;

        private ViewHolder(View itemView) {
            super(itemView);
            wvFormula = (WebView) itemView.findViewById(R.id.wvFormula);
            wvFormula.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });

            wvFormula.setLongClickable(true);

            cvRecycler = (CardView) itemView.findViewById(R.id.cvRecycler);
            cvRecycler.setClickable(true);
        }

        public void mostrar(String nombre, String expresion, int color) {
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            String cssPath = "jqmath-0.4.3.css";
            String jqueryPath = "jquery-1.4.3.min.js";
            String jqmathPath = "jqmath-etc-0.4.5.min.js";

            String pagina =
                    "<!DOCTYPE>" +
                    "<head>" +
                        "<style>h2 { font-weight:normal; }</style>" +
                        "<title>Formula</title>" +
                        "<link rel=\"stylesheet\" href=\"" + cssPath + "\" type=\"text/css\">" +
                        "<script src=\"" + jqueryPath + "\" type=\"text/javascript\"></script>" +
                        "<script src=\"" + jqmathPath + "\" type=\"text/javascript\" charset=\"utf-8\"></script>" +
                    "</head>" +
                    "<body>" +
                        String.format("<h2 style='color:%s'>$%s(x) = %s$</h2>", hexColor, nombre, expresion) +
                    "</body>";


            WebSettings configuraciones = wvFormula.getSettings();
            configuraciones.setJavaScriptEnabled(true);
            configuraciones.setDefaultTextEncodingName("utf-8");
            wvFormula.loadDataWithBaseURL(FunctionPropertiesFragment.assets, pagina, "text/html", "utf-8", null);
        }
    }
}
