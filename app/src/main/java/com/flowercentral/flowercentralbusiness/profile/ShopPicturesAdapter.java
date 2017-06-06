package com.flowercentral.flowercentralbusiness.profile;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.profile.model.ShopPictureDetails;
import com.flowercentral.flowercentralbusiness.rest.BaseModel;
import com.flowercentral.flowercentralbusiness.rest.QueryBuilder;
import com.flowercentral.flowercentralbusiness.util.Util;
import com.flowercentral.flowercentralbusiness.volley.ErrorData;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

class ShopPicturesAdapter extends RecyclerView.Adapter<ShopPicturesAdapter.ViewHolder> {

    private static final String TAG = ShopPicturesAdapter.class.getSimpleName();
    private ArrayList<ShopPictureDetails> shopPicturesList = new ArrayList<>();
    private Context mContext;
    private RelativeLayout mRootLayout;
    private ShopPictures.RefreshViews mRefreshViews;
    private MaterialDialog mMaterialDialog;

    ShopPicturesAdapter(ArrayList<ShopPictureDetails> shopPicturesList, RelativeLayout rootLayout,
                        ShopPictures.RefreshViews refreshViews) {
        this.shopPicturesList = shopPicturesList;
        mRootLayout = rootLayout;
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
    public void onBindViewHolder(final ShopPicturesAdapter.ViewHolder holder, int position) {

        if(position < shopPicturesList.size()) {
            holder.shopImage.setVisibility(View.VISIBLE);
            holder.textViewRemove.setVisibility(View.VISIBLE);
            holder.uploadImage.setVisibility(View.GONE);

            Picasso.
                    with(mContext).
                    load(shopPicturesList.get(position).getImageUrl()).
                    into(holder.shopImage);

            holder.textViewRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePicture(shopPicturesList.get(holder.getAdapterPosition()).getPictureId());
                }
            });
        } else {
            holder.shopImage.setVisibility(View.GONE);
            holder.textViewRemove.setVisibility(View.GONE);
            holder.uploadImage.setVisibility(View.VISIBLE);

            holder.uploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    private void removePicture(String pictureId) {
        mMaterialDialog = Util.showProgressDialog(mContext, "Shop Pictures", "Removing pictures, please wait",false);

        BaseModel<JSONObject> baseModel = new BaseModel<JSONObject>(mContext) {
            @Override
            public void onSuccess(int statusCode, Map<String, String> headers, JSONObject response) {
                try {
                    if (response.getInt(mContext.getString(R.string.api_res_status)) == 1) {
                        Snackbar.make(mRootLayout, "Remove picture successfull!", Snackbar.LENGTH_SHORT).show();
                        mRefreshViews.performRefreshView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(mRootLayout, "Unable to remove picture.!", Snackbar.LENGTH_SHORT).show();
                }
                dismissMaterialDialog();
            }

            @Override
            public void onError(ErrorData error) {
                dismissMaterialDialog();
                if (error != null) {
                    error.setErrorMessage("Shop pictures remove failed. Cause -> " + error.getErrorMessage());

                    switch (error.getErrorType()) {
                        case NETWORK_NOT_AVAILABLE:
                            Snackbar.make(mRootLayout, mContext.getResources().getString(R.string.msg_internet_unavailable), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INTERNAL_SERVER_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case CONNECTION_TIMEOUT:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case APPLICATION_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case INVALID_INPUT_SUPPLIED:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case AUTHENTICATION_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        case UNAUTHORIZED_ERROR:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Snackbar.make(mRootLayout, error.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };

        String url = QueryBuilder.getRemovePictureUrl();
        try {
            JSONObject requestObject = new JSONObject();
            requestObject.put(mContext.getString(R.string.api_key_picture_id), pictureId);
            String srcFormat = "yyyy-MM-dd HH:mm";
            SimpleDateFormat formatSrc = new SimpleDateFormat(srcFormat, Locale.getDefault());
            requestObject.put(mContext.getString(R.string.api_key_timestamp), formatSrc.format(Calendar.getInstance().getTime()));
            baseModel.executePostJsonRequest(url, requestObject, TAG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void dismissMaterialDialog() {
        if(null != mMaterialDialog && mMaterialDialog.isShowing()) {
            mMaterialDialog.dismiss();
            mMaterialDialog = null;
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

        @BindView(R.id.text_view_remove)
        TextView textViewRemove;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
