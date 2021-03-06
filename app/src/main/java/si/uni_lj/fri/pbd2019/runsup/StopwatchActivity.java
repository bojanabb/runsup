package si.uni_lj.fri.pbd2019.runsup;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import si.uni_lj.fri.pbd2019.runsup.helpers.MainHelper;
import si.uni_lj.fri.pbd2019.runsup.helpers.SportActivities;
import si.uni_lj.fri.pbd2019.runsup.services.TrackerService;

public class StopwatchActivity extends AppCompatActivity {

    boolean firstClick = true;
    int sportActivity = 0;
    int tempF = 1;
    long duration = 0;
    double distance = 0;
    double pace = 0;
    int activity = 0;
    double calories = 0;
   static TextView txtvDistance;
    static TextView txtvDuration;
    static TextView txtvPace;
    static TextView txtvCalories;
    static int state=0;
    MyBroadCastReceiver mBroadcastReceiver;

    ArrayList<Double> paceList;
    static ArrayList<List<Location>> fPositionList;
    List<Location> locations;
    private static final int REQUEST_ID_LOCATION_PERMISSIONS = 34;
    private final String TAG = "MainActivity";
    public final static String TICK = "si.uni_lj.fri.pbd2019.runsup2.TICK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startUpdates();
        setContentView(R.layout.activity_stopwatch);
        Intent intent = new Intent(StopwatchActivity.this, TrackerService.class);
        paceList = new ArrayList<Double>();
        fPositionList = new ArrayList<List<Location>>();
        startService(intent);
        //startLocationUpdates();
        final Button button = (Button) findViewById(R.id.button_stopwatch_start);
        final Button button1 = (Button) findViewById(R.id.button_stopwatch_endworkout);
        mBroadcastReceiver = new MyBroadCastReceiver();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstClick == true) {
                    if (tempF == 1) {
                        Intent intent = new Intent(StopwatchActivity.this, TrackerService.class);
                        intent.setAction(TrackerService.COMMAND_START);
                        intent.putExtra("sportActivity", sportActivity);
                        startService(intent);
                        tempF = 0;
                    } else {
                        Intent intent = new Intent(StopwatchActivity.this, TrackerService.class);
                        intent.setAction(TrackerService.COMMAND_CONTINUE);
                        startService(intent);
                    }
                    button.setText(R.string.stopwatch_stop);
                    button1.setVisibility(View.INVISIBLE);
                    firstClick = false;
                } else {
                    Intent intent = new Intent(StopwatchActivity.this, TrackerService.class);
                    intent.setAction(TrackerService.COMMAND_PAUSE);
                    startService(intent);
                    StopwatchActivity.super.onPause();
                    button.setText(R.string.stopwatch_continue);
                    button1.setText(R.string.stopwatch_endworkout);
                    button1.setVisibility(View.VISIBLE);
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder b = new AlertDialog.Builder(StopwatchActivity.this);
                            b.setTitle("Please Confirm")
                                    .setMessage("Are you sure?")
                                    .setCancelable(true)
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(StopwatchActivity.this, WorkoutDetailActivity.class);
                                            sendInfo(intent);
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog al = b.create();
                            al.show();
                        }
                    });
                    firstClick = true;
                }
            }
        });
        super.onCreate(savedInstanceState);
        final Button chooseType = (Button) findViewById(R.id.button_stopwatch_selectsport);
        chooseType.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonSelectSportPressed(chooseType);
            }
        });
    }

    @Override
    public void onPause() {
        unregisterReceiver(mBroadcastReceiver);
        Intent intent = new Intent(StopwatchActivity.this, TrackerService.class);
            stopService(intent);
        super.onPause();
    }

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TICK);
        registerReceiver(mBroadcastReceiver, intentFilter);
        Intent intent = new Intent(StopwatchActivity.this, TrackerService.class);
        startService(intent);
        super.onResume();
    }

    class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                duration = intent.getExtras().getLong("duration");
                txtvDuration = (TextView) findViewById(R.id.textview_stopwatch_duration);
                String resDur = MainHelper.formatDuration(duration);
                txtvDuration.setText(resDur);
                activity = intent.getExtras().getInt("sportActivity");
                txtvCalories = (TextView) findViewById(R.id.textview_stopwatch_calories);
                if (ActivityCompat.checkSelfPermission(StopwatchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(StopwatchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    distance = intent.getExtras().getDouble("distance");
                    txtvDistance = (TextView) findViewById(R.id.textview_stopwatch_distance);
                    String disRes = MainHelper.formatDistance(distance);
                    txtvDistance.setText(disRes);
                    pace = intent.getExtras().getDouble("pace");
                    txtvPace = (TextView) findViewById(R.id.textview_stopwatch_pace);
                    String paceRes = MainHelper.formatPace(pace);
                    paceList.add(pace);
                    txtvPace.setText(paceRes);
                    calories = intent.getExtras().getDouble("calories");
                    String calRes = MainHelper.formatCalories(calories);
                    txtvCalories.setText(calRes);
                    ArrayList<Location> positionList = intent.getExtras().getParcelableArrayList("positionList");
                    int state = intent.getExtras().getInt("state");
                    if (state == 0) {
                        locations = new ArrayList<Location>();
                        locations.addAll(positionList);
                    } else {
                        if (state == 1) {
                            fPositionList.add(locations);
                            locations.clear();
                            locations = new ArrayList<Location>();
                        } else {
                            locations.addAll(positionList);
                        }
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(StopwatchActivity.this, TrackerService.class);
        stopService(intent);
        super.onDestroy();
    }


    private void startUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Asking for permission");
            ActivityCompat.requestPermissions(StopwatchActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ID_LOCATION_PERMISSIONS);
        }
    }

    public void typeActivity(int type) {
        Button btnTypeAct = (Button) findViewById(R.id.button_stopwatch_selectsport);
        if (type == 0) {
            btnTypeAct.setText("Running");
            sportActivity = 0;

            }
            else {
                if (type == 1) {
                btnTypeAct.setText("Walking");
                sportActivity = 1;
            } else {
                btnTypeAct.setText("Cycling");
                sportActivity = 2;

            }
        }
    }

    public void buttonSelectSportPressed(Button button) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Select Sport Activity:");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        for (Integer activity : SportActivities.act) {
            arrayAdapter.add(SportActivities.getActivityType(this, activity));
        }
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                typeActivity(which);
            }
        });
        AlertDialog dialog = builderSingle.create();
        dialog.show();
    }

    public static double avg(ArrayList<Double> avgL) {
        double sum = 0;
        for (int i = 0; i < avgL.size(); i++) {
            sum = sum + avgL.get(i);
        }
        double avgg = sum / avgL.size();
        return avgg;
    }

    public void sendInfo(Intent intent) {
        intent.putExtra("duration", duration);
//        intent.putExtra("distance",distance);
//        intent.putExtra("pace",pace);
        intent.putExtra("sportActivity", activity);
//        intent.putExtra("calories",calories);
        if (ActivityCompat.checkSelfPermission(StopwatchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(StopwatchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            intent.putExtra("distance", distance);
            intent.putExtra("pace", MainHelper.formatPace(avg(paceList)));
            intent.putExtra("finalPositionList", fPositionList);
            intent.putExtra("calories", calories);
        }
    }
}