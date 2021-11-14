package com.justit.voicetotext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class UpdateData extends AppCompatActivity {
    Database database;
    SQLiteDatabase sqLiteDatabase;
    String text, date;
    ImageView edit;
    EditText textEdit;
    TextView dateEdit;
    private static int id = 0;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        textEdit = findViewById(R.id.textview_id1);
        edit = findViewById(R.id.edit1);
        database = new Database(UpdateData.this);
        dateEdit = findViewById(R.id.textview_date1);
        date = getIntent().getStringExtra("date");
        text = getIntent().getStringExtra("text");

        textEdit.setText(text);
        dateEdit.setText(date);


        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-7148413509095909/1143566527");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(UpdateData.this)
                        .setMessage("Are you sure you want to Update?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {

                                ContentValues contentValues = new ContentValues();
                                contentValues.put(Database.DATE, dateEdit.getText().toString());
                                contentValues.put(Database.DATA, textEdit.getText().toString());
                                Log.d("dateEdit", dateEdit.getText().toString());


                                sqLiteDatabase = database.getWritableDatabase();
                                long recid = sqLiteDatabase.update("info", contentValues, "data=?", new String[]{text});
                                if (recid != -1) {
                                    Toast.makeText(UpdateData.this, "Data update successfully", Toast.LENGTH_SHORT).show();

                                } else {

                                    Toast.makeText(UpdateData.this, "Something wrong try again", Toast.LENGTH_SHORT).show();
                                }

                                Intent i = new Intent(UpdateData.this, History.class);
                                startActivity(i);
                            }

                        }).create().show();
            }

        });
    }
}