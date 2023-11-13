package com.example.application;

import android.content.Intent;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;

public class RegistrActivity extends AppCompatActivity {
    private TextInputLayout textInputLayoutPasswordReg;
    private TextInputLayout textInputLayoutRepeatPasswordReg;
    private TextInputLayout textInputLayoutTelephoneReg;
    private TextInputLayout textInputLayoutLoginReg;
    private Button buttonReg;
    private TextView textViewReg;
    private TextView textViewVillageReg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registr);
        init();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        Handler handler = new Handler();
        handler.postDelayed(() -> startAnimation(alphaAnimation), 500);
        buttonReg.setOnClickListener(v -> openHomeActivity());
        textViewReg.setOnClickListener(v -> returnToLogin());
    }
    public void returnToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("isFirstRun", true);
        startActivity(intent);
    }

    public void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void startAnimation(AlphaAnimation alphaAnimation) {
        textInputLayoutPasswordReg.startAnimation(alphaAnimation);
        textInputLayoutRepeatPasswordReg.startAnimation(alphaAnimation);
        textInputLayoutTelephoneReg.startAnimation(alphaAnimation);
        textInputLayoutLoginReg.startAnimation(alphaAnimation);
        textViewVillageReg.startAnimation(alphaAnimation);
        buttonReg.startAnimation(alphaAnimation);
        textViewReg.startAnimation(alphaAnimation);
    }

    private void init () {
        textInputLayoutPasswordReg = findViewById(R.id.textInputLayoutPasswordReg);
        textInputLayoutRepeatPasswordReg = findViewById(R.id.textInputLayoutRepeatPasswordReg);
        textInputLayoutTelephoneReg = findViewById(R.id.textInputLayoutTelephoneReg);
        textInputLayoutLoginReg = findViewById(R.id.textInputLayoutLoginReg);
        textViewVillageReg = findViewById(R.id.textViewVillageReg);
        buttonReg = findViewById(R.id.buttonReg);
        textViewReg = findViewById(R.id.textViewReg);
    }
}