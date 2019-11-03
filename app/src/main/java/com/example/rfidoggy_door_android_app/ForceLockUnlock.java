package com.example.rfidoggy_door_android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ForceLockUnlock extends AppCompatActivity {
    private Button lockForeverButton;
    private Button unlockForeverButton;
    private Button resetButton;
    private String forceLockStatus="";

    Intent intent = new Intent(ForceLockUnlock.this, MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_force_lock_unlock);

        lockForeverButton = findViewById(R.id.lockForeverButtonID);
        unlockForeverButton = findViewById(R.id.unlockForeverButtonID);
        resetButton = findViewById(R.id.resetButtonID);

        lockForeverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forceLockStatus = "LOCKED";
                intent.putExtra("lockStatus", forceLockStatus);
                setResult(RESULT_OK, intent);
            }
        });

        unlockForeverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forceLockStatus = "UNLOCKED";
                intent.putExtra("lockStatus", forceLockStatus);
                setResult(RESULT_OK, intent);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forceLockStatus = "N/A";
                intent.putExtra("lockStatus", forceLockStatus);
                setResult(RESULT_OK, intent);
            }
        });
    }
}
