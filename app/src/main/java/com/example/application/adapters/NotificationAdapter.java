package com.example.application.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.application.R;
import com.example.application.data.IncidentMap;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends ArrayAdapter<IncidentMap> {
    private final Context context;
    private final List <IncidentMap> data;

    public NotificationAdapter(Context context, List<IncidentMap> data) {
        super(context, R.layout.item_alarm, data);
        this.context = context;
        this.data = data;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder")
        View view = inflater.inflate(R.layout.item_alarm, parent, false);
        TextView description = view.findViewById(R.id.textDescription);
        TextView localTime = view.findViewById(R.id.textLocalTime);
        description.setText(data.get(position).getDescription());
        localTime.setText(data.get(position).getEventTime());
        return view;
    }
}
