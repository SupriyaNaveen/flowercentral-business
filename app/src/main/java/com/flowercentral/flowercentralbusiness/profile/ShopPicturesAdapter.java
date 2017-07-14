package com.flowercentral.flowercentralbusiness.profile;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.ShopPicturesItemRowBinding;
import com.flowercentral.flowercentralbusiness.profile.model.ShopPictureDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;

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
        ShopPicturesItemRowBinding binder = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.shop_pictures_item_row, parent, false);
        mContext = parent.getContext();
        return new ShopPicturesAdapter.ViewHolder(binder);
    }

    @Override
    public void onBindViewHolder(final ShopPicturesAdapter.ViewHolder holder, final int position) {

        final ShopPicturesItemRowBinding binder = holder.picturesItemRowBinder;
        if (position < shopPicturesList.size()) {
            binder.imageViewShopPicture.setVisibility(View.VISIBLE);
            binder.textViewPictureUpload.setVisibility(View.GONE);

            Picasso.
                    with(mContext).
                    load(shopPicturesList.get(position).getImageUrl()).
                    into(binder.imageViewShopPicture);

            //Show/hide selected image list
            final String pictureId = shopPicturesList.get(position).getPictureId();
            if (selectedPictureIdList.contains(pictureId)) {
                binder.imageViewSelected.setVisibility(View.VISIBLE);
            } else {
                binder.imageViewSelected.setVisibility(View.GONE);
            }

            binder.imageViewShopPicture.setLongClickable(true);
            binder.imageViewShopPicture.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    isImageSelectable = true;
                    //Show the selected button
                    binder.imageViewSelected.setVisibility(View.VISIBLE);
                    selectedPictureIdList.add(pictureId);
                    mRefreshViews.refreshDeleteIcon();
                    return true;
                }
            });

            binder.imageViewShopPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isImageSelectable) {
                        //If already selected, then un select
                        if (selectedPictureIdList.contains(pictureId)) {
                            binder.imageViewSelected.setVisibility(View.GONE);
                            selectedPictureIdList.remove(pictureId);

                            if (selectedPictureIdList.isEmpty()) {
                                isImageSelectable = false;
                                mRefreshViews.refreshDeleteIcon();
                            }
                        } else {
                            binder.imageViewSelected.setVisibility(View.VISIBLE);
                            selectedPictureIdList.add(pictureId);
                        }
                    }
                }
            });

        } else {
            binder.imageViewShopPicture.setVisibility(View.GONE);
            binder.textViewPictureUpload.setVisibility(View.VISIBLE);

            binder.textViewPictureUpload.setOnClickListener(new View.OnClickListener() {
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

    class ViewHolder extends RecyclerView.ViewHolder {

       ShopPicturesItemRowBinding picturesItemRowBinder;

        ViewHolder(ShopPicturesItemRowBinding binder) {
            super(binder.getRoot());
            picturesItemRowBinder = binder;
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
