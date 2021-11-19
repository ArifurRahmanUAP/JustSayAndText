package com.justit.voicetotext;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.work.OneTimeWorkRequest;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class History extends Fragment {
    View view;
    ListView listView;
    LinearLayout showlinearLayout;
    ImageView empty, editDone;
    int editDonePosition;
    String[] text, date, color;
    int[] id;
    SQLiteDatabase sqLiteDatabase;
    private Database database;
    Spinner searchSpinner;
    int position;
    String s;
    EditText showData;
    TextView showDate;
    JustSay justSay;
    public AdView mAdView;

    String[] history = {"Filter History by Category", "Voice to Text", "Translated Text", "Image to Text"};


    public History(JustSay justSay) {
        this.justSay = justSay;
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
        searchSpinner = view.findViewById(R.id.searchSpinner);
        showlinearLayout = view.findViewById(R.id.showlinearLayout);
        showData = view.findViewById(R.id.showData);
        showDate = view.findViewById(R.id.showDate);
        editDone = view.findViewById(R.id.editDone);
        mAdView = view.findViewById(R.id.adViewHistory);


        ArrayAdapter fromAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, history);
        fromAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        searchSpinner.setAdapter(fromAdapter);

        MobileAds.initialize(getActivity());
        AdRequest adRequest = new AdRequest.Builder().build();

        AdView adView = new AdView(getActivity());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView.loadAd(adRequest);

        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                position = i;
//                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.RED);
//                ((TextView) adapterView.getChildAt(1)).setTextColor(Color.RED);
//                ((TextView) adapterView.getChildAt(2)).setTextColor(Color.GREEN);
//                ((TextView) adapterView.getChildAt(3)).setTextColor(Color.BLUE);

                if (i == 0) {
                    displayData("0");
                } else {
                    s = String.valueOf(position);
                    displayData(s);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }


    private void copytoClip(String text) {
        ClipboardManager clipBoard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Data", text);
        clipBoard.setPrimaryClip(clip);

        Toast.makeText(getContext(), "Copied", Toast.LENGTH_SHORT).show();
    }

    public void displayData(String z) {


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.textview_id);
                final String test = textView.getText().toString();
                copytoClip(test);


                return true;
            }
        });

        sqLiteDatabase = database.getReadableDatabase();
        Cursor cursor;
        if (z.equals("0"))
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM info ORDER BY id DESC", null);
        else
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM info WHERE type='" + z + "'" + " ORDER BY id DESC", null);
        Log.d("pod", cursor.toString());


        if (cursor.getCount() > 0) {
            listView.setVisibility(View.VISIBLE);
            empty.setVisibility(view.GONE);
            id = new int[cursor.getCount()];
            color = new String[cursor.getCount()];
            text = new String[cursor.getCount()];
            date = new String[cursor.getCount()];

            int i = 0;
            while (cursor.moveToNext()) {
                id[i] = cursor.getInt(0);
                color[i] = cursor.getString(1);
                text[i] = cursor.getString(3);
                date[i] = cursor.getString(4);
                i++;
            }
            CustomAdapter adapter = new CustomAdapter();
            listView.setAdapter(adapter);

            empty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

        } else if (cursor.getCount() == 0) {
            empty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Empty", Toast.LENGTH_LONG).show();
        }
    }

//    public void displayData() {
//
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView textView = (TextView) view.findViewById(R.id.textview_id);
//                final String test = textView.getText().toString();
//                copytoClip(test);
//                return true;
//            }
//        });
//
//        sqLiteDatabase = database.getReadableDatabase();
//        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM info ORDER BY id DESC", null);
//
//        if (cursor.getCount() > 0) {
//            id = new int[cursor.getCount()];
//            color = new String[cursor.getCount()];
//            text = new String[cursor.getCount()];
//            date = new String[cursor.getCount()];
//
//            int i = 0;
//            while (cursor.moveToNext()) {
//                id[i] = cursor.getInt(0);
//                color[i] = cursor.getString(1);
//                text[i] = cursor.getString(3);
//                date[i] = cursor.getString(4);
//                i++;
//            }
//            CustomAdapter adapter = new CustomAdapter();
//            listView.setAdapter(adapter);
//            empty.setVisibility(View.GONE);
//            listView.setVisibility(View.VISIBLE);
//
//        } else if (cursor.getCount() == 0) {
//            empty.setVisibility(View.VISIBLE);
//            listView.setVisibility(View.GONE);
//            Toast.makeText(getContext(), "Empty", Toast.LENGTH_LONG).show();
//        }
//    }

    private class CustomAdapter extends BaseAdapter {

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
            ImageView delete, edit, share, translate;
            TextView textView1, textView;
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sample_view, parent, false);
            delete = convertView.findViewById(R.id.delete);
            translate = convertView.findViewById(R.id.gtranslate);
            edit = convertView.findViewById(R.id.edit);
            share = convertView.findViewById(R.id.share);
            textView = convertView.findViewById(R.id.textview_id);
            textView1 = convertView.findViewById(R.id.textview_date);
            textView.setText(text[position]);
            textView1.setText(date[position]);
            textView.setTextColor(Color.parseColor(color[position]));
//            cardView.setBackgroundColor(Color.parseColor(color[position]));

            translate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String shareBody = text[position];
                    justSay.setHistoryText(shareBody);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, justSay).commit();

                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listView.setVisibility(View.GONE);
                    showlinearLayout.setVisibility(View.VISIBLE);
                    showDate.setText(date[position]);
                    showData.setText(text[position]);
                    editDonePosition = position;
                }
            });

            editDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(getActivity())
                            .setMessage("Are you sure you want to Update?")
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0, int arg1) {

                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(Database.DATE, showDate.getText().toString());
                                    contentValues.put(Database.DATA, showData.getText().toString());
                                    sqLiteDatabase = database.getWritableDatabase();

                                    long recid = sqLiteDatabase.update("info", contentValues, "id=?", new String[]{String.valueOf(id[editDonePosition])});

                                    if (recid != -1) {
                                        Toast.makeText(getActivity(), "Data update successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Something wrong try again", Toast.LENGTH_SHORT).show();
                                    }
                                    showlinearLayout.setVisibility(View.GONE);
                                    listView.setVisibility(View.VISIBLE);
                                    displayData("0");
                                }
                            }).create().show();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                                    displayData("0");
                                }
                            }).create().show();
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