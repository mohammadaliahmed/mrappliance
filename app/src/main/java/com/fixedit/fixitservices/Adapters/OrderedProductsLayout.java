package com.fixedit.fixitservices.Adapters;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fixedit.fixitservices.Models.ServiceCountModel;
import com.fixedit.fixitservices.R;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by AliAh on 25/06/2018.
 */

public class OrderedProductsLayout extends RecyclerView.Adapter<OrderedProductsLayout.ViewHolder> {
    Context context;
    ArrayList<ServiceCountModel> itemList;

    public OrderedProductsLayout(Context context, ArrayList<ServiceCountModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ordered_products_layout, parent, false);
        OrderedProductsLayout.ViewHolder viewHolder = new OrderedProductsLayout.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ServiceCountModel model = itemList.get(position);
        holder.title.setText(model.getService().getName());
        holder.quantity.setText("Quantity: " + model.getQuantity());
        holder.serial.setText((position+1)+")");
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(context, ViewProduct.class);
//                i.putExtra("productId",model.getProduct().getId());
//                context.startActivity(i);
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, quantity,serial;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            quantity = itemView.findViewById(R.id.quantity);
            serial = itemView.findViewById(R.id.serial);


        }
    }
}
