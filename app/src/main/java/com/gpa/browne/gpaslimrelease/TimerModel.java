package com.gpa.browne.gpaslimrelease;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Roveros on 07/11/2017.
 */

public class TimerModel {
    static final int READ_BLOCK_SIZE = 100;
    String startTime, endTime, pomType, dir, fileName;
    List < String > dayList, weekList;
    Calendar calendar;
    Context context;

    public TimerModel(Context context, String pomType, String dir) {

        this.context = context;
        dayList = new ArrayList < String > ();
        weekList = new ArrayList < String > ();

        //pom, shortBreak, longBreak
        this.pomType = pomType;
        this.dir = dir;

        //Use Calender to get the current date and then format it and store it as a string
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        fileName = dateFormat.format(calendar.getTime()) + ".txt";

        //Use Calender to get the current time and then format it and store it as a string
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        startTime = timeFormat.format(calendar.getTime());
    }

    public void setEndTime() {
        //Use Calender to get the current time and then format it and store it as a string
        calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        endTime = timeFormat.format(calendar.getTime());
    }

    //appends an end type to records if there is a valid directory
    public void appendEndType(String endType) {
        if (!dir.equals("no dir")) {

            // add-write text into file
            try {
                File myMainDir = context.getDir("logs", Context.MODE_PRIVATE);

                File mySubDir = new File(myMainDir, dir);
                mySubDir.mkdir();

                File myFinalDir = new File(mySubDir, fileName);

                FileOutputStream out = new FileOutputStream(myFinalDir, true); //Use the stream as usual to write into the file
                OutputStreamWriter outputWriter = new OutputStreamWriter(out);
                outputWriter.write(endType);
                outputWriter.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //persists variables
    public int persist() {
        if (!dir.equals("no dir")) {
            //error codes: 2 = no directory given, 1 = success, 0 = empty variables, -1 unknown error

            //if none of the variables are empty or null then continue
            if (!TextUtils.isEmpty(pomType) && !TextUtils.isEmpty(startTime) &&
                    !TextUtils.isEmpty(endTime) && !TextUtils.isEmpty(fileName)) {

                // add-write text into file
                try {

                    File myMainDir = context.getDir("logs", Context.MODE_PRIVATE);

                    //  File myTopicDir = new File(myMainDir, dir);
                    //  mySubDir.mkdir();

                    File mySubDir = new File(myMainDir, dir);
                    mySubDir.mkdir();

                    File myFinalDir = new File(mySubDir, fileName);
                    //File myFinalDir = new File(mySubDir, "debug.txt");

                    FileOutputStream out = new FileOutputStream(myFinalDir, true); //Use the stream as usual to write into the file
                    OutputStreamWriter outputWriter = new OutputStreamWriter(out);
                    outputWriter.write(pomType + "." + startTime + "." + endTime + ".");
                    outputWriter.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ERROR", "Could not persist data");
                    return -1;
                }
                return 1;

            } else {
                return 0;
            }
        }
        return 2;
    }


    public List < String > getDayList(String date) {
        //reading text from file
        try {
            Log.i("INFO", "Retrieving filename: " + date);
            FileInputStream fileIn = context.openFileInput(date);
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            InputRead.close();
            Log.i("INFO", "File contents: " + s);
            dayList.add(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dayList;
    }

    public boolean delete(String date) {
        File dir = context.getFilesDir();
        File file = new File(dir, date);
        return file.delete();
    }

}