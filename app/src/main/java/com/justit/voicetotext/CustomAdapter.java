package com.justit.voicetotext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomAdapter extends ArrayAdapter<String> {

    String[] names;
    int[] images;
    Context context;

    public CustomAdapter(Context context, String[] names, int[] images) {
        super(context, R.layout.spinner_items, names);
        this.names = names;
        this.images = images;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           View view = inflater.inflate(R.layout.spinner_items, null);
            TextView country = view.findViewById(R.id.spinnertextviewid);
            ImageView flags = view.findViewById(R.id.spinnerimageid);

            country.setText(names[position]);
            flags.setImageResource(images[position]);

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.spinner_items, null);
        TextView country = view.findViewById(R.id.spinnertextviewid);
        ImageView flags = view.findViewById(R.id.spinnerimageid);

        country.setText(names[position]);
        flags.setImageResource(images[position]);

        return view;

    }
}
