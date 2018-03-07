package com.gpa.browne.gpaslim;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TimerController timer;
    GPAConfigModel config;
    TextView tvTimerDisplay, tvCounterDisplay;
    EditText etSessionTitle;
    CoordinatorLayout layout;
    ProgressBar progressBar;

    String topics[];

    String prizeData = "";
    String prizeLog = "";
    String settings = "";

    String topicTitle = "";
    String badgeData = "";
    String goalData = "";
    String logData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init the GPAConfigModel
        config = new GPAConfigModel(this);

        //get timer display TextView from view
        tvTimerDisplay = findViewById(R.id.tvTimerDisplay);

        //get the counter display from the view
        tvCounterDisplay = findViewById(R.id.tvCounterDisplay);

        //get the session title from the view
        etSessionTitle = findViewById(R.id.etSessionTitle);

        //get the layout from the view. Will be used to edit background colour
        layout = findViewById(R.id.coordLayout);

        //get progress bar
        progressBar = findViewById(R.id.progressBar);

        //init the TimerModel and pass it the tvTimerDisplay TextView
        timer = new TimerController(MainActivity.this, layout, progressBar, config, tvTimerDisplay, tvCounterDisplay, etSessionTitle);

        topics = null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_LONG).show();
            config = new GPAConfigModel(this);
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("shortBreakLength", config.getShortBreakLength());
            intent.putExtra("longBreakLength", config.getLongBreakLength());
            intent.putExtra("pomLength", config.getPomLength());
            startActivity(intent);
        } else if (id == R.id.action_exit) {
            onExitClick();
        } else if (id == R.id.action_email) {
            sendEmail();
        } else if (id == R.id.action_select_topic) {
            displayTopics();
        }

        return super.onOptionsItemSelected(item);
    }


    private void displayTopics() {
        File myMainDir = getDir("logs", Context.MODE_PRIVATE);
        File[] files = myMainDir.listFiles();

        if(files.length != 0){
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Choose Topic");

            final StringBuilder sb = new StringBuilder();
            for (File inFile : files) {
                if (inFile.isDirectory()) {
                    Log.i("INFO", inFile.getName());
                    sb.append(inFile.getName()+"\n");
                }
            }
            sb.append("Exit");
            final String[] types = sb.toString().split("\\n");
            b.setItems(types, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    if(!types[which].equals("Exit")){
                        etSessionTitle.setText(types[which]);
                    }

                }
            });
            b.show();
        } else {
            Toast.makeText(this, "No topics detected", Toast.LENGTH_SHORT).show();
        }

    }

    //Starts timer if not already started
    public void onStartClick(View view) {
        timer.start("pom");
    }

    //Stops timer if timer has been started
    public void onStopClick(View view) {
        if (timer.isRunning()) {
            Log.i("Click Event", "onStopClick");
            timer.stop();
        }
    }

    //Exits tha application
    public void onExitClick() {
        if(timer.isRunning()){
            timer.stop();
        }
        finish();
        System.exit(0);
    }

    //Sends all text data for the session title
    protected void sendEmail() {

        Log.i("INFO", "Sending email ... ");

        String[] TO = {
                "b00075549@student.itb.ie"
        };
        String[] CC = {
                "robert.browne@student.itb.ie"
        };
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "All Data");

        //get prizedata ¦
        //get prizelog ¦
        //get settings |
        //foreach topic {
        //      get topic title ¦
        //      get all badge files, sort by creation date, add to list, add to string week 1 @ week 2 @ etc ¦
        //      get all goal files, sort by creation date, add to list, add to string week 1 @ week 2 @ etc ¦
        //      get all log files, sort by creation date, add to list, add to string day 1 @ day 2 @ etc |
        //remove final |

        File myMainDir = getDir("logs", Context.MODE_PRIVATE);
        File[] files = myMainDir.listFiles();

        if(files.length != 0) {
            final StringBuilder sb = new StringBuilder();
            for (File inFile : files) {
                if (inFile.isDirectory()) {
                    //Log.i("INFO", inFile.getName());
                    sb.append(inFile.getName() + "\n");
                }
            }
            final String[] types = sb.toString().split("\\n");
            topics = types;
        }

        prizeData = "NULL";
        prizeLog = "NULL";
        settings = getSettings();

        if(TextUtils.isEmpty(prizeData)){
            prizeData = "NULL";
        }
        if(TextUtils.isEmpty(prizeLog)){
            prizeLog = "NULL";
        }
        if(TextUtils.isEmpty(settings)){
            settings = "NULL";
        }

        String allTopicData = "";
        String generalData = prizeData + "¦" + prizeLog + "¦" + settings;
        Log.i("INFO-email", generalData);


        for (String topic : topics) {
            topicTitle = topic;

            if(TextUtils.isEmpty(badgeData)){
                badgeData = badgeData + "NULL";
            } else {
                badgeData = badgeData + "@" + "NULL";
            }

            if(TextUtils.isEmpty(goalData)){
                goalData = goalData + "NULL";
            } else {
                goalData = goalData + "@" + "NULL";
            }

            if(TextUtils.isEmpty(logData)){
                logData = logData + getLogData(topic);
            } else {
                logData = logData + "@" + getLogData(topic);
            }

            String tempTopicData = topicTitle + "¦" + badgeData + "¦" + goalData + "¦" + logData;
            Log.i("INFO-email", "TopicData: " + tempTopicData);

            if(TextUtils.isEmpty(allTopicData)){
                allTopicData = allTopicData + tempTopicData;
            } else {
                allTopicData = allTopicData + "|" + tempTopicData;
            }
        }

        if(TextUtils.isEmpty(badgeData)){
            badgeData = "NULL";
        }
        if(TextUtils.isEmpty(goalData)){
            goalData = "NULL";
        }
        if(TextUtils.isEmpty(logData)){
            logData = "NULL";
        }

        String allData = generalData + "|" + allTopicData;
        Log.i("INFO-email", "All Data: " + allData);
        //concat all the files contents into a single string
        emailIntent.putExtra(Intent.EXTRA_TEXT, allData);


