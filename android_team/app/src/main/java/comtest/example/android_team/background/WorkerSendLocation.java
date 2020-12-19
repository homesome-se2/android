package comtest.example.android_team.background;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import comtest.example.android_team.AppManager;
import comtest.example.android_team.R;
import comtest.example.android_team.models.ReadWriteCache;

public class WorkerSendLocation extends Worker {

    private Context currentContext;
    private FusedLocationProviderClient fLocation;
    private static final String TAG = "Info";
    private static final String CHANNEL_ID = "channelGPS";

    public WorkerSendLocation(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        currentContext = context;
    }

    public Result doWork() {
        Log.i("Info", "DO_WORK: Start method");
        try {
            Thread.sleep(60000 * 1);//5 minutes cycle
        } catch (InterruptedException e) {
            Log.e("Info", "DO_WORK: Thread sleep failed...");
            e.printStackTrace();
        }
        doTheActualProcessingWork();
        return Result.success();
    }

    private void doTheActualProcessingWork() {
        Log.i("Info", "DO_WORK: Start EXECUTING TASK!!!!");
        final LocationManager manager = (LocationManager)currentContext.getSystemService(Context.LOCATION_SERVICE );

        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
            Log.i("Info", "GPS NOT AVAILABLE");
        }else {
            Log.i("Info", "GPS IS AVAILABLE");
            fLocation = LocationServices.getFusedLocationProviderClient(currentContext);
            getLocation();
        }
        addNewWork();
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        /* PRIORITY_BALANCED_POWER_ACCURACY = 102
         * PRIORITY_HIGH_ACCURACY = 100
         * PRIORITY_LOW_POWER = 104
         * PRIORITY_NO_POWER = 105
         */
        Task<Location> locationTask = fLocation.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null);
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //We have a location
                    Log.i(TAG, "onSuccess: LONGITUDE: " + location.getLongitude());
                    Log.i(TAG, "onSuccess: LATITUDE: " + location.getLatitude());

                    // Create the NotificationChannel, but only on API 26+ because
                    // the NotificationChannel class is new and not in the support library
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        CharSequence name = "AppName";
                        String description = "AppName";
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
                        channel.setDescription(description);
                        // Register the channel with the system; you can't change the importance
                        // or other notification behaviors after this
                        NotificationManager notificationManager = currentContext.getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);
                    }

                    String completeAddress = getCompleteAddressString(location.getLatitude(), location.getLongitude());
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(currentContext, CHANNEL_ID)
                            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                            .setContentTitle("HomeSome Update")
                            .setContentText("You are at " + completeAddress)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText("You are at " + completeAddress));

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(currentContext);

                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(1001, builder.build());

                    // Server connect
                    if (!AppManager.getInstance().appInFocus) {
                        Log.i("Info", "SEND DATA TO SERVER: App not in focus, background");
                        ReadWriteCache readWriteCache = new ReadWriteCache(currentContext);
                        if (readWriteCache.cacheFileExist()) {
                            String cacheData = readWriteCache.readFromCache();
                            String[] commands = cacheData.split(":");
                            String username = commands[0].trim();
                            String sessionKey = commands[1].trim();
                            AppManager.getInstance().establishConnection();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            String logIn = "501::" + username + "::" + sessionKey + "::" + location.getLongitude() + "::" + location.getLatitude() + "";
                            AppManager.getInstance().requestToServer(logIn);
                            Log.i("Info", "SEND DATA TO SERVER: App not in focus, SENDING REQUEST");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            AppManager.getInstance().endConnection();
                        }
                    } else {
                        Log.i("Info", "SEND DATA TO SERVER: App in focus, background");
                        String logIn = "502::" + location.getLongitude() + "::" + location.getLatitude() + "";
                        AppManager.getInstance().requestToServer(logIn);
                    }
                } else {
                    Log.i(TAG, "onSuccess: Location was null...");
                }
            }
        });

        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: " + e.getLocalizedMessage());
            }
        });
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(currentContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    private void addNewWork() {
        Log.i("Info", "DO_WORK: Adds new work to que");
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        OneTimeWorkRequest refreshWork = new OneTimeWorkRequest.Builder(WorkerSendLocation.class)
                .setConstraints(constraints)
                .addTag("sendGPSPos")
                .build();
        WorkManager.getInstance(currentContext).enqueueUniqueWork("sendGPSPos", ExistingWorkPolicy.REPLACE, refreshWork);
    }
}
