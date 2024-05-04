package com.example.application.activity;

import android.animation.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.application.R;
import com.example.application.Utils;
import com.example.application.async.AuthorizationRequestTask;
import com.example.application.exception.CLIENT;
import com.example.application.exception.SERVER;
import com.example.application.data.LoginData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private DisplayMetrics metrics;
    private ImageView imageView;
    private Button button;
    private TextView textViewVillage, textViewEnter, textViewRegister;
    private TextInputLayout textInputLayoutPassword, textInputLayoutTelephone;
    private TextInputEditText textInputEditTextTelephoneLogin, textInputEditTextPasswordLogin;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initProfile();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponents();
        initMetrix();
        if (!afterRegistration()) {
            ObjectAnimator moveAnimator = ObjectAnimator.ofFloat(
                    imageView,
                    "translationY", 0f, -metrics.heightPixels * 0.31f);
            ObjectAnimator scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(
                    imageView,
                    PropertyValuesHolder.ofFloat("scaleX", 1f, 0.75f),
                    PropertyValuesHolder.ofFloat("scaleY", 1f, 0.75f)
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
            imageView.setTranslationY(-metrics.heightPixels * 0.31f);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
            alphaAnimation.setDuration(500);
            alphaAnimation.setFillAfter(true);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> startAnimation(alphaAnimation), 500);
        }
        button.setOnClickListener(v -> validate());
        textViewRegister.setOnClickListener(v -> openRegisterActivity());
    }

    public void validate() {
        Editable phoneEdit = textInputEditTextTelephoneLogin.getEditableText();
        Editable passwordEdit = textInputEditTextPasswordLogin.getEditableText();

        String strPhone = phoneEdit.toString();
        if (phoneEdit.length() == 0 || passwordEdit.length() == 0)
            Toast.makeText(LoginActivity.this, CLIENT.FILL_FIELD.toString(), Toast.LENGTH_LONG).show();
        else if (phoneEdit.length() > 10 || passwordEdit.length() > 32)
            Toast.makeText(LoginActivity.this, CLIENT.LONG_VALUE.toString(), Toast.LENGTH_LONG).show();
        else if (phoneEdit.length() != 10)
            Toast.makeText(LoginActivity.this, CLIENT.INVALID_PHONE_NUMBER.toString(), Toast.LENGTH_LONG).show();
        else if (!Utils.isNumber(strPhone))
            Toast.makeText(LoginActivity.this, CLIENT.INVALID_PHONE_NUMBER.toString(), Toast.LENGTH_LONG).show();
        else {
            Long phone = Long.parseLong("8".concat(strPhone));
            String password = passwordEdit.toString();
            LoginData loginData = new LoginData(phone, password);
            try {
                AuthorizationRequestTask task = new AuthorizationRequestTask(loginData);
                task.execute();
                Call<Integer> call = task.get();
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                        Integer t = response.body();
                        if (t != null) {
                            if (t.equals(200))
                                openHomeActivity();
                            else
                                onFailure(call, new Throwable(SERVER.WRONG_COMBINATION.toString()));
                        } else
                            onFailure(call, new Throwable(SERVER.NOT_ACCESS.toString()));
                    }

                    @Override
                    public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean afterRegistration() {
        Bundle argument = getIntent().getExtras();
        if (argument != null)
            return argument.getBoolean("isFirstRun");
        return false;
    }

    private void initMetrix() {
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    private void initProfile() {
        SharedPreferences preferences = this.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        long phone = preferences.getLong("phone", 0);
        String login = preferences.getString("login", null);
        String password = preferences.getString("password", null);
        if (login != null || password != null || phone != 0)
            openHomeActivity();
    }

    public void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
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

    private void initComponents() {
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
}