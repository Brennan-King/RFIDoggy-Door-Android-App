package com.example.rfidoggy_door_android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private Button forceLockUnlockButton;
    private Button setTempButton;
    private Button setCurfewButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forceLockUnlockButton = (Button) findViewById(R.id.forceLockUnlockButtonID);
        forceLockUnlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForceLockUnlock();
            }
        });

        setTempButton = (Button) findViewById(R.id.setTemperatureButtonID);
        setTempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSetTemperature();
            }
        });

        setCurfewButton = (Button) findViewById(R.id.setCurfewButtonID);
        setCurfewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSetCurfew();
            }
        });
    }

    public void openForceLockUnlock(){
        Intent intent = new Intent(this, ForceLockUnlock.class);
        startActivity(intent);
    }

    public void openSetTemperature(){
        Intent intent = new Intent(this, SetTemperature.class);
        startActivity(intent);
    }

    public void openSetCurfew(){
        Intent intent = new Intent(this, SetCurfew.class);
        startActivity(intent);
    }

}
