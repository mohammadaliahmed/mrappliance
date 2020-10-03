package com.appsinventiv.mrappliance.Notifications;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.appsinventiv.mrappliance.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationHistoryAdapter extends RecyclerView.Adapter<NotificationHistoryAdapter.ViewHolder> {
    Context context;
    ArrayList<CustomerNotificationModel> itemList = new ArrayList<>();

    public NotificationHistoryAdapter(Context context, ArrayList<CustomerNotificationModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item_layout, parent, false);
        NotificationHistoryAdapter.ViewHolder viewHolder = new NotificationHistoryAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CustomerNotificationModel model = itemList.get(position);
        holder.title.setText(model.getTitle());
        holder.message.setText(model.getMessage());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = null;
                if (model.getType().equalsIgnoreCase("marketing")) {
                    showAlert(model);
                } else if (model.getType().equalsIgnoreCase("brand")) {
//                    resultIntent = new Intent(context, ProductsFromThatBrand.class);
//                    resultIntent.putExtra("brand", model.getId());
//                    context.startActivity(resultIntent);

                } else if (model.getType().equalsIgnoreCase("product")) {
//                    resultIntent = new Intent(context, ViewProduct.class);
//                    resultIntent.putExtra("productId", model.getId());
//                    context.startActivity(resultIntent);

                }
            }
        });
    }

    private void showAlert(CustomerNotificationModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(model.getTitle());
        builder.setMessage(model.getMessage());

        // add the buttons
        builder.setPositiveButton("Ok", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            message = itemView.findViewById(R.id.message);
        }
    }
}
