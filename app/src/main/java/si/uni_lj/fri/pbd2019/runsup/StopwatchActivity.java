package si.uni_lj.fri.pbd2019.runsup;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import si.uni_lj.fri.pbd2019.runsup.helpers.MainHelper;
import si.uni_lj.fri.pbd2019.runsup.services.TrackerService;


public class StopwatchActivity extends AppCompatActivity {

    boolean firstClick=true;
    int tempF=1;
    MyBroadCastReceiver mBroadcastReceiver;
    public final static String TICK  = "si.uni_lj.fri.pbd2019.runsup2.TICK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

                    final Button button = (Button) findViewById(R.id.button_stopwatch_start);
                    final Button button1 = (Button) findViewById(R.id.button_stopwatch_endworkout);
        mBroadcastReceiver = new MyBroadCastReceiver();
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(firstClick==true) {
                                if(tempF==1) {
                                    Intent intent = new Intent(StopwatchActivity.this, TrackerService.class);
                                    intent.setAction(TrackerService.COMMAND_START);
                                    startService(intent);
                                    tempF=0;
                                }
                                else {
                                    Intent intent = new Intent(StopwatchActivity.this, TrackerService.class);
                                    intent.setAction(TrackerService.COMMAND_CONTINUE);
                                    startService(intent);
                                }
                                button.setText("Stop");
                                button1.setVisibility(View.INVISIBLE);
                                firstClick=false;
                            }
                            else {
                                Intent intent = new Intent(StopwatchActivity.this, TrackerService.class);
                                intent.setAction(TrackerService.COMMAND_PAUSE);
                                startService(intent);
                                button.setText("Continue");
                                button1.setVisibility(View.VISIBLE);
                                button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder b = new AlertDialog.Builder(StopwatchActivity.this);
                            b.setTitle("Please Confirm");
                            b.setMessage("Are you sure?");
                            b.setCancelable(true);

                            b.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(StopwatchActivity.this, WorkoutDetailActivity.class);
                                    startActivity(intent);

                                }
                            });
                            b.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog al = b.create();
                            al.show();
                        }
                    });
                    firstClick=true;
                }
            }
        });

    }

    @Override
    public void onPause() {
        Log.d("MainActivity","in pause");
        unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("MainActivity","in resume");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TICK);
        registerReceiver(mBroadcastReceiver, intentFilter);
        super.onResume();
    }

    class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String duration = intent.getStringExtra("duration");
                long temp=Long.parseLong(duration);
                String result = MainHelper.formatDuration(temp);
                TextView text=(TextView) findViewById(R.id.textview_stopwatch_duration);
                text.setText(result);
            }
            catch (Exception e) {
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
}
