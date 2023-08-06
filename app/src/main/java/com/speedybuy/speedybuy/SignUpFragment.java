package com.speedybuy.speedybuy;

import static com.speedybuy.speedybuy.DBqueries.firebaseFirestore;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignUpFragment extends Fragment {


    public SignUpFragment() {
        // Required empty public constructor
    }

    private TextView alreadyHaveAnAccount;
    private FrameLayout parentFrameLayout;

    private FirebaseAuth mAuth;
    // variable for our text input
    // field for phone and OTP.
    private EditText edtPhone, edtOTP;
    // buttons for generating OTP and verifying OTP
    private Button verifyOTPBtn, generateOTPBtn;
    // string for storing our verification ID
    private String verificationId;

    private String phoneNumber;
    private EditText email;
    private EditText password;
    private TextView fullname;
    private TextView confirmPassword;

    private ImageButton closeBtn;
    private Button signUpBtn;
    private ProgressBar progressBar;
    private CollectionReference userCollection;
    private FirebaseAuth firebaseAuth;
    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    // private FirebaseAuth mAuth;
    public static boolean disableCloseBtn = false;
    public static final int CLIENT_TRANSIENT_ERROR = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        alreadyHaveAnAccount = view.findViewById(R.id.tv_already_have_an_account);
        parentFrameLayout = getActivity().findViewById(R.id.register_frame_layout);

        email = view.findViewById(R.id.sign_up_email);
        fullname = view.findViewById(R.id.sign_up_full_name);
        password = view.findViewById(R.id.sign_up_password);
        confirmPassword = view.findViewById(R.id.sign_up_confirm_password);

        closeBtn = view.findViewById(R.id.sign_up_close_btn);
        signUpBtn = view.findViewById(R.id.sign_up_btn);
        progressBar = view.findViewById(R.id.sign_up_progressbar);

        mAuth = FirebaseAuth.getInstance();

        // initializing variables for button and Edittext.
        edtPhone = view.findViewById(R.id.idEdtPhoneNumber);
        edtOTP =view.findViewById(R.id.idEdtOtp);
        verifyOTPBtn = view.findViewById(R.id.idBtnVerify);
        generateOTPBtn = view.findViewById(R.id.idBtnGetOtp);


        firebaseAuth = FirebaseAuth.getInstance();

        if (disableCloseBtn){
            closeBtn.setVisibility(View.GONE);
        }else {
            closeBtn.setVisibility(View.VISIBLE);
        }



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent helpIntent = new Intent(getActivity(), HelpActivity.class);
                startActivity(helpIntent);
            }
        });
        email.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fullname.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String emaill=email.getText().toString().trim();
                String uname=fullname.getText().toString().trim();
                phoneNumber = edtPhone.getText().toString().trim();
