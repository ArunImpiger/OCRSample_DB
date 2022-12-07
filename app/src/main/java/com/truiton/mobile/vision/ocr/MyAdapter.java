package com.truiton.mobile.vision.ocr;


import android.content.Context;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<String> nameList;
    private Context ctx;

    public MyAdapter(ArrayList<String> nameList , Context ctx) {
        this.nameList = nameList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx.getApplicationContext()).inflate(R.layout.name_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        holder.name.setText(nameList.get(position));
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.nameText);
        }
    }
}
