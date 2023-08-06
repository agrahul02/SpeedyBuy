package com.speedybuy.speedybuy;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class DeliveryActivity extends AppCompatActivity {

    public static List<CartItemModel> cartItemModelList;
    private RecyclerView deliveryRecyclerView;
    public static CartAdapter cartAdapter;
    private Button changeOrAddNewAddressBtn;
    public static final int SELECT_ADDRESS = 0;
    private TextView totalAmount;
    private TextView fullname;
    private String name, mobileNo;
    private TextView fullAddress;
    private TextView pincode;
    private Button continueBtn;
    public static Dialog loadingDialog;
    private Dialog paymentMethodDialog;
    private TextView codTitle;
    private View divider;
    private ImageButton paytm, cod;
    private String paymentMethod = "PAYTM";
    private ConstraintLayout orderConfirmationLayout;
    private ImageButton continueShippingBtn;
    private TextView orderId;
    private boolean successResponse = false;
    public static boolean fromCart;
    private String order_id;

    public static boolean codOrderConfirm = false;
    public static boolean getQtyIDs = true;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        deliveryRecyclerView = findViewById(R.id.delivery_recyclerview);
        changeOrAddNewAddressBtn = findViewById(R.id.change_or_add_address_btn);
        totalAmount = findViewById(R.id.total_cart_amount);
        fullname = findViewById(R.id.fullname_shipping);
        fullAddress = findViewById(R.id.address_shipping);
        pincode = findViewById(R.id.pin_code_shiping);
        continueBtn = findViewById(R.id.cart_continue_btn);
        orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);
        continueShippingBtn = findViewById(R.id.continue_shopping_btn);
        orderId = findViewById(R.id.order_id);

        //////// loadingDialog
        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ///////// loadingDialog

        //////// paymentDialog
        paymentMethodDialog = new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paytm = paymentMethodDialog.findViewById(R.id.paytm);
        cod = paymentMethodDialog.findViewById(R.id.cod_btn);
        codTitle = paymentMethodDialog.findViewById(R.id.cod_btn_title);
        divider = paymentMethodDialog.findViewById(R.id.divider_payment);

        ///////// paymentDialog

        firebaseFirestore = FirebaseFirestore.getInstance();
        getQtyIDs = true;


        order_id = UUID.randomUUID().toString().substring(0, 28);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRecyclerView.setLayoutManager(layoutManager);


        cartAdapter = new CartAdapter(cartItemModelList, totalAmount, false);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        changeOrAddNewAddressBtn.setVisibility(View.VISIBLE);
        changeOrAddNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQtyIDs = false;
                Intent myAddressesIntent = new Intent(DeliveryActivity.this, MyAddressActivity.class);
                myAddressesIntent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean allProductAvailable = true;
                for (CartItemModel cartItemModel : cartItemModelList) {
                    if (cartItemModel.isQtyError()) {
                        allProductAvailable = false;
                        break;
                    }
                    if (cartItemModel.getType() == CartItemModel.CART_ITEM) {
                        if (!cartItemModel.isCOD()) {
                            cod.setEnabled(false);
                            cod.setAlpha(0.5f);
                            codTitle.setAlpha(0.5f);
                            divider.setVisibility(View.GONE);
                            break;
                        } else {
                            cod.setEnabled(true);
                            cod.setAlpha(1f);
                            codTitle.setAlpha(1f);
                            divider.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if (allProductAvailable) {
                    paymentMethodDialog.show();
                }

            }
        });

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "COD";
                // placeOrderDetails();
                verify();
            }
        });

        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "PAYTM";
                placeOrderDetails();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        ///////// access quntity ////////////////
        if (getQtyIDs) {
            loadingDialog.show();
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {

                for (int y = 0; y < cartItemModelList.get(x).getProductQuantity(); y++) {
                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);

                    Map<String, Object> timestamp = new HashMap<String, Object>();
                    timestamp.put("time", FieldValue.serverTimestamp());
                    int finalX = x;
                    int finalY = y;
                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        cartItemModelList.get(finalX).getQtyIDs().add(quantityDocumentName);

                                        if (finalY + 1 == cartItemModelList.get(finalX).getProductQuantity()) {

                                            firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID()).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(cartItemModelList.get(finalX).getStockQuantity()).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                List<String> serverQuantity = new ArrayList<String>();

                                                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                    serverQuantity.add(queryDocumentSnapshot.getId());
                                                                }
                                                                long availableQty = 0;
                                                                boolean noLongerAvailable = true;
                                                                for (String qtyId : cartItemModelList.get(finalX).getQtyIDs()) {
                                                                    cartItemModelList.get(finalX).setQtyError(false);
                                                                    if (!serverQuantity.contains(qtyId)) {

                                                                        if (noLongerAvailable) {
                                                                            cartItemModelList.get(finalX).setInStock(false);
                                                                        } else {
                                                                            cartItemModelList.get(finalX).setQtyError(true);
                                                                            cartItemModelList.get(finalX).setMaxQuantity(availableQty);
                                                                            Toast.makeText(DeliveryActivity.this, "all product not ", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                    } else {
                                                                        availableQty++;
                                                                        noLongerAvailable = false;
                                                                    }
                                                                }
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });

                                        }
                                    } else {
                                        loadingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                loadingDialog.dismiss();
            }
            loadingDialog.dismiss();
        } else {
            getQtyIDs = true;
            loadingDialog.dismiss();
        }
        ///////// access quntity ////////////////
        name = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        if (DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo().equals("")) {
            fullname.setText(name + " - " + mobileNo);
        }else {
            fullname.setText(name + " - " + mobileNo + "or" +DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo());

        }
        String flatNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFlateNo();
        String locality = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landmark = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        String city = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String state = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getState();

        if (landmark.equals("")){
            fullAddress.setText(flatNo +" "+ locality +" "+ city +" "+ state);
        }else{
            fullAddress.setText(flatNo +" "+ locality +" "+ landmark +" "+ city +" "+ state);
        }


        pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPincode());

        if (codOrderConfirm) {
            showConfirmationLayout();
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();

        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                if (!successResponse) {
                    for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if (qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size() - 1))) {
                                            cartItemModelList.get(finalX).getQtyIDs().clear();
                                        }
                                    }
                                });
                    }
                } else {
                    cartItemModelList.get(x).getQtyIDs().clear();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (successResponse) {
            finish();
            return;
        }
        super.onBackPressed();
    }

    private void showConfirmationLayout() {
        successResponse = true;
        codOrderConfirm = false;
        getQtyIDs = false;

        for (int x = 0; x < cartItemModelList.size() - 1; x++) {

            for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID).update("user_ID", FirebaseAuth.getInstance().getUid());
            }

        }

        if (MainActivity.mainActivity != null) {
            MainActivity.mainActivity.finish();
            MainActivity.mainActivity = null;
            MainActivity.showCart = false;
        } else {
            MainActivity.resetMainActivity = true;
        }
        if (ProductDetailsActivity.productDetailsActivity != null) {
            ProductDetailsActivity.productDetailsActivity.finish();
            ProductDetailsActivity.productDetailsActivity = null;
        }

        /////// sent conformations
        String SMS_API = "https://console.firebase.google.com/project/myspeedybuy-149bc/authentication/providers";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> body = new HashMap<>();

                return body;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        requestQueue.add(stringRequest);


        if (fromCart) {
            loadingDialog.show();
            Map<String, Object> updateCartList = new HashMap<>();
            long cartListSize = 0;
            final List<Integer> indexList = new ArrayList<>();
            for (int x = 0; x < DBqueries.cartList.size(); x++) {
                if (!cartItemModelList.get(x).isInStock()) {
                    updateCartList.put("product_ID_" + cartListSize, cartItemModelList.get(x).getProductID());
                    cartListSize++;
                } else {
                    indexList.add(x);
                }
            }
            updateCartList.put("list_size", cartListSize);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA").document("MY_CART")
                    .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                for (int x = 0; x < indexList.size(); x++) {
                                    DBqueries.cartList.remove(indexList.get(x).intValue());
                                    DBqueries.cartItemModelList.remove(indexList.get(x).intValue());
                                    DBqueries.cartItemModelList.remove(DBqueries.cartItemModelList.size() - 1);
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
        }
        continueBtn.setEnabled(false);
        changeOrAddNewAddressBtn.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        orderId.setText("Order ID" + order_id);
        orderConfirmationLayout.setVisibility(View.VISIBLE);

        continueShippingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void placeOrderDetails(){
        String userID = FirebaseAuth.getInstance().getUid();
        loadingDialog.show();
        for (CartItemModel cartItemModel : cartItemModelList) {
            if (cartItemModel.getType() == CartItemModel.CART_ITEM) {

                Map<String,Object> orderDetails = new HashMap<String,Object>();
                orderDetails.put("ORDER ID", order_id);
                orderDetails.put("Product Id", cartItemModel.getProductID());
                orderDetails.put("Product Image", cartItemModel.getProductImage());
                orderDetails.put("Product Title", cartItemModel.getProductTitle());
                orderDetails.put("User Id", userID);
                orderDetails.put("Product Quantity", cartItemModel.getProductQuantity());
                if (cartItemModel.getCuttedPrice() != null){
                    orderDetails.put("Cutted Price",cartItemModel.getCuttedPrice());
                }else {
                    orderDetails.put("Cutted Price","");
                }
                orderDetails.put("Product Price", cartItemModel.getProductPrice());
                if (cartItemModel.getSelectedCoupenId() != null){
                    orderDetails.put("Coupen id",cartItemModel.getSelectedCoupenId());
                }else {
                    orderDetails.put("Coupen id",cartItemModel.getSelectedCoupenId());
                }
                if (cartItemModel.getDiscountedPrice() != null){
                    orderDetails.put("Discount Price",cartItemModel.getDiscountedPrice());
                }else {
                    orderDetails.put("Discount Price",cartItemModel.getDiscountedPrice());
                }
                orderDetails.put("Ordered date", FieldValue.serverTimestamp());
                orderDetails.put("Packed date", FieldValue.serverTimestamp());
                orderDetails.put("Shipped date", FieldValue.serverTimestamp());
                orderDetails.put("Delivery date", FieldValue.serverTimestamp());
                orderDetails.put("Cancel date", FieldValue.serverTimestamp());
                orderDetails.put("Order status", "Ordered");
                orderDetails.put("Payment Method",paymentMethod);
                orderDetails.put("Address", fullAddress.getText());
                orderDetails.put("FullName", fullname.getText());
                orderDetails.put("Pincode", pincode.getText());
                orderDetails.put("Delivery Price",cartItemModelList.get(cartItemModelList.size() - 1).getDeliveryPrice());
                orderDetails.put("Cancellation requested",false);


                firebaseFirestore.collection("ORDERS").document(order_id).collection("orderItem").document(cartItemModel.getProductID())
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else {
                Map<String,Object> orderDetails = new HashMap<>();
                orderDetails.put("Total Items",cartItemModel.getTotalAmount());
                orderDetails.put("Total Items Price",cartItemModel.getTotalItemPrice());
                orderDetails.put("Delivery Price",cartItemModel.getDeliveryPrice());
                orderDetails.put("Total Amount",cartItemModel.getTotalAmount());
                orderDetails.put(" saved Amount",cartItemModel.getSavedAmount());
                orderDetails.put("Payment Status", "not paid");
                orderDetails.put("Order status", "Cancelled");
                firebaseFirestore.collection("ORDERS").document(order_id)
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    if (paymentMethod.equals("PAYTM")) {
                                        paytm();
                                    } else {
                                        cod();
                                    }
                                }else{
                                    String error = task.getException().getMessage();
                                    Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    private void paytm(){
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        loadingDialog.show();
        /*     if (ContextCompat.checkSelfPermission(DeliveryActivity.this, Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DeliveryActivity.this,new String[]{Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION,Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION},101);
                    RECEIVE_SMS  READ_SMS  DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION
                }*/
        final String M_id = "";
        final String customer_id = FirebaseAuth.getInstance().getUid();

        String url = "http://";
        final String callbackUrl = "http";
       /* if (x == cartItemModelList.size() -2){
            Map<String,Object> updateStatus = new HashMap<>();
            updateStatus.put("Payment Status", "not paid");
            updateStatus.put("Order status", "Cancelled");
            firebaseFirestore.collection("ORDERS").document(order_id).update(updateStatus)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                            }
                        }
                    });

        }
                RequestQueue requestQueue = volley.newRequestQueue(DeliveryActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
                   @Override
                   public void onResponse(String response) {

                       try {
                           JSONObject jsonObject = new JSONObject(response);
                           if (jsonObject.has("CHECKSUMHASH")){
                               String CHECKSUMHASH = jsonObject.getString("CHECKSUM");
                           }
                       }
                   }
                });*/
    }

    private void cod(){
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        Intent otpIntent = new Intent(DeliveryActivity.this,OTPverificationActivity.class);
        otpIntent.putExtra("mobileNo",mobileNo.substring(0,10));
        otpIntent.putExtra("OrderID",order_id);
        startActivity(otpIntent);
    }
    private void verify(){

        Intent otpIntent = new Intent(DeliveryActivity.this,OTPverificationActivity.class);

        startActivity(otpIntent);
    }
}