package com.example.shivanikoul.instagram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class postAdapter extends  RecyclerView.Adapter<postAdapter.ViewHolder> {
    Context context;


    private ArrayList<PostModel> postList =null;

    public postAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PostModel model =postList.get(position);

        holder.userName.setText(model.getUserName());
        Glide.with(context).load(model.getPostImgUrl()).into(holder.postImage);


    }

    @Override
    public int getItemCount() {
        return postList==null?0:postList.size();
    }

    public void swap(ArrayList<PostModel>postList) {
        this.postList =postList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView postImage;
        public ViewHolder(View itemView) {
            super(itemView);

            userName =itemView.findViewById(R.id.username);
            postImage =itemView.findViewById(R.id.userimage);
        }
    }
}
