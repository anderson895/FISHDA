package com.project.fish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FishAdapter extends ArrayAdapter<FishItem> {

    public FishAdapter(Context context, List<FishItem> fishList) {
        super(context, 0, fishList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FishItem fishItem = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_fish, parent, false);
        }

        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.tv_name);
        TextView tvPrice = convertView.findViewById(R.id.tv_price);
        TextView tvDescription = convertView.findViewById(R.id.tv_description);
        TextView tvQuantity = convertView.findViewById(R.id.tv_quantity);

        // Populate the data into the template view using the data object
        if (fishItem != null) {
            tvName.setText(fishItem.getItemName());
            tvPrice.setText(String.valueOf(fishItem.getPrice()));
            tvDescription.setText(fishItem.getDescription());
            tvQuantity.setText(String.valueOf(fishItem.getQuantity()));
        }

        // Return the completed view to render on screen
        return convertView;
    }
}

