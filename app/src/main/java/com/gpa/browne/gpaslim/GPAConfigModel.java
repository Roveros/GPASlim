package com.gpa.browne.gpaslim;

/**
 * Created by Roveros on 31/10/2017.
 */

public class GPAConfigModel {

    int pomLength, shortBreakLength, longBreakLength;

    public GPAConfigModel(){
        //should be fetching these values from persistent storage
        //Create persistent model object to retrieve data

        //if no values exist, use defaults
        pomLength = 25;
        shortBreakLength = 5;
        longBreakLength = 15;
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
