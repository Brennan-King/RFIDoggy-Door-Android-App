package com.example.rfidoggy_door_android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * Main activity of the Android application.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Buttons for the UI screen that can be used to navigate the menus.
     */

    CurrentWeatherData currentWeatherData = new CurrentWeatherData();

    DoorLockManager manager = new DoorLockManager();

    private Button forceLockUnlockButton;
    private Button setTempButton;
    private Button setCurfewButton;

    private TextView curfewTimeTextView;
    private TextView currentTimeTextView;
    private TextView forceLockStatusTextView;
    private TextView maxTempTextView;
    private TextView minTempTextView;
    private TextView weatherStatusTextView;
    private TextView masterLockStatusTextView;

    private int maxTemp;
    private int minTemp;
    private float currentTemp;
    private String currentTime;
    private String timeRange = "";
    private String forceLockUnlock;
    private String currentWeatherStatus;
    private String weatherStatus;

    private String arduinoLockData = "";

    public final static int CURFEW_REQUEST_CODE = 2;
    public final static int LOCK_UNLOCK_REQUEST_CODE = 3;
    public final static int WEATHER_REQUEST_CODE = 4;



    private HashMap<String, String> weatherData;
    private HashMap<String, String> liveWeatherData;

    /**
     * Bluetooth objects used to establish a communication between device and Arduino.
     */
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private BluetoothCommunicationThread bluetoothCommunicationThread = null;

    /**
     * Used to check if LED it on or off.
     */
    private boolean ledFlag;

    /**
     * Used to handle messages sent between the device and Arduino.
     */
    public Handler messageHandler;

    /**
     * HC-05 bluetooth module properties.
     */
    public final static String BLUETOOTH_MODULE_MAC_ADDRESS = "98:D3:71:FD:89:7D";
    public final static int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final UUID HC05_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    /**
     * Main entry point for the application. Sets all UI elements and initiates bluetooth.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentWeatherData.startAsyncExecution();

        Log.i("[BLUETOOTH]", "Attempting to send data");

        forceLockStatusTextView = findViewById(R.id.forceLockStatusTextViewID);
        forceLockUnlockButton = findViewById(R.id.forceLockUnlockButtonID);
        forceLockUnlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForceLockUnlock();
            }
        });

        minTempTextView = findViewById(R.id.mintTempTextViewID);
        maxTempTextView = findViewById(R.id.maxTempTextViewID);
        setTempButton = findViewById(R.id.setTemperatureButtonID);
        setTempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSetTemperature();
            }
        });
        weatherStatusTextView = findViewById(R.id.weatherStatusTextViewID);

        setCurfewButton = findViewById(R.id.setCurfewButtonID);
        setCurfewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSetCurfew();
            }
        });

        curfewTimeTextView = findViewById(R.id.curfewTimeTextViewId);
        currentTimeTextView = findViewById(R.id.currentTimeId);

        masterLockStatusTextView = findViewById(R.id.masterLockStatusTextViewID);

        Thread timeThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted() ) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long date = System.currentTimeMillis();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                                String dateString = simpleDateFormat.format(date);
                                currentTimeTextView.setText(dateString);

                                currentTime = dateString;

                                arduinoLockData = manager.DoorLogic(maxTemp, minTemp, currentTemp, currentTime, timeRange, forceLockUnlock, weatherStatus);
                                masterLockStatusTextView.setText(arduinoLockData);

                                //Log.i("[BLUETOOTH]", "Attempting to send data");

                                if(bluetoothSocket.isConnected() && bluetoothCommunicationThread != null) {

                                    bluetoothCommunicationThread.writeOutCommunication(arduinoLockData.getBytes());
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Failed to connect to Arduino",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                catch(InterruptedException e) {

                }
            }
        };
        timeThread.start();

        Thread weatherThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted() ) {
                        Thread.sleep(5000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int degreeSym=0;
                                liveWeatherData = currentWeatherData.fetchCurrentWeatherData();

                                weatherStatus = liveWeatherData.get("currentWeatherStatus") + "  ,  " + liveWeatherData.get("currentTemp");
                                if(liveWeatherData.get("currentTemp") != null) {
                                    degreeSym = liveWeatherData.get("currentTemp").indexOf("°");
                                }
                                if(degreeSym != 0) {
                                    currentTemp = Float.valueOf(liveWeatherData.get("currentTemp").substring(0, degreeSym));
                                }
                            }
                        });
                    }
                }
                catch(InterruptedException e) {

                }
            }
        };
        weatherThread.start();


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        promptUserForBluetoothActivation(bluetoothAdapter);
    }

    /**
     * Opens force lock/unlock menu screen.
     */
    public void openForceLockUnlock(){
        Intent intent = new Intent(this, ForceLockUnlock.class);
        startActivityForResult(intent, LOCK_UNLOCK_REQUEST_CODE);
    }

    /**
     * Opens set temperature menu screen.
     */
    public void openSetTemperature(){
        Intent intent = new Intent(this, SetTemperature.class);
        startActivityForResult(intent, WEATHER_REQUEST_CODE);
    }

    /**
     * Opens set curfew menu screen.
     */
    public void openSetCurfew(){
        Intent intent = new Intent(this, SetCurfew.class);
        startActivityForResult(intent,CURFEW_REQUEST_CODE);
    }


    /**
     * Prompts the user to activate their devices bluetooth if it is not currently enabled.
     *
     * @param bluetoothAdapter The bluetooth adapter for the device.
     */
    public void promptUserForBluetoothActivation(BluetoothAdapter bluetoothAdapter) {
        if(!bluetoothAdapter.isEnabled()) {
            Intent promptUserToEnableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(promptUserToEnableBluetooth, REQUEST_ENABLE_BLUETOOTH);
        }
        else {
            startBluetoothOperation();
        }
    }

    @Override
    /**
     * Receives the confirmation of the user after they are prompted to enable bluetooth and then
     * enables bluetooth operation.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BLUETOOTH){
            startBluetoothOperation();
        }
        else if (resultCode == RESULT_OK && requestCode == CURFEW_REQUEST_CODE){
            Bundle bundle = data.getExtras();
            String curfew = bundle.getString("curfewTime");
            curfewTimeTextView.setText(curfew);
            timeRange = bundle.getString("curfewTime");
        }
        else if (resultCode == RESULT_OK && requestCode == LOCK_UNLOCK_REQUEST_CODE){
            Bundle bundle = data.getExtras();
            String lockStatus = bundle.getString("lockStatus");
            forceLockStatusTextView.setText(lockStatus);
            forceLockUnlock = bundle.getString("lockStatus");
        }
        else if (resultCode == RESULT_OK && requestCode == WEATHER_REQUEST_CODE){
            Bundle bundle = data.getExtras();
            weatherData = (HashMap<String, String>)bundle.getSerializable("weatherData");

            if(weatherData.get("minTemp") != null && weatherData.get("maxTemp") != null){
                minTempTextView.setText(weatherData.get("minTemp"));
                maxTempTextView.setText(weatherData.get("maxTemp"));
                minTemp = Integer.valueOf(weatherData.get("minTemp"));
                maxTemp = Integer.valueOf(weatherData.get("maxTemp"));
            }

            weatherStatusTextView.setText(weatherStatus);
        }

        arduinoLockData = manager.DoorLogic(maxTemp, minTemp, currentTemp, currentTime, timeRange, forceLockUnlock, weatherStatus);
        masterLockStatusTextView.setText(arduinoLockData);
    }

    /**
     * Starts bluetooth communication between device and Arduino bluetooth module. Also creates and
     * starts the thread used to handle concurrent message sending and receiving.
     */
    public void startBluetoothOperation() {
        if(bluetoothAdapter.isEnabled()) {
            BluetoothSocket temp;
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(BLUETOOTH_MODULE_MAC_ADDRESS);

            try {
                temp = bluetoothDevice.createRfcommSocketToServiceRecord(HC05_UUID);
                bluetoothSocket = temp;
                bluetoothSocket.connect();
                Log.i("[BLUETOOTH]","Connected to: " + bluetoothDevice.getName());
            }
            catch(IOException e) {
                try{bluetoothSocket.close();}catch(IOException c){return;}
            }

            Log.i("[BLUETOOTH]", "Creating handler");
            messageHandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message message) {
                    //Checks if message recieved is a response type
                    if(message.what == BluetoothCommunicationThread.getResponseMessage()) {
                        String txt = (String)message.obj;
                    }
                }
            };

            Log.i("[BLUETOOTH]", "Creating and running Thread");
            bluetoothCommunicationThread = new BluetoothCommunicationThread
                    (bluetoothSocket, messageHandler);
            bluetoothCommunicationThread.start();
        }
    }
}