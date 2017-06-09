package com.flowercentral.flowercentralbusiness.profile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.profile.model.ShopPictureDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

class ShopPicturesAdapter extends RecyclerView.Adapter<ShopPicturesAdapter.ViewHolder> {

    private ArrayList<ShopPictureDetails> shopPicturesList = new ArrayList<>();
    private Context mContext;
    private ShopPictures.RefreshViews mRefreshViews;

    boolean isImageSelectable;
    private HashSet<String> selectedPictureIdList = new HashSet<>();

    ShopPicturesAdapter(ArrayList<ShopPictureDetails> shopPicturesList,
                        ShopPictures.RefreshViews refreshViews) {
        this.shopPicturesList = shopPicturesList;
        mRefreshViews = refreshViews;
    }

    @Override
    public ShopPicturesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_pictures_item_row, parent, false);
        mContext = parent.getContext();
        return new ShopPicturesAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ShopPicturesAdapter.ViewHolder holder, final int position) {

        if (position < shopPicturesList.size()) {
            holder.shopImage.setVisibility(View.VISIBLE);
            holder.uploadImage.setVisibility(View.GONE);

            Picasso.
                    with(mContext).
                    load(shopPicturesList.get(position).getImageUrl()).
                    into(holder.shopImage);

            //Show/hide selected image list
            final String pictureId = shopPicturesList.get(position).getPictureId();
            if (selectedPictureIdList.contains(pictureId)) {
                holder.imageViewSelected.setVisibility(View.VISIBLE);
            } else {
                holder.imageViewSelected.setVisibility(View.GONE);
            }

            holder.shopImage.setLongClickable(true);
            holder.shopImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    isImageSelectable = true;
                    //Show the selected button
                    holder.imageViewSelected.setVisibility(View.VISIBLE);
                    selectedPictureIdList.add(pictureId);
                    mRefreshViews.refreshDeleteIcon();
                    return true;
                }
            });

            holder.shopImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isImageSelectable) {
                        //If already selected, then un select
                        if (selectedPictureIdList.contains(pictureId)) {
                            holder.imageViewSelected.setVisibility(View.GONE);
                            selectedPictureIdList.remove(pictureId);

                            if (selectedPictureIdList.isEmpty()) {
                                isImageSelectable = false;
                                mRefreshViews.refreshDeleteIcon();
                            }
                        } else {
                            holder.imageViewSelected.setVisibility(View.VISIBLE);
                            selectedPictureIdList.add(pictureId);
                        }
                    }
                }
            });

        } else {
            holder.shopImage.setVisibility(View.GONE);
            holder.uploadImage.setVisibility(View.VISIBLE);

            holder.uploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isImageSelectable) {
                        mRefreshViews.uploadNewImage();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return shopPicturesList.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_view_shop_picture)
        ImageView shopImage;

        @BindView(R.id.text_view_picture_upload)
        TextView uploadImage;

        @BindView(R.id.image_view_selected)
        ImageView imageViewSelected;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    void clearSelectedPathList() {
        isImageSelectable = false;
        selectedPictureIdList.clear();
        mRefreshViews.refreshDeleteIcon();
        notifyDataSetChanged();
    }

    HashSet<String> getSelectedPictureIdList() {
        return selectedPictureIdList;
    }
}
