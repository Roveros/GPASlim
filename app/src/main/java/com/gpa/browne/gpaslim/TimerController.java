package com.gpa.browne.gpaslim;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * Created by Roveros on 31/10/2017.
 */

class TimerController {
    private TimerModel model;
    private GPAConfigModel config;
    private int millisinFuture, countDownInterval;
    private TextView tvTimerDisplay, tvCounterDisplay;
    private EditText etSessionTitle;
    private int counter;
    private CountDownTimer timer;
    private boolean running;
    private Context context;
    private CoordinatorLayout layout;
    private ProgressBar progressBar;


    TimerController(Context context, CoordinatorLayout layout, ProgressBar progressBar, GPAConfigModel config,
                    TextView tvTimerDisplay, TextView tvCounterDisplay, EditText etSessionTitle) {
        running = false;

        this.context = context;
        this.layout = layout;
        this.progressBar = progressBar;
        this.config = config;
        countDownInterval = 1000; // tick every 1 second

        this.tvTimerDisplay = tvTimerDisplay;
        tvTimerDisplay.setText(millisToTimerString(millisinFuture));

        this.tvCounterDisplay = tvCounterDisplay;
        counter = 0;
        tvCounterDisplay.setText(counter + "");

        this.etSessionTitle = etSessionTitle;
    }

    void start(final String type) {
        String dir = "";

        //if the contents of the edit text are not empty or null then set the directory
        if (!TextUtils.isEmpty(etSessionTitle.getText().toString())) {
            dir = etSessionTitle.getText().toString();
        } else {
            dir = "no dir";
        }

        //Generate a model for this pom
        model = new TimerModel(context, type, dir);

        if (type.equals("pom")) {
            //model param to track timer state
            running = true;
            layout.setBackgroundColor(Color.parseColor("#ffcc0000"));
            progressBar.getProgressDrawable().setColorFilter(
                    Color.parseColor("#ff669900"), android.graphics.PorterDuff.Mode.SRC_IN);

            /*        millisinFuture = config.getPomLength() * 60000;     // 1000 = 1 second, 60000 = 60 seconds or 1 minute*/
            millisinFuture = 25 * 60000; // 25 * 1 Minute

        } else if (type.equals("shortBreak")) {
            //model param to track timer state
            running = true;
            layout.setBackgroundColor(Color.parseColor("#ff669900"));
            progressBar.getProgressDrawable().setColorFilter(
                    Color.parseColor("#a9a9a9"), android.graphics.PorterDuff.Mode.SRC_IN);

            /*millisinFuture = config.getShortBreakLength() * 60000;    // 1000 = 1 second, 60000 = 60 seconds or 1 minute*/
            millisinFuture = 5 * 60000; // 5 * 1 Minute

        } else if (type.equals("longBreak")) {
            //model param to track timer state
            running = true;
            layout.setBackgroundColor(Color.parseColor("#ff669900"));
            progressBar.getProgressDrawable().setColorFilter(
                    Color.parseColor("#a9a9a9"), android.graphics.PorterDuff.Mode.SRC_IN);

            //update counter and counter display
            counter = 0;
            tvCounterDisplay.setText(counter + "");

            /*millisinFuture = config.getLongBreakLength() * 60000;     // 1000 = 1 second, 60000 = 60 seconds or 1 minute*/
            millisinFuture = 15 * 60000; // 15 * 1 Minute
        }

        //set progress bar maximum = to the total number of miliseconds of the timer
        progressBar.setMax(millisinFuture);


        //if timer is running then start countdown
        if (running) {
            //param 1 and 2 in milliseconds, i.e. 30000 = 30 seconds
            timer = new CountDownTimer(millisinFuture, countDownInterval) {

                //for every tick
                public void onTick(long millisUntilFinished) {
                    tvTimerDisplay.setText(millisToTimerString(millisUntilFinished));

                    progressBar.setProgress(millisinFuture - (int) millisUntilFinished);
                }

                //on timer finish
                public void onFinish() {
                    model.setEndTime();
                    model.persist();

                    progressBar.setProgress(0);

                    //on finish calls an alert dialog as appropriate
                    if (type.equals("pom")) {
                        //update counter and counter display if session type == pom
                        counter++;
                        tvCounterDisplay.setText(counter + "");

                        if (counter >= 4) {
                            alertShowUserOptions("longBreak");
                        } else {
                            alertShowUserOptions("shortBreak");
                        }
                    } else {
                        alertShowUserOptions("pom");
                    }

                    //update timer display
                    tvTimerDisplay.setText(millisToTimerString(0));
                    running = false;
                }
            }.start();
        }

        //model param to track timer state
    }

