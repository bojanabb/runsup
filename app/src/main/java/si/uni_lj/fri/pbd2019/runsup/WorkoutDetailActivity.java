package si.uni_lj.fri.pbd2019.runsup;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDetailActivity extends AppCompatActivity {

    int sportActivity=0;
    long duration=0;
    double distance=0;
    double pace=0;
    double calories=0;
    ArrayList<List<Location>> finalPositionList;
//     TextView titleTV = (TextView)findViewById(R.id.textview_workoutdetail_workouttitle);
//    TextView SportAct = (TextView)findViewById(R.id.textview_workoutdetail_sportactivity);
//    TextView DateAct = (TextView)findViewById(R.id.textview_workoutdetail_activitydate);
//    TextView ValDuration = (TextView)findViewById(R.id.textview_workoutdetail_valueduration);
//    TextView ValCalories = (TextView)findViewById(R.id.textview_workoutdetail_valuecalories);
//    TextView ValDistance = (TextView)findViewById(R.id.textview_workoutdetail_valuedistance);
//        String titleTV = getResources().getString(R.string.workoutdetail_workoutname);
//        String SportAct = getResources().getString(R.string.textview_workoutdetail_sportactivity);
//        String DateAct = getResources().getString(R.string.textview_workoutdetail_activitydate);
//        String ValDur = getResources().getString(R.string.textview_workoutdetail_valueduration);
//        String ValCal = getResources().getString(R.string.textview_workoutdetail_valuecalories);
//        String ValDist = getResources().getString(R.string.textview_workoutdetail_valuedistance);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);
        final Button buttonn = (Button) findViewById(R.id.button_workoutdetail_showmap);
        buttonn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutDetailActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
