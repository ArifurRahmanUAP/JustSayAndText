package com.justit.voicetotext;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ImageToText extends Fragment {

    private ImageView retake, share;
    private TextView textView;
    private static final int REQUEST_CAMERA_CODE = 100;
    Bitmap bitmap;
    Database db;
    private AdView mAdView;
    InterstitialAd mInterstitialAd;

    public ImageToText() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_texttoimage, container, false);


//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }

//        Toast.makeText(MainActivity.this,"Developed by Arifur Rahman",Toast.LENGTH_LONG).show();
        share = view.findViewById(R.id.shareid);
        textView = view.findViewById(R.id.textview);
//        copy = view.findViewById(R.id.copyid);
        retake = view.findViewById(R.id.retake);
//        history = view.findViewById(R.id.historyid);
        db = new Database(getActivity());

        MobileAds.initialize(getActivity());
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = new AdView(getActivity());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("cca-app-pub-7148413509095909/1143566527");
        mAdView = view.findViewById(R.id.adView);
        mAdView.loadAd(adRequest);
        textView.setText(this.cropedText);

        if (textView != null) {
            retake.setVisibility(View.VISIBLE);
            share.setVisibility(View.VISIBLE);
        }
//        InterstitialAd.load(this,"ca-app-pub-7148413509095909/5821178132", adRequest,
//                new InterstitialAdLoadCallback() {
//                    @Override
//                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                        // The mInterstitialAd reference will be null until
//                        // an ad is loaded.
//                        mInterstitialAd = interstitialAd;
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        // Handle the error
//                        mInterstitialAd = null;
//                    }
//                });

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ;
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
        }

        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mInterstitialAd != null) {
//                    mInterstitialAd.show(getActivity());
//                } else {
//                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
//                }
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(getActivity());
            }
        });

//        copy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String copy_text = textView.getText().toString();
//                copytoClip(copy_text);
//                ContentValues contentValues = new ContentValues();
//                contentValues.put("name", textView.getText().toString());
//                contentValues.put("date", currentDateandTime);
//                SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
//
//                Long recid = sqLiteDatabase.insert("info", null, contentValues);
//
//                if (recid != null) {
//
//                    Toast.makeText(getActivity(), "Data Coppied and saved", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    Toast.makeText(getActivity(), "Something is Wrong pls try again ", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String copy_text = textView.getText().toString();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = copy_text;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        return view;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
//        {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if(resultCode == RESULT_OK)
//            {
//                Uri resultUri = result.getUri();
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),resultUri);
//                    getTextFromImage(bitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }

    //    private  void getTextFromImage(Bitmap bitmap) {
//
//        TextRecognizer recognizer = new TextRecognizer.Builder(getActivity()).build();
//        if (!recognizer.isOperational()) {
//            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
//        } else {
//            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
//            StringBuilder stringBuilder = new StringBuilder();
//            for (int i = 0; i < textBlockSparseArray.size(); i++) {
//                TextBlock textBlock = textBlockSparseArray.valueAt(i);
//                stringBuilder.append(textBlock.getValue());
//                stringBuilder.append("\n");
//            }
//
//            String result = getArguments().getString("result");
//
//
//            textView.setText(result);
//            copy.setVisibility(View.VISIBLE);
//            retake.setVisibility(View.VISIBLE);
//            share.setVisibility(View.VISIBLE);
//        }
//    }

    String cropedText;

    public void setImageText(String theText) {
        cropedText = theText;
    }
}