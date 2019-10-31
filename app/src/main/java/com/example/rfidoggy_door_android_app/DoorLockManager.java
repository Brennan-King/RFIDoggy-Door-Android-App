package com.example.rfidoggy_door_android_app;

public class DoorLockManager {

    private String minTime;
    private String maxTime;

    public String DoorLogic(int maxTemp, int minTemp, int currentTemp, String currentTime, String timeRange, String forceLockUnlock, String weatherStatus){
        String lockUnlock;
        getMinMaxTime(timeRange);

        int time = Integer.valueOf(currentTime);
        int maxTimeLogic = Integer.valueOf(maxTime);
        int minTimeLogic = Integer.valueOf(minTime);


        if (currentTemp > maxTemp || currentTemp < minTemp)
            lockUnlock = "Lock";
        else if (weatherStatus != "Clear" || weatherStatus != "Cloudy")
            lockUnlock = "Lock";
        else if (time > maxTimeLogic || time < minTimeLogic)
            lockUnlock = "Lock";
        else if (forceLockUnlock == "Unlock")
            lockUnlock = "Lock";
        else
            lockUnlock = "Unlock";

        return lockUnlock;
    }

    private void getMinMaxTime(String timeRange){
        String segments[] = timeRange.split("-");
        minTime = segments[segments.length-1];
        maxTime = segments[segments.length];
    }
}
