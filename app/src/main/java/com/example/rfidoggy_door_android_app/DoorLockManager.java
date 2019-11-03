package com.example.rfidoggy_door_android_app;

import java.time.LocalTime;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DoorLockManager {

    private String minTime;
    private String maxTime;

    public String DoorLogic(int maxTemp, int minTemp, float currentTemp, String currentTime, String timeRange, String forceLockUnlock, String weatherStatus){

        String lockUnlock = "Unlock";
        getMinMaxTime(timeRange);

        LocalTime timeCurrent = LocalTime.parse(currentTime);
        LocalTime timeMin = LocalTime.parse(minTime);
        LocalTime timeMax = LocalTime.parse(maxTime);


        if(!(weatherStatus == null)){
            int weatherStatusSpace = weatherStatus.indexOf(" ");
            weatherStatus = weatherStatus.substring(0, weatherStatusSpace);
        }


        if (currentTemp > maxTemp || currentTemp < minTemp)
            lockUnlock = "Lock";
        else if (!(weatherStatus == null)) {
            if (!(weatherStatus.equals("Clear") || weatherStatus.equals("Cloudy")))
                lockUnlock = "Lock";
        }
        else if (timeCurrent.isAfter(timeMin) || timeCurrent.isBefore(timeMax))
            lockUnlock = "Lock";
        else if (!(forceLockUnlock == null)){
            if (forceLockUnlock.equals("LOCKED"))
                lockUnlock = "Lock";
            else if (forceLockUnlock.equals("UNLOCKED"))
                lockUnlock = "Unlock";
        }
        else
            lockUnlock = "Unlock";

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
