package com.example.rfidoggy_door_android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SetTemperature extends AppCompatActivity {

    String LAT = "37.9485";
    String LON = "-91.7715";
    String API = "b94f5f54afb9c5dfe83eb82f83e35460";

    private EditText setMaxTempTextBox;
    private EditText setMinTempTextBox;

    private TextView currentTempTextView;
    private TextView weatherStatusTextView;
    private String currentWeatherStatus;
    private String currentTemp;

    private String userSetMaxTemp;
    private String userSetMinTemp;

    private HashMap<String, String> weatherAPIData = new HashMap<>();

    Intent intent = new Intent(SetTemperature.this, MainActivity.class);
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_temperature);

        setMaxTempTextBox = findViewById(R.id.setMaxTempTextBoxID);
        setMinTempTextBox = findViewById(R.id.setMinTempTextBoxID);
        currentTempTextView = findViewById(R.id.temp);
        weatherStatusTextView = findViewById(R.id.status);


        setMinTempTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userSetMinTemp = setMinTempTextBox.getText().toString();
                weatherAPIData.put("minTemp", userSetMinTemp);
                bundle.putSerializable("weatherData", weatherAPIData);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
            }
        });

        setMaxTempTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userSetMaxTemp = setMaxTempTextBox.getText().toString();
                weatherAPIData.put("maxTemp", userSetMaxTemp);
                bundle.putSerializable("weatherData", weatherAPIData);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
            }
        });

        new weatherTask().execute();
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /* Showing the ProgressBar, Making the main design GONE */
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&units=imperial&appid=" + API);
            return response;
        }

        protected void onPostExecute(String response) {
            try {
                JSONObject jsonObj = new JSONObject(response);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                currentTemp = main.getString("temp") + "°F";

                currentWeatherStatus = weather.getString("main");

                //* Populating extracted data into our views *//*
                weatherStatusTextView.setText(currentWeatherStatus.toUpperCase());
                weatherAPIData.put("currentWeatherStatus",currentWeatherStatus);
                bundle.putSerializable("weatherData", weatherAPIData);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);

                currentTempTextView.setText(currentTemp);
                weatherAPIData.put("currentTemp",currentTemp);
                bundle.putSerializable("weatherData", weatherAPIData);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);

                //temp_minTxt.setText(tempMin);
                //temp_maxTxt.setText(tempMax);


                //* Views populated, Hiding the loader, Showing the main design *//*
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }
    }
}
