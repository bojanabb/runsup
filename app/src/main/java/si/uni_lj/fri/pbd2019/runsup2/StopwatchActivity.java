package si.uni_lj.fri.pbd2019.runsup2;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class StopwatchActivity extends AppCompatActivity {

    /* button.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               button.setText("Stop");
                   button.setOnClickListener(new View.OnClickListener() {
                       public void onClick(View v) {
                           button.setText("Continue");
                           button1.setVisibility(View.VISIBLE);
                           button.setOnClickListener(new View.OnClickListener() {
                               public void onClick(View v) {
                                   button.setText("Start");
                                   button1.setVisibility(View.GONE);
                               }
                           });
                       }
                   });
           }
       });*/

    /*
    startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isClickedFirstTime == true) {
                    startButton.setText("Stop");
                    endButton.setVisibility(View.INVISIBLE);
                    isClickedFirstTime = false;
                } else {

                    startButton.setText("Continue");
                    endButton.setVisibility(View.VISIBLE);
                    endButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialogOpen(StopwatchActivity.this);
                        }
                    });
                    isClickedFirstTime = true;
                }
     */

    /*  AlertDialog.Builder b = new AlertDialog.Builder(getApplicationContext());
                            b.setTitle("Please Confirm");
                            b.setMessage("Are you sure?");
                            b.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //otvori novi
                                }
                            });
                            b.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog al = b.create();
                            al.show(); */

    boolean firstClick=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        final Button button = (Button) findViewById(R.id.button_stopwatch_start);
        final Button button1 = (Button) findViewById(R.id.button_stopwatch_endworkout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstClick==true) {
                    button.setText("Stop");
                    button1.setVisibility(View.INVISIBLE);
                    firstClick=false;
                }
                else {
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

    //button_stopwatch_start





}
