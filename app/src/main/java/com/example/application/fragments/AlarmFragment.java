package com.example.application.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.application.R;
import com.example.application.adapters.NotificationAdapter;
import com.example.application.async.ClearIncidentsRequestTask;
import com.example.application.async.GetNotificationRequestTask;
import com.example.application.data.UserData;
import com.example.application.exception.SERVER;
import com.example.application.model.Incident;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlarmFragment extends Fragment {
    private View inflatedView = null;
    private ListView listView;
    private TextView clear;
    private TextView noNotification;
    private ProgressBar progressBarNotification;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AlarmFragment() {
    }

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
        initListener();
        initNotifications();
        return inflatedView;
    }

    private void initNotifications() {
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
                    if (list != null) {
                        progressBarNotification.setVisibility(View.INVISIBLE);
                        noNotification.setVisibility(View.INVISIBLE);
                        sort(list);
                        if (getActivity() != null) {
                            NotificationAdapter notificationAdapter = new NotificationAdapter(getActivity(), list);
                            listView.setAdapter(notificationAdapter);
                        }
                    } else
                        onFailure(call, new Throwable(SERVER.NOT_ACCESS_NOTIFICATION.toString()));
                }

                @Override
                public void onFailure(@NotNull Call<List<Incident>> call, @NotNull Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void sort(List<Incident> list) {
        list.sort((incident1, incident2) -> {
            if (incident1.getTime().before(incident2.getTime()))
                return 1;
            else if (incident1.getTime().after(incident2.getTime()))
                return -1;
            else
                return 0;
        });
    }

    private void initListener() {
        clear.setOnClickListener(v -> {
            SharedPreferences preferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            long phone = preferences.getLong("phone", 0);
            String login = preferences.getString("login", null);
            UserData data = new UserData(phone, login);
            ClearIncidentsRequestTask task = new ClearIncidentsRequestTask(data);
            task.execute();
            try {
                Call <Integer> call = task.get();
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                        listView.setAdapter(null);
                        noNotification.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {}
                });
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private void init() {
        clear = inflatedView.findViewById(R.id.clear);
        listView = inflatedView.findViewById(R.id.listIncident);
        noNotification = inflatedView.findViewById(R.id.noNotification);
        progressBarNotification = inflatedView.findViewById(R.id.progressBarNotification);
    }
}
