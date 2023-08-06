package com.speedybuy.speedybuy;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsActivity<simpleDateFormat> extends AppCompatActivity {

    private int position;

    private TextView title,price,quantity;
    private ImageView productImage,orderedIndicator,packedIndicator,shippedIndicator,deliveredIndicator;
    private ProgressBar O_P_progress,P_S_progress,S_D_progress;
    private TextView orderedTitle,packedTitle,shippedTitle,deliveredTitle;
    private TextView orderedDate, packedDate,shippedDate,deliveredDate;
    private TextView orderedBody, packedBody,shippedBody,deliveredBody;
    private LinearLayout rateNowContainer;
    private int rating;
    private TextView fullName,address,pincode;
    private TextView totalItems,totalItemsPrice,deliveryPrice,totalAmount,savedAmount;
    private Dialog loadingDialog,cancelDialog;
    private simpleDateFormat simpleDateFormat;
    private Button cancelOrderButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Toolbar toolbar = findViewById(R.id.toolbars);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //////// loadingDialog
        loadingDialog = new Dialog(OrderDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ///////// loadingDialog

        //////// cancelDialog
        cancelDialog = new Dialog(OrderDetailsActivity.this);
        cancelDialog.setContentView(R.layout.order_cancel_dialog);
        cancelDialog.setCancelable(true);
        cancelDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
       // cancelDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ///////// cancelDialog

        position = getIntent().getIntExtra("Position", -1);
        MyOrderItemModel model = DBqueries.myOrderItemsModelList.get(position);

        title = findViewById(R.id.product_title_order_details);
        price = findViewById(R.id.product_price_order_details);
        quantity = findViewById(R.id.product_quantity_order_details);
        productImage = findViewById(R.id.product_image_order_details);
        cancelOrderButton = findViewById(R.id.cancel_btn_order_details);

        orderedIndicator = findViewById(R.id.order_indicator_order_status);
        packedIndicator = findViewById(R.id.packed_indicator_order_status);
        shippedIndicator = findViewById(R.id.shipped_indicator_order_status);
        deliveredIndicator = findViewById(R.id.deliverd_indicator__order_details);

        O_P_progress = findViewById(R.id.orderd_packed_progressbar_order_details);
        P_S_progress = findViewById(R.id.packed_shipped_progressbar_order_details);
        S_D_progress = findViewById(R.id.shipped_delivered_progressbar_order_details);

        orderedTitle = findViewById(R.id.ordered_title_order_details);
        packedTitle = findViewById(R.id.packed_title_order_details);
        shippedTitle = findViewById(R.id.shipping_title_order_details);
        deliveredTitle = findViewById(R.id.deliverd_title_order_details);

        orderedDate = findViewById(R.id.ordered_date_order_details);
        packedDate = findViewById(R.id.packed_date_order_details);
        shippedDate = findViewById(R.id.shipping_date_order_details);
        deliveredDate = findViewById(R.id.deliverd_date_order_details);

        orderedBody = findViewById(R.id.ordered_body_order_details);
        packedBody = findViewById(R.id.packed_body_order_details);
        shippedBody = findViewById(R.id.shipping_body_order_details);
        deliveredBody = findViewById(R.id.deliverd_body_order_details);

        rateNowContainer = findViewById(R.id.rate_now_container_order_details);
        fullName = findViewById(R.id.fullname_shipping);
        address = findViewById(R.id.address_shipping);
        pincode = findViewById(R.id.pin_code_shiping);

        totalItems = findViewById(R.id.total_items);
        totalItemsPrice = findViewById(R.id.total_item_price);
        deliveryPrice = findViewById(R.id.delivery_charge_price);
        totalAmount = findViewById(R.id.total_price);
        savedAmount = findViewById(R.id.saved_amount);


        title.setText(model.getProductTitle());
        if (!model.getDiscountedPrice().equals("")) {
            price.setText("-"+model.getDiscountedPrice());
        }else {
            price.setText(model.getProductPrice());
        }
         quantity.setText("Qty :"+ model.getProductQuantity());
        Glide.with(this).load(model.getProductImage()).into(productImage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            simpleDateFormat = (simpleDateFormat) new SimpleDateFormat("EEE, dd MMM YYYY hh:mm aa");
        }
        switch (model.getOrderStatus()){
            case "Ordered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(model.getOrderedDate()));
//                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);
                O_P_progress.setVisibility(View.GONE);

                packedIndicator.setVisibility(View.GONE);
                packedBody.setVisibility(View.GONE);
                packedDate.setVisibility(View.GONE);
                packedTitle.setVisibility(View.GONE);

                shippedIndicator.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);

                break;
            case "Packed":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(model.getOrderedDate()));
                //orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(model.getPackedDate()));
                //orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                O_P_progress.setProgress(100);
                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);


                shippedIndicator.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                break;
            case "Shipped":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(model.getOrderedDate()));
                //orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(model.getPackedDate()));
                //orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                shippedDate.setText(String.valueOf(model.getShippedDate()));


                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setVisibility(View.GONE);


                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                break;
            case "Out for Delivery":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(model.getOrderedDate()));
                //orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(model.getPackedDate()));
                //packedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                shippedDate.setText(String.valueOf(model.getShippedDate()));
                //shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                deliveredDate.setText(String.valueOf(model.getDeliveryDate()));
                //deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);

                deliveredTitle.setText("Out for delivery");
                deliveredBody.setText("your order Out for delivery");
                break;
            case "Delivered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                orderedDate.setText(String.valueOf(model.getOrderedDate()));
                //orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                packedDate.setText(String.valueOf(model.getPackedDate()));
                //packedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                shippedDate.setText(String.valueOf(model.getShippedDate()));
                //shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                deliveredDate.setText(String.valueOf(model.getDeliveryDate()));
                //deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);


                break;
            case "Cancelled":

                if (model.getPackedDate().after(model.getOrderedDate())) {

                    if (model.getShippedDate().after(model.getPackedDate())) {

                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        orderedDate.setText(String.valueOf(model.getOrderedDate()));
                        //orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        packedDate.setText(String.valueOf(model.getPackedDate()));
                        ///packedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        shippedDate.setText(String.valueOf(model.getShippedDate()));
                        //shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                        deliveredDate.setText(String.valueOf(model.getCanceledDate()));
                        //deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        deliveredTitle.setText("Cancelled");
                        deliveredBody.setText("Your Order Cancel");

                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);
                        S_D_progress.setProgress(100);

                    }else {
                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        orderedDate.setText(String.valueOf(model.getOrderedDate()));
                        //orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        packedDate.setText(String.valueOf(model.getPackedDate()));
                        //packedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                        shippedDate.setText(String.valueOf(model.getCanceledDate()));
                       // shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        shippedTitle.setText("Cancelled");
                        shippedBody.setText("Your Order Cancel");

                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);
                        S_D_progress.setVisibility(View.GONE);


                        deliveredIndicator.setVisibility(View.GONE);
                        deliveredBody.setVisibility(View.GONE);
                        deliveredDate.setVisibility(View.GONE);
                        deliveredTitle.setVisibility(View.GONE);
                    }
                }else {
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                    orderedDate.setText(String.valueOf(model.getOrderedDate()));
                    //orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    packedDate.setText(String.valueOf(model.getCanceledDate()));
                    //packedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));
                    packedTitle.setText("Cancelled");
                    packedBody.setText("Your Order Cancel");

                    O_P_progress.setProgress(100);
                    P_S_progress.setVisibility(View.GONE);
                    S_D_progress.setVisibility(View.GONE);


                    shippedIndicator.setVisibility(View.GONE);
                    shippedBody.setVisibility(View.GONE);
                    shippedDate.setVisibility(View.GONE);
                    shippedTitle.setVisibility(View.GONE);

                    deliveredIndicator.setVisibility(View.GONE);
                    deliveredBody.setVisibility(View.GONE);
                    deliveredDate.setVisibility(View.GONE);
                    deliveredTitle.setVisibility(View.GONE);
                }

                break;
        }
