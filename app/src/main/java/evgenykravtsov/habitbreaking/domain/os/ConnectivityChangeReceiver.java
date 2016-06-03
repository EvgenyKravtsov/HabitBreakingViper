package evgenykravtsov.habitbreaking.domain.os;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import evgenykravtsov.habitbreaking.interactor.SendStatisticDataInteractor;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    private static int callCounter;

    //// BROADCAST RECEIVER INTERFACE

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null) {
            callCounter++;
            if (callCounter % 2 != 0) {
                new SendStatisticDataInteractor().interact();
            } else {
                callCounter = 0;
            }

            // TODO Delete test code
            Log.d(AppController.APP_TAG, "Call Counter  = " + callCounter);
        }
    }
}
