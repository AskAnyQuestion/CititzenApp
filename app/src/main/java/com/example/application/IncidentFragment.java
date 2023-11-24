package com.example.application;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.content.Context.NOTIFICATION_SERVICE;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflatedView =  inflater.inflate(R.layout.fragment_incident, container, false);
        Button redButton = inflatedView.findViewById(R.id.button);
        redButton.setOnClickListener(v -> {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(inflater.getContext())
                            .setSmallIcon(R.drawable.bell)
                            .setContentTitle("Title")
                            .setContentText("Notification text");
            Notification notification = builder.build();
            NotificationManager notificationManager =
                    (NotificationManager) inflatedView.getContext().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        });
        return inflatedView;
    }
}