//                String mobileNumber=mobile.getText().toString().trim();
//                String pass=password.getText().toString().trim();
                checkEmailAndPassword(uname);
            }
        });

        generateOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edtPhone.getText().toString())) {
                    Toast.makeText(getActivity(), "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
                } else {
                    String phone = "+91" + edtPhone.getText().toString();
                    sendVerificationCode(phone);
                    checkEmailAndPassword(phone); // Pass the phone number to the method
                }
            }
        });


        verifyOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edtOTP.getText().toString())) {
                    Toast.makeText(getActivity(), "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    verifyCode(edtOTP.getText().toString());
                }
            }
        });
    }


    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Intent i = new Intent(getActivity(), RegisterActivity.class);
                            Toast.makeText(getContext(), "your number is verified", Toast.LENGTH_SHORT).show();
                            // startActivity();
                            //getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(getActivity())
                        .setCallbacks(mCallBack)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verificationId = s;
                }

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    final String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        edtOTP.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_form_left, R.anim.slide_form_right);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();

    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(email.getText())) {
            if (!TextUtils.isEmpty(fullname.getText())) {
                if (!TextUtils.isEmpty(password.getText()) && password.length() >= 8){
                    if (!TextUtils.isEmpty(confirmPassword.getText())){
                        signUpBtn.setEnabled(true);
                        signUpBtn.setTextColor(Color.rgb(255, 255, 255));
                    }else {
                        signUpBtn.setEnabled(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            signUpBtn.setTextColor(Color.argb(50,255,255,255));
                        }
                    }
                }else {
                    signUpBtn.setEnabled(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        signUpBtn.setTextColor(Color.argb(50,255,255,255));
                    }
                }
            }else {
                signUpBtn.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    signUpBtn.setTextColor(Color.argb(50,255,255,255));
                }
            }
        }else {
            signUpBtn.setEnabled(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                signUpBtn.setTextColor(Color.argb(50,255,255,255));
            }
        }
    }


    private void checkEmailAndPassword(String uname) {

        Drawable customErrorIcon = getResources().getDrawable(R.drawable.error_icon);
        customErrorIcon.setBounds(0,0,customErrorIcon.getIntrinsicWidth(),customErrorIcon.getIntrinsicHeight());

        if (email.getText().toString().matches(emailPattern)){
            if (password.getText().toString().equals(confirmPassword.getText().toString())){

                progressBar.setVisibility(View.VISIBLE);
                signUpBtn.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    signUpBtn.setTextColor(Color.argb(50,255,255,255));
                }

                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Map<String, Object> userdata = new HashMap<>();
                                    userdata.put("fullname", fullname.getText().toString());
                                    userdata.put("email", email.getText().toString());
                                    userdata.put("mobileNumber", phoneNumber);
                                    userdata.put("profile","");

                                    firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                            .set(userdata)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mAuth = FirebaseAuth.getInstance();

                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        String email = user.getEmail();
                                                        String uid = user.getUid();

                                                        HashMap<Object, String> hashMap = new HashMap<>();
                                                        hashMap.put("email", email);
                                                        hashMap.put("uid", uid);
                                                        hashMap.put("name", uname);
                                                        hashMap.put("onlineStatus", "online");
                                                        hashMap.put("typingTo", "noOne");
                                                        hashMap.put("image", "");
                                                        hashMap.put("mobileNumber", phoneNumber);


                                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                        DatabaseReference reference = database.getReference("Users");
                                                        reference.child(uid).setValue(hashMap);
                                                        Toast.makeText(getActivity(), "Registered User " + user.getEmail(), Toast.LENGTH_LONG).show();

                                                        mainIntent();
                                                        Intent maincIntent = new Intent(getActivity(), MainActivity.class);maincIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(maincIntent);
                                                        getActivity().finish();

                                                        CollectionReference userDataReference = firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                                                .collection("USER_DATA");

                                                        //// MAPS
                                                        Map<String, Object> wishlistMap = new HashMap<>();
                                                        wishlistMap.put("list_size", (long) 0);

                                                        Map<String, Object> ratingMap = new HashMap<>();
                                                        ratingMap.put("list_size", (long) 0);

                                                        Map<String, Object> cartMap = new HashMap<>();
                                                        cartMap.put("list_size", (long) 0);

                                                        Map<String, Object> myAddressesMap = new HashMap<>();
                                                        myAddressesMap.put("list_size", (long) 0);

                                                        Map<String, Object> notificationMap = new HashMap<>();
                                                        myAddressesMap.put("list_size", (long) 0);
                                                        //// MAPS

                                                        final List<String> documentNames = new ArrayList<>();
                                                        documentNames.add("MY_WISHLIST");
                                                        documentNames.add("MY_RATINGS");
                                                        documentNames.add("MY_CART");
                                                        documentNames.add("MY_ADDRESSES");
                                                        documentNames.add("MY_NOTIFICATIONS");

                                                        List<Map<String,Object>> documentFields = new ArrayList<>();
                                                        documentFields.add(wishlistMap);
                                                        documentFields.add(ratingMap);
                                                        documentFields.add(cartMap);
                                                        documentFields.add(myAddressesMap);
                                                        documentFields.add(notificationMap);

                                                        for (int x = 0; x < documentNames.size(); x++) {
                                                            int finalX = x;
                                                            userDataReference.document(documentNames.get(x))
                                                                    .set(documentFields.get(x)).addOnCompleteListener(new OnCompleteListener<Void>(){
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                if (finalX == documentNames.size()-1) {
                                                                                    //  Intent mainIntent = new Intent(getActivity(), MainActivity.class);

                                                                                }
                                                                            } else {
                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                signUpBtn.setEnabled(true);
                                                                                signUpBtn.setTextColor(Color.rgb(255, 255, 255));
                                                                                String error = task.getException().getMessage();
                                                                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        }

                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    signUpBtn.setEnabled(true);
                                    signUpBtn.setTextColor(Color.rgb(255, 255, 255));
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }else {
                confirmPassword.setError("password not match",customErrorIcon);
            }
        }else {
            email.setError("Invalid Email",customErrorIcon);
        }
    }

    private void mainIntent(){
        if (disableCloseBtn){
            disableCloseBtn = false;
        }else {
            Intent mainIntent = new Intent(getActivity(), MainActivity.class);
            startActivity(mainIntent);
        }
        getActivity().finish();
    }

}
