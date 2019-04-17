package si.uni_lj.fri.pbd2019.runsup.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.Timer;

import si.uni_lj.fri.pbd2019.runsup.StopwatchActivity;

public class TrackerService extends Service {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final IBinder mBinder = new LocalBinder();
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    public long duration=0;
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
        Log.d("MainActivity","Service has started");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction()==COMMAND_START) {
            start = SystemClock.uptimeMillis();
            mLastUpdateTime = "";
            timeTick();
        }
        else if(intent.getAction()==COMMAND_PAUSE){
            this.duration =(SystemClock.uptimeMillis()	-start);
            handler.removeMessages(0);
        }
        else if(intent.getAction()==COMMAND_CONTINUE) {
            start=SystemClock.uptimeMillis()-duration;
            timeTick();
        }
        else if(intent.getAction()==COMMAND_STOP) {
            handler.removeMessages(0);
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public int getState() {
        return 0;
    }

    public long getDuration() {
        return duration;
    }

    public long getDistance() {
        long dist=0;
         return dist;
    }

    public long getPace() {
        long pac=0;
        return pac;
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
}
