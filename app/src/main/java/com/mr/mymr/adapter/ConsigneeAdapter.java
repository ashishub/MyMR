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
import com.mr.mymr.dto.CustomerDTO;

import java.util.ArrayList;
import java.util.List;

public class ConsigneeAdapter extends ArrayAdapter<CustomerDTO> {

    private static final String TAG = "ConsigneeAdapter";

    Context mContext;
    List<CustomerDTO> customerDTOS;
    private int itemLayout;
    private List<CustomerDTO> allCustomerDTOs;
    private ListFilter listFilter = new ListFilter();

    public ConsigneeAdapter(@NonNull Context context, int resource, @NonNull List<CustomerDTO> customers) {
        super(context, resource, customers);
        this.customerDTOS = customers;
        this.mContext = context;
        this.itemLayout = resource;
    }

    @Override
    public int getCount() {
        return customerDTOS.size();
    }

    @Nullable
    @Override
    public CustomerDTO getItem(int position) {
//        Log.d(TAG, customerDTOS.get(position).toString());
        return customerDTOS.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        }
        CustomerDTO customerDTO = getItem(position);
        TextView view = convertView.findViewById(R.id.forAutoCompleteTxtView);
        view.setText(customerDTO.getCustomer_name());
        view.setTag(customerDTO);

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    private class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (allCustomerDTOs == null) {
                synchronized (lock) {
                    allCustomerDTOs = new ArrayList<>(customerDTOS);
                }
            }
            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = allCustomerDTOs;
                    results.count = allCustomerDTOs.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();
                ArrayList<CustomerDTO> matchValues = new ArrayList<CustomerDTO>();

                for (CustomerDTO customerDTO : allCustomerDTOs) {
                    if (customerDTO.getCustomer_name().toLowerCase().contains(searchStrLowerCase)) {
                        matchValues.add(customerDTO);
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
                customerDTOS = (ArrayList<CustomerDTO>)results.values;
            } else {
                customerDTOS = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }
    }
}
