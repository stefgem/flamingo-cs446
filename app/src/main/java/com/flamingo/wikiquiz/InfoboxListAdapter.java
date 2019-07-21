package com.flamingo.wikiquiz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.util.List;

public class InfoboxListAdapter extends RecyclerView.Adapter<InfoboxListAdapter.InfoboxViewHolder> {


    class InfoboxViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconImage;
        private final TextView nameTextView, categoryTextView, birthYearTextView;

        private InfoboxViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.db_display_image_view);
            nameTextView = itemView.findViewById(R.id.db_display_name_text_view);
            categoryTextView = itemView.findViewById(R.id.db_display_category_text_view);
            birthYearTextView = itemView.findViewById(R.id.db_display_birthyear_text_view);
        }
    }

    private final LayoutInflater mInflater;
    private List<Infobox> _infoboxes; // Cached copy of words

    InfoboxListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public InfoboxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.review_db_display_item, parent, false);
        return new InfoboxViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InfoboxViewHolder holder, int position) {
        if (_infoboxes != null) {
            Infobox current = _infoboxes.get(position);

            ByteArrayInputStream bis = new ByteArrayInputStream(current.getImageBlob());
            Bitmap bp = BitmapFactory.decodeStream(bis); //decode stream to a bitmap image
            holder.iconImage.setImageBitmap(bp);

            holder.nameTextView.setText(current.getInfobox().getName());
            holder.categoryTextView.setText(current.getInfobox().getCategory());
            holder.birthYearTextView.setText(String.valueOf(current.getInfobox().getBirthYear()));
        } else {
            // Covers the case of data not being ready yet.
            // could put placeholder data here.
        }
    }

    void setInfoboxes(List<Infobox> infoboxes) {
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

