package com.appsinventiv.mrappliance.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.appsinventiv.mrappliance.Models.LogsModel;
import com.appsinventiv.mrappliance.Models.OrderModel;
import com.appsinventiv.mrappliance.R;
import com.appsinventiv.mrappliance.Utils.CommonUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by AliAh on 25/06/2018.
 */

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.ViewHolder> {
    Context context;
    ArrayList<LogsModel> itemList;


    public LogsAdapter(Context context, ArrayList<LogsModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public LogsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_logs_layout, parent, false);
        LogsAdapter.ViewHolder viewHolder = new LogsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LogsAdapter.ViewHolder holder, int position) {
        final LogsModel model = itemList.get(position);
        holder.time.setText(CommonUtils.getTime(model.getTime()));
        holder.order.setText(model.getText());

    }


    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, order;

        public ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            order = itemView.findViewById(R.id.order);

        }
    }


}
