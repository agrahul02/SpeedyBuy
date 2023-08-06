package com.speedybuy.speedybuy;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordFragment extends Fragment {


    private EditText registeredEmail;
    private Button resetPasswordButton;
    private TextView goBack;
    private FrameLayout parentFrameLayout;
    private ViewGroup emailIconContainer;
    private ImageView emailIcon;
    private TextView emailIconText;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        registeredEmail = view.findViewById(R.id.forget_password_email);
        resetPasswordButton = view.findViewById(R.id.reset_password_btn);
        goBack = view.findViewById(R.id.tv_forget_password_go_back);

        emailIconContainer = view.findViewById(R.id.forget_password_email_icon_container);
        emailIcon = view.findViewById(R.id.forget_password_email_icon);
        emailIconText = view.findViewById(R.id.forget_password_email_icon_text);
        progressBar = view.findViewById(R.id.forget_password_progressbar);

        parentFrameLayout = getActivity().findViewById(R.id.register_frame_layout);

        firebaseAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registeredEmail.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();

            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIconText.setVisibility(View.GONE);

                TransitionManager.beginDelayedTransition(emailIconContainer);

                progressBar.setVisibility(View.VISIBLE);

                resetPasswordButton.setEnabled(false);
                resetPasswordButton.setTextColor(Color.argb(50, 255, 255, 255));

                firebaseAuth.sendPasswordResetEmail(registeredEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ScaleAnimation scaleAnimation = new ScaleAnimation(1,0,1,0,emailIcon.getWidth()/2,emailIcon.getHeight()/2);
                                    scaleAnimation.setDuration(100);
                                    scaleAnimation.setInterpolator(new LinearInterpolator());
                                    scaleAnimation.setRepeatMode(Animation.REVERSE);
                                    scaleAnimation.setRepeatCount(1);
                                    scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                            // This method will be called when the animation starts.
                                            // You could perform any required actions here.
                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            // This method will be called when the animation ends.
                                            // You could perform any required actions here, such as hiding the email icon container.
                                            emailIconText.setText("Recovery Email sent successfully check your inbox");
                                            emailIconText.setTextColor(getResources().getColor(R.color.green));

                                            TransitionManager.beginDelayedTransition(emailIconContainer);
                                            emailIconText.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                            // This method will be called when the animation repeats.
                                            // You could perform any required actions here.
                                            emailIcon.setImageResource(R.drawable.email);
                                            emailIcon.setVisibility(View.VISIBLE);
                                        }
                                    });

                                    emailIcon.startAnimation(scaleAnimation);

                                } else {
                                    String error = task.getException().getMessage();

                                    resetPasswordButton.setEnabled(true);
                                    resetPasswordButton.setTextColor(Color.rgb(255, 255, 255));

                                    emailIconText.setText(error);
                                    emailIconText.setTextColor(getResources().getColor(R.color.red));
                                    TransitionManager.beginDelayedTransition(emailIconContainer);
                                    emailIconText.setVisibility(View.VISIBLE);
                                }
                                progressBar.setVisibility(View.GONE);

                            }

                        });
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });
    }

    private void checkInput() {
        if (TextUtils.isEmpty(registeredEmail.getText())) {
            resetPasswordButton.setEnabled(false);
            resetPasswordButton.setTextColor(Color.argb(50, 255, 255, 255));
        } else {
            resetPasswordButton.setEnabled(true);
            resetPasswordButton.setTextColor(Color.rgb(255, 255, 255));
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_form_left, R.anim.slideout_form_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();

    }
}