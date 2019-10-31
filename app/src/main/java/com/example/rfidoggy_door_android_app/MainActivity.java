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
    private Button forceLockUnlockButton;
    private Button setTempButton;
    private Button setCurfewButton;
    private Button turnOnLEDButton;

    private TextView curfewTimeTextView;
    private TextView currentTimeTextView;
    private TextView lockStatusTextView;
    private TextView maxTempTextView;
    private TextView minTempTextView;
    private TextView weatherStatusTextView;

    public final static int CURFEW_REQUEST_CODE = 2;
    public final static int LOCK_UNLOCK_REQUEST_CODE = 3;
    public final static int WEATHER_REQUEST_CODE = 4;

    private String weatherStatus;

    private HashMap<String, String> weatherData;


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

        Log.i("[BLUETOOTH]", "Attempting to send data");

        lockStatusTextView = findViewById(R.id.lockStatusTextViewID);
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

        Thread thread = new Thread() {
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
                            }
                        });
                    }
                }
                catch(InterruptedException e) {

                }
            }
        };
        thread.start();

        turnOnLEDButton = findViewById(R.id.turnOnLEDButtonId);
        turnOnLEDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[BLUETOOTH]", "Attempting to send data");

                if(bluetoothSocket.isConnected() && bluetoothCommunicationThread != null) {
                    if(!ledFlag) {
                        String messageToSend = "ON";
                        bluetoothCommunicationThread.
                                writeOutCommunication(messageToSend.getBytes());
                        ledFlag = true;
                    }
                    else {
                        String messageToSend = "OFF";
                        bluetoothCommunicationThread.
                                writeOutCommunication(messageToSend.getBytes());
                        ledFlag = false;
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Failed to connect to Arduino",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

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
        }
        else if (resultCode == RESULT_OK && requestCode == LOCK_UNLOCK_REQUEST_CODE){
            Bundle bundle = data.getExtras();
            String lockStatus = bundle.getString("lockStatus");
            lockStatusTextView.setText(lockStatus);
        }
        else if (resultCode == RESULT_OK && requestCode == WEATHER_REQUEST_CODE){
            Bundle bundle = data.getExtras();
            weatherData = (HashMap<String, String>)bundle.getSerializable("weatherData");
            minTempTextView.setText(weatherData.get("minTemp"));
            maxTempTextView.setText(weatherData.get("maxTemp"));

            weatherStatus = weatherData.get("currentWeatherStatus") + "  ,  " + weatherData.get("currentTemp");

            weatherStatusTextView.setText(weatherStatus);
        }
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