    void stop() {
        //only perform logic when timer is running
        if (running) {
            model.setEndTime();
            model.appendEndType("cancelled%");

            //Stop and null the timer object
            timer.cancel();
            timer = null;

            //update view colour scheme and progress bar
            layout.setBackgroundColor(Color.parseColor("#ffcc0000"));
            progressBar.getProgressDrawable().setColorFilter(
                    Color.parseColor("#ff669900"), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setProgress(0);

            //update model information
            running = false;
            tvTimerDisplay.setText(millisToTimerString(0));

            //reset counter display on session stop
            counter = 0;
            tvCounterDisplay.setText(counter + "");
        }
    }

    //takes milliseconds long variable and returns a formatted mm:ss string
    private String millisToTimerString(long millis) {

        //values rounded to keep timer countdown display consistent with 1000ms ticks
        long minutes = (Math.round(millis * 0.001f)) / 60;
        long seconds = (Math.round(millis * 0.001f)) % 60;

        String timerString = (String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
        return timerString;
    }

    //this method pops up an alert dialog based on the type requested
    private void alertShowUserOptions(String timerType) {
        String previousType, title, displayTimerType;

        progressBar.setProgress(millisinFuture);

        MediaPlayer ring= MediaPlayer.create(context,R.raw.notify);
        ring.start();

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

        //setting string values based on parameter, these will be used in the dialogue
        switch (timerType) {
            case "shortBreak":
                displayTimerType = "Break";
                previousType = "Pom";
                title = previousType + " Complete";
                break;
            case "longBreak":
                displayTimerType = "Break";
                previousType = "Pom";
                title = previousType + " Complete: \nLong Break Available!";
                break;
            default:
                displayTimerType = "Pom";
                previousType = "Break";
                title = previousType + " Over";
                break;
        }

        //alert builder offer 2 - 3 choices based on parameters received
        //will call start appropriate timer based on logic if necessary
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage("How would you like to continue?");
        final String finalType = timerType;
        builder.setNegativeButton("End",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing ... maybe record End
                        model.appendEndType("end%");
                        progressBar.setProgress(0);
                        counter = 0;
                        tvCounterDisplay.setText(counter + "");
                        layout.setBackgroundColor(Color.parseColor("#ffcc0000"));
                        progressBar.getProgressDrawable().setColorFilter(
                                Color.parseColor("#ff669900"), android.graphics.PorterDuff.Mode.SRC_IN);
                    }
                });
        builder.setPositiveButton("Start " + displayTimerType,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (finalType.equals("longBreak")) {
                            counter = 0;
                            tvCounterDisplay.setText(counter + "");
                        }

                        start(finalType);
                    }
                });
        if (displayTimerType.equals("Break")) {
            builder.setNeutralButton("Skip " + displayTimerType,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            start("pom");
                        }
                    });
        }
       final AlertDialog alert = builder.show();

        // Auto end after 1 minute if no selection is made
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing()) {
                    alert.dismiss();
                    model.appendEndType("end%");
                    progressBar.setProgress(0);
                    counter = 0;
                    tvCounterDisplay.setText(counter + "");
                    layout.setBackgroundColor(Color.parseColor("#ffcc0000"));
                    progressBar.getProgressDrawable().setColorFilter(
                            Color.parseColor("#ff669900"), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
        };

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 60000); // 1 Minute

    }

    boolean isRunning() {
        return running;
    }
}