/*         //concat all the files contents into a single string
        try {
            for (File file: fileList) {
                stringBuilder.append(readFileToString(file.getPath()));
            }
            emailIntent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
        } catch (Exception e) {
            Log.e("ERROR!", "IOException converting files to string.");
        }*/

        //Start the email intent
        try {
            startActivity(Intent.createChooser(emailIntent, "Choose an email provider"));
            finish();
            Log.i("INFO", "Email sent ...");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "No email client installed. Please download and install Microsoft Outlook", Toast.LENGTH_SHORT).show();
        }

    }

    public String getSettings() {
        GPAConfigModel model = new GPAConfigModel(this);
        String settings = model.getSettings();

        Log.i("INFO-Email", "Settings: " + settings);
        return settings;
    }

    public String getLogData(String topic) {
 /*       TimerModel model = new TimerModel(this, topic);
        ArrayList <String[]> allLogData = model.getAllLogData();
        Get all log data into an arraylist of logs
        */
        ArrayList<String[]> logList = new ArrayList<>();
        String logs[] = new String[2];
        DateFormat df1;

        Calendar cal = Calendar.getInstance(new Locale("en","UK"));
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        //get date of the this weeks first day (Monday)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = dateFormat.format(cal.getTime());

        //Identify the directory that goals data is saved in
        File myMainDir = getDir("logs", Context.MODE_PRIVATE);
        File mySubDir = new File(myMainDir, topic);

        //create it if it doesn't exist
        if(!mySubDir.exists()){
            mySubDir.mkdir();
        }

        //for each file in there add it tot the list of files
        ArrayList<String> files = new ArrayList<String>(); //ArrayList cause you don't know how many files there is
        File[] filesInFolder = mySubDir.listFiles(); // This returns all the folders and files in your path


        for (File file : filesInFolder) { //For each of the entries do:
            if (!file.isDirectory()) { //check that it's not a dir
                files.add(new String(file.getName())); //push the filename as a string
            }
        }

        //If there are files
        if(!files.isEmpty()) {
            logList.clear();
            // for each file in the topic folder ...
            for (String file : files) {

                File myFinalDir = new File(mySubDir, file);
                Log.i("INFO", "Searching for " + file + " ...");
                if (myFinalDir.exists()) {
                    Log.i("INFO", "File detected.");
                    Log.i("INFO", "Path: " + myFinalDir.getAbsolutePath());
                    Log.i("INFO", "File name: " + myFinalDir.getName());

                    //data tokenised example:
                    //type.startTime.endTime.endType%type.startTime.endTime.type.startTime.endTime.endType
                    //pom.14:21:35.14:21:40.cancelled%pom.14:21:53.14:21:58.pom.14:22:00.14:22:05.end%
                    try {
                        Log.i("INFO", "Attempting to retrieving log data ...");
                        FileInputStream fis = new FileInputStream(new File(myFinalDir.getAbsolutePath()));
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader bufferedReader = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }
                        Log.i("INFO", "Log data retrieved successfully.");
                        Log.i("INFO", "File contents: " + sb.toString());
                        fis.close();
                        //split the data by week
                        String rawlogData = sb.toString();
                        Log.i("INFO", "logs contents pre write: " + logs);

                        //the last iteration of this loop should contain the most recently saved data for this topic
                        //So badges[0] will have the most recent date in it

                        //split the data by variable
                        Log.i("INFO", "rawlogData: " + rawlogData);
                        logs[1] = rawlogData;

                        df1 = new SimpleDateFormat("yyyy-MM-dd");
                        String rawDate = (file.toString().subSequence(0, file.toString().length() - 4).toString());
                        Date tempDate = df1.parse(rawDate);
                        Calendar tempcal = Calendar.getInstance(new Locale("en", "UK"));
                        tempcal.setTime(tempDate);
                        logs[0] = tempcal.getTime().toString();
                        logList.add(logs);

                        Log.i("INFO", "Logs[] contents post write: " + Arrays.toString(logs));
                    } catch (Exception e) {
                        Log.i("INFO", "Unable to retrieve log data.");
                        Log.i("INFO", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Log.i("INFO", "Logs data could not be detected. Generating default logs.txt");
                    //create default data for this week.
                }
            }
        }

        ArrayList <Date> dates = new ArrayList<>();
        DateFormat df;
        for (String array[] : logList) {
            try {
                df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
                dates.add(df.parse(array[0]));
            } catch (ParseException e) {
                Log.i("INFO-getLogData", "Error formatting date");
            }
        }

        Boolean unsorted = true;
        while (unsorted) {
            unsorted = false;
            for (int i = 0; i < dates.size()-1; i++) {
                Date temp1 = dates.get(i);
                Date temp2 = dates.get(i+1);
                String tempArray1[] = logList.get(i);
                String tempArray2[] = logList.get(i+1);


                //if temp1 is after temp2, switch'em to sort by date ascending
                if(temp1.compareTo(temp2) == 1){
                    dates.set(i, temp2);
                    dates.set(i+1, temp1);
                    logList.set(i, tempArray2);
                    logList.set(i+1, tempArray1);
                    unsorted = true;
                    Log.i("INFO-getLogData", "Switching");
                }
            }
        }

        //dates and allLogData is now sorted by date

        String allDataString = "";
        //for each array in the list
        for (int i = 0; i < logList.size() ; i++) {
            String tempLogString = "";
            //for each variable in the array, append to string
            for (String variable : logList.get(i)) {
                if(TextUtils.isEmpty(tempLogString)){
                    tempLogString = tempLogString + variable;
                } else {
                    tempLogString = tempLogString + "." + variable;
                }
            }

            //take the string above and add it to the allDataString
            if(TextUtils.isEmpty(allDataString)){
                allDataString = allDataString + tempLogString;
            } else {
                allDataString = allDataString + "@" + tempLogString;
            }
        }

        Log.i("INFO-Email", "All Log data for topic " + topic + ": " + allDataString);
        return allDataString;
    }


    //reads files and returns a string
    static String readFileToString(String path) throws IOException {
        InputStream is = new FileInputStream(path);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();

        while (line != null) {
            sb.append(line);
            line = buf.readLine();
        }

        String fileAsString = sb.toString();
        System.out.println("Contents : " + fileAsString);

        return fileAsString;
    }
}