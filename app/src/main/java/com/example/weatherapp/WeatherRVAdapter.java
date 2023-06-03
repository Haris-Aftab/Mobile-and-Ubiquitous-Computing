package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.weatherapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Adapters provide a binding from an app-specific data set to views that are displayed within a RecyclerView
 */
public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<WeatherRVModel> weatherRVModelArrayList;

    /**
     * Sets the recycler view content
     * @param context
     * @param weatherRVModelArrayList List of items
     */
    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
    }

    /**
     * Creates a view holder
     * @param parent
     * @param viewType
     * @return View holder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the data with the view holder for a specific position.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Gets data from specific position
        WeatherRVModel model = weatherRVModelArrayList.get(position);

        // Set temperature
        holder.temperatureTV.setText(model.getTemperature() + "°c");

        // Set description
        holder.descriptionTV.setText(model.getDescription());

        // Create date format variables
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("EEE hh:mm aa");

        try {
            Date t = input.parse(model.getTime());
            // Set time with correct format
            holder.timeTV.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get amount of recycler view items
     * @return Amount of recycler view items
     */
    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    /**
     * Contains the view information for displaying one list item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView descriptionTV;
        private final TextView temperatureTV;
        private final TextView timeTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionTV = itemView.findViewById(R.id.idTVDescription);
            temperatureTV = itemView.findViewById(R.id.idTVTemperature);
            timeTV = itemView.findViewById(R.id.idTVTime);
        }
    }
}
