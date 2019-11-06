package com.example.rfidoggy_door_android_app;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CurrentWeatherData {
    private String LAT = "37.9485";
    private String LON = "-91.7715";
    private String API = "b94f5f54afb9c5dfe83eb82f83e35460";

    private String currentTemp;
    private String currentWeatherStatus;

    private weatherTask weatherTaskAsync = new weatherTask();

    private HashMap<String, String> weatherAPIData = new HashMap<>();

    public void startAsyncExecution() {
        weatherTaskAsync.execute();
    }

    public HashMap<String, String> fetchCurrentWeatherData() {
        return weatherAPIData;
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

                weatherAPIData.put("currentTemp",currentTemp);
                weatherAPIData.put("currentWeatherStatus",currentWeatherStatus);
            }
            catch (JSONException e) {

            }
        }
    }
}
