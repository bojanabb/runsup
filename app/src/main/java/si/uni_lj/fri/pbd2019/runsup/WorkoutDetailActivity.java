package si.uni_lj.fri.pbd2019.runsup;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.internal.Util;
import si.uni_lj.fri.pbd2019.runsup.helpers.MainHelper;
import si.uni_lj.fri.pbd2019.runsup.helpers.SportActivities;

public class WorkoutDetailActivity extends AppCompatActivity {

    int sportActivity=0;
    long duration=0;
    double distance=0;
    double pace=0;
    double calories=0;
    int activity=0;
    ArrayList<List<Location>> finalPositionList;

    public static boolean doesPackageExists(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            return false;
        }
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static Intent getTwitterIntent(Context ctx, String shareText)
    {
        Intent shareIntent;
        //calls doesPackageExists

        if(doesPackageExists(ctx, "com.twitter.android"))
        {
            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setClassName("com.twitter.android",
                    "com.twitter.android.PostActivity");
            shareIntent.setType("text/*");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            return shareIntent;
        }
        else
        {
            String tweetUrl = "https://twitter.com/intent/tweet?text=" + shareText;
            Uri uri = Uri.parse(tweetUrl);
            shareIntent = new Intent(Intent.ACTION_VIEW, uri);
            return shareIntent;
        }
    }


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

        final EditText shareM = (EditText) findViewById(R.id.edit_text);
        shareM.setText("I was out for WORKOUT_TYPE. I did DISTANCE UNIT in DURATION.", TextView.BufferType.NORMAL);

        getInfo();
        TextView durationTextView = (TextView) findViewById(R.id.textview_workoutdetail_valueduration);
        String durationString= MainHelper.formatDuration(duration);
        durationTextView.setText(durationString);
        String distanceString= MainHelper.formatDistance(distance);
        TextView distanceTextView = (TextView) findViewById(R.id.textview_workoutdetail_valuedistance);
        distanceTextView.setText(distanceString+" km");
        TextView paceTextView = (TextView) findViewById(R.id.textview_workoutdetail_valueavgpace);
        String paceString= MainHelper.formatPace(pace);
        paceTextView.setText(paceString+ "min/km");
        TextView caloriesTextView = (TextView) findViewById(R.id.textview_workoutdetail_valuecalories);
        String caloriesString= MainHelper.formatCalories(calories);
        caloriesTextView.setText(caloriesString+"  cal");
        TextView activityTextView = (TextView) findViewById(R.id.textview_workoutdetail_sportactivity);
        activityTextView.setText(SportActivities.getActivityType(this,activity));
        TextView date=(TextView) findViewById(R.id.textview_workoutdetail_activitydate);
        DateFormat currDate = SimpleDateFormat.getDateTimeInstance();
        Date today = Calendar.getInstance().getTime();
        date.setText(currDate.format(today));
        final Button email = (Button) findViewById(R.id.button_workoutdetail_emailshare);
        final Button facebook = (Button) findViewById(R.id.button_workoutdetail_fbsharebtn);
        final Button twitter = (Button) findViewById(R.id.button_workoutdetail_twittershare);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","", null ));
                eIntent.putExtra(Intent.EXTRA_SUBJECT, "Workout summary");
                eIntent.putExtra(Intent.EXTRA_TEXT, shareM.getText());
                startActivity(Intent.createChooser(eIntent, "Send email"));
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fbIntent = new Intent(Intent.ACTION_SEND);
                fbIntent.setType("text/plain");
                fbIntent.putExtra(Intent.EXTRA_TEXT, shareM.getText());
                PackageManager pm = getPackageManager();
                List<ResolveInfo> activityList = pm.queryIntentActivities(fbIntent, 0);
                for (final ResolveInfo app : activityList) {
                    if ((app.activityInfo.name).contains("facebook")) {
                        final ActivityInfo activity = app.activityInfo;
                        final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                        fbIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        fbIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        fbIntent.setComponent(name);
                        startActivity(fbIntent);
                        break;
                    }
                }
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getTwitterIntent(WorkoutDetailActivity.this, shareM.getText().toString()));
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    public void getInfo() {
        Intent in = getIntent();
        duration=in.getExtras().getLong("duration");
        distance=in.getExtras().getDouble("distance");
        pace=in.getExtras().getDouble("pace");
        calories=in.getExtras().getDouble("calories");
        activity=in.getExtras().getInt("activity");
    }

}
