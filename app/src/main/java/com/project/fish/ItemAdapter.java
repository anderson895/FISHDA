package com.project.fish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends BaseAdapter {

    private List<ManageItems.itemData> dataList;
    private Context context;
    private OnItemClickListener itemClickListener;

    // Constructor to initialize the data, context, and click listener
    public ItemAdapter(Context context, List<ManageItems.itemData> dataList, OnItemClickListener itemClickListener) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.single_user, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Extract itemData object
        ManageItems.itemData itemData = dataList.get(position);

        // Set the text of the TextView to username or email
        holder.textView.setText(itemData.getItemName()); // Or getEmail() depending on your requirement

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
        TextView textView;
    }
}
