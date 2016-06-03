package evgenykravtsov.habitbreaking.domain.os;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import evgenykravtsov.habitbreaking.domain.ConsumptionManagerService;

public class AppController extends Application {

    public static final String APP_TAG = "HabitBreaking";

    private static Context applicationContext;

    //// PUBLIC METHODS

    public static Context getAppContext() {
        return applicationContext;
    }

    public static boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    //// APPLICATION LIFECYCLE

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO Delete test code
        Log.d(APP_TAG, "Application Created");

        applicationContext = getApplicationContext();
        startConsumptionManagerService();
    }

    //// PRIVATE METHODS

    private void startConsumptionManagerService() {
        Intent startConsumptionManagerService = new Intent(this, ConsumptionManagerService.class);
        startService(startConsumptionManagerService);
    }
}
