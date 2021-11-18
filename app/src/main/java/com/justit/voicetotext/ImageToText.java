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

        share = view.findViewById(R.id.shareid);
        textView = view.findViewById(R.id.textview);
        retake = view.findViewById(R.id.retake);
        db = new Database(getActivity());

        MobileAds.initialize(getActivity());
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = new AdView(getActivity());
        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId("ca-app-pub-7148413509095909/1143566527");
        adView.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
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

    String cropedText;

    public void setImageText(String theText) {
        cropedText = theText;
    }
}