package com.mmga.mmgahottweet.ui;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mmga.mmgahottweet.R;
import com.mmga.mmgahottweet.data.Constant;
import com.mmga.mmgahottweet.data.model.Status;
import com.mmga.mmgahottweet.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    List<Status> itemList = new ArrayList<>();
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

    public void refreshAdapterData(List<Status> statusList) {
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


        Status status = itemList.get(position);

        if (status.getReTweet() != null) {
            //转推者信息
            holder.rtLayout.setVisibility(View.VISIBLE);
            uri = Uri.parse(status.getUser().getProfileImageUrl());
            holder.rtAvatar.setImageURI(uri);
            holder.rtUser.setText(String.format("转推者: @%s", status.getUser().getScreenName()));

            //原推内容
            holder.userName.setText(status.getReTweet().getRtUser().getUserName());
            holder.screenName.setText(String.format("/@%s", status.getReTweet().getRtUser().getScreenName()));
            holder.content.setText(status.getReTweet().getRtText());
            uri = Uri.parse(status.getReTweet().getRtUser().getProfileImageUrl());
            holder.avatar.setImageURI(uri);

        } else {
            holder.userName.setText(status.getUser().getName());
            holder.screenName.setText(String.format("/@%s", status.getUser().getScreenName()));
            holder.content.setText(status.getText());
            uri = Uri.parse(status.getUser().getProfileImageUrl());
            holder.avatar.setImageURI(uri);
        }

        String time = DateUtil.parseDate(status.getCreatedAt());
        holder.createTime.setText(time);


    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public boolean isShortList() {
        return getItemCount() < Constant.DEFAULT_COUNT;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView createTime;
        TextView userName;
        TextView screenName;
        TextView content;
        SimpleDraweeView avatar;
        LinearLayout rtLayout;
        SimpleDraweeView rtAvatar;
        TextView rtUser;


        public MyViewHolder(View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.user_name);
            screenName = (TextView) itemView.findViewById(R.id.screen_name);
            createTime = (TextView) itemView.findViewById(R.id.create_time);
            content = (TextView) itemView.findViewById(R.id.content);
            avatar = (SimpleDraweeView) itemView.findViewById(R.id.avatar);
            rtLayout = (LinearLayout) itemView.findViewById(R.id.rt_layout);
            rtAvatar = (SimpleDraweeView) itemView.findViewById(R.id.rt_avatar);
            rtUser = (TextView) itemView.findViewById(R.id.rt_user);

        }
    }

}