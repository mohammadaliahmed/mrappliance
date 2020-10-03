package com.appsinventiv.mrappliance.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsinventiv.mrappliance.Activities.ViewInvoice;
import com.appsinventiv.mrappliance.Models.OrderModel;
import com.appsinventiv.mrappliance.R;
import com.appsinventiv.mrappliance.Utils.CommonUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by AliAh on 25/06/2018.
 */

public class TrackOrdersAdapter extends RecyclerView.Adapter<TrackOrdersAdapter.ViewHolder> {
    Context context;
    ArrayList<OrderModel> itemList;
    OrderedProductsLayout adapter;

    TrackAdapterCallbacks callbacks;
    private boolean canRate;

    public TrackOrdersAdapter(Context context, ArrayList<OrderModel> itemList, TrackAdapterCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;
    }

    @NonNull
    @Override
    public TrackOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.track_order_layout, parent, false);
        TrackOrdersAdapter.ViewHolder viewHolder = new TrackOrdersAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackOrdersAdapter.ViewHolder holder, int position) {
        final OrderModel model = itemList.get(position);

        if (model.isJobDone()) {
            holder.order.setText("Order Id: " + model.getOrderId()
                    + "\n\nOrder Status: " + model.getOrderStatus()
                    + "\n\nTotal amount: AED " + model.getTotalPrice() + "\n\n" + "Order Time: " + CommonUtils.getFormattedDate(model.getTime())
            );
        } else {
            holder.order.setText("Order Id: " + model.getOrderId()
                    + "\n\nOrder Status: " + model.getOrderStatus()
                    + "\n\nEstimated cost: AED" + model.getTotalPrice() + "\n\n" + "Order Time: " + CommonUtils.getFormattedDate(model.getTime()));

        }


        holder.serial.setText((position + 1) + ")");

        holder.viewQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onViewQuote(model);
            }
        });
        holder.viewLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onViewLogs(model);
            }
        });


    }


    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView serial, order;
        Button viewQuote, viewLogs;

        public ViewHolder(View itemView) {
            super(itemView);
            serial = itemView.findViewById(R.id.serial);
            order = itemView.findViewById(R.id.order);
            viewQuote = itemView.findViewById(R.id.viewQuote);
            viewLogs = itemView.findViewById(R.id.viewLogs);

        }
    }

    public interface TrackAdapterCallbacks {

        public void onViewQuote(OrderModel model);

        public void onViewLogs(OrderModel model);
    }
}
