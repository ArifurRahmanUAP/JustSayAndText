package com.justit.voicetotext;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    ImageView appInfo;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.toolbar);
        getSupportActionBar().setElevation(0);
        View view = getSupportActionBar().getCustomView();
        appInfo = view.findViewById(R.id.appinfo);

        appInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
            }
        });
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new JustSay()).commit();
    }

    Fragment selectedFragment = null;
    JustSay justSay = new JustSay();
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_justSay:
                    selectedFragment = justSay;

                    break;

                case R.id.nav_imageToText:
//                    selectedFragment = new ImageToText();
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(MainActivity.this);
                    break;

                case R.id.nav_History:
                    selectedFragment = new History(justSay);
                    break;
            }

            if (item.getItemId() != R.id.nav_imageToText)
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    getTextFromImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getTextFromImage(Bitmap bitmap) {

        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
            ImageToText imageToText = new ImageToText();
            imageToText.setImageText(stringBuilder.toString());

            Database db = new Database(this);
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a  MMM d, yyyy", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            ContentValues contentValues = new ContentValues();
            contentValues.put(Database.DATA, stringBuilder.toString());
            contentValues.put(Database.DATE, currentDateandTime);
            contentValues.put(Database.TYPE, "3");
            contentValues.put(Database.COLOR, "#FFFFFF");
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();

            sqLiteDatabase.insert("info", null, contentValues);

            selectedFragment = imageToText;

            getSupportFragmentManager().beginTransaction().
                    replace(R.id.fragment_container, selectedFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to exit this application? ");
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                    finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
