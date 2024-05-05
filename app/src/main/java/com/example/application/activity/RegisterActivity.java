package com.example.application.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.application.async.RegistrationRequestTask;
import com.example.application.exception.SERVER;
import com.example.application.exception.CLIENT;
import com.example.application.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout textInputLayoutPasswordReg, textInputLayoutRepeatPasswordReg,
            textInputLayoutTelephoneReg, textInputLayoutLoginReg;
    private Button buttonReg;
    private TextView textViewReg, textViewVillageReg;
    private TextInputEditText textInputEditTextLoginReg;
    private TextInputEditText textInputEditTextPasswordReg;
    private TextInputEditText textInputEditTextPasswordRepeatReg;
    private TextInputEditText textInputEditTextTelephoneReg;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registr);
        initCompoments();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> startAnimation(alphaAnimation), 500);
        buttonReg.setOnClickListener(v -> validate());
        textViewReg.setOnClickListener(v -> returnToLogin());
    }

    public void returnToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("isFirstRun", true);
        startActivity(intent);
        finish();
    }

    public void validate() {
        Editable phoneEdit = textInputEditTextTelephoneReg.getEditableText();
        Editable loginEdit = textInputEditTextLoginReg.getEditableText();
        Editable passwordEdit = textInputEditTextPasswordReg.getEditableText();
        Editable repeatPasswordEdit = textInputEditTextPasswordRepeatReg.getEditableText();

        String strPhone = phoneEdit.toString();
        if (phoneEdit.length() == 0 || loginEdit.length() == 0 || passwordEdit.length() == 0 || repeatPasswordEdit.length() == 0)
            Toast.makeText(RegisterActivity.this, CLIENT.FILL_FIELD.toString(), Toast.LENGTH_LONG).show();
        else if (phoneEdit.length() > 10 || loginEdit.length() > 16 || passwordEdit.length() > 32 || repeatPasswordEdit.length() > 32)
            Toast.makeText(RegisterActivity.this, CLIENT.LONG_VALUE.toString(), Toast.LENGTH_LONG).show();
        else if (!passwordEdit.toString().equals(repeatPasswordEdit.toString()))
            Toast.makeText(RegisterActivity.this, CLIENT.PASSWORD_MISMATCH.toString(), Toast.LENGTH_LONG).show();
        else if (!Utils.isNumber(strPhone))
            Toast.makeText(RegisterActivity.this, CLIENT.INVALID_PHONE_NUMBER.toString(), Toast.LENGTH_LONG).show();
        else {
            Long phone = Long.parseLong("8".concat(strPhone));
            String login = loginEdit.toString();
            String password = passwordEdit.toString();
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User u = new User(phone, login, password, task.getResult());
                    registationRequest(u);
                }
            });
        }
    }

    public void registationRequest(User user) {
        this.user = user;
        try {
            RegistrationRequestTask task = new RegistrationRequestTask(user);
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
                            onFailure(call, new Throwable(SERVER.USER_ALREADY_ACCESS.toString()));
                    } else
                        onFailure(call, new Throwable(SERVER.NOT_ACCESS.toString()));
                }

                @Override
                public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
                    Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openHomeActivity() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("phone", user.getPhone());
        editor.putString("login", user.getLogin());
        editor.putString("password", user.getPassword());
        editor.putString("token", user.getToken());
        editor.apply();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
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

    private void initCompoments() {
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