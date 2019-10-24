package com.example.rfidoggy_door_android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SetTemperature extends AppCompatActivity<String, Void, String> {

    String LAT = "37.9485";
    String LON = "91.7715";
    String API = "b94f5f54afb9c5dfe83eb82f83e35460";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_temperature);
    }

    protected String doInBackground(String... args) {
        String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&units=metric&appid=" + API);
        return response;
    }

    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONObject main = jsonObj.getJSONObject("main");
            JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

            String temp = main.getString("temp") + "°C";
            String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
            String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";

            String weatherDescription = weather.getString("main");


            /* Populating extracted data into our views */
            statusTxt.setText(weatherDescription.toUpperCase());
            tempTxt.setText(temp);
            temp_minTxt.setText(tempMin);
            temp_maxTxt.setText(tempMax);

            /* Views populated, Hiding the loader, Showing the main design */
            findViewById(R.id.loader).setVisibility(View.GONE);
            findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);


        } catch (JSONException e) {
            findViewById(R.id.loader).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.VISIBLE);
        }

    }
}
