package com.cryptosasa;
/**
 * Created by Levit Nudi on 14/10/17.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.apptakk.http_request.HttpRequest;
import com.apptakk.http_request.HttpRequestTask;
import com.apptakk.http_request.HttpResponse;
import com.bumptech.glide.Glide;
import java.util.Random;

public class ConversionActivity extends AppCompatActivity {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    Context context = this;
    Button backBtn;
    private String CRYPTO_TYPE = "BTC";
    private int CRYPTO_POSITION =0;
    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor = null;
    EditText base_currency, quote_currency;
    public String BASE_CURRENCY = "BTC";
    public String QUOTE_CURRENCY = "USD";
    private String CURRENCY_SYMBOL = "";
    public String CRYPTO_URL = "https://min-api.cryptocompare.com/data/price?fsym="+BASE_CURRENCY+"&tsyms="+QUOTE_CURRENCY;
    Resources res;
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversion_form);

        base_currency = (EditText)findViewById(R.id.base_currency_conversion);
        quote_currency = (EditText)findViewById(R.id.quote_currency_conversion);

        sharedPref = getSharedPreferences(getString(R.string.shared_pref_crypto), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Bundle extras = getIntent().getExtras();

        res = getResources();
        String[] currArry = res.getStringArray(R.array.currency_array);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                handleShakeEvent(count);
            }

        });
        if (extras != null) {
            CRYPTO_POSITION = extras.getInt("crypto_position");
            if(Integer.parseInt(sharedPref.getString("List_"+CRYPTO_POSITION, null).split("#")[2])==(R.drawable.btc_logo)){
                CRYPTO_TYPE = "BTC";
                base_currency.setHint("1 "+CRYPTO_TYPE);
            }else if(Integer.parseInt(sharedPref.getString("List_"+CRYPTO_POSITION, null).split("#")[2])==(R.drawable.eth_logo)){
                CRYPTO_TYPE = "ETH";
                base_currency.setHint("1 "+CRYPTO_TYPE);
            }

            BASE_CURRENCY = CRYPTO_TYPE;
            CURRENCY_SYMBOL = sharedPref.getString("List_"+CRYPTO_POSITION, null).split("#")[1];

            for(int i=0; i<currArry.length; i++){

                if(currArry[i].split(",")[1].contains(CURRENCY_SYMBOL)){
                    QUOTE_CURRENCY = currArry[i].split(",")[0];
                }
            }

            CRYPTO_URL = "https://min-api.cryptocompare.com/data/price?fsym="+BASE_CURRENCY+"&tsyms="+QUOTE_CURRENCY;

            new HttpRequestTask(
                    new HttpRequest(CRYPTO_URL, HttpRequest.POST, "{ \"currency\": \"value\" }"),
                    new HttpRequest.Handler() {
                        @Override
                        public void response(HttpResponse response) {
                            if (response.code == 200) {
                                String result = response.body.replaceAll("\"", "")
                                        .replace("{", "").replace("}", "").split(":")[1];
                                if(base_currency.getText().length()==0) {
                                    quote_currency.setText(CURRENCY_SYMBOL + " " + result);
                                }else if(base_currency.getText().length()>0){
                                    float initialResult = Float.parseFloat(String.valueOf(result));
                                    float convertedResult = initialResult * Float.parseFloat(String.valueOf(base_currency.getText()));

                                    quote_currency.setText(CURRENCY_SYMBOL + " " + convertedResult);
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "error, check your internet connection!", Toast.LENGTH_LONG).show();
                                Log.e(this.getClass().toString(), "Request unsuccessful: " + response);
                            }
                        }
                    }).execute();
        }


        ImageView img_cover = (ImageView) findViewById(R.id.currency_img);

        try {
            if(CRYPTO_TYPE.contentEquals("ETH")) {
                Glide.with(this).load(R.drawable.eth_txt).into(img_cover);
            }else  if(CRYPTO_TYPE.contentEquals("BTC")){
                Glide.with(this).load(R.drawable.btc_txt).into(img_cover);
            }

        } catch (Exception e) {}

        //add editTextListener to base_currency textView to detect changes and do conversions
        base_currency.addTextChangedListener(new EditTextListener());
        backBtn = (Button)findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });
    }

    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
    public void handleShakeEvent(int count) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("Trade Tip Of the Day !");
        String[] message = {"Trade on LiteCoin with stoploss of x BTC","What goes down – does not necessarily go up again","Choose Two or Three Liquid Shares.","Determine Entry and Target Prices.","Book Your Profits when Target is reached"};
        Random rand = new Random();
        int max = 5;
        int min = 0;
        int ran = min + rand.nextInt(max);
        alertDialogBuilder.setMessage(message[ran]);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Got It", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }





    /*
    Implement editTextListener to convert values as they are typed
     */

    private class EditTextListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //filter result string to get values as float
            CRYPTO_URL = "https://min-api.cryptocompare.com/data/price?fsym="+BASE_CURRENCY+"&tsyms="+base_currency.getText().toString()+","+QUOTE_CURRENCY;
            new HttpRequestTask(
                    new HttpRequest(CRYPTO_URL, HttpRequest.POST, "{ \"currency\": \"value\" }"),
                    new HttpRequest.Handler() {
                        @Override
                        public void response(HttpResponse response) {
                            if (response.code == 200) {
                                String result = response.body.replaceAll("\"", "")
                                        .replace("{", "").replace("}", "").split(":")[1];
                                if(base_currency.getText().length()==0) {
                                    quote_currency.setText(CURRENCY_SYMBOL + " " + result);
                                }else if(base_currency.getText().length()>0){
                                    float initialResult = Float.parseFloat(String.valueOf(result));
                                    float convertedResult = initialResult * Float.parseFloat(String.valueOf(base_currency.getText()));

                                    quote_currency.setText(CURRENCY_SYMBOL + " " + convertedResult);
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "error, check your internet connection!", Toast.LENGTH_LONG).show();
                                Log.e(this.getClass().toString(), "Request unsuccessful: " + response);
                            }
                        }
                    }).execute();

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
