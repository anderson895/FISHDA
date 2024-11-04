package com.project.fish;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ItemAdapterUser extends BaseAdapter {

    private List<ManageItems.itemData> dataList;
    private Context context;
    private OnItemClickListener itemClickListener;

    // Constructor to initialize the data, context, and click listener
    public ItemAdapterUser(Context context, List<ManageItems.itemData> dataList, OnItemClickListener itemClickListener) {
        this.context = context;
        this.dataList = dataList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.single_product, parent, false);
            holder = new ViewHolder();
            holder.imgView = convertView.findViewById(R.id.imgView);
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvQuantity = convertView.findViewById(R.id.tvQuantity);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.tvDescription = convertView.findViewById(R.id.tvDescription);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Extract itemData object
        ManageItems.itemData itemData = dataList.get(position);

        // Set the text of the TextView to username or email
        holder.tvName.setText("Fish : " + itemData.getItemName()); // Or getEmail() depending on your requirement
        holder.tvQuantity.setText("Quantity : " + itemData.getItemQuantity()); // Or getEmail() depending on your requirement
        holder.tvPrice.setText("Price : ₱" + itemData.getItemPrice()); // Or getEmail() depending on your requirement
        holder.tvDescription.setText("Description : " + itemData.getItemDescription()); // Or getEmail() depending on your requirement

        // Set OnClickListener to handle item clicks
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the clicked item position to the listener
                itemClickListener.onItemClick(position);
            }
        });

        return convertView;
    }

    // Interface to handle item clicks
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    static class ViewHolder {
        ImageView imgView;
        TextView tvName;
        TextView tvQuantity;
        TextView tvPrice;
        TextView tvDescription;
    }
}
