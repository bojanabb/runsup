package si.uni_lj.fri.pbd2019.runsup.helpers;

import android.content.Context;

import java.util.List;

public class SportActivities {

    public static Integer[] act={1,2,3};

    public static String getActivityType(Context ctx, Integer type) {
        if(type==1) return "Running";
        else if(type==2) return "Walking";
        return "Cycling";
    }

    public static double getMET(int activityType, Float speed) {
       float speedn = (int)Math.ceil(speed * 2.23694f);
       double met;
       if(activityType==0) {
           if(speedn==4) {
               met=6.0;
           }
           else if (speedn==5) {
               met=8.3;
           }
           else if(speedn==6) {
               met=9.8;
           }
           else if(speedn==7) {
               met=11.0;
           }
           else if(speedn==8) {
               met=11.8;
           }
           else if(speedn==9) {
               met=12.8;
           }
           else if(speedn==10) {
               met=14.5;
           }
           else if(speedn==11) {
               met=16.0;
           }
           else if(speedn==12) {
               met=19.0;
           }
           else if(speedn==13) {
               met=19.8;
           }
           else if(speedn==14) {
               met=23.0;
           }
           else {
                met=2.23694f*speed*1.535353535;
           }
       }
       else if(activityType==1) {
           if(speedn==1) {
               met=2.0;
           }
           else if(speedn==2) {
               met=2.8;
           }
           else if(speedn==3) {
               met=3.1;
           }
           else if(speedn==4) {
               met=3.5;
           }
           else {
                met=2.23694f*speed*1.14;
           }
       }
       else{
           if(speedn==10) {
               met=6.8;
           }
           else if(speedn==12) {
               met=8.0;
           }
           else if(speedn==14) {
               met=10.0;
           }
           else if(speedn==16) {
               met =12.8;
           }
           else if(speedn==18) {
               met=13.6;
           }
           else if(speedn==20) {
               met=15.8;
           }
           else {
               met=0.744444444*2.23694f*speed;
           }
       }
       return met;
    }

    public static float avgList(List<Float>myList) {
        float sum=0;
        float avg=0;
        for(int i=0; i<myList.size(); i++) {
            sum += myList.get(i);
        }
        avg=sum/myList.size();
        return avg;
    }
    public static double countCalories(int sportActivity, float weight, List<Float> SpeedList, double timeFillingSpeedListInHours) {
        float avg=avgList(SpeedList);
        double met = getMET(sportActivity, avg);
        double cal = met*weight*timeFillingSpeedListInHours;
        return cal;
    }
}
