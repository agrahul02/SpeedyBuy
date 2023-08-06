package com.speedybuy.speedybuy;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OTPverificationActivity extends AppCompatActivity {

    private TextView phoneNo;
    private EditText otp;
    private Button verifyBtn;
    private String userNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        phoneNo = findViewById(R.id.phone_no);
        otp = findViewById(R.id.otp);
        verifyBtn = findViewById(R.id.verify_btn);
        userNo = getIntent().getStringExtra("mobileNo");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        phoneNo.setText("Verification code has been sent to +91" + userNo);

        Random random = new Random();
        int OTP_number = random.nextInt(999999 - 111111) + 111111;
        String SMS_API = "https://console.firebase.google.com/project/myspeedybuy-149bc/authentication/providers";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                verifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (otp.getText().toString().equals(String.valueOf(OTP_number))){

                                Map<String,Object> updateStatus = new HashMap<>();
                                updateStatus.put("Order status", "Ordered");
                                String OrderID = getIntent().getStringExtra("OrderID");
                                FirebaseFirestore.getInstance().collection("ORDERS").document(OrderID).update(updateStatus)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Map<String,Object> userOrder = new HashMap<>();
                                                    userOrder.put("order_id",OrderID);
                                                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(OrderID).set(userOrder)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        DeliveryActivity.codOrderConfirm = true;
                                                                        finish();
                                                                    }else {
                                                                        Toast.makeText(OTPverificationActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });



                                                }else {
                                                    Toast.makeText(OTPverificationActivity.this, "Order CANCELLED", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                        }else {
                            Toast.makeText(OTPverificationActivity.this, "incorrect OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast.makeText(OTPverificationActivity.this, "failed to send OTP", Toast.LENGTH_SHORT).show();
            }
        }){
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
                5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(OTPverificationActivity.this);
        requestQueue.add(stringRequest);

    }
}