package com.speedybuy.speedybuy;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class HorizontalProductScrollAdapter extends RecyclerView.Adapter<HorizontalProductScrollAdapter.ViewHolder> {

    private final List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    public HorizontalProductScrollAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        String resource = horizontalProductScrollModelList.get(position).getProductImage();
        String title = horizontalProductScrollModelList.get(position).getProductTitle();
        String description = horizontalProductScrollModelList.get(position).getProductDescription();
        String  price = horizontalProductScrollModelList.get(position).getProductPrice();
        String productId = horizontalProductScrollModelList.get(position).getProductID();

        viewHolder.setData(productId,resource,title,description,price);

    }


    @Override
    public int getItemCount() {
        if (horizontalProductScrollModelList.size() > 8){
            return 8;
        }else{
            return horizontalProductScrollModelList.size();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView productImage;
        private final TextView productTitle;
        private final TextView productDescription;
        private final TextView productPrice;
        private ConstraintLayout card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.h_s_product_image);
            productTitle = itemView.findViewById(R.id.h_s_product_title);
            productDescription = itemView.findViewById(R.id.h_s_product_description);
            productPrice = itemView.findViewById(R.id.h_s_product_price);
            card = itemView.findViewById(R.id.card);

        }

        private void setData(final String productId,String resource,String title,String description,String price){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.small_placeholder)).into(productImage);
            productPrice.setText(price);
            productDescription.setText(description);
            productTitle.setText(title);


            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID", productId);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });


        }

    }
}
