package com.allanlopez.veggie_os.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.allanlopez.veggie_os.R;
import com.allanlopez.veggie_os.pojo.Food;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;

public class CustomSuggestionAdapter extends SuggestionsAdapter<Food, CustomSuggestionAdapter.SuggestionHolder> {

    public CustomSuggestionAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public SuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.item_custom_suggestion, parent, false);
        return new SuggestionHolder(view);
    }

    @Override
    public void onBindSuggestionHolder(Food suggestion, SuggestionHolder holder, int position) {
        holder.title.setText(suggestion.food_name);
        holder.subtitle.setText(suggestion.serving_qty);
        holder.unit.setText(suggestion.serving_unit);
    }

    @Override
    public int getSingleViewHeight() {
        return 60;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                String term = constraint.toString();
                if(term.isEmpty())
                    suggestions = suggestions_clone;
                else {
                    suggestions = new ArrayList<>();
                    for (Food item: suggestions_clone)
                        if(item.food_name.toLowerCase().contains(term.toLowerCase()))
                            suggestions.add(item);
                }
                results.values = suggestions;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                suggestions = (ArrayList<Food>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    static class SuggestionHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView subtitle;
        protected TextView unit;

        public SuggestionHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.searchTitle);
            subtitle = (TextView) itemView.findViewById(R.id.searchQuantity);
            unit = (TextView) itemView.findViewById(R.id.searchUnit);
        }


    }
}
