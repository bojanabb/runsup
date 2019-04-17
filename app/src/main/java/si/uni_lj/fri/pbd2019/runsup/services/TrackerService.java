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
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Timer;

import si.uni_lj.fri.pbd2019.runsup.StopwatchActivity;

public class TrackerService extends Service {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final IBinder mBinder = new LocalBinder();
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    LocationManager mLocationManager;
    public long duration=0;
    //int sportType = 0;
    int mState=0;

    float distance=0;
    Boolean mRequestingLocationUpdates = false;
    String mLastUpdateTime;
    ArrayList<Location> locationList;
    long start=0;
    Timer timer;
    Handler handler;
    public final static String COMMAND_START = "si.uni_lj.fri.pbd2019.runsup2.COMMAND_START";
    public final static String COMMAND_CONTINUE = "si.uni_lj.fri.pbd2019.runsup2.COMMAND_CONTINUE";
    public final static String COMMAND_PAUSE = "si.uni_lj.fri.pbd2019.runsup2.COMMAND_PAUSE";
    public final static String COMMAND_STOP = "si.uni_lj.fri.pbd2019.runsup2.COMMAND_STOP";


    private void broadCastSend()
    {
        try
        {
            Intent mBroadCastIntent = new Intent();
            mBroadCastIntent.setAction(StopwatchActivity.TICK);
            mBroadCastIntent.putExtra("duration", String.valueOf(getDuration()));
            mBroadCastIntent.putExtra("distance", getDistance());
            mBroadCastIntent.putExtra("pace", getPace());
            mBroadCastIntent.putExtra("state", getState());
            sendBroadcast(mBroadCastIntent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updateStopwatch()
    {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(StopwatchActivity.TICK);
        this.duration =(SystemClock.uptimeMillis()	-start);
        broadCastIntent.putExtra("duration", String.valueOf(getDuration()/1000));
        broadCastIntent.putExtra("distance", getDistance());
        broadCastIntent.putExtra("pace", getPace());
        broadCastIntent.putExtra("state", getState());
        sendBroadcast(broadCastIntent);
    }

    public void  timeTick() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateStopwatch();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    @Override
    public void onCreate() {
        handler=new Handler();
        locationList = new ArrayList<Location>();
        Log.d("MainActivity","Service has started");
        super.onCreate();
    }


    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double lat=location.getLatitude();
            double lon=location.getLongitude();
            Log.d("MainLoc", String.valueOf(lat));
            Log.d("MainLoc", String.valueOf(lon));
            int size=locationList.size();
            if(mState==2){
                distance=distance+locationList.get(size-1).distanceTo(location);
                if(distance<=100){
                    locationList.add(location);
                }
            }
            else {
                if (size != 0) {
                    distance = distance + locationList.get(size - 1).distanceTo(location);
                }
                locationList.add(location);
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
           // sportType=intent.getExtras().getInt("sportType");
            mState=0;
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null) {
                            locationList.add(location);
                        }
                    }
                });
            }
            Log.d("TrackerCheck", "in command start");
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
            mState=1;
            handler.removeMessages(0);
        }
        else if(intent.getAction()==COMMAND_CONTINUE) {
            start=SystemClock.uptimeMillis()-duration;
            mState=2;
            timeTick();
        }
        else if(intent.getAction()==COMMAND_STOP) {
            mState=3;
            handler.removeMessages(0);
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public int getState() {
        return mState;
    }

    public long getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    public double getPace() {
        return (getDuration()/60)/(getDistance()/1000);
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
}
