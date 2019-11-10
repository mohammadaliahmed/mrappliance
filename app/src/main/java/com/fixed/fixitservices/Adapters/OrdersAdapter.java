package com.fixed.fixitservices.Adapters;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.fixed.fixitservices.Activities.ViewInvoice;
import com.fixed.fixitservices.Models.OrderModel;
import com.fixed.fixitservices.R;
import com.fixed.fixitservices.Utils.CommonUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by AliAh on 25/06/2018.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    Context context;
    ArrayList<OrderModel> itemList;
    OrderedProductsLayout adapter;

    AdapterCallbacks callbacks;
    private boolean canRate;

    public OrdersAdapter(Context context, ArrayList<OrderModel> itemList, AdapterCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_layout, parent, false);
        OrdersAdapter.ViewHolder viewHolder = new OrdersAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder holder, int position) {
        final OrderModel model = itemList.get(position);


        if (System.currentTimeMillis() - model.getTime() > 120000) {
            holder.cancel.setVisibility(View.GONE);
        } else {
            holder.cancel.setVisibility(View.VISIBLE);

        }

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onCancel(model);
            }
        });
        if (model.getBillNumber() != 0) {
            holder.viewBill.setVisibility(View.VISIBLE);
        } else {
            holder.viewBill.setVisibility(View.GONE);
        }
        if (model.isJobDone()) {
            holder.order.setText("Order Id: " + model.getOrderId()
                    + "\n\nOrder Status: " + model.getOrderStatus()
                    + "\n\nTotal amount: Rs." + model.getTotalPrice()
            );
        } else {
            holder.order.setText("Order Id: " + model.getOrderId()
                    + "\n\nOrder Status: " + model.getOrderStatus()
                    + "\n\nEstimated cost: Rs" + model.getTotalPrice() + " - Rs" + (model.getTotalPrice() + 200));

        }


        holder.time.setText("Order Time: " + CommonUtils.getFormattedDate(model.getTime()));
        holder.serial.setText((position + 1) + ")");
