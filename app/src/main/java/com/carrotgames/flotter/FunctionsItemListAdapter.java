package com.carrotgames.flotter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cristian on 10/10/16.
 */

public class FunctionsItemListAdapter extends RecyclerView.Adapter<FunctionsItemListAdapter.ItemFunctionHolder> {

    private Context context;
    private ArrayList<FunctionItem> lista;

    private String[] funciones = new String[0];
    private int[] colores = new int[0];

    public FunctionsItemListAdapter(Context context, ArrayList<FunctionItem> lista) {
        this.context = context;
        this.lista = lista;
        translateData();
    }
    @Override
    public ItemFunctionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.functions_item, parent, false);
        return new ItemFunctionHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemFunctionHolder holder, int position) {
        final FunctionItem listItem = lista.get(position);
        final int index = position;
        holder.tvFormula.setText(listItem.getNombre() + "(x) = " + listItem.getExpresion());
        holder.tvFormula.setTextColor(listItem.getColor());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FunctionPropertiesActivities.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(context.getResources().getString(R.string.put_funciones), funciones);
                intent.putExtra(context.getResources().getString(R.string.put_colores), colores);
                intent.putExtra(context.getResources().getString(R.string.put_index), index);
                context.startActivity(intent);
                Log.i("onClick3", listItem.getNombre() + "(x) = " + listItem.getExpresion());
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    private void translateData() {
        funciones = new String[lista.size()];
        colores = new int[lista.size()];

        for (int i = 0; i < lista.size(); i++) {
            funciones[i] = lista.get(i).getNombre() + "#" + lista.get(i).getExpresion();
            colores[i] = lista.get(i).getColor();
        }
    }
    public static class ItemFunctionHolder extends RecyclerView.ViewHolder {

        private TextView tvFormula;

        public ItemFunctionHolder(View itemView) {
            super(itemView);
            tvFormula = (TextView) itemView.findViewById(R.id.tvFormula);
        }
    }
}