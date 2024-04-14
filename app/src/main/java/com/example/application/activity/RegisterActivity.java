package com.example.application.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.application.R;
import com.example.application.Utils;
import com.example.application.exception.VALIDATE_FIELD;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout textInputLayoutPasswordReg, textInputLayoutRepeatPasswordReg,
            textInputLayoutTelephoneReg, textInputLayoutLoginReg;
    private Button buttonReg;
    private TextView textViewReg, textViewVillageReg;
    private TextInputEditText textInputEditTextLoginReg;
    private TextInputEditText textInputEditTextPasswordReg;
    private TextInputEditText textInputEditTextPasswordRepeatReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registr);
        init();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> startAnimation(alphaAnimation), 500);
        buttonReg.setOnClickListener(v -> openHomeActivity());
        textViewReg.setOnClickListener(v -> returnToLogin());
    }

    public void returnToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("isFirstRun", true);
        startActivity(intent);
        finish();
    }

    public void openHomeActivity() {
        Editable phoneEdit = textInputEditTextLoginReg.getEditableText();
        Editable loginEdit  = textInputEditTextLoginReg.getEditableText();
        Editable passwordEdit  = textInputEditTextPasswordReg.getEditableText();
        Editable repeatPasswordEdit  = textInputEditTextPasswordRepeatReg.getEditableText();

        if (phoneEdit.length() == 0 || loginEdit.length() == 0 ||
                passwordEdit.length() == 0 || repeatPasswordEdit.length() == 0)
            Toast.makeText(RegisterActivity.this, VALIDATE_FIELD.FILL_FIELD.toString(), Toast.LENGTH_LONG).show();
        else if (phoneEdit.length() > 10 ||
                 loginEdit.length() > 16 ||
                 passwordEdit.length() > 32 ||
                 repeatPasswordEdit.length() > 32
        )
            Toast.makeText(RegisterActivity.this, VALIDATE_FIELD.LONG_VALUE.toString(), Toast.LENGTH_LONG).show();
        else if (!passwordEdit.toString().equals(repeatPasswordEdit.toString()))
            Toast.makeText(RegisterActivity.this, VALIDATE_FIELD.PASSWORD_MISMATCH.toString(), Toast.LENGTH_LONG).show();
        else {
            String str = phoneEdit.toString();
            if (Utils.isNumber(str)) {
                Long phone = Long.parseLong(str);
                String password = textInputEditTextPasswordReg.getEditableText().toString();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
            else
                Toast.makeText(RegisterActivity.this, VALIDATE_FIELD.INVALID_PHONE_NUMBER.toString(), Toast.LENGTH_LONG).show();
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
        TextInputEditText textInputEditTextTelephoneReg = findViewById(R.id.textInputEditTextTelephoneReg);
        textInputEditTextPasswordReg = findViewById(R.id.textInputEditTextPasswordReg);
        textInputEditTextPasswordRepeatReg = findViewById(R.id.textInputEditTextPasswordRepeatReg);
    }
}