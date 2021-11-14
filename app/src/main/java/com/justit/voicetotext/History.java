package com.justit.voicetotext;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class History extends Fragment {
    View view;
    ListView listView;
    ImageView empty;
    String[] text, date;
    int[] id;
    SQLiteDatabase sqLiteDatabase;
    private Database database;

    public History() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history, container, false);
        listView = view.findViewById(R.id.listviewid);
        database = new Database(getContext());
        empty = view.findViewById(R.id.empty);
        displayData();

        return view;
    }

    private void displayData() {

        sqLiteDatabase = database.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select *from info", null);
        if (cursor.getCount() > 0) {
            id = new int[cursor.getCount()];
            text = new String[cursor.getCount()];
            date = new String[cursor.getCount()];

            int i = 0;
            while (cursor.moveToNext()) {
                id[i] = cursor.getInt(0);
                text[i] = cursor.getString(2);
                date[i] = cursor.getString(3);
                i++;
            }
            Custom adapter = new Custom();
            listView.setAdapter(adapter);

        } else if (cursor.getCount() == 0) {
            empty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Empty", Toast.LENGTH_LONG).show();
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.textview_id);
                final String test = textView.getText().toString();
                copytoClip(test);
                return true;
            }
        });
    }

    private void copytoClip(String text) {
        ClipboardManager clipBoard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Data", text);
        clipBoard.setPrimaryClip(clip);

        Toast.makeText(getContext(), "Copied", Toast.LENGTH_SHORT).show();
    }

    private class Custom extends BaseAdapter {

        @Override
        public int getCount() {
            return text.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView delete, edit, share;
            TextView textView1, textView;
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sample_view, parent, false);
            delete = convertView.findViewById(R.id.delete);
            share = convertView.findViewById(R.id.share);
            edit = convertView.findViewById(R.id.edit);
            textView = convertView.findViewById(R.id.textview_id);
            textView1 = convertView.findViewById(R.id.textview_date);
            textView.setText(text[position]);
            textView1.setText(date[position]);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(getActivity())
                            .setMessage("Are you sure you want to delete?")
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0, int arg1) {
                                    sqLiteDatabase = database.getWritableDatabase();
                                    long recd = sqLiteDatabase.delete("info", "id=" + id[position], null);
                                    if (recd != 1) {
                                        Toast.makeText(getContext(), "Record deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    displayData();
                                }
                            }).create().show();
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), UpdateData.class);
                    intent.putExtra("id", id[position]);
                    intent.putExtra("text", text[position]);
                    intent.putExtra("date", date[position]);
                    startActivity(intent);
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = text[position];
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));

                }
            });

            return convertView;
        }
    }
}