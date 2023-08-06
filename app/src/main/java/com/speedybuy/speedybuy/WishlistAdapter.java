package com.speedybuy.speedybuy;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder>{


    private boolean fromSearch;
    List<WishlistModel> wishlistModelList;
    private final Boolean wishlist;
    private int lastPosition = -1;

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public WishlistAdapter(List<WishlistModel> wishlistModelList, Boolean wishlist) {
        this.wishlistModelList = wishlistModelList;
        this.wishlist = wishlist;
    }

    public List<WishlistModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public void setWishlistModelList(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String productId = wishlistModelList.get(position).getProductId();
        String resource = wishlistModelList.get(position).getProductImage();
        String title = wishlistModelList.get(position).getProductTitle();
      //  Long freeCoupens = wishlistModelList.get(position).getFreeCoupens();
        String rating = wishlistModelList.get(position).getRating();
        String totalRatings = wishlistModelList.get(position).getTotalRatings();
        String productPrice = wishlistModelList.get(position).getProductPrice();
        String cuttedPrice = wishlistModelList.get(position).getCutterPrice();
        boolean paymentmethod = wishlistModelList.get(position).isCOD();
        boolean inStock = wishlistModelList.get(position).isInStock();
        holder.setData(productId,resource, title,rating,totalRatings,productPrice,cuttedPrice,paymentmethod,position,inStock);

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView productImage;
        private final TextView productTitle;
        //private final TextView freeCoupens;
       // private final ImageView coupenIcon;
        private final TextView rating;
        private final TextView totalRating;
        private final View priceCut;
        private final TextView productPrice;
        private final TextView cuttedPrice;
        private final TextView paymentMethod;
        private final ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image_wishlist);
            productTitle = itemView.findViewById(R.id.product_title_wishlist);
            //freeCoupens = itemView.findViewById(R.id.free_coupen_wishlist);
            //coupenIcon = itemView.findViewById(R.id.coupen_icon_wishlist);
            rating = itemView.findViewById(R.id.tv_product_rating_miniview);
            totalRating = itemView.findViewById(R.id.total_rating_wislist);
            priceCut = itemView.findViewById(R.id.price_cut);
            productPrice = itemView.findViewById(R.id.product_price_wishlist);
            cuttedPrice = itemView.findViewById(R.id.cutted_price_wishlist);
            paymentMethod = itemView.findViewById(R.id.payment_method_wishlist);
            deleteBtn = itemView.findViewById(R.id.delete_btn_wishlist);
        }
        private void setData(String productId, String resource, String title, String averageRate, String totalRatingNo, String price, String cuttedPriceValue, boolean COD, int index, boolean inStock){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.small_placeholder)).into(productImage);
            productTitle.setText(title);
            /*if (freeCoupenNo != 0 && inStock){
                coupenIcon.setVisibility(View.VISIBLE);
                if (freeCoupenNo == 1){
                    freeCoupens.setText("free " + freeCoupenNo + " coupen");
                }else {
                    freeCoupens.setText("free " + freeCoupenNo + " coupenss");
                }
            }else {
                coupenIcon.setVisibility(View.INVISIBLE);
                freeCoupens.setVisibility(View.INVISIBLE);
            }*/
            LinearLayout linearLayout = (LinearLayout) rating.getParent();
            if (inStock){
                rating.setVisibility(View.VISIBLE);
                totalRating.setVisibility(View.VISIBLE);
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);

                rating.setText(averageRate);
                totalRating.setText("("+totalRatingNo+")ratings)");
                productPrice.setText(price);
                cuttedPrice.setText(cuttedPriceValue);
                if (COD){
                    paymentMethod.setVisibility(View.VISIBLE);
                }else {
                    paymentMethod.setVisibility(View.INVISIBLE);
                }
            }else {
                linearLayout.setVisibility(View.INVISIBLE);
                rating.setVisibility(View.INVISIBLE);
                totalRating.setVisibility(View.INVISIBLE);
                productPrice.setText("WELCOME");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.teal_200));
                cuttedPrice.setVisibility(View.INVISIBLE);
                paymentMethod.setVisibility(View.INVISIBLE);
            }


           if (wishlist){
               deleteBtn.setVisibility(View.VISIBLE);
           }else {
               deleteBtn.setVisibility(View.GONE);
           }
            deleteBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (!ProductDetailsActivity.running_wishlist_query) {
                       ProductDetailsActivity.running_wishlist_query = true;
                       DBqueries.removeFormWishlist(index, itemView.getContext());
                   }
               }
            });
             itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (fromSearch){
                         ProductDetailsActivity.fromSearch = true;
                     }
                     Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                     productDetailsIntent.putExtra("PRODUCT_ID", productId);
                     itemView.getContext().startActivity(productDetailsIntent);
                 }
             });
        }
    }
}
