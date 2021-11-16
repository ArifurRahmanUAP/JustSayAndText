package com.justit.voicetotext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class JustSay extends Fragment {

    private static final int RESULT_OK = -1;
    InterstitialAd mInterstitialAd;
    private CheckBox checkBoxId;
    private Spinner fromSpinner, toSpinner;
    private TextInputEditText sourceEdt, translateTv;
    private ImageView mic, sourseTexeShare, translatedTexeShare;
    private MaterialButton translateBtn;
    String languageCode = "0";
    public AdView mAdView;
    private static final int REQUEST_PERMISSION_CODE = 0;
    int fromLanguageCode, toLanguageCode = 0;
    String voiceLanguageCode;

    String[] fromLanguages = {"English", "Bengali", "Hindi", "Urdu", "Philippine", "Afrikaans", "Arabic", "Korean", "Japanese",
            "Catalan", "Spanish", "Swedish", "Chinese", "Danish", "German", "Dutch", "Greek", "French", "Indonesian", "Italian"};

    String[] toLanguages = {"Bengali", "English", "Hindi", "Urdu", "Philippine", "Afrikaans", "Arabic", "Korean", "Japanese",
            "Catalan", "Spanish", "Swedish", "Chinese", "Danish", "German", "Dutch", "Greek", "French", "Indonesian", "Italian"};

    public JustSay() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_justsay, container, false);

