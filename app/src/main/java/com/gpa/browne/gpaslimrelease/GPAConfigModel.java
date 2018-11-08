package com.gpa.browne.gpaslimrelease;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

/**
 * Created by Roveros on 31/10/2017.
 */

public class GPAConfigModel {

    private int pomLength, shortBreakLength, longBreakLength;
    private Context context;
    static final int READ_BLOCK_SIZE = 100;

    public GPAConfigModel(Context context){
        //should be fetching these values from persistent storage
        //Create persistent model object to retrieve data
        shortBreakLength = 5;
        longBreakLength = 15;
        pomLength = 25;

        this.context = context;
        detectSettings(this.context);
    }

    private void detectSettings(Context context) {
        File myMainDir = context.getDir("settings", Context.MODE_PRIVATE);

        File myFinalDir = new File(myMainDir, "settings.txt");

        if (myFinalDir.exists()){
            Log.i("INFO","Settings.txt detected");
            Log.i("INFO", "Path: "+ myFinalDir.getAbsolutePath());
            Log.i("INFO", "File name: " + myFinalDir.getName());

            //parse settings
            //reading text from file
            try {
                Log.i("INFO", "Retrieving settings...");
                FileInputStream fis = new FileInputStream(new File(myFinalDir.getAbsolutePath()));
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                Log.i("INFO", "Setting Retrieved.");
                Log.i("INFO", "File contents: " + sb.toString());
                fis.close();
                String[] settings = sb.toString().split("@");
                Log.i("INFO", Arrays.toString(settings));
                String shortBreakLength[] = settings[0].split(":");
                String longBreakLength[] = settings[1].split(":");
                String pomLength[] = settings[2].split(":");
                setShortBreakLength(Integer.valueOf(shortBreakLength[1]));
                setLongBreakLength(Integer.valueOf(longBreakLength[1]));
                setPomLength(Integer.valueOf(pomLength[1]));
                //dayList.add(s);
            } catch (Exception e) {
                Log.i("INFO", "Unable to retrieve settings.");
                Log.i("INFO", e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.i("INFO","Settings.txt not detected, default settings generated");
            createDefaultSettings();
        }
    }

    public String getSettings(){
        String settings = pomLength +"."+ shortBreakLength +"."+ longBreakLength;
        return settings;
    }

    public void createDefaultSettings(){
        // add-write text into file

        String settings = "";
        settings = "shortBreakLength:"+shortBreakLength+"@longBreakLength:"+longBreakLength+"@pomLength:"+pomLength+"";

        try {
            File myMainDir = context.getDir("settings", Context.MODE_PRIVATE);

            myMainDir.mkdir();

            File myFinalDir = new File(myMainDir, "settings.txt");

            FileOutputStream out = new FileOutputStream(myFinalDir, false); //Use the stream as usual to write into the file
            OutputStreamWriter outputWriter = new OutputStreamWriter(out);
            outputWriter.write(settings);
            outputWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSettings(int shortBreakLength, int longBreakLength, int pomLength){
        // add-write text into file
        String settings = "";
        settings = "shortBreakLength:"+shortBreakLength+"@longBreakLength:"+longBreakLength+"@pomLength:"+pomLength+"";
        Log.i("INFO", "Saving new settings ...");

        try {
            File myMainDir = context.getDir("settings", Context.MODE_PRIVATE);

            myMainDir.mkdir();

            File myFinalDir = new File(myMainDir, "settings.txt");

            FileOutputStream out = new FileOutputStream(myFinalDir, false); //Use the stream as usual to write into the file
            OutputStreamWriter outputWriter = new OutputStreamWriter(out);
            outputWriter.write(settings);
            outputWriter.close();

            Log.i("INFO",settings);

        } catch (Exception e) {
            Log.i("INFO", "Failed to save new settings");
            e.printStackTrace();
        }
    }

    public int getPomLength() {
        return pomLength;
    }

    public void setPomLength(int pomLength) {
        this.pomLength = pomLength;
    }

    public int getShortBreakLength() {
        return shortBreakLength;
    }

    public void setShortBreakLength(int shortBreakLength) {
        this.shortBreakLength = shortBreakLength;
    }

    public int getLongBreakLength() {
        return longBreakLength;
    }

    public void setLongBreakLength(int longBreakLength) {
        this.longBreakLength = longBreakLength;
    }
}
