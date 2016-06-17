package evgenykravtsov.habitbreaking.interactor;

import android.util.Log;

import java.util.List;

import evgenykravtsov.habitbreaking.data.ApplicationDataStorage;
import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.data.StatisticDataStorage;
import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;
import evgenykravtsov.habitbreaking.domain.os.AppController;
import evgenykravtsov.habitbreaking.network.NetworkModuleFactory;
import evgenykravtsov.habitbreaking.network.ServerConnection;

public class SendStatisticDataInteractor {

    // Module dependencies
    private StatisticDataStorage statisticDataStorage;
    private ApplicationDataStorage applicationDataStorage;
    private ServerConnection serverConnection;

    //// CONSTRUCTORS

    public SendStatisticDataInteractor() {
        statisticDataStorage = DataModuleFactory.provideStatisticDataStorage();
        applicationDataStorage = DataModuleFactory.provideApplicationDataStorage();
        serverConnection = NetworkModuleFactory.provideServerConnection();
    }

    //// PUBLIC METHODS

    public void interact() {
        if (applicationDataStorage.loadRegistrationDate() == ApplicationDataStorage.DEFAULT_REGISTRATION_DATE_VALUE) {

            // TODO Delete test code
            Log.d(AppController.APP_TAG, "User Is Not Registered");

            return;
        }

        if (!AppController.isInternetAvailable()) {

            // TODO Delete test code
            Log.d(AppController.APP_TAG, "Internet Is Not Available");

            return;
        }

        List<StatisticDataEntity> statisticData = statisticDataStorage.getUnsentStatisticData();
        if (statisticData != null && statisticData.size() > 0) {
            serverConnection.sendStatisticData(statisticData);
        }
    }
}
