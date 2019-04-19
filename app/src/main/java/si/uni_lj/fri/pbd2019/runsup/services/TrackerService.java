package si.uni_lj.fri.pbd2019.runsup.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import si.uni_lj.fri.pbd2019.runsup.StopwatchActivity;
import si.uni_lj.fri.pbd2019.runsup.helpers.SportActivities;

public class TrackerService extends Service {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final IBinder mBinder = new LocalBinder();
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    LocationManager mLocationManager;
    public long duration=0;
    static int state=0;
    static int sportActivity=0;
    static double distance=0;
    Boolean mRequestingLocationUpdates = false;
    String mLastUpdateTime;
    static double timeFillingList = 0;
    static double currentTimeFilling=0;
    static double cal=0;
    static double timeF=0;
    static double difference=0;
    ArrayList<Location> locationList;
    ArrayList<Double> time;
    List<Float> speed;
    static double timeS=0;
    Location lastLocation;
    long start=0;
    Timer timer;
    Handler handler;
    public final static String COMMAND_START = "si.uni_lj.fri.pbd2019.runsup.COMMAND_START";
    public final static String COMMAND_CONTINUE = "si.uni_lj.fri.pbd2019.runsup.COMMAND_CONTINUE";
    public final static String COMMAND_PAUSE = "si.uni_lj.fri.pbd2019.runsup.COMMAND_PAUSE";
    public final static String COMMAND_STOP = "si.uni_lj.fri.pbd2019.runsup.COMMAND_STOP";

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        }
    }


    private void updateStopwatch()
    {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(StopwatchActivity.TICK);
        duration =(SystemClock.uptimeMillis()	-start);

        broadCastIntent.putExtra("duration",getDuration()/1000);
        broadCastIntent.putExtra("sportActivity", sportActivity);
        broadCastIntent.putExtra("state", getState());

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            broadCastIntent.putExtra("distance", distance);
            broadCastIntent.putExtra("pace", getPace());
            broadCastIntent.putExtra("positionList", locationList);
            broadCastIntent.putExtra("calories", cal);
            locationList.clear();
            if(speed.size()>2) {
                cal= SportActivities.countCalories(sportActivity, 60f, speed, timeFillingList/3600000.0);
            }
        }
        sendBroadcast(broadCastIntent);
    }

    public void timeTick() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("Proba","tik");
                updateStopwatch();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    @Override
    public void onCreate() {
        locationList=new ArrayList<Location>();
        handler=new Handler();
        time=new ArrayList<Double>();
        speed=new ArrayList<Float>();
        locationList= new ArrayList<Location>();
        super.onCreate();
        Log.d("Proba", "Create");
    }


    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            timeF=duration;
            difference=timeF-timeS;
            timeS=timeF;
            time.add(difference);
            float lat=(float)location.getLatitude();
            float lon=(float)location.getLongitude();
//            Log.d("MainLoc", String.valueOf(lat));
//            Log.d("MainLoc", String.valueOf(lon));
            int size=locationList.size();
            if(state==2 && lastLocation != null){
                float latLast = (float)(lastLocation.getLatitude());
                float lonLast = (float)(lastLocation.getLongitude());
                distance=distance + distanceBetween(lat, lon, latLast, lonLast);
                if(distance<=100){
                    locationList.add(location);
                    speed.add(1000*(float) distance/getDuration());
                }
                else {
                    speed.add(1000*(float) distance/getDuration());
                    currentTimeFilling=duration;
                }
            }
            else {
                if (state==0) {
                   if(lastLocation != null) {
                       float latLast =(float)(lastLocation.getLatitude());
                       float lonLast = (float) (lastLocation.getLongitude());
                       distance=distance+distanceBetween(lat, lon, latLast, lonLast);
                   }
                    locationList.add(location);
                   speed.add((float)distance/getDuration());
                   timeFillingList = duration - currentTimeFilling;
                }
            lastLocation = location;
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction()==COMMAND_START) {
            sportActivity = intent.getExtras().getInt("sportActivity");
            state=0;
            Log.d("Proba", "Start");
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null) {
                            lastLocation = location;;
                            locationList.add(location);
                        }
                    }
                });
            }
            start = SystemClock.uptimeMillis();
            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, locationListenerGPS);
            mLastUpdateTime = "";
            timeTick();
        }
        else if(intent.getAction()==COMMAND_PAUSE){
            this.duration =(SystemClock.uptimeMillis()	-start);
            state=1;
            handler.removeMessages(0);
        }
        else if(intent.getAction()==COMMAND_CONTINUE) {
            start=SystemClock.uptimeMillis()-duration;
            state=2;
            timeTick();
        }
        else if(intent.getAction()==COMMAND_STOP) {
            state=3;
            handler.removeMessages(0);
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public int getState() {
        return state;
    }

    public long getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    public double getPace() {
        if (distance == 0.0) return 0.0;
        else if (2*minE(time)>duration-timeS) return 0.0;
        else return (duration) / (distance * 60);
    }

    public TrackerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public TrackerService getService() {
            return TrackerService.this;
        }
    }

    public void printLocations(){
        int len=locationList.size();
        Log.d("MainActivityLen",String.valueOf(len));
        if(len>0) {
            for (int i = 1; i <= len; i++) {
                double lat = locationList.get(i).getLatitude();
                double lon = locationList.get(i).getLongitude();
                Log.d("MainLoc", String.valueOf(lat));
                Log.d("MainLoc", String.valueOf(lon));
            }
        }
    }

    public double distanceBetween(float lat_a, float lng_a, float lat_b, float lng_b) {
        float p=(float)(180.f/Math.PI);

        float a1= lat_a/p;
        float a2= lng_a/p;
        float b1= lat_b/p;
        float b2= lng_b/p;

        double f1 = Math.cos(a2)*Math.cos(a1)*Math.cos(b1)*Math.cos(b2);
        double f2 = Math.sin(a2)*Math.cos(a1)*Math.cos(b1)*Math.sin(b2);
        double f3 = Math.sin(a1)*Math.sin(b1);
        double fin = Math.acos(f1+f2+f3);

        return 6366000*fin;
    }

    public double minE(ArrayList<Double> mList) {
        double min=0;
        if(mList.isEmpty()) return 0;
        else {
            int s=mList.size();
            min=mList.get(0);
            for(int i=0; i<s; i++) {
                if(min<mList.get(i)) {
                    min=mList.get(i);
                }
            }
        }
        return min;
    }
}
