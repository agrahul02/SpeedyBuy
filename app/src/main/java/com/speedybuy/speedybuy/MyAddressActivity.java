package com.speedybuy.speedybuy;

import static com.speedybuy.speedybuy.DeliveryActivity.SELECT_ADDRESS;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MyAddressActivity extends AppCompatActivity {

    private  int previousAddress;
    private LinearLayout addNewAddressBtn;
    private TextView addressesSaved;
    private RecyclerView myAddressesRecyclerView;
    private Button deliverHereBtn;
    private static AddressesAdapter addressesAdapter;
    private Dialog loadingDialog;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);


        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbars);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //////// loadingDialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                addressesSaved.setText(DBqueries.addressesModelList.size() +" saved addresses");
            }
        });
        ///////// loadingDialog


        previousAddress = DBqueries.selectedAddress;

        myAddressesRecyclerView = findViewById(R.id.address_recyclerview);
        deliverHereBtn = findViewById(R.id.deliver_here_btn);
        addNewAddressBtn = findViewById(R.id.add_new_address_btn);
        addressesSaved = findViewById(R.id.address_saved);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myAddressesRecyclerView.setLayoutManager(layoutManager);


        mode = getIntent().getIntExtra("MODE",-1);
        if (mode == SELECT_ADDRESS){
            deliverHereBtn.setVisibility(View.VISIBLE);
        }else{
            deliverHereBtn.setVisibility(View.GONE);
        }
        deliverHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DBqueries.selectedAddress != previousAddress){
                    final int previousAddressIndex = previousAddress;
                    loadingDialog.show();
                    Map<String,Object> updateSelection = new HashMap<>();
                    updateSelection.put("selected_"+ (previousAddress + 1),false);
                    updateSelection.put("selected_"+ (DBqueries.selectedAddress + 1),true);

                    previousAddress = DBqueries.selectedAddress;

                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                            .update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        finish();
                                    }else {
                                        previousAddress = previousAddressIndex;
                                        String error = task.getException().getMessage();
                                        Toast.makeText(MyAddressActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                    loadingDialog.dismiss();
                                }
                            });
                }else {
                    finish();
                }
            }
        });

        addressesAdapter = new AddressesAdapter(DBqueries.addressesModelList,mode,loadingDialog);
        myAddressesRecyclerView.setAdapter(addressesAdapter);
        ((SimpleItemAnimator)myAddressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressesAdapter.notifyDataSetChanged();

        addNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAddressIntent = new Intent(MyAddressActivity.this, AddAddressActivity.class);
                if (mode != SELECT_ADDRESS){
                    addAddressIntent.putExtra("INTENT", "manage");
                }else {
                    addAddressIntent.putExtra("INTENT", "null");
                }
                startActivity(addAddressIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        addressesSaved.setText(DBqueries.addressesModelList.size() +" saved addresses");
    }

    public static void refreshItem(int deselect, int select){
        addressesAdapter.notifyItemChanged(deselect);
        addressesAdapter.notifyItemChanged(select);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            if (mode == SELECT_ADDRESS) {
                if (DBqueries.selectedAddress != previousAddress) {
                    DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                    DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(true);
                    DBqueries.selectedAddress = previousAddress;
                }
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (mode == SELECT_ADDRESS) {
            if (DBqueries.selectedAddress != previousAddress) {
                DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(true);
                DBqueries.selectedAddress = previousAddress;
            }
        }
        super.onBackPressed();
    }
}