package com.fixedit.fixitservices.Adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.fixedit.fixitservices.Models.ServiceCountModel;
import com.fixedit.fixitservices.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListAdapter.ViewHolder> {
    Context context;
    ArrayList<ServiceCountModel> itemList;

    public InvoiceListAdapter(Context context, ArrayList<ServiceCountModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.invoice_item_list, parent, false);
        InvoiceListAdapter.ViewHolder viewHolder = new InvoiceListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceCountModel model = itemList.get(position);
        holder.serial.setText("" + (position + 1));
        holder.productName.setText(model.getService().getName());
        holder.unit.setText("" + model.getQuantity());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView serial, productName, unit, qIntoP, tPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            serial = itemView.findViewById(R.id.serial);
            productName = itemView.findViewById(R.id.productName);
            unit = itemView.findViewById(R.id.unit);

        }
    }
}
