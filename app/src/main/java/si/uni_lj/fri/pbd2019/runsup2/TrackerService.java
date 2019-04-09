package si.uni_lj.fri.pbd2019.runsup2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;

public class TrackerService extends Service {
    public TrackerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
