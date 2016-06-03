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
import evgenykravtsov.habitbreaking.data.StatisticDataStorage;
import evgenykravtsov.habitbreaking.domain.event.ConsumptionTimeDifferenceEvent;
import evgenykravtsov.habitbreaking.domain.os.AppController;
import evgenykravtsov.habitbreaking.interactor.event.ConsumptionLockEvent;

public class ConsumptionManagerService extends Service {

    // Module dependencies
    private ApplicationDataStorage applicationDataStorage;
    private StatisticDataStorage statisticDataStorage;

    //// SERVICE LIFECYCLE

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Consumption Manager Service Created");

        applicationDataStorage = DataModuleFactory.provideApplicationDataStorage();
        statisticDataStorage = DataModuleFactory.provideStatisticDataStorage();
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
            @SuppressWarnings("InfiniteLoopStatement")
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
        switch (applicationDataStorage.loadMode()) {
            case FREE:
                processTimeForFreeMode();
                break;
            case CONTROL:
                processTimeForRestrictedMode();
                break;
            case HEALTH:
                processTimeForRestrictedMode();
                break;
        }
    }

    private void processTimeForFreeMode() {
        long consumptionDate = applicationDataStorage.loadConsumptionDate();

        if (consumptionDate == ApplicationDataStorage.CONSUMPTION_DATE_DEFAULT_VALUE) {
            return;
        }

        long timeDifferenceSeconds = Utils.getCurrentTimeUnixSeconds() - consumptionDate;
        ConsumptionTimeDifferenceEvent event = new ConsumptionTimeDifferenceEvent(timeDifferenceSeconds);
        EventBus.getDefault().post(event);
    }

    private void processTimeForRestrictedMode() {
        checkAccumulation();

        if (applicationDataStorage.loadConsumptionLockStatus()) {
            long timeDifferenceSeconds = applicationDataStorage.loadConsumptionUnlockDate() -
                    Utils.getCurrentTimeUnixSeconds();

            if (timeDifferenceSeconds >= 0) {
                ConsumptionTimeDifferenceEvent event = new ConsumptionTimeDifferenceEvent(timeDifferenceSeconds);
                EventBus.getDefault().post(event);
            } else {
                applicationDataStorage.saveConsumptionLockStatus(false);
                ConsumptionLockEvent event = new ConsumptionLockEvent(false);
                EventBus.getDefault().post(event);
            }
        }
    }

    private void checkAccumulation() {
        // TODO Delete test code
        if (applicationDataStorage.loadAccumulationDate() ==
                ApplicationDataStorage.ACCUMULATION_DATE_DEFAULT_VALUE) {
            applicationDataStorage.saveAccumulationDate(Utils.getCurrentTimeUnixSeconds());
        }

        if (Utils.getCurrentTimeUnixSeconds() >= applicationDataStorage.loadAccumulationDate()) {
            applicationDataStorage.saveAccumulationRegister(
                    applicationDataStorage.loadAccumulationRegister() + 1);

            applicationDataStorage.saveAccumulationDate(
                    applicationDataStorage.loadAccumulationDate() + secondsToNextAccumulation());
        }
    }

    private int secondsToNextAccumulation() {
        return 1440 / (int) (statisticDataStorage.getAverageConsumption() * 60);
    }
}
