package com.example.application;

import android.content.Intent;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class RegistrActivity extends AppCompatActivity {
    private TextInputLayout textInputLayoutPasswordReg, textInputLayoutRepeatPasswordReg,
            textInputLayoutTelephoneReg, textInputLayoutLoginReg;
    private Button buttonReg;
    private TextView textViewReg, textViewVillageReg;
    private TextInputEditText textInputEditTextLoginReg, textInputEditTextPasswordReg,
            textInputEditTextPasswordRepeatReg, textInputEditTextTelephoneReg;

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
        Long phone = Long.parseLong("8".concat(textInputEditTextTelephoneReg.getEditableText().toString()));
        String login = textInputEditTextLoginReg.getEditableText().toString();
        String password = textInputEditTextPasswordReg.getEditableText().toString();
        String repeatPassword = textInputEditTextPasswordRepeatReg.getEditableText().toString();
        if (login.equals("") || password.equals("") || repeatPassword.equals("")) {
            Toast.makeText(RegistrActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_LONG).show();
        } else {
            if (password.equals(repeatPassword)) {
                DbHelper dbHelper = new DbHelper(this);
                if (dbHelper.checkLogin(login)) {
                    Toast.makeText(RegistrActivity.this, "Пользователь уже существует", Toast.LENGTH_LONG).show();
                    return;
                }
                boolean registeredSuccess = dbHelper.insertData(phone, login, password);
                if (registeredSuccess) {
                    Toast.makeText(RegistrActivity.this, "Пользователь зарегистрирован", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(RegistrActivity.this, "Пользователь не зарегистрирован", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(RegistrActivity.this, "Пароли не совпадают", Toast.LENGTH_LONG).show();
            }
        }
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

    private void init() {
        textInputLayoutPasswordReg = findViewById(R.id.textInputLayoutPasswordReg);
        textInputLayoutRepeatPasswordReg = findViewById(R.id.textInputLayoutRepeatPasswordReg);
        textInputLayoutTelephoneReg = findViewById(R.id.textInputLayoutTelephoneReg);
        textInputLayoutLoginReg = findViewById(R.id.textInputLayoutLoginReg);
        textViewVillageReg = findViewById(R.id.textViewVillageReg);
        buttonReg = findViewById(R.id.buttonReg);
        textViewReg = findViewById(R.id.textViewReg);
        textInputEditTextLoginReg = findViewById(R.id.textInputEditTextLoginReg);
        textInputEditTextTelephoneReg = findViewById(R.id.textInputEditTextTelephoneReg);
        textInputEditTextPasswordReg = findViewById(R.id.textInputEditTextPasswordReg);
        textInputEditTextPasswordRepeatReg = findViewById(R.id.textInputEditTextPasswordRepeatReg);
    }
}