package evgenykravtsov.habitbreaking.interactor;

import android.util.Log;

import evgenykravtsov.habitbreaking.data.ApplicationDataStorage;
import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.domain.os.AppController;
import evgenykravtsov.habitbreaking.domain.os.Utils;

public class ConsumptionInteractor extends Interactor {

    // Module dependencies
    private ApplicationDataStorage applicationDataStorage;

    //// CONSTRUCTORS

    public ConsumptionInteractor() {
        applicationDataStorage = DataModuleFactory.provideApplicationDataStorage();
    }

    //// INTERACTOR INTERFACE

    @Override
    public void interact() {

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Consumption Interaction");

        applicationDataStorage.saveConsumptionDate(Utils.getCurrentTimeUnixSeconds());
    }
}
