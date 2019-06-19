package com.example.androidnotification.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidnotification.R;
import com.example.androidnotification.model.MyItemNotification;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private ArrayList<MyItemNotification> listNotification;

    public RecyclerAdapter(ArrayList<MyItemNotification> listNotification) {
        this.listNotification = listNotification;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View notificationView = inflater.inflate(R.layout.item_notification, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(notificationView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        MyItemNotification item = listNotification.get(i);
        if (item.getAvatar() != null) {
            myViewHolder.imgAvatar.setImageDrawable(item.getAvatar());
        }
        myViewHolder.txtName.setText(item.getName());
        myViewHolder.txtContent.setText(item.getContent());
    }

    @Override
    public int getItemCount() {
        return listNotification.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgAvatar;
        public TextView txtName;
        public TextView txtContent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtFrom);
            txtContent = itemView.findViewById(R.id.txtContent);
        }
    }
}
