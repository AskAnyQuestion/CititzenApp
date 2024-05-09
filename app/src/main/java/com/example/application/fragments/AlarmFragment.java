package com.example.application.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.application.R;
import com.example.application.activity.HomeActivity;
import com.example.application.adapters.NotificationAdapter;
import com.example.application.async.GetNotificationRequestTask;
import com.example.application.data.IncidentMap;
import com.example.application.data.LoginData;
import com.example.application.data.UserData;
import com.example.application.model.Incident;
import com.example.application.model.Notification;
import com.yandex.mapkit.geometry.Point;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
        SharedPreferences preferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        long phone = preferences.getLong("phone", 0);
        String login = preferences.getString("login", null);
        UserData data = new UserData(phone, login);
        GetNotificationRequestTask task = new GetNotificationRequestTask(data);
        task.execute();
        try {
            Call<List<Incident>> call = task.get();
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NotNull Call<List<Incident>> call, @NotNull Response<List<Incident>> response) {
                    List<Incident> list = response.body();
                    assert list != null;
                    sort(list);
                    NotificationAdapter notificationAdapter = new NotificationAdapter(getContext(), list);
                    listView.setAdapter(notificationAdapter);
                }

                @Override
                public void onFailure(@NotNull Call<List<Incident>> call, @NotNull Throwable t) {

                }
            });
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return inflatedView;
    }
    private void sort(List <Incident> list) {
        list.sort((incident1, incident2) -> {
            if (incident1.getTime().before(incident2.getTime()))
                return 1;
            else if (incident1.getTime().after(incident2.getTime()))
                return -1;
            else
                return 0;
        });
    }

    private void init() {
        listView = inflatedView.findViewById(R.id.listIncident);
    }
}
