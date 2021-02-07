package com.mr.mymr.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mr.mymr.R;
import com.mr.mymr.dto.ItemDTO;

import java.util.ArrayList;
import java.util.List;

public class ItemDropDownAdapter extends ArrayAdapter<ItemDTO> {

    Context mContext;
    List<ItemDTO> itemsDTOS;
    private int itemLayout;
    private List<ItemDTO> allItemsDTOS;
    private ListFilter listFilter = new ListFilter();

    private static final String TAG = "ItemDropDownAdapter";

    public ItemDropDownAdapter(@NonNull Context context, int resource, @NonNull List<ItemDTO> items) {
        super(context, resource, items);
        this.itemsDTOS = items;
        this.mContext = context;
        this.itemLayout = resource;
    }

    @Override
    public int getCount() {
        return itemsDTOS.size();
    }

    @Nullable
    @Override
    public ItemDTO getItem(int position) {
//        Log.d(TAG, itemsDTOS.get(position).toString());
        return itemsDTOS.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        }
        ItemDTO item = getItem(position);
        TextView view = convertView.findViewById(R.id.forAutoCompleteTxtView);
        view.setText(item.getItemDesc());
        view.setTag(item);

        return convertView;
//        return super.getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (allItemsDTOS == null) {
                synchronized (lock) {
                    allItemsDTOS = new ArrayList<>(itemsDTOS);
                }
            }
            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = allItemsDTOS;
                    results.count = allItemsDTOS.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();
                ArrayList<ItemDTO> matchValues = new ArrayList<ItemDTO>();

                for (ItemDTO itemDTO : allItemsDTOS) {
                    if (itemDTO.getItemDesc().toLowerCase().contains(searchStrLowerCase)) {
                        matchValues.add(itemDTO);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                itemsDTOS = (ArrayList<ItemDTO>)results.values;
            } else {
                itemsDTOS = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }
    }
}
