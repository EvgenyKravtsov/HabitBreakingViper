package evgenykravtsov.habitbreaking.interactor;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.data.StatisticDataStorage;
import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;
import evgenykravtsov.habitbreaking.domain.os.AppController;
import evgenykravtsov.habitbreaking.network.NetworkModuleFactory;
import evgenykravtsov.habitbreaking.network.ServerConnection;
import evgenykravtsov.habitbreaking.network.ServerConnectionListener;
import evgenykravtsov.habitbreaking.network.event.DownloadDataEvent;
import evgenykravtsov.habitbreaking.network.event.NoInternetConnectionEvent;
import evgenykravtsov.habitbreaking.network.event.NoStatisticForUserEvent;

public class DownloadStatisticInteractor implements ServerConnectionListener {

    // Module dependencies
    private StatisticDataStorage statisticDataStorage;
    private ServerConnection serverConnection;

    //// CONSTRUCTORS

    public DownloadStatisticInteractor() {
        statisticDataStorage = DataModuleFactory.provideStatisticDataStorage();
        serverConnection = NetworkModuleFactory.provideServerConnection();
    }

    //// PUBLIC METHODS

    public void interact(long registrationDate) {
        if (!AppController.isInternetAvailable()) {
            EventBus.getDefault().post(new NoInternetConnectionEvent());
            return;
        }

        EventBus.getDefault().post(new DownloadDataEvent(0));
        serverConnection.setServerConnectionListener(this);
        serverConnection.getStatistic(registrationDate);
    }

    //// SERVER CONNECTION LISTENER INTERFACE

    @Override
    public void onStatisticDataReceived(List<StatisticDataEntity> entities) {

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Statistic Data Received");

        if (entities.size() > 0) {
            for (StatisticDataEntity entity : entities) {

                // TODO Delete test code
                Log.d(AppController.APP_TAG, entity.toString());
            }

            statisticDataStorage.clearStatisticStorage();
            statisticDataStorage.saveStatistic(entities);
        } else {
            EventBus.getDefault().post(new NoStatisticForUserEvent());
        }

        serverConnection.removeServerConnectionListener(this);
    }
}
