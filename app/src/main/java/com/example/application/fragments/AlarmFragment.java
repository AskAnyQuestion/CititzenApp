package com.example.application.fragments;

import android.os.Bundle;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.application.R;
import com.example.application.adapters.NotificationAdapter;
import com.example.application.map.IncidentMap;

public class AlarmFragment extends Fragment {

    private View inflatedView = null;
    private ListView listView;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AlarmFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlarmFragment.
     */
    public static AlarmFragment newInstance(String param1, String param2) {
        AlarmFragment fragment = new AlarmFragment();
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
        this.inflatedView = inflater.inflate(R.layout.fragment_alarm, container, false);
        init();
        // Fix
        // ListView
        IncidentMap[] dataArr = new IncidentMap[1];
        IncidentMap data = new IncidentMap();
        data.setDescription("Группа мигрантов устроила разбойное нападение");
        dataArr[0] = data;
        // Adapter
        NotificationAdapter notificationAdapter = new NotificationAdapter(getContext(), dataArr);
        listView.setAdapter(notificationAdapter);
        return inflatedView;
    }

    private void init() {
        listView = inflatedView.findViewById(R.id.listIncident);
    }
}
