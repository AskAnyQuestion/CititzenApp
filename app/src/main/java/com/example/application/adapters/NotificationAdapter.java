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
import com.example.application.model.Incident;

public class NotificationAdapter extends ArrayAdapter<Incident> {
    private final Context context;
    private final Incident[] data;

    public NotificationAdapter(Context context, Incident[] data) {
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
        description.setText(data[position].getDescription());
        localTime.setText(data[position].getDateTime());
        return view;
    }
}
