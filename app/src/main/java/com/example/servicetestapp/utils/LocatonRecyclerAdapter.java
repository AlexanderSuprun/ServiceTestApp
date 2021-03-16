package com.example.servicetestapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicetestapp.R;

import java.util.ArrayList;

public class LocatonRecyclerAdapter extends RecyclerView.Adapter<LocatonRecyclerAdapter.ViewHolder> {

    private final ArrayList<LocationPoint> locationPoints;
    private final Context context;

    public LocatonRecyclerAdapter(ArrayList<LocationPoint> point, Context context) {
        this.locationPoints = point;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationPoint point = locationPoints.get(position);
        holder.textViewLongitude.setText(context.getString(R.string.value_longitude, point.getLongitude()));
        holder.textViewLatitude.setText(context.getString(R.string.value_latitude, point.getLatitude()));
    }

    @Override
    public int getItemCount() {
        return locationPoints.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textViewLatitude;
        AppCompatTextView textViewLongitude;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLatitude = itemView.findViewById(R.id.text_view_rv_item_latitude);
            textViewLongitude = itemView.findViewById(R.id.text_view_rv_item_longitude);
        }
    }
}
