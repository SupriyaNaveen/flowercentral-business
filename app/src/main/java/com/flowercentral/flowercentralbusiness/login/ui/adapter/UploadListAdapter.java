package com.flowercentral.flowercentralbusiness.login.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowercentral.flowercentralbusiness.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 25-05-2017.
 */

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Uri> uploadDataList;

    public UploadListAdapter(Context context, ArrayList<Uri> values) {
        this.context = context;
        this.uploadDataList = values;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upload_data_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textViewData.setText(uploadDataList.get(position).getPath());
        holder.imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDataList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploadDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_data)
        TextView textViewData;

        @BindView(R.id.image_view_close)
        ImageView imageViewClose;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

