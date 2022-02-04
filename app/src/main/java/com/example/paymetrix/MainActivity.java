package com.example.paymetrix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {
    TextView tv_gross, tv_netpay, tv_error;
    Toolbar toolbar;
    TextView tv_income_tx;
    public TextInputLayout tiet_basic, tiet_si, tiet_gpf;
    AutoCompleteTextView tiet_darate, tiet_hrarate;
    Button btn_calcuate;
    private InterstitialAd mInterstitialAd;
    private static final String TAG = "MainActivity";
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> arrayAdapter1;
    String[] DArate = {"27", "28", "29", "30", "31"};
    String[] HRArate = {"8","9","10","11","12","13","14","15"};
    private final String myEmail = "mailto:amandhaker191@gmail.com";

    int darate,hrarate, incomerate;
    double total_tax = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tiet_basic = findViewById(R.id.tiet_basic);
        tv_income_tx = findViewById(R.id.tv_income_tx);

        tiet_darate =(AutoCompleteTextView) findViewById(R.id.tiet_datrate);      //
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.darate, DArate);
        tiet_darate.setAdapter(arrayAdapter);
        tiet_darate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parant, View view, int position, long l) {
                darate = (int) parant.getItemIdAtPosition(position);
                darate = Integer.parseInt(DArate[darate]);
                System.out.println(darate);
            }
        });

        tiet_hrarate =(AutoCompleteTextView) findViewById(R.id.tiet_hrarate);
        arrayAdapter1 = new ArrayAdapter<String>(this,R.layout.hrarate, HRArate);
        tiet_hrarate.setAdapter(arrayAdapter1);
        tiet_hrarate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parant, View view, int position, long l) {
                hrarate = (int) parant.getItemIdAtPosition(position);
                hrarate = Integer.parseInt(HRArate[hrarate]);
                System.out.println(hrarate);
            }
        });
        tiet_si = findViewById(R.id.tiet_si);
        tiet_gpf = findViewById(R.id.tiet_gpf);
        tv_gross = findViewById(R.id.tv_gross);
        tv_netpay = findViewById(R.id.tv_net_pay);
        btn_calcuate = findViewById(R.id.btn_calculate);
        tv_error = findViewById(R.id.tv_error);
        toolbar = findViewById(R.id.toolbar);


        btn_calcuate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String basic = tiet_basic.getEditText().getText().toString();
                int darate = MainActivity.this.darate;
                int hrarate = MainActivity.this.hrarate;
                String sip = tiet_si.getEditText().getText().toString();
                String gpf = tiet_gpf.getEditText().getText().toString();
//                String income_tx = til_income_tx.getEditText().getText().toString();
//                int income = Integer.parseInt(income_tx);
                if (basic.equals("") || darate == 0 || hrarate == 0 || sip.equals("") || gpf.equals("")) {
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setTextColor(Color.RED);
                    tv_error.setText("Please Enter All fields");
                } else {
                    tv_error.setVisibility(View.GONE);
                    int intbasic = Integer.parseInt(basic);
                    int intdarate = darate;
                    int inthrarate = hrarate;
                    int npsrate = 10;
                    int intsip = Integer.parseInt(sip);
                    int intdpf = Integer.parseInt(gpf);


                    calculate(intbasic, intdarate, inthrarate, 10, intsip, intdpf);

                    MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
                        @Override
                        public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                            showad();
                        }
                    });
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(MainActivity.this);
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                }

            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Toast.makeText(MainActivity.this,item.getItemId(),Toast.LENGTH_SHORT).show();
                switch (item.getItemId()){
                    case R.id.about:
                        startActivity(new Intent(MainActivity.this,AppInfo.class));
                        return true;
                    case R.id.contact_us:
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(myEmail));
//                        intent.putExtra(Intent.EXTRA_SUBJECT,et_email_subject.getText().toString());
//                        intent.putExtra(Intent.EXTRA_TEXT,et_email_message.getText().toString());
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    private void showad() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    public void calculate(double basic, double DArate, double HRArate, double NPSrate, double SIP, double GPF) {
        double DA,HRA, gross;
        double NPS, Netpay;
        double total_tax = 0, incomerate = 0;
        DA = (basic / 100) * DArate;
        System.out.println("DA: " + DA);
        HRA = (basic / 100) * HRArate;
        System.out.println("HRA: " + HRA);
        System.out.println(basic + DA);
        NPS = ((basic + DA) / 100) * NPSrate;
        System.out.println("NPS: " + NPS);
        gross = basic + DA + HRA;
        System.out.println("gross: " + gross);
        tv_gross.setText("Gross: " + (int) gross);
        Netpay = gross - (SIP + GPF + NPS);
        System.out.println("NetPay: " + Netpay);
        tv_netpay.setText("Netpay: " + (int) Netpay);
        double val = Netpay * 12;
        System.out.println("val: " + val);
        System.out.println(Netpay);
        double val2 = 0;
        if (val <= 250000) {
            val2 = val - 0;
            incomerate = 0;
            total_tax = 0;
        }else if (val <= 300000) {
            val2 = val - 250000;
            incomerate = 5;
            total_tax = 0;
        }  else if (val <= 500000) {
            val2 = val - 30000;
            incomerate = 15000;
            total_tax = 5;
        } else if (val < 750000) {
            val2 = val - 500000;
            incomerate = 10;
            total_tax = 25000;
        } else if (val < 1000000) {
            val2 = val - 750000;
            incomerate = 15;
            total_tax = 75000;
            System.out.println("val: " + val);
            System.out.println("val2: " + val2);
        } else if (val < 1250000) {
            val2 = val - 1000000;
            incomerate = 20;
            total_tax = 150000;
        }else if (val < 1500000) {
            val2 = val - 1250000;
            incomerate = 25;
            total_tax = 250000;
        }else if (val > 1500000) {
            val2 = val - 1500000;
            incomerate = 30;
            total_tax = 375000;
        }
        System.out.println("total tax: " + total_tax);
        System.out.println("income rate: " + incomerate);
        total_tax = total_tax + ((val2 / 100) * incomerate);
        System.out.println("Total Tax: " + total_tax);
        tv_income_tx.setText("Total Income Tax: " + total_tax);
    }
}