//        if (model.isRated()) {
//            holder.ratingLayout.setVisibility(View.VISIBLE);
//            holder.ratingBar.setRating(model.getRating());
//        } else {
//            holder.ratingLayout.setVisibility(View.GONE);
//        }
//        if (model.isCancelled()) {
//            holder.cancelled.setVisibility(View.VISIBLE);
//            holder.cancelled.setText("Order Cancelled\nReason: " + model.getCancelReason());
//
//        } else {
//            holder.cancelled.setVisibility(View.GONE);
//        }


        if (model.getOrderStatus().equalsIgnoreCase("pending")) {
            holder.jobColor.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
        } else if (model.getOrderStatus().equalsIgnoreCase("under process")) {
            holder.jobColor.setBackgroundColor(context.getResources().getColor(R.color.colorBlue));

        } else if (model.getOrderStatus().equalsIgnoreCase("completed")) {
            holder.jobColor.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));

        } else if (model.getOrderStatus().equalsIgnoreCase("cancelled")) {
            holder.jobColor.setBackgroundColor(context.getResources().getColor(R.color.colorYellow));

        } else {
            holder.jobColor.setVisibility(View.GONE);
        }
        holder.viewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewInvoice.class);
                i.putExtra("invoiceId", "" + model.getBillNumber());
                context.startActivity(i);
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.recycler_order_products.setLayoutManager(layoutManager);
        adapter = new OrderedProductsLayout(context, model.getCountModelArrayList());

        holder.recycler_order_products.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        holder.rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (model.isJobDone() && !model.isRated()) {
//                    showRatingDialog(model);
//                }i
                if (canRate) {
                    showRatingDialog(model);
                } else {


                }
            }
        });


        if (model.isArrived() && !model.isCancelled() && !model.isJobStarted() && !model.isJobFinish() && !model.isJobDone() && !model.isRated()) {
            holder.cancelled.setText("Serviceman arrived");
            holder.cancelled.setVisibility(View.VISIBLE);
            holder.ratingLayout.setVisibility(View.GONE);
            holder.rate.setVisibility(View.GONE);

        } else if (!model.isArrived() && model.isCancelled() && !model.isJobStarted() && !model.isJobFinish() && !model.isJobDone() && !model.isRated()) {
            holder.cancelled.setText("Service Cancelled\nReason: " + model.getCancelReason());
            holder.cancelled.setVisibility(View.VISIBLE);
            holder.ratingLayout.setVisibility(View.GONE);
            holder.rate.setVisibility(View.GONE);

        } else if (model.isArrived() && !model.isCancelled() && model.isJobStarted() && !model.isJobFinish() && !model.isJobDone() && !model.isRated()) {
            holder.cancelled.setText("Job is in progress");
            holder.cancelled.setVisibility(View.VISIBLE);
            holder.ratingLayout.setVisibility(View.GONE);
            holder.rate.setVisibility(View.GONE);

        } else if (model.isArrived() && !model.isCancelled() && model.isJobStarted() && model.isJobFinish() && !model.isJobDone() && !model.isRated()) {
            holder.cancelled.setText("Job finished");
            holder.cancelled.setVisibility(View.VISIBLE);
            holder.ratingLayout.setVisibility(View.GONE);
            holder.rate.setVisibility(View.GONE);

        } else if (model.isArrived() && !model.isCancelled() && model.isJobStarted() && model.isJobFinish() && !model.isJobDone() && !model.isRated()) {
            holder.cancelled.setText("Job finished");
            holder.ratingLayout.setVisibility(View.GONE);
            holder.rate.setVisibility(View.GONE);
            holder.cancelled.setVisibility(View.VISIBLE);
        } else if (model.isArrived() && !model.isCancelled() && model.isJobStarted() && model.isJobFinish() && model.isJobDone() && !model.isRated()) {
            holder.cancelled.setText("Job Done\nPlease rate the service");
            canRate = true;
            holder.cancelled.setVisibility(View.VISIBLE);
            holder.ratingLayout.setVisibility(View.GONE);
            holder.rate.setVisibility(View.VISIBLE);

        } else if (model.isArrived() && !model.isCancelled() && model.isJobStarted() && model.isJobFinish() && model.isJobDone() && model.isRated()) {
            holder.cancelled.setVisibility(View.GONE);
            holder.ratingLayout.setVisibility(View.VISIBLE);
            holder.rate.setVisibility(View.GONE);

            holder.ratingBar.setRating(model.getRating());
        } else {
            holder.cancelled.setVisibility(View.GONE);
            holder.ratingLayout.setVisibility(View.GONE);
            holder.rate.setVisibility(View.GONE);

        }


    }

    private void showRatingDialog(OrderModel model) {
        callbacks.onRating(model);
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView serial, order, time, viewBill;
        RecyclerView recycler_order_products;
        View jobColor;
        LinearLayout ratingLayout;
        AppCompatRatingBar ratingBar;
        TextView cancelled;
        Button rate;
        ImageView cancel;

        public ViewHolder(View itemView) {
            super(itemView);
            cancel = itemView.findViewById(R.id.cancel);
            serial = itemView.findViewById(R.id.serial);
            recycler_order_products = itemView.findViewById(R.id.recycler_order_products);
            order = itemView.findViewById(R.id.order);
            time = itemView.findViewById(R.id.time);
            viewBill = itemView.findViewById(R.id.viewBill);
            jobColor = itemView.findViewById(R.id.jobColor);
            ratingLayout = itemView.findViewById(R.id.ratingLayout);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            cancelled = itemView.findViewById(R.id.cancelled);
            rate = itemView.findViewById(R.id.rate);
        }
    }

    public interface AdapterCallbacks {
        public void onRating(OrderModel model);
        public void onCancel(OrderModel model);
    }
}
