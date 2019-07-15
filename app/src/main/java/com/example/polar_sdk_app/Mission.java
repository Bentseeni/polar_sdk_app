package com.example.polar_sdk_app;

import java.util.Date;
import java.util.Random;

public class Mission {

    String missionName;
    int missionScore;
    double missionTime;
    int missionValue;
    int missionId;

    public boolean isMissionCompleted() {
        return missionCompleted;
    }

    public void setMissionCompleted(boolean missionCompleted) {
        this.missionCompleted = missionCompleted;
    }

    boolean missionCompleted;
    Random random;
    Date now;

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public int getMissionScore() {
        return missionScore;
    }

    public void setMissionScore(int missionScore) {
        this.missionScore = missionScore;
    }

    public double getMissionTime() {
        return missionTime;
    }

    public void setMissionTime(double missionTime) {
        this.missionTime = missionTime;
    }

    public int getMissionValue() {
        return missionValue;
    }

    public void setMissionValue(int missionValue) {
        this.missionValue = missionValue;
    }

    public int getMissionId() {
        return missionId;
    }

    public void setMissionId(int missionId) {
        this.missionId = missionId;
    }

    public Mission() {

        now = new Date();
        random = new Random();
        missionId = random.nextInt(2);

        if (missionId == 0)
        {
            missionName = "Heart rate";
            missionScore = 100;
            missionTime = now.getTime();
            missionValue = 90;
            missionCompleted = false;
        }
        else if (missionId == 1)
        {
            missionName = "Speed";
            missionScore = 100;
            missionTime = now.getTime();
            missionValue = 15;
            missionCompleted = false;
        }
        else if (missionId == 2)
        {
            missionName = "Distance";
            missionScore = 100;
            missionTime = now.getTime();
            missionValue = 200;
            missionCompleted = false;
        }


    }

    public Mission(int id)
    {
        now = new Date();
        missionId = id;

        if (missionId == 0)
        {
            missionName = "Heart rate";
            missionScore = 100;
            missionTime = now.getTime();
            missionValue = 90;
            missionCompleted = false;
        }
        else if (missionId == 1)
        {
            missionName = "Speed";
            missionScore = 100;
            missionTime = now.getTime();
            missionValue = 15;
            missionCompleted = false;
        }
        else if (missionId == 2)
        {
            missionName = "Distance";
            missionScore = 100;
            missionTime = now.getTime();
            missionValue = 200;
            missionCompleted = false;
        }
    }
}