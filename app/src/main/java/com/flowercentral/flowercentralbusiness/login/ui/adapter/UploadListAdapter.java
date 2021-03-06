package com.flowercentral.flowercentralbusiness.login.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.UploadDataRowBinding;
import com.flowercentral.flowercentralbusiness.login.ui.model.FileDetails;

import java.util.ArrayList;

/**
 *
 */
public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {
    private final ArrayList<FileDetails> mUploadDataList;

    /**
     * @param values values
     */
    public UploadListAdapter(ArrayList<FileDetails> values) {
        this.mUploadDataList = values;
    }

    /**
     * @param parent   parent
     * @param viewType viewType
     * @return view
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UploadDataRowBinding itemView = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.upload_data_row, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * @param holder   holder
     * @param position position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.rowBinder.textViewData.setText(mUploadDataList.get(position).getFileName());
        holder.rowBinder.imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUploadDataList.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
    }

    /**
     * @return size
     */
    @Override
    public int getItemCount() {
        if (mUploadDataList != null)
            return mUploadDataList.size();
        else return 0;
    }

    /**
     *
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private UploadDataRowBinding rowBinder;

        ViewHolder(UploadDataRowBinding binder) {
            super(binder.getRoot());
            rowBinder = binder;
        }
    }
}

