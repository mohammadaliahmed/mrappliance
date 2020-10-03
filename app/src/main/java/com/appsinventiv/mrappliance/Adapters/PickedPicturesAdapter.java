package com.appsinventiv.mrappliance.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appsinventiv.mrappliance.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PickedPicturesAdapter extends RecyclerView.Adapter<PickedPicturesAdapter.ViewHolder> {
    Context context;
    List<Uri> itemList;
    PickCallbacks callbacks;

    public PickedPicturesAdapter(Context context, List<Uri> itemList, PickCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;
    }

    public void setItemList(List<Uri> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.img_item_layout, parent, false);
        PickedPicturesAdapter.ViewHolder viewHolder = new PickedPicturesAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Glide.with(context).load(itemList.get(position)).into(holder.image);
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cancel, image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            cancel = itemView.findViewById(R.id.cancel);
        }
    }

    public interface PickCallbacks {
        public void onDelete(int position);
    }
}
