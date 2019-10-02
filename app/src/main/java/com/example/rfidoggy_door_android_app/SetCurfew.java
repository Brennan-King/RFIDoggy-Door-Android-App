package com.example.rfidoggy_door_android_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SetCurfew extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private Button curfewStartButton;
    private Button curfewEndButton;
    private int curfewStartHour;
    private int curfewStartMinute;
    private int curfewEndHour;
    private int curfewEndMinute;
    private Boolean curfewStartPressed = false;
    private Boolean curfewEndPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_curfew);

        curfewStartButton = findViewById(R.id.curfewStartButton);
        curfewEndButton = findViewById(R.id.curfewEndButton);

        curfewStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curfewStartPressed = true;
                DialogFragment timePicker = new TimePicker();
                timePicker.show(getSupportFragmentManager(), "time Picker");
            }
        });

        curfewEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curfewEndPressed = true;
                DialogFragment timePicker = new TimePicker();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
    }

    @Override
    public void onTimeSet(android.widget.TimePicker timePicker, int hourOfDay, int minute) {
        if(curfewStartPressed) {
            TextView textViewCurfewStart = findViewById(R.id.curfewStartTextView);
            textViewCurfewStart.setText("Hour: " + hourOfDay + " Minute: " + minute);
            curfewStartHour = hourOfDay;
            curfewStartMinute = minute;
            curfewStartPressed = false;
            Intent intent = new Intent(SetCurfew.this, MainActivity.class);
            intent.putExtra("curfewTimeRange", formatCurfewStartTime());
            setResult(RESULT_OK, intent);
            //finish();
        }

        if(curfewEndPressed) {
            TextView textViewCurfewEnd = findViewById(R.id.curfewEndTextView);
            textViewCurfewEnd.setText("Hour: " + hourOfDay + " Minute: " + minute);
            curfewEndHour = hourOfDay;
            curfewEndMinute = minute;
            curfewEndPressed = false;
        }
    }

    private String formatCurfewStartTime() {
        String curfewStartHourString;
        String curfewStartMinuteString;

        if(curfewStartHour < 10) {
            curfewStartHourString = "0" + curfewStartHour;
        }
        else {
            curfewStartHourString = Integer.toString(curfewStartHour);
        }

        if(curfewStartMinute < 10) {
            curfewStartMinuteString = "0" + curfewStartMinute;
        }
        else {
            curfewStartMinuteString = Integer.toString(curfewStartMinute);
        }

        return curfewStartHourString + ":" + curfewStartMinuteString;
    }

   //public void updateMainCurfewTextView() {
       // TextView textview = findViewById(R.id.curfewTimeRangeTextView);
        //textview.setText(formatCurfewStartTime());
   //}

}
