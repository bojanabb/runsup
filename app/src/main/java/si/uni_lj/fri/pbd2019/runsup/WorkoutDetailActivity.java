package si.uni_lj.fri.pbd2019.runsup;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDetailActivity extends AppCompatActivity {

    int sportActivity=0;
    long duration=0;
    double distance=0;
    double pace=0;
    double calories=0;
    ArrayList<List<Location>> finalPositionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EditText sharemes = (EditText)findViewById(R.id.share_m)
        setContentView(R.layout.activity_workout_detail);
        final Button buttonn = (Button) findViewById(R.id.button_workoutdetail_showmap);
        buttonn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutDetailActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        final Button email = (Button) findViewById(R.id.button_workoutdetail_emailshare);
        final Button facebook = (Button) findViewById(R.id.button_workoutdetail_fbsharebtn);
        final Button twitter = (Button) findViewById(R.id.button_workoutdetail_twittershare);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","", null ));
                eIntent.putExtra(Intent.EXTRA_SUBJECT, "Workout summary");
//                eIntent.putExtra(Intent.EXTRA_TEXT, sha)
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
