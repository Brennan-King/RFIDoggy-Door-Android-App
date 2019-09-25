package com.example.rfidoggy_door_android_app;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Thread used to concurrently handle communication between device and bluetooth module.
 */
public class BluetoothCommunicationThread extends Thread {
    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private Handler bluetoothCommunicationHandler;
    private static final int RESPONSE_MESSAGE = 10;


    /**
     * Constructor that sets up input and output stream between device and bluetooth module.
     *
     * @param bluetoothSocket Used for IO communication.
     * @param bluetoothCommunicationHandler Handles messages sent back and forth.
     */
    public BluetoothCommunicationThread(BluetoothSocket bluetoothSocket,
                                        Handler bluetoothCommunicationHandler) {

        this.bluetoothSocket = bluetoothSocket;
        this.bluetoothCommunicationHandler = bluetoothCommunicationHandler;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        Log.i("[THREAD-CT]", "Creating thread");

        try {
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
        }
        catch (IOException e) {
            Log.e("[THREAD-CT]", "Error:" + e.getMessage());
        }

        this.inputStream = inputStream;
        this.outputStream = outputStream;

        try {
            this.outputStream.flush();
        }
        catch (IOException e) {
            return;
        }

        Log.i("[THREAD-CT]", "IO's obtained");
    }

    @Override
    /**
     * A constantly running method that looks for new messages to be sent back and forth. This is
     * the entry point for another concurrent thread.
     */
    public void run() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        Log.i("[THREAD-CT]", "Starting thread");

        while (true) {
            try {
                String arduinoResponse = bufferedReader.readLine();
                Message message = new Message();
                message.what = RESPONSE_MESSAGE;
                message.obj = arduinoResponse;
                bluetoothCommunicationHandler.sendMessage(message);
            }
            catch (IOException e) {
                break;
            }
        }
        Log.i("[THREAD-CT]", "While loop ended");
    }

    @SuppressWarnings("unused")
    /**
     * Sends data to the blutooth module.
     *
     * @param bytes Information being sent converted to bytes.
     */
    public void writeOutCommunication(byte[] bytes) {
        try {
            Log.i("[THREAD-CT]", "Writing bytes to Arduino");
            outputStream.write(bytes);
        }
        catch (IOException e) {
        }
    }

    @SuppressWarnings("unused")
    /**
     * Terminates communication between device and bluetooth module.
     */
    public void terminateCommunication() {
        try {
            bluetoothSocket.close();
        }
        catch (IOException e) {}
    }

    /**
     * Retrieves the response message.
     *
     * @return RESPONSE_MESSAGE
     */
    public static int getResponseMessage() {
        return RESPONSE_MESSAGE;
    }

}
