package com.example.application.activity;

import android.animation.*;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.application.R;
import com.example.application.Utils;
import com.example.application.exception.VALIDATE_FIELD;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView textViewVillage;
    private TextView textViewEnter;
    private Button button;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutTelephone;
    private TextView textViewRegister;
    private TextInputEditText textInputEditTextTelephoneLogin, textInputEditTextPasswordLogin;

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
            Handler handler = new Handler(Looper.getMainLooper());
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
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> startAnimation(alphaAnimation), 500);
        }
        button.setOnClickListener(v -> openHomeActivity());
        textViewRegister.setOnClickListener(v -> openRegisterActivity());
    }

    public void openHomeActivity() {
        Editable editPhone = textInputEditTextTelephoneLogin.getEditableText();
        Editable editPassword = textInputEditTextPasswordLogin.getEditableText();

        if (editPhone.length() == 0 || editPassword.length() == 0)
            Toast.makeText(LoginActivity.this, VALIDATE_FIELD.FILL_FIELD.toString(), Toast.LENGTH_LONG).show();
        else if (editPhone.length() > 10 ||
                editPassword.length() > 32)
            Toast.makeText(LoginActivity.this, VALIDATE_FIELD.LONG_VALUE.toString(), Toast.LENGTH_LONG).show();
        else if (editPhone.length() != 10)
            Toast.makeText(LoginActivity.this, VALIDATE_FIELD.INVALID_PHONE_NUMBER.toString(), Toast.LENGTH_LONG).show();
        else {
            String str = editPhone.toString();
            if (Utils.isNumber(str)) {
                Long phone = Long.parseLong(str);
                String password = textInputEditTextPasswordLogin.getEditableText().toString();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
            else
                Toast.makeText(LoginActivity.this, VALIDATE_FIELD.INVALID_PHONE_NUMBER.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void startAnimation(AlphaAnimation alphaAnimation) {
        button.startAnimation(alphaAnimation);
        textViewVillage.startAnimation(alphaAnimation);
        textViewEnter.startAnimation(alphaAnimation);
        textInputLayoutPassword.startAnimation(alphaAnimation);
        textInputLayoutTelephone.startAnimation(alphaAnimation);
        textViewRegister.startAnimation(alphaAnimation);
    }

    private void initViews() {
        imageView = findViewById(R.id.imageViewLogin);
        textViewVillage = findViewById(R.id.textViewVillageLogin);
        textViewEnter = findViewById(R.id.textViewEnterLogin);
        button = findViewById(R.id.buttonLogin);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPasswordLogin);
        textInputLayoutTelephone = findViewById(R.id.textInputLayoutTelephoneLogin);
        textViewRegister = findViewById(R.id.textViewRegistrLogin);
        textInputEditTextTelephoneLogin = findViewById(R.id.textInputEditTextTelephoneLogin);
        textInputEditTextPasswordLogin = findViewById(R.id.textInputEditTextPasswordLogin);
    }

    private boolean isNotFirstRun() {
        Bundle argument = getIntent().getExtras();
        if (argument != null)
            return argument.getBoolean("isFirstRun");
        return false;
    }
}