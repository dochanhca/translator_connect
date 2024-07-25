package com.example.translateconnector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.imoktranslator.R;
import com.imoktranslator.customview.OpenSansTextView;

import java.util.ArrayList;
import java.util.List;

public class SuggestionContactAdapter extends ArrayAdapter<String> {
    private int layoutItem;
    private List<String> items, tempItems, suggestions;
    private Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (String name : tempItems) {
                    if (name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(name);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<String> filterList = (ArrayList<String>) results.values;
            if (results.count > 0) {
                clear();
                for (String name : filterList) {
                    add(name);
                    notifyDataSetChanged();
                }
            }
        }
    };

    public SuggestionContactAdapter(@NonNull Context context, int layoutItem, @NonNull List<String> items) {
        super(context, layoutItem, items);
        this.layoutItem = layoutItem;
        this.items = items;
        this.tempItems = new ArrayList<>(items);
        this.suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(layoutItem, parent, false);
        OpenSansTextView username = convertView.findViewById(R.id.user_name);
        username.setText(items.get(position));
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }
}
