package evgenykravtsov.habitbreaking.domain;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import evgenykravtsov.habitbreaking.data.ApplicationDataStorage;
import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.domain.event.ConsumptionTimeDifferenceEvent;
import evgenykravtsov.habitbreaking.domain.os.AppController;
import evgenykravtsov.habitbreaking.domain.os.Utils;

public class ConsumptionManagerService extends Service {

    // Module dependencies
    private ApplicationDataStorage applicationDataStorage;

    //// SERVICE LIFECYCLE

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Consumption Manager Service Created");

        applicationDataStorage = DataModuleFactory.provideApplicationDataStorage();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Consumption Manager Service Started");

        startConsumptionManagerThread();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //// PRIVATE METHODS

    private void startConsumptionManagerThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                // TODO Delete test code
                Log.d(AppController.APP_TAG, "Consumption Manager Thread Started");

                while (true) {
                    processConsumptionTime();

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void processConsumptionTime() {
        long consumptionDate = applicationDataStorage.loadConsumptionDate();

        if (consumptionDate == ApplicationDataStorage.CONSUMPTION_DATE_DEFAULT_VALUE) {
            return;
        }

        long timeDifferenceSeconds = Utils.getCurrentTimeUnixSeconds() - consumptionDate;
        ConsumptionTimeDifferenceEvent event = new ConsumptionTimeDifferenceEvent(timeDifferenceSeconds);
        EventBus.getDefault().post(event);
    }
}
