package si.uni_lj.fri.pbd2019.runsup2;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WorkoutDetailActivity extends AppCompatActivity {

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
}
