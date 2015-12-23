package com.mmga.mmgahottweet.ui;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mmga.mmgahottweet.R;
import com.mmga.mmgahottweet.data.model.Status;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    List<Status> itemList = new ArrayList<>();
    String detailInfo;
    Uri uri;

    public RecyclerViewAdapter() {

    }

    public void setAdapterData(List<Status> list) {
        this.itemList = list;
        notifyDataSetChanged();
    }

    public void addAdapterData(List<Status> statusList) {
        this.itemList.addAll(statusList);
        notifyDataSetChanged();
    }

    public void refreshAdapterData(List<Status> statusList){
        this.itemList.clear();
        itemList.addAll(statusList);
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.content.setText(itemList.get(position).getText());
        detailInfo = itemList.get(position).getUser().getName() + itemList.get(position).getCreatedAt();
        holder.info.setText(detailInfo);

        uri = Uri.parse(itemList.get(position).getUser().getProfileImageUrl());
        holder.avatar.setImageURI(uri);
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView info;
        TextView content;
        SimpleDraweeView avatar;

        public MyViewHolder(View itemView) {
            super(itemView);

            info = (TextView) itemView.findViewById(R.id.info);
            content = (TextView) itemView.findViewById(R.id.content);
            avatar = (SimpleDraweeView) itemView.findViewById(R.id.avatar);
        }
    }

}