package com.flowercentral.flowercentralbusiness.login.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.login.ui.model.FileDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {
    private final ArrayList<FileDetails> mUploadDataList;

    /**
     *
     * @param values values
     */
    public UploadListAdapter(ArrayList<FileDetails> values) {
        this.mUploadDataList = values;
    }

    /**
     *
     * @param parent parent
     * @param viewType viewType
     * @return view
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upload_data_row, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     *
     * @param holder holder
     * @param position position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.textViewData.setText(mUploadDataList.get(position).getFileName());
        holder.imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUploadDataList.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
    }

    /**
     *
     * @return size
     */
    @Override
    public int getItemCount() {
        return mUploadDataList.size();
    }

    /**
     *
     */
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