//        Bundle bundle = this.getArguments();
//        String datas = bundle.getString("datas");
//        sourceEdt.setText(datas);

        MobileAds.initialize(requireContext());

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }

        //        Share
        sourseTexeShare = view.findViewById(R.id.sourseTexeShare);
        translatedTexeShare = view.findViewById(R.id.translatedTexeShare);

        //ADDVIEW

        MobileAds.initialize(getActivity());
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = new AdView(getActivity());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-4459566286777302/7966254460");
        mAdView = view.findViewById(R.id.adView);
        mAdView.loadAd(adRequest);

        fromSpinner = view.findViewById(R.id.idFromSpinner);
        toSpinner = view.findViewById(R.id.idToSpinner);
        sourceEdt = view.findViewById(R.id.idEdtsource);
        mic = view.findViewById(R.id.idMic);
        checkBoxId = view.findViewById(R.id.checkBoxId);
        translateBtn = view.findViewById(R.id.idBtnTranslate);
        translateTv = view.findViewById(R.id.idEdttranslated);

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode = getLanguageCode(fromLanguages[position], true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter fromAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, fromLanguages);
        fromAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode = getLanguageCode(toLanguages[position], false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sourseTexeShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = sourceEdt.getText().toString();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                if (checkBoxId.isChecked()) {
                    sourceEdt.setText("");
                }
            }
        });

        translatedTexeShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = translateTv.getText().toString();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                if (checkBoxId.isChecked()) {
                    translateTv.setText("");
                }
            }
        });

        ArrayAdapter toAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, toLanguages);
        toAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(getActivity());
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

                translateTv.setText("");
                if (sourceEdt.getText().toString().isEmpty()) {

                    Toast.makeText(getActivity(), "Please enter text to translate", Toast.LENGTH_LONG).show();
                } else if (fromLanguageCode == 0) {
                    Toast.makeText(getActivity(), "Please Select source language", Toast.LENGTH_LONG).show();
                } else if (toLanguageCode == 0) {
                    Toast.makeText(getActivity(), "Please Select the language to make translation", Toast.LENGTH_LONG).show();
                } else {
                    translateText(fromLanguageCode, toLanguageCode, sourceEdt.getText().toString());
                }
            }
        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "Just Say");
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, voiceLanguageCode);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 9000);
                i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 9000);
                try {
                    startActivityForResult(i, REQUEST_PERMISSION_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        translateTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String shareBody = translateTv.getText().toString();
                copytoClip(shareBody);
                return true;
            }
        });

        InterstitialAd.load(getActivity(), "ca-app-pub-4459566286777302/2399533752", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });
        return view;
    }

    private void copytoClip(String text) {
        ClipboardManager clipBoard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Data", text);
        clipBoard.setPrimaryClip(clip);
        Toast.makeText(getActivity(), "Copied", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            sourceEdt.setText(result.get(0));


            Database db = new Database(getContext());
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a  MMM d, yyyy", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            ContentValues contentValues = new ContentValues();
            contentValues.put(Database.DATA, result.get(0));
            contentValues.put(Database.DATE, currentDateandTime);
            contentValues.put(Database.COLOR, "#ff0000");
            contentValues.put(Database.TYPE, "1");
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            sqLiteDatabase.insert("info", null, contentValues);
        }
    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String source) {
        translateTv.setText("Translating..");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode).build();

        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translateTv.setText("Translating...");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translateTv.setText(s);
                        Database db = new Database(getContext());
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a  MMM d, yyyy", Locale.getDefault());
                        String currentDateandTime = sdf.format(new Date());
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Database.DATA, s);
                        contentValues.put(Database.DATE, currentDateandTime);
                        contentValues.put(Database.COLOR, "#00c800");
                        contentValues.put(Database.TYPE, "2");
                        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
                        sqLiteDatabase.insert("info", null, contentValues);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Fail to translate:" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Fail to download Language" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private int getLanguageCode(String language, boolean isFromSpinner) {

        int selectedLanguage = FirebaseTranslateLanguage.EN;
        switch (language) {
            case "English":
                languageCode = "en";
                selectedLanguage = FirebaseTranslateLanguage.EN;
                break;

            case "Bengali":
                languageCode = "bn";
                selectedLanguage = FirebaseTranslateLanguage.BN;
                break;

            case "Chinese":
                languageCode = "zh";
                selectedLanguage = FirebaseTranslateLanguage.ZH;
                break;

            case "Danish":
                languageCode = "da";
                selectedLanguage = FirebaseTranslateLanguage.DA;
                break;

            case "German":
                languageCode = "de";
                selectedLanguage = FirebaseTranslateLanguage.DE;
                break;

            case "Dutch":
                languageCode = "nl";
                selectedLanguage = FirebaseTranslateLanguage.NL;
                break;

            case "Greek":
                languageCode = "el";
                selectedLanguage = FirebaseTranslateLanguage.EL;
                break;

            case "French":
                languageCode = "fr";
                selectedLanguage = FirebaseTranslateLanguage.FR;
                break;

            case "Indonesian":
                languageCode = "id";
                selectedLanguage = FirebaseTranslateLanguage.ID;
                break;

            case "Italian":
                languageCode = "it";
                selectedLanguage = FirebaseTranslateLanguage.IT;
                break;

            case "Hindi":
                languageCode = "hi";
                selectedLanguage = FirebaseTranslateLanguage.HI;
                break;

            case "Belarusian":
                languageCode = "be";
                selectedLanguage = FirebaseTranslateLanguage.BE;
                break;

            case "Urdu":
                languageCode = "ur";
                selectedLanguage = FirebaseTranslateLanguage.UR;
                break;

            case "Afrikaans":
                languageCode = "af";
                selectedLanguage = FirebaseTranslateLanguage.AF;
                break;

            case "Arabic":
                languageCode = "ar";
                selectedLanguage = FirebaseTranslateLanguage.AR;
                break;

            case "Korean":
                languageCode = "ko";
                selectedLanguage = FirebaseTranslateLanguage.KO;
                break;

            case "Catalan":
                languageCode = "ca";
                selectedLanguage = FirebaseTranslateLanguage.CA;
                break;

            case "Spanish":
                languageCode = "es";
                selectedLanguage = FirebaseTranslateLanguage.ES;
                break;

            case "Japanese":
                languageCode = "ja";
                selectedLanguage = FirebaseTranslateLanguage.JA;
                break;

            case "Swedish":
                languageCode = "sv";
                selectedLanguage = FirebaseTranslateLanguage.SV;
                break;

            case "Philippine":
                languageCode = "fil";
                selectedLanguage = FirebaseTranslateLanguage.PL;
                break;
        }
        if (isFromSpinner)
            voiceLanguageCode = languageCode;
        return selectedLanguage;
    }
}