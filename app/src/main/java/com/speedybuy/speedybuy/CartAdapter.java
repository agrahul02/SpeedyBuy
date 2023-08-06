package com.speedybuy.speedybuy;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CartAdapter extends RecyclerView.Adapter {

    private final List<CartItemModel> cartItemModelList;
    private int lastPosition = -1;
    private final TextView cartTotalAmount;

    private final boolean showDeleteBtn;

    /////// coupen Dialog ////////////
    private TextView coupenTitle;
    private  TextView coupenExpiryDate;
    private TextView coupenBody;
    public static RecyclerView coupenRecyclerView;
    private  LinearLayout selectedCoupen;
    private TextView discountedPrice;
    private TextView originalPrice;
    private LinearLayout applyORremoveBtnContainer;
    private TextView footerText;
    private Button removeCoupenBtn,applyCoupenBtn;
    private String productOriginalPrice;
    /////// coupen Dialog ///////////////////

    public CartAdapter(List<CartItemModel> cartItemModelList,TextView cartTotalAmount,boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount = cartTotalAmount;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < cartItemModelList.size()) {
            return CartItemModel.CART_ITEM;
           // return VIEW_TYPE_CART_ITEM;
        } else {
            return CartItemModel.TOTAL_AMOUNT;
           // return VIEW_TYPE_CART_TOTAL_AMOUNT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
        case CartItemModel.CART_ITEM:
           // case VIEW_TYPE_CART_ITEM:
                View cartItemView = inflater.inflate(R.layout.cart_item_layout, parent, false);
                viewHolder = new CartItemViewHolder(cartItemView);
                break;
                 case CartItemModel.TOTAL_AMOUNT:
            //case VIEW_TYPE_CART_TOTAL_AMOUNT:
                View cartTotalAmountView = inflater.inflate(R.layout.cart_total_amount_layout, parent, false);
                viewHolder = new CartTotalAmountViewHolder(cartTotalAmountView);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (cartItemModelList.get(position).getType()){
            case CartItemModel.CART_ITEM:
                String productID = cartItemModelList.get(position).getProductID();
                String resource = cartItemModelList.get(position).getProductImage();
                String title = cartItemModelList.get(position).getProductTitle();
                String productPrice = cartItemModelList.get(position).getProductPrice();
                String cuttedPrice = cartItemModelList.get(position).getCuttedPrice();
                Long offerApplied = cartItemModelList.get(position).getOfferApplied();
                boolean inStock = cartItemModelList.get(position).isInStock();
                Long productQuantity = cartItemModelList.get(position).getProductQuantity();
                Long maxQuantity = cartItemModelList.get(position).getMaxQuantity();
                boolean qtyError = cartItemModelList.get(position).isQtyError();
                List<String> qtyIds = cartItemModelList.get(position).getQtyIDs();
                Long stockQty = cartItemModelList.get(position).getStockQuantity();
                boolean COD = cartItemModelList.get(position).isCOD();

                ((CartItemViewHolder)holder).setItemDetails(productID,resource,title,productPrice,cuttedPrice,offerApplied,position,inStock,String.valueOf(productQuantity),maxQuantity,qtyError,qtyIds,stockQty,COD);
                CartItemViewHolder iewHolder = (CartItemViewHolder) holder;

                break;
            case CartItemModel.TOTAL_AMOUNT:
                int totalItems = 0;
                int totalItemPrice = 0;
                String deliveryPrice;
                int totalAmount;
                int savedAmount = 0;



                for (int x = 0; x < cartItemModelList.size(); x++) {
                    if (cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()){
                        int quantity = Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));
                        totalItems = totalItems + quantity;
                        if (TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupenId())) {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice())*quantity;
                        }else {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())*quantity;
                        }

                        if (!TextUtils.isEmpty(cartItemModelList.get(x).getCuttedPrice())){
                            savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getCuttedPrice())- Integer.parseInt(cartItemModelList.get(x).getProductPrice()))* quantity;
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupenId())) {
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice())- Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice()))* quantity;
                            }
                        }else {
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupenId())) {
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice())- Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice()))* quantity;
                            }
                        }
                    }
                }
                if (totalItemPrice > 500){
                    deliveryPrice = "Cost";
                    totalAmount = totalItemPrice;
                }else {
                    deliveryPrice = "60";
                    totalAmount = totalItemPrice + 60;
                }
                cartItemModelList.get(position).setTotalItems(totalItems);
                cartItemModelList.get(position).setTotalItemPrice(totalItemPrice);
                cartItemModelList.get(position).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalAmount(totalAmount);
                cartItemModelList.get(position).setSavedAmount(savedAmount);

               // CartTotalAmountViewHolder viewHolder = (CartTotalAmountViewHolder) holder;
               // ((CartTotalAmountViewHolder)holder).setTotalAmount(totalItems, totalItemPrice, deliveryPrice, totalAmount, savedAmount);
            break;
            default:
                return;
        }

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

     class CartItemViewHolder extends RecyclerView.ViewHolder{

        private final ImageView productImage;
        //private final ImageView freeCoupenIcon;
        private final TextView productTitle;
      //  private final TextView freeCoupens;
        private final TextView productPrice;
        private final TextView cuttedPrice;
        private final TextView offerApplied;
        private final TextView coupenApplied;
        private final TextView productQuantity;
        private final LinearLayout coupenRedemptionLayout;
        private final TextView coupenRedemptionBody;

        private final LinearLayout deleteBtn;
        private final Button redeemBtn;
        private final ImageView codIndicator;

        //Viewholder//
        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_titlen);
           // freeCoupenIcon = itemView.findViewById(R.id.free_coupen_icon);
          //  freeCoupens = itemView.findViewById(R.id.tv_free_coupen);
            productPrice = itemView.findViewById(R.id.product_pricen);
            cuttedPrice = itemView.findViewById(R.id.cuted_price);
            offerApplied = itemView.findViewById(R.id.offers_applied);
            coupenApplied = itemView.findViewById(R.id.coupen_applied);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            coupenRedemptionLayout = itemView.findViewById(R.id.coupen_redemption_layout_cart);
            coupenRedemptionBody = itemView.findViewById(R.id.tv_coupen_redemption_cart);
            codIndicator = itemView.findViewById(R.id.cod_indicator_cart_item);

            redeemBtn = itemView.findViewById(R.id.coupen_redemption_btn_cart);
            deleteBtn = itemView.findViewById(R.id.remove_item_btn);
        }

        private void setItemDetails(String productID, String resource, String title, String productPriceText, String cuttedPriceText, Long offerAppliedNo, final int position, boolean inStock,String quantity, Long maxQuantity,boolean qtyError,List<String> qtyIds,long stockQty,boolean COD){

            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.small_placeholder)).into(productImage);
          productTitle.setText(title);

            final Dialog checkCoupenPriceDialog = new Dialog(itemView.getContext());
            checkCoupenPriceDialog.setContentView(R.layout.coupen_redeem_dialog);
            checkCoupenPriceDialog.setCancelable(false);
            checkCoupenPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            /*if (COD){
                codIndicator.setVisibility(View.VISIBLE);
            }else {
                codIndicator.setVisibility(View.INVISIBLE);
            }*/
            if (inStock) {

              /*  if (freeCoupensNo > 0){
                    freeCoupenIcon.setVisibility(View.VISIBLE);
                    freeCoupens.setVisibility(View.VISIBLE);
                    if (freeCoupensNo == 1){
                        freeCoupens.setText("free "+freeCoupensNo+" Coupen");
                    }else {
                        freeCoupens.setText("free "+freeCoupensNo+" Coupens");
                    }
                }else {
                    freeCoupenIcon.setVisibility(View.INVISIBLE);

                    freeCoupens.setVisibility(View.INVISIBLE);
                }*/

                productPrice.setText(productPriceText);
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setText(cuttedPriceText);
                coupenRedemptionLayout.setVisibility(View.VISIBLE);

                ////// coupens

                ImageView toggelRecyclerView = checkCoupenPriceDialog.findViewById(R.id.toggel_recyclerview);
                RecyclerView coupensRecyclerView = checkCoupenPriceDialog.findViewById(R.id.coupen_recyclerview);
                selectedCoupen = checkCoupenPriceDialog.findViewById(R.id.selected_coupen);
//                coupenTitle = checkCoupenPriceDialog.findViewById(R.id.coupen_title);
               // coupenExpiryDate = checkCoupenPriceDialog.findViewById(R.id.coupen_validity_reward);
                //coupenBody = checkCoupenPriceDialog.findViewById(R.id.coupen_body_reward);
                footerText = checkCoupenPriceDialog.findViewById(R.id.footer_text);
                applyORremoveBtnContainer = checkCoupenPriceDialog.findViewById(R.id.apply_or_remove_btn_container);
                removeCoupenBtn = checkCoupenPriceDialog.findViewById(R.id.remove_btn);
                applyCoupenBtn = checkCoupenPriceDialog.findViewById(R.id.apply_btn);

                footerText.setVisibility(View.GONE);
                applyORremoveBtnContainer.setVisibility(View.VISIBLE);
                originalPrice = checkCoupenPriceDialog.findViewById(R.id.original_price);
                discountedPrice = checkCoupenPriceDialog.findViewById(R.id.discounted_price);


                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                coupensRecyclerView.setLayoutManager(layoutManager);

                originalPrice.setText(productPrice.getText());
                productOriginalPrice = productPriceText;
                MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(position,DBqueries.rewardsModelList,true,coupensRecyclerView,selectedCoupen,productOriginalPrice,coupenTitle,coupenExpiryDate,coupenBody,discountedPrice,cartItemModelList);
                coupensRecyclerView.setAdapter(myRewardsAdapter);
                myRewardsAdapter.notifyDataSetChanged();

               /* applyCoupenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupenId())) {
                            for (RewardModel rewardModel : DBqueries.rewardsModelList) {
                                if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())) {
                                    rewardModel.setAlreadyUsed(true);
                                    coupenRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward_gridient_background));
                                    coupenRedemptionBody.setText(rewardModel.getCoupenBody());
                                    redeemBtn.setText("Coupen");
                                }
                            }
                            coupenApplied.setVisibility(View.VISIBLE);
                            cartItemModelList.get(position).setDiscountedPrice(discountedPrice.getText().toString().substring(3,discountedPrice.getText().length() - 2));
                            productPrice.setText(discountedPrice.getText());
                            String offerDiscountAmt = String.valueOf(Long.valueOf(productPriceText)- Long.valueOf(discountedPrice.getText().toString().substring(3,discountedPrice.getText().length() - 2)));
                            coupenApplied.setText("Coupen applied -" + offerDiscountAmt);
                            notifyItemChanged(cartItemModelList.size() - 1);
                            checkCoupenPriceDialog.dismiss();
                        }
                    }
                });
                removeCoupenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (RewardModel rewardModel : DBqueries.rewardsModelList){
                            if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())){
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                        coupenTitle.setText("Coupen");
                        coupenExpiryDate.setText("validity");
                        coupenBody.setText("Tap the icon on the right and select coupen");
                        coupenApplied.setVisibility(View.INVISIBLE);
                        coupenRedemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.coupenRed));
                        coupenRedemptionBody.setText("apply coupen here");
                        redeemBtn.setText("Redeem");
                        cartItemModelList.get(position).setSelectedCoupenId(null);
                        productPrice.setText("Rs."+productPriceText+"/-");
                        notifyItemChanged(cartItemModelList.size() - 1);
                        checkCoupenPriceDialog.dismiss();
                    }
                });*/
                toggelRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showDialogRecyclerView();
                    }
                });


                  /*  if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupenId())) {
                        for (RewardModel rewardModel : DBqueries.rewardsModelList) {
                            if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())) {
                                coupenRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward_gridient_background));
                                coupenRedemptionBody.setText(rewardModel.getCoupenBody());
                                redeemBtn.setText("Coupen");

                                coupenBody.setText(rewardModel.getCoupenBody());
                                if (rewardModel.getType().equals("Discount")){
                                    coupenTitle.setText(rewardModel.getType());
                                }else {
                                    coupenTitle.setText("FLAT Rs."+rewardModel.getDiscORamt()+"OFF");
                                }
                                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");
                                coupenExpiryDate.setText("till"+simpleDateFormat.format(rewardModel.getTimestamp()));
                            }
                        }
                        discountedPrice.setText("-"+cartItemModelList.get(position).getDiscountedPrice());
                        coupenApplied.setVisibility(View.VISIBLE);
                        productPrice.setText("-"+cartItemModelList.get(position).getDiscountedPrice());
                        String offerDiscountAmt = String.valueOf(Long.valueOf(productPriceText)- Long.valueOf(cartItemModelList.get(position).getDiscountedPrice()));
                        coupenApplied.setText("Coupen applied -" + offerDiscountAmt);
                    }else {
                        coupenApplied.setVisibility(View.INVISIBLE);
                        coupenRedemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.coupenRed));
                        coupenRedemptionBody.setText("apply coupen here");
                        redeemBtn.setText("Redeem");


                    }*/
                ////// coupens

                productQuantity.setText("Qty: " + quantity);

                if (!showDeleteBtn) {
                    if (qtyError) {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.teal_200));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.teal_200)));

                    } else {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.black)));

                    }
                }
                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        quantityDialog.setCancelable(false);

                        final EditText quantityNo = quantityDialog.findViewById(R.id.quantity_no);
                        Button cancelBtn = quantityDialog.findViewById(R.id.cancel_btn);
                        Button okBtn = quantityDialog.findViewById(R.id.ok_btn);
                        quantityNo.setHint("Max "+ maxQuantity);

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });
                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(quantityNo.getText())) {
                                    if (Long.valueOf(quantityNo.getText().toString()) <= maxQuantity && Long.valueOf(quantityNo.getText().toString()) != 0 ) {
                                        if (itemView.getContext() instanceof MainActivity){
                                            cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                        }else {
                                            if (DeliveryActivity.fromCart) {
                                                cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            } else {
                                                DeliveryActivity.cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            }
                                        }
                                        productQuantity.setText("Qty: " + quantityNo.getText());
                                        notifyItemChanged(cartItemModelList.size() - 1);

                                        if (!showDeleteBtn){
                                            DeliveryActivity.loadingDialog.show();
                                            DeliveryActivity.cartItemModelList.get(position).setQtyError(false);
                                            final int initialQty = Integer.parseInt(quantity);
                                            final int finalQty = Integer.parseInt(quantityNo.getText().toString());
                                            final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                            if (finalQty > initialQty) {

                                                for (int y = 0; y < finalQty - initialQty; y++) {
                                                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);

                                                    Map<String, Object> timestamp = new HashMap<String, Object>();
                                                    timestamp.put("time", FieldValue.serverTimestamp());
                                                    int finalY = y;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    qtyIds.add(quantityDocumentName);

                                                                    if (finalY + 1 == finalQty - initialQty) {

                                                                        firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(stockQty).get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            List<String> serverQuantity = new ArrayList<String>();

                                                                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                                                serverQuantity.add(queryDocumentSnapshot.getId());
                                                                                            }
                                                                                            long availableQty = 0;
                                                                                            for (String qtyId : qtyIds) {

                                                                                                if (!serverQuantity.contains(qtyId)) {
                                                                                                        DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                                        DeliveryActivity.cartItemModelList.get(position).setMaxQuantity(availableQty);
                                                                                                        Toast.makeText(itemView.getContext(), "all product not ", Toast.LENGTH_SHORT).show();
                                                                                                }else {
                                                                                                    availableQty++;
                                                                                                }
                                                                                            }
                                                                                          DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                                                        } else {
                                                                                            String error = task.getException().getMessage();
                                                                                            Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                                    }

                                                                                });

                                                                    }
                                                                }
                                                            });

                                                }
                                            }else if (initialQty > finalQty){
                                                for (int x = 0; x < initialQty - finalQty; x++) {
                                                   final String qtyId = qtyIds.get(qtyIds.size() - 1 - x);

                                                    int finalX = x;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").document(qtyId).delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>(){
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                   qtyIds.remove(qtyId);
                                                                    DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                                    if ( finalX + 1 == initialQty - finalQty){
                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(itemView.getContext(), "Max quantity :"+ maxQuantity, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                quantityDialog.dismiss();

                            }
                        });
                        quantityDialog.show();
                    }
                });
                if (offerAppliedNo > 0) {
                    offerApplied.setVisibility(View.VISIBLE);
                    String offerDiscountAmt = String.valueOf(Long.valueOf(cuttedPriceText)- Long.valueOf(productPriceText));
                    offerApplied.setText("offer applied -" +offerDiscountAmt);
                }else {
                    offerApplied.setVisibility(View.INVISIBLE);
                }
            }else {
                productPrice.setText("WELCOME");
                cuttedPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.teal_200));
                cuttedPrice.setText("");
                coupenRedemptionLayout.setVisibility(View.GONE);
                //freeCoupens.setVisibility(View.INVISIBLE);
                productQuantity.setVisibility(View.INVISIBLE);
                coupenApplied.setVisibility(View.GONE);
                offerApplied.setVisibility(View.GONE);
               // freeCoupenIcon.setVisibility(View.INVISIBLE);
            }


            if (showDeleteBtn){
                deleteBtn.setVisibility(View.VISIBLE);
            }else {
                deleteBtn.setVisibility(View.GONE);
            }



            redeemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (RewardModel rewardModel : DBqueries.rewardsModelList){
                        if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())){
                            rewardModel.setAlreadyUsed(false);
                        }
                    }
                    checkCoupenPriceDialog.show();
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupenId())) {
                        for (RewardModel rewardModel : DBqueries.rewardsModelList){
                            if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())){
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                    }
                        if (!ProductDetailsActivity.running_cart_query){
                        ProductDetailsActivity.running_cart_query = true;
                        DBqueries.removeFromCart(position,itemView.getContext(),cartTotalAmount);
                    }
                }
            });
        }
         private void showDialogRecyclerView() {
             if (coupenRecyclerView.getVisibility() == View.GONE) {
                 coupenRecyclerView.setVisibility(View.VISIBLE);
                 selectedCoupen.setVisibility(View.GONE);
             } else {
                 coupenRecyclerView.setVisibility(View.GONE);
                 selectedCoupen.setVisibility(View.VISIBLE);
             }
         }
     }

    class CartTotalAmountViewHolder extends RecyclerView.ViewHolder {

        private final TextView totalItems;
        private final TextView totalItemPrice;
        private final TextView deliveryPrice;
        private final TextView totalAmount;
        private final TextView savedAmount;


        public CartTotalAmountViewHolder(@NonNull View itemView) {
            super(itemView);

            totalItems = itemView.findViewById(R.id.total_items);
            totalItemPrice = itemView.findViewById(R.id.total_item_price);
            deliveryPrice = itemView.findViewById(R.id.delivery_charge_price);
            totalAmount = itemView.findViewById(R.id.total_price);
            savedAmount = itemView.findViewById(R.id.saved_amount);
        }

        private void setTotalAmount(int totalItemText, int totalItemPriceText, String deliveryPriceText, int totalAmountText, int savedAmountText) {
            totalItems.setText("Price("+totalItemText+" items)");
            totalItemPrice.setText(totalItemPriceText);
            if (deliveryPriceText.equals("FREE")){
                deliveryPrice.setText(deliveryPriceText);
            }else {
                deliveryPrice.setText(deliveryPriceText);
            }

            totalAmount.setText(totalAmountText);
            cartTotalAmount.setText(totalAmountText);
            savedAmount.setText("You save"+savedAmountText+"on this order");

            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
            if (totalItemPriceText == 0){
                if (DeliveryActivity.fromCart) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                    DeliveryActivity.cartItemModelList.remove(DeliveryActivity.cartItemModelList.size() - 1);
                }
                if (showDeleteBtn){
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                parent.setVisibility(View.GONE);
            }else {
                parent.setVisibility(View.VISIBLE);
            }
        }
    }
}
