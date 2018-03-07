package com.gpa.browne.gpaslim;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private SeekBar sbPomLength, sbShortBreakLength, sbLongBreakLength;
    private TextView tvPomLength, tvShortBreakLength, tvLongBreakLength;
    int config[] = {0,0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //this places a back button on the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();

        config[0] = intent.getIntExtra("shortBreakLength",0);
        config[1] = intent.getIntExtra("longBreakLength",0);
        config[2] = intent.getIntExtra("pomLength",0);

        sbPomLength = (SeekBar) findViewById(R.id.sbPomLength);
        tvPomLength = (TextView) findViewById(R.id.tvPomLength);
        sbLongBreakLength = (SeekBar) findViewById(R.id.sbLongBreakLength);
        tvLongBreakLength = (TextView) findViewById(R.id.tvLongBreakLength);
        sbShortBreakLength = (SeekBar) findViewById(R.id.sbShortBreakLength);
        tvShortBreakLength = (TextView) findViewById(R.id.tvShortBreakLength);

        sbPomLength.setMax(40);
        sbPomLength.incrementProgressBy(5);
        sbPomLength.setProgress(config[2] - 20);
        tvPomLength.setText("Pom Length: " + config[2] + " minutes");

        sbLongBreakLength.setMax(20);
        sbLongBreakLength.incrementProgressBy(5);
        sbLongBreakLength.setProgress(config[1] - 10);
        tvLongBreakLength.setText("Long Break Length: " + config[1] + " minutes");

        sbShortBreakLength.setMax(10);
        sbShortBreakLength.incrementProgressBy(5);
        sbShortBreakLength.setProgress(config[0] - 5);
        tvShortBreakLength.setText("Short Break Length: " + config[0] + " minutes");

        // perform seek bar change listener event used for getting the progress value
        sbPomLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 20;
                progress = progress / 5;
                progress = progress * 5;
                config[2] = progress;
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                tvPomLength.setText("Pom Length: " + progressChangedValue + " minutes");
            }
        });

        // perform seek bar change listener event used for getting the progress value
        sbLongBreakLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 10;
                progress = progress / 5;
                progress = progress * 5;
                config[1] = progress;
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                tvLongBreakLength.setText("Long Break Length: " + progressChangedValue + " minutes");
            }
        });

        // perform seek bar change listener event used for getting the progress value
        sbShortBreakLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 5;
                progress = progress / 5;
                progress = progress * 5;
                config[0] = progress;
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                tvShortBreakLength.setText("Short Break Length: " + progressChangedValue + " minutes");
            }
        });
    }

    //This override is neede to allow for a back button on the toolbar for this activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            GPAConfigModel model = new GPAConfigModel(getApplicationContext());
            model.saveSettings(config[0], config[1] , config[2]);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDefaultClick(View view) {
        tvPomLength.setText("Pom Length: " + 25 + " minutes");
        tvLongBreakLength.setText("Long Break Length: " + 15 + " minutes");
        tvShortBreakLength.setText("Short Break Length: " + 5 + " minutes");
        sbPomLength.setProgress(25 - 20);
        sbLongBreakLength.setProgress(15 - 10);
        sbShortBreakLength.setProgress(5 - 5);
        //persist this
        GPAConfigModel model = new GPAConfigModel(getApplicationContext());
        model.saveSettings(config[0], config[1] , config[2]);
    }

    public void onHelpClick(View view) {
        Toast.makeText(getApplicationContext(), "Use the sliders to set the number of minutes for each timer. " +
                "Timer can be set in increments of 5. The Default settings are recommended.",Toast.LENGTH_LONG).show();
    }
}
