package com.flamingo.wikiquiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InfoboxListAdapter extends RecyclerView.Adapter<InfoboxListAdapter.InfoboxViewHolder> {


    class InfoboxViewHolder extends RecyclerView.ViewHolder {
        private final TextView infoboxItemView;

        private InfoboxViewHolder(@NonNull View itemView) {
            super(itemView);
            infoboxItemView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Infobox> _infoboxes; // Cached copy of words

    InfoboxListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public InfoboxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_infobox_item, parent, false);
        return new InfoboxViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InfoboxViewHolder holder, int position) {
        if (_infoboxes != null) {
            Infobox current = _infoboxes.get(position);
            holder.infoboxItemView.setText(current.getInfobox().getName()); // TODO
        } else {
            // Covers the case of data not being ready yet.
            holder.infoboxItemView.setText("No Word");
        }
    }

    void setInfoboxes(List<Infobox> infoboxes){
        _infoboxes = infoboxes;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // _infoboxes has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (_infoboxes != null)
            return _infoboxes.size();
        else return 0;
    }
}

