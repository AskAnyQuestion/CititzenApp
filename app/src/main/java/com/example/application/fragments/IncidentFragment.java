package com.example.application.fragments;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.application.activity.HomeActivity;
import com.example.application.R;
import com.example.application.exception.VALIDATE_FIELD;
import com.google.android.material.textfield.TextInputEditText;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.application.exception.VALIDATE_FIELD.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IncidentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IncidentFragment extends Fragment {
    View inflatedView = null;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int GALLERY_REQ_CODE = 1000;
    private Button redButton, materialButton;
    private ImageView image;
    private TextView textView3;
    private CheckBox checkBox;
    private TextInputEditText editTextTextPersonName;
    private Uri uri;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IncidentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncidentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IncidentFragment newInstance(String param1, String param2) {
        IncidentFragment fragment = new IncidentFragment();
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

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflatedView =  inflater.inflate(R.layout.fragment_incident, container, false);
        init();
        redButton.setOnClickListener(v -> {
            if (materialButton.getVisibility() != 0) {
                if (!editTextTextPersonName.getEditableText().toString().isEmpty()) {
                    if (checkBox.isChecked()) {
                        Intent intent = new Intent(this.getContext(), HomeActivity.class);
                        intent.putExtra("imageUri", uri.toString());
                        intent.putExtra("text", editTextTextPersonName.getEditableText().toString());
                        startActivity(intent);
                    } else
                        Toast.makeText(this.getContext(), SEND_DATA.toString(), Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(this.getContext(), FILL_FIELD.toString(), Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(this.getContext(), MATERIAL_NOT_FOUND.toString(), Toast.LENGTH_LONG).show();
        });
        materialButton.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_PICK);
            gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQ_CODE);
        });
        return inflatedView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GALLERY_REQ_CODE) {
            assert data != null;
            this.uri = data.getData();
            image.setVisibility(View.VISIBLE);
            materialButton.setVisibility(View.INVISIBLE);
            textView3.setText("1. Материал загружен");
            image.setImageURI(uri);
        }
    }

    public void init() {
        checkBox = inflatedView.findViewById(R.id.checkBox);
        textView3 = inflatedView.findViewById(R.id.textView3);
        redButton = inflatedView.findViewById(R.id.button);
        materialButton = inflatedView.findViewById(R.id.button2);
        image = inflatedView.findViewById(R.id.image);
        editTextTextPersonName = inflatedView.findViewById(R.id.editTextTextPersonName);
    }
}