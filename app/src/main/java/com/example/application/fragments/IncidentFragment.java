package com.example.application.fragments;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.example.application.activity.HomeActivity;
import com.example.application.R;
import com.example.application.adapters.ViewPagerUriAdapter;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.example.application.exception.CLIENT.*;

public class IncidentFragment extends Fragment {
    private static final int MAX_PHOTOS = 4;
    private View inflatedView = null;
    private ViewPager viewPage;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button redButton;
    private ArrayList<Uri> chooseImageList;
    private ImageView uploadImage;
    private TextView textView3;
    private CheckBox checkBox;
    private TextInputEditText editTextTextPersonName;
    private String mParam1;
    private String mParam2;

    public IncidentFragment() {
    }

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

    @SuppressLint({"WrongConstant", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_incident, container, false);
        init();
        redButton.setOnClickListener(v -> {
            if (uploadImage.getVisibility() != 0) {
                if (!editTextTextPersonName.getEditableText().toString().isEmpty()) {
                    if (checkBox.isChecked()) {
                        Intent intent = new Intent(this.getContext(), HomeActivity.class);
                        intent.putExtra("images", chooseImageList);
                        intent.putExtra("text", editTextTextPersonName.getEditableText().toString());
                        startActivity(intent);
                    } else
                        Toast.makeText(getContext(), SEND_DATA.toString(), Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(), FILL_FIELD.toString(), Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getContext(), MATERIAL_NOT_FOUND.toString(), Toast.LENGTH_LONG).show();
        });
        uploadImage.setOnClickListener(v -> openGallery());
        viewPage.setOnTouchListener(new View.OnTouchListener() {
            private static final float SENSITIVITY = 5;
            float initialX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN -> initialX = event.getX();
                    case MotionEvent.ACTION_UP -> {
                        float currentX = event.getX();
                        if (Math.abs(initialX - currentX) < SENSITIVITY) {
                            openGallery();
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        return inflatedView;
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        chooseImageList.clear();
        if (resultCode == RESULT_OK && requestCode == 1 && data != null) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                int count = Math.min(clipData.getItemCount(), MAX_PHOTOS);
                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    chooseImageList.add(uri);
                }
            } else {
                Uri uri = data.getData();
                chooseImageList.add(uri);
            }
            setAdapter();
            uploadImage.setVisibility(View.INVISIBLE);
        } else
            Toast.makeText(getContext(), MATERIAL_NOT_FOUND.toString(), Toast.LENGTH_LONG).show();
    }

    private void setAdapter() {
        ViewPagerUriAdapter adapter = new ViewPagerUriAdapter(this.getContext(), chooseImageList);
        viewPage.setAdapter(adapter);
    }

    public void init() {
        checkBox = inflatedView.findViewById(R.id.checkBox);
        textView3 = inflatedView.findViewById(R.id.textView3);
        redButton = inflatedView.findViewById(R.id.button);
        uploadImage = inflatedView.findViewById(R.id.imageUpload);
        viewPage = inflatedView.findViewById(R.id.viewPager);
        editTextTextPersonName = inflatedView.findViewById(R.id.editTextTextPersonName);
        chooseImageList = new ArrayList<>();
    }
}