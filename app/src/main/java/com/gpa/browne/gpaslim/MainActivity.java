package com.gpa.browne.gpaslim;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TimerController timer;
    GPAConfigModel config;
    TextView tvTimerDisplay, tvCounterDisplay;
    EditText etSessionTitle;
    CoordinatorLayout layout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init the GPAConfigModel
        config = new GPAConfigModel();

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
            Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_LONG).show();

        } else if (id == R.id.action_exit) {
            onExitClick();
        } else if (id == R.id.action_email) {
            sendEmail();
        }

        return super.onOptionsItemSelected(item);
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
        finish();
        System.exit(0);
    }

    //Sends all text data for the session title
    protected void sendEmail() {
        if(!TextUtils.isEmpty(etSessionTitle.getText())){
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
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Session Data");

            File myFinalDir;
            List<File> fileList = new ArrayList<>();

            StringBuilder stringBuilder = new StringBuilder();

            // add-write text into file
            try {
                File myMainDir = getDir("logs", Context.MODE_PRIVATE);
                File mySubDir = new File(myMainDir, etSessionTitle.getText().toString());

                //list all the text files in the directory
                String fileListString[] = mySubDir.list();
                for (String file:fileListString) {
                    myFinalDir = new File(mySubDir, file);
                    fileList.add(myFinalDir);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR!", "Could not locate file: " + etSessionTitle.getText().toString() + "/");
            }

            //concat all the files contents into a single string
            try {
                for (File file: fileList) {
                    stringBuilder.append(readFileToString(file.getPath()));
                }
                emailIntent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
            } catch (Exception e) {
                Log.e("ERROR!", "IOException converting files to string.");
            }

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