//////////// rating layout //////////////
        rating = model.getRating();
        setRating(rating);
        for (int i = 0; i < rateNowContainer.getChildCount(); i++) {
            final int starPosition = i;
            rateNowContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingDialog.show();
                    setRating(starPosition);
                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("PRODUCTS").document(model.getProductId());

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
                            if (DBqueries.myRatedIds.contains(model.getProductId())) {

                                myRating.put("rating_" + DBqueries.myRatedIds.indexOf(model.getProductId()), (long) starPosition + 1);

                            } else {
                                myRating.put("list_size", (long) DBqueries.myRatedIds.size() + 1);
                                myRating.put("product_ID_" + DBqueries.myRatedIds.size(), model.getProductId());
                                myRating.put("rating_" + DBqueries.myRatedIds.size(), (long) starPosition + 1);
                            }
                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                                    .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                DBqueries.myOrderItemsModelList.get(position).setRating(starPosition);
                                                if (DBqueries.myRatedIds.contains(model.getProductId())){
                                                    DBqueries.myRating.set(DBqueries.myRatedIds.indexOf(model.getProductId()),Long.parseLong(String.valueOf(starPosition+1)));
                                                }else {
                                                    DBqueries.myRatedIds.add(model.getProductId());
                                                    DBqueries.myRating.add(Long.parseLong(String.valueOf(starPosition+1)));
                                                }

                                            }else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
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

        if (model.isCancellationRequested()){
            cancelOrderButton.setVisibility(View.VISIBLE);
            cancelOrderButton.setEnabled(false);
            cancelOrderButton.setText("Cancellation in process");
            cancelOrderButton.setTextColor(getResources().getColor(R.color.teal_200));
            cancelOrderButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        }else {
            if (model.getOrderStatus().equals("Ordered") || model.getOrderStatus().equals("Packed")){
                cancelOrderButton.setVisibility(View.VISIBLE);
                cancelOrderButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDialog.findViewById(R.id.no_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDialog.dismiss();
                            }
                        });
                        cancelDialog.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDialog.dismiss();
                                loadingDialog.show();
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("Order Id",model.getOrderId());
                                map.put("product Id",model.getProductId());
                                map.put("Order Cancelled",false);
                                FirebaseFirestore.getInstance().collection("CANCELLED ORDERS").document().set(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    FirebaseFirestore.getInstance().collection("ORDERS").document(model.getOrderId()).collection("OrderItems").document(model.getProductId()).update("Cancellation request",true)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        model.setCancellationRequested(true);
                                                                        cancelOrderButton.setEnabled(false);
                                                                        cancelOrderButton.setText("Cancellation in process");
                                                                        cancelOrderButton.setTextColor(getResources().getColor(R.color.teal_200));
                                                                        cancelOrderButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                                                    }else{
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    loadingDialog.dismiss();
                                                                }
                                                            });
                                                }else{
                                                    loadingDialog.dismiss();
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                        cancelDialog.show();
                    }
                });
            }
        }

        fullName.setText(model.getFullName());
        address.setText(model.getAddress());
        pincode.setText(model.getPincode());

        totalItems.setText("Price("+model.getProductQuantity()+"items)");

        Long totalItemsPriceValue;

        if (model.getDiscountedPrice().equals("")){
           totalItemsPriceValue = model.getProductQuantity()*Long.valueOf(model.getProductPrice());
            totalItemsPrice.setText("-"+totalItemsPriceValue+"-");

        }else {
            totalItemsPriceValue = model.getProductQuantity()*Long.valueOf(model.getDiscountedPrice());
            totalItemsPrice.setText("-"+totalItemsPriceValue+"-");
        }
        if (model.getDeliveryPrice().equals("FREE")){
            deliveryPrice.setText(model.getDeliveryPrice());
            totalAmount.setText(totalItemsPrice.getText());
        }else {
            deliveryPrice.setText("-" + model.getDeliveryPrice() + "-");
            totalAmount.setText("-"+ (totalItemsPriceValue+Long.valueOf(model.getDeliveryPrice()))+"-");
        }
        if (!model.getCuttedprice().equals("")){
            if (!model.getDiscountedPrice().equals("")){
                savedAmount.setText("You saved "+model.getProductQuantity()*(Long.valueOf(model.getCuttedprice()) -Long.valueOf(model.getDiscountedPrice())) +"on this order");
            }else {
                savedAmount.setText("You saved "+model.getProductQuantity()*(Long.valueOf(model.getCuttedprice()) -Long.valueOf(model.getProductPrice())) +"on this order");
            }
        }else {
            if (!model.getDiscountedPrice().equals("")){
                savedAmount.setText("You saved "+model.getProductQuantity()*(Long.valueOf(model.getProductPrice()) -Long.valueOf(model.getProductPrice())) +"on this order");
            }else {
                savedAmount.setText("You saved on this order");

            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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