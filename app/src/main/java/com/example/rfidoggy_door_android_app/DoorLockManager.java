package com.example.rfidoggy_door_android_app;

import java.time.LocalTime;


public class DoorLockManager {

    private String minTime;
    private String maxTime;
    private LocalTime timeCurrent;
    private LocalTime timeMin;
    private LocalTime timeMax;

    public String DoorLogic(int maxTemp, int minTemp, float currentTemp, String currentTime, String timeRange, String forceLockUnlock, String weatherStatus){

        String lockUnlock = "U";
        getMinMaxTime(timeRange);

        if(minTime != null && maxTime != null){
            timeCurrent = LocalTime.parse(currentTime);
            timeMin = LocalTime.parse(minTime);
            timeMax = LocalTime.parse(maxTime);
        }

        if(weatherStatus != null){
            int weatherStatusSpace = weatherStatus.indexOf(" ");
            weatherStatus = weatherStatus.substring(0, weatherStatusSpace);
        }

        if ((currentTemp > maxTemp || currentTemp < minTemp) && (minTemp != maxTemp))
            lockUnlock = "L";

        if (weatherStatus != null) {
            if (!(weatherStatus.equals("Clear") || weatherStatus.equals("Cloudy")))
                lockUnlock = "L";
        }

        if(timeMin != null && timeMax != null){
            if (timeCurrent.isAfter(timeMin) && timeCurrent.isBefore(timeMax))
                lockUnlock = "L";
        }

        if (forceLockUnlock != null){
            if (forceLockUnlock.equals("LOCKED"))
                lockUnlock = "L";
            else if (forceLockUnlock.equals("UNLOCKED"))
                lockUnlock = "U";
        }

        return lockUnlock;
    }

    private void getMinMaxTime(String timeRange){
        if(!(timeRange == "")){

            String segments[] = timeRange.split("-");
            minTime = segments[segments.length-2];
            maxTime = segments[segments.length-1];
        }
    }
}
