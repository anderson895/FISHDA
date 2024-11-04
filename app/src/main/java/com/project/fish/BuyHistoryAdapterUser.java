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

public class BuyHistoryAdapterUser extends BaseAdapter {

    private List<ClassBuy> dataList;
    private Context context;
    private OnItemClickListener itemClickListener;

    // Constructor to initialize the data, context, and click listener
    public BuyHistoryAdapterUser(Context context, List<ClassBuy> dataList, OnItemClickListener itemClickListener) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.single_history, parent, false);
            holder = new ViewHolder();
            holder.imgView = convertView.findViewById(R.id.imgView);
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvQuantity = convertView.findViewById(R.id.tvQuantity);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.tvDescription = convertView.findViewById(R.id.tvDescription);
            holder.tvStatus = convertView.findViewById(R.id.tvStatus);



            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Extract itemData object
        ClassBuy itemData = dataList.get(position);

        // Set the text of the TextView to username or email
        holder.tvName.setText("Fish : " + itemData.getBuyName()); // Or getEmail() depending on your requirement
        holder.tvQuantity.setText("Buy : " + itemData.getBuyQuantity()); // Or getEmail() depending on your requirement
        holder.tvPrice.setText("Price : ₱" + itemData.getBuyPrice()); // Or getEmail() depending on your requirement
        holder.tvDescription.setText("Description : " + itemData.getBuyDescription()); // Or getEmail() depending on your requirement
        holder.tvStatus.setText("Status : " + itemData.getBuyStatus());
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
        TextView tvStatus;
    }
}
