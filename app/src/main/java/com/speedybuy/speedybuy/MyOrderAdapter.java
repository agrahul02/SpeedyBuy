package com.speedybuy.speedybuy;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.Viewholder> {

    private final List<MyOrderItemModel> myOrderItemModelList;
    private final Dialog loadingDialog;

    public MyOrderAdapter(List<MyOrderItemModel> myOrderItemModelList,Dialog loadingDialog) {
        this.myOrderItemModelList = myOrderItemModelList;
        this.loadingDialog = loadingDialog;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_item_layout,parent,false);
       return new Viewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        String resource = myOrderItemModelList.get(position).getProductImage();
        String productId = myOrderItemModelList.get(position).getProductId();
        int rating = myOrderItemModelList.get(position).getRating();
        String title = myOrderItemModelList.get(position).getProductTitle();
        String orderStatus = myOrderItemModelList.get(position).getOrderStatus();
        Date date;
        switch (orderStatus) {

            case "Ordered":
                date = myOrderItemModelList.get(position).getOrderedDate();
                break;
            case "Packed":
                date = myOrderItemModelList.get(position).getPackedDate();
                break;
            case "Shipped":
                date = myOrderItemModelList.get(position).getShippedDate();
                break;
            case "Delivered":
                date = myOrderItemModelList.get(position).getDeliveryDate();
                break;
            case "Cancelled":
                date = myOrderItemModelList.get(position).getCanceledDate();
                break;
            default:
                date = myOrderItemModelList.get(position).getCanceledDate();

        }
        holder.setData(resource,title,orderStatus,date,rating,productId,position);
    }

    @Override
    public int getItemCount() {
        return myOrderItemModelList.size();
    }

     class Viewholder extends RecyclerView.ViewHolder{

        private final ImageView prductImage;
        private final ImageView orderIndicator;
        private final TextView prductTitle;
         private final TextView deliveryStatus;
        private final LinearLayout rateNowContainer;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            prductImage = itemView.findViewById(R.id.product_imagen);
            prductTitle = itemView.findViewById(R.id.product_titlee);
            orderIndicator = itemView.findViewById(R.id.order_indicator);
            deliveryStatus = itemView.findViewById(R.id.order_deliverd_date);
            rateNowContainer = itemView.findViewById(R.id.rate_now_container_order_details);


        }
        private void setData(String resource,String titlee,String orderStatus, Date date,int rating,final String productID,int position){
            Glide.with(itemView.getContext()).load(resource).into(prductImage);
            prductTitle.setText(titlee);
            if (orderStatus.equals("cancelled")) {
                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.red)));
            }else {
                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.green)));
            }

            deliveryStatus.setText(orderStatus+ date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent orderDetailsIntent = new Intent(itemView.getContext(), OrderDetailsActivity.class);
                    orderDetailsIntent.putExtra("Position",position);
                    itemView.getContext().startActivity(orderDetailsIntent);
                }
            });

            //////////// rating layout //////////////
            setRating(rating);
            for (int i = 0; i < rateNowContainer.getChildCount(); i++) {
                final int starPosition = i;
                rateNowContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.show();
                        setRating(starPosition);
                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("PRODUCTS").document(productID);

                        FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Object>() {
                            @Nullable
                            @Override
                            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                DocumentSnapshot documentSnapshot = transaction.get(documentReference);

                                if (rating != 0){
                                    Long increase = documentSnapshot.getLong(starPosition+1+"_star") + 1;
                                    Long decrease = documentSnapshot.getLong(rating+1+"_star") - 1;
                                    transaction.update(documentReference,starPosition+1+"_star",increase);
                                    transaction.update(documentReference,rating+1+"_star",decrease);

                                }else {
                                    Long increase = documentSnapshot.getLong(starPosition+1+"_star") + 1;
                                    transaction.update(documentReference,starPosition+1+"_star",increase);
                                }
                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Object>() {
                            @Override
                            public void onSuccess(Object object) {

                                Map<String, Object> myRating = new HashMap<>();
                                if (DBqueries.myRatedIds.contains(productID)) {

                                    myRating.put("rating_" + DBqueries.myRatedIds.indexOf(productID), (long) starPosition + 1);

                                } else {
                                    myRating.put("list_size", (long) DBqueries.myRatedIds.size() + 1);
                                    myRating.put("product_ID_" + DBqueries.myRatedIds.size(), productID);
                                    myRating.put("rating_" + DBqueries.myRatedIds.size(), (long) starPosition + 1);
                                }
                                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                                        .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {

                                                                                            DBqueries.myOrderItemsModelList.get(position).setRating(starPosition);
                                                                                            if (DBqueries.myRatedIds.contains(productID)){
                                                                                                DBqueries.myRating.set(DBqueries.myRatedIds.indexOf(productID),Long.parseLong(String.valueOf(starPosition+1)));
                                                                                            }else {
                                                                                                DBqueries.myRatedIds.add(productID);
                                                                                                DBqueries.myRating.add(Long.parseLong(String.valueOf(starPosition+1)));
                                                                                            }

                                                                                        }else {
                                                                                            String error = task.getException().getMessage();
                                                                                            Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                        loadingDialog.dismiss();
                                                                                    }
                                                                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {

                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.dismiss();
                            }
                        });
                    }
                });
            }
            //////////// rating layout //////////////
        }
        private void setRating(int starPosition) {
            for (int i = 0; i < rateNowContainer.getChildCount(); i++) {
                ImageView starBtn = (ImageView)rateNowContainer.getChildAt(i);
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
                if (i <= starPosition){
                    starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
                }
            }
        }
    }
}
