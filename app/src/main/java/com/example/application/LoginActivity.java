package com.example.application;

import android.animation.*;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView textViewVillage;
    private TextView textViewEnter;
    private Button button;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutTelephone;
    private TextView textViewRegistr;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        if (!isNotFirstRun()) {
            ObjectAnimator moveAnimator = ObjectAnimator.ofFloat(
                    imageView,
                    "translationY", 0f, -900f);
            ObjectAnimator scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(
                    imageView,
                    PropertyValuesHolder.ofFloat("scaleX", 1f, 0.8f),
                    PropertyValuesHolder.ofFloat("scaleY", 1f, 0.8f)
            );
            @SuppressLint("Recycle") AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(moveAnimator, scaleAnimator);
            animatorSet.setDuration(300);
            Handler handler = new Handler();
            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
            alphaAnimation.setDuration(500);
            alphaAnimation.setFillAfter(true);
            handler.postDelayed(animatorSet::start, 1000);
            handler.postDelayed(() -> startAnimation(alphaAnimation), 1500);
        } else {
            imageView.setScaleX(0.8f);
            imageView.setScaleY(0.8f);
            imageView.setTranslationY(-900f);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
            alphaAnimation.setDuration(500);
            alphaAnimation.setFillAfter(true);
            Handler handler = new Handler();
            handler.postDelayed(() -> startAnimation(alphaAnimation), 500);
        }
        button.setOnClickListener(v -> openHomeActivity());
        textViewRegistr.setOnClickListener(v -> openRegistrActivity());
    }

    public void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void openRegistrActivity() {
        Intent intent = new Intent(this, RegistrActivity.class);
        startActivity(intent);
    }

    private void startAnimation(AlphaAnimation alphaAnimation) {
        button.startAnimation(alphaAnimation);
        textViewVillage.startAnimation(alphaAnimation);
        textViewEnter.startAnimation(alphaAnimation);
        textInputLayoutPassword.startAnimation(alphaAnimation);
        textInputLayoutTelephone.startAnimation(alphaAnimation);
        textViewRegistr.startAnimation(alphaAnimation);
    }

    private void initViews() {
        imageView = findViewById(R.id.imageViewLogin);
        textViewVillage = findViewById(R.id.textViewVillageLogin);
        textViewEnter = findViewById(R.id.textViewEnterLogin);
        button = findViewById(R.id.buttonLogin);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPasswordLogin);
        textInputLayoutTelephone = findViewById(R.id.textInputLayoutTelephoneLogin);
        textViewRegistr = findViewById(R.id.textViewRegistrLogin);
    }

    private boolean isNotFirstRun() {
        Bundle argument = getIntent().getExtras();
        if (argument != null)
            return argument.getBoolean("isFirstRun");
        else
            return false;
    }
}