package com.example.application.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.application.R;
import com.example.application.async.UpdateUserRequestTask;
import com.example.application.data.UserData;
import com.example.application.exception.CLIENT;
import com.example.application.exception.SERVER;
import com.google.android.material.textfield.TextInputEditText;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.concurrent.ExecutionException;

public class ProfileFragment extends Fragment {
    private TextView login;
    private TextView save;
    private TextInputEditText textInputEditTextLogin, textInputEditTextTelephone;
    private View inflatedView = null;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_profile, container, false);
        initComponents();
        initProfile();
        initListener();
        return inflatedView;
    }

    private void initListener() {
        save.setOnClickListener(v -> {
            SharedPreferences preferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            long phone = preferences.getLong("phone", 0);
            String login = preferences.getString("login", null);
            String password = preferences.getString("password", null);

            Editable loginEdit = textInputEditTextLogin.getEditableText();
            Editable phoneEdit = textInputEditTextTelephone.getEditableText();

            String strPhone = phoneEdit.toString();
            if (loginEdit.length() == 0 || phoneEdit.length() == 0)
                Toast.makeText(getActivity(), CLIENT.FILL_FIELD.toString(), Toast.LENGTH_LONG).show();
            else if (phoneEdit.length() > 10 || loginEdit.length() > 16)
                Toast.makeText(getActivity(), CLIENT.LONG_VALUE.toString(), Toast.LENGTH_LONG).show();
            else {
                Long phoneNew = Long.parseLong("8".concat(strPhone));
                String loginNew = loginEdit.toString();
                UserData userData = new UserData(phone, login);
                Toast.makeText(getActivity(), SERVER.SAVE.toString(), Toast.LENGTH_LONG).show();
                this.login.setText(login);
                UpdateUserRequestTask task = new UpdateUserRequestTask(login, phone, loginNew, phoneNew);
                task.execute();
            }
        });
    }

    private void initProfile() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        long phone = preferences.getLong("phone", 0);
        String login = preferences.getString("login", null);
        String password = preferences.getString("password", null);
        this.login.setText(login);
    }

    public void initComponents() {
        save = inflatedView.findViewById(R.id.save);
        login = inflatedView.findViewById(R.id.textViewEnterLogin);
        textInputEditTextLogin = inflatedView.findViewById(R.id.textInputEditTextLogin);
        textInputEditTextTelephone = inflatedView.findViewById(R.id.textInputEditTextTelephone);
    }
}