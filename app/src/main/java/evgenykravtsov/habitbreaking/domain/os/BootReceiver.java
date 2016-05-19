package evgenykravtsov.habitbreaking.domain.os;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Boot Event Received");
    }
}
