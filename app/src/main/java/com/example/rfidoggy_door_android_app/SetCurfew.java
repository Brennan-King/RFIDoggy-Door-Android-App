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
    private boolean curfewStartExtraSet = false;
    private boolean curfewEndExtraSet = false;
    private String formattedCurfewStartTime="";
    private String formattedCurfewEndTime="";

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
        Intent intent = new Intent(SetCurfew.this, MainActivity.class);

        if(curfewStartPressed) {
            TextView textViewCurfewStart = findViewById(R.id.curfewStartTextView);
            textViewCurfewStart.setText("Hour: " + hourOfDay + " Minute: " + minute);
            curfewStartHour = hourOfDay;
            curfewStartMinute = minute;
            curfewStartPressed = false;
            formattedCurfewStartTime = formatCurfewStartTime();
            curfewStartExtraSet = true;
        }

        if(curfewEndPressed) {
            TextView textViewCurfewEnd = findViewById(R.id.curfewEndTextView);
            textViewCurfewEnd.setText("Hour: " + hourOfDay + " Minute: " + minute);
            curfewEndHour = hourOfDay;
            curfewEndMinute = minute;
            curfewEndPressed = false;
            formattedCurfewEndTime = formatCurfewEndTime();
            curfewEndExtraSet = true;
        }

        if(curfewStartExtraSet && curfewEndExtraSet) {
            intent.putExtra("curfewTime", formattedCurfewStartTime + "-" +
                    formattedCurfewEndTime);
            setResult(RESULT_OK, intent);
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

    private String formatCurfewEndTime() {
        String curfewEndHourString;
        String curfewEndMinuteString;

        if(curfewEndHour < 10) {
            curfewEndHourString = "0" + curfewEndHour;
        }
        else {
            curfewEndHourString = Integer.toString(curfewEndHour);
        }

        if(curfewEndMinute < 10) {
            curfewEndMinuteString = "0" + curfewEndMinute;
        }
        else {
            curfewEndMinuteString = Integer.toString(curfewEndMinute);
        }

        return curfewEndHourString + ":" + curfewEndMinuteString;
